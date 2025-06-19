package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.repository.PedidoRepository
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportesUiState(
    val tituloResultado: String = "Resultados",
    val resultados: List<String> = emptyList(),
    val isLoading: Boolean = false
)

class ReportesViewModel(
    private val repository: UsuarioRepository,
    private val pedidoRepository: PedidoRepository // Añadimos el repositorio de pedidos
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportesUiState())
    val uiState = _uiState.asStateFlow()

    private fun limpiarResultados() {
        _uiState.update { it.copy(isLoading = true, resultados = emptyList(), tituloResultado = "") }
    }

    // --- FUNCIONES PARA CADA REPORTE ---

    fun generarReporteClientesActivos() {
        limpiarResultados()
        viewModelScope.launch {
            val clientes = repository.obtenerClientesActivos()
            _uiState.update { it.copy(isLoading = false, resultados = clientes.map { "Céd: ${it.cedula}, N: ${it.nombre}" }, tituloResultado = "Clientes Activos (${clientes.size})") }
        }
    }

    fun generarReporteClientesSuspendidos() {
        limpiarResultados()
        viewModelScope.launch {
            val clientes = repository.obtenerClientesSuspendidos()
            _uiState.update { it.copy(isLoading = false, resultados = clientes.map { "Céd: ${it.cedula}, N: ${it.nombre}" }, tituloResultado = "Clientes Suspendidos (${clientes.size})") }
        }
    }

    fun generarReporteRepartidoresSinAmonestaciones() {
        limpiarResultados()
        viewModelScope.launch {
            val repartidores = repository.obtenerRepartidoresSinAmonestaciones()
            _uiState.update { it.copy(isLoading = false, resultados = repartidores.map { "Céd: ${it.cedula}, N: ${it.nombre}" }, tituloResultado = "Repartidores Sin Amonestaciones (${repartidores.size})") }
        }
    }

    fun generarReporteRestaurantes() {
        limpiarResultados()
        viewModelScope.launch {
            val restaurantes = repository.obtenerReporteRestaurantes()
            _uiState.update { it.copy(isLoading = false, resultados = restaurantes, tituloResultado = "Listado de Restaurantes (${restaurantes.size})") }
        }
    }

    fun generarReporteRestauranteMasPedidos() {
        limpiarResultados()
        viewModelScope.launch {
            val resultado = pedidoRepository.obtenerReporteRestauranteConMasPedidos()
            _uiState.update { it.copy(isLoading = false, resultados = listOf(resultado), tituloResultado = "Restaurante con Más Pedidos") }
        }
    }

    fun generarReporteMontoVendidoPorRestaurante() {
        limpiarResultados()
        viewModelScope.launch {
            val resultados = pedidoRepository.obtenerReporteMontoVendidoPorRestaurante()
            _uiState.update { it.copy(isLoading = false, resultados = resultados, tituloResultado = "Monto Vendido por Restaurante") }
        }
    }

    fun generarReporteMontoTotalVendido() {
        limpiarResultados()
        viewModelScope.launch {
            val resultado = pedidoRepository.obtenerReporteMontoTotalVendido()
            _uiState.update { it.copy(isLoading = false, resultados = listOf(resultado), tituloResultado = "Monto Total de Ventas") }
        }
    }

    fun generarReporteRestauranteMenosPedidos() {
        limpiarResultados()
        viewModelScope.launch {
            val resultado = pedidoRepository.obtenerReporteRestauranteConMenosPedidos()
            _uiState.update { it.copy(isLoading = false, resultados = listOf(resultado), tituloResultado = "Restaurante con Menos Pedidos") }
        }
    }

    fun generarReporteQuejasPorRepartidor() {
        limpiarResultados()
        viewModelScope.launch {
            val resultados = repository.obtenerReporteQuejasPorRepartidor()
            _uiState.update { it.copy(isLoading = false, resultados = resultados, tituloResultado = "Quejas por Repartidor") }
        }
    }

    fun generarReportePedidosPorCliente() {
        limpiarResultados()
        viewModelScope.launch {
            val resultados = pedidoRepository.obtenerReportePedidosPorCliente()
            _uiState.update { it.copy(isLoading = false, resultados = resultados, tituloResultado = "Pedidos por Cliente") }
        }
    }

    fun generarReporteClienteConMasPedidos() {
        limpiarResultados()
        viewModelScope.launch {
            val resultado = pedidoRepository.obtenerReporteClienteConMasPedidos()
            _uiState.update { it.copy(isLoading = false, resultados = listOf(resultado), tituloResultado = "Cliente con Más Pedidos") }
        }
    }

    fun generarReporteHoraPico() {
        limpiarResultados()
        viewModelScope.launch {
            val resultado = pedidoRepository.obtenerReporteHoraPico()
            _uiState.update { it.copy(isLoading = false, resultados = listOf(resultado), tituloResultado = "Hora Pico de Pedidos") }
        }
    }
}