package com.una.cletaeats.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.una.cletaeats.data.model.Repartidor
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 1. Creamos una clase de estado para el formulario del repartidor
data class RepartidorFormState(
    val cedula: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val numeroTarjeta: String = "",
    val correo: String = "",
    val password: String = "",
    // Campos para los mensajes de error
    val errorCedula: String? = null,
    val errorNombre: String? = null,
    val errorDireccion: String? = null,
    val errorTelefono: String? = null,
    val errorNumeroTarjeta: String? = null,
    val errorCorreo: String? = null,
    val errorPassword: String? = null
)

// 2. Creamos el ViewModel
class RegistroRepartidorViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _repartidorState = MutableStateFlow(RepartidorFormState())
    val repartidorState: StateFlow<RepartidorFormState> = _repartidorState

    // --- Funciones para actualizar el estado desde la UI ---
    fun onCedulaChange(value: String) {
        _repartidorState.value = _repartidorState.value.copy(cedula = value, errorCedula = null)
    }
    fun onNombreChange(value: String) {
        _repartidorState.value = _repartidorState.value.copy(nombre = value, errorNombre = null)
    }
    fun onDireccionChange(value: String) {
        _repartidorState.value = _repartidorState.value.copy(direccion = value, errorDireccion = null)
    }
    fun onTelefonoChange(value: String) {
        _repartidorState.value = _repartidorState.value.copy(telefono = value, errorTelefono = null)
    }
    fun onNumeroTarjetaChange(value: String) {
        _repartidorState.value = _repartidorState.value.copy(numeroTarjeta = value, errorNumeroTarjeta = null)
    }
    fun onCorreoChange(value: String) {
        _repartidorState.value = _repartidorState.value.copy(correo = value, errorCorreo = null)
    }
    fun onPasswordChange(value: String) {
        _repartidorState.value = _repartidorState.value.copy(password = value, errorPassword = null)
    }

    // --- Lógica principal de validación y registro ---
    fun validarYRegistrarRepartidor(): Boolean {
        val state = _repartidorState.value

        // El documento pide no repetir cédulas para repartidores diferentes
        val errorCedula = if (state.cedula.isBlank()) "Cédula requerida" else if (usuarioRepository.existeRepartidor(state.cedula)) "Esta cédula ya está registrada" else null
        val errorNombre = if (state.nombre.isBlank()) "Nombre requerido" else null
        val errorDireccion = if (state.direccion.isBlank()) "Dirección requerida" else null
        val errorTelefono = if (state.telefono.length < 8) "Teléfono inválido" else null
        val errorNumeroTarjeta = if (state.numeroTarjeta.length < 8) "Número de tarjeta inválido" else null
        val errorCorreo = if (!state.correo.contains("@")) "Correo inválido" else null
        val errorPassword = if (state.password.length < 6) "Contraseña debe tener al menos 6 caracteres" else null

        // Actualizamos el estado con los nuevos errores (si los hay)
        _repartidorState.value = state.copy(
            errorCedula = errorCedula,
            errorNombre = errorNombre,
            errorDireccion = errorDireccion,
            errorTelefono = errorTelefono,
            errorNumeroTarjeta = errorNumeroTarjeta,
            errorCorreo = errorCorreo,
            errorPassword = errorPassword
        )

        // Si no hay ningún error, procedemos a registrar
        val esFormularioValido = listOf(errorCedula, errorNombre, errorDireccion, errorTelefono, errorNumeroTarjeta, errorCorreo, errorPassword).all { it == null }
        if (esFormularioValido) {
            val nuevoRepartidor = obtenerRepartidorDesdeEstado()
            Log.d("CletaEatsDebug", "ViewModel está llamando al repositorio para agregar: $nuevoRepartidor") // <-- AÑADE ESTA LÍNEA
            usuarioRepository.agregarRepartidor(nuevoRepartidor)
            return true
        }
        return false
    }

    // --- Funciones de ayuda ---
    private fun obtenerRepartidorDesdeEstado(): Repartidor {
        val state = _repartidorState.value
        return Repartidor(
            cedula = state.cedula,
            nombre = state.nombre,
            direccion = state.direccion,
            telefono = state.telefono,
            numeroTarjeta = state.numeroTarjeta,
            correo = state.correo,
            password = state.password
            // Los otros campos como 'estado', 'amonestaciones', etc., usan sus valores por defecto del constructor de Repartidor.
        )
    }

    fun limpiarFormulario() {
        _repartidorState.value = RepartidorFormState()
    }
}