package com.erick.notasapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.erick.notasapp.data.model.Reminder
import com.erick.notasapp.R

@Composable
fun ReminderListSection(
    reminders: List<Reminder>,
    textColor: Color,
    onDelete: (Reminder) -> Unit,
    onEdit: (Reminder) -> Unit,
    isDarkMode: Boolean,              // << NUEVO
    onThemeChange: (Boolean) -> Unit  // << NUEVO
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        //  NUEVA FILA DE AJUSTES + SWITCH TEMA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isDarkMode) "Dark Mode" else "Light Mode",
                    color = textColor
                )
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onThemeChange(it) }
                )
            }
        }

        // ---------------------------
        //      LISTA DE RECORDATORIOS
        // ---------------------------

        Text(
            text = "Recordatorios agregados:",
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        reminders.forEach { reminder ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {
                        Text(
                            text = "Fecha/Hora:",
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "${reminder.reminderTime}",
                            color = textColor
                        )
                    }

                    Row {
                        IconButton(onClick = { onEdit(reminder) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.lapiz),
                                contentDescription = "Editar",
                                tint = Color(0xFF4A90E2)
                            )
                        }

                        IconButton(onClick = { onDelete(reminder) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.basura),
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}
