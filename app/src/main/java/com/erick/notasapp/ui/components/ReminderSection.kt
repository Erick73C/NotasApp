package com.erick.notasapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun ReminderSection(
    fecha: String,
    hora: String,
    onFechaChange: (String) -> Unit,
    onHoraChange: (String) -> Unit,
    textColor: Color,
    isDarkMode: Boolean,              // << NUEVO
    onThemeChange: (Boolean) -> Unit  // << NUEVO
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        //NUEVA FILA DE AJUSTES + SWITCH TEMA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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

        // -----------------------------
        // SECCIÓN ORIGINAL DEL RECORDATORIO
        // -----------------------------

        Text(
            "Recordatorio",
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = onFechaChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("YYYY-MM-DD") },
            label = { Text("Fecha") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = hora,
            onValueChange = onHoraChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("HH:MM") },
            label = { Text("Hora") }
        )
    }
}
