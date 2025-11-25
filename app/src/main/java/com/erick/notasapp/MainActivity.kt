package com.erick.notasapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erick.notasapp.screens.NotasScreen
import com.erick.notasapp.ui.components.Tareas
import com.erick.notasapp.ui.theme.NotasAppTheme
import com.erick.notasapp.ui.screens.ListaNotasScreen
import com.erick.notasapp.ui.screens.NuevaNotaScreen
import com.erick.notasapp.ui.screens.AjustesScreen
import com.erick.notasapp.ui.screens.LanguageManager
import com.erick.notasapp.ui.screens.Preview.AudioRecorderScreen
import com.erick.notasapp.ui.screens.Preview.PreviewImageScreen
import com.erick.notasapp.ui.screens.Preview.PreviewVideoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Carga el idioma
        LanguageManager.loadLocale(this)

        super.onCreate(savedInstanceState)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val navController = rememberNavController()

            NotasAppTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    bottomBar = {
                        Tareas(
                            darkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            onSettingsClick = { navController.navigate("ajustes") }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavigation(
                            navController = navController,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, onToggleTheme: () -> Unit) {

    NavHost(navController = navController, startDestination = "notas") {

        // Pantalla principal
        composable("notas") {
            NotasScreen(navController)
        }

        // Nueva nota
        composable("nueva_nota") {
            NuevaNotaScreen(navController)
        }

        // Editar nota
        composable("nueva_nota/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NuevaNotaScreen(navController, noteId)
        }

        // Ajustes
        composable("ajustes") {
            AjustesScreen(
                navController = navController,
                onToggleTheme = onToggleTheme
            )
        }

        composable(
            route = "previewImage?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")
            val uri = uriString?.let { Uri.parse(it) }
            if (uri != null) PreviewImageScreen(uri)
        }

        composable(
            route = "previewVideo?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->

            val uriString = backStackEntry.arguments?.getString("uri")
            val uri = uriString?.let { Uri.parse(it) }

            if (uri != null) {
                PreviewVideoScreen(
                    navController = navController,
                    uri = uri.toString()
                )
            }
        }

        composable("audioRecorder") {
            AudioRecorderScreen(navController)
        }
    }
}
