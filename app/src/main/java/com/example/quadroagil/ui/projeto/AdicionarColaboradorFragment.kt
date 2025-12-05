package com.example.quadroagil.ui.view.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.data.repository.ParticipacaoRepository
import com.example.quadroagil.data.repository.UsuarioRepository
import com.example.quadroagil.databinding.FragmentAdicionarColaboradorBinding
import com.example.quadroagil.ui.projeto.AdicionarTarefaFragment
import com.example.quadroagil.ui.viewmodel.projeto.AdicionarColaboradorViewModel
import com.example.quadroagil.ui.viewmodel.projeto.AdicionarColaboradorViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class AdicionarColaboradorFragment : Fragment() {

    private var _binding: FragmentAdicionarColaboradorBinding? = null
    private val binding get() = _binding!!

    private lateinit var idProjeto: String

    private val viewModel: AdicionarColaboradorViewModel by viewModels {
        AdicionarColaboradorViewModelFactory(
            idProjeto = idProjeto,
            participacaoRepository = ParticipacaoRepository(),
            usuarioRepository = UsuarioRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        idProjeto = arguments?.getString("idProjeto")
            ?: throw IllegalArgumentException("idProjeto é obrigatório")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdicionarColaboradorBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbarTask)

        activity.supportActionBar?.apply {
            title = "Adicionar Colaborador"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbarTask.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clique no botão usando Binding
        binding.btnAdicionar.setOnClickListener {
            viewModel.adicionarColaborador()
        }

        // Observa mensagens do ViewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.mensagem.collectLatest { msg ->
                msg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance( idProjeto: String): AdicionarColaboradorFragment {
            val fragment = AdicionarColaboradorFragment()
            val args = Bundle()
            args.putString("idProjeto", idProjeto)
            fragment.arguments = args
            return fragment
        }
    }
}
