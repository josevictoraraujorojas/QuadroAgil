package com.example.quadroagil.ui.projeto

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quadroagil.R
import com.example.quadroagil.databinding.FragmentVisaoGeralBinding
import com.example.quadroagil.model.Tarefa
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class VisaoGeralFragment : Fragment() {

    private var _binding: FragmentVisaoGeralBinding? = null
    private val binding get() = _binding!!

    private val meses = listOf(
        "Janeiro","Fevereiro","Março","Abril","Maio","Junho",
        "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisaoGeralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tarefas = gerarMock()

        montarDonuts(tarefas)
        montarLegenda(tarefas)
        montarPieChart(tarefas)
    }

    private fun gerarMock(): List<Tarefa> {
        val list = mutableListOf<Tarefa>()

        meses.forEachIndexed { i, _ ->
            list.add(Tarefa("id1-$i", "Task", "Desc", "10/${i+1}", "afazer"))
            list.add(Tarefa("id2-$i", "Task", "Desc", "12/${i+1}", "fazendo"))
            list.add(Tarefa("id3-$i", "Task", "Desc", "15/${i+1}", "feito"))
        }

        return list
    }

    private fun montarDonuts(tarefas: List<Tarefa>) {
        val grid = binding.gridMeses
        grid.removeAllViews()

        meses.forEachIndexed { idx, mes ->
            val donut = DonutView(requireContext())
            val tamanho = (resources.displayMetrics.widthPixels / 4) - 20
            donut.layoutParams = ViewGroup.LayoutParams(tamanho, tamanho)

            val monthNum = idx + 1
            val doMes = tarefas.filter { t ->
                t.dataEntrega.split("/").getOrNull(1)?.toIntOrNull() == monthNum
            }

            val total = doMes.size
            val done = doMes.count { it.status == "feito" }

            val percent = if (total == 0) 0f else (done * 100f) / total

            donut.setPercentage(percent)
            donut.setLabel(mes.take(3)) // Jan, Fev, Mar...

            grid.addView(donut)
        }
    }

    private fun montarLegenda(tarefas: List<Tarefa>) {
        val afazer = tarefas.count { it.status == "afazer" }
        val fazendo = tarefas.count { it.status == "fazendo" }
        val feito = tarefas.count { it.status == "feito" }

        binding.txtAfazer.text = "A Fazer: $afazer"
        binding.txtFazendo.text = "Fazendo: $fazendo"
        binding.txtFeito.text = "Feito: $feito"
    }

    private fun montarPieChart(tarefas: List<Tarefa>) {
        val afazer = tarefas.count { it.status == "afazer" }
        val fazendo = tarefas.count { it.status == "fazendo" }
        val feito = tarefas.count { it.status == "feito" }

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
        binding.pieChart.centerText = "Tarefas"

        binding.pieChart.animateY(900, Easing.EaseInOutQuad)
        binding.pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}