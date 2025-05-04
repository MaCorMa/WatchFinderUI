// WF/watchfinderAndroid/viewmodels/SearchVM.kt
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
    private val genreRepository: GenreRepository,
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAvailableGenres()
    }

    private fun loadAvailableGenres() {
        viewModelScope.launch {
            try {
                val genres: List<String> = genreRepository.getAllGenres()
                _uiState.update {
                    val allGenres = listOf("Todos") + genres
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

    // --- Funciones para actualizar el estado desde la UI (sin cambios) ---
    fun onUserInputChange(query: String) {
        _uiState.update {
            it.copy(
                userInput = query,
                searchError = null,
                noResultsFound = false,
                selectedGenre = setOf("Todos"),
                triggerNavigationToResults = false // Resetea el trigger al escribir
            )
        }
    }

    fun onSearchTypeChange(type: String) {
        _uiState.update {
            it.copy(
                selectedSearchType = type,
                searchError = null,
                noResultsFound = false,
                triggerNavigationToResults = false // Resetea el trigger
            )
        }
    }

    fun onGenreChipClicked(genre: String) {
        _uiState.update { currentState ->
            val currentSelection = currentState.selectedGenre.toMutableSet()
            val isTodosSelected = currentSelection.contains("Todos")

            if (genre == "Todos") {
                currentSelection.clear()
                currentSelection.add("Todos")
            } else {
                currentSelection.remove("Todos")
                if (currentSelection.contains(genre)) {
                    currentSelection.remove(genre)
                } else {
                    currentSelection.add(genre)
                }
                if (currentSelection.isEmpty()) {
                    currentSelection.add("Todos")
                }
            }

            currentState.copy(
                selectedGenre = currentSelection,
                userInput = "",
                searchError = null,
                noResultsFound = false,
                triggerNavigationToResults = false // Resetea el trigger
            )
        }
    }

    // --- Función para ejecutar la búsqueda (MODIFICADA) ---
    fun performSearch() {
        searchJob?.cancel() // Cancela búsqueda anterior
        _uiState.update {
            it.copy(
                isLoading = true,
                searchError = null,
                noResultsFound = false,
                triggerNavigationToResults = false, // Asegura que está en false al empezar
                navigationResultsMovies = emptyList(), // Limpia resultados anteriores
                navigationResultsSeries = emptyList()
            )
        }
        val currentState = _uiState.value // Obtiene estado actual para usar en la corrutina

        searchJob = viewModelScope.launch {
            try {
                // Usa List<MovieCard> y List<SeriesCard> directamente
                val moviesResult = mutableListOf<MovieCard>()
                val seriesResult = mutableListOf<SeriesCard>()

                val genresToSearch = currentState.selectedGenre.filter { it != "Todos" }.toList()

                if (genresToSearch.isNotEmpty()) {
                    // Búsqueda por Géneros
                    println("Searching by Genres: ${genresToSearch}, Type: ${currentState.selectedSearchType}")
                    when (currentState.selectedSearchType) {
                        "Movies" -> moviesResult.addAll(movieRepository.searchMoviesByGenre(genresToSearch))
                        "Series" -> seriesResult.addAll(seriesRepository.searchSeriesByGenre(genresToSearch).filterIsInstance<SeriesCard>()) // Asegura el tipo
                        "Both" -> {
                            moviesResult.addAll(movieRepository.searchMoviesByGenre(genresToSearch))
                            seriesResult.addAll(seriesRepository.searchSeriesByGenre(genresToSearch).filterIsInstance<SeriesCard>()) // Asegura el tipo
                        }
                    }
                } else if (currentState.userInput.isNotBlank()) {
                    // Búsqueda por Título
                    println("Searching by Title: ${currentState.userInput}, Type: ${currentState.selectedSearchType}")
                    when (currentState.selectedSearchType) {
                        "Movies" -> moviesResult.addAll(movieRepository.searchMoviesByTitle(currentState.userInput))
                        "Series" -> seriesResult.addAll(seriesRepository.searchSeriesByTitle(currentState.userInput).filterIsInstance<SeriesCard>()) // Asegura el tipo
                        "Both" -> {
                            moviesResult.addAll(movieRepository.searchMoviesByTitle(currentState.userInput))
                            seriesResult.addAll(seriesRepository.searchSeriesByTitle(currentState.userInput).filterIsInstance<SeriesCard>()) // Asegura el tipo
                        }
                    }
                } else {
                    // No hay input válido
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            searchError = "Introduce un título o selecciona uno o más géneros"
                        )
                    }
                    return@launch // Termina la corrutina aquí
                }

                // --- Actualizar Estado para NAVEGACIÓN ---
                val noResults = moviesResult.isEmpty() && seriesResult.isEmpty()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        noResultsFound = noResults, // Actualiza si no hubo resultados
                        // Guarda los resultados en las listas de navegación
                        navigationResultsMovies = moviesResult,
                        navigationResultsSeries = seriesResult,
                        // Activa el flag para navegar SOLO SI hay resultados
                        triggerNavigationToResults = !noResults
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, searchError = "Error en la búsqueda: ${e.message}")
                }
                e.printStackTrace() // Imprime el error en Logcat para depuración
            }
        }
    }

    // --- NUEVA: Función para resetear el flag de navegación ---
    fun onResultsNavigated() {
        _uiState.update { it.copy(triggerNavigationToResults = false) }
    }


    // --- Función para Resetear (Sin cambios grandes, pero asegura limpiar navigation state) ---
    fun resetSearch() {
        searchJob?.cancel() // Cancela cualquier búsqueda en curso
        _uiState.update {
            // Resetea a los valores iniciales, excepto la lista de géneros cargada
            SearchUiState(
                availableGenres = it.availableGenres,
                selectedGenre = setOf(it.availableGenres.firstOrNull() ?: "Todos")
                // Los demás campos volverán a sus valores por defecto (false, emptyList, etc.)
            )
        }
    }
}