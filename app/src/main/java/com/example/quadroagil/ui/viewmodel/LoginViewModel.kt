package com.example.quadroagil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError


    fun login(email: String, senha: String) {

        if (email.isBlank() || senha.isBlank()) {
            _loginError.value = "Preencha todos os campos"
            return
        }

        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                _loginSuccess.value = true
            }
            .addOnFailureListener { e ->
                _loginError.value = e.message ?: "Erro ao fazer login"
            }
    }
}