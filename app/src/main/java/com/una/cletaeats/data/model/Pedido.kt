// Clase Pedido
package com.una.cletaeats.data.model

data class Pedido(
    val id: String,
    val cliente: Cliente,
    val restaurante: Restaurante,
    val combos: List<Combo>,
    val repartidor: Repartidor?,
    val estado: EstadoPedido,
    val fechaHoraPedido: String,
    var fechaHoraEntrega: String? = null
) {
    val subtotal: Double get() = combos.sumOf { it.precio }
    val costoTransporte: Int get() = calcularCostoTransporte()
    val iva: Double get() = subtotal * 0.13
    val total: Double get() = subtotal + iva + costoTransporte

    private fun calcularCostoTransporte(): Int {
        return repartidor?.let {
            val tarifa = if (esFeriado()) 1500 else 1000
            (it.distanciaPedido * tarifa).toInt()
        } ?: 0
    }

    private fun esFeriado(): Boolean {
        // TODO: implementar verificación real
        return false
    }
}

enum class EstadoPedido {
    EN_PREPARACION,
    EN_CAMINO,
    ENTREGADO,
    SUSPENDIDO
}
