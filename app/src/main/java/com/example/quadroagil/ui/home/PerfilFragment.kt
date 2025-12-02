package com.example.quadroagil.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.databinding.FragmentPerfilBinding
import com.example.quadroagil.ui.viewmodel.PerfilViewModel

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    // Declaração do ViewModel
    private lateinit var viewModel: PerfilViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o ViewModel
        viewModel = ViewModelProvider(this)[PerfilViewModel::class.java]

        // Configura os Observadores
        setupObservers()

        // Configura os Cliques (Tela -> ViewModel)
        setupListeners()

        // Carrega os dados iniciais do usuário
        viewModel.carregarDados()
    }

    private fun setupObservers() {
        // Quando os dados do usuário são carregados do banco
        viewModel.usuarioCarregado.observe(viewLifecycleOwner) { usuario ->
            binding.etNome.setText(usuario.nome)
            binding.etEmail.setText(usuario.email)
            binding.etTelefone.setText(usuario.telefone)

            binding.etSenha.text?.clear()
            binding.etConfirmaSenha.text?.clear()
        }

        // Feedback de Loading
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSalvar.isEnabled = !isLoading
            binding.btnSalvar.text = if (isLoading) "Salvando..." else "Salvar"

            binding.btnSairPerfil.isEnabled = !isLoading
        }

        // Feedback de Erro
        viewModel.erro.observe(viewLifecycleOwner) { mensagem ->
            Toast.makeText(requireContext(), mensagem, Toast.LENGTH_LONG).show()
        }

        // Feedback de Sucesso
        viewModel.sucesso.observe(viewLifecycleOwner) { mensagem ->
            Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
            // Se mudou a senha, limpa os campos de senha
            binding.etSenha.text?.clear()
            binding.etConfirmaSenha.text?.clear()
        }
    }

    private fun setupListeners() {
        // Botão Salvar
        binding.btnSalvar.setOnClickListener {
            // Passa os dados da tela para as variáveis do ViewModel
            viewModel.nome = binding.etNome.text.toString()
            viewModel.email = binding.etEmail.text.toString() // Agora inclui o email
            viewModel.telefone = binding.etTelefone.text.toString()
            viewModel.senha = binding.etSenha.text.toString()
            viewModel.confirmarSenha = binding.etConfirmaSenha.text.toString()

            // Chama a função de salvar no ViewModel
            viewModel.salvarAlteracoes()
            viewModel.carregarDados()
        }

        // Botão Sair
        binding.btnSairPerfil.setOnClickListener {
            (activity as? HomeActivity)?.mostrarDialogoSair()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}