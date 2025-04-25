package com.example.watchfinder.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watchfinder.viewmodels.DetailsVM
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DetailScreen(
    itemType: String,
    itemId: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailsVM = hiltViewModel()
) {
    val offSetX = remember { Animatable(0f) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val limit = screenWidth
    val exit = (screenWidth.value * 6f)
    val scope = rememberCoroutineScope()
    // Llama al ViewModel para que cargue los detalles basado en type/id
    LaunchedEffect(itemType, itemId) {
        viewModel.loadDetails(itemType, itemId)
    }

    val uiState by viewModel.uiState.collectAsState() // Asume que DetailViewModel tiene un uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> {
                Progress()
            }

            uiState.error != null -> {
                Text("Error: ${uiState.error}")
                // Podrías añadir un botón para reintentar o volver
            }

            uiState.movieDetail != null -> {
                Box(
                    //Aquí se modifica el movimiento, se redondea nuestra variable del eje X (offsetX), y también podemos definir el eje Y.
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
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
                        .pointerInput(itemId) {
                            //Esto detecta movimientos horizontales
                            detectHorizontalDragGestures(
                                //Accion al empezar
                                onDragStart = {},
                                //Accion al terminar, aquí va la lógica si la carta se va o se queda
// Dentro de .pointerInput(itemId) { detectHorizontalDragGestures( ... ) }

                                onDragEnd = {
                                    val final = offSetX.value
                                    // Comprobar si hemos pasado el límite (en cualquier dirección)
                                    if (abs(final) > limit.value) {
                                        // Sí, hemos deslizado lo suficiente. Animamos hacia fuera y luego navegamos atrás.
                                        val target =
                                            if (final > 0) exit else -exit // Hacia dónde animar (fuera de pantalla)

                                        scope.launch {
                                            offSetX.animateTo(
                                                targetValue = target,
                                                animationSpec = tween(durationMillis = 300) // Duración animación salida
                                            ) { // Este bloque se ejecuta al COMPLETAR la animación
                                                onNavigateBack() // Llama a la función para volver atrás
                                            }
                                            // No es necesario hacer snapTo(0f) aquí porque ya hemos navegado fuera
                                        }
                                    } else {
                                        // No, no hemos deslizado lo suficiente. Volvemos al centro.
                                        scope.launch {
                                            offSetX.animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(durationMillis = 300) // Duración animación retorno
                                            )
                                        }
                                    }
                                }
// El resto de detectHorizontalDragGestures (onDragStart y el bloque lambda final) se quedan como estaban.
                            ) { change, dragAmount ->
                                println("Dragging, OffsetX: ${offSetX.value}")
                                //Esto es para que no reaccione a otros gestos
                                change.consume()
                                //Esto hace que siga al dedo
                                scope.launch {
                                    offSetX.snapTo(offSetX.value + dragAmount)
                                }
                            }
                        }
                ) {
                    //Todo eso eran los modificadores de la  caja que contiene la tarjeta, eso es lo que se mueve, ahora cargamos la tarjeta y le pasamos la peli actual
                    MovieCard(movie = uiState.movieDetail!!,
                        onFavoriteClick = {
                            viewModel.addToFavorites(
                                uiState.movieDetail!!._id ?: "", "movie"
                            )
                        },
                        onSeenClick = {
                            viewModel.addToSeen(
                                uiState.movieDetail!!._id ?: "",
                                "movie"
                            )
                        })
                }
            }

            uiState.seriesDetail != null -> {
                Box(
                    //Aquí se modifica el movimiento, se redondea nuestra variable del eje X (offsetX), y también podemos definir el eje Y.
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
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
                        .pointerInput(itemId) {
                            //Esto detecta movimientos horizontales
                            detectHorizontalDragGestures(
                                //Accion al empezar
                                onDragStart = {},
                                //Accion al terminar, aquí va la lógica si la carta se va o se queda
// Dentro de .pointerInput(itemId) { detectHorizontalDragGestures( ... ) }

                                onDragEnd = {
                                    val final = offSetX.value
                                    // Comprobar si hemos pasado el límite (en cualquier dirección)
                                    if (abs(final) > limit.value) {
                                        // Sí, hemos deslizado lo suficiente. Animamos hacia fuera y luego navegamos atrás.
                                        val target =
                                            if (final > 0) exit else -exit // Hacia dónde animar (fuera de pantalla)

                                        scope.launch {
                                            offSetX.animateTo(
                                                targetValue = target,
                                                animationSpec = tween(durationMillis = 300) // Duración animación salida
                                            ) { // Este bloque se ejecuta al COMPLETAR la animación
                                                onNavigateBack() // Llama a la función para volver atrás
                                            }
                                            // No es necesario hacer snapTo(0f) aquí porque ya hemos navegado fuera
                                        }
                                    } else {
                                        // No, no hemos deslizado lo suficiente. Volvemos al centro.
                                        scope.launch {
                                            offSetX.animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(durationMillis = 300) // Duración animación retorno
                                            )
                                        }
                                    }
                                }
// El resto de detectHorizontalDragGestures (onDragStart y el bloque lambda final) se quedan como estaban.
                            ) { change, dragAmount ->
                                println("Dragging, OffsetX: ${offSetX.value}")
                                //Esto es para que no reaccione a otros gestos
                                change.consume()
                                //Esto hace que siga al dedo
                                scope.launch {
                                    offSetX.snapTo(offSetX.value + dragAmount)
                                }
                            }
                        }
                ) {
                    //Todo eso eran los modificadores de la  caja que contiene la tarjeta, eso es lo que se mueve, ahora cargamos la tarjeta y le pasamos la peli actual
                    SeriesCard(series = uiState.seriesDetail!!,
                        onFavoriteClick = {
                            viewModel.addToFavorites(
                                uiState.seriesDetail!!._id ?: "", "series"
                            )
                        },
                        onSeenClick = {
                            viewModel.addToSeen(
                                uiState.seriesDetail!!._id ?: "",
                                "series"
                            )
                        }
                    )
                }
            }

            else -> {
                Text("No se encontraron detalles.")
            }
        }
        // Podrías añadir un botón flotante o en una TopAppBar para volver atrás
        // FloatingActionButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "") }
    }
}