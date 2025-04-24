package com.example.watchfinder.repository

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.Utils
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.data.prefs.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesRepository@Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val utils: Utils
) {

    suspend fun getAllSeriesCards(): List<SeriesCard> {
        val apiCards = apiService.getSeries()
        return apiCards.map{
            series -> utils.seriesToCard(series)
        }
    }

    suspend fun searchSeriesByGenre(selectedGenres: List<String>): Collection<SeriesCard> {
        val apiCards = apiService.getSeriesByGenre(selectedGenres)
        return apiCards.map{
                series -> utils.seriesToCard(series)
        }
    }

    suspend fun searchSeriesByTitle(userInput: String): Collection<SeriesCard> {
        val apiCards = apiService.getSeriesByTitle(userInput)
        return apiCards.map{
                series -> utils.seriesToCard(series)
        }
    }

    suspend fun searchById(id: String): SeriesCard {
        return utils.seriesToCard(apiService.getSeriesById(id))
    }
}