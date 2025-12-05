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

    val email = MutableStateFlow("")

    private val _mensagem = MutableStateFlow<String?>(null)
    val mensagem: StateFlow<String?> = _mensagem

    private val _sucesso = MutableStateFlow(false)
    val sucesso: StateFlow<Boolean> = _sucesso

    fun adicionarColaborador() {
        val emailDigitado = email.value.trim()


        if (emailDigitado.isEmpty()) {
            _mensagem.value = "O email não pode estar vazio"
            return
        }

        viewModelScope.launch {
            try {

                val usuario = usuarioRepository.buscarPorEmail(emailDigitado)

                if (usuario == null) {
                    _mensagem.value = "Nenhum usuário encontrado com este email"
                    return@launch
                }

                val result = repository.adicionarParticipacao(
                    idUsuario = usuario.id,
                    idProjeto = idProjeto,
                    papel = Papel.COLABORADOR
                )

                if (result.isSuccess) {
                    _mensagem.value = "Colaborador adicionado com sucesso!"
                    _sucesso.value = true
                } else {

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
