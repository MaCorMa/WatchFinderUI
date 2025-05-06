package com.example.watchfinder.api

import com.example.watchfinder.data.dto.ChangePasswordRequest
import com.example.watchfinder.data.dto.ForgotPasswordRequest
import com.example.watchfinder.data.dto.ImageUrlResponse
import com.example.watchfinder.data.dto.Item
import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.dto.LoginResponse
import com.example.watchfinder.data.dto.ProfileImageUpdateResponse
import com.example.watchfinder.data.dto.RegisterRequest
import com.example.watchfinder.data.dto.ResetPasswordRequest
import com.example.watchfinder.data.dto.UserData
import com.example.watchfinder.data.model.Movie
import com.example.watchfinder.data.model.Series
import com.example.watchfinder.data.model.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {

    @GET("/movies/getall")
    suspend fun getMovies(): List<Movie>
    @GET("/series/getall")
    suspend fun getSeries(): List<Series>

    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<ResponseBody>
    @GET("/auth/validate")
    suspend fun validate(): Response<Map<String, String>>

    @GET("/users/getprofile")
    suspend fun getProfile(): Response<User>
    @POST("/users/addtolist")
    suspend fun addToList(@Body item: Item): Response<String>

    @POST("auth/forgot-password")
    suspend fun sendPasswordResetEmail(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @Multipart
    @PATCH("/users/profile/image")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): Response<ProfileImageUpdateResponse>

    @PUT("/users/profile")
    suspend fun updateProfile(@Body updatedUserData: UserData): Response<UserData>

    @GET("users/profile/image-url")
    suspend fun getImageUrl(): Response<ImageUrlResponse>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>

    @DELETE("/users/delete")  // Asegúrate de que la ruta sea correcta
    suspend fun deleteAccount(): Response<Unit>
}


