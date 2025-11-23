package com.example.quadroagil.data.model

import java.util.Date

data class Projeto(
    val id: String = "",
    val nome: String = "",
    val area: String = "",
    val email: String = "",
    val descricao: String = "",
    val dataCriacao: Date? = null,
    val idResumoProjeto: String = ""
)


