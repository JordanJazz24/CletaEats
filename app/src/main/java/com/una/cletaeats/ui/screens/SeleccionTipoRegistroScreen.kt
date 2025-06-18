package com.una.cletaeats.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class TipoRegistro {
    CLIENTE, REPARTIDOR, RESTAURANTE
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionTipoRegistroScreen(
    onTipoSeleccionado: (TipoRegistro) -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Seleccionar tipo de registro") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onTipoSeleccionado(TipoRegistro.CLIENTE) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse como Cliente")
            }
            Button(
                onClick = { onTipoSeleccionado(TipoRegistro.REPARTIDOR) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse como Repartidor")
            }
            Button(
                onClick = { onTipoSeleccionado(TipoRegistro.RESTAURANTE) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse como Restaurante")
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}
