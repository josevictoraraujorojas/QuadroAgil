package com.example.quadroagil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Usuario
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    // Dados da UI
    var nome: String = ""
    var email: String = ""
    var telefone: String = ""
    var senha: String = ""
    var confirmarSenha: String = ""

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> get() = _erro

    private val _sucesso = MutableLiveData<String>()
    val sucesso: LiveData<String> get() = _sucesso

    private val _usuarioCarregado = MutableLiveData<Usuario>()
    val usuarioCarregado: LiveData<Usuario> get() = _usuarioCarregado

    fun carregarDados() {
        // Evita recarregar se já estiver carregando para não sobrescrever dados
        if (_loading.value == true) return

        _loading.value = true
        viewModelScope.launch {
            val resultado = repository.obterUsuarioAtual()
            _loading.value = false
            resultado.fold(
                onSuccess = { usuario ->
                    nome = usuario.nome
                    email = usuario.email
                    telefone = usuario.telefone
                    _usuarioCarregado.value = usuario
                    Log.d("DEBUG_PERFIL", "CARREGOU: Email original vindo do banco: '${usuario.email}'")
                },
                onFailure = {
                    _erro.value = "Erro ao carregar perfil: ${it.message}"
                }
            )
        }
    }

    private fun validarCampos(): String? {
        if (nome.isBlank()) return "Nome é obrigatório"
        if (telefone.isBlank()) return "Telefone é obrigatório"
        if (senha.isNotEmpty()) {
            if (senha.length < 6) return "Senha muito curta"
            if (senha != confirmarSenha) return "Senhas não conferem"
        }
        return null
    }

    fun salvarAlteracoes() {
        val nomeParaSalvar = nome
        val telefoneParaSalvar = telefone
        val emailParaSalvar = email
        val senhaParaSalvar = senha

        Log.d("DEBUG_PERFIL", "1. Iniciando Salvar. Email CAPTURADO: '$emailParaSalvar'")

        val erroValidacao = validarCampos()
        if (erroValidacao != null) {
            _erro.value = erroValidacao
            return
        }

        _loading.value = true

        viewModelScope.launch {
            val resultadoDados = repository.atualizarDadosUsuario(nomeParaSalvar, telefoneParaSalvar)

            resultadoDados.fold(
                onSuccess = {
                    Log.d("DEBUG_PERFIL", "2. Dados básicos salvos. Indo para sensíveis...")
                    processarAlteracoesSensiveis(emailParaSalvar, senhaParaSalvar)
                },
                onFailure = {
                    _loading.value = false
                    _erro.value = "Erro ao salvar dados: ${it.message}"
                }
            )
        }
    }

    private suspend fun processarAlteracoesSensiveis(emailNovoDigitado: String, senhaNovaDigitada: String) {
        val emailOriginal = _usuarioCarregado.value?.email?.trim() ?: ""
        val emailNovo = emailNovoDigitado.trim()

        // Log para prova real
        Log.d("DEBUG_PERFIL", "3. COMPARACAO: Original='$emailOriginal' VS NovoCapturado='$emailNovo'")

        val emailMudou = emailNovo != emailOriginal && emailNovo.isNotEmpty()
        val senhaMudou = senhaNovaDigitada.isNotEmpty()

        var erroOcorrido: String? = null

        if (emailMudou) {
            Log.d("DEBUG_PERFIL", "4. Email mudou! Atualizando...")
            val resultadoEmail = repository.atualizarEmail(emailNovo)
            resultadoEmail.onFailure { erroOcorrido = "Erro no E-mail: ${it.message}" }
        } else {
            Log.d("DEBUG_PERFIL", "4. Email IGUAL. Não atualiza.")
        }

        if (senhaMudou && erroOcorrido == null) {
            val resultadoSenha = repository.atualizarSenha(senhaNovaDigitada)
            resultadoSenha.onFailure { erroOcorrido = "Erro na Senha: ${it.message}" }
        }

        _loading.value = false

        if (erroOcorrido != null) {
            _erro.value = erroOcorrido
        } else {
            _sucesso.value = "Perfil atualizado com sucesso!"
            // Atualiza o objeto local com o novo email
            val usuarioAtualizado = _usuarioCarregado.value?.copy(email = emailNovo)
            if (usuarioAtualizado != null) {
                _usuarioCarregado.value = usuarioAtualizado
            }
        }
    }
}