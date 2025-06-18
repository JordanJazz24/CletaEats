package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ClienteFormState(
    val cedula: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val numeroTarjeta: String = "",
    val correo: String = "",
    val password: String = "",
    val errorCedula: String? = null,
    val errorNombre: String? = null,
    val errorDireccion: String? = null,
    val errorTelefono: String? = null,
    val errorNumeroTarjeta: String? = null,
    val errorCorreo: String? = null,
    val errorPassword: String? = null
)

class RegistroClienteViewModel : ViewModel() {

    private val _clienteState = MutableStateFlow(ClienteFormState())
    val clienteState: StateFlow<ClienteFormState> = _clienteState

    fun onCedulaChange(value: String) {
        _clienteState.value = _clienteState.value.copy(cedula = value)
    }

    fun onNombreChange(value: String) {
        _clienteState.value = _clienteState.value.copy(nombre = value)
    }

    fun onDireccionChange(value: String) {
        _clienteState.value = _clienteState.value.copy(direccion = value)
    }

    fun onTelefonoChange(value: String) {
        _clienteState.value = _clienteState.value.copy(telefono = value)
    }

    fun onNumeroTarjetaChange(value: String) {
        _clienteState.value = _clienteState.value.copy(numeroTarjeta = value)
    }

    fun onCorreoChange(value: String) {
        _clienteState.value = _clienteState.value.copy(correo = value)
    }

    fun onPasswordChange(value: String) {
        _clienteState.value = _clienteState.value.copy(password = value)
    }

    fun validarYRegistrarCliente(): Boolean {
        var valido = true
        val state = _clienteState.value

        val errorCedula = if (state.cedula.length < 5) "Cédula inválida" else null
        val errorNombre = if (state.nombre.isBlank()) "Nombre requerido" else null
        val errorDireccion = if (state.direccion.isBlank()) "Dirección requerida" else null
        val errorTelefono = if (state.telefono.length < 8) "Teléfono inválido" else null
        val errorNumeroTarjeta = if (state.numeroTarjeta.length < 8) "Número de tarjeta inválido" else null
        val errorCorreo = if (!state.correo.contains("@")) "Correo inválido" else null
        val errorPassword = if (state.password.length < 6) "Contraseña debe tener al menos 6 caracteres" else null

        if (listOf(errorCedula, errorNombre, errorDireccion, errorTelefono, errorNumeroTarjeta, errorCorreo, errorPassword).any { it != null }) {
            valido = false
        }

        _clienteState.value = state.copy(
            errorCedula = errorCedula,
            errorNombre = errorNombre,
            errorDireccion = errorDireccion,
            errorTelefono = errorTelefono,
            errorNumeroTarjeta = errorNumeroTarjeta,
            errorCorreo = errorCorreo,
            errorPassword = errorPassword
        )
        return valido
    }

    fun obtenerCliente() = com.una.cletaeats.data.model.Cliente(
        cedula = _clienteState.value.cedula,
        nombre = _clienteState.value.nombre,
        direccion = _clienteState.value.direccion,
        telefono = _clienteState.value.telefono,
        numeroTarjeta = _clienteState.value.numeroTarjeta,
        estado = com.una.cletaeats.data.model.EstadoCliente.ACTIVO,
        correo = _clienteState.value.correo,
        password = _clienteState.value.password
    )

    fun limpiarFormulario() {
        _clienteState.value = ClienteFormState()
    }
}
