package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.model.TipoComida
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 1. Clase de estado para el formulario del restaurante
data class RestauranteFormState(
    val cedulaJuridica: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val tipoComida: TipoComida = TipoComida.TRADICIONAL, // Valor por defecto
    val correo: String = "",
    val password: String = "",
    // Campos para los mensajes de error
    val errorCedula: String? = null,
    val errorNombre: String? = null,
    val errorDireccion: String? = null,
    val errorTelefono: String? = null,
    val errorCorreo: String? = null,
    val errorPassword: String? = null
)

// 2. Creamos el ViewModel
class RegistroRestauranteViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _restauranteState = MutableStateFlow(RestauranteFormState())
    val restauranteState: StateFlow<RestauranteFormState> = _restauranteState

    // --- Funciones para actualizar el estado desde la UI ---
    fun onCedulaChange(value: String) {
        _restauranteState.value = _restauranteState.value.copy(cedulaJuridica = value, errorCedula = null)
    }
    fun onNombreChange(value: String) {
        _restauranteState.value = _restauranteState.value.copy(nombre = value, errorNombre = null)
    }
    fun onDireccionChange(value: String) {
        _restauranteState.value = _restauranteState.value.copy(direccion = value, errorDireccion = null)
    }
    fun onTelefonoChange(value: String) {
        _restauranteState.value = _restauranteState.value.copy(telefono = value, errorTelefono = null)
    }
    fun onTipoComidaChange(tipo: TipoComida) {
        _restauranteState.value = _restauranteState.value.copy(tipoComida = tipo)
    }
    fun onCorreoChange(value: String) {
        _restauranteState.value = _restauranteState.value.copy(correo = value, errorCorreo = null)
    }
    fun onPasswordChange(value: String) {
        _restauranteState.value = _restauranteState.value.copy(password = value, errorPassword = null)
    }

    // --- Lógica principal de validación y registro ---
    fun validarYRegistrarRestaurante(): Boolean {
        val state = _restauranteState.value

        val errorCedula = if (state.cedulaJuridica.isBlank()) "Cédula jurídica requerida" else if (usuarioRepository.existeRestaurante(state.cedulaJuridica)) "Esta cédula ya está registrada" else null
        val errorNombre = if (state.nombre.isBlank()) "Nombre requerido" else null
        val errorDireccion = if (state.direccion.isBlank()) "Dirección requerida" else null
        val errorTelefono = if (state.telefono.length < 8) "Teléfono inválido" else null
        val errorCorreo = if (!state.correo.contains("@")) "Correo inválido" else null
        val errorPassword = if (state.password.length < 6) "Contraseña debe tener al menos 6 caracteres" else null

        _restauranteState.value = state.copy(
            errorCedula = errorCedula,
            errorNombre = errorNombre,
            errorDireccion = errorDireccion,
            errorTelefono = errorTelefono,
            errorCorreo = errorCorreo,
            errorPassword = errorPassword
        )

        val esFormularioValido = listOf(errorCedula, errorNombre, errorDireccion, errorTelefono, errorCorreo, errorPassword).all { it == null }
        if (esFormularioValido) {
            val nuevoRestaurante = obtenerRestauranteDesdeEstado()
            usuarioRepository.agregarRestaurante(nuevoRestaurante)
            return true
        }
        return false
    }

    // --- Funciones de ayuda ---
    private fun obtenerRestauranteDesdeEstado(): Restaurante {
        val state = _restauranteState.value
        return Restaurante(
            cedulaJuridica = state.cedulaJuridica,
            nombre = state.nombre,
            direccion = state.direccion,
            telefono = state.telefono,
            tipoComida = state.tipoComida,
            correo = state.correo,
            password = state.password
            // calificacionPromedio y cantidadPedidos usan sus valores por defecto
        )
    }

    fun limpiarFormulario() {
        _restauranteState.value = RestauranteFormState()
    }
}