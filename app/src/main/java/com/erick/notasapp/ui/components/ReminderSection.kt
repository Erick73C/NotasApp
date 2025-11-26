package com.erick.notasapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun ReminderSection(
    fecha: String,
    hora: String,
    onFechaChange: (String) -> Unit,
    onHoraChange: (String) -> Unit,
    textColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Recordatorio", color = textColor, style = MaterialTheme.typography.titleMedium)
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