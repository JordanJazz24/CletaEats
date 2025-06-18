package com.una.cletaeats.ui.screens

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
import com.una.cletaeats.data.model.TipoComida
import com.una.cletaeats.data.repository.UsuarioRepository
import com.una.cletaeats.viewmodel.CletaEatsViewModelFactory
import com.una.cletaeats.viewmodel.RegistroRestauranteViewModel
import kotlinx.coroutines.launch

// Imports de Material 3 y de Íconos
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroRestauranteScreen(
    viewModel: RegistroRestauranteViewModel, // 1. Recibe el ViewModel
    onRestauranteRegistrado: () -> Unit,
    onVolver: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val restauranteState by viewModel.restauranteState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registro de Restaurante") })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Cédula Jurídica ---
            OutlinedTextField(
                value = restauranteState.cedulaJuridica,
                onValueChange = viewModel::onCedulaChange,
                label = { Text("Cédula Jurídica") },
                isError = restauranteState.errorCedula != null,
                modifier = Modifier.fillMaxWidth()
            )
            restauranteState.errorCedula?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Nombre del Restaurante ---
            OutlinedTextField(
                value = restauranteState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del Restaurante") },
                isError = restauranteState.errorNombre != null,
                modifier = Modifier.fillMaxWidth()
            )
            restauranteState.errorNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Dirección ---
            OutlinedTextField(
                value = restauranteState.direccion,
                onValueChange = viewModel::onDireccionChange,
                label = { Text("Dirección del Local") },
                isError = restauranteState.errorDireccion != null,
                modifier = Modifier.fillMaxWidth()
            )
            restauranteState.errorDireccion?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Teléfono ---
            OutlinedTextField(
                value = restauranteState.telefono,
                onValueChange = viewModel::onTelefonoChange,
                label = { Text("Número de Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = restauranteState.errorTelefono != null,
                modifier = Modifier.fillMaxWidth()
            )
            restauranteState.errorTelefono?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- MENÚ DESPLEGABLE para Tipo de Comida ---
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = restauranteState.tipoComida.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Comida") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TipoComida.values().forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.name) },
                            onClick = {
                                viewModel.onTipoComidaChange(tipo)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // --- Correo Electrónico ---
            OutlinedTextField(
                value = restauranteState.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = restauranteState.errorCorreo != null,
                modifier = Modifier.fillMaxWidth()
            )
            restauranteState.errorCorreo?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // --- Contraseña ---
            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = restauranteState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                isError = restauranteState.errorPassword != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar" else "Mostrar")
                    }
                }
            )
            restauranteState.errorPassword?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botón de Registro ---
            Button(
                onClick = {
                    if (viewModel.validarYRegistrarRestaurante()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Restaurante registrado exitosamente")
                            viewModel.limpiarFormulario()
                            onRestauranteRegistrado()
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Por favor, corrija los errores")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Restaurante")
            }
        }
    }
}