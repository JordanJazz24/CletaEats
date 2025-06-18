package com.una.cletaeats.data.model

enum class EstadoCliente { ACTIVO, SUSPENDIDO }

data class Cliente(
    val cedula: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val numeroTarjeta: String,
    var estado: EstadoCliente = EstadoCliente.ACTIVO,
    override val correo: String,
    override val password: String
) : Usuario(correo, password, TipoUsuario.CLIENTE)