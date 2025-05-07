package com.example.watchfinder.repository

import android.util.Log
import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.Utils
import com.example.watchfinder.data.dto.Item
import com.example.watchfinder.data.dto.ProfileImageUpdateResponse
import com.example.watchfinder.data.dto.UserData
import com.example.watchfinder.data.model.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.IOException
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
    // trae el user del backend
    suspend fun getUserProfile(): User {
        val response: Response<User> = apiService.getProfile()
        if (response.isSuccessful) {
            // extrae User del body de la Response
            val user = response.body()
            if (user == null) {
                throw IllegalStateException("user body es null")
            }
            return user
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = "Error recogiendo profile: ${response.code()} ${response.message()} - $errorBody"
            throw IOException(errorMessage)
        }
    }
    // sube imagen del user
    suspend fun uploadProfileImage(imageBytes: ByteArray): Result<String> {
        return try {
            // Create a MultipartBody.Part from the image bytes
            val requestFile = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", "profile.jpg", requestFile) // "image" should match backend field name

            // Assuming apiService has a suspend function like uploadProfileImage(image: MultipartBody.Part): Response<ImageUrlResponse>
            // where ImageUrlResponse is a data class like data class ImageUrlResponse(val profileImageUrl: String)
            val response: Response<ProfileImageUpdateResponse> = apiService.uploadProfileImage(body)

            if (response.isSuccessful) {
                val imageUrlResponse = response.body()
                val newImageUrl = imageUrlResponse?.profileImageUrl
                Log.d("UserRepository", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA Image upload response: $imageUrlResponse, URL: $newImageUrl")
                if (newImageUrl != null) {
                    Result.success(newImageUrl)
                } else {
                    Result.failure(IOException("Image upload successful but no image URL returned"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(
                    "UserRepository",
                    "Error uploading image: ${response.code()} ${response.message()} - $errorBody"
                )
                Result.failure(IOException("Error uploading image: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getProfileImageUrl(): Result<String> {
        return try {
            val response = apiService.getImageUrl()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.imageUrl.isNotEmpty()) {
                    Log.d("UserRepository", "getProfileImageUrl: Got image URL: ${body.imageUrl}")
                    Result.success(body.imageUrl)
                } else {
                    Result.failure(IOException("No image URL found in response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepository", "getProfileImageUrl: Error: ${response.code()} ${response.message()} - $errorBody")
                Result.failure(IOException("Error fetching image URL: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "getProfileImageUrl: Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(email: String?, username: String?): Result<User> {
        return try {
            val requestBody = UserData(email = email, username = username)
            val response: Response<UserData> = apiService.updateProfile(requestBody)

            if (response.isSuccessful) {
                val updatedUserData = response.body()
                if (updatedUserData != null) {
                    //después de actualizar user, recupera user del backend
                    val user = getUserProfile()
                    Result.success(user)
                } else {
                    Result.failure(IllegalStateException("Actualización pero no ha devuelto UserData"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(IOException("Error actualizando perfil: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val response = apiService.deleteAccount()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(IOException("Error al eliminar la cuenta: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}


