package com.erick.notasapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Note
import com.erick.notasapp.data.model.Repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//se encargue exclusivamente de la lista de notas (lectura, eliminación y actualización automática cuando cambia la base de datos).
class NoteListViewModel(private val repository: NoteRepository) : ViewModel() {

    // --- Lista de todas las notas expuesta como flujo (Flow -> StateFlow) ---
    val allNotes: StateFlow<List<Note>> = repository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Eliminar nota ---
    fun delete(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}