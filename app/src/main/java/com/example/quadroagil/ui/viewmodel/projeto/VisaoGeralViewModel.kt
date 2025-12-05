package com.example.quadroagil.ui.viewmodel.projeto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadroagil.data.model.ResumoProjeto
import com.example.quadroagil.data.repository.ResumoProjetoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ListenerRegistration

class VisaoGeralViewModel(
    private val idProjeto: String,
    private val repository: ResumoProjetoRepository
) : ViewModel() {

    private val _concluidas = MutableStateFlow(0)
    val concluidas: StateFlow<Int> = _concluidas

    private val _emAndamento = MutableStateFlow(0)
    val emAndamento: StateFlow<Int> = _emAndamento

    private val _naoIniciadas = MutableStateFlow(0)
    val naoIniciadas: StateFlow<Int> = _naoIniciadas

    private var listener: ListenerRegistration? = null

    init {
        iniciarEscuta()
    }

    private fun iniciarEscuta() {
        listener?.remove() // Caso recrie o viewmodel por navegação

        // 🔥 Listener em tempo real
        listener = repository.listenResumoProjeto(idProjeto) { resumo ->
            atualizarValores(resumo)
        }

        // 🔥 Sincronização inicial (garante contagem correta mesmo antes do listener disparar)
        viewModelScope.launch {
            repository.atualizarResumo(idProjeto)
        }
    }

    private fun atualizarValores(resumo: ResumoProjeto) {
        _concluidas.value = resumo.concluidas
        _emAndamento.value = resumo.emAndamento
        _naoIniciadas.value = resumo.naoIniciadas
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
