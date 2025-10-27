package com.erick.notasapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erick.notasapp.R

@Composable
fun NotasScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("nueva_nota") },
                containerColor = Color(0xFFE91E63)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.content_agregar),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(Color(0xFFFCE4EC))
        ) {
            // Título
            Text(
                text = stringResource(R.string.title_app_notas),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD81B60),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista simulada
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(2) { index ->
                    NotaItem(
                        titulo = "Ejemplo de nota ${index + 1}",
                        fecha = if (index == 0)
                            "10 Oct 10:10am"
                        else
                            "12 Oct 1:10pm"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recordatorios
            Text(
                text = stringResource(R.string.recordatorios),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD81B60)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8BBD0))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(". 10 octubre 10:00am", color = Color(0xFF880E4F))
                    Text(". 11 octubre 2:00pm", color = Color(0xFF880E4F))
                    Text(". 13 octubre 4:00pm", color = Color(0xFF880E4F))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("lista_notas") },
                modifier = Modifier.wrapContentWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
            ) {
                Text(text = stringResource(R.string.btn_agregar_recordatorio), color = Color.White)
            }
        }
    }
}

@Composable
fun NotaItem(titulo: String, fecha: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF06292))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.lapiz),
                contentDescription = stringResource(R.string.content_imagen_nota),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFD81B60), shape = RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Próximo recordatorio: $fecha",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }

            IconButton(onClick = { /* menú */ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.content_opciones),
                    tint = Color.White
                )
            }
        }
    }
}
