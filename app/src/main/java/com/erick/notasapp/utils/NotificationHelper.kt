package com.erick.notasapp.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.erick.notasapp.MainActivity
import com.erick.notasapp.R

object NotificationHelper {

    private const val CHANNEL_ID = "REMINDER_CHANNEL"
    private const val CHANNEL_NAME = "Recordatorios"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // da prioridad alta para que la notificación aparezca visiblemente
            )

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    //aqui programa una alarma que se activará en un momento futuro se dispara al ejecutar el AlarmReceiver
    fun scheduleNotification(
        context: Context,
        noteTitle: String,
        noteId: Int,
        reminderTime: Long
    ) {
        val intent = Intent(context, com.erick.notasapp.receivers.AlarmReceiver::class.java).apply {
            putExtra("noteId", noteId)
            putExtra("noteTitle", noteTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId, // Se usa el noteId para que cada alarma sea única
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //aqui obtiene todas las alarmas del sistema
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        try {
            //aqui mandala notificacion a la hora exacta
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, //aqi usa el reloj
                reminderTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    //Cancela una alarma que fue programada
    fun cancelNotification(context: Context, noteId: Int) {
        val intent = Intent(context, com.erick.notasapp.receivers.AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.cancel(pendingIntent)
        }
    }

    //Construye y muestra la notificación
    fun showNotificationNow(
        context: Context,
        noteTitle: String,
        noteId: Int
    ) {

        // Creamos un intent para que al tocar la notificación, se abra la MainActivit
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Recordatorio")
            .setContentText(noteTitle)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ícono seguro
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(noteId, notification)
    }
}
