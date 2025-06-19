package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.EstadoRepartidor
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.data.repository.PedidoRepository
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado para manejar el resultado de la calificación
data class CalificacionUiState(
    val calificacionExitosa: Boolean = false
)

class CalificacionViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalificacionUiState())
    val uiState = _uiState.asStateFlow()

    fun procesarCalificacionNegativa(pedido: Pedido, queja: String) {
        viewModelScope.launch {
            val repartidor = pedido.repartidor ?: return@launch // Salir si no hay repartidor

            // Añadimos la nueva queja a la lista
            repartidor.quejas.add(queja)
            var amonestaciones = repartidor.amonestaciones
            var estadoActual = repartidor.estado

            // Lógica: cada 3 quejas, se suma una amonestación
            // Usamos el módulo (%) para ver si es el 3er, 6to, 9no... reporte
            if (repartidor.quejas.size > 0 && repartidor.quejas.size % 3 == 0) {
                amonestaciones++
            }

            // Lógica: Con 4 amonestaciones, queda fuera.
            // Lo marcaremos como OCUPADO permanentemente para simularlo.
            if (amonestaciones >= 4) {
                estadoActual = EstadoRepartidor.OCUPADO
            }

            // Creamos el objeto repartidor actualizado
            val repartidorActualizado = repartidor.copy(
                amonestaciones = amonestaciones,
                estado = estadoActual
                // La lista de quejas ya fue modificada directamente
            )
            usuarioRepository.actualizarRepartidor(repartidorActualizado)

            // Marcamos el pedido como calificado para que no se pueda volver a calificar
            marcarPedidoComoCalificado(pedido)

            _uiState.update { it.copy(calificacionExitosa = true) }
        }
    }

    fun procesarCalificacionPositiva(pedido: Pedido) {
        viewModelScope.launch {
            // Si la calificación es positiva, solo marcamos el pedido como calificado
            marcarPedidoComoCalificado(pedido)
            _uiState.update { it.copy(calificacionExitosa = true) }
        }
    }

    private fun marcarPedidoComoCalificado(pedido: Pedido) {
        val pedidoActualizado = pedido.copy(calificado = true)
        pedidoRepository.actualizarPedido(pedidoActualizado)
    }

    fun resetearEstado() {
        _uiState.update { it.copy(calificacionExitosa = false) }
    }
}