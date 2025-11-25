package com.example.quadroagil.ui.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quadroagil.databinding.FragmentProjetoDetalhesBinding
import com.example.quadroagil.model.Tarefa

class TarefasFragment : Fragment() {

    private lateinit var binding: FragmentProjetoDetalhesBinding

    private val tarefasMock = listOf(
        Tarefa("1", "UX/UI", "Refazer tela inicial", "17/02", "afazer"),
        Tarefa("2", "API", "Criar endpoint", "18/02", "fazendo"),
        Tarefa("3", "Correções", "Arrumar login", "21/02", "feito")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjetoDetalhesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarSecoes()
    }

    private fun configurarSecoes() {

        val afazer = tarefasMock.filter { it.status == "afazer" }
        val fazendo = tarefasMock.filter { it.status == "fazendo" }
        val feito = tarefasMock.filter { it.status == "feito" }

        configurarRecycler(binding.secAfazer.recycler, afazer)
        configurarRecycler(binding.secFazendo.recycler, fazendo)
        configurarRecycler(binding.secFeito.recycler, feito)

        // Titulos
        binding.secAfazer.txtTitulo.text = "A Fazer"
        binding.secFazendo.txtTitulo.text = "Fazendo"
        binding.secFeito.txtTitulo.text = "Feito"

        // Eventos do botão +
        binding.secAfazer.btnAdd.setOnClickListener { adicionarTarefa("afazer") }
        binding.secFazendo.btnAdd.setOnClickListener { adicionarTarefa("fazendo") }
        binding.secFeito.btnAdd.setOnClickListener { adicionarTarefa("feito") }
    }

    private fun configurarRecycler(recycler: RecyclerView, lista: List<Tarefa>) {
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = TarefaAdapter(
            lista,
            onEditar = { tarefa ->
                abrirFragment(EditarTarefaFragment.newInstance(tarefa))
            },
            onExcluir = { tarefa ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Excluir tarefa?")
                    .setMessage("Deseja excluir '${tarefa.nome}'?")
                    .setPositiveButton("Sim") { _, _ ->
                        Toast.makeText(requireContext(), "Excluído", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
    }

    private fun adicionarTarefa(status: String) {
        abrirFragment(AdicionarTarefaFragment.newInstance(status))
    }

    private fun abrirFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.quadroagil.R.id.fragmentContainer, fragment) // container da Home
            .addToBackStack(null)
            .commit()
    }
}