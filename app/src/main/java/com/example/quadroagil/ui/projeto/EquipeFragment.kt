package com.example.quadroagil.ui.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadroagil.R
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import com.example.quadroagil.databinding.FragmentEquipeBinding
import com.example.quadroagil.ui.view.projeto.AdicionarColaboradorFragment
import com.example.quadroagil.ui.viewmodel.projeto.EquipeViewModel
import com.example.quadroagil.ui.viewmodel.projeto.EquipeViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class EquipeFragment : Fragment() {

    private var _binding: FragmentEquipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EquipeAdapter
    private lateinit var viewModel: EquipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idProjeto = arguments?.getString("idProjeto") ?: ""

        val factory = EquipeViewModelFactory(
            idProjeto,
            ParticipacaoRepository(),
            UsuarioRepository()
        )

        viewModel = ViewModelProvider(this, factory)[EquipeViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Inicializa o adapter
        adapter = EquipeAdapter() { idUsuario ->
            confirmarRemocao(idUsuario)
        }

        // 🚀 AQUI ESTAVA O ERRO
        binding.recyclerEquipe.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEquipe.adapter = adapter

        // Observa lista via Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listaMembros.collect { lista ->
                    adapter.updateList(lista)
                }
            }
        }

        // Botão de adicionar membro
        binding.btnAddMembro.setOnClickListener {
            val fragment = AdicionarColaboradorFragment.newInstance(idProjeto)
            abrirFragment(fragment)
        }
    }

    private fun confirmarRemocao(idUsuario: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar")
            .setMessage("Deseja remover este membro da equipe?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.removerMembro(idUsuario)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
