package com.example.quadroagil.ui.cadastro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quadroagil.MainActivity
import com.example.quadroagil.R

class CadastroParte2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cadastro_parte2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnVoltar = view.findViewById<View>(R.id.btnVoltar)
        val btnFinalizar = view.findViewById<View>(R.id.btnFinalizar)

        btnVoltar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnFinalizar.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}