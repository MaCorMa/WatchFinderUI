package com.example.watchfinder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchfinder.data.UiState.DiscoverMoviesUiState
import com.example.watchfinder.data.UiState.DiscoverSeriesUiState
import com.example.watchfinder.data.UserManager
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.repository.SeriesRepository
import com.example.watchfinder.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverSeriesVM @Inject constructor(
    private val userRepository: UserRepository,
    private val seriesRepository: SeriesRepository,
    private val userManager: UserManager
) : ViewModel() {


    private val _uiState = MutableStateFlow(DiscoverSeriesUiState()) // Inicializa con tu state class
    val uiState: StateFlow<DiscoverSeriesUiState> = _uiState.asStateFlow()


    init {
        getRecomendationsSeries()
    }


    fun getRecomendationsSeries() {
        if (_uiState.value.isLoading || _uiState.value.finished) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val recomends = seriesRepository.getAllSeriesCards()


                if (recomends.isNotEmpty()) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            cards = (currentState.cards + recomends).distinctBy { it._id },
                            finished = false
                        )
                    }
                } else {
                    // Si no vienen nuevas recomendaciones...
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            finished = currentState.cards.isEmpty()
                        )
                    }
                }
            } catch (e: Exception) {
                // Captura errores que pueda lanzar el repositorio (red, etc.)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar: ${e.message ?: "Desconocido"}"
                    )
                }
                println("Error en fetchRecommendations: ${e.message}") // Log para depuración
            }
        }
    }

    private fun cardSwiped(swipedCard: SeriesCard) {
        _uiState.update { currentState ->
            val remainingCards = currentState.cards.filterNot { it._id == swipedCard._id }
            // Si quedan pocas tarjetas y no estamos cargando ni hemos terminado, pide más
            if (remainingCards.size < 3 && !currentState.isLoading && !currentState.finished) {
                getRecomendationsSeries()
            }
            currentState.copy(cards = remainingCards)
        }
    }

    // Llamado desde la UI para un swipe de LIKE (derecha)
    fun cardLiked(likedCard: SeriesCard) {
        cardSwiped(likedCard) // Ejecuta lógica común
        viewModelScope.launch {
            try {
                // Informa al backend (a través del repositorio)
                userRepository.addToList(likedCard._id, "liked", "series")
            } catch (e: Exception) {
                println("Error al registrar Like: ${e.message}")
                // Quizás quieras mostrar un error temporal en la UI
            }
        }
    }

    // Llamado desde la UI para un swipe de DISLIKE (izquierda)
    fun cardDisliked(dislikedCard: SeriesCard) {
        cardSwiped(dislikedCard) // Ejecuta lógica común
        viewModelScope.launch {
            try {
                userRepository.addToList(dislikedCard._id, "disliked", "series")
            } catch (e: Exception) {
                println("Error al registrar Dislike: ${e.message}")
                // Quizás quieras mostrar un error temporal en la UI
            }
        }
    }

    fun cardSeen(cardSeen: SeriesCard) {
        cardSwiped(cardSeen) // Ejecuta lógica común
        viewModelScope.launch {
            try {
                userRepository.addToList(cardSeen._id, "seen", "series")
            } catch (e: Exception) {
                println("Error al registrar Dislike: ${e.message}")
                // Quizás quieras mostrar un error temporal en la UI
            }
        }
    }

    fun cardFav(cardFav: SeriesCard) {
        cardSwiped(cardFav) // Ejecuta lógica común
        viewModelScope.launch {
            try {
                userRepository.addToList(cardFav._id, "fav", "series")
            } catch (e: Exception) {
                println("Error al registrar Dislike: ${e.message}")
                // Quizás quieras mostrar un error temporal en la UI
            }
        }
    }
}