// En OrderViewModel.kt
package com.una.cletaeats.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.model.Combo
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.una.cletaeats.data.repository.UsuarioRepository
import java.util.Calendar

data class OrderUiState(
    val catalogoCombos: List<Combo> = emptyList(),
    val combosEnPedido: List<Combo> = emptyList(),
    val subtotal: Double = 0.0,
    val iva: Double = 0.0,
    val costoEnvio: Double = 0.0,
    val total: Double = 0.0,
    val pedidoResult: Pedido? = null,
    val errorPedido: String? = null
)

class OrderViewModel(private val context: Context,
                     private val repository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    // 3. Le pasamos el 'repository' al crear el PedidoRepository
    private val pedidoRepository = PedidoRepository(context, repository)

    // Guardamos la distancia simulada para este pedido
    private var distanciaSimuladaEnKm: Int = 0
    init {
        generarCatalogoDeCombos()
        // 1. Simulamos la distancia y recalculamos los totales tan pronto como se crea el ViewModel
        simularDistanciaYCalcularCostos()
    }

    private fun simularDistanciaYCalcularCostos() {
        distanciaSimuladaEnKm = (1..15).random()
        recalcularTotales()
    }
    // 2. La lógica de si es feriado ahora vive aquí
    private fun esFeriado(): Boolean {
        val calendario = Calendar.getInstance()
        val diaDeLaSemana = calendario.get(Calendar.DAY_OF_WEEK)
        return diaDeLaSemana == Calendar.SATURDAY || diaDeLaSemana == Calendar.SUNDAY
    }



    fun agregarComboAlPedido(combo: Combo) {
        _uiState.update { it.copy(combosEnPedido = it.combosEnPedido + combo) }
        recalcularTotales()
    }

    fun removerComboDelPedido(combo: Combo) {
        _uiState.update { currentState ->
            val nuevaLista = currentState.combosEnPedido.toMutableList()
            nuevaLista.remove(combo)
            currentState.copy(combosEnPedido = nuevaLista)
        }
        recalcularTotales()
    }

    fun realizarPedido(cliente: Cliente, restaurante: Restaurante) {
        viewModelScope.launch {
            val combos = _uiState.value.combosEnPedido
            if (combos.isNotEmpty()) {
                // 3. Le pasamos la distancia y el costo ya calculados al repositorio
                val resultadoPedido = pedidoRepository.realizarPedido(
                    cliente,
                    restaurante,
                    combos,
                    distanciaSimuladaEnKm,
                    _uiState.value.costoEnvio
                )
                if (resultadoPedido != null) {
                    _uiState.update { it.copy(pedidoResult = resultadoPedido, errorPedido = null) }
                } else {
                    _uiState.update { it.copy(errorPedido = "No hay repartidores disponibles.") }
                }
            }
        }
    }

    fun limpiarEstadoPedido() {
        _uiState.update { OrderUiState(catalogoCombos = it.catalogoCombos) }
    }

    private fun generarCatalogoDeCombos() {
        val combos = List(9) { i ->
            val numeroCombo = i + 1
            val precio = 3000.0 + (numeroCombo * 1000.0)
            Combo(numero = numeroCombo, nombre = "Combo No. $numeroCombo", precio = precio)
        }
        _uiState.update { it.copy(catalogoCombos = combos) }
    }

    private fun recalcularTotales() {
        _uiState.update { currentState ->
            val subtotal = currentState.combosEnPedido.sumOf { it.precio }
            val costoPorKm = if (esFeriado()) 1500 else 1000
            val costoEnvio = (distanciaSimuladaEnKm * costoPorKm).toDouble()
            val iva = subtotal * 0.13
            val total = subtotal + costoEnvio + iva

            currentState.copy(
                subtotal = subtotal,
                iva = iva,
                costoEnvio = costoEnvio, // <-- Actualizamos el estado con el costo
                total = total
            )
        }
    }
}