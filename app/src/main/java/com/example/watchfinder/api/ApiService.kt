package com.example.watchfinder.api

import com.example.watchfinder.data.dto.Item
import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.dto.LoginResponse
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.data.model.Movie
import com.example.watchfinder.data.model.Series
import com.example.watchfinder.data.dto.RegisterRequest
import com.example.watchfinder.data.dto.SeriesCard
import com.example.watchfinder.data.model.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("/movies/getall")
    suspend fun getMovies(): List<Movie>
    @GET("/movies/getbygenre")
    suspend fun getMoviesByGenre(@Query("genres") selectedGenres: List<String>): List<Movie>
    @GET("/movies/getbytitle")
    suspend fun getMoviesByTitle(@Query("title") userInput: String): List<Movie>
    @GET("/movies/getbyid")
    suspend fun getMoviesById(@Query("id") itemId: String): Movie

    @GET("/series/getall")
    suspend fun getSeries(): List<Series>
    @GET("/series/getbygenre")
    suspend fun getSeriesByGenre(@Query("genres") selectedGenres: List<String>): List<Series>
    @GET("/series/getbytitle")
    suspend fun getSeriesByTitle(@Query("title") userInput: String): List<Series>
    @GET("/series/getbyid")
    suspend fun getSeriesById(@Query("id") itemId: String): Series

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
    @GET("/users/getfavseries")
    suspend fun getFavSeries(): List<Series>
    @GET("/users/getfavmovies")
    suspend fun getFavMovies(): List<Movie>

}