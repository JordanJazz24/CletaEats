package com.una.cletaeats.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.model.EstadoPedido
import com.una.cletaeats.data.model.Pedido
import com.una.cletaeats.viewmodel.CalificacionViewModel
import com.una.cletaeats.viewmodel.MisPedidosViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    misPedidosViewModel: MisPedidosViewModel, // Cambiado para claridad
    calificacionViewModel: CalificacionViewModel, // Nuevo ViewModel para la calificación
    cliente: Cliente,
    onPedidoClick: (Pedido) -> Unit,
    onVolver: () -> Unit
) {
    val uiState by misPedidosViewModel.uiState.collectAsState()
    val calificacionUiState by calificacionViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estado para controlar el diálogo de calificación
    var showRatingDialog by remember { mutableStateOf(false) }
    var pedidoParaCalificar by remember { mutableStateOf<Pedido?>(null) }


    LaunchedEffect(key1 = cliente.cedula) {
        misPedidosViewModel.cargarPedidosDelCliente(cliente.cedula)
    }

    // Efecto para reaccionar a una calificación exitosa
    LaunchedEffect(key1 = calificacionUiState.calificacionExitosa) {
        if (calificacionUiState.calificacionExitosa) {
            Toast.makeText(context, "Gracias por tu calificación.", Toast.LENGTH_SHORT).show()
            showRatingDialog = false // Cierra el diálogo
            misPedidosViewModel.cargarPedidosDelCliente(cliente.cedula) // Refresca la lista de pedidos
            calificacionViewModel.resetearEstado() // Resetea el estado en el ViewModel
        }
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
                // ... (Las secciones stickyHeader se mantienen igual)
                items(uiState.pedidosPendientes) { pedido ->
                    PedidoItemCliente(pedido = pedido, onPedidoClick = onPedidoClick)
                }
                items(uiState.pedidosCompletados) { pedido ->
                    PedidoItemCliente(
                        pedido = pedido,
                        onPedidoClick = onPedidoClick,
                        onCalificarClick = {
                            pedidoParaCalificar = it
                            showRatingDialog = true
                        }
                    )
                }
            }
        }
    }

    // Si showRatingDialog es true, mostramos nuestro diálogo
    if (showRatingDialog && pedidoParaCalificar != null) {
        RatingDialog(
            pedido = pedidoParaCalificar!!,
            viewModel = calificacionViewModel,
            onDismiss = { showRatingDialog = false }
        )
    }
}

@Composable
fun PedidoItemCliente(
    pedido: Pedido,
    onPedidoClick: (Pedido) -> Unit,
    onCalificarClick: ((Pedido) -> Unit)? = null // Hacemos el callback opcional
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onPedidoClick(pedido) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Pedido a: ${pedido.restaurante.nombre}", fontWeight = FontWeight.Bold)
            Text("Fecha: ${pedido.fechaHoraPedido}")
            Text("Estado: ${pedido.estado}", color = MaterialTheme.colorScheme.primary)
            Text("Total: ₡${pedido.total}", fontWeight = FontWeight.SemiBold)

            // Mostramos el botón solo si el pedido está ENTREGADO y NO ha sido calificado
            if (pedido.estado == EstadoPedido.ENTREGADO && !pedido.calificado && onCalificarClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onCalificarClick(pedido) }, modifier = Modifier.align(Alignment.End)) {
                    Text("Calificar Entrega")
                }
            }
        }
    }
}

@Composable
fun RatingDialog(
    pedido: Pedido,
    viewModel: CalificacionViewModel,
    onDismiss: () -> Unit
) {
    var showQuejaInput by remember { mutableStateOf(false) }
    var quejaText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Calificar Entrega") },
        text = {
            Column {
                Text("Por favor, califica el servicio del repartidor: ${pedido.repartidor?.nombre}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        viewModel.procesarCalificacionPositiva(pedido)
                    }) { Text("Positivo") }

                    Button(
                        onClick = { showQuejaInput = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Negativo") }
                }

                if (showQuejaInput) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = quejaText,
                        onValueChange = { quejaText = it },
                        label = { Text("¿Cuál fue el problema? (Queja)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (showQuejaInput) {
                Button(
                    onClick = {
                        if (quejaText.isNotBlank()) {
                            viewModel.procesarCalificacionNegativa(pedido, quejaText)
                        }
                    },
                    enabled = quejaText.isNotBlank()
                ) {
                    Text("Enviar Queja")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}