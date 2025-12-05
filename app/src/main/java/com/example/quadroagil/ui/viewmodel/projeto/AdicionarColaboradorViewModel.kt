package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.model.Papel
import com.example.quadroagil.data.repository.UsuarioRepository

class AdicionarColaboradorViewModel(
    private val idProjeto: String,
    private val repository: ParticipacaoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    // Campo usado no XML
    val email = MutableStateFlow("")

    // Estado para exibir mensagens no fragment
    private val _mensagem = MutableStateFlow<String?>(null)
    val mensagem: StateFlow<String?> = _mensagem

    fun adicionarColaborador() {
        val emailDigitado = email.value.trim()

        if (emailDigitado.isEmpty()) {
            _mensagem.value = "O email não pode estar vazio"
            return
        }

        viewModelScope.launch {
            try {
                // 1. Buscar ID do usuário pelo email
                val usuario = usuarioRepository.buscarPorEmail(emailDigitado)

                if (usuario == null) {
                    _mensagem.value = "Nenhum usuário encontrado com este email"
                    return@launch
                }

                // 2. Tentar adicionar participação
                val result = repository.adicionarParticipacao(
                    idUsuario = usuario.id,
                    idProjeto = idProjeto,
                    papel = Papel.COLABORADOR
                )

                if (result.isSuccess) {
                    _mensagem.value = "Colaborador adicionado com sucesso!"
                } else {

                    // PEGAR A MENSAGEM DO REPOSITORY
                    val erro = result.exceptionOrNull()?.message

                    if (erro?.contains("já participa") == true) {
                        _mensagem.value = "Este usuário já participa deste projeto"
                    } else {
                        _mensagem.value = "Falha ao adicionar colaborador"
                    }
                }

            } catch (e: Exception) {
                _mensagem.value = "Erro: ${e.message}"
            }
        }
    }
}
