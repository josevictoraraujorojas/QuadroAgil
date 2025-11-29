package com.example.quadroagil.ui.projeto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quadroagil.R
import com.example.quadroagil.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val nomeProjeto = intent.getStringExtra("projetoNome")
        binding.toolbarTask.title = nomeProjeto ?: "Projeto"
        setSupportActionBar(binding.toolbarTask)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarTask.setNavigationOnClickListener { finish() }


        replaceFragment(TarefasFragment())


        binding.bottomNavigationTask.selectedItemId = R.id.nav_tarefas


        binding.bottomNavigationTask.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_visao_geral -> replaceFragment(VisaoGeralFragment())
                R.id.nav_tarefas -> replaceFragment(TarefasFragment())
                R.id.nav_equipe -> replaceFragment(EquipeFragment())
                R.id.nav_editar -> replaceFragment(EditarProjetoFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}