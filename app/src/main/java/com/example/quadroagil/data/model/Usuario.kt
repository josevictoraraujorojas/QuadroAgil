package com.example.quadroagil.data.model

import java.util.Date


data class Usuario(
   val id: String = "",
   val nome: String = "",
   val email: String = "",
   val dataNasc: Date? = null,
   val telefone: String = ""
)

