package com.erick.notasapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.NavController
import com.erick.notasapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaNotaScreen(navController: NavController) {
    val isDarkTheme = isSystemInDarkTheme()

    val primaryPink = if (isDarkTheme) Color(0xFFFF80AB) else Color(0xFFD81B60)
    val cardPink = if (isDarkTheme) Color(0xFF4A148C) else Color(0xFFF8BBD0)
    val buttonTextColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_nueva_nota), color = buttonTextColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.salir),
                            contentDescription = stringResource(R.string.content_atras)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryPink)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(stringResource(R.string.label_titulo), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = textColor)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.hint_titulo)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(stringResource(R.string.label_descripcion), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = textColor)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text(stringResource(R.string.hint_descripcion)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.label_multimedia), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MultimediaButton(iconRes = R.drawable.imagen, texto = stringResource(R.string.btn_agregar_imagen), textColor = textColor)
                MultimediaButton(iconRes = R.drawable.microfono, texto = stringResource(R.string.btn_agregar_audio), textColor = textColor)
                MultimediaButton(iconRes = R.drawable.video, texto = stringResource(R.string.btn_agregar_video), textColor = textColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(stringResource(R.string.label_recordatorios), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = cardPink)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.reloj),
                            contentDescription = stringResource(R.string.content_hora),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("10 Oct 10:00 AM", fontSize = 14.sp, color = textColor)
                    }
                    IconButton(onClick = { /* eliminar */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.basura),
                            contentDescription = stringResource(R.string.content_eliminar)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { /* agregar recordatorio */ },
                modifier = Modifier.wrapContentWidth(),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(stringResource(R.string.btn_agregar_recordatorio_simple), color = textColor)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { /* cancelar */ },
                    shape = RoundedCornerShape(50)
                ) {
                    Text(stringResource(R.string.btn_cancelar), fontSize = 16.sp, color = textColor)
                }

                Button(
                    onClick = { /* guardar */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPink)
                ) {
                    Text(stringResource(R.string.btn_guardar), fontSize = 16.sp, color = buttonTextColor)
                }
            }
        }
    }
}

@Composable
fun MultimediaButton(iconRes: Int, texto: String, textColor: Color) {
    OutlinedButton(
        onClick = { /* agregar multimedia */ },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = texto,
                modifier = Modifier.size(24.dp)
            )
            Text(texto, fontSize = 12.sp, color = textColor)
        }
    }
}
