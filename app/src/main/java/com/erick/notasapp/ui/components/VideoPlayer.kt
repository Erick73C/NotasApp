package com.erick.notasapp.ui.components

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current


    val exoPlayer = remember(context) {
        try {
            ExoPlayer.Builder(context).build()
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    if (exoPlayer == null) {
        Text("Error al cargar el reproductor", color = Color.Red)
        return
    }

    LaunchedEffect(uri) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
}

