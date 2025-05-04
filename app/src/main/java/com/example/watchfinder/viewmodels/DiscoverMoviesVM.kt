package com.example.watchfinder.viewmodels

import android.util.Log // Importa Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchfinder.data.UiState.DiscoverMoviesUiState
// Quita UserManager si no se usa aquí directamente
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.repository.MovieRepository
import com.example.watchfinder.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async // Para llamadas paralelas
import kotlinx.coroutines.awaitAll // Para esperar resultados paralelos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverMoviesVM @Inject constructor(
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository
    // Quita userManager si no se usa
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverMoviesUiState())
    val uiState: StateFlow<DiscoverMoviesUiState> = _uiState.asStateFlow()

    // Quita _contentType y selectContentType si no son necesarios aquí

    init {
        // Llama a la función combinada al iniciar
        loadInitialData()
    }

    // Función para cargar todo lo necesario inicialmente
    fun loadInitialData() {
        if (_uiState.value.isLoading) return // Evita recargas
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Lanza las llamadas en paralelo para eficiencia
                val favMoviesDeferred = async { userRepository.getFavMovies() }
                // ASUNCIÓN: Tienes getSeenMovies en UserRepository similar a getFavMovies
                val seenMoviesDeferred = async { userRepository.getSeenMovies() }
                val recommendationsDeferred = async { movieRepository.getAllMovieCards() }

                // Espera a que todas terminen
                //val (favMoviesResult, seenMoviesResult, recommendationsResult) = awaitAll(favMoviesDeferred, seenMoviesDeferred, recommendationsDeferred)
                // Descomenta la línea anterior y comenta las siguientes si tienes awaitAll disponible y configurado
                val favMoviesResult = favMoviesDeferred.await()
                val seenMoviesResult = seenMoviesDeferred.await() // Necesitas esta función en el repo
                val recommendationsResult = recommendationsDeferred.await()


                // Extrae los IDs a Sets
                val favIds = favMoviesResult.mapNotNull { it._id }.toSet()
                val seenIds = seenMoviesResult.mapNotNull { it._id }.toSet() // Necesitas esta función en el repo

                // Actualiza el estado con todos los datos
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        // Combina las recomendaciones actuales con las nuevas, evitando duplicados
                        cards = (currentState.cards + recommendationsResult).distinctBy { it._id },
                        favoriteMovieIds = favIds,
                        seenMovieIds = seenIds, // Necesitas esta función en el repo
                        finished = recommendationsResult.isEmpty() && currentState.cards.isNotEmpty() // Considera finished si no vienen nuevas y ya tenías algunas
                    )
                }

            } catch (e: Exception) {
                Log.e("DiscoverMoviesVM", "Error loading initial data: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar datos: ${e.message ?: "Desconocido"}"
                    )
                }
            }
        }
    }

    // cardSwiped ahora solo elimina la tarjeta de la lista visual
    private fun cardSwiped(swipedCardId: String) {
        _uiState.update { currentState ->
            val remainingCards = currentState.cards.filterNot { it._id == swipedCardId }
            // Lógica para pedir más tarjetas si quedan pocas
            if (remainingCards.size < 3 && !currentState.isLoading && !currentState.finished) {
                // Podrías llamar a una función que solo traiga más 'cards' o re-llamar a loadInitialData
                // pero cuidado con volver a traer favs/seen innecesariamente.
                // getMoreRecommendations() // Necesitarías una función específica para esto
            }
            currentState.copy(cards = remainingCards)
        }
    }

    // --- Lógica de Swipe (Like/Dislike) ---
    fun cardLiked(likedCard: MovieCard) {
        cardSwiped(likedCard._id) // Elimina de la UI
        viewModelScope.launch {
            try {
                userRepository.addToList(likedCard._id, "liked", "movie")
                // Opcional: Añadir a la lista local de likedIds si quieres coherencia inmediata
                // _uiState.update { it.copy(likedMovieIds = it.likedMovieIds + likedCard._id) }
            } catch (e: Exception) {
                Log.e("DiscoverMoviesVM", "Error registering Like: ${e.message}")
            }
        }
    }

    fun cardDisliked(dislikedCard: MovieCard) {
        cardSwiped(dislikedCard._id) // Elimina de la UI
        viewModelScope.launch {
            try {
                userRepository.addToList(dislikedCard._id, "disliked", "movie")
            } catch (e: Exception) {
                Log.e("DiscoverMoviesVM", "Error registering Dislike: ${e.message}")
            }
        }
    }


    fun cardFav(favCard: MovieCard) {
        val currentFavIds = _uiState.value.favoriteMovieIds
        val isCurrentlyFavorite = favCard._id in currentFavIds

        viewModelScope.launch {
            try {
                val success: Boolean
                if (isCurrentlyFavorite) {
                    // Quitar de favoritos
                    Log.d("DiscoverMoviesVM", "Attempting remove fav: ${favCard._id}")
                    success = userRepository.removeFromList(favCard._id, "fav", "movie") // Necesitas esta función
                    if (success) {
                        // Actualizar estado local quitando el ID
                        _uiState.update { it.copy(favoriteMovieIds = currentFavIds - favCard._id) }
                    }
                } else {
                    // Añadir a favoritos
                    Log.d("DiscoverMoviesVM", "Attempting add fav: ${favCard._id}")
                    success = userRepository.addToList(favCard._id, "fav", "movie")
                    if (success) {
                        // Actualizar estado local añadiendo el ID
                        _uiState.update { it.copy(favoriteMovieIds = currentFavIds + favCard._id) }
                    }
                }
                if (!success) {
                    Log.w("DiscoverMoviesVM", "Failed to toggle favorite state for ${favCard._id}")
                    // Opcional: Mostrar error temporal en UI
                }
            } catch (e: Exception) {
                Log.e("DiscoverMoviesVM", "Error toggling Fav: ${e.message}")
                // Opcional: Mostrar error
            }
        }
        // No quitamos la tarjeta de la UI al pulsar el botón, solo con swipe
        // cardSwiped(favCard._id) // <-- NO LLAMAR A cardSwiped aquí
    }

    fun cardSeen(seenCard: MovieCard) {
        val currentSeenIds = _uiState.value.seenMovieIds
        val isCurrentlySeen = seenCard._id in currentSeenIds

        viewModelScope.launch {
            try {
                val success: Boolean
                if (isCurrentlySeen) {
                    // Quitar de vistos
                    Log.d("DiscoverMoviesVM", "Attempting remove seen: ${seenCard._id}")
                    success = userRepository.removeFromList(seenCard._id, "seen", "movie") // Necesitas esta función
                    if (success) {
                        _uiState.update { it.copy(seenMovieIds = currentSeenIds - seenCard._id) }
                    }
                } else {
                    // Añadir a vistos
                    Log.d("DiscoverMoviesVM", "Attempting add seen: ${seenCard._id}")
                    success = userRepository.addToList(seenCard._id, "seen", "movie")
                    if (success) {
                        _uiState.update { it.copy(seenMovieIds = currentSeenIds + seenCard._id) }
                    }
                }
                if (!success) {
                    Log.w("DiscoverMoviesVM", "Failed to toggle seen state for ${seenCard._id}")
                    // Opcional: Mostrar error temporal en UI
                }
            } catch (e: Exception) {
                Log.e("DiscoverMoviesVM", "Error toggling Seen: ${e.message}")
                // Opcional: Mostrar error
            }
        }
    }

}