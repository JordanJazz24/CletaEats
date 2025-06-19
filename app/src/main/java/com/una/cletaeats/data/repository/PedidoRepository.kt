package com.una.cletaeats.data.repository

import android.content.Context
import com.una.cletaeats.data.model.*

// 1. El constructor ahora recibe el UsuarioRepository
class PedidoRepository(
    private val context: Context,
    private val usuarioRepository: UsuarioRepository
) {

    // 2. El DAO ahora recibe el usuarioRepository que nos pasaron
    private val pedidoDao = PedidoDao(context, usuarioRepository)

    fun realizarPedido(cliente: Cliente, restaurante: Restaurante, combos: List<Combo>,distanciaKm: Int,costoEnvio: Double): Pedido? {
        val repartidorAsignado = usuarioRepository.obtenerRepartidores()
            .find { it.estado == EstadoRepartidor.DISPONIBLE && it.amonestaciones < 4 }

        if (repartidorAsignado == null) {
            return null
        }
        val distanciaSimulada = (1..15).random() // Un valor entre 1.0 y 15.99

        val nuevoPedido = Pedido(
            id = pedidoDao.generarNuevoId(),
            cliente = cliente,
            restaurante = restaurante,
            combos = combos,
            repartidor = repartidorAsignado,
            estado = EstadoPedido.EN_PREPARACION,
            fechaHoraPedido = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
            costoTransporte = costoEnvio // Guardamos el costo de envío
        )

        pedidoDao.agregarPedido(nuevoPedido)

        // 3. Actualizamos el repartidor con su nuevo estado Y la distancia de este pedido
        val repartidorActualizado = repartidorAsignado.copy(
            estado = EstadoRepartidor.OCUPADO,
            distanciaPedido = distanciaSimulada // También guardamos la distancia en su perfil
        )
        usuarioRepository.actualizarRepartidor(repartidorActualizado)

        return nuevoPedido
    }

    fun obtenerPedidos(): List<Pedido> {
        return pedidoDao.obtenerTodosLosPedidos()
    }

    fun actualizarPedido(pedido: Pedido) {
        pedidoDao.actualizarPedido(pedido)
    }


    // ==========================================================
    // ===== LÓGICA DE REPORTES - AÑADE ESTE BLOQUE COMPLETO ====
    // ==========================================================

    /**
     * Reporte i) Restaurante con mayor número de pedidos
     */
    fun obtenerReporteRestauranteConMasPedidos(): String {
        val pedidos = pedidoDao.obtenerTodosLosPedidos()
        if (pedidos.isEmpty()) return "No hay pedidos registrados."

        val restaurante = pedidos.groupBy { it.restaurante.nombre }
            .maxByOrNull { it.value.size }
            ?.key ?: "N/A"

        return "Restaurante con más pedidos: $restaurante"
    }

    /**
     * Reporte j) Monto total vendido por cada restaurante
     */
    fun obtenerReporteMontoVendidoPorRestaurante(): List<String> {
        return pedidoDao.obtenerTodosLosPedidos()
            .groupBy { it.restaurante.nombre }
            .map { (nombre, pedidos) ->
                val totalVendido = pedidos.sumOf { it.total }
                "Restaurante: $nombre - Total Vendido: ₡${String.format("%.2f", totalVendido)}"
            }
    }

    /**
     * Reporte k) Monto total vendido por todos los restaurantes
     */
    fun obtenerReporteMontoTotalVendido(): String {
        val montoTotal = pedidoDao.obtenerTodosLosPedidos().sumOf { it.total }
        return "Monto total vendido por todos los restaurantes: ₡${String.format("%.2f", montoTotal)}"
    }

    /**
     * Reporte l) Restaurante con menor número de pedidos
     */
    fun obtenerReporteRestauranteConMenosPedidos(): String {
        val pedidos = pedidoDao.obtenerTodosLosPedidos()
        if (pedidos.isEmpty()) return "No hay pedidos registrados."

        val restaurante = pedidos.groupBy { it.restaurante.nombre }
            .minByOrNull { it.value.size }
            ?.key ?: "N/A"

        return "Restaurante con menos pedidos: $restaurante"
    }

    /**
     * Reporte n) Listado de Pedidos por cada cliente
     */
    fun obtenerReportePedidosPorCliente(): List<String> {
        return pedidoDao.obtenerTodosLosPedidos()
            .groupBy { it.cliente.nombre }
            .flatMap { (nombreCliente, pedidos) ->
                val listaDePedidos = pedidos.map { "  - Pedido ID: ${it.id} a ${it.restaurante.nombre}" }
                listOf("Cliente: $nombreCliente (${pedidos.size} pedidos)") + listaDePedidos
            }
    }

    /**
     * Reporte o) Información del cliente con mayor número de pedidos
     */
    fun obtenerReporteClienteConMasPedidos(): String {
        val pedidos = pedidoDao.obtenerTodosLosPedidos()
        if (pedidos.isEmpty()) return "No hay pedidos registrados."

        val cliente = pedidos.groupBy { it.cliente }
            .maxByOrNull { it.value.size }
            ?.key

        return if (cliente != null) {
            "Cliente con más pedidos: ${cliente.nombre} (Cédula: ${cliente.cedula})"
        } else {
            "No se pudo determinar el cliente con más pedidos."
        }
    }

    /**
     * Reporte p) Saber la hora en la que se realizaron más pedidos (hora pico)
     */
    fun obtenerReporteHoraPico(): String {
        val pedidos = pedidoDao.obtenerTodosLosPedidos()
        if (pedidos.isEmpty()) return "No hay pedidos para analizar."

        val horaPico = pedidos
            .mapNotNull {
                // Extraemos la hora (HH) del string de fecha "yyyy-MM-dd HH:mm:ss"
                it.fechaHoraPedido.substringAfter(" ").substringBefore(":").toIntOrNull()
            }
            .groupBy { it }
            .maxByOrNull { it.value.size }
            ?.key

        return if (horaPico != null) {
            "La hora pico de pedidos es entre las $horaPico:00 y las ${horaPico + 1}:00."
        } else {
            "No se pudo determinar la hora pico."
        }
    }
}