package com.erick.notasapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// 🎨 Definición de colores rosa
val PinkMain = Color(0xFFF43F5E)     // Rosa principal
val PinkLight = Color(0xFFFB7185)    // Rosa claro
val WhiteCard = Color(0xFFFFFFFF)    // Blanco para texto o fondo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaNotasScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Notas", color = WhiteCard) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinkMain
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("nueva_nota") },
                containerColor = PinkMain,
                contentColor = WhiteCard
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Nota")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Aquí aparecerán tus notas.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6B7280) // Gris suave para contraste
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* acción de ejemplo */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkLight,
                    contentColor = WhiteCard
                )
            ) {
                Text("Agregar Nota")
            }
        }
    }
}
