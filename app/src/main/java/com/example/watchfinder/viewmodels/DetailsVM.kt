package com.example.watchfinder.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchfinder.data.UiState.DetailsUiState
import com.example.watchfinder.data.dto.MovieCard // Importa DTOs si UserRepository los devuelve
import com.example.watchfinder.data.dto.SeriesCard // Importa DTOs si UserRepository los devuelve
import com.example.watchfinder.repository.MovieRepository
import com.example.watchfinder.repository.SeriesRepository
import com.example.watchfinder.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async // Para llamadas paralelas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsVM @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    // --- loadDetails MODIFICADO ---
    fun loadDetails(itemType: String, itemId: String) {
        if (_uiState.value.isLoading && (_uiState.value.movieDetail?._id == itemId || _uiState.value.seriesDetail?._id == itemId)) {
            // Ya está cargando o mostrando este item, evita recarga innecesaria
            return
        }

        viewModelScope.launch {
            _uiState.update {
                // Inicia carga, limpia datos previos
                it.copy(isLoading = true, error = null, movieDetail = null, seriesDetail = null, isFavorite = false, isSeen = false)
            }

            try {
                // 1. Carga los detalles del Item específico
                var loadedMovie: MovieCard? = null
                var loadedSeries: SeriesCard? = null
                val isMovie = itemType.equals("movie", ignoreCase = true)

                if (isMovie) {
                    loadedMovie = movieRepository.searchById(itemId)
                } else {
                    loadedSeries = seriesRepository.searchById(itemId)
                }

                // Comprueba si se encontró el item
                if (loadedMovie == null && loadedSeries == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Item no encontrado (ID: $itemId)") }
                    return@launch
                }

                // 2. Carga las listas del usuario EN PARALELO
                //    (Asume que tienes getSeenMovies y getSeenSeries en UserRepository)
                val favMoviesDeferred = async { userRepository.getFavMovies() }
                val seenMoviesDeferred = async { userRepository.getSeenMovies() } // Necesitas esto en Repo
                val favSeriesDeferred = async { userRepository.getFavSeries() }
                val seenSeriesDeferred = async { userRepository.getSeenSeries() } // Necesitas esto en Repo

                // 3. Espera resultados y extrae IDs
                val favMovieIds = favMoviesDeferred.await().mapNotNull { it._id }.toSet()
                val seenMovieIds = seenMoviesDeferred.await().mapNotNull { it._id }.toSet() // Necesitas esto en Repo
                val favSeriesIds = favSeriesDeferred.await().mapNotNull { it._id }.toSet()
                val seenSeriesIds = seenSeriesDeferred.await().mapNotNull { it._id }.toSet() // Necesitas esto en Repo


                // 4. Determina el estado inicial correcto
                val initialIsFavorite = if (isMovie) {
                    itemId in favMovieIds
                } else {
                    itemId in favSeriesIds
                }
                val initialIsSeen = if (isMovie) {
                    itemId in seenMovieIds
                } else {
                    itemId in seenSeriesIds
                }

                // 5. Actualiza el UiState final con todo
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movieDetail = loadedMovie,
                        seriesDetail = loadedSeries,
                        isFavorite = initialIsFavorite, // <-- Estado inicial REAL
                        isSeen = initialIsSeen         // <-- Estado inicial REAL
                    )
                }

            } catch (e: Exception) {
                Log.e("DetailsVM", "Error loading details or user lists: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar: ${e.message}")
                }
            }
        }
    }

    // --- toggleFavorite (sin cambios respecto a la versión corregida anterior) ---
    fun toggleFavorite() {
        val currentState = _uiState.value
        val itemId: String
        val itemType: String

        if (currentState.movieDetail != null) { /* ... obtener itemId y itemType ... */ itemId = currentState.movieDetail._id; itemType = "movie" }
        else if (currentState.seriesDetail != null) { /* ... obtener itemId y itemType ... */ itemId = currentState.seriesDetail._id; itemType = "series" }
        else { Log.w("DetailsVM", "toggleFavorite called but no item detail is loaded."); return }

        val currentlyFavorite = currentState.isFavorite
        _uiState.update { it.copy(isFavorite = !currentlyFavorite, error = null) } // Update optimista

        viewModelScope.launch {
            try {
                val success: Boolean
                if (!currentlyFavorite) {
                    // AÑADIR
                    Log.d("DetailsVM", "Attempting to fav item: $itemId ($itemType)")
                    success = userRepository.addToList(itemId, "fav", itemType)
                    if (!success) {
                        Log.w("DetailsVM", "Backend call FAILED for state: fav (add)")
                        _uiState.update { it.copy(isFavorite = currentlyFavorite, error = "No se pudo añadir a favoritos") } // Revertir
                    } else {
                        Log.d("DetailsVM", "Backend call successful for state: fav (add)")
                        _uiState.update { it.copy(error = null) }
                    }
                } else {
                    // QUITAR
                    Log.d("DetailsVM", "Attempting to remove_fav item: $itemId ($itemType)")
                    success = userRepository.removeFromList(itemId, "fav", itemType) // Necesitas esto en Repo
                    if (!success) {
                        Log.w("DetailsVM", "Backend call FAILED for state: fav (remove)")
                        _uiState.update { it.copy(isFavorite = currentlyFavorite, error = "Error al quitar de favoritos") } // Revertir
                    } else {
                        Log.d("DetailsVM", "Backend call successful for state: fav (remove)")
                        _uiState.update { it.copy(error = null) }
                    }
                }
            } catch (e: Exception) {
                Log.e("DetailsVM", "Error toggling favorite state: ${e.message}", e)
                _uiState.update { it.copy(isFavorite = currentlyFavorite, error = "Error al cambiar favorito") } // Revertir
            }
        }
    }

    // --- toggleSeen (sin cambios respecto a la versión corregida anterior) ---
    fun toggleSeen() {
        val currentState = _uiState.value
        val itemId: String
        val itemType: String

        if (currentState.movieDetail != null) { /* ... obtener itemId y itemType ... */ itemId = currentState.movieDetail._id; itemType = "movie" }
        else if (currentState.seriesDetail != null) { /* ... obtener itemId y itemType ... */ itemId = currentState.seriesDetail._id; itemType = "series" }
        else { Log.w("DetailsVM", "toggleSeen called but no item detail is loaded."); return }

        val currentlySeen = currentState.isSeen
        _uiState.update { it.copy(isSeen = !currentlySeen, error = null) } // Update optimista

        viewModelScope.launch {
            try {
                val success: Boolean
                if (!currentlySeen) {
                    // AÑADIR
                    Log.d("DetailsVM", "Attempting to seen item: $itemId ($itemType)")
                    success = userRepository.addToList(itemId, "seen", itemType)
                    if (!success) {
                        Log.w("DetailsVM", "Backend call FAILED for state: seen (add)")
                        _uiState.update { it.copy(isSeen = currentlySeen, error = "No se pudo marcar como visto") } // Revertir
                    } else {
                        Log.d("DetailsVM", "Backend call successful for state: seen (add)")
                        _uiState.update { it.copy(error = null) }
                    }
                } else {
                    // QUITAR
                    Log.d("DetailsVM", "Attempting to remove_seen item: $itemId ($itemType)")
                    success = userRepository.removeFromList(itemId, "seen", itemType) // Necesitas esto en Repo
                    if (!success) {
                        Log.w("DetailsVM", "Backend call FAILED for state: seen (remove)")
                        _uiState.update { it.copy(isSeen = currentlySeen, error = "Error al desmarcar como visto") } // Revertir
                    } else {
                        Log.d("DetailsVM", "Backend call successful for state: seen (remove)")
                        _uiState.update { it.copy(error = null) }
                    }
                }
            } catch (e: Exception) {
                Log.e("DetailsVM", "Error toggling seen state: ${e.message}", e)
                _uiState.update { it.copy(isSeen = currentlySeen, error = "Error al cambiar visto") } // Revertir
            }
        }
    }
}