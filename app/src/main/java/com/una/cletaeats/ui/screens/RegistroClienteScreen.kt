package com.una.cletaeats.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.viewmodel.RegistroClienteViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.una.cletaeats.data.repository.UsuarioRepository
import com.una.cletaeats.viewmodel.CletaEatsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteScreen(
    onClienteRegistrado: (Cliente) -> Unit,
    onVolver: () -> Unit = { /* Acción por defecto si no se necesita */ }
) {
    // Aplicamos el mismo patrón que en LoginScreen
    val context = LocalContext.current.applicationContext
    val repository = remember { UsuarioRepository(context) }
    val factory = remember { CletaEatsViewModelFactory(repository) }
    val viewModel: RegistroClienteViewModel = viewModel(factory = factory)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val clienteState = viewModel.clienteState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Cliente") }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = clienteState.value.cedula,
                onValueChange = { viewModel.onCedulaChange(it) },
                label = { Text("Cédula") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = clienteState.value.errorCedula != null,
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorCedula?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = clienteState.value.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorNombre?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = clienteState.value.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorDireccion?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = clienteState.value.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorTelefono?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = clienteState.value.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorCorreo?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = clienteState.value.numeroTarjeta,
                onValueChange = { viewModel.onNumeroTarjetaChange(it) },
                label = { Text("Número de Tarjeta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorNumeroTarjeta?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (viewModel.validarYRegistrarCliente()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Cliente registrado exitosamente")
                            onClienteRegistrado(viewModel.obtenerCliente())
                            viewModel.limpiarFormulario()
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Corrija los errores antes de enviar")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}
