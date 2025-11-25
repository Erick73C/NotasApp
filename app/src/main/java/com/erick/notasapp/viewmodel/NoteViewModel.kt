package com.erick.notasapp.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Note
import com.erick.notasapp.data.model.Repository.NotesRepository
import kotlinx.coroutines.launch
import java.io.File

class NoteViewModel(private val repository: NotesRepository) : ViewModel() {

    var titulo by mutableStateOf("")
        private set

    var descripcion by mutableStateOf("")
        private set

    fun onTituloChange(newValue: String) {
        titulo = newValue
    }

    fun onDescripcionChange(newValue: String) {
        descripcion = newValue
    }

    fun loadNoteById(noteId: Int) {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            note?.let {
                titulo = it.title
                descripcion = it.description
            }
        }
    }

    fun saveNote(noteId: Int? = null, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (noteId == null) {

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

    fun clearFields() {
        titulo = ""
        descripcion = ""
    }

}