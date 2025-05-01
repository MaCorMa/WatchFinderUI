package com.example.watchfinder.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.viewmodels.SearchVM
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.example.watchfinder.R


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Search(
    viewModel: SearchVM = hiltViewModel(),
    onNavigateToDetail: (itemType: String, itemId: String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Búsqueda", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))

        // --- Campo de Texto ---
        TextField(
            value = uiState.userInput,
            onValueChange = viewModel::onUserInputChange, // Llama al VM
            label = { Text("Introduce título...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = (uiState.selectedGenre == setOf("Todos"))
        )
        Spacer(Modifier.height(16.dp))

        // --- Selector de Tipo (Radio Buttons) ---
        SearchTypeSelectorComponent(
            selectedOption = uiState.selectedSearchType, // Usa el estado del VM
            onOptionSelected = viewModel::onSearchTypeChange // Llama al VM
        )
        Spacer(Modifier.height(16.dp))

        // --- FilterChips de Géneros ---
        Text(
            "Seleccionar Género:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            uiState.availableGenres.forEach { genre ->
                val isSelected = genre in uiState.selectedGenre

                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onGenreChipClicked(genre) },
                    label = { Text(genre) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Seleccionado",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
        Spacer(Modifier.height(24.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = viewModel::performSearch, // Llama al VM
                enabled = !uiState.isLoading // Deshabilita si está cargando
            ) {
                if (uiState.isLoading) {
                    Progress()
                } else {
                    Text("Buscar")
                }
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = viewModel::resetSearch) { // Llama al VM
                Text("Reset")
            }
        }
        Spacer(Modifier.height(16.dp))

        // --- Mostrar Errores o Indicador de "No Resultados" ---
        if (uiState.searchError != null) {
            Text(
                text = uiState.searchError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else if (uiState.noResultsFound) {
            Text(
                text = "No se encontraron resultados.",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // --- Cuadrícula de Resultados ---
        // Combina las listas de películas y series en una sola para el grid
        // Necesitarás una forma de distinguir o un tipo común si la Card es diferente
        val combinedResults = uiState.searchResultsMovies + uiState.searchResultsSeries

        if (combinedResults.isNotEmpty() && !uiState.isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp), // Ajusta el tamaño mínimo de celda
                modifier = Modifier.fillMaxSize(), // Ocupa el espacio restante
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(combinedResults) { item ->
                    when (item) {
                        is MovieCard -> SearchResultItem(
                            title = item.Title,
                            posterPath = item.Poster,
                            onClick = {
                                val itemId = item._id // Asume que _id es el identificador único
                                val itemType = "movie"
                                println("Clicked Movie ID: $itemId")
                                onNavigateToDetail(
                                    itemType,
                                    itemId
                                ) // Mejor pasar una lambda desde AppNavigation
                            })

                        is SeriesCard -> SearchResultItem(
                            title = item.Title,
                            posterPath = item.Poster,
                            onClick = {
                                val itemId = item._id // Asume que _id es el identificador único
                                val itemType = "series"
                                println("Clicked Series ID: $itemId")
                                // Llama a tu función de navegación
                                // navController.navigate("detail/$itemType/$itemId")
                                onNavigateToDetail(itemType, itemId) // Mejor pasar una lambda
                            }) // Asume que SeriesCard tiene name
                        // Añade más tipos si es necesario
                    }
                }
            }
        } else if (uiState.isLoading) {
        }
    }
}

@Composable
fun SearchResultItem(
    title: String,
    posterPath: String?,
    onClick: () -> Unit
) {
    println("SearchResultItem -> Title: $title, Poster URL: $posterPath")
    Card(
        modifier = Modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = posterPath, // Construye la URL completa
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f), // Relación de aspecto típica de póster
                // placeholder = painterResource(id = R.drawable.placeholder_image), // Opcional
                //error = painterResource(id = R.drawable.error_image), // Opcional
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                onError = { error ->
                    // BUSCA ESTE MENSAJE EN LOGCAT (con tag SearchResultItemDebug y nivel Error 'E')
                    Log.e("SearchResultItemDebug", "Coil Error loading $posterPath: ${error.result.throwable}")
                }

            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                maxLines = 2, // Limita el título a 2 líneas
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Añade puntos suspensivos
            )
        }
    }
}

@Composable
fun SearchTypeSelectorComponent(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Movies", "Series", "Both") // O los textos que prefieras

    Column(modifier = Modifier.fillMaxWidth()) {
        // Puedes descomentar el Text si quieres un título explícito aquí
        // Text("Buscar por tipo:", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            // Centra los items en la fila si quieres
            // horizontalArrangement = Arrangement.Center
        ) {
            options.forEach { optionText ->
                Row(
                    Modifier
                        .selectable(
                            selected = (selectedOption == optionText),
                            onClick = { onOptionSelected(optionText) },
                            role = Role.RadioButton
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ), // Padding alrededor de cada opción
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == optionText),
                        onClick = null // El click se maneja en la Row
                    )
                    Spacer(Modifier.width(4.dp)) // Espacio pequeño entre botón y texto
                    Text(text = optionText)
                }
            }
        }
    }
}