// WF/watchfinderAndroid/screens/SearchResultsScreen.kt
package com.example.watchfinder.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard

@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun SearchResultsScreen(
    resultsMovies: List<MovieCard>, // Recibe la lista de películas
    resultsSeries: List<SeriesCard>, // Recibe la lista de series
    onNavigateBack: () -> Unit, // Lambda para volver atrás
    onNavigateToDetail: (itemType: String, itemId: String) -> Unit // Lambda para ir a detalles
) {
    val combinedResults = resultsMovies + resultsSeries // Combina las listas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                // Puedes añadir colores si tu tema no los define bien
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica el padding del Scaffold
                .padding(horizontal = 16.dp) // Padding adicional si es necesario
        ) {
            if (combinedResults.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron resultados.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(combinedResults) { item ->
                        when (item) {
                            is MovieCard -> SearchResultItem( // Reutiliza tu Composable existente
                                title = item.Title,
                                posterPath = item.Poster,
                                onClick = { onNavigateToDetail("movie", item._id) } // Pasa tipo y ID
                            )
                            is SeriesCard -> SearchResultItem( // Reutiliza tu Composable existente
                                title = item.Title,
                                posterPath = item.Poster,
                                onClick = { onNavigateToDetail("series", item._id) } // Pasa tipo y ID
                            )
                        }
                    }
                }
            }
        }
    }
}

// Nota: Asegúrate de que SearchResultItem (de Search.kt) sea accesible aquí
// o muévelo a un paquete común si es necesario.