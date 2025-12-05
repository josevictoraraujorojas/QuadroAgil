package com.example.quadroagil.ui.projeto

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.quadroagil.data.repository.ProjetoRepository
import com.example.quadroagil.databinding.FragmentEditarProjetoBinding
import com.example.quadroagil.ui.home.HomeActivity
import com.example.quadroagil.ui.viewmodel.projeto.EditarProjetoViewModel
import com.example.quadroagil.ui.viewmodel.projeto.EditarProjetoViewModelFactory

class EditarProjetoFragment : Fragment() {

    private var _binding: FragmentEditarProjetoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EditarProjetoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditarProjetoBinding.inflate(inflater, container, false)

        val projetoId = arguments?.getString("idProjeto") ?: ""

        val factory = EditarProjetoViewModelFactory(
            ProjetoRepository(),
            projetoId
        )

        viewModel = viewModels<EditarProjetoViewModel> { factory }.value

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.podeEditar.observe(viewLifecycleOwner) { pode ->
            binding.btnSalvar.isEnabled = pode
            binding.inputNome.isEnabled = pode
            binding.inputArea.isEnabled = pode
            binding.inputEmail.isEnabled = pode
        }


        binding.btnSalvar.setOnClickListener {
            val result = viewModel.salvar()

            if (result.isSuccess) {
                Toast.makeText(requireContext(), "Alterações salvas!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSair.setOnClickListener {
            mostrarDialogoSair()
        }
    }

    private fun mostrarDialogoSair() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair do projeto")
            .setMessage("Tem certeza que deseja sair deste projeto?")
            .setPositiveButton("Sim") { _, _ ->

                Toast.makeText(requireContext(), "Você saiu do projeto", Toast.LENGTH_SHORT).show()

                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

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
