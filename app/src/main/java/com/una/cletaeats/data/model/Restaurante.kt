package com.una.cletaeats.data.model

enum class TipoComida { ITALIANA, CHINA, MEXICANA, RAPIDA, TRADICIONAL }

data class Restaurante(
    val cedulaJuridica: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val tipoComida: TipoComida,
    var calificacionPromedio: Double = 0.0,
    var cantidadPedidos: Int = 0,
    override val correo: String,
    override val password: String
) : Usuario(correo, password, TipoUsuario.RESTAURANTE)