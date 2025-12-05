package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.repository.NotaRepository

class TarefasViewModelFactory(
    private val idProjeto: String,
    private val repository: NotaRepository = NotaRepository()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TarefasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TarefasViewModel(idProjeto, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
