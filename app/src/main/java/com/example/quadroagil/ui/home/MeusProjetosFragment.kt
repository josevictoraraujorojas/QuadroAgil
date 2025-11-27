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
import com.example.quadroagil.databinding.FragmentMeusProjetosBinding
import com.example.quadroagil.ui.projeto.TaskActivity
import com.example.quadroagil.ui.viewmodel.MeusProjetosViewModel

class MeusProjetosFragment : Fragment() {

    private var _binding: FragmentMeusProjetosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProjetoAdapter
    private val viewModel: MeusProjetosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeusProjetosBinding.inflate(inflater, container, false)

        adapter = ProjetoAdapter(
            projetos = mutableListOf(),
            onRemoveClick = { projeto -> mostrarDialogoExcluir(projeto) },
            onItemClick = { projeto -> abrirTaskActivity(projeto) } // agora passa Projeto
        )

        binding.rvMeusProjetos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMeusProjetos.adapter = adapter

        // Botão para criar novo projeto
        binding.btnNovoProjeto.setOnClickListener {
            (activity as? HomeActivity)?.replaceFragment(CriarProjetoFragment())
        }

        observarViewModel()
        viewModel.carregarProjetosDoUsuario()

        return binding.root
    }

    private fun observarViewModel() {
        viewModel.projetos.observe(viewLifecycleOwner) { lista ->
            adapter.atualizarLista(lista)
        }

        viewModel.erro.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirTaskActivity(projeto: Projeto) {
        val intent = Intent(requireContext(), TaskActivity::class.java)
        // envia id e nome do projeto para TaskActivity
        intent.putExtra("projetoId", projeto.id)
        intent.putExtra("projetoNome", projeto.nome)
        startActivity(intent)
    }

    private fun mostrarDialogoExcluir(projeto: Projeto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir projeto")
            .setMessage("Deseja realmente excluir \"${projeto.nome}\"?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.excluirProjeto(projeto.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}