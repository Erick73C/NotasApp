package com.erick.notasapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erick.notasapp.data.model.Repository.NotesRepository

class NoteListViewModelFactory(private val repository: NotesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}