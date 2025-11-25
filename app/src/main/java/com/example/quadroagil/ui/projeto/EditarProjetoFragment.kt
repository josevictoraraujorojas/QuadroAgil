package com.example.quadroagil.ui.projeto

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.quadroagil.databinding.FragmentEditarProjetoBinding
import com.example.quadroagil.ui.home.HomeActivity

class EditarProjetoFragment : Fragment() {

    private var _binding: FragmentEditarProjetoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarProjetoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preencherCampos()

        binding.btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }

        binding.btnSair.setOnClickListener {
            mostrarDialogoSair()
        }
    }

    private fun preencherCampos() {
        // MOCK — depois você puxa do Firestore
        binding.inputNome.setText("Tech")
        binding.inputArea.setText("TI")
        binding.inputEmail.setText("tech@gmail.com")
    }

    private fun salvarAlteracoes() {
        val nome = binding.inputNome.text.toString().trim()
        val area = binding.inputArea.text.toString().trim()
        val email = binding.inputEmail.text.toString().trim()

        if (nome.isEmpty() || area.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "Alterações salvas!", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoSair() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair do projeto")
            .setMessage("Tem certeza que deseja sair deste projeto?")
            .setPositiveButton("Sim") { _, _ ->

                Toast.makeText(requireContext(), "Você saiu do projeto", Toast.LENGTH_SHORT).show()

                // Volta para a HomeActivity
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                // Garante que a activity atual seja fechada
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}