package com.erick.notasapp.ui.screens.Preview

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.erick.notasapp.viewmodel.MultimediaViewModel

@Composable
fun AudioRecorderScreen(
    navController: NavController,
    multimediaVM: MultimediaViewModel
)
{
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Grabadora de Audio", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(40.dp))

        if (!multimediaVM.isRecording) {
            Button(onClick = { multimediaVM.startRecording(context) }) {
                Text("Iniciar Grabación")
            }
        } else {
            Button(onClick = { multimediaVM.stopRecording() }) {
                Text("Detener Grabación")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Regresar")
        }
    }
}
