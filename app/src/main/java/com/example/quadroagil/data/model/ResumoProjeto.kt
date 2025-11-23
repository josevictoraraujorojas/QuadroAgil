package com.example.quadroagil.data.model

data class ResumoProjeto(
    val id: String = "",
    val idProjeto: String = "",
    val totalNota: Int = 0,
    val concluidas: Int = 0,
    val emAndamento: Int = 0,
    val naoIniciadas: Int = 0
)
