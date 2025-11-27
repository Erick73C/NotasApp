package com.erick.notasapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erick.notasapp.R
import com.erick.notasapp.data.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteCard(
    note: Note,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    // Detectar modo oscuro
    val isDark = isSystemInDarkTheme()

    // Colores adaptados al modo oscuro
    val bgColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF7F7F7)
    val titleColor = if (isDark) Color(0xFFFF80AB) else Color.Black
    val textColor = if (isDark) Color(0xFFD0D0D0) else Color.DarkGray
    val iconColor = if (isDark) Color.White else Color.Gray
    val dateColor = if (isDark) Color(0xFFBBBBBB) else Color.Gray

    // Formato de fecha
    val formattedDate = remember(note.createdAt) {
        val date = Date(note.createdAt)
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        formatter.format(date)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEdit(note) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Título + botón opciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_opciones),
                            tint = iconColor
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                showMenu = false
                                onEdit(note)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = {
                                showMenu = false
                                onDelete(note)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = note.description,
                fontSize = 15.sp,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = if (isDark) Color(0xFF333333) else Color.LightGray, thickness = 1.dp)

            // Fecha
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = dateColor,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 6.dp)
            )
        }
    }
}
