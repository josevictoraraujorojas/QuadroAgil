package com.example.quadroagil.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadroagil.databinding.FragmentMeusProjetosBinding
import com.example.quadroagil.ui.projeto.TaskActivity

class MeusProjetosFragment : Fragment() {

    private var _binding: FragmentMeusProjetosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProjetoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeusProjetosBinding.inflate(inflater, container, false)

        val listaProjetos = mutableListOf("Projeto 1", "Projeto 2", "Projeto 3")

        adapter = ProjetoAdapter(
            projetos = listaProjetos,
            onRemoveClick = { projeto -> mostrarDialogoExcluir(projeto) },
            onItemClick = { projeto ->
                abrirTaskActivity(projeto)
            }
        )

        binding.rvMeusProjetos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMeusProjetos.adapter = adapter

        binding.btnNovoProjeto.setOnClickListener {
            (activity as? HomeActivity)?.replaceFragment(CriarProjetoFragment())
        }
        return binding.root
    }

    private fun abrirTaskActivity(projeto: String) {
        val intent = Intent(requireContext(), TaskActivity::class.java)
        intent.putExtra("projetoNome", projeto) // Enviando nome do projeto
        startActivity(intent)
    }

    private fun mostrarDialogoExcluir(projeto: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir projeto")
            .setMessage("Deseja realmente excluir \"$projeto\"?")
            .setPositiveButton("Sim") { _, _ ->
                // Lógica de exclusão (por enquanto apenas visual)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}