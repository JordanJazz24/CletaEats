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
import com.una.cletaeats.data.model.EstadoPedido
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.data.model.Repartidor
import com.una.cletaeats.viewmodel.RepartidorDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepartidorDashboardScreen(
    viewModel: RepartidorDashboardViewModel,
    repartidor: Repartidor,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Usamos LaunchedEffect para cargar los pedidos una sola vez cuando la pantalla se muestra
    LaunchedEffect(key1 = repartidor.cedula) {
        viewModel.cargarPedidosDelRepartidor(repartidor.cedula)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos Asignados") },
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
        } else if (uiState.misPedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes pedidos asignados por el momento.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.misPedidos) { pedido ->
                    PedidoItemRepartidor(
                        pedido = pedido,
                        onUpdateStatus = { nuevoEstado ->
                            viewModel.actualizarEstadoPedido(pedido, nuevoEstado)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PedidoItemRepartidor(pedido: Pedido, onUpdateStatus: (EstadoPedido) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Pedido ID: ${pedido.id}", style = MaterialTheme.typography.titleMedium)
            Divider()
            Text("Restaurante: ${pedido.restaurante.nombre}")
            Text("Dirección de entrega: ${pedido.cliente.direccion}")
            Text("Cliente: ${pedido.cliente.nombre} - ${pedido.cliente.telefono}")
            Text("Estado Actual: ${pedido.estado}", fontWeight = FontWeight.Bold)
            Divider()
            Text("Acciones:", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Solo mostramos los botones de acción si el pedido no ha sido entregado
                if (pedido.estado != EstadoPedido.ENTREGADO) {
                    Button(
                        onClick = { onUpdateStatus(EstadoPedido.EN_CAMINO) },
                        enabled = pedido.estado == EstadoPedido.EN_PREPARACION
                    ) {
                        Text("En Camino")
                    }
                    Button(
                        onClick = { onUpdateStatus(EstadoPedido.ENTREGADO) },
                        enabled = pedido.estado == EstadoPedido.EN_CAMINO
                    ) {
                        Text("Entregado")
                    }
                } else {
                    Text("Este pedido ya fue completado.")
                }
            }
        }
    }
}