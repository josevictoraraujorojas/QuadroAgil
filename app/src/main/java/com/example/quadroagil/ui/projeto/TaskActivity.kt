package com.example.quadroagil.ui.projeto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quadroagil.R
import com.example.quadroagil.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding

    // Id e nome do projeto vindo do Intent
    private lateinit var idProjeto: String
    private lateinit var nomeProjeto: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebe os dados do projeto
        idProjeto = intent.getStringExtra("projetoId") ?: ""
        nomeProjeto = intent.getStringExtra("projetoNome") ?: "Projeto"

        // Fragment inicial: Tarefas
        replaceFragment(TarefasFragment().apply {
            arguments = Bundle().apply { putString("idProjeto", idProjeto) }
        })

        // Configura BottomNavigation
        binding.bottomNavigationTask.selectedItemId = R.id.nav_tarefas
        binding.bottomNavigationTask.setOnItemSelectedListener { item ->
            val fragment: Fragment? = when (item.itemId) {
                R.id.nav_visao_geral -> VisaoGeralFragment().apply {
                    arguments = Bundle().apply { putString("idProjeto", idProjeto) }
                }
                R.id.nav_tarefas -> TarefasFragment().apply {
                    arguments = Bundle().apply { putString("idProjeto", idProjeto) }
                }
                R.id.nav_equipe -> EquipeFragment().apply {
                    arguments = Bundle().apply { putString("idProjeto", idProjeto) }
                }
                R.id.nav_editar -> EditarProjetoFragment().apply {
                    arguments = Bundle().apply { putString("idProjeto", idProjeto) }
                }
                else -> null
            }
            fragment?.let { replaceFragment(it) }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
    }
}
