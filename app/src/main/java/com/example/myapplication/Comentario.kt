package com.example.myapplication

data class Comentario(
    val id: String = "",
    val texto: String = "",
    val autorId: String = "",
    val autorNombre: String = "",
    val fecha: Long = System.currentTimeMillis()
)
