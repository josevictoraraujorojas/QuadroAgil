package com.example.quadroagil.ui.projeto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quadroagil.R

class EquipeAdapter(
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<EquipeAdapter.ViewHolder>() {

    private var membros: List<Pair<String, String>> = emptyList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNome = view.findViewById<TextView>(R.id.txtNome)
        val btnDelete = view.findViewById<ImageView>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membro, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount() = membros.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (nome, idUsuario) = membros[position]
        holder.txtNome.text = nome
        holder.btnDelete.setOnClickListener {
            onDeleteClick(idUsuario)
        }
    }

    fun updateList(newList: List<Pair<String, String>>) {
        membros = newList
        notifyDataSetChanged()
    }
}
