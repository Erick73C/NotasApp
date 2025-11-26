package com.erick.notasapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erick.notasapp.data.model.Repository.ReminderRepository

class ReminderViewModelFactory(
    private val repo: ReminderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            return ReminderViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}