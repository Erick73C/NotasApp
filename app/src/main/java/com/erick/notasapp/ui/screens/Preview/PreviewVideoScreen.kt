package com.erick.notasapp.ui.screens.Preview


import android.graphics.drawable.Icon
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.erick.notasapp.ui.components.VideoPlayer
import com.erick.notasapp.R

@Composable
fun PreviewVideoScreen(navController: NavController, uri: Uri) {

    if (uri.toString().isBlank()) {
        Text("URI inválido")
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VideoPlayer(uri)

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painterResource(R.drawable.salir),
                contentDescription = "Volver",
                tint = Color.White
            )
        }
    }
}
