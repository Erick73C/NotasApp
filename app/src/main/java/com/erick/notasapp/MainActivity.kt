package com.erick.notasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erick.notasapp.screens.NotasScreen
import com.erick.notasapp.ui.components.Tareas
import com.erick.notasapp.ui.theme.NotasAppTheme
import com.erick.notasapp.ui.screens.ListaNotasScreen
import com.erick.notasapp.ui.screens.NuevaNotaScreen
import com.erick.notasapp.ui.screens.AjustesScreen
import com.erick.notasapp.ui.screens.LanguageManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageManager.loadLocale(this)
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
                        AppNavigation(navController, onToggleTheme = { isDarkTheme = !isDarkTheme })
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, onToggleTheme: () -> Unit) {
    NavHost(navController = navController, startDestination = "notas") {
        composable("notas") { NotasScreen(navController) }
        composable("nueva_nota") { NuevaNotaScreen(navController) }
        composable("nueva_nota/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NuevaNotaScreen(navController, noteId)
        }
        composable("ajustes") {
            AjustesScreen(
                navController = navController,
                onToggleTheme = onToggleTheme
            )
        }
    }
}
