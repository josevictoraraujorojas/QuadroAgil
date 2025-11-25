package com.example.quadroagil.ui.cadastro

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quadroagil.MainActivity
import com.example.quadroagil.R
import com.example.quadroagil.ui.viewmodel.CadastroViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CadastroParte1Fragment : Fragment() {

    private val viewModel: CadastroViewModel by activityViewModels()
    private var dataSelecionada: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cadastro_parte1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnVoltar = view.findViewById<View>(R.id.btnVoltar)
        val btnProximo = view.findViewById<View>(R.id.btnProximo)
        val etDataNascimento = view.findViewById<TextInputEditText>(R.id.etDataNascimento)
        val etNomeCompleto = view.findViewById<TextInputEditText>(R.id.etNomeCompleto)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)

        etDataNascimento.setOnClickListener {
            showDatePicker(etDataNascimento)
        }

        btnProximo.setOnClickListener {

            viewModel.nome = etNomeCompleto.text.toString()
            viewModel.email = etEmail.text.toString()
            viewModel.dataNasc = dataSelecionada

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerCadastro, CadastroParte2Fragment())
                .addToBackStack(null)
                .commit()
        }

        btnVoltar.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
    private fun showDatePicker(et: TextInputEditText) {
        val calendar = Calendar.getInstance()

        val text = et.text.toString()

        if (text.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                calendar.time = sdf.parse(text)!!
            } catch (_: Exception) {}
        }

        val ano = calendar.get(Calendar.YEAR)
        val mes = calendar.get(Calendar.MONTH)
        val dia = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->

                val selected = Calendar.getInstance()
                selected.set(year, month, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                et.setText(sdf.format(selected.time))

                dataSelecionada = selected.time     // <<<< DATA REAL (Date)

            }, ano, mes, dia
        )

        datePicker.show()
    }
}