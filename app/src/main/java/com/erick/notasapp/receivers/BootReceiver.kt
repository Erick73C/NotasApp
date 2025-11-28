package com.erick.notasapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.erick.notasapp.data.model.Repository.ReminderRepository
import com.erick.notasapp.data.model.database.DatabaseProvider // Asegúrate de que esta clase exista y sea correcta
import com.erick.notasapp.utils.NotificationHelper // Asegúrate de que esta clase exista y sea correcta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    // SE EJECUTA AL COMPLETARSE EL ARRANQUE DEL SISTEMA (INTENT.ACTION_BOOT_COMPLETED)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // Las operaciones de base de datos se ejecutan en un hilo de IO
            CoroutineScope(Dispatchers.IO).launch {

                // 1. INICIALIZAR LA BASE DE DATOS Y EL REPOSITORIO DE RECORDATORIOS
                val db = DatabaseProvider.provideDatabase(context)
                val repository = ReminderRepository(db.reminderDao())

                // 2. OBTENER LA LISTA DE TODOS LOS RECORDATORIOS FUTUROS ACTIVOS DE LA BD
                val futureReminders = repository.getAllFutureReminders()

                // 3. RECORRER LA LISTA Y REPROGRAMAR CADA ALARMA
                futureReminders.forEach { reminder ->
                    // Vuelve a llamar a la función de programación de AlarmManager
                    // Las propiedades del objeto 'reminder' (noteTitle, noteId, reminderTime)
                    // ahora están definidas en el modelo Reminder.kt
                    NotificationHelper.scheduleNotification(
                        context,
                        reminder.noteTitle,
                        reminder.noteId,
                        reminder.reminderTime
                    )
                }
            }
        }
    }
}