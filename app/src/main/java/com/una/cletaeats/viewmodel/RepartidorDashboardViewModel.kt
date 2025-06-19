package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.EstadoPedido
import com.una.cletaeats.data.model.EstadoRepartidor
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.data.repository.PedidoRepository
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para el dashboard del repartidor
data class RepartidorDashboardUiState(
    val misPedidos: List<Pedido> = emptyList(),
    val isLoading: Boolean = true
)

class RepartidorDashboardViewModel(
    private val pedidoRepository: PedidoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepartidorDashboardUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPedidosDelRepartidor(repartidorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Obtenemos todos los pedidos y los filtramos
            val pedidosDelRepartidor = pedidoRepository.obtenerPedidos()
                .filter { it.repartidor?.cedula == repartidorId }
            _uiState.update { it.copy(misPedidos = pedidosDelRepartidor, isLoading = false) }
        }
    }

    fun actualizarEstadoPedido(pedido: Pedido, nuevoEstado: EstadoPedido) {
        viewModelScope.launch {
            // Creamos una copia del pedido con el nuevo estado
            val pedidoActualizado = pedido.copy(estado = nuevoEstado)

            // SI EL PEDIDO SE ENTREGA...
            if (nuevoEstado == EstadoPedido.ENTREGADO) {
                // 1. Guardamos la hora de entrega en el pedido
                pedidoActualizado.fechaHoraEntrega = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

                // 2. OBTENEMOS AL REPARTIDOR Y LO LIBERAMOS
                val repartidor = pedido.repartidor
                if (repartidor != null) {
                    val repartidorLiberado = repartidor.copy(estado = EstadoRepartidor.DISPONIBLE)
                    // 3. Guardamos los cambios del repartidor usando el UsuarioRepository
                    usuarioRepository.actualizarRepartidor(repartidorLiberado)
                }
            }

            // Siempre guardamos los cambios del pedido
            pedidoRepository.actualizarPedido(pedidoActualizado)

            // Volvemos a cargar la lista de pedidos para refrescar la pantalla
            cargarPedidosDelRepartidor(pedido.repartidor!!.cedula)
        }
    }
}