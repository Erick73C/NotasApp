package com.erick.notasapp.ui.Navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erick.notasapp.ui.screens.NuevaNotaScreen
import com.erick.notasapp.ui.screens.Preview.AudioRecorderScreen
import com.erick.notasapp.ui.screens.Preview.PreviewImageScreen
import com.erick.notasapp.ui.screens.Preview.PreviewVideoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "nuevaNota") {

        composable("nuevaNota") {
            NuevaNotaScreen(navController)
        }

        composable("audioRecorder") {
            AudioRecorderScreen(navController)
        }

        composable(
            "previewImage?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) {
            val uri = Uri.parse(it.arguments?.getString("uri"))
            PreviewImageScreen(uri)
        }

        composable(
            route = "previewVideo?uri={uri}",
            arguments = listOf(navArgument("uri") { defaultValue = "" })
        ) {
            PreviewVideoScreen(
                navController = navController,
                uri = it.arguments?.getString("uri")
            )
        }

    }
}
