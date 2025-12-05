package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.repository.ProjetoRepository

class EditarProjetoViewModelFactory(
    private val repository: ProjetoRepository,
    private val projetoId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditarProjetoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditarProjetoViewModel(repository, projetoId) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido")
    }
}