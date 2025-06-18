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
import com.una.cletaeats.ui.screens.*
import com.una.cletaeats.ui.theme.CletaEatsTheme

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

@Composable
fun AppMain() {
    var pantallaActual by remember { mutableStateOf("login") }
    var tipoRegistroSeleccionado by remember { mutableStateOf<TipoRegistro?>(null) }

    // Aquí puedes tener una lista o repo simulado para usuarios si quieres

    when (pantallaActual) {
        "login" -> LoginScreen(
            onLoginSuccess = { /* Navegar a pantalla principal o home */ },
            onNavigateToRegistro = {
                pantallaActual = "seleccion_registro"
            }
        )
        "seleccion_registro" -> SeleccionTipoRegistroScreen(
            onTipoSeleccionado = {
                tipoRegistroSeleccionado = it
                pantallaActual = "registro"
            },
            onVolver = { pantallaActual = "login" }
        )
        "registro" -> {
            when (tipoRegistroSeleccionado) {
                TipoRegistro.CLIENTE -> RegistroClienteScreen(
                    onClienteRegistrado = { cliente: Cliente ->
                        // Aquí puedes agregar cliente al repositorio o lista
                        pantallaActual = "login" // Volver a login tras registro
                    },
                    onVolver = {
                        pantallaActual = "seleccion_registro"
                    }
                )
                TipoRegistro.REPARTIDOR -> {
                    RegistroRepartidorScreen(
                        onRepartidorRegistrado = {
                            // Tras un registro exitoso, volvemos al login
                            pantallaActual = "login"
                        },
                        onVolver = {
                            // Si el usuario quiere volver, regresa a la pantalla de selección
                            pantallaActual = "seleccion_registro"
                        }
                    )
                }
                TipoRegistro.RESTAURANTE -> {
                    RegistroRestauranteScreen(
                        onRestauranteRegistrado = {
                            // Tras un registro exitoso, volvemos al login
                            pantallaActual = "login"
                        },
                        onVolver = {
                            // Si el usuario quiere volver, regresa a la pantalla de selección
                            pantallaActual = "seleccion_registro"
                        }
                    )
                }
                null -> {
                    // Si por alguna razón es null, volvemos a selección
                    pantallaActual = "seleccion_registro"
                }
            }
        }
        else -> {
            // En caso de pantalla desconocida, ir a login
            pantallaActual = "login"
        }
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
