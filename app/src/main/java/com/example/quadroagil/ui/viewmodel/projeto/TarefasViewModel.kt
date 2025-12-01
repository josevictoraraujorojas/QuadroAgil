package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.*
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.data.repository.NotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class TarefasViewModel(
    private val idProjeto: String,
    private val repository: NotaRepository = NotaRepository()
) : ViewModel() {

    // -----------------------
    // Todas as notas do projeto
    // -----------------------
    private val _todasNotas = MutableLiveData<List<Nota>>(emptyList())
    val todasNotas: LiveData<List<Nota>> = _todasNotas

    // -----------------------
    // Notas filtradas por status
    // -----------------------
    val notasAfazer = MediatorLiveData<List<Nota>>()
    val notasFazendo = MediatorLiveData<List<Nota>>()
    val notasFeito = MediatorLiveData<List<Nota>>()

    init {
        // Atualiza cada lista filtrada quando todasNotas mudar
        notasAfazer.addSource(_todasNotas) { notas ->
            notasAfazer.value = notas.filter { it.status == Status.AFAZER }
        }
        notasFazendo.addSource(_todasNotas) { notas ->
            notasFazendo.value = notas.filter { it.status == Status.FAZENDO }
        }
        notasFeito.addSource(_todasNotas) { notas ->
            notasFeito.value = notas.filter { it.status == Status.FEITO }
        }

        // Iniciar o fluxo de atualizações em tempo real do Firestore
        observarNotasTempoReal()
    }

    // -----------------------
    // Observa notas em tempo real do Firestore
    // -----------------------
    private fun observarNotasTempoReal() {
        viewModelScope.launch(Dispatchers.IO) {
            callbackFlow {
                val listener = repository.observarNotasDoProjeto(idProjeto) { notas ->
                    trySend(notas)
                }
                awaitClose { listener.remove() }
            }.collect { notas ->
                _todasNotas.postValue(notas)
            }
        }
    }

    // -----------------------
    // Operações sobre notas
    // -----------------------

    fun adicionarNota(nota: Nota) {
        viewModelScope.launch {
            repository.cadastrarNota(nota)
        }
    }

    fun removerNota(nota: Nota) {
        viewModelScope.launch {
            repository.removerNota(nota)
        }
    }

    fun editarNota(nota: Nota) {
        viewModelScope.launch {
            repository.atualizarNota(nota)
        }
    }

    fun alterarStatusNota(idNota: String, novoStatus: Status) {
        viewModelScope.launch {
            repository.alterarStatusNota(idNota, novoStatus.name)
        }
    }
}
