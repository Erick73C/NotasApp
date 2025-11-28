package com.erick.notasapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.erick.notasapp.utils.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {

    // SE EJECUTA EXACTAMENTE A LA HORA PROGRAMADA POR ALARMMANAGER
    override fun onReceive(context: Context, intent: Intent) {

        // RECUPERAR DATOS PASADOS AL PROGRAMAR
        val noteId = intent.getIntExtra("noteId", -1)
        val noteTitle = intent.getStringExtra("noteTitle") ?: "NOTA SIN TÍTULO"

        if (noteId != -1) {
            // LLAMA A LA FUNCIÓN DEL HELPER PARA CONSTRUIR Y MOSTRAR LA NOTIFICACIÓN VISUAL
            NotificationHelper.showNotificationNow(context, noteTitle, noteId)
        }
    }
}