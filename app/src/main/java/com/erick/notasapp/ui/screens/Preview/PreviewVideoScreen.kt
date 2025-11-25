package com.erick.notasapp.ui.screens.Preview


import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.erick.notasapp.ui.components.VideoPlayer

@Composable
fun PreviewVideoScreen(navController: NavController, uri: String?) {
    val realUri = uri?.let { Uri.parse(it) }

    if (realUri == null) {
        Text("Video no disponible", color = Color.Red)
        return
    }

    VideoPlayer(uri = realUri)
}
