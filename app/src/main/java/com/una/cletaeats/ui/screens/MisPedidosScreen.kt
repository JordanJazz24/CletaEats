package com.una.cletaeats.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.viewmodel.MisPedidosViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    viewModel: MisPedidosViewModel,
    cliente: Cliente,
    onPedidoClick: (Pedido) -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = cliente.cedula) {
        viewModel.cargarPedidosDelCliente(cliente.cedula)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Pedidos") }) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Sección de Pedidos Pendientes
                stickyHeader {
                    Column(Modifier.background(MaterialTheme.colorScheme.surfaceVariant).fillMaxWidth().padding(16.dp)) {
                        Text("Pedidos Pendientes", style = MaterialTheme.typography.titleMedium)
                    }
                }
                if (uiState.pedidosPendientes.isEmpty()) {
                    item { Text("No tienes pedidos pendientes.", modifier = Modifier.padding(16.dp)) }
                } else {
                    items(uiState.pedidosPendientes) { pedido ->
                        PedidoItemCliente(pedido = pedido, onPedidoClick = onPedidoClick)
                    }
                }

                // Sección de Pedidos Completados
                stickyHeader {
                    Column(Modifier.background(MaterialTheme.colorScheme.surfaceVariant).fillMaxWidth().padding(16.dp)) {
                        Text("Historial de Pedidos", style = MaterialTheme.typography.titleMedium)
                    }
                }
                if (uiState.pedidosCompletados.isEmpty()) {
                    item { Text("No tienes pedidos en tu historial.", modifier = Modifier.padding(16.dp)) }
                } else {
                    items(uiState.pedidosCompletados) { pedido ->
                        PedidoItemCliente(pedido = pedido, onPedidoClick = onPedidoClick)
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoItemCliente(pedido: Pedido, onPedidoClick: (Pedido) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onPedidoClick(pedido) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Pedido a: ${pedido.restaurante.nombre}", fontWeight = FontWeight.Bold)
            Text("Fecha: ${pedido.fechaHoraPedido}")
            Text("Estado: ${pedido.estado}", color = MaterialTheme.colorScheme.primary)
            Text("Total: ₡${pedido.total}", fontWeight = FontWeight.SemiBold)
        }
    }
}