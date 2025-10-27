package com.erick.notasapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erick.notasapp.R

val PinkMain = Color(0xFFF43F5E)     // Rosa principal
val PinkLight = Color(0xFFFB7185)    // Rosa claro
val WhiteCard = Color(0xFFFFFFFF)    // Blanco

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaNotasScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_mis_notas), color = WhiteCard) },
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
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fab_nueva_nota))
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
                text = stringResource(R.string.msg_sin_notas),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkLight,
                    contentColor = WhiteCard
                )
            ) {
                Text(stringResource(R.string.btn_agregar_nota))
            }
        }
    }
}
