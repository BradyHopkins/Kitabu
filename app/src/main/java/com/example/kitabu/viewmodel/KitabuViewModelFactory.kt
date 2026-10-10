package com.example.kitabu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kitabu.repository.KitabuRepository

class KitabuViewModelFactory(
    private val repository: KitabuRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(KitabuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KitabuViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}