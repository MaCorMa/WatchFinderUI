package com.example.watchfinder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchfinder.data.UiState.SearchUiState // Importa tu UiState
import com.example.watchfinder.data.dto.MovieCard // Importa tu DTO de Movie
import com.example.watchfinder.data.dto.SeriesCard // Importa tu DTO de Series
import com.example.watchfinder.repository.GenreRepository // Repositorio para obtener géneros
import com.example.watchfinder.repository.MovieRepository // Repositorio para buscar pelis
import com.example.watchfinder.repository.SeriesRepository // Repositorio para buscar series
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    // Inyecta los repositorios que necesitas
    private val genreRepository: GenreRepository,
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository
    // private val searchRepository: SearchRepository // Alternativa combinada
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null // Para cancelar búsquedas anteriores si se escribe rápido

    init {
        loadAvailableGenres() // Carga los géneros al iniciar el ViewModel
    }

    private fun loadAvailableGenres() {
        viewModelScope.launch {
            // Opcional: indicar carga de géneros si quieres un estado específico
            try {
                // Llama a tu repositorio para obtener la lista de géneros
                // Asegúrate que genreRepository.getAllGenres() devuelve List<String>
                val genres: List<String> = genreRepository.getAllGenres()
                _uiState.update {
                    // Añade "Todos" al principio y actualiza el estado
                    val allGenres = listOf("Todos") + genres // Ya son Strings, no necesitas .map
                    it.copy(
                        availableGenres = allGenres,
                        selectedGenre = setOf(allGenres.firstOrNull() ?: "Todos")
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(searchError = "Error al cargar géneros: ${e.message}")
                }
            }
        }
    }

    // --- Funciones para actualizar el estado desde la UI ---

    fun onUserInputChange(query: String) {
        _uiState.update {
            it.copy(
                userInput = query,
                searchError = null,
                noResultsFound = false,
                selectedGenre = setOf("Todos")
            )
        }
        // Opcional: añadir debounce si quieres buscar mientras escribe
    }

    fun onSearchTypeChange(type: String) {
        // 'type' debería ser "Movies", "Series", o "Both"
        _uiState.update {
            it.copy(
                selectedSearchType = type,
                searchError = null,
                noResultsFound = false
            )
        }
    }

    fun onGenreChipClicked(genre: String) {
        _uiState.update { currentState ->
            val currentSelection = currentState.selectedGenre.toMutableSet()
            val isTodosSelected = currentSelection.contains("Todos")

            if (genre == "Todos") {
                // Si se pulsa "Todos", seleccionar solo "Todos"
                currentSelection.clear()
                currentSelection.add("Todos")
            } else {
                // Si se pulsa otro género
                currentSelection.remove("Todos") // Quitar "Todos" si estaba
                if (currentSelection.contains(genre)) {
                    // Si ya estaba seleccionado, quitarlo
                    currentSelection.remove(genre)
                } else {
                    // Si no estaba seleccionado, añadirlo
                    currentSelection.add(genre)
                }
                // Si después de quitar/añadir nos quedamos sin selección, volver a "Todos"
                if (currentSelection.isEmpty()) {
                    currentSelection.add("Todos")
                }
            }

            currentState.copy(
                selectedGenre = currentSelection,
                userInput = "", // Limpiar input de texto al tocar un género
                searchError = null,
                noResultsFound = false
            )
        }
    }

    // --- Función para ejecutar la búsqueda ---

    fun performSearch() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = true,
                searchError = null,
                noResultsFound = false,
                searchResultsMovies = emptyList(),
                searchResultsSeries = emptyList()
            )
        }
        val currentState = _uiState.value

        searchJob = viewModelScope.launch {
            try {
                val moviesResult = mutableListOf<Any>() // Usa tus DTOs
                val seriesResult = mutableListOf<Any>() // Usa tus DTOs

                // Comprueba si hay géneros seleccionados (y no es solo "Todos")
                val genresToSearch = currentState.selectedGenre.filter { it != "Todos" }.toList()

                if (genresToSearch.isNotEmpty()) {
                    // --- Búsqueda por Géneros (Lista) ---
                    println("Searching by Genres: ${genresToSearch}, Type: ${currentState.selectedSearchType}")
                    when (currentState.selectedSearchType) {
                        // Asegúrate que tus métodos de repo aceptan List<String>
                        "Movies" -> moviesResult.addAll(
                            movieRepository.searchMoviesByGenre(
                                genresToSearch
                            )
                        )

                        "Series" -> seriesResult.addAll(
                            seriesRepository.searchSeriesByGenre(
                                genresToSearch
                            )
                        )

                        "Both" -> {
                            moviesResult.addAll(movieRepository.searchMoviesByGenre(genresToSearch))
                            seriesResult.addAll(seriesRepository.searchSeriesByGenre(genresToSearch))
                        }
                    }
                } else if (currentState.userInput.isNotBlank()) {
                    // --- Búsqueda por Título (solo si no hay géneros específicos seleccionados) ---
                    println("Searching by Title: ${currentState.userInput}, Type: ${currentState.selectedSearchType}")
                    when (currentState.selectedSearchType) {
                        "Movies" -> moviesResult.addAll(
                            movieRepository.searchMoviesByTitle(
                                currentState.userInput
                            )
                        )

                        "Series" -> seriesResult.addAll(
                            seriesRepository.searchSeriesByTitle(
                                currentState.userInput
                            )
                        )

                        "Both" -> {
                            moviesResult.addAll(movieRepository.searchMoviesByTitle(currentState.userInput))
                            seriesResult.addAll(seriesRepository.searchSeriesByTitle(currentState.userInput))
                        }
                    }
                } else {
                    // No hay géneros seleccionados (solo "Todos") ni texto introducido
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            searchError = "Introduce un título o selecciona uno o más géneros"
                        )
                    }
                    return@launch
                }

                // --- Actualizar Estado con Resultados ---
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        searchResultsMovies = moviesResult,
                        searchResultsSeries = seriesResult,
                        noResultsFound = moviesResult.isEmpty() && seriesResult.isEmpty()
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, searchError = "Error en la búsqueda: ${e.message}")
                }
                e.printStackTrace()
            }
        }
    }

    // --- Función para Resetear ---
    fun resetSearch() {
        _uiState.update {
            // Resetea a los valores iniciales, excepto la lista de géneros cargada
            SearchUiState(
                availableGenres = it.availableGenres,
                selectedGenre = setOf(it.availableGenres.firstOrNull() ?: "Todos"))
        }
    }
}