package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Projeto
import com.example.quadroagil.data.model.Papel
import com.example.quadroagil.data.repository.ProjetoRepository
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class EditarProjetoViewModel(
    private val repository: ProjetoRepository,
    private val projetoId: String
) : ViewModel() {

    val nome = MutableLiveData("")
    val area = MutableLiveData("")
    val email = MutableLiveData("")

    val podeEditar = MutableLiveData(false)

    private val participacaoRepo = ParticipacaoRepository()
    private val usuarioRepo = UsuarioRepository()

    private var projetoAtual: Projeto? = null

    init {
        carregarProjeto()
        verificarPermissao()
    }

    private fun carregarProjeto() {
        viewModelScope.launch {
            val projeto = repository.buscarProjeto(projetoId)
            projeto?.let {
                projetoAtual = it
                nome.value = it.nome
                area.value = it.area
                email.value = it.email
            }
        }
    }

    private fun verificarPermissao() {
        viewModelScope.launch {

            val usuario = usuarioRepo.usuarioAtual() ?: return@launch
            val userId = usuario.uid

            val participacao =
                participacaoRepo.buscarParticipacao(userId, projetoId)

            // Só DONO pode editar
            podeEditar.value = participacao?.papel == Papel.DONO
        }
    }

    fun salvar(): Result<Unit> {

        if (podeEditar.value == false) {
            return Result.failure(Exception("Você não é o dono do projeto"))
        }

        val atual = projetoAtual ?: return Result.failure(Exception("Projeto não carregado"))

        val atualizado = atual.copy(
            nome = nome.value ?: "",
            area = area.value ?: "",
            email = email.value ?: ""
        )

        return try {
            viewModelScope.launch {
                repository.atualizarProjeto(atualizado)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
