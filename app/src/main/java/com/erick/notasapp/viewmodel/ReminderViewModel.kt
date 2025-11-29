package com.erick.notasapp.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Reminder
import com.erick.notasapp.data.model.Repository.ReminderRepository
import com.erick.notasapp.utils.NotificationHelper
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    // ---- CONTEXTO GLOBAL que asignarás desde la UI ----
    lateinit var appContext: Context

    var reminders = mutableStateListOf<Reminder>()
        private set

    var showDatePicker by mutableStateOf(false)
    var showTimePicker by mutableStateOf(false)

    var selectedYear by mutableStateOf(0)
    var selectedMonth by mutableStateOf(0)
    var selectedDay by mutableStateOf(0)
    var selectedHour by mutableStateOf(0)
    var selectedMinute by mutableStateOf(0)

    private var editingId: Int? = null

    fun loadReminders(noteId: Int) {
        viewModelScope.launch {
            repository.getRemindersForNote(noteId).collect { list ->
                reminders.clear()
                reminders.addAll(list)
            }
        }
    }

    fun openNewReminder() {
        val cal = Calendar.getInstance()
        selectedYear = cal.get(Calendar.YEAR)
        selectedMonth = cal.get(Calendar.MONTH) + 1
        selectedDay = cal.get(Calendar.DAY_OF_MONTH)
        selectedHour = cal.get(Calendar.HOUR_OF_DAY)
        selectedMinute = cal.get(Calendar.MINUTE)
        editingId = null
        showDatePicker = true
    }

    fun prepareEdit(reminder: Reminder) {
        val cal = Calendar.getInstance().apply { timeInMillis = reminder.reminderTime }
        selectedYear = cal.get(Calendar.YEAR)
        selectedMonth = cal.get(Calendar.MONTH) + 1
        selectedDay = cal.get(Calendar.DAY_OF_MONTH)
        selectedHour = cal.get(Calendar.HOUR_OF_DAY)
        selectedMinute = cal.get(Calendar.MINUTE)
        editingId = reminder.id
        showDatePicker = true
    }

    fun onDatePicked(y: Int, m: Int, d: Int) {
        selectedYear = y
        selectedMonth = m
        selectedDay = d
        showDatePicker = false
        showTimePicker = true
    }

    fun onTimePicked(h: Int, min: Int) {
        selectedHour = h
        selectedMinute = min
        showTimePicker = false

        val cal = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth - 1, selectedDay, h, min)
        }
        val millis = cal.timeInMillis

        if (editingId == null) {
            //lo actualizamos al guardar
            reminders.add(
                Reminder(id = 0, noteId = 0, noteTitle = "", reminderTime = millis)
            )
        } else {
            val index = reminders.indexOfFirst { it.id == editingId }
            if (index != -1) {
                reminders[index] = reminders[index].copy(reminderTime = millis)
            }
        }
    }

    /**
     * Guarda los recordatorios en la DB y programa y cancela notificaciones
*/
    suspend fun saveAll(noteId: Int, noteTitle: String) {
        // snapshot para evitar ConcurrentModification
        val snapshot = reminders.toList()

        snapshot.forEach { r ->
            // este se usa para programar una alerta futura o para quitarla -- notificationHelper
            if (r.id == 0) {
                // Inserta y obtiene el id generado por la BD
                val insertedId = repository.insert(
                    Reminder(
                        id = 0,
                        noteId = noteId,
                        noteTitle = noteTitle,
                        reminderTime = r.reminderTime
                    )
                )
                val newReminderId = insertedId.toInt()

                // aqui cancelar cualquier notificación que pudiera existir con este ID, para evitar duplicados.
                NotificationHelper.cancelNotification(appContext, newReminderId)

                // Si el recordatorio es en el futuro
                if (r.reminderTime > System.currentTimeMillis()) {
                    // programara una nueva notificación para que se muestre en el tiempo especificado
                    NotificationHelper.scheduleNotification(
                        context = appContext,
                        noteTitle = noteTitle,
                        noteId = newReminderId,     //el ID único para identificar esta alarma
                        reminderTime = r.reminderTime
                    )
                }
            } else {
                // actualizamos el noteId por si venía 0
                repository.update(r.copy(noteId = noteId, noteTitle = noteTitle))

                // cancela la notificación anterior, ya que se va a editar o reprogramar.
                NotificationHelper.cancelNotification(appContext, r.id)

                // Si la nueva hora del recordatorio es en el futuro
                if (r.reminderTime > System.currentTimeMillis()) {
                    // programama la notificación con la nueva hora.
                    NotificationHelper.scheduleNotification(
                        context = appContext,
                        noteTitle = noteTitle,
                        noteId = r.id,               // Usa el ID existente para la alarma.
                        reminderTime = r.reminderTime
                    )
                }
            }
        }
    }

    fun deleteReminder(r: Reminder) {
        viewModelScope.launch {
            if (r.id != 0) {
                repository.delete(r)
                // aqui ancelamos la notificación programada, porque el recordatorio fue eliminado
                NotificationHelper.cancelNotification(appContext, r.id)
            }
            reminders.remove(r)
        }
    }

    fun hidePickers() {
        showDatePicker = false
        showTimePicker = false
    }

    fun clear() {
        reminders.clear()
    }
}
