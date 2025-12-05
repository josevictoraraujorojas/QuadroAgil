package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Projeto
import com.example.quadroagil.data.repository.ProjetoRepository
import kotlinx.coroutines.launch

class EditarProjetoViewModel(
    private val repository: ProjetoRepository,
    private val projetoId: String
) : ViewModel() {

    val nome = MutableLiveData("")
    val area = MutableLiveData("")
    val email = MutableLiveData("")

    private var projetoAtual: Projeto? = null

    init {
        carregarProjeto()
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

    fun salvar(): Result<Unit> {
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