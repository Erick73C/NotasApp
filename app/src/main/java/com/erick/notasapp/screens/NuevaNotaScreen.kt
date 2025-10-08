package com.erick.notasapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erick.notasapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaNotaScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                //BOTON PARA LA NAVEGACION HACIA ATRAS
                title = { Text("Nueva Nota", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.salir),
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
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
            // Campo de título
            Text("Título", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escribe un título") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de descripción
            Text("Descripción", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Escribe una descripción") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección multimedia
            Text("Multimedia", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MultimediaButton(iconRes = R.drawable.imagen, texto = "Agregar imagen")
                MultimediaButton(iconRes = R.drawable.microfono, texto = "Agregar audio")
                MultimediaButton(iconRes = R.drawable.video, texto = "Agregar video")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección de recordatorios
            Text("Recordatorios", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
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
                            contentDescription = "Hora"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("10 Oct 10:00 AM", fontSize = 14.sp)
                    }
                    IconButton(onClick = { /* eliminar */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.basura),
                            contentDescription = "Eliminar"
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
                Text("+ agregar recordatorio")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botones inferiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { /* cancelar */ },
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Cancelar", fontSize = 16.sp)
                }

                Button(
                    onClick = { /* guardar */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Guardar", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MultimediaButton(iconRes: Int, texto: String) {
    OutlinedButton(
        onClick = { /* agregar multimedia */ },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = texto,
                modifier = Modifier.size(24.dp)
            )
            Text(texto, fontSize = 12.sp, color = Color.Black)
        }
    }
}