package com.erick.notasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erick.notasapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    navController: NavController,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current

    var currentLang by remember { mutableStateOf(LanguageManager.getSavedLanguage(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.salir),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Tema
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onToggleTheme,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .height(60.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    painter = painterResource(R.drawable.reloj),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.dark_mode))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lenguaje
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón Español
                Button(
                    onClick = {
                        LanguageManager.setLocale(context, "es")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentLang == "es")
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("Español")
                }

                // Botón Inglés
                Button(
                    onClick = {
                        LanguageManager.setLocale(context, "eng")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentLang == "eng")
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("English")
                }
            }
        }
    }
}
