package com.example.quadroagil.ui.projeto

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quadroagil.data.model.Nota
import com.example.quadroagil.databinding.FragmentEditarTarefaBinding
import com.example.quadroagil.model.Tarefa

class EditarTarefaFragment : Fragment() {

    private lateinit var binding: FragmentEditarTarefaBinding
    private var tarefa: Nota? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tarefa = arguments?.getSerializable("nota") as? Nota
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarTarefaBinding.inflate(inflater, container, false)

        configurarDatePickers()
        preencherCampos()

        binding.btnEditar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun preencherCampos() {
        tarefa?.let { t ->
            binding.edtTitulo.setText(t.titulo)
            binding.edtDescricao.setText(t.descricao)
            binding.btnDataInicio.text = t.dataInicio as CharSequence?
            binding.btnDataFim.text = t.dataFim as CharSequence?
            binding.edtResponsavel.setText(t.idUsuario)
        }
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
        fun newInstance(tarefa: Nota): EditarTarefaFragment {
            val f = EditarTarefaFragment()
            val args = Bundle()
            args.putSerializable("tarefa", tarefa)
            f.arguments = args
            return f
        }
    }
}