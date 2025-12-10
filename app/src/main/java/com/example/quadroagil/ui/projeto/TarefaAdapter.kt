package com.example.quadroagil.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.data.repository.NotaRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import com.example.quadroagil.ui.viewmodel.projeto.TarefasViewModel
import com.example.quadroagil.databinding.ItemTarefaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TarefaAdapter(
    private val viewModel: TarefasViewModel,
    private val onEditar: (Nota) -> Unit,
    private val onExcluir: (Nota) -> Unit
) : ListAdapter<Nota, TarefaAdapter.NotaViewHolder>(DiffCallback()) {

    inner class NotaViewHolder(val binding: ItemTarefaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(nota: Nota) {
            binding.nota = nota
            binding.executePendingBindings()

            // --------------------------
            // FORMATAR DATAS
            // --------------------------
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            binding.txtDataInicio.text = "Início: ${nota.dataInicio?.let { sdf.format(it) } ?: "Sem data"}"
            binding.txtDataFim.text = "Fim: ${nota.dataFim?.let { sdf.format(it) } ?: "Sem data"}"

            val podeEditar = viewModel.podeEditarOuRemover()
            val podeAlterarStatus = viewModel.podeAlterarStatus(nota)

            binding.btnEditar.visibility = if (podeEditar) View.VISIBLE else View.GONE
            binding.btnExcluir.visibility = if (podeEditar) View.VISIBLE else View.GONE
            binding.btnEditar.setOnClickListener { onEditar(nota) }
            binding.btnExcluir.setOnClickListener { onExcluir(nota) }

            // --------------------------
            // Configurar spinner de status
            // --------------------------
            val listaStatus = listOf("A Fazer", "Fazendo", "Feito")
            val adapterSpinner = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                listaStatus
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            binding.spinnerStatus.adapter = adapterSpinner
            binding.spinnerStatus.setSelection(
                when (nota.status) {
                    Status.AFAZER -> 0
                    Status.FAZENDO -> 1
                    Status.FEITO -> 2
                }
            )

            binding.spinnerStatus.isEnabled = podeAlterarStatus
            binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val novoStatus = when (position) {
                        0 -> Status.AFAZER
                        1 -> Status.FAZENDO
                        else -> Status.FEITO
                    }
                    if (novoStatus != nota.status && podeAlterarStatus) {
                        CoroutineScope(Dispatchers.IO).launch {
                            NotaRepository().alterarStatusNota(nota.id, novoStatus.name)
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTarefaBinding.inflate(inflater, parent, false)
        return NotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Nota>() {
        override fun areItemsTheSame(old: Nota, new: Nota) = old.id == new.id
        override fun areContentsTheSame(old: Nota, new: Nota) = old == new
    }
}