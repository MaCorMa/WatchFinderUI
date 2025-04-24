package com.example.watchfinder.data.UiState

data class SearchUiState(
    // --- Estados de los Controles ---
    val userInput: String = "",
    val selectedSearchType: String = "Both",
    val availableGenres: List<String> = listOf("Todos"), // Lista inicial, se cargará del VM
    val selectedGenre: Set<String> = setOf("Todos"), // Género seleccionado (FilterChip), empieza con "Todos"

    // --- Estado de la Búsqueda y Resultados ---
    val isLoading: Boolean = false, // Para mostrar indicador de carga
    val searchResultsMovies: List<Any> = emptyList(), // Lista para resultados de películas (Usa tu DTO, ej: MovieCard)
    val searchResultsSeries: List<Any> = emptyList(), // Lista para resultados de series (Usa tu DTO, ej: SeriesCard)
    // O una lista única si tu API devuelve resultados mezclados
    // val searchResults: List<Any> = emptyList()
    val searchError: String? = null, // Para mostrar mensajes de error
    val noResultsFound: Boolean = false // Para indicar si la búsqueda no encontró nada
)