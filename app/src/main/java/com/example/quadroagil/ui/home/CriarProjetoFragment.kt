package com.example.quadroagil.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.quadroagil.databinding.FragmentCriarProjetoBinding

class CriarProjetoFragment : Fragment() {

    private var _binding: FragmentCriarProjetoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CriarProjetoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarProjetoBinding.inflate(inflater, container, false)

        binding.btnCriar.setOnClickListener {
            val nome = binding.etNomeProjeto.text.toString()
            val area = binding.etArea.text.toString()
            val email = binding.etEmailProjeto.text.toString()
            val descricao = binding.etDescricaoProjeto.text.toString()

            viewModel.criarProjeto(nome, area, email, descricao)
        }

        observarViewModel()

        return binding.root
    }

    private fun observarViewModel() {
        viewModel.erro.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.sucesso.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Projeto criado com sucesso!", Toast.LENGTH_SHORT).show()
            (activity as? HomeActivity)?.replaceFragment(MeusProjetosFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}