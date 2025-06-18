package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.cletaeats.data.model.TipoUsuario
import com.una.cletaeats.data.model.Usuario
import com.una.cletaeats.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginResult {
    data class Success(val usuario: Usuario) : LoginResult()
    data class Error(val mensaje: String) : LoginResult()
    object Loading : LoginResult()
    object Idle : LoginResult()
}

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository // Ya no hay valor por defecto
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginState: StateFlow<LoginResult> = _loginState

    fun login(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _loginState.value = LoginResult.Error("Correo y contraseña no pueden estar vacíos")
            return
        }

        _loginState.value = LoginResult.Loading

        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuario(correo, password)
            if (usuario != null) {
                _loginState.value = LoginResult.Success(usuario)
            } else {
                _loginState.value = LoginResult.Error("Usuario o contraseña incorrectos")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginResult.Idle
    }
}
