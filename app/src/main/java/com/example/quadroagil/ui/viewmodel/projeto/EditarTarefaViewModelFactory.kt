package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.data.repository.NotaRepository
import com.example.quadroagil.data.repository.UsuarioRepository

class EditarTarefaViewModelFactory(
    private val idProjeto: String,
    private val idNota: String,
    private val status: Status,
    private val notaRepo: NotaRepository = NotaRepository(),
    private val usuarioRepo: UsuarioRepository = UsuarioRepository()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditarTarefaViewModel::class.java)) {
            return EditarTarefaViewModel(
                idProjeto,
                idNota,
                status,
                notaRepo,
                usuarioRepo
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido")
    }
}
