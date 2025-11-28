package com.erick.notasapp.utils

import android.Manifest
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
import com.erick.notasapp.MainActivity
import com.erick.notasapp.R
import com.erick.notasapp.receivers.AlarmReceiver

object NotificationHelper {

    private const val CHANNEL_ID = "notasapp_reminders"
    private const val CHANNEL_NAME = "Recordatorios NotasApp"
    private const val CHANNEL_DESCRIPTION = "Notificaciones para los recordatorios de tus notas"

    /**
     * Crea el canal de notificaciones, necesario para Android 8 (Oreo) y superior.
     * Debe llamarse una sola vez, idealmente al iniciar la aplicación.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            // Registra el canal en el sistema de notificaciones
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Programa una alarma que disparará una notificación en el futuro.
     */
    fun scheduleNotification(
        context: Context,
        noteTitle: String,
        noteId: Int,
        reminderTime: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("noteTitle", noteTitle)
            putExtra("noteId", noteId)
        }

        // El PendingIntent permite que el AlarmManager ejecute nuestro Intent más tarde.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId, // El ID único asegura que no sobrescribimos otras alarmas.
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // En Android 12+ se requiere permiso explícito para programar alarmas exactas.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Idealmente, aquí deberías guiar al usuario a los ajustes para dar el permiso.
                // Por ahora, si no hay permiso, la alarma no se programa.
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }

    /**
     * Cancela una alarma previamente programada usando su ID.
     */
    fun cancelNotification(context: Context, noteId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        // Para cancelar, se debe crear un PendingIntent que coincida con el que se usó para programar.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Muestra la notificación de forma inmediata. Es llamado por el AlarmReceiver.
     */
    fun showNotificationNow(
        context: Context,
        noteTitle: String,
        noteId: Int
    ) {
        // En Android 13+ se necesita el permiso POST_NOTIFICATIONS.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si el permiso no está concedido, no se puede mostrar la notificación.
            // La solicitud del permiso debe manejarse en la UI.
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_note_id", noteId) // Extra para que la app sepa qué nota abrir.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.reloj) // Asegúrate de tener este ícono en res/drawable
            .setContentTitle("Recordatorio: $noteTitle")
            .setContentText("Toca para ver tu nota.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // La notificación se cierra al tocarla
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        NotificationManagerCompat.from(context).notify(noteId, builder.build())
    }
}
