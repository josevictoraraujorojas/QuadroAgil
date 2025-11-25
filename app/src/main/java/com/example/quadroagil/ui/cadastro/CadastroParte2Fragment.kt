package com.example.quadroagil.ui.cadastro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quadroagil.MainActivity
import com.example.quadroagil.R
import com.example.quadroagil.ui.viewmodel.CadastroViewModel
import com.google.android.material.textfield.TextInputEditText

class CadastroParte2Fragment : Fragment() {

    private val viewModel: CadastroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cadastro_parte2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTelefone = view.findViewById<TextInputEditText>(R.id.etTelefone)
        val etSenha = view.findViewById<TextInputEditText>(R.id.etSenha)
        val etConfirmarSenha = view.findViewById<TextInputEditText>(R.id.etConfirmarSenha)

        val btnVoltar = view.findViewById<View>(R.id.btnVoltar)
        val btnFinalizar = view.findViewById<View>(R.id.btnFinalizar)


        // Observa erro vindo do ViewModel
        viewModel.erro.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        // Observa sucesso vindo do ViewModel
        viewModel.sucesso.observe(viewLifecycleOwner) { usuario ->
            Toast.makeText(requireContext(), "Cadastro concluído!", Toast.LENGTH_SHORT).show()
            abrirMain()
        }


        // Mesmo botão voltar
        btnVoltar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnFinalizar.setOnClickListener {

            // Guarda os valores no ViewModel
            viewModel.telefone = etTelefone.text.toString()
            viewModel.senha = etSenha.text.toString()

            if (etSenha.text.toString() != etConfirmarSenha.text.toString()) {
                etConfirmarSenha.error = "As senhas não coincidem"
                return@setOnClickListener
            }

            viewModel.cadastrar()
        }
    }


    private fun abrirMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}