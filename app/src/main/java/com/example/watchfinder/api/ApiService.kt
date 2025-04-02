package com.example.watchfinder.api

import com.example.watchfinder.data.Movie
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("getall")
    fun getMovies(): Call<List<Movie>>

}