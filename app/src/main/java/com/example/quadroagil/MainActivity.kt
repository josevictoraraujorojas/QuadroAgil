package com.example.quadroagil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quadroagil.databinding.ActivityMainBinding
import com.example.quadroagil.ui.cadastro.CadastroActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnEntrar.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()

            if (email.isBlank() || senha.isBlank()) {
                Snackbar.make(binding.root, "Preencha todos os campos", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Login em desenvolvimento", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}