package com.una.cletaeats.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.una.cletaeats.data.repository.UsuarioRepository
import com.una.cletaeats.viewmodel.CletaEatsViewModelFactory
import com.una.cletaeats.viewmodel.RegistroRepartidorViewModel
import kotlinx.coroutines.launch

// Imports de Material 3 y de Íconos
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroRepartidorScreen(
    onRepartidorRegistrado: () -> Unit,
    onVolver: () -> Unit
) {
    // Boilerplate para obtener el ViewModel
    val context = LocalContext.current.applicationContext
    val repository = remember { UsuarioRepository(context) }
    val factory = remember { CletaEatsViewModelFactory(repository) }
    val viewModel: RegistroRepartidorViewModel = viewModel(factory = factory)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val repartidorState by viewModel.repartidorState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Repartidor") }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        // Hacemos la columna "scrollable" por si no cabe en pantallas pequeñas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Cédula ---
            OutlinedTextField(
                value = repartidorState.cedula,
                onValueChange = viewModel::onCedulaChange,
                label = { Text("Cédula") },
                isError = repartidorState.errorCedula != null,
                modifier = Modifier.fillMaxWidth()
            )
            repartidorState.errorCedula?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Nombre ---
            OutlinedTextField(
                value = repartidorState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre Completo") },
                isError = repartidorState.errorNombre != null,
                modifier = Modifier.fillMaxWidth()
            )
            repartidorState.errorNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Dirección ---
            OutlinedTextField(
                value = repartidorState.direccion,
                onValueChange = viewModel::onDireccionChange,
                label = { Text("Dirección Exacta") },
                isError = repartidorState.errorDireccion != null,
                modifier = Modifier.fillMaxWidth()
            )
            repartidorState.errorDireccion?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Teléfono ---
            OutlinedTextField(
                value = repartidorState.telefono,
                onValueChange = viewModel::onTelefonoChange,
                label = { Text("Número de Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = repartidorState.errorTelefono != null,
                modifier = Modifier.fillMaxWidth()
            )
            repartidorState.errorTelefono?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Número de Tarjeta ---
            OutlinedTextField(
                value = repartidorState.numeroTarjeta,
                onValueChange = viewModel::onNumeroTarjetaChange,
                label = { Text("Número de Tarjeta (para depósitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = repartidorState.errorNumeroTarjeta != null,
                modifier = Modifier.fillMaxWidth()
            )
            repartidorState.errorNumeroTarjeta?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Correo Electrónico ---
            OutlinedTextField(
                value = repartidorState.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = repartidorState.errorCorreo != null,
                modifier = Modifier.fillMaxWidth()
            )
            repartidorState.errorCorreo?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Contraseña ---
            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = repartidorState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                isError = repartidorState.errorPassword != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar" else "Mostrar")
                    }
                }
            )
            repartidorState.errorPassword?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botón de Registro ---
            Button(
                onClick = {
                    Log.d("CletaEatsDebug", "Botón 'Registrarse' presionado.") // <-- AÑADE ESTA LÍNEA
                    if (viewModel.validarYRegistrarRepartidor()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Repartidor registrado exitosamente")
                            viewModel.limpiarFormulario()
                            onRepartidorRegistrado()
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Por favor, corrija los errores")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}