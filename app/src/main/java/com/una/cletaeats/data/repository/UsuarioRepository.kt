package com.una.cletaeats.data.repository

import android.content.Context
import android.util.Log
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.model.Repartidor
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.model.TipoUsuario
import com.una.cletaeats.data.model.Usuario

// Ahora es una clase que necesita el contexto de la aplicación
class UsuarioRepository(context: Context) {

    // Instanciamos nuestros DAOs, pasándoles el contexto
    private val clienteDao = ClienteDao(context)
    private val repartidorDao = RepartidorDao(context)
    private val restauranteDao = RestauranteDao(context)

    // ==================== LÓGICA DE USUARIOS ====================

    // La lógica de login ahora debe leer de TODOS los archivos de usuarios
    fun obtenerUsuario(correo: String, password: String): Usuario? {
        if (correo == "admin@cletaeats.com" && password == "admin123") {
            // Si las credenciales coinciden, creamos un objeto Usuario de tipo ADMIN
            return Usuario(correo, password, TipoUsuario.ADMIN)
        }
        // Buscamos en la lista de clientes
        val clienteEncontrado = clienteDao.obtenerTodosLosClientes()
            .find { it.correo == correo && it.password == password }
        if (clienteEncontrado != null) return clienteEncontrado

        // AHORA BUSCAMOS TAMBIÉN EN LA LISTA DE REPARTIDORES
        val repartidorEncontrado = repartidorDao.obtenerTodosLosRepartidores()
            .find { it.correo == correo && it.password == password }
        if (repartidorEncontrado != null) return repartidorEncontrado

        // AHORA BUSCAMOS TAMBIÉN EN LA LISTA DE RESTAURANTES
        val restauranteEncontrado = restauranteDao.obtenerTodosLosRestaurantes()
            .find { it.correo == correo && it.password == password }
        if (restauranteEncontrado != null) return restauranteEncontrado

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

    // ==================== LÓGICA DE REPARTIDORES ====================

    fun agregarRepartidor(repartidor: Repartidor) {
        // La responsabilidad de guardar ahora es del DAO
        Log.d("CletaEatsDebug", "Repositorio está llamando al RepartidorDao.") // <-- AÑADE ESTA LÍNEA
        repartidorDao.agregarRepartidor(repartidor)
    }

    fun obtenerRepartidores(): List<Repartidor> {
        return repartidorDao.obtenerTodosLosRepartidores()
    }

    fun existeRepartidor(cedula: String): Boolean {
        // Nota: El documento del proyecto pide no repetir cédulas para repartidores
        return repartidorDao.obtenerTodosLosRepartidores().any { it.cedula == cedula }
    }

    fun actualizarRepartidor(repartidor: Repartidor) {
        repartidorDao.actualizarRepartidor(repartidor)
    }

    // ==================== LÓGICA DE RESTAURANTES ====================

    fun agregarRestaurante(restaurante: Restaurante) {
        restauranteDao.agregarRestaurante(restaurante)
    }

    fun obtenerRestaurantes(): List<Restaurante> {
        val lista = restauranteDao.obtenerTodosLosRestaurantes()
        Log.d("HomeScreenDebug", "Repositorio está devolviendo una lista con ${lista.size} restaurantes.")
        return restauranteDao.obtenerTodosLosRestaurantes()
    }

    fun existeRestaurante(cedulaJuridica: String): Boolean {
        // El documento pide no repetir cédulas jurídicas
        return restauranteDao.obtenerTodosLosRestaurantes().any { it.cedulaJuridica == cedulaJuridica }
    }

    fun actualizarRestaurante(restaurante: Restaurante) {
        restauranteDao.actualizarRestaurante(restaurante)
    }
    // ==========================================================
    // ===== LÓGICA DE REPORTES ====
    // ==========================================================

    /**
     * Reporte e) Listado de Clientes: id, cédula, nombre, solo estado activo
     */
    fun obtenerClientesActivos(): List<Cliente> {
        return clienteDao.obtenerTodosLosClientes().filter { it.estado == com.una.cletaeats.data.model.EstadoCliente.ACTIVO }
    }

    /**
     * Reporte f) Listado de Clientes: id, cédula, nombre, solo estado suspendido
     */
    fun obtenerClientesSuspendidos(): List<Cliente> {
        return clienteDao.obtenerTodosLosClientes().filter { it.estado == com.una.cletaeats.data.model.EstadoCliente.SUSPENDIDO }
    }

    /**
     * Reporte g) Listado de Repartidores: id, cédula, nombre, con cero amonestaciones
     */
    fun obtenerRepartidoresSinAmonestaciones(): List<Repartidor> {
        return repartidorDao.obtenerTodosLosRepartidores().filter { it.amonestaciones == 0 }
    }
    /**
     * Reporte h) Listado de Restaurantes: nombre, cedula jurídica, dirección, tipo de comida
     */
    fun obtenerReporteRestaurantes(): List<String> {
        return restauranteDao.obtenerTodosLosRestaurantes().map {
            "Restaurante: ${it.nombre} (C.J. ${it.cedulaJuridica}) - Tipo: ${it.tipoComida} - Dir: ${it.direccion}"
        }
    }

    /**
     * Reporte m) Listado de quejas por cada repartidor
     */
    fun obtenerReporteQuejasPorRepartidor(): List<String> {
        return repartidorDao.obtenerTodosLosRepartidores().map { repartidor ->
            val quejasStr = if (repartidor.quejas.isEmpty()) "Sin quejas." else repartidor.quejas.joinToString(" | ")
            "Repartidor: ${repartidor.nombre} (Cédula: ${repartidor.cedula}) - Quejas: [${quejasStr}]"
        }
    }
}