package com.erick.notasapp.ui.components

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import com.erick.notasapp.R
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerItem(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var position by remember { mutableStateOf(0) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(200)
            mediaPlayer?.let {
                position = it.currentPosition
                duration = it.duration
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(
        modifier = modifier
            .width(220.dp)
            .padding(8.dp)
            .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        Text(
            text = "Audio",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    if (!isPlaying) {
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(context, uri)
                                prepare()
                            }
                            duration = mediaPlayer?.duration ?: 0
                        }
                        mediaPlayer?.start()
                    } else {
                        mediaPlayer?.pause()
                    }
                    isPlaying = !isPlaying
                }
            ) {
                Icon(
                    painterResource(
                        if (isPlaying) R.drawable.pausa else R.drawable.play
                    ),
                    contentDescription = "PlayPause",
                    tint = Color.Black
                )
            }

            Text(
                text = "${formatMs(position)} / ${formatMs(duration)}",
                fontSize = 12.sp
            )
        }

        Slider(
            value = if (duration > 0) position.toFloat() else 0f,
            onValueChange = { value ->
                mediaPlayer?.seekTo(value.toInt())
                position = value.toInt()
            },
            valueRange = 0f..(if (duration > 0) duration.toFloat() else 1f)
        )
    }
}

// Convierte milisegundos → mm:ss
fun formatMs(ms: Int): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%02d:%02d".format(min, sec)
}
