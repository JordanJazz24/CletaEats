package com.una.cletaeats

import android.content.Intent
import android.widget.Toast
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
import com.una.cletaeats.data.model.Pedido
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
    var pedidoSeleccionado by remember { mutableStateOf<Pedido?>(null) }

    // CREACIÓN CENTRALIZADA DE DEPENDENCIAS
    val context = LocalContext.current
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
    val misPedidosViewModel: com.una.cletaeats.viewmodel.MisPedidosViewModel = viewModel(factory = factory)
    val gestionarMenuViewModel: com.una.cletaeats.viewmodel.GestionarMenuViewModel = viewModel(factory = factory)
    val PANTALLA_GESTIONAR_MENU = "gestionar_menu"
    val PANTALLA_MIS_PEDIDOS = "mis_pedidos"
    val PANTALLA_FACTURA = "factura"

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
                // Nos aseguramos de tener un cliente logueado
                val cliente = usuarioLogueado as? Cliente
                if (cliente != null) {
                    // 1. Formateamos el menú para que se vea bien en el correo
                    val menuFormateado = StringBuilder()
                    menuFormateado.append("¡Hola, ${cliente.nombre}! Aquí tienes el menú de ${restaurante.nombre}:\n\n")
                    (1..9).forEach { i ->
                        val descripcion = restaurante.menu[i] ?: "Descripción no disponible."
                        val precio = 3000.0 + (i * 1000.0)
                        menuFormateado.append("Combo No. $i: $descripcion (₡$precio)\n")
                    }
                    menuFormateado.append("\n¡Buen provecho!\n- El equipo de CletaEats")

                    // 2. Creamos el Intent para enviar el correo
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822" // Tipo estándar para clientes de correo
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(cliente.correo)) // Destinatario
                        putExtra(Intent.EXTRA_SUBJECT, "Menú de ${restaurante.nombre}") // Asunto
                        putExtra(Intent.EXTRA_TEXT, menuFormateado.toString()) // Cuerpo del correo
                    }

                    // 3. Lanzamos el selector de apps de correo
                    try {
                        // Usamos el 'context' que ya tenemos en AppMain
                        context.startActivity(
                            Intent.createChooser(intent, "Elige una aplicación de correo:")
                        )
                    } catch (e: android.content.ActivityNotFoundException) {
                        // Opcional: Manejar el caso de que el usuario no tenga apps de correo
                        Toast.makeText(context, "No se encontró ninguna aplicación de correo.", Toast.LENGTH_SHORT).show()
                    }
                }

                // 4. Después de lanzar el intent, continuamos con la navegación a la pantalla de menú
                // por si el usuario decide no enviar el correo y quiere ordenar de todas formas.
                restauranteIdSeleccionado = restaurante.cedulaJuridica
                pantallaActual = "menu_restaurante"
            },
            onMisPedidosClick = { pantallaActual = PANTALLA_MIS_PEDIDOS }
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
                    // AÑADE ESTE PARÁMETRO Y SU LÓGICA
                    onGestionarMenuClick = {
                        pantallaActual = PANTALLA_GESTIONAR_MENU
                    },
                    onLogout = {
                        usuarioLogueado = null
                        pantallaActual = "login"
                    }
                )
            } else {
                pantallaActual = "login"
            }
        }
        PANTALLA_GESTIONAR_MENU -> {
            val restaurante = usuarioLogueado as? com.una.cletaeats.data.model.Restaurante
            if (restaurante != null) {
                GestionarMenuScreen(
                    viewModel = gestionarMenuViewModel,
                    restaurante = restaurante,
                    onGuardado = { pantallaActual = "home_restaurante" },
                    onVolver = { pantallaActual = "home_restaurante" }
                )
            } else {
                pantallaActual = "login"
            }
        }

        PANTALLA_MIS_PEDIDOS -> {
            val cliente = usuarioLogueado as? Cliente
            if (cliente != null) {
                MisPedidosScreen(
                    viewModel = misPedidosViewModel,
                    cliente = cliente,
                    onPedidoClick = { pedido ->
                        pedidoSeleccionado = pedido
                        pantallaActual = PANTALLA_FACTURA
                    },
                    onVolver = { pantallaActual = "home_cliente" }
                )
            } else {
                pantallaActual = "login"
            }
        }

        PANTALLA_FACTURA -> {
            val pedido = pedidoSeleccionado
            if (pedido != null) {
                FacturaScreen(
                    pedido = pedido,
                    onVolver = { pantallaActual = PANTALLA_MIS_PEDIDOS }
                )
            } else {
                pantallaActual = PANTALLA_MIS_PEDIDOS
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
