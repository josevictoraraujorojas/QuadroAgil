package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.*
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.data.repository.NotaRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditarTarefaViewModel(
    private val idProjeto: String,
    private val idNota: String,
    private val status: Status,
    private val notaRepo: NotaRepository = NotaRepository(),
    private val usuarioRepo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    val titulo = MutableLiveData<String>()
    val descricao = MutableLiveData<String>()

    // Agora guarda o **ID** do responsável (não o nome)
    val responsavelSelecionado = MutableLiveData<String>()

    val dataInicio = MutableLiveData<String>()
    val dataFim = MutableLiveData<String>()

    private val _usuariosProjeto = MutableLiveData<List<UsuarioItem>>()
    val usuariosProjeto: LiveData<List<UsuarioItem>> = _usuariosProjeto

    private val _notaAtualizada = MutableLiveData<Boolean>()
    val notaAtualizada: LiveData<Boolean> = _notaAtualizada

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    init {
        carregarUsuariosDoProjeto()
    }

    // ----------------------------------------------------------
    // 1) CARREGA USUÁRIOS → depois carrega a nota
    // ----------------------------------------------------------
    private fun carregarUsuariosDoProjeto() {
        viewModelScope.launch {
            val usuarios = usuarioRepo.listarUsuariosDoProjetoSimples(idProjeto)
            val lista = usuarios.map { UsuarioItem(it.id, it.nome) }

            _usuariosProjeto.postValue(lista)

            carregarNotaExistente()
        }
    }

    // ----------------------------------------------------------
    // 2) BUSCA A NOTA EXISTENTE
    // ----------------------------------------------------------
    private fun carregarNotaExistente() {
        viewModelScope.launch {
            val nota = notaRepo.buscarNotaPorId(idNota)
            nota?.let {

                titulo.postValue(it.titulo)
                descricao.postValue(it.descricao)
                dataInicio.postValue(it.dataInicio?.let { d -> dateFormatter.format(d) })
                dataFim.postValue(it.dataFim?.let { d -> dateFormatter.format(d) })

                // Armazena o ID do responsável
                responsavelSelecionado.postValue(it.idUsuario)
            }
        }
    }

    // ----------------------------------------------------------
    // 3) ATUALIZA NOTA
    // ----------------------------------------------------------
    fun atualizarNota() {
        val usuarioId = responsavelSelecionado.value ?: return

        val dataInicioDate = dataInicio.value?.let { dateFormatter.parse(it) }
        val dataFimDate = dataFim.value?.let { dateFormatter.parse(it) }

        val notaAtualizadaObj = Nota(
            id = idNota,
            titulo = titulo.value ?: "",
            descricao = descricao.value ?: "",
            idProjeto = idProjeto,
            idUsuario = usuarioId,
            status = status,
            dataInicio = dataInicioDate,
            dataFim = dataFimDate
        )

        viewModelScope.launch {
            val result = notaRepo.atualizarNota(notaAtualizadaObj)
            _notaAtualizada.postValue(result.isSuccess)
        }
    }

    // ----------------------------------------------------------
    data class UsuarioItem(val id: String, val nome: String)
}
