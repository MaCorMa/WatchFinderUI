package com.example.watchfinder.repository

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.UserManager
import com.example.watchfinder.data.prefs.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val userManager: UserManager
) {

    fun getAllGenres(): List<String> {
        val generosDePeliculas: List<String> = listOf(
            "Action",
            "Adventure",
            "Animation",
            "Biography",
            "Comedy",
            "Crime",
            "Drama",
            "Family",
            "Fantasy",
            "Film-Noir",
            "History",
            "Horror",
            "Music",
            "Musical",
            "Mystery",
            "Romance",
            "Sci-Fi",
            "Sport",
            "Thriller",
            "War",
            "Western"
        )
        return generosDePeliculas
    }
}