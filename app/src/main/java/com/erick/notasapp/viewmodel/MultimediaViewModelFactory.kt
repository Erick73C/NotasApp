package com.erick.notasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erick.notasapp.data.model.Repository.MultimediaRepository

class MultimediaViewModelFactory(
    private val repository: MultimediaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultimediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MultimediaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}