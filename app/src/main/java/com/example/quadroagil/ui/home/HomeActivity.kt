package com.example.quadroagil.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quadroagil.MainActivity
import com.example.quadroagil.R
import com.example.quadroagil.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicia com "Meus Projetos"
        replaceFragment(MeusProjetosFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_colaboracoes -> replaceFragment(ColaboracoesFragment())
                R.id.nav_meus_projetos -> replaceFragment(MeusProjetosFragment())
                R.id.nav_sair -> mostrarDialogoSair()
            }
            true
        }
    }

    private fun deslogar() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        // Volta para a tela inicial (MainActivity)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }

    private fun mostrarDialogoSair() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Deseja realmente sair?")
            .setPositiveButton("Sim") { _, _ ->
                deslogar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}