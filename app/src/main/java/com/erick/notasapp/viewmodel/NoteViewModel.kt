package com.erick.notasapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Note
import com.erick.notasapp.data.model.Repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//Su función es conectar la interfaz (UI) con la base de datos (Room), sin que la UI tenga que conocer directamente el repositorio o los DAOs.

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    // Flujo de notas desde la BD
    val allNotes: StateFlow<List<Note>> = repository
        .getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(note: Note) {
        viewModelScope.launch { repository.insert(note) }
    }

    fun update(note: Note) {
        viewModelScope.launch { repository.update(note) }
    }

    fun delete(note: Note) {
        viewModelScope.launch { repository.delete(note) }
    }

    fun search(query: String) = repository.searchNotes(query)
}