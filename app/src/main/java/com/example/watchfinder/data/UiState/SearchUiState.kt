package com.example.watchfinder.data.UiState

import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard

data class SearchUiState(
    // --- Estados de los Controles ---
    val userInput: String = "",
    val selectedSearchType: String = "Both",
    val availableGenres: List<String> = listOf("Todos"), // Lista inicial, se cargará del VM
    val selectedGenre: Set<String> = setOf("Todos"), // Género seleccionado (FilterChip), empieza con "Todos"

    val isLoading: Boolean = false,
    val triggerNavigationToResults: Boolean = false,
    val navigationResultsMovies: List<MovieCard> = emptyList(),
    val navigationResultsSeries: List<SeriesCard> = emptyList(),
    val searchError: String? = null,
    val noResultsFound: Boolean = false
)
