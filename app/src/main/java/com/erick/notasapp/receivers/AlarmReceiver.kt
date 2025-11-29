package com.erick.notasapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.erick.notasapp.utils.NotificationHelper


class AlarmReceiver : BroadcastReceiver() {

    //llama el metodo cuando llega la hora programada
    override fun onReceive(context: Context, intent: Intent) {

        // aqui extrae los datos
        val noteId = intent.getIntExtra("noteId", -1)
        val title = intent.getStringExtra("noteTitle") ?: "Recordatorio"

        // ID válido
        if (noteId != -1) {
            //llama otro metodo de notificationhelper y muestre la notificacion
            NotificationHelper.showNotificationNow(
                context = context,
                noteTitle = title,
                noteId = noteId
            )
        }
    }
}
