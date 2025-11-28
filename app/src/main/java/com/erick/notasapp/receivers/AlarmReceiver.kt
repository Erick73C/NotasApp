package com.erick.notasapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.erick.notasapp.utils.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val noteId = intent.getIntExtra("noteId", -1)
        val title = intent.getStringExtra("noteTitle") ?: "Recordatorio"

        if (noteId != -1) {
            NotificationHelper.showNotificationNow(
                context = context,
                noteTitle = title,
                noteId = noteId
            )
        }
    }
}
