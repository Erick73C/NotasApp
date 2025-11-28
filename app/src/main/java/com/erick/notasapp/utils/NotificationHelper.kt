package com.erick.notasapp.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import com.erick.notasapp.MainActivity
import com.erick.notasapp.R
import com.erick.notasapp.receivers.AlarmReceiver

object NotificationHelper {

    private const val CHANNEL_ID = "notasapp_reminders"

    // CREA EL CANAL DE NOTIFICACIÓN
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "RECORDATORIOS NOTASAPP"
            val descriptionText = "NOTIFICACIONES DE RECORDATORIOS DE TUS NOTAS"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // PPROGRAMAMPS LA ALARMA CON ALARMMANAGER
    fun scheduleNotification(
        context: Context,
        noteTitle: String,
        noteId: Int,
        reminderTime: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // INTENT QUE IRÁ AL ALARMRECEIVER CUANDO LLEGUE LA HORA
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("noteTitle", noteTitle)
            putExtra("noteId", noteId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId, // ID ÚNICO PARA QUE LA ALARMA NO SE SOBREESCRIBA
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // VERIFICAR PERMISOS DE ALARMA EXACTA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        // PROGRAMA LA ALARMA EXACTA USANDO SETEXACTANDALLOWWHILEIDLE PARA PRECISIÓN MÁXIMA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        }
    }

    // CANCELA UNA ALARMA PREVIAMENTE PROGRAMADA
    fun cancelNotification(context: Context, noteId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    // MOSTRAR LA NOTIFICACIÓN VISUAL
    fun showNotificationNow(
        context: Context,
        noteTitle: String,
        noteId: Int
    ) {
        // PERMISO DE NOTIFICACIONES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_note_id", noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PENDINGINTENT QUE SE ACTIVA AL TOCAR LA NOTIFICACIÓN
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.reloj)
            .setContentTitle("RECORDATORIO: $noteTitle")
            .setContentText("TOCA PARA VER TU NOTA")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // LLAMA AL PENDINGINTENT AL TOCAR
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        NotificationManagerCompat.from(context).notify(noteId, builder.build())
    }
}