package com.erick.notasapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Reminder
import com.erick.notasapp.data.model.Repository.ReminderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _remindersState = mutableStateOf<List<Reminder>>(emptyList())
    var reminders = mutableStateListOf<Reminder>()
        private set

    // Pickers
    var showDatePicker by mutableStateOf(false)
        private set
    var showTimePicker by mutableStateOf(false)
        private set

    // Valores limpios (nombres únicos)
    var selectedYear by mutableStateOf(2025)
    var selectedMonth by mutableStateOf(1)
    var selectedDay by mutableStateOf(1)
    var selectedHour by mutableStateOf(0)
    var selectedMinute by mutableStateOf(0)

    private var editingId: Int? = null
    private var collectorJob: Job? = null

    fun loadReminders(noteId: Int) {
        collectorJob?.cancel()
        collectorJob = viewModelScope.launch {
            repository.getRemindersForNote(noteId).collectLatest { list ->
                reminders.clear()
                reminders.addAll(list)
            }
        }
    }

    fun openNewReminder() {
        val now = Calendar.getInstance()
        selectedYear = now.get(Calendar.YEAR)
        selectedMonth = now.get(Calendar.MONTH) + 1
        selectedDay = now.get(Calendar.DAY_OF_MONTH)
        selectedHour = now.get(Calendar.HOUR_OF_DAY)
        selectedMinute = now.get(Calendar.MINUTE)

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

    fun onDatePicked(y: Int, m1: Int, d: Int) {
        selectedYear = y
        selectedMonth = m1
        selectedDay = d

        showDatePicker = false
        showTimePicker = true
    }

    fun onTimePicked(h: Int, min: Int, noteId: Int? = null, onSaved: () -> Unit = {}) {
        selectedHour = h
        selectedMinute = min

        showTimePicker = false

        // 👉 SE GUARDA EL RECORDATORIO EN ESTE MOMENTO
        if (noteId != null) {
            saveReminder(noteId) {
                onSaved()
            }
        }
    }

    fun saveReminder(noteId: Int, onComplete: () -> Unit = {}) {

        val cal = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth - 1, selectedDay, selectedHour, selectedMinute, 0)
        }
        val millis = cal.timeInMillis

        viewModelScope.launch {

            if (editingId == null) {
                val newId = repository.insert(
                    Reminder(
                        id = 0,
                        noteId = noteId,
                        reminderTime = millis
                    )
                ).toInt()

                reminders.add(
                    Reminder(
                        id = newId,
                        noteId = noteId,
                        reminderTime = millis
                    )
                )

            } else {

                val updated = Reminder(
                    id = editingId!!,
                    noteId = noteId,
                    reminderTime = millis
                )

                repository.update(updated)

                val index = reminders.indexOfFirst { it.id == editingId }
                if (index != -1) reminders[index] = updated
            }

            editingId = null
            onComplete()
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.delete(reminder)
            reminders.remove(reminder)
        }
    }


    fun hidePickers() {
        showDatePicker = false
        showTimePicker = false
    }

    override fun onCleared() {
        super.onCleared()
        collectorJob?.cancel()
    }
}
