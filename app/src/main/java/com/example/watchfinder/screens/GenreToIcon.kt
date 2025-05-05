package com.example.watchfinder.screens

import androidx.compose.runtime.Composable
import com.example.watchfinder.R

@Composable
fun GenretoIcon(genreName: String): Int? {

    return when (genreName) {
    "Action" -> R.drawable.action
    "Adventure" -> R.drawable.adventure
    "Animation" -> R.drawable.animacion
    "Biography" -> R.drawable.biography
    "Comedy" -> R.drawable.comedy
    "Crime" -> R.drawable.crime
    "Drama" -> R.drawable.drama
    "Family" -> R.drawable.family
    "Fantasy" -> R.drawable.fantasy
    "Film-Noir" -> R.drawable.noir
    "History" -> R.drawable.history
    "Horror" -> R.drawable.horror
    "Music" -> R.drawable.music
    "Musical" -> R.drawable.musical
    "Mystery" -> R.drawable.mistery
    "Romance" -> R.drawable.romance
    "Sci-Fi" -> R.drawable.scifi
    "Sport" -> R.drawable.sports
    "Thriller" -> R.drawable.thriller
    "War" -> R.drawable.war
    "Western" -> R.drawable.western
        else -> null}
}