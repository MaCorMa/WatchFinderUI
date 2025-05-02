package com.example.watchfinder.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView // Importa PlayerView de media3

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUrl: String, playWhenReady: Boolean) {
    val context = LocalContext.current

    // 1. Inicializar ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare() // Prepara el reproductor
            this.playWhenReady = playWhenReady
        }
    }

    // 2. Liberar el reproductor cuando el Composable se va
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }


        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false

                }
            },
            update = { view ->
                // Actualizar si es necesario, por ejemplo, si cambia la URL
                val mediaItem = MediaItem.fromUri(videoUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                // exoPlayer.playWhenReady = true // Opcional
            }
        )

}