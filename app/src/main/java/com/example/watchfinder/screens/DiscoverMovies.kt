package com.example.watchfinder.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watchfinder.R
import com.example.watchfinder.viewmodels.DiscoverMoviesVM
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DiscoverMovies(discoverViewModel: DiscoverMoviesVM = hiltViewModel()) {

    val uiState by discoverViewModel.uiState.collectAsState()
    val currentCards = uiState.cards
    //Aquí usamos remember porque al cambair entre tarjetas, esta función (Discover) se vuelve a ejecutar (se recompone),
    //remember es una forma de mantener valores entre esas ejecuciones (así index no empieza siempre de 0)
    //Y mutableStateOf, si el índice cambia, avisa a Compose de que el contenido ha cambiado (para recomponerse).
    //el by es para simplificar la lectura y cambio de valor de index

    //Cogemos el ancho de la pantalla (en dp)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    //Decidimos hasta dónde hay que deslizar para ejecutar la acción, tengo la impresión de que cuanto más alto sea "exit" más tiene que serlo esta.
    val limit = screenWidth
    //Destino de la tarjeta, si lo ponemos muy bajo la tarjeta se queda congelada en mitad de la pantalla y desaparece, muy ortopédico.
    val exit = (screenWidth.value * 6f)
    //Esto será lo que dirige la animación, X es el eje, hablamos de movimiento horizontal, empieza en 0.
    val offSetX = remember { Animatable(0f) }
    //Una corrutina para lanzar la animación
    val scope = rememberCoroutineScope()

    //Tarjeta contenido actual
    val currentMovie = currentCards.getOrNull(0)
    //Otra para la siguiente, porque cuando el user deslice se tiene que ver la de abajo
    val nextMovie = currentCards.getOrNull(1)

    val favoriteIds = uiState.favoriteMovieIds
    val seenIds = uiState.seenMovieIds

    var showAnimationIcon by remember { mutableStateOf(false) }
    var animationIconType by remember { mutableStateOf<Int?>(null) } // Guardará Icons.Filled.Check o Icons.Filled.Close
    val iconOffsetY = remember { Animatable(0f) } // Controla el desplazamiento vertical
    val iconAlpha = remember { Animatable(0f) }

    // --- NUEVO: Función para iniciar la animación del icono ---
    fun triggerIconAnimation(isLike: Boolean) {
        scope.launch {
            // 1. Preparar icono y resetear valores
            showAnimationIcon = true
            // Define qué icono usar según el swipe
            animationIconType = if (isLike) {
                R.drawable.heartfilllike // <-- USA EL ID DE TU PNG PARA "LIKE"
            } else {
                R.drawable.cross // <-- USA EL ID DE TU PNG PARA "DISLIKE"
            }
            iconOffsetY.snapTo(0f) // Empieza en el centro verticalmente
            iconAlpha.snapTo(1f)   // Empieza totalmente visible

            // 2. Animar Desplazamiento y Opacidad en paralelo
            launch {
                iconOffsetY.animateTo(
                    targetValue = -150f, // Mover hacia arriba (ajusta este valor)
                    animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing) // Duración y efecto
                )
            }
            launch {
                iconAlpha.animateTo(
                    targetValue = 0f, // Desvanecer hasta ser invisible
                    animationSpec = tween(durationMillis = 800, easing = EaseIn) // Duración y efecto
                )
            }

            // 3. (Opcional) Podrías resetear showAnimationIcon = false después de delay(500),
            //    pero como alpha es 0, ya no se verá.
        }
    }
    // -------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        when {
            uiState.isLoading && currentCards.isEmpty() -> {
                Progress()
            }

            uiState.error != null-> {
                Text("Error ${uiState.error}")
            }

            currentCards.isEmpty() && !uiState.isLoading -> {
                Text("¡Eso es todo por ahora!") // Reemplaza "C'est fini!"
            }


            //Ahora toca envolver la tarjeta en una Box, y poder aplicarle los movimientos,
            //el Column es  un contenedor con su modificador (sus caracteristicas), aquí  ahora iría movieCard, pero necesitamos la lógica para que se mueva
            // la tareta, así que la vamos a envolver en una Box con un montón de modificaciones.
            currentMovie != null -> {

                //Esta caja sólo comprueba que nextMovie no sea null y la muestra, así siempre hay algo debajo de la tarjeta.
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (nextMovie != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            val isNextFavorite = nextMovie._id in favoriteIds
                            val isNextSeen = nextMovie._id in seenIds
                            MovieCard(
                                movie = nextMovie,
                                isFavorite = isNextFavorite, // <-- Usa el estado real
                                isSeen = isNextSeen,         // <-- Usa el estado real
                                onFavoriteClick = {},
                                onSeenClick = {},
                                playWhenReady = false
                            )
                        }
                    }

                    if (showAnimationIcon && animationIconType != null) {
                        Icon(
                            painter = painterResource(id= animationIconType!!),
                            contentDescription = if (animationIconType == R.drawable.heartfilllike) "Like Animation" else "Dislike Animation",
                            modifier = Modifier
                                .align(Alignment.Center) // Centrado en el Box principal
                                // Aplica el desplazamiento vertical animado
                                .offset(y = iconOffsetY.value.dp)
                                // Aplica la opacidad animada
                                .alpha(iconAlpha.value),
                            // Color del icono (puedes cambiarlo)
                            tint = Color.Red
                        )
                    }

                    Box(
                        //Aquí se modifica el movimiento, se redondea nuestra variable del eje X (offsetX), y también podemos definir el eje Y.
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(offSetX.value.roundToInt(), 0) }
                            //Con esto modificamos los ejes de la animación, por ejemplo rotación sobre eje Z, o escalado.
                            .graphicsLayer {
                                rotationZ = offSetX.value / 30f

                                //Esto ajusta el eje sobre el que la tarjeta gira
                                transformOrigin = TransformOrigin(0.5f, 0.5f)
                                //Esto hace que se vuelva más pequeña según sale de la pantalla, tenemos que ver si queremos dejarlo.
                                //Ponemos esta fórmula para que se adapte a la anchura del terminal en el que esté, screenWidth siempre será un valor cambiante dependiendo del dispositivo.
                                //Y OffSt value también cambia porque nos está indicando la posición de la tarjeta.
                                scaleX = 1f - (abs(offSetX.value) / (screenWidth.value * 6))
                                scaleY = 1f - (abs(offSetX.value) / (screenWidth.value * 6))

                                // Podríamos hasta hacer que rote sobre sí misma, pero creo que quedaba mal. Puedes descomentar, correr la app y probar, a ver qué opinas.
                                // rotationY = (offSetX.value / screenWidth.value) * 10f
                            }
                            //esto permite escuchar el comportamiento del pointer, que en este caso son los gestos del usuario, y dentro definimos cómo actuará en consecuencia
                            .pointerInput(currentMovie._id) {
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
                                            val isLike = final > 0 // Determina si es like (derecha) o dislike (izquierda)

                                            // --- AÑADE ESTA LÍNEA ---
                                            triggerIconAnimation(isLike = isLike)

                                            scope.launch {
                                                //Lo animamos HACIA el parámetro objetivo
                                                offSetX.animateTo(
                                                    targetValue = target,
                                                    //el tiempo que tarda
                                                    animationSpec = tween(durationMillis = 400)
                                                )
                                                if (final > 0) { // Derecha = Like
                                                    discoverViewModel.cardLiked(currentMovie)
                                                } else { // Izquierda = Dislike
                                                    discoverViewModel.cardDisliked(currentMovie)
                                                }
                                                offSetX.snapTo(0f)
                                            }
                                        } else {
                                            //Si no pasamos el límite vuelve al centro, usamos la variable de antes, la de la corrutina
                                            scope.launch {
                                                offSetX.animateTo(
                                                    0f
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
                        val isCurrentFavorite = currentMovie._id in favoriteIds
                        val isCurrentSeen = currentMovie._id in seenIds

                        MovieCard(
                            movie = currentMovie,
                            isFavorite = isCurrentFavorite, // <-- Usa el estado real
                            isSeen = isCurrentSeen,         // <-- Usa el estado real
                            onFavoriteClick = { discoverViewModel.cardFav(currentMovie) },
                            onSeenClick = { discoverViewModel.cardSeen(currentMovie) },
                            playWhenReady = true
                        )
                    }
                }

            }
        }
    }

}