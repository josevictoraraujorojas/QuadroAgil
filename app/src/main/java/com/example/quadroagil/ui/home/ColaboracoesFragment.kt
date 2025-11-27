package com.example.quadroagil.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadroagil.databinding.FragmentColaboracoesBinding
import com.example.quadroagil.ui.projeto.TaskActivity

class ColaboracoesFragment : Fragment() {

    private var _binding: FragmentColaboracoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProjetoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColaboracoesBinding.inflate(inflater, container, false)

//        adapter = ProjetoAdapter(
//            mutableListOf("Projeto 1", "Projeto 2", "Projeto 3"),
//            onRemoveClick = { projeto ->
//                mostrarDialogoSairProjeto(projeto)
//            },
//            onItemClick = { projeto ->
//                abrirTaskActivity(projeto)
//            }
//        )

        binding.rvProjetos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProjetos.adapter = adapter


        return binding.root
    }

    private fun abrirTaskActivity(projeto: String) {
        val intent = Intent(requireContext(), TaskActivity::class.java)
        intent.putExtra("projetoNome", projeto) // Enviando nome do projeto
        startActivity(intent)
    }

    private fun mostrarDialogoSairProjeto(nome: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair do projeto")
            .setMessage("Deseja realmente sair de \"$nome\"?")
            .setPositiveButton("Sim") { _, _ ->
                // futuramente remover do Firestore
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}