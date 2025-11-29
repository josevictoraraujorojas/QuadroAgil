package com.example.quadroagil.ui.projeto

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.databinding.FragmentAdicionarTarefaBinding
import com.example.quadroagil.ui.viewmodel.projeto.AdicionarTarefaViewModel
import com.example.quadroagil.ui.viewmodel.projeto.AdicionarTarefaViewModelFactory
import java.util.*

class AdicionarTarefaFragment : Fragment() {

    private lateinit var binding: FragmentAdicionarTarefaBinding
    private lateinit var idProjeto: String
    private lateinit var status: Status
    private lateinit var viewModel: AdicionarTarefaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = arguments?.getSerializable("status") as? Status ?: Status.AFAZER
        idProjeto = arguments?.getString("idProjeto") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdicionarTarefaBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(
            this,
            AdicionarTarefaViewModelFactory(idProjeto, status)
        )[AdicionarTarefaViewModel::class.java]

        binding.viewModel = viewModel

        configurarDatePickers()
        configurarSpinnerResponsavel()
        configurarBotaoAdicionar()
        observarViewModel()

        return binding.root
    }

    private fun configurarDatePickers() {
        binding.btnDataInicio.setOnClickListener {
            mostrarDatePicker { data -> viewModel.dataInicio.value = data }
        }

        binding.btnDataFim.setOnClickListener {
            mostrarDatePicker { data -> viewModel.dataFim.value = data }
        }
    }

    private fun mostrarDatePicker(callback: (String) -> Unit) {
        val hoje = Calendar.getInstance()
        val dp = DatePickerDialog(requireContext(),
            { _, ano, mes, dia ->
                callback("%02d/%02d/%04d".format(dia, mes + 1, ano))
            },
            hoje.get(Calendar.YEAR),
            hoje.get(Calendar.MONTH),
            hoje.get(Calendar.DAY_OF_MONTH)
        )
        dp.show()
    }

    private fun configurarSpinnerResponsavel() {
        viewModel.usuariosProjeto.observe(viewLifecycleOwner) { lista ->
            if (lista.isEmpty()) return@observe

            val nomes = lista.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerResponsavel.adapter = adapter

            binding.spinnerResponsavel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.responsavelSelecionado.value = nomes[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun configurarBotaoAdicionar() {
        binding.btnAdicionar.setOnClickListener {
            viewModel.salvarNota()
        }
    }

    private fun observarViewModel() {
        viewModel.notaSalva.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso == true) parentFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(status: Status, idProjeto: String): AdicionarTarefaFragment {
            val fragment = AdicionarTarefaFragment()
            val args = Bundle()
            args.putSerializable("status", status)
            args.putString("idProjeto", idProjeto)
            fragment.arguments = args
            return fragment
        }
    }
}
