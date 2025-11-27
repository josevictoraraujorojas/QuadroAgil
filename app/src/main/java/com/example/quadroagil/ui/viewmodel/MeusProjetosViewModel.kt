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

class MeusProjetosViewModel(
    private val participacaoRepository: ParticipacaoRepository = ParticipacaoRepository(),
    private val projetoRepository: ProjetoRepository = ProjetoRepository()
) : ViewModel() {

    private val _projetos = MutableLiveData<List<Projeto>>()
    val projetos: LiveData<List<Projeto>> get() = _projetos

    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> get() = _erro

    fun carregarProjetosDoUsuario() {
        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual == null) {
            _erro.value = "Usuário não autenticado"
            return
        }

        val idUsuario = usuarioAtual.uid // ← você usa o UID do Firebase Auth

        viewModelScope.launch {
            try {
                // 1️⃣ Buscar participações onde ele é DONO
                val participacoes = participacaoRepository.listarProjetosDoUsuario(idUsuario)
                    .filter { it.papel == Papel.DONO }

                // 2️⃣ Buscar projetos reais no Firestore
                val listaProjetos = mutableListOf<Projeto>()

                participacoes.forEach { part ->
                    val projeto = projetoRepository.buscarProjeto(part.idProjeto)
                    if (projeto != null) listaProjetos.add(projeto)
                }

                _projetos.value = listaProjetos

            } catch (e: Exception) {
                _erro.value = e.message ?: "Erro desconhecido"
            }
        }
    }
    fun excluirProjeto(idProjeto: String) {
        viewModelScope.launch {
            try {
                // Deletar participações relacionadas
                val usuarios = participacaoRepository.listarUsuariosDoProjeto(idProjeto)
                usuarios.forEach { participacao ->
                    participacaoRepository.removerParticipacao(participacao.id)
                }

                // Deletar o projeto
                val resultado = projetoRepository.removerProjeto(idProjeto)

                resultado.fold(
                    onSuccess = {
                        // Atualizar lista local removendo o projeto
                        val listaAtual = _projetos.value?.toMutableList() ?: mutableListOf()
                        listaAtual.removeAll { it.id == idProjeto }
                        _projetos.value = listaAtual
                    },
                    onFailure = {
                        _erro.value = it.message ?: "Erro ao remover projeto"
                    }
                )

            } catch (e: Exception) {
                _erro.value = e.message ?: "Erro ao excluir"
            }
        }
    }
}