package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.UsuarioRepository

class EquipeViewModelFactory(
    private val idProjeto: String,
    private val repoParticipacao: ParticipacaoRepository,
    private val repoUsuario: UsuarioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EquipeViewModel(idProjeto, repoParticipacao, repoUsuario) as T
    }
}