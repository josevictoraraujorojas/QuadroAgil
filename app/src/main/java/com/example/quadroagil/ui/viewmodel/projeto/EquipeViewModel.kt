package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Papel
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EquipeViewModel(
    private val idProjeto: String,
    private val repoParticipacao: ParticipacaoRepository,
    private val repoUsuario: UsuarioRepository
) : ViewModel() {

    private val _listaMembros = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val listaMembros: StateFlow<List<Pair<String, String>>> = _listaMembros

    private val _mensagem = MutableStateFlow<String?>(null)
    val mensagem: StateFlow<String?> = _mensagem

    private var usuarioLogadoPapel: Papel? = null

    init {
        carregarUsuarioLogado()
        observarEquipeTempoReal()
    }

    private fun carregarUsuarioLogado() {
        viewModelScope.launch {
            val usuarioLogado = repoUsuario.obterUsuarioLogado()
            if (usuarioLogado != null) {
                val participacao =
                    repoParticipacao.buscarParticipacao(usuarioLogado.id, idProjeto)
                usuarioLogadoPapel = participacao?.papel
            }
        }
    }

    private fun observarEquipeTempoReal() {
        repoParticipacao.listenUsuariosDoProjeto(idProjeto) { participacoes ->
            viewModelScope.launch {
                try {
                    val membros = participacoes.mapNotNull { part ->
                        repoUsuario.buscarPorId(part.idUsuario)?.let {
                            Pair(it.nome, it.id)
                        }
                    }

                    _listaMembros.value = membros

                } catch (e: Exception) {
                    _mensagem.value = "Erro ao carregar equipe: ${e.message}"
                }
            }
        }
    }

    fun removerMembro(idUsuario: String) {
        viewModelScope.launch {
            if (usuarioLogadoPapel != Papel.DONO) {
                _mensagem.value = "Apenas o dono pode remover membros"
                return@launch
            }

            try {
                val result = repoParticipacao.removerParticipacao(idUsuario, idProjeto)
                if (!result.isSuccess) {
                    _mensagem.value = "Erro ao remover membro"
                }
            } catch (e: Exception) {
                _mensagem.value = "Erro: ${e.message}"
            }
        }
    }

    fun podeAdicionarRemoverMembro(): Boolean {
        return usuarioLogadoPapel == Papel.DONO
    }
}
