package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para el dashboard del restaurante
data class RestauranteDashboardUiState(
    val pedidosRecibidos: List<Pedido> = emptyList(),
    val isLoading: Boolean = true
)

class RestauranteDashboardViewModel(
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestauranteDashboardUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPedidosDelRestaurante(restauranteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Obtenemos todos los pedidos y los filtramos por la cédula jurídica del restaurante
            val pedidosDelRestaurante = pedidoRepository.obtenerPedidos()
                .filter { it.restaurante.cedulaJuridica == restauranteId }
            _uiState.update { it.copy(pedidosRecibidos = pedidosDelRestaurante, isLoading = false) }
        }
    }
}