package com.erick.notasapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Note
import com.erick.notasapp.data.model.Repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    // --- Estados para la pantalla de nueva nota ---
    var titulo by mutableStateOf("")
        private set

    var descripcion by mutableStateOf("")
        private set

    // --- Métodos para modificar los valores desde la UI ---
    fun onTituloChange(newValue: String) {
        titulo = newValue
    }

    fun onDescripcionChange(newValue: String) {
        descripcion = newValue
    }

    // --- Cargar nota existente (para editar) ---
    fun loadNoteById(noteId: Int) {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            note?.let {
                titulo = it.title
                descripcion = it.description
            }
        }
    }

    // --- Guardar nueva nota o actualizar existente ---
    fun saveNote(noteId: Int? = null, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (noteId == null) {
                // Crear nueva nota
                if (titulo.isNotBlank() || descripcion.isNotBlank()) {
                    repository.insert(
                        Note(
                            title = titulo,
                            description = descripcion,
                            type = "nota"
                        )
                    )
                }
            } else {
                // Actualizar nota existente
                val existingNote = repository.getNoteById(noteId)
                existingNote?.let {
                    repository.update(
                        it.copy(title = titulo, description = descripcion)
                    )
                }
            }
            onComplete()
        }
    }

    // --- Limpiar campos ---
    fun clearFields() {
        titulo = ""
        descripcion = ""
    }
}
