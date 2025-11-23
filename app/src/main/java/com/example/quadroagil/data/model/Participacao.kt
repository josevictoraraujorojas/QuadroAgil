package com.example.quadroagil.data.model

data class Participacao(
    val id: String = "",
    val papel: Papel = Papel.COLABORADOR,
    val idUsuario: String = "",
    val idProjeto: String = ""
)

