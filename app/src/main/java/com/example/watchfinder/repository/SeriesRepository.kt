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
        println("--- Repository: Deserialized Series Models ---")
        apiCards.take(5).forEach { seriesModel ->
            println("Title: ${seriesModel.title}, Poster (from Model): ${seriesModel.poster}")
        }
        println("--------------------------------------------")
        val seriesCard: List<SeriesCard> = apiCards.map{
            series -> utils.seriesToCard(series)
        }
        println("--- Repository: Mapped Series DTOs ---")
        apiCards.take(5).forEach { seriesCard -> // Itera sobre la lista de DTOs resultante
            println("Title: ${seriesCard.title}, Poster (from DTO): ${seriesCard.poster}") // Accede al campo Poster del DTO
        }
        println("---------------------------------------")
        return seriesCard
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