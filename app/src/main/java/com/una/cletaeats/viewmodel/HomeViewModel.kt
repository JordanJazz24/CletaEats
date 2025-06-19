package com.una.cletaeats.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado para la pantalla principal, contendrá la lista de restaurantes
data class HomeUIState(
    val restaurantes: List<Restaurante> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    // El bloque init se ejecuta tan pronto como el ViewModel es creado
    init {
        Log.d("HomeScreenDebug", "HomeViewModel ha sido inicializado.")
        cargarRestaurantes()
    }

    private fun cargarRestaurantes() {
        // Usamos viewModelScope para lanzar una corutina segura
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Obtenemos los restaurantes desde el repositorio
            val listaRestaurantes = usuarioRepository.obtenerRestaurantes()
            // Actualizamos el estado con la nueva lista
            Log.d("HomeScreenDebug", "ViewModel ha cargado una lista con ${listaRestaurantes.size} restaurantes.")
            _uiState.value = _uiState.value.copy(restaurantes = listaRestaurantes, isLoading = false)
        }
    }
}