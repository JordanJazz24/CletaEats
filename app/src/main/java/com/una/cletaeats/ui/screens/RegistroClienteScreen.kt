package com.una.cletaeats.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.repository.UsuarioRepository
import com.una.cletaeats.viewmodel.CletaEatsViewModelFactory
import com.una.cletaeats.viewmodel.RegistroClienteViewModel
import kotlinx.coroutines.launch

// Imports de Material 3 - Asegúrate de que solo sean estos
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

// Imports para los íconos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteScreen(
    viewModel: RegistroClienteViewModel, // 1. Recibe el ViewModel
    onClienteRegistrado: () -> Unit,
    onVolver: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val clienteState = viewModel.clienteState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registro de Cliente") })
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
            // --- Campo Cédula ---
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

            // --- Campo Nombre ---
            OutlinedTextField(
                value = clienteState.value.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                isError = clienteState.value.errorNombre != null,
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorNombre?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // --- Campo Dirección ---
            OutlinedTextField(
                value = clienteState.value.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección") },
                isError = clienteState.value.errorDireccion != null,
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorDireccion?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // --- Campo Teléfono ---
            OutlinedTextField(
                value = clienteState.value.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = clienteState.value.errorTelefono != null,
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorTelefono?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // --- Campo Correo ---
            OutlinedTextField(
                value = clienteState.value.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = clienteState.value.errorCorreo != null,
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorCorreo?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // --- Campo Contraseña (con la variable de estado adentro) ---
            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = clienteState.value.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                isError = clienteState.value.errorPassword != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )
            clienteState.value.errorPassword?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // --- Campo Número de Tarjeta ---
            OutlinedTextField(
                value = clienteState.value.numeroTarjeta,
                onValueChange = { viewModel.onNumeroTarjetaChange(it) },
                label = { Text("Número de Tarjeta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = clienteState.value.errorNumeroTarjeta != null,
                modifier = Modifier.fillMaxWidth()
            )
            clienteState.value.errorNumeroTarjeta?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botón Registrar ---
            Button(
                onClick = {
                    if (viewModel.validarYRegistrarCliente()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Cliente registrado exitosamente")
                            // onClienteRegistrado(viewModel.obtenerCliente()) // Considera si necesitas esto ahora
                            viewModel.limpiarFormulario()
                            // onVolver() // Opcional: navegar hacia atrás después del registro
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