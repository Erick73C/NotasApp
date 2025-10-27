package com.erick.notasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erick.notasapp.screens.NotasScreen
import com.erick.notasapp.ui.theme.NotasAppTheme
import com.erick.notasapp.ui.theme.screens.ListaNotasScreen
import com.erick.notasapp.ui.theme.screens.NuevaNotaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado para controlar el tema oscuro/claro
            var isDarkTheme by remember { mutableStateOf(false) }

            NotasAppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                Column(modifier = Modifier.fillMaxSize()) {
                    // Botón para alternar tema
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { isDarkTheme = !isDarkTheme }) {
                            Text(if (isDarkTheme) "Modo Claro" else "Modo Oscuro")
                        }
                    }

                    // Navegación de la app
                    AppNavigation(navController)
                }
            }
        }
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
