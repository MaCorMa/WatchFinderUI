package com.example.watchfinder.repository

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.Utils
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

    fun searchSeriesByGenre(selectedGenre: String): Collection<SeriesCard> {

    }

    fun searchSeriesByTitle(userInput: String): Collection<SeriesCard> {

    }

    fun getSeriesDetailsById(itemId: String): SeriesCard? {

    }
}