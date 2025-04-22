package com.example.watchfinder.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watchfinder.viewmodels.DetailsVM
import androidx.compose.ui.Modifier

@Composable
fun DetailScreen(
    itemType: String,
    itemId: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailsVM = hiltViewModel()
) {
    // Llama al ViewModel para que cargue los detalles basado en type/id
    LaunchedEffect(itemType, itemId) {
        viewModel.loadDetails(itemType, itemId)
    }

    val uiState by viewModel.uiState.collectAsState() // Asume que DetailViewModel tiene un uiState

    Box (modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        when {
            uiState.isLoading -> {
                Progress()
            }
            uiState.error != null -> {
                Text("Error: ${uiState.error}")
                // Podrías añadir un botón para reintentar o volver
            }
            uiState.movieDetail != null -> {
                // ¡REUTILIZA TU COMPOSABLE EXISTENTE!
                MovieCard(movie = uiState.movieDetail!!)
                // Añade un botón "Atrás" si es necesario
            }
            uiState.seriesDetail != null -> {
                // ¡REUTILIZA TU COMPOSABLE EXISTENTE!
                SeriesCard(series = uiState.seriesDetail!!) // Necesitarás SeriesCard composable
                // Añade un botón "Atrás" si es necesario
            }
            else -> {
                Text("No se encontraron detalles.")
            }
        }
        // Podrías añadir un botón flotante o en una TopAppBar para volver atrás
        // FloatingActionButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "") }
    }
}