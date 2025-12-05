package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.*
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.data.repository.NotaRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AdicionarTarefaViewModel(
    val idProjeto: String,
    val status: Status,
    private val notaRepo: NotaRepository = NotaRepository(),
    private val usuarioRepo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    val titulo = MutableLiveData<String>()
    val descricao = MutableLiveData<String>()
    val responsavelSelecionado = MutableLiveData<String>()
    val dataInicio = MutableLiveData<String>()
    val dataFim = MutableLiveData<String>()

    private val _usuariosProjeto = MutableLiveData<List<UsuarioItem>>()
    val usuariosProjeto: LiveData<List<UsuarioItem>> = _usuariosProjeto

    private val _notaSalva = MutableLiveData<Boolean>()
    val notaSalva: LiveData<Boolean> = _notaSalva

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    init {
        carregarUsuariosDoProjeto()
    }

    private fun carregarUsuariosDoProjeto() {
        viewModelScope.launch {
            val usuarios = usuarioRepo.listarUsuariosDoProjetoSimples(idProjeto)
            _usuariosProjeto.postValue(usuarios.map { UsuarioItem(it.id, it.nome) })
        }
    }

    fun salvarNota() {
        val usuario = _usuariosProjeto.value
            ?.firstOrNull { it.nome == responsavelSelecionado.value }
            ?: return

        val dataInicioDate = dataInicio.value?.let { dateFormatter.parse(it) }
        val dataFimDate = dataFim.value?.let { dateFormatter.parse(it) }

        val novaNota = Nota(
            titulo = titulo.value ?: "",
            descricao = descricao.value ?: "",
            idProjeto = idProjeto,
            idUsuario = usuario.id,
            status = status,
            dataInicio = dataInicioDate,
            dataFim = dataFimDate
        )

        viewModelScope.launch {
            val result = notaRepo.cadastrarNota(novaNota)
            _notaSalva.postValue(result.isSuccess)
        }
    }

    data class UsuarioItem(val id: String, val nome: String)
}
