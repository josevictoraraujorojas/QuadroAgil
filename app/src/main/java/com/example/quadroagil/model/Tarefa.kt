package com.example.quadroagil.model

import java.io.Serializable

data class Tarefa(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val dataEntrega: String = "",
    val status: String = "",
    val responsavel: String = ""
) : Serializable