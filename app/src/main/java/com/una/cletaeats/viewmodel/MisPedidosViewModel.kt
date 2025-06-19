package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.EstadoPedido
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para la pantalla de "Mis Pedidos"
data class MisPedidosUiState(
    // Separamos los pedidos en dos listas para mostrarlos en secciones distintas
    val pedidosPendientes: List<Pedido> = emptyList(),
    val pedidosCompletados: List<Pedido> = emptyList(),
    val isLoading: Boolean = true
)

class MisPedidosViewModel(
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisPedidosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPedidosDelCliente(clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Obtenemos todos los pedidos y los filtramos por la cédula del cliente
            val todosMisPedidos = pedidoRepository.obtenerPedidos()
                .filter { it.cliente.cedula == clienteId }

            // Usamos partition para dividir la lista en dos: pendientes y completados
            val (pendientes, completados) = todosMisPedidos.partition {
                it.estado == EstadoPedido.EN_PREPARACION || it.estado == EstadoPedido.EN_CAMINO
            }

            _uiState.update {
                it.copy(
                    pedidosPendientes = pendientes.sortedByDescending { p -> p.fechaHoraPedido },
                    pedidosCompletados = completados.sortedByDescending { p -> p.fechaHoraPedido },
                    isLoading = false
                )
            }
        }
    }
}