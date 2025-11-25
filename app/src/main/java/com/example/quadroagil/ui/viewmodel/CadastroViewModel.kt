package com.example.quadroagil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Usuario
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.util.Date

class CadastroViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    // Dados do usuário preenchidos ao longo das telas
    var nome: String = ""
    var email: String = ""
    var dataNasc: Date? = null
    var telefone: String = ""
    var senha: String = ""

    // LiveData para comunicar com a UI
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> get() = _erro

    private val _sucesso = MutableLiveData<Usuario>()
    val sucesso: LiveData<Usuario> get() = _sucesso


    // --- Função de validação antes de cadastrar ---
    private fun validarCampos(): String? {
        if (nome.isBlank()) return "Nome é obrigatório"
        if (email.isBlank()) return "Email é obrigatório"
        if (dataNasc == null) return "Data de nascimento é obrigatória"
        if (telefone.isBlank()) return "Telefone é obrigatório"
        if (senha.length < 6) return "A senha deve ter pelo menos 6 caracteres"
        return null
    }


    // --- Função final para cadastrar ---
    fun cadastrar() {
        val erroValidacao = validarCampos()
        if (erroValidacao != null) {
            _erro.value = erroValidacao
            return
        }

        _loading.value = true

        val usuario = Usuario(
            id = "",
            nome = nome,
            email = email,
            dataNasc = dataNasc,
            telefone = telefone
        )

        viewModelScope.launch {
            val resultado = repository.cadastrarUsuario(email, senha, usuario)

            _loading.value = false

            resultado.fold(
                onSuccess = {
                    _sucesso.value = it
                },
                onFailure = {
                    _erro.value = it.message ?: "Erro desconhecido"
                }
            )
        }
    }
}