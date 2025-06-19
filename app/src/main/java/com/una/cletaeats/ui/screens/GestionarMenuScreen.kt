package com.una.cletaeats.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.viewmodel.GestionarMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarMenuScreen(
    viewModel: GestionarMenuViewModel,
    restaurante: Restaurante,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Cargamos el menú actual del restaurante una sola vez al entrar a la pantalla
    LaunchedEffect(key1 = Unit) {
        viewModel.cargarMenuActual(restaurante)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestionar Menú de ${restaurante.nombre}") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            // Usamos LazyColumn para que sea "scrollable" y no haya problemas con el teclado
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.descripcionesCombos.size) { index ->
                    val numeroCombo = index + 1
                    val descripcionActual = uiState.descripcionesCombos[numeroCombo] ?: ""

                    OutlinedTextField(
                        value = descripcionActual,
                        onValueChange = { nuevaDescripcion ->
                            viewModel.onDescripcionChange(numeroCombo, nuevaDescripcion)
                        },
                        label = { Text("Descripción Combo No. $numeroCombo") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }
            }
            // Botón para guardar los cambios
            Button(
                onClick = {
                    viewModel.guardarMenu(restaurante)
                    Toast.makeText(context, "Menú guardado con éxito", Toast.LENGTH_SHORT).show()
                    onGuardado()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text("Guardar Menú")
            }
        }
    }
}