package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para la pantalla de gestión del menú
data class MenuManagementUiState(
    // Un mapa para guardar la descripción de cada combo (key=numero, value=descripción)
    val descripcionesCombos: Map<Int, String> = emptyMap()
)

class GestionarMenuViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuManagementUiState())
    val uiState = _uiState.asStateFlow()

    // Carga las descripciones del menú actual del restaurante
    fun cargarMenuActual(restaurante: Restaurante) {
        // Creamos un mapa completo del 1 al 9, con las descripciones existentes o un texto vacío
        val menuCompleto = (1..9).associateWith { restaurante.menu[it] ?: "" }
        _uiState.update { it.copy(descripcionesCombos = menuCompleto) }
    }

    // Actualiza la descripción de un combo específico
    fun onDescripcionChange(numeroCombo: Int, nuevaDescripcion: String) {
        _uiState.update { currentState ->
            val descripcionesActualizadas = currentState.descripcionesCombos.toMutableMap()
            descripcionesActualizadas[numeroCombo] = nuevaDescripcion
            currentState.copy(descripcionesCombos = descripcionesActualizadas)
        }
    }

    // Guarda el menú completo en el archivo
    fun guardarMenu(restauranteOriginal: Restaurante) {
        viewModelScope.launch {
            // Creamos una copia del restaurante y le asignamos el nuevo menú
            val restauranteActualizado = restauranteOriginal.copy(
                menu = _uiState.value.descripcionesCombos.toMutableMap()
            )
            // Usamos la función que ya habíamos creado para actualizar el restaurante
            repository.actualizarRestaurante(restauranteActualizado)
        }
    }
}