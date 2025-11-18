package com.example.quadroagil.ui.projeto

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quadroagil.databinding.FragmentAdicionarTarefaBinding

class AdicionarTarefaFragment : Fragment() {

    private lateinit var binding: FragmentAdicionarTarefaBinding
    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = arguments?.getString("status")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdicionarTarefaBinding.inflate(inflater, container, false)

        configurarDatePickers()

        binding.btnAdicionar.text = "Adicionar"

        binding.btnAdicionar.setOnClickListener {
            // TODO: salvar tarefa (status já vem no argumento)
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun configurarDatePickers() {
        binding.btnDataInicio.setOnClickListener {
            mostrarDatePicker { data -> binding.btnDataInicio.text = data }
        }

        binding.btnDataFim.setOnClickListener {
            mostrarDatePicker { data -> binding.btnDataFim.text = data }
        }
    }

    private fun mostrarDatePicker(callback: (String) -> Unit) {
        val dp = DatePickerDialog(requireContext())
        dp.setOnDateSetListener { _, y, m, d ->
            callback("%02d/%02d/%d".format(d, m + 1, y))
        }
        dp.show()
    }

    companion object {
        fun newInstance(status: String): AdicionarTarefaFragment {
            val fragment = AdicionarTarefaFragment()
            val args = Bundle()
            args.putString("status", status)
            fragment.arguments = args
            return fragment
        }
    }
}