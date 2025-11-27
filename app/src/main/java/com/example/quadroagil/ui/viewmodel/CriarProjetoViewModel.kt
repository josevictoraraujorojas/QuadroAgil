package com.example.quadroagil.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.Papel
import com.example.quadroagil.data.model.Projeto
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.ProjetoRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class CriarProjetoViewModel(
    private val projetoRepository: ProjetoRepository = ProjetoRepository(),
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val participacaoRepository: ParticipacaoRepository = ParticipacaoRepository()
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

        viewModelScope.launch {

            //Buscar usuário logado no Firestore
            val usuarioLogado = usuarioRepository.obterUsuarioLogado()

            if (usuarioLogado == null) {
                _erro.value = "Usuário não encontrado no banco de dados"
                return@launch
            }

            //Criar objeto Projeto
            val projeto = Projeto(
                id = "",
                nome = nome,
                area = area,
                email = email,
                descricao = descricao,
                dataCriacao = null,
                idResumoProjeto = ""
            )

            //Salvar projeto no Firestore
            val resultado = projetoRepository.criarProjeto(projeto)

            resultado.fold(
                onSuccess = { projetoCriado ->

                    //Criar Participacao como DONO
                    participacaoRepository.adicionarParticipacao(
                        idUsuario = usuarioLogado.id,
                        idProjeto = projetoCriado.id,
                        papel = Papel.DONO
                    )

                    _sucesso.value = projetoCriado
                },
                onFailure = { erro ->
                    _erro.value = erro.message ?: "Erro ao criar projeto"
                }
            )
        }
    }
}
