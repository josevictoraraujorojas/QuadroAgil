package com.example.quadroagil.ui.projeto

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.data.model.Status
import com.example.quadroagil.databinding.FragmentEditarTarefaBinding
import com.example.quadroagil.ui.viewmodel.projeto.EditarTarefaViewModel
import com.example.quadroagil.ui.viewmodel.projeto.EditarTarefaViewModelFactory

class EditarTarefaFragment : Fragment() {

    private lateinit var binding: FragmentEditarTarefaBinding

    private val viewModel: EditarTarefaViewModel by viewModels {
        EditarTarefaViewModelFactory(
            idProjeto = requireArguments().getString("idProjeto")!!,
            idNota = requireArguments().getString("idNota")!!,
            status = requireArguments().getSerializable("status") as Status
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarTarefaBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbarTask)

        activity.supportActionBar?.apply {
            title = "Editar Tarefa"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbarTask.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        configurarSpinner()
        configurarDatePickers()
        observarAtualizacao()

        return binding.root
    }

    private fun configurarSpinner() {

        viewModel.usuariosProjeto.observe(viewLifecycleOwner) { lista ->

            val nomes = lista.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerResponsavel.adapter = adapter

            val idResp = viewModel.responsavelSelecionado.value
            val index = lista.indexOfFirst { it.id == idResp }

            if (index >= 0)
                binding.spinnerResponsavel.setSelection(index)

            binding.spinnerResponsavel.onItemSelectedListener =
                object : android.widget.AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: android.widget.AdapterView<*>?,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        viewModel.responsavelSelecionado.value = lista[pos].id
                    }

                    override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
                }
        }
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
        val dp = DatePickerDialog(requireContext())
        dp.setOnDateSetListener { _, y, m, d ->
            callback("%02d/%02d/%04d".format(d, m + 1, y))
        }
        dp.show()
    }

    private fun observarAtualizacao() {

        binding.btnEditar.setOnClickListener {
            viewModel.titulo.value = binding.edtTitulo.text.toString()
            viewModel.descricao.value = binding.edtDescricao.text.toString()

            viewModel.atualizarNota()
        }

        viewModel.notaAtualizada.observe(viewLifecycleOwner) { ok ->
            if (ok) parentFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(nota: Nota): EditarTarefaFragment {
            val f = EditarTarefaFragment()
            val args = Bundle()

            args.putString("idNota", nota.id)
            args.putString("idProjeto", nota.idProjeto)
            args.putSerializable("status", nota.status)

            f.arguments = args
            return f
        }
    }
}
