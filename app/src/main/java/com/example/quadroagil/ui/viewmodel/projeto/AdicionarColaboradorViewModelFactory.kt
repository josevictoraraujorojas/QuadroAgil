package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.UsuarioRepository

class AdicionarColaboradorViewModelFactory(
    private val idProjeto: String,
    private val participacaoRepository: ParticipacaoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdicionarColaboradorViewModel::class.java)) {
            return AdicionarColaboradorViewModel(
                idProjeto,
                participacaoRepository,
                usuarioRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
