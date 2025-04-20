package com.example.watchfinder.api

import com.example.watchfinder.data.dto.Item
import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.dto.LoginResponse
import com.example.watchfinder.data.model.Movie
import com.example.watchfinder.data.model.Series
import com.example.watchfinder.data.dto.RegisterRequest
import com.example.watchfinder.data.model.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    @POST("auth/reset-password")
    suspend fun sendPasswordResetEmail(@Body email: String): Response<Unit>

}