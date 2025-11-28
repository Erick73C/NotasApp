package com.erick.notasapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Reminder
import com.erick.notasapp.data.model.Repository.ReminderRepository
import kotlinx.coroutines.launch
import java.util.*

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

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
            // CORRECCIÓN 1: Añadido noteTitle = ""
            reminders.add(
                Reminder(id = 0, noteId = -1, noteTitle = "", reminderTime = millis)
            )
        } else {
            val index = reminders.indexOfFirst { it.id == editingId }
            if (index != -1) {
                reminders[index] = reminders[index].copy(reminderTime = millis)
            }
        }
    }

    suspend fun saveAll(noteId: Int) {
        val snapshot = reminders.toList()

        snapshot.forEach { r ->
            if (r.id == 0) {
                // CORRECCIÓN 2: Añadido noteTitle = ""
                repository.insert(
                    Reminder(
                        id = 0,
                        noteId = noteId,
                        noteTitle = "", // Requerido para la inserción
                        reminderTime = r.reminderTime
                    )
                )
            } else {
                repository.update(r.copy(noteId = noteId))
            }
        }
    }

    fun deleteReminder(r: Reminder) {
        viewModelScope.launch {
            if (r.id != 0) repository.delete(r)
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
