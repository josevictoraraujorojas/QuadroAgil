package com.example.quadroagil.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.databinding.ItemTarefaBinding

class TarefaAdapter(
    private val onEditar: (Nota) -> Unit,
    private val onExcluir: (Nota) -> Unit
) : ListAdapter<Nota, TarefaAdapter.NotaViewHolder>(DiffCallback()) {

    inner class NotaViewHolder(val binding: ItemTarefaBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(nota: Nota) {
            binding.nota = nota
            binding.onEditar = onEditar
            binding.onExcluir = onExcluir
            binding.executePendingBindings()
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
