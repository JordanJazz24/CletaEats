package com.una.cletaeats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.una.cletaeats.data.repository.UsuarioRepository

// Esta clase sabe cómo crear nuestros ViewModels
class CletaEatsViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {

    // Esta es la función principal de la factory
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Revisa qué ViewModel se está pidiendo y lo crea con el repositorio
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(RegistroClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroClienteViewModel(repository) as T
        }
        // Si se pide un ViewModel desconocido, lanza un error
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}