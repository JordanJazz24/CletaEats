package com.una.cletaeats.data.model

open class Usuario(
   open val correo: String,
   open val password: String,
   open val rol: TipoUsuario
)
enum class TipoUsuario {
    CLIENTE, REPARTIDOR, RESTAURANTE, ADMIN
}