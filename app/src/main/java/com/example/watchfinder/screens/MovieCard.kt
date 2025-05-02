package com.example.watchfinder.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.watchfinder.data.dto.MovieCard
import com.example.watchfinder.ui.theme.WatchFinderTheme

@Composable
fun MovieCard(
    movie: MovieCard,
    showActions: Boolean = true,
    onFavoriteClick: () -> Unit = {},
    onSeenClick: () -> Unit = {},
    playWhenReady: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Sombra suave
    ) {
        Box( // Box para apilar el "vídeo" y la información
            modifier = Modifier.fillMaxSize() // El Box ocupa todo el espacio de la Card
        ) {

            // --- Capa 1: Placeholder para el Vídeo (Detrás) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .clip(CardDefaults.shape)
            ) {
                val videoUrl = movie.Url // o series.Url
                if (!videoUrl.isNullOrBlank()) {
                    VideoPlayer(videoUrl = videoUrl, playWhenReady = playWhenReady) // Llama a tu composable de vídeo
                } else {
                    // Opcional: Muestra un placeholder si no hay URL
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray), // Un placeholder diferente
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Trailer no disponible")
                    }
                }
            }
            // Esta es la caja donde va los datos.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(400.dp)// Ocupa todo el espacio disponible sobre el vídeo
                    .background(

                        Brush.verticalGradient(
                            0.0f to Color.Black.copy(alpha = 0.0f),
                            0.3f to Color.Black.copy(alpha = 0.75f),
                            0.6f to Color.Black.copy(alpha = 0.99f),
                            0.95f to Color.Black
                        )
                    )
                    .padding(10.dp)
            ) {

                Text(
                    "Placeholder para bandera de país",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Text(
                    movie.Title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                //Aquí he metido un Row para poder separar Runtime de Idiomas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.Runtime ?: "Duración no disponible",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    //Este espaciador es lo que hace que Idiomas vaya a la derecha, es un espaciador horizontal.
                    Spacer(modifier = Modifier.weight(1f))
                    val languages = movie.Languages
                    val languagesToShow = languages?.takeIf { it.isNotEmpty() }?.let { langs ->
                        val maxToShow = 2
                        langs.take(maxToShow).joinToString(", ") +
                                if (langs.size > maxToShow) ", ..." else ""
                    } ?: "Idiomas no disp."
                    Text(

                        languagesToShow,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray

                    )
                }

                //Espaciador antes de la sinopsis
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    movie.Plot ?: "No disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                //Otro más antes del resto de datos
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    movie.Director ?: "No disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                val ratings = movie.Ratings
                val ratingsToShow = ratings?.takeIf { it.isNotEmpty() }?.let { rtngs ->
                    val maxToShow = 3
                    rtngs.take(maxToShow).joinToString(", ") +
                            if (rtngs.size > maxToShow) ", ..." else ""
                } ?: "Ratings no disp."
                Text(
                    ratingsToShow,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                val cast = movie.Cast
                val castToShow = cast?.takeIf { it.isNotEmpty() }?.let { cst ->
                    val maxToShow = 3
                    cst.take(maxToShow).joinToString(", ") +
                            if (cast.size > maxToShow) ", ..." else ""
                } ?: "Reparto no disp."
                Text(
                    castToShow,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )


                Spacer(modifier = Modifier.height(10.dp))

                val genres = movie.Genres
                val genresToShow = genres?.takeIf { it.isNotEmpty() }?.let { gnrs ->
                    val maxToShow = 3
                    gnrs.take(maxToShow).joinToString(", ") +
                            if (genres.size > maxToShow) ", ..." else ""
                } ?: "Géneros no disp."
                Text(
                    genresToShow,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    movie.Awards ?: "No disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )


            }

            // --- Elemento 11: Plataformas (podría ir aquí o en el Box del vídeo) ---
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Alineado arriba a la derecha
                    .padding(8.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small
                    ) // Fondo semitransparente
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (showActions) {
                        // Icono Favorito (Corazón) - Arriba Izquierda
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier
                                .padding(1.dp) // Espaciado
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite, // Cambia a FavoriteBorder si gestionas estado
                                contentDescription = "Añadir a Favoritos",
                                tint = Color.White // O un color que contraste
                            )
                        }

                        // Icono Visto (Ojo) - Arriba Derecha (junto a Plataformas?)
                        IconButton(
                            onClick = onSeenClick,
                            modifier = Modifier
                                .padding(1.dp)) {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder, // Cambia si gestionas estado
                                contentDescription = "Marcar como Visto",
                                tint = Color.White // O un color que contraste
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Plataf (11)",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

        } // Fin Box principal (apilador)
    } // Fin Card
}

/*@Preview(showBackground = true)
@Composable
fun MovieCardPreview() {
    // 1. Crea datos falsos (dummy) del tipo MovieCard (DTO)
    val dummyMovie = MovieCard(
        _id = "preview123",
        Title = "Película de Prueba Muy Larga Para Ver Cómo Queda",
        Plot = "Esta es una descripción de ejemplo bastante larga para ver cómo se ajusta el texto en varias líneas dentro de la tarjeta de la película.",
        Url = "https://via.placeholder.com/600x900.png?text=Movie+Poster", // Usa una URL de placeholder
        Genres = listOf("Acción", "Aventura", "Ciencia Ficción"),
        Runtime = "125 min",
        Director = "Director Famoso",
        Cast = listOf("Actor Principal", "Actriz Secundaria", "Otro Actor"),
        Ratings = listOf("IMDb: 7.5/10"),
        Languages = listOf("Español", "Inglés"),
        Country = "País Ejemplo",
        Awards = "Algún premio",
        Year = 2024,
        ReleaseDate = "2024-01-15",
        Rated = "PG-13",
        Poster = "A"
        // Asegúrate de llenar todos los campos NO NULOS de tu DTO
    )

    // 2. Llama a tu MovieCard real pasándole los datos falsos
    WatchFinderTheme { // Envuelve en tu tema si es necesario
        MovieCard(dummyMovie)
    }
}*/