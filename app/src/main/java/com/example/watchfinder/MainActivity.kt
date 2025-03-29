package com.example.watchfinder


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.watchfinder.ui.theme.WatchFinderTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            WatchFinderTheme {
                MainScreen()
            }
        }
    }
}


@Composable
fun MainScreen() {

    var current by remember { mutableStateOf("Discover") }

    Scaffold(
        bottomBar = {
            BottomBar(
                current = current,
                click = { newSection -> current = newSection }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues))
        {
            when (current) {
                "Search" -> Search()
                "My Content" -> MyContent()
                "Discover" -> Discover()
                "Achievements" -> Achievements()
                "Profile" -> Profile()
            }
        }
    }
}

@Composable
fun BottomBar(current: String, click: (String) -> Unit) {

    val items = listOf("Search", "My Content", "Discover", "Achievements", "Profile")

    NavigationBar {

        items.forEach { sectionName ->
            NavigationBarItem(
                selected = (sectionName == current),
                onClick = { click(sectionName) },
                icon = {
                    when (sectionName) {
                        "Search" -> Icon(Icons.Filled.Search, contentDescription = sectionName)
                        "My Content" -> Icon(
                            Icons.Filled.Favorite,
                            contentDescription = sectionName
                        )

                        "Discover" -> Icon(Icons.Filled.Star, contentDescription = sectionName)
                        "Achievements" -> Icon(
                            Icons.Filled.Warning,
                            contentDescription = sectionName
                        )

                        "Profile" -> Icon(Icons.Filled.Person, contentDescription = sectionName)
                    }
                }, label = { Text(sectionName) }
            )
        }
    }

}

@Composable
fun Search() {
    Text("Estás en la pantalla HOME - ¡Aquí irán las tarjetas de pelis!")
}

@Composable
fun MyContent() {
    Text("Estás en la pantalla FAVORITOS")
}

@Composable
fun Discover() {

    //Aquí usamos remember porque al cambair entre tarjetas, esta función (Discover) se vuelve a ejecutar (se recompone),
    //remember es una forma de mantener valores entre esas ejecuciones (así index no empieza siempre de 0)
    //Y mutableStateOf, si el índice cambia, avisa a Compose de que el contenido ha cambiado (para recomponerse).
    val movies = remember {
        mutableStateOf(
            listOf(
                "Peli 1",
                "Peli 2",
                "Peli 3",
                "Peli 4",
                "Peli 5"
            )
        )
    }
    //el by es para simplificar la lectura y cambio de valor de index
    var index by remember { mutableStateOf(0) }

    //Cogemos el ancho de la pantalla (en dp)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    //Decidimos hasta dónde hay que deslizar para ejecutar la acción
    val limit = screenWidth / 4
    //Destino de la tarjeta
    val exit = (screenWidth.value * 1.5f)
    //Esto será lo que dirige la animación, X es el eje, hablamos de movimiento horizontal, empieza en 0.
    val offSetX = remember { Animatable(0f) }
    //Una corrutina para lanzar la animación
    val scope = rememberCoroutineScope()
    //Peli actual
    val currentMovie = movies.value.getOrNull(index)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        //Ahora toca envolver la tarjeta en una Box, y poder aplicarle los movimientos,
        //el Column es  un contenedor con su modificador (sus caracteristicas), aquí  ahora iría movieCard, pero necesitamos la lógica para que se mueva
        // la tareta, así que la vamos a envolver en una Box con un montón de modificaciones.
        if (currentMovie != null) {

            Box(
                //Aquí se modifica el movimiento, se redondea nuestra variable del eje X (offsetX), y también podemos definir el eje Y.
                modifier = Modifier
                    .offset { IntOffset(offSetX.value.roundToInt(), 0) }
                    //Con esto rotamos el eje Z, para que se gire un poco y no sea tan estático
                    .graphicsLayer { rotationZ = offSetX.value / 50f }
                    //esto permite escuchar el comportamiento del pointer, que en este caso son los gestos del usuario, y dentro definimos cómo actuará en consecuencia
                    .pointerInput(Unit) {
                        //Esto detecta movimientos horizontales
                        detectHorizontalDragGestures(
                            //Accion al empezar
                            onDragStart = {},
                            //Accion al terminar, aquí va la lógica si la carta se va o se queda
                            onDragEnd = {
                                val final = offSetX.value
                                //Comprobar si hemos pasado el límite
                                if (abs(final) > limit.value) {
                                    //Si es que sí, aquí ajustamos la dirección, si es positivo a un lado, y si es negativo, al otro lado. Pero en esta variable sólo
                                    //se almacena el valor.
                                    val target = if (final > 0) exit else -exit


                                    scope.launch {
                                        //Lo animamos HACIA el parámetro objetivo
                                        offSetX.animateTo(
                                            targetValue = target,
                                            //el tiempo que tarda
                                            animationSpec = tween(durationMillis = 400)
                                        )
                                        /////////////////////////////////////////////////////////////////////
                                        //Subimos el index,IMPORTANTE: el posible que aquí tengamos que meter lógica luego y no sea tan sencillo como cambiar un index
                                        /////////////////////////////////////////////////////////////////////
                                        index++
                                        //Poner el offSet a 0 para la siguiente tarjeta
                                        offSetX.snapTo(0f)
                                    }
                                } else {
                                    //Si no pasamos el límite vuelve al centro, usamos la variable de antes, la de la corrutina
                                    scope.launch {
                                        offSetX.animateTo(0f
                                        )
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            //Esto es para que no reaccione a otros gestos
                            change.consume()
                            //Esto hace que siga al dedo
                            scope.launch {
                                offSetX.snapTo(offSetX.value + dragAmount)
                            }
                        }
                    }
                    .fillMaxHeight(0.85f)
                    .fillMaxWidth()
            ) {
                //Todo eso eran los modificadores de la caja que contiene la tarjeta, eso es lo que se mueve, ahora cargamos la tarjeta y le pasamos la peli actual
                MovieCard(movieTitle = currentMovie)
            }

        } else {
            Text("C'est fini!")
        }
    }

}

@Composable
fun Achievements() {
    Text("Tus muertos")
}

@Composable
fun Profile() {
    Text("Estás en la pantalla PERFIL")
}

@Composable
fun MovieCard(movieTitle: String) {

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
            ) {
                // Aquí podríamos poner el icono de Plataformas (11) más tarde
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
                    movieTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                //Aquí he metido un Row para poder separar Runtime de Idiomas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Placeholder para Runtime",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    //Este espaciador es lo que hace que Idiomas vaya a la derecha, es un espaciador horizontal.
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Placeholder para Idiomas (3)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray

                    )
                }

                //Espaciador antes de la sinopsis
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    "\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\" ...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                //Otro más antes del resto de datos
                Spacer(modifier = Modifier.height(25.dp))

                //ROW de director y Ratings.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Placeholder para Director",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "Placeholder Ratings",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }


                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Placeholder Cast",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )


                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Placeholder Géneros",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "Placeholder Awards",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )

                }
            }

            // --- Elemento 11: Plataformas (podría ir aquí o en el Box del vídeo) ---
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Alineado arriba a la derecha
                    .padding(8.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    ) // Fondo semitransparente
                    .padding(4.dp)
            ) {
                Text(
                    "Plataf (11)",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }

        } // Fin Box principal (apilador)
    } // Fin Card

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WatchFinderTheme {
        MainScreen()
    }
}