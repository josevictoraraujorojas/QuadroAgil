package com.example.quadroagil.ui.cadastro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quadroagil.MainActivity
import com.example.quadroagil.R

class CadastroParte1Fragment : Fragment() {

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

        btnProximo.setOnClickListener {
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
}
