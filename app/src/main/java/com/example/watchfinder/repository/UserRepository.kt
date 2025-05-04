package com.example.watchfinder.repository

import android.util.Log
import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.Utils
import com.example.watchfinder.data.dto.Item
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.data.model.Movie
import com.example.watchfinder.data.prefs.TokenManager
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val utils: Utils
){


    suspend fun addToList(id: String, state: String, type: String): Boolean { // Devuelve Boolean
        val item = Item(id, state, type) // Asegúrate que Item DTO existe
        return try {
            val response: Response<Void> = apiService.addToList(item) // Llama a la API
            if (response.isSuccessful) {
                Log.i("UserRepository", "addToList successful for item $id")
                true // La llamada HTTP tuvo éxito (código 2xx)
            } else {
                // La llamada HTTP falló (código 4xx, 5xx)
                Log.w("UserRepository", "addToList failed for item $id - Code: ${response.code()}, Message: ${response.message()}")
                // Podrías intentar leer response.errorBody() aquí si necesitas más detalles
                false
            }
        } catch (e: Exception) {
            // Error de red, de parsing (aunque con Void no debería parsear), etc.
            Log.e("UserRepository", "Exception in addToList for item $id: ${e.message}", e)
            false // Falló
        }
    }

    suspend fun removeFromList(id: String, state: String, type: String): Boolean { // Devuelve Boolean
        val item = Item(id, state, type) // Asegúrate que Item DTO existe
        return try {
            val response: Response<Void> = apiService.removeFromList(item) // Llama a la API
            if (response.isSuccessful) {
                Log.i("UserRepository", "remove successful for item $id")
                true // La llamada HTTP tuvo éxito (código 2xx)
            } else {
                // La llamada HTTP falló (código 4xx, 5xx)
                Log.w("UserRepository", "addToList failed for item $id - Code: ${response.code()}, Message: ${response.message()}")
                // Podrías intentar leer response.errorBody() aquí si necesitas más detalles
                false
            }
        } catch (e: Exception) {
            // Error de red, de parsing (aunque con Void no debería parsear), etc.
            Log.e("UserRepository", "Exception in addToList for item $id: ${e.message}", e)
            false // Falló
        }
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

    suspend fun getSeenMovies(): List<MovieCard> {
        val apiCards = apiService.getSeenMovies()
        return apiCards.map{
                movie -> utils.movieToCard(movie)
        }
    }

    suspend fun getSeenSeries(): List<SeriesCard> {
        val apiCards = apiService.getSeenSeries()
        return apiCards.map{
                series -> utils.seriesToCard(series)
        }
    }
}
