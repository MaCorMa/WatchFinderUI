package com.example.watchfinder.repository

import com.example.watchfinder.api.RetroFitClient
import com.example.watchfinder.data.Movie

class MovieRepository {

    private val apiService = RetroFitClient.instance

    suspend fun getAllMovies(): List<Movie> {
        return apiService.getMovies()
    }
}
