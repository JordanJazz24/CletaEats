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
    var fechaHoraEntrega: String? = null,
    var calificado: Boolean = false,
    val costoTransporte: Double,
) {
    val subtotal: Double get() = combos.sumOf { it.precio }
    val iva: Double get() = subtotal * 0.13
    val total: Double get() = subtotal + iva + costoTransporte

    private fun calcularCostoTransporte(): Int {
        return repartidor?.let {
            val tarifa = if (esFeriado()) 1500 else 1000
            (it.distanciaPedido * tarifa).toInt()
        } ?: 0
    }

    private fun esFeriado(): Boolean {
        // Simulación simple: consideraremos Sábado y Domingo como "feriados" para la tarifa
        val calendario = java.util.Calendar.getInstance()
        val diaDeLaSemana = calendario.get(java.util.Calendar.DAY_OF_WEEK)
        return diaDeLaSemana == java.util.Calendar.SATURDAY || diaDeLaSemana == java.util.Calendar.SUNDAY
    }
}

enum class EstadoPedido {
    EN_PREPARACION,
    EN_CAMINO,
    ENTREGADO,
    SUSPENDIDO
}
