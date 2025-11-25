package com.example.quadroagil.ui.projeto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.quadroagil.R
import com.example.quadroagil.model.Tarefa

class TarefaAdapter(
    private val lista: List<Tarefa>,
    private val onEditar: (Tarefa) -> Unit,
    private val onExcluir: (Tarefa) -> Unit
) : RecyclerView.Adapter<TarefaAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.findViewById<TextView>(R.id.txtNome)
        val descricao = view.findViewById<TextView>(R.id.txtDescricao)
        val data = view.findViewById<TextView>(R.id.txtData)
        val btnEditar = view.findViewById<ImageButton>(R.id.btnEditar)
        val btnExcluir = view.findViewById<ImageButton>(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarefa, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = lista[position]

        holder.nome.text = t.nome
        holder.descricao.text = t.descricao
        holder.data.text = t.dataEntrega

        holder.btnEditar.setOnClickListener { onEditar(t) }
        holder.btnExcluir.setOnClickListener { onExcluir(t) }
    }
}

