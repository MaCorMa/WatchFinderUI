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
                        selectedGenre = allGenres.firstOrNull() ?: "Todos" // Selecciona "Todos" o el primero si existe
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
        _uiState.update { it.copy(userInput = query, searchError = null, noResultsFound = false) }
        // Opcional: añadir debounce si quieres buscar mientras escribe
    }

    fun onSearchTypeChange(type: String) {
        // 'type' debería ser "Movies", "Series", o "Both"
        _uiState.update { it.copy(selectedSearchType = type, searchError = null, noResultsFound = false) }
    }

    fun onGenreChange(genre: String) {
        _uiState.update {
            it.copy(
                selectedGenre = genre,
                // Limpia el input de texto si se selecciona un género específico
                userInput = if (genre != "Todos") "" else it.userInput,
                searchError = null,
                noResultsFound = false
            )
        }
    }

    // --- Función para ejecutar la búsqueda ---

    fun performSearch() {
        searchJob?.cancel() // Cancela la búsqueda anterior si existe

        _uiState.update { it.copy(isLoading = true, searchError = null, noResultsFound = false, searchResultsMovies = emptyList(), searchResultsSeries = emptyList()) }

        val currentState = _uiState.value // Captura el estado actual para la coroutine

        searchJob = viewModelScope.launch {
            try {
                val moviesResult = mutableListOf<MovieCard>()
                val seriesResult = mutableListOf<SeriesCard>()

                if (currentState.selectedGenre != "Todos") {
                    // --- Búsqueda por Género ---
                    // Necesitarás endpoints/funciones de repo que busquen por género
                    // y opcionalmente filtren por tipo (Movies/Series/Both)
                    println("Searching by Genre: ${currentState.selectedGenre}, Type: ${currentState.selectedSearchType}")
                    // Ejemplo (tendrás que adaptar a tus repositorios):
                    when (currentState.selectedSearchType) {
                        "Movies" -> moviesResult.addAll(movieRepository.searchMoviesByGenre(currentState.selectedGenre))
                        "Series" -> seriesResult.addAll(seriesRepository.searchSeriesByGenre(currentState.selectedGenre))
                        "Both" -> {
                            moviesResult.addAll(movieRepository.searchMoviesByGenre(currentState.selectedGenre))
                            seriesResult.addAll(seriesRepository.searchSeriesByGenre(currentState.selectedGenre))
                            // O una llamada combinada si tu API lo permite:
                            // val combinedResults = searchRepository.searchByGenre(currentState.selectedGenre)
                            // // ... luego separar movies y series ...
                        }
                    }
                } else if (currentState.userInput.isNotBlank()) {
                    // --- Búsqueda por Título ---
                    println("Searching by Title: ${currentState.userInput}, Type: ${currentState.selectedSearchType}")
                    when (currentState.selectedSearchType) {
                        "Movies" -> moviesResult.addAll(movieRepository.searchMoviesByTitle(currentState.userInput))
                        "Series" -> seriesResult.addAll(seriesRepository.searchSeriesByTitle(currentState.userInput))
                        "Both" -> {
                            // Lanza ambas búsquedas (pueden ser en paralelo con async/await)
                            moviesResult.addAll(movieRepository.searchMoviesByTitle(currentState.userInput))
                            seriesResult.addAll(seriesRepository.searchSeriesByTitle(currentState.userInput))
                            // O una llamada combinada:
                            // val combinedResults = searchRepository.searchByTitle(currentState.userInput)
                            // // ... luego separar ...
                        }
                    }
                } else {
                    // No hay género seleccionado ni texto introducido -> No hacer nada o mostrar mensaje
                    _uiState.update { it.copy(isLoading = false, searchError = "Introduce un título o selecciona un género") }
                    return@launch // Salir de la coroutine
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
                // --- Manejo de Errores ---
                _uiState.update {
                    it.copy(isLoading = false, searchError = "Error en la búsqueda: ${e.message}")
                }
                e.printStackTrace() // Para depuración
            }
        }
    }

    // --- Función para Resetear ---
    fun resetSearch() {
        _uiState.update {
            // Resetea a los valores iniciales, excepto la lista de géneros cargada
            SearchUiState(availableGenres = it.availableGenres, selectedGenre = it.availableGenres[0])
        }
    }
}