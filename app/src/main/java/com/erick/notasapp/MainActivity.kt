package com.erick.notasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erick.notasapp.ui.theme.screens.ListaNotasScreen
import com.erick.notasapp.ui.theme.screens.NuevaNotaScreen
import com.erick.notasapp.screenst.NotasScreen
import com.erick.notasapp.ui.theme.NotasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotasAppTheme {
                NotasApp()
            }
        }
    }
}

@Composable
fun NotasApp() {
    val navController = rememberNavController()

    Surface(color = MaterialTheme.colorScheme.background) {
        AppNavigation(navController)
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "notas_screen"
    ) {
        composable("notas_screen") {  NotasScreen(navController) }
        composable("lista_notas") { ListaNotasScreen(navController) }
        composable("nueva_nota") { NuevaNotaScreen(navController) }
    }
}
