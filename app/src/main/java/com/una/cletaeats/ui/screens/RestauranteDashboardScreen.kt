package com.una.cletaeats.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.viewmodel.RestauranteDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestauranteDashboardScreen(
    viewModel: RestauranteDashboardViewModel,
    restaurante: Restaurante,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargamos los pedidos del restaurante una sola vez
    LaunchedEffect(key1 = restaurante.cedulaJuridica) {
        viewModel.cargarPedidosDelRestaurante(restaurante.cedulaJuridica)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos Recibidos") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.pedidosRecibidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No has recibido pedidos por el momento.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.pedidosRecibidos) { pedido ->
                    PedidoItemRestaurante(pedido = pedido)
                }
            }
        }
    }
}

@Composable
fun PedidoItemRestaurante(pedido: Pedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Pedido para: ${pedido.cliente.nombre}", style = MaterialTheme.typography.titleMedium)
            Text("Teléfono: ${pedido.cliente.telefono}")
            Divider()
            Text("Detalle del Pedido:", fontWeight = FontWeight.Bold)
            // Mostramos los combos solicitados
            pedido.combos.forEach { combo ->
                Text("- ${combo.nombre}")
            }
            Divider()
            Text("Estado Actual: ${pedido.estado}", fontWeight = FontWeight.Bold)
            Text("Repartidor Asignado: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
        }
    }
}