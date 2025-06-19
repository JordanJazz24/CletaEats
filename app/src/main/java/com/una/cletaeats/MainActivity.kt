package com.una.cletaeats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.model.TipoUsuario
import com.una.cletaeats.ui.screens.*
import com.una.cletaeats.ui.theme.CletaEatsTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.una.cletaeats.data.model.Restaurante

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CletaEatsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppMain()
                }
            }
        }
    }
}

// En MainActivity.kt
@Composable
fun AppMain() {
    // ESTADOS DE NAVEGACIÓN
    var pantallaActual by remember { mutableStateOf("login") }
    var tipoRegistroSeleccionado by remember { mutableStateOf<com.una.cletaeats.ui.screens.TipoRegistro?>(null) }
    var restauranteIdSeleccionado by remember { mutableStateOf<String?>(null) }
    var usuarioLogueado by remember { mutableStateOf<com.una.cletaeats.data.model.Usuario?>(null) }

    // CREACIÓN CENTRALIZADA DE DEPENDENCIAS
    val context = LocalContext.current.applicationContext
    val repository = remember { com.una.cletaeats.data.repository.UsuarioRepository(context) }
    // Se crea la factory UNA SOLA VEZ, con los DOS argumentos correctos.
    val factory = remember { com.una.cletaeats.viewmodel.CletaEatsViewModelFactory(context, repository) }

    // Creación de TODOS los ViewModels en un solo lugar.
    val loginViewModel: com.una.cletaeats.viewmodel.LoginViewModel = viewModel(factory = factory)
    val registroClienteViewModel: com.una.cletaeats.viewmodel.RegistroClienteViewModel = viewModel(factory = factory)
    val registroRepartidorViewModel: com.una.cletaeats.viewmodel.RegistroRepartidorViewModel = viewModel(factory = factory)
    val registroRestauranteViewModel: com.una.cletaeats.viewmodel.RegistroRestauranteViewModel = viewModel(factory = factory)
    val homeViewModel: com.una.cletaeats.viewmodel.HomeViewModel = viewModel(factory = factory)
    val orderViewModel: com.una.cletaeats.viewmodel.OrderViewModel = viewModel(factory = factory)
    val repartidorDashboardViewModel: com.una.cletaeats.viewmodel.RepartidorDashboardViewModel = viewModel(factory = factory)
    val restauranteDashboardViewModel: com.una.cletaeats.viewmodel.RestauranteDashboardViewModel = viewModel(factory = factory)

    // NAVEGACIÓN
    when (pantallaActual) {
        "login" -> LoginScreen(
            viewModel = loginViewModel,
            onLoginSuccess = { usuario ->
                usuarioLogueado = usuario
                when (usuario.rol) {
                    com.una.cletaeats.data.model.TipoUsuario.CLIENTE -> pantallaActual = "home_cliente"
                    com.una.cletaeats.data.model.TipoUsuario.REPARTIDOR -> pantallaActual = "home_repartidor"
                    com.una.cletaeats.data.model.TipoUsuario.RESTAURANTE -> pantallaActual = "home_restaurante"
                }
            },
            onNavigateToRegistro = { pantallaActual = "seleccion_registro" }
        )
        "seleccion_registro" -> SeleccionTipoRegistroScreen(
            onTipoSeleccionado = { tipo ->
                tipoRegistroSeleccionado = tipo
                pantallaActual = "registro"
            },
            onVolver = { pantallaActual = "login" }
        )
        "registro" -> when (tipoRegistroSeleccionado) {
            com.una.cletaeats.ui.screens.TipoRegistro.CLIENTE -> RegistroClienteScreen(viewModel = registroClienteViewModel, onClienteRegistrado = { pantallaActual = "login" }, onVolver = { pantallaActual = "seleccion_registro" })
            com.una.cletaeats.ui.screens.TipoRegistro.REPARTIDOR -> RegistroRepartidorScreen(viewModel = registroRepartidorViewModel, onRepartidorRegistrado = { pantallaActual = "login" }, onVolver = { pantallaActual = "seleccion_registro" })
            com.una.cletaeats.ui.screens.TipoRegistro.RESTAURANTE -> RegistroRestauranteScreen(viewModel = registroRestauranteViewModel, onRestauranteRegistrado = { pantallaActual = "login" }, onVolver = { pantallaActual = "seleccion_registro" })
            else -> pantallaActual = "login"
        }
        "home_cliente" -> HomeScreen(
            viewModel = homeViewModel,
            onLogout = { usuarioLogueado = null; pantallaActual = "login" },
            onRestaurantClick = { restaurante ->
                restauranteIdSeleccionado = restaurante.cedulaJuridica
                pantallaActual = "menu_restaurante"
            }
        )
        "menu_restaurante" -> {
            val cliente = usuarioLogueado as? com.una.cletaeats.data.model.Cliente
            val restaurante = restauranteIdSeleccionado?.let { id -> repository.obtenerRestaurantes().find { it.cedulaJuridica == id } }
            if (cliente != null && restaurante != null) {
                MenuScreen(
                    viewModel = orderViewModel,
                    cliente = cliente,
                    restaurante = restaurante,
                    onVolver = { pantallaActual = "home_cliente" },
                    onPedidoExitoso = { pantallaActual = "home_cliente" }
                )
            } else {
                pantallaActual = "home_cliente"
            }
        }
        "home_repartidor" -> {
            // Nos aseguramos de que el usuario logueado sea un repartidor
            val repartidor = usuarioLogueado as? com.una.cletaeats.data.model.Repartidor
            if (repartidor != null) {
                // Y mostramos la pantalla del dashboard, pasándole el viewModel y el repartidor
                RepartidorDashboardScreen(
                    viewModel = repartidorDashboardViewModel,
                    repartidor = repartidor,
                    onLogout = {
                        usuarioLogueado = null
                        pantallaActual = "login"
                    }
                )
            } else {
                // Si algo sale mal, por seguridad, volvemos al login
                pantallaActual = "login"
            }
        }

        "home_restaurante" -> {
            val restaurante = usuarioLogueado as? Restaurante
            if (restaurante != null) {
                RestauranteDashboardScreen(
                    viewModel = restauranteDashboardViewModel,
                    restaurante = restaurante,
                    onLogout = {
                        usuarioLogueado = null
                        pantallaActual = "login"
                    }
                )
            } else {
                pantallaActual = "login"
            }
        }
        else -> pantallaActual = "login"
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PlaceholderScreen(mensaje: String, onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pendiente") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(mensaje)
            Spacer(modifier = androidx.compose.ui.Modifier.height(20.dp))
            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
    }
}
