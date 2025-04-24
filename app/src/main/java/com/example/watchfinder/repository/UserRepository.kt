package com.example.watchfinder.repository

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.Utils
import com.example.watchfinder.data.dto.Item
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.data.model.Movie
import com.example.watchfinder.data.prefs.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val utils: Utils
){


    suspend fun addToList(id: String, state: String, type: String): String {
        val item = Item(id, state, type)
        apiService.addToList(item)
        return "Enviada"
    }

    suspend fun getFavMovies(): List<MovieCard> {
        val apiCards = apiService.getFavMovies()
        return apiCards.map{
                movie -> utils.movieToCard(movie)
        }
    }

    suspend fun getFavSeries(): List<SeriesCard> {
        val apiCards = apiService.getFavSeries()
        return apiCards.map{
                series -> utils.seriesToCard(series)
        }
    }
}
