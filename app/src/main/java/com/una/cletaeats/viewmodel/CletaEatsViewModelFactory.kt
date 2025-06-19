// En CletaEatsViewModelFactory.kt
package com.una.cletaeats.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.una.cletaeats.data.repository.PedidoRepository
import com.una.cletaeats.data.repository.UsuarioRepository

class CletaEatsViewModelFactory(
    private val context: Context,
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {

    //La factory ahora también puede crear el PedidoRepository cuando lo necesite
    private val pedidoRepository by lazy { PedidoRepository(context) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository) as T
            modelClass.isAssignableFrom(RegistroClienteViewModel::class.java) -> RegistroClienteViewModel(repository) as T
            modelClass.isAssignableFrom(RegistroRepartidorViewModel::class.java) -> RegistroRepartidorViewModel(repository) as T
            modelClass.isAssignableFrom(RegistroRestauranteViewModel::class.java) -> RegistroRestauranteViewModel(repository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> OrderViewModel(context) as T
            modelClass.isAssignableFrom(RepartidorDashboardViewModel::class.java) -> RepartidorDashboardViewModel(pedidoRepository, repository) as T
            modelClass.isAssignableFrom(RestauranteDashboardViewModel::class.java) -> RestauranteDashboardViewModel(pedidoRepository) as T
            modelClass.isAssignableFrom(GestionarMenuViewModel::class.java) -> GestionarMenuViewModel(repository) as T
            modelClass.isAssignableFrom(MisPedidosViewModel::class.java) -> MisPedidosViewModel(pedidoRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}