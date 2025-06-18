package com.una.cletaeats.data.repository

import android.content.Context
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.model.Usuario

// Ahora es una clase que necesita el contexto de la aplicación
class UsuarioRepository(context: Context) {

    // Instanciamos nuestros DAOs, pasándoles el contexto
    private val clienteDao = ClienteDao(context)
    // private val repartidorDao = RepartidorDao(context)  // <-- Añadiremos estos después
    // private val restauranteDao = RestauranteDao(context)

    // ==================== LÓGICA DE USUARIOS ====================

    // La lógica de login ahora debe leer de TODOS los archivos de usuarios
    fun obtenerUsuario(correo: String, password: String): Usuario? {
        val clientes = clienteDao.obtenerTodosLosClientes()
        // val repartidores = repartidorDao.obtenerTodosLosRepartidores()
        // val restaurantes = restauranteDao.obtenerTodosLosRestaurantes()

        // Buscamos en la lista de clientes
        val usuarioEncontrado = clientes.find { it.correo == correo && it.password == password }
        if (usuarioEncontrado != null) return usuarioEncontrado

        // Repetiríamos la búsqueda para repartidores y restaurantes...

        return null // Si no se encuentra en ninguna lista
    }

    // ==================== LÓGICA DE CLIENTES ====================

    fun agregarCliente(cliente: Cliente) {
        // La responsabilidad de guardar ahora es del DAO
        clienteDao.agregarCliente(cliente)
    }

    fun obtenerClientes(): List<Cliente> {
        return clienteDao.obtenerTodosLosClientes()
    }

    fun existeCliente(cedula: String): Boolean {
        return clienteDao.obtenerTodosLosClientes().any { it.cedula == cedula }
    }
}