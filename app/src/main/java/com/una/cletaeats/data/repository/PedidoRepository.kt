package com.una.cletaeats.data.repository

import android.content.Context
import com.una.cletaeats.data.model.*

class PedidoRepository(private val context: Context) {

    private val pedidoDao = PedidoDao(context)
    private val usuarioRepository = UsuarioRepository(context)

    fun realizarPedido(cliente: Cliente, restaurante: Restaurante, combos: List<Combo>): Pedido? {
        // 1. Asignar el primer repartidor disponible
        val repartidorAsignado = usuarioRepository.obtenerRepartidores()
            .find { it.estado == EstadoRepartidor.DISPONIBLE && it.amonestaciones < 4 }

        if (repartidorAsignado == null) {
            // No hay repartidores disponibles, no se puede realizar el pedido
            return null
        }

        // 2. Crear el nuevo objeto Pedido
        val nuevoPedido = Pedido(
            id = pedidoDao.generarNuevoId(),
            cliente = cliente,
            restaurante = restaurante,
            combos = combos,
            repartidor = repartidorAsignado,
            estado = EstadoPedido.EN_PREPARACION,
            fechaHoraPedido = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        )

        // 3. Guardar el pedido en el archivo
        pedidoDao.agregarPedido(nuevoPedido)

        // 4. Actualizar el estado del repartidor a "Ocupado"
        val repartidorActualizado = repartidorAsignado.copy(estado = EstadoRepartidor.OCUPADO) //
        usuarioRepository.actualizarRepartidor(repartidorActualizado)

        return nuevoPedido
    }

    fun obtenerPedidos(): List<Pedido> {
        return pedidoDao.obtenerTodosLosPedidos()
    }
}