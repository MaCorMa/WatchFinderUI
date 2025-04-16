package com.example.watchfinder.data.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val name: String,
    val age: Int,
    var likedMovies: MutableList<Movie>,
    var dislikedMovies: MutableList<Movie>,
    var likedSeries: MutableList<Series>,
    var dislikedSeries: MutableList<Series>,
    var seenMovies: MutableList<Movie>,
    var seenSeries: MutableList<Series>,
    var favMovies: MutableList<Movie>,
    var favSeries: MutableList<Series>,
    var achievements: MutableList<Int>
){
    fun addLikedMovie(movie:Movie){
        this.likedMovies.add(movie)
    }
    fun addDislikedMovie(movie:Movie){
        this.dislikedMovies.add(movie)
    }
    fun addSeenMovie(movie:Movie){
        this.seenMovies.add(movie)
    }
    fun addFavMovie(movie:Movie){
        this.favMovies.add(movie)
    }

    fun addLikedSeries(series:Series){
        this.likedSeries.add(series)
    }
    fun addDislikedSeries(series:Series){
        this.dislikedSeries.add(series)
    }
    fun addSeenSeries(series:Series){
        this.seenSeries.add(series)
    }
    fun addFavSeries(series:Series){
        this.favSeries.add(series)
    }

}
