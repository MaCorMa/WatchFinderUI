package com.example.watchfinder.api

import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.dto.LoginResponse
import com.example.watchfinder.data.Movie
import com.example.watchfinder.data.Series
import com.example.watchfinder.data.dto.RegisterRequest
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
}