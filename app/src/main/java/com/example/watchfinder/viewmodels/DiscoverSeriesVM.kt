package com.example.watchfinder.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Asegúrate que el import del UiState es el correcto
import com.example.watchfinder.data.UiState.DiscoverSeriesUiState
// Importa SeriesCard en lugar de MovieCard donde sea necesario
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.repository.SeriesRepository // Repositorio de Series
import com.example.watchfinder.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverSeriesVM @Inject constructor(
    private val userRepository: UserRepository,
    private val seriesRepository: SeriesRepository // <- Repositorio correcto
    // Quita userManager si no se usa
) : ViewModel() {

    // Usa el UiState correcto
    private val _uiState = MutableStateFlow(DiscoverSeriesUiState())
    val uiState: StateFlow<DiscoverSeriesUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Llama a los métodos correctos para series
                val favSeriesDeferred = async { userRepository.getFavSeries() }
                // ASUNCIÓN: Tienes getSeenSeries en UserRepository similar a getFavSeries
                val seenSeriesDeferred = async { userRepository.getSeenSeries() } // Necesitas esta función
                val recommendationsDeferred = async { seriesRepository.getAllSeriesCards() } // Llama al repo de series

                // Espera resultados
                val favSeriesResult = favSeriesDeferred.await()
                val seenSeriesResult = seenSeriesDeferred.await() // Necesitas esta función
                val recommendationsResult = recommendationsDeferred.await()

                // Extrae IDs (asegúrate que SeriesCard tiene _id)
                val favIds = favSeriesResult.mapNotNull { it._id }.toSet()
                val seenIds = seenSeriesResult.mapNotNull { it._id }.toSet() // Necesitas esta función

                // Actualiza el estado
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        cards = (currentState.cards + recommendationsResult).distinctBy { it._id },
                        favoriteSeriesIds = favIds, // <- Guarda IDs de series favoritas
                        seenSeriesIds = seenIds,    // <- Guarda IDs de series vistas (Necesitas la función)
                        finished = recommendationsResult.isEmpty() && currentState.cards.isNotEmpty()
                    )
                }

            } catch (e: Exception) {
                Log.e("DiscoverSeriesVM", "Error loading initial data: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar datos: ${e.message ?: "Desconocido"}")
                }
            }
        }
    }

    private fun cardSwiped(swipedCardId: String) {
        _uiState.update { currentState ->
            val remainingCards = currentState.cards.filterNot { it._id == swipedCardId }
            if (remainingCards.size < 3 && !currentState.isLoading && !currentState.finished) {
                // Lógica para cargar más si es necesario
                // getMoreRecommendations()
            }
            currentState.copy(cards = remainingCards)
        }
    }

    // --- Lógica de Swipe (Like/Dislike para Series) ---
    fun cardLiked(likedCard: SeriesCard) { // <- Parámetro es SeriesCard
        cardSwiped(likedCard._id)
        viewModelScope.launch {
            try {
                userRepository.addToList(likedCard._id, "liked", "series") // <- type es "series"
            } catch (e: Exception) {
                Log.e("DiscoverSeriesVM", "Error registering Like: ${e.message}")
            }
        }
    }

    fun cardDisliked(dislikedCard: SeriesCard) { // <- Parámetro es SeriesCard
        cardSwiped(dislikedCard._id)
        viewModelScope.launch {
            try {
                userRepository.addToList(dislikedCard._id, "disliked", "series") // <- type es "series"
            } catch (e: Exception) {
                Log.e("DiscoverSeriesVM", "Error registering Dislike: ${e.message}")
            }
        }
    }

    // --- Lógica de Toggle para Botones (Fav/Seen para Series) ---
    fun cardFav(favCard: SeriesCard) { // <- Parámetro es SeriesCard
        val currentFavIds = _uiState.value.favoriteSeriesIds // <- Usa el Set de series
        val isCurrentlyFavorite = favCard._id in currentFavIds

        viewModelScope.launch {
            try {
                val success: Boolean
                if (isCurrentlyFavorite) {
                    Log.d("DiscoverSeriesVM", "Attempting remove fav: ${favCard._id}")
                    success = userRepository.removeFromList(favCard._id, "fav", "series") // <- type es "series"
                    if (success) {
                        _uiState.update { it.copy(favoriteSeriesIds = currentFavIds - favCard._id) } // <- Actualiza Set de series
                    }
                } else {
                    Log.d("DiscoverSeriesVM", "Attempting add fav: ${favCard._id}")
                    success = userRepository.addToList(favCard._id, "fav", "series") // <- type es "series"
                    if (success) {
                        _uiState.update { it.copy(favoriteSeriesIds = currentFavIds + favCard._id) } // <- Actualiza Set de series
                    }
                }
                if (!success) {
                    Log.w("DiscoverSeriesVM", "Failed to toggle favorite state for ${favCard._id}")
                }
            } catch (e: Exception) {
                Log.e("DiscoverSeriesVM", "Error toggling Fav: ${e.message}")
            }
        }
        // No quitar tarjeta al pulsar botón
    }

    fun cardSeen(seenCard: SeriesCard) { // <- Parámetro es SeriesCard
        val currentSeenIds = _uiState.value.seenSeriesIds // <- Usa el Set de series
        val isCurrentlySeen = seenCard._id in currentSeenIds

        viewModelScope.launch {
            try {
                val success: Boolean
                if (isCurrentlySeen) {
                    Log.d("DiscoverSeriesVM", "Attempting remove seen: ${seenCard._id}")
                    success = userRepository.removeFromList(seenCard._id, "seen", "series") // <- type es "series"
                    if (success) {
                        _uiState.update { it.copy(seenSeriesIds = currentSeenIds - seenCard._id) } // <- Actualiza Set de series
                    }
                } else {
                    Log.d("DiscoverSeriesVM", "Attempting add seen: ${seenCard._id}")
                    success = userRepository.addToList(seenCard._id, "seen", "series") // <- type es "series"
                    if (success) {
                        _uiState.update { it.copy(seenSeriesIds = currentSeenIds + seenCard._id) } // <- Actualiza Set de series
                    }
                }
                if (!success) {
                    Log.w("DiscoverSeriesVM", "Failed to toggle seen state for ${seenCard._id}")
                }
            } catch (e: Exception) {
                Log.e("DiscoverSeriesVM", "Error toggling Seen: ${e.message}")
            }
        }
    }

}