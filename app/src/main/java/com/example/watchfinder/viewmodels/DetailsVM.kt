package com.example.watchfinder.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchfinder.data.UiState.DetailsUiState
import com.example.watchfinder.repository.MovieRepository
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
class DetailsVM @Inject constructor(
    // 2. Inyecta los repositorios necesarios
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // 3. Crea el StateFlow para la UI
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    // 4. Función para cargar los detalles
    fun loadDetails(itemType: String, itemId: String) {
        // Evita recargar si ya se está cargando
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            // Pone el estado en "cargando" y limpia datos/errores anteriores
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    movieDetail = null,
                    seriesDetail = null
                )
            }

            try {
                // Decide qué repositorio llamar según el tipo
                when (itemType.lowercase()) { // Usa lowercase para comparar sin importar mayúsculas/minúsculas
                    "movie" -> {
                        // Llama al repositorio para obtener detalles de la película
                        val movie = movieRepository.searchById(itemId) // Asegúrate que esta función exista
                        _uiState.update { it.copy(isLoading = false, movieDetail = movie) }
                    }
                    "series" -> {
                        // Llama al repositorio para obtener detalles de la serie
                        val series = seriesRepository.searchById(itemId) // Asegúrate que esta función exista
                        _uiState.update { it.copy(isLoading = false, seriesDetail = series) }
                    }
                    else -> {
                        // Tipo desconocido
                        _uiState.update { it.copy(isLoading = false, error = "Tipo de contenido desconocido: $itemType") }
                    }
                }
            } catch (e: Exception) {
                // Maneja errores de red o del repositorio
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar detalles: ${e.message}")
                }
                e.printStackTrace() // Útil para depurar
            }
        }
    }

// En DetailsVM.kt

    fun addToFavorites(itemId: String, itemType: String) {
        viewModelScope.launch {
            // Llama al método del repo que ahora devuelve Boolean
            val success = try {
                userRepository.addToList(itemId, "fav", itemType)
            } catch (e: Exception) { // Captura por si el repo lanzara alguna excepción no esperada
                Log.e("DetailsVM", "Error calling repository addToList for fav: ${e.message}", e)
                false
            }

            if (success) {
                println("Añadido a Favoritos (Confirmado por API): $itemId ($itemType)")
                // Aquí podrías actualizar la UI si fuera necesario (ej. mostrar un Snackbar)
            } else {
                println("FALLO al añadir a Favoritos (API o Red): $itemId ($itemType)")
                // Actualizar UI para mostrar error
                _uiState.update { it.copy(error = "No se pudo añadir a favoritos") } // Ejemplo
            }
        }
    }

    fun addToSeen(itemId: String, itemType: String) {
        viewModelScope.launch {
            val success = try {
                userRepository.addToList(itemId, "seen", itemType)
            } catch (e: Exception) {
                Log.e("DetailsVM", "Error calling repository addToList for seen: ${e.message}", e)
                false
            }

            if (success) {
                println("Añadido a Vistos (Confirmado por API): $itemId ($itemType)")
                // Actualizar UI si es necesario
            } else {
                println("FALLO al añadir a Vistos (API o Red): $itemId ($itemType)")
                // Actualizar UI para mostrar error
                _uiState.update { it.copy(error = "No se pudo añadir a vistos") } // Ejemplo
            }
        }
    }
}