package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.repository.ResumoProjetoRepository

class VisaoGeralViewModelFactory(
    private val idProjeto: String,
    private val repository: ResumoProjetoRepository = ResumoProjetoRepository()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisaoGeralViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VisaoGeralViewModel(idProjeto, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
