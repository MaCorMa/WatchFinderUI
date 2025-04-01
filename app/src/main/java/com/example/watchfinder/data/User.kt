package com.example.watchfinder.data

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val name: String,
    val age: Int,
    val likedMovies: List<Movie>,
    val dislikedMovies: List<Movie>,
    val likedSeries: List<Series>,
    val dislikedSeries: List<Series>,
    val seenMovies: List<Movie>,
    val seenSeries: List<Series>,
    val favMovies: List<Movie>,
    val favSeries: List<Series>,
    val achievements: List<Int>
)
