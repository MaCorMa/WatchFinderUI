package com.example.watchfinder.data.UiState

import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard

data class DetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val movieDetail: MovieCard? = null,
    val seriesDetail: SeriesCard? = null
 )