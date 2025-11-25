package com.example.quadroagil.ui.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.quadroagil.R
import com.example.quadroagil.databinding.FragmentEquipeBinding

class EquipeFragment : Fragment() {

    private var _binding: FragmentEquipeBinding? = null
    private val binding get() = _binding!!

    private val membros = mutableListOf(
        "Ellie Jones",
        "Ze do queijo",
        "Joao Pneu",
        "Dany"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        montarLista()

        binding.btnAddMembro.setOnClickListener {
            Toast.makeText(requireContext(), "Adicionar novo membro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun montarLista() {
        val container = binding.containerEquipe
        container.removeAllViews()

        membros.forEach { nome ->
            val itemView = layoutInflater.inflate(R.layout.item_membro, container, false)

            val txtNome = itemView.findViewById<TextView>(R.id.txtNome)
            val btnDelete = itemView.findViewById<ImageView>(R.id.btnDelete)

            txtNome.text = nome

            btnDelete.setOnClickListener {
                mostrarPopupDelete(nome)
            }

            container.addView(itemView)
        }
    }

    private fun mostrarPopupDelete(nome: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar")
            .setMessage("Deseja remover $nome da equipe?")
            .setPositiveButton("Sim") { _, _ ->
                membros.remove(nome)
                montarLista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}