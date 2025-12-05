package com.example.quadroagil.data.model

data class Participacao(
    var id: String = "",
    var papel: Papel = Papel.COLABORADOR,
    var idUsuario: String = "",
    var idProjeto: String = ""
)

