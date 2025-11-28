package com.example.quadroagil.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadroagil.data.model.Projeto
import com.example.quadroagil.databinding.FragmentColaboracoesBinding
import com.example.quadroagil.ui.projeto.TaskActivity
import com.example.quadroagil.ui.viewmodel.ColaboracoesViewModel

class ColaboracoesFragment : Fragment() {

    private var _binding: FragmentColaboracoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProjetoAdapter
    private val viewModel: ColaboracoesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColaboracoesBinding.inflate(inflater, container, false)

        adapter = ProjetoAdapter(
            projetos = mutableListOf(),
            onRemoveClick = { projeto -> mostrarDialogoSair(projeto) },
            onItemClick = { projeto -> abrirTaskActivity(projeto) }
        )

        binding.rvProjetos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProjetos.adapter = adapter

        observarViewModel()
        viewModel.carregarColaboracoes()

        return binding.root
    }

    private fun observarViewModel() {
        viewModel.projetos.observe(viewLifecycleOwner) { lista ->
            adapter.atualizarLista(lista)
        }

        viewModel.erro.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.sucesso.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Você saiu do projeto.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirTaskActivity(projeto: Projeto) {
        val intent = Intent(requireContext(), TaskActivity::class.java)
        intent.putExtra("projetoId", projeto.id)
        intent.putExtra("projetoNome", projeto.nome)
        startActivity(intent)
    }

    private fun mostrarDialogoSair(projeto: Projeto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair do projeto")
            .setMessage("Deseja realmente sair de \"${projeto.nome}\"?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.sairDoProjeto(projeto.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}