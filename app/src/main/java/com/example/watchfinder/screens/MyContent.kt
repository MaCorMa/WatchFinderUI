package com.example.watchfinder.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Importa el icono correcto
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.data.model.Movie // Importa modelos completos
import com.example.watchfinder.data.model.Series // Importa modelos completos
import com.example.watchfinder.viewmodels.MyContentVM

@Composable
fun MyContent(
    viewModel: MyContentVM = hiltViewModel(),
    // Necesitas recibir la lambda para navegar a detalles
    onNavigateToDetail: (itemType: String, itemId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Decide qué vista mostrar según el estado
    when (uiState.currentView) {
        MyContentView.BUTTONS -> {
            MyContentButtonView(
                onMoviesClick = viewModel::loadFavoriteMovies,
                onSeriesClick = viewModel::loadFavoriteSeries,
                error = uiState.error // Pasa el error si existe
            )
        }
        MyContentView.MOVIES -> {
            MyContentListView(
                isLoading = uiState.isLoading,
                error = uiState.error,
                items = uiState.favoriteMovies, // Pasa la lista de películas
                itemType = "movie", // Indica el tipo
                onNavigateBack = viewModel::showButtonsView, // Lambda para volver
                onNavigateToDetail = onNavigateToDetail
            )
        }
        MyContentView.SERIES -> {
            MyContentListView(
                isLoading = uiState.isLoading,
                error = uiState.error,
                items = uiState.favoriteSeries, // Pasa la lista de series
                itemType = "series", // Indica el tipo
                onNavigateBack = viewModel::showButtonsView, // Lambda para volver
                onNavigateToDetail = onNavigateToDetail
            )
        }
    }
}

@Composable
fun MyContentButtonView(
    onMoviesClick: () -> Unit,
    onSeriesClick: () -> Unit,
    error: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Contenido", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        // Muestra error si existe
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(modifier = Modifier.fillMaxWidth().padding(10.dp), onClick = onMoviesClick) {
            Text("Películas Favoritas")
        }
        Button(modifier = Modifier.fillMaxWidth().padding(10.dp), onClick = onSeriesClick) {
            Text("Series Favoritas")
        }
        // Puedes añadir más botones para otras listas (Vistas, Pendientes, etc.)
    }
}

// Composable genérico para mostrar la lista (reutiliza la lógica)
@Composable
fun <T> MyContentListView(
    isLoading: Boolean,
    error: String?,
    items: List<T>,
    itemType: String, // "movie" o "series"
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (itemType: String, itemId: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Botón para volver a la selección
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Progress() // Reusa tu composable Progress
                }
            }
            error != null -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }
            items.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No tienes ${itemType}s favoritas añadidas.")
                }
            }
            else -> {
                // Reutiliza LazyVerticalGrid y SearchResultItem de Search.kt
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items) { item ->
                        // Necesitamos extraer título, poster y ID de forma genérica
                        // O adaptar SearchResultItem o crear uno nuevo
                        val title: String
                        val posterPath: String?
                        val id: String

                        when (item) {
                            is MovieCard -> {
                                title = item.Title
                                posterPath = item.Poster
                                id = item._id ?: "" // Asegúrate que _id no es null o maneja el caso
                            }
                            is SeriesCard -> {
                                title = item.Title
                                posterPath = item.Poster
                                id = item._id ?: "" // Asegúrate que _id no es null o maneja el caso
                            }
                            else -> return@items // No debería pasar si T es Movie o Series
                        }

                        if (id.isNotEmpty()) {
                            // Reutiliza el item de resultado de búsqueda de Search.kt
                            SearchResultItem( // Asegúrate que este Composable existe y es accesible
                                title = title,
                                posterPath = posterPath,
                                onClick = { onNavigateToDetail(itemType, id) }
                            )
                        }
                    }
                }
            }
        }
    }
}