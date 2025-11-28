package com.example.quadroagil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Papel
import com.example.quadroagil.data.model.Projeto
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.ProjetoRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ColaboracoesViewModel(
    private val participacaoRepository: ParticipacaoRepository = ParticipacaoRepository(),
    private val projetoRepository: ProjetoRepository = ProjetoRepository()
) : ViewModel() {

    private val _projetos = MutableLiveData<List<Projeto>>()
    val projetos: LiveData<List<Projeto>> get() = _projetos

    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> get() = _erro

    // LiveData para sinalizar sucesso em operações
    private val _sucesso = MutableLiveData<Boolean>()
    val sucesso: LiveData<Boolean> get() = _sucesso

    fun carregarColaboracoes() {
        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual == null) {
            _erro.value = "Usuário não autenticado"
            return
        }

        val idUsuario = usuarioAtual.uid

        viewModelScope.launch {
            try {
                // Buscar participações do usuário e filtrar onde NÃO é dono
                val participacoes = participacaoRepository.listarProjetosDoUsuario(idUsuario)
                    .filter { it.papel != Papel.DONO }   // só colaborações

                // Buscar cada projeto correspondente
                val listaProjetos = mutableListOf<Projeto>()
                participacoes.forEach { part ->
                    val projeto = projetoRepository.buscarProjeto(part.idProjeto)
                    if (projeto != null) listaProjetos.add(projeto)
                }

                _projetos.value = listaProjetos

            } catch (e: Exception) {
                _erro.value = e.message ?: "Erro ao carregar colaborações"
            }
        }
    }

    fun sairDoProjeto(idProjeto: String) {
        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual == null) {
            _erro.value = "Usuário não autenticado"
            return
        }

        val idUsuario = usuarioAtual.uid

        viewModelScope.launch {
            try {
                // Buscar a participação específica (do usuário logado nesse projeto)
                val participacoes = participacaoRepository.listarProjetosDoUsuario(idUsuario)
                val participacao = participacoes.find { it.idProjeto == idProjeto }

                if (participacao == null) {
                    _erro.value = "Participação não encontrada"
                    return@launch
                }

                // Remover a participação (sair do projeto)
                val resultado = participacaoRepository.removerParticipacao(participacao.id)
                resultado.fold(
                    onSuccess = {
                        // recarrega lista e sinaliza sucesso
                        carregarColaboracoes()
                        _sucesso.value = true
                    },
                    onFailure = {
                        _erro.value = it.message ?: "Erro ao sair do projeto"
                    }
                )

            } catch (e: Exception) {
                _erro.value = e.message ?: "Erro ao sair do projeto"
            }
        }
    }
}