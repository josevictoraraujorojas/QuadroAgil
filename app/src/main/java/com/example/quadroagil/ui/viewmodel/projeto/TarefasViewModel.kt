package com.example.quadroagil.ui.viewmodel.projeto

import android.util.Log
import androidx.lifecycle.*
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.data.model.Papel
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.data.repository.NotaRepository
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class TarefasViewModel(
    private val idProjeto: String,
    private val repository: NotaRepository = NotaRepository(),
    private val repoUsuario: UsuarioRepository = UsuarioRepository(),
    private val repoParticipacao: ParticipacaoRepository = ParticipacaoRepository()
) : ViewModel() {

    private val _todasNotas = MutableLiveData<List<Nota>>(emptyList())
    val todasNotas: LiveData<List<Nota>> = _todasNotas

    val notasAfazer = MediatorLiveData<List<Nota>>()
    val notasFazendo = MediatorLiveData<List<Nota>>()
    val notasFeito = MediatorLiveData<List<Nota>>()

    private var usuarioLogadoPapel: Papel? = null
    private var usuarioLogadoId: String? = null

    private var responsavel: String? = null

    private val _usuarioCarregado = MutableLiveData(false)
    val usuarioCarregado: LiveData<Boolean> = _usuarioCarregado

    init {
        carregarUsuarioLogado()
        notasAfazer.addSource(_todasNotas) { notas -> notasAfazer.value = notas.filter { it.status == Status.AFAZER } }
        notasFazendo.addSource(_todasNotas) { notas -> notasFazendo.value = notas.filter { it.status == Status.FAZENDO } }
        notasFeito.addSource(_todasNotas) { notas -> notasFeito.value = notas.filter { it.status == Status.FEITO } }

        observarNotasTempoReal()
    }

    private fun carregarUsuarioLogado() {
        viewModelScope.launch {
            val usuario = repoUsuario.obterUsuarioLogado()
            usuario?.let {
                usuarioLogadoId = it.id
                val participacao = repoParticipacao.buscarParticipacao(it.id, idProjeto)
                usuarioLogadoPapel = participacao?.papel
                _usuarioCarregado.postValue(true)
            }
        }
    }

    private fun observarNotasTempoReal() {
        viewModelScope.launch(Dispatchers.IO) {
            callbackFlow {
                val listener = repository.observarNotasDoProjeto(idProjeto) { notas -> trySend(notas) }
                awaitClose { listener.remove() }
            }.collect { notas ->
                notas.forEach {
                    nota ->
                    if (nota.idUsuario==""){
                        nota.nomeUsuario = "Nenhum"
                    }else{
                        nota.nomeUsuario = repoUsuario.buscarPorId(nota.idUsuario)?.nome ?: "Nenhum"
                    }
                }
                _todasNotas.postValue(notas)
            }
        }
    }

    fun podeEditarOuRemover(): Boolean {
        return usuarioLogadoPapel == Papel.DONO
    }

    fun podeAlterarStatus(nota: Nota): Boolean {
        return usuarioLogadoPapel == Papel.DONO || nota.idUsuario == usuarioLogadoId
    }

    fun removerNota(nota: Nota) {
        if (!podeEditarOuRemover()) return
        viewModelScope.launch {
            repository.removerNota(nota)
        }
    }
}