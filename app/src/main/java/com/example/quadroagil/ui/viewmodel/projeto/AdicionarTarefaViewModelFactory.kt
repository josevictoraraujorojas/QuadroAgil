package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.repository.NotaRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import com.example.quadroagil.data.model.Status

class AdicionarTarefaViewModelFactory(
    private val idProjeto: String,
    private val status: Status,
    private val notaRepo: NotaRepository = NotaRepository(),
    private val usuarioRepo: UsuarioRepository = UsuarioRepository()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdicionarTarefaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdicionarTarefaViewModel(idProjeto, status, notaRepo, usuarioRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
