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

    fun saveNote(noteId: Int?, onSaved: (Int) -> Unit) {
        viewModelScope.launch {
            if (noteId == null) {
                val id = repository.insert(
                    Note(
                        title = titulo,
                        description = descripcion,
                        type = "nota"
                    )
                ).toInt()

                onSaved(id)

            } else {
                repository.update(
                    Note(
                        id = noteId,
                        title = titulo,
                        description = descripcion,
                        type = "nota"
                    )
                )
                onSaved(noteId)
            }
        }
    }


    fun clearFields() {
        titulo = ""
        descripcion = ""
    }

}