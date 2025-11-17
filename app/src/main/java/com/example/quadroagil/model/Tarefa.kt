package com.example.quadroagil.model

data class Tarefa(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val dataEntrega: String = "",
    val status: String = "" // "afazer", "fazendo", "feito"
)