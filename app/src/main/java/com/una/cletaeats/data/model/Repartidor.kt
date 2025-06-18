package com.una.cletaeats.data.model

enum class EstadoRepartidor { DISPONIBLE, OCUPADO }

data class Repartidor(
    val cedula: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val numeroTarjeta: String,
    var estado: EstadoRepartidor = EstadoRepartidor.DISPONIBLE,
    var distanciaPedido: Double = 0.0,
    var kmRecorridosDiarios: Double = 0.0,
    var amonestaciones: Int = 0,
    val costoKmHabiles: Int = 1000,
    val costoKmFeriados: Int = 1500,
    val quejas: MutableList<String> = mutableListOf(),
    override val correo: String,
    override val password: String
) : Usuario(correo, password, TipoUsuario.REPARTIDOR)