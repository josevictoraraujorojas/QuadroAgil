package com.example.quadroagil.ui.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.databinding.FragmentProjetoDetalhesBinding
import com.example.quadroagil.ui.adapter.TarefaAdapter
import com.example.quadroagil.ui.viewmodel.projeto.TarefasViewModel
import com.example.quadroagil.ui.viewmodel.projeto.TarefasViewModelFactory

class TarefasFragment : Fragment() {

    private lateinit var binding: FragmentProjetoDetalhesBinding

    // PEGAR idProjeto vindo do fragment anterior
    private lateinit var idProjeto: String

    private val viewModel: TarefasViewModel by viewModels {
        TarefasViewModelFactory(idProjeto)
    }

    private lateinit var adapterAfazer: TarefaAdapter
    private lateinit var adapterFazendo: TarefaAdapter
    private lateinit var adapterFeito: TarefaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // pega idProjeto dos argumentos passados pela Activity
        idProjeto = arguments?.getString("idProjeto") ?: ""
        println("DEBUG: idProjeto=$idProjeto")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjetoDetalhesBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarAdapters()
        observarViewModel()
        configurarBotoes()
    }

    // -----------------------
    // CONFIGURAÇÃO DOS ADAPTERS
    // -----------------------
    private fun configurarAdapters() {
        adapterAfazer = TarefaAdapter(
            onEditar = { abrirEditar(it) },
            onExcluir = { confirmarExcluir(it) }
        )
        adapterFazendo = TarefaAdapter(
            onEditar = { abrirEditar(it) },
            onExcluir = { confirmarExcluir(it) }
        )
        adapterFeito = TarefaAdapter(
            onEditar = { abrirEditar(it) },
            onExcluir = { confirmarExcluir(it) }
        )

        binding.secAfazer.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.secAfazer.recycler.adapter = adapterAfazer

        binding.secFazendo.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.secFazendo.recycler.adapter = adapterFazendo

        binding.secFeito.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.secFeito.recycler.adapter = adapterFeito
    }

    // -----------------------
    // OBSERVERS DO VIEWMODEL
    // -----------------------
    private fun observarViewModel() {
        viewModel.notasAfazer.observe(viewLifecycleOwner) {
            adapterAfazer.submitList(it)
        }
        viewModel.notasFazendo.observe(viewLifecycleOwner) {
            adapterFazendo.submitList(it)
        }
        viewModel.notasFeito.observe(viewLifecycleOwner) {
            adapterFeito.submitList(it)
        }
    }

    // -----------------------
    // BOTÕES "+"
    // -----------------------
    private fun configurarBotoes() {
        binding.secAfazer.txtTitulo.text = "A Fazer"
        binding.secFazendo.txtTitulo.text = "Fazendo"
        binding.secFeito.txtTitulo.text = "Feito"

        binding.secAfazer.btnAdd.setOnClickListener {
            abrirAdicionar(Status.AFAZER)
        }
        binding.secFazendo.btnAdd.setOnClickListener {
            abrirAdicionar(Status.FAZENDO)
        }
        binding.secFeito.btnAdd.setOnClickListener {
            abrirAdicionar(Status.FEITO)
        }
    }

    // -----------------------
    // NAVEGAÇÃO PARA FRAGMENTS
    // -----------------------
    private fun abrirAdicionar(status: Status) {
        val fragment = AdicionarTarefaFragment.newInstance(status, idProjeto)
        abrirFragment(fragment)
    }

    private fun abrirEditar(nota: Nota) {
        val fragment = EditarTarefaFragment.newInstance(nota)
        abrirFragment(fragment)
    }

    private fun abrirFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.quadroagil.R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    // -----------------------
    // EXCLUIR NOTA
    // -----------------------
    private fun confirmarExcluir(nota: Nota) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir tarefa?")
            .setMessage("Deseja excluir '${nota.titulo}'?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.removerNota(nota)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
