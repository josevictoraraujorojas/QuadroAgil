package com.example.quadroagil.ui.projeto

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.quadroagil.R
import com.example.quadroagil.databinding.FragmentVisaoGeralBinding
import com.example.quadroagil.ui.viewmodel.projeto.VisaoGeralViewModel
import com.example.quadroagil.ui.viewmodel.projeto.VisaoGeralViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch

class VisaoGeralFragment : Fragment() {

    private var _binding: FragmentVisaoGeralBinding? = null
    private val binding get() = _binding!!

    private lateinit var idProjeto: String

    private val viewModel: VisaoGeralViewModel by viewModels {
        VisaoGeralViewModelFactory(idProjeto)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idProjeto = arguments?.getString("idProjeto")
            ?: throw IllegalArgumentException("idProjeto é obrigatório!")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisaoGeralBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 Como o id já é passado no constructor do ViewModel,
        // ele já inicia o listener automaticamente
        // (não precisa chamar iniciar() mais)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch { viewModel.naoIniciadas.collect { atualizarGraficos() } }
                launch { viewModel.emAndamento.collect { atualizarGraficos() } }
                launch { viewModel.concluidas.collect { atualizarGraficos() } }
            }
        }
    }

    private fun atualizarGraficos() {
        montarLegenda(
            afazer = viewModel.naoIniciadas.value,
            fazendo = viewModel.emAndamento.value,
            feito = viewModel.concluidas.value
        )

        montarPieChart(
            afazer = viewModel.naoIniciadas.value,
            fazendo = viewModel.emAndamento.value,
            feito = viewModel.concluidas.value
        )
    }

    private fun montarLegenda(afazer: Int, fazendo: Int, feito: Int) {
        binding.txtAfazer.text = "A Fazer: $afazer"
        binding.txtFazendo.text = "Fazendo: $fazendo"
        binding.txtFeito.text = "Feito: $feito"
    }

    private fun montarPieChart(afazer: Int, fazendo: Int, feito: Int) {
        val entries = listOf(
            PieEntry(afazer.toFloat(), "A Fazer"),
            PieEntry(fazendo.toFloat(), "Fazendo"),
            PieEntry(feito.toFloat(), "Feito")
        )

        val ds = PieDataSet(entries, "")
        ds.colors = listOf(
            requireContext().getColor(R.color.status_afazer),
            requireContext().getColor(R.color.status_fazendo),
            requireContext().getColor(R.color.status_feito)
        )
        ds.valueTextColor = Color.WHITE
        ds.valueTextSize = 12f

        val data = PieData(ds)

        binding.pieChart.data = data
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setDrawEntryLabels(false)

        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.holeRadius = 60f
        binding.pieChart.transparentCircleRadius = 65f
        binding.pieChart.centerText = "Tarefas"

        binding.pieChart.animateY(900, Easing.EaseInOutQuad)
        binding.pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
