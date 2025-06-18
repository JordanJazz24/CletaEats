package com.una.cletaeats.data.repository

import com.una.cletaeats.data.model.*

object UsuarioRepository {
    private val usuarios: MutableList<Usuario> = mutableListOf()

    // ==================== COMÚN ====================
    fun agregarUsuario(usuario: Usuario) {
        if (!existeUsuario(usuario.correo)) {
            usuarios.add(usuario)
        }
    }

    fun existeUsuario(correo: String): Boolean {
        return usuarios.any { it.correo == correo }
    }

    fun obtenerUsuario(correo: String, password: String): Usuario? {
        return usuarios.find { it.correo == correo && it.password == password }
    }

    fun eliminarUsuario(correo: String): Boolean {
        return usuarios.removeIf { it.correo == correo }
    }

    fun actualizarUsuario(usuarioActualizado: Usuario): Boolean {
        val index = usuarios.indexOfFirst { it.correo == usuarioActualizado.correo }
        return if (index != -1) {
            usuarios[index] = usuarioActualizado
            true
        } else {
            false
        }
    }

    // ==================== CLIENTES ====================
    fun agregarCliente(cliente: Cliente) {
        agregarUsuario(cliente)
    }

    fun obtenerClientes(): List<Cliente> {
        return usuarios.filterIsInstance<Cliente>()
    }

    fun obtenerClientePorCorreo(correo: String): Cliente? {
        return usuarios.filterIsInstance<Cliente>().find { it.correo == correo }
    }

    fun actualizarCliente(cliente: Cliente): Boolean {
        return actualizarUsuario(cliente)
    }

    fun eliminarCliente(correo: String): Boolean {
        return usuarios.removeIf { it is Cliente && it.correo == correo }
    }

    // ==================== REPARTIDORES ====================
    fun agregarRepartidor(repartidor: Repartidor) {
        agregarUsuario(repartidor)
    }

    fun obtenerRepartidores(): List<Repartidor> {
        return usuarios.filterIsInstance<Repartidor>()
    }

    fun obtenerRepartidorPorCorreo(correo: String): Repartidor? {
        return usuarios.filterIsInstance<Repartidor>().find { it.correo == correo }
    }

    fun actualizarRepartidor(repartidor: Repartidor): Boolean {
        return actualizarUsuario(repartidor)
    }

    fun eliminarRepartidor(correo: String): Boolean {
        return usuarios.removeIf { it is Repartidor && it.correo == correo }
    }

    // ==================== RESTAURANTES ====================
    fun agregarRestaurante(restaurante: Restaurante) {
        agregarUsuario(restaurante)
    }

    fun obtenerRestaurantes(): List<Restaurante> {
        return usuarios.filterIsInstance<Restaurante>()
    }

    fun obtenerRestaurantePorCorreo(correo: String): Restaurante? {
        return usuarios.filterIsInstance<Restaurante>().find { it.correo == correo }
    }

    fun actualizarRestaurante(restaurante: Restaurante): Boolean {
        return actualizarUsuario(restaurante)
    }

    fun eliminarRestaurante(correo: String): Boolean {
        return usuarios.removeIf { it is Restaurante && it.correo == correo }
    }
}
