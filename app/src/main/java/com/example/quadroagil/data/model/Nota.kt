package com.example.quadroagil.data.model

import java.util.Date

data class Nota(
    val id: String = "",
    val idProjeto: String = "",
    val idUsuario: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val dataInicio: Date? = null,
    val dataFim: Date? = null,
    val status: Status = Status.AFAZER
)

