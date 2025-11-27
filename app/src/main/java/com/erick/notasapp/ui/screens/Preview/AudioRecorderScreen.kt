package com.erick.notasapp.ui.screens.Preview

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.jar.Manifest

@Composable
fun AudioRecorderScreen(
    navController: NavController,
    multimediaVM: MultimediaViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Grabadora de Audio", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                multimediaVM.checkAndRequestPermissions(
                    context,
                    onGranted = { multimediaVM.startRecording(context) },
                    onDenied = {
                        Toast.makeText(
                            context,
                            "Permiso de micrófono requerido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            enabled = !multimediaVM.isRecording
        ) {
            Text("Iniciar Grabación")
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val uri = multimediaVM.stopRecording()
                if (uri != null) {
                    multimediaVM.addAudio(uri)
                }
                navController.popBackStack()
            },
            enabled = multimediaVM.isRecording
        ) {
            Text("Detener Grabación")
        }

        Spacer(Modifier.height(40.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Regresar")
        }
    }
}

