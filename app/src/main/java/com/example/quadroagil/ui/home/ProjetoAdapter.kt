package com.example.quadroagil.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quadroagil.R

class ProjetoAdapter(
    private val projetos: MutableList<String>,
    private val onRemoveClick: (String) -> Unit,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ProjetoAdapter.ProjetoViewHolder>() {

    inner class ProjetoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeProjeto: TextView = view.findViewById(R.id.tvNomeProjeto)
        val btnExcluir: ImageButton = view.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjetoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_projeto, parent, false)
        return ProjetoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjetoViewHolder, position: Int) {
        val projeto = projetos[position]
        holder.nomeProjeto.text = projeto

        holder.itemView.setOnClickListener { onItemClick(projeto) }
        holder.btnExcluir.setOnClickListener { onRemoveClick(projeto) }
    }

    override fun getItemCount() = projetos.size
}