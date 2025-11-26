package com.example.quadroagil.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Projeto
import com.example.quadroagil.data.repository.ProjetoRepository
import kotlinx.coroutines.launch

class CriarProjetoViewModel(
    private val repository: ProjetoRepository = ProjetoRepository()
) : ViewModel() {

    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> get() = _erro

    private val _sucesso = MutableLiveData<Projeto>()
    val sucesso: LiveData<Projeto> get() = _sucesso

    fun criarProjeto(nome: String, area: String, email: String, descricao: String) {

        if (nome.isBlank()) {
            _erro.value = "O nome do projeto é obrigatório"
            return
        }

        if (email.isBlank()) {
            _erro.value = "O email do projeto é obrigatório"
            return
        }

        if (descricao.isBlank()) {
            _erro.value = "A descrição é obrigatória"
            return
        }

        val projeto = Projeto(
            id = "",
            nome = nome,
            area = area,
            email = email,
            descricao = descricao,
            dataCriacao = null, // será preenchido pelo repository
            idResumoProjeto = ""
        )

        viewModelScope.launch {
            val resultado = repository.criarProjeto(projeto)

            resultado.fold(
                onSuccess = { _sucesso.value = it },
                onFailure = { _erro.value = it.message ?: "Erro desconhecido" }
            )
        }
    }
}
