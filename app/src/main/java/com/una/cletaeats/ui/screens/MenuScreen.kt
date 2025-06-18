package com.una.cletaeats.ui.screens

import android.widget.Toast
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
import com.una.cletaeats.data.model.Combo
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: OrderViewModel, // 1. AHORA SÍ, RECIBE EL VIEWMODEL
    cliente: Cliente,
    restaurante: Restaurante,
    onVolver: () -> Unit,
    onPedidoExitoso: () -> Unit
) {
    // 2. YA NO SE CREA NINGÚN VIEWMODEL AQUÍ DENTRO

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Efecto para reaccionar al resultado del pedido
    LaunchedEffect(uiState.pedidoResult) {
        if (uiState.pedidoResult != null) {
            Toast.makeText(context, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
            viewModel.limpiarEstadoPedido()
            onPedidoExitoso()
        }
    }
    // Efecto para reaccionar a errores (ej: no hay repartidores)
    LaunchedEffect(uiState.errorPedido) {
        if (uiState.errorPedido != null) {
            Toast.makeText(context, uiState.errorPedido, Toast.LENGTH_LONG).show()
            viewModel.limpiarEstadoPedido() // Limpia el error para no volver a mostrarlo
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Selecciona tus Combos") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.catalogoCombos) { combo ->
                    ComboItem(combo = combo, onAdd = { viewModel.agregarComboAlPedido(combo) })
                }
            }
            // El botón ahora llama directamente al viewModel
            OrderSummary(
                combos = uiState.combosEnPedido,
                subtotal = uiState.subtotal,
                iva = uiState.iva,
                total = uiState.total,
                onRemove = { combo -> viewModel.removerComboDelPedido(combo) },
                onPlaceOrder = { viewModel.realizarPedido(cliente, restaurante) }
            )
        }
    }
}

// El código para ComboItem y OrderSummary no necesita ningún cambio.
@Composable
fun ComboItem(combo: Combo, onAdd: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(combo.nombre, fontWeight = FontWeight.Bold)
                Text("₡${combo.precio}")
            }
            Button(onClick = onAdd) { Text("Agregar") }
        }
    }
}

@Composable
fun OrderSummary(
    combos: List<Combo>,
    subtotal: Double,
    iva: Double,
    total: Double,
    onRemove: (Combo) -> Unit,
    onPlaceOrder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen del Pedido", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            combos.forEach { combo ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(combo.nombre)
                    TextButton(onClick = { onRemove(combo) }) { Text("Quitar") }
                }
            }
            Divider(Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal:")
                Text("₡$subtotal")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("IVA (13%):")
                Text("₡$iva")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", fontWeight = FontWeight.Bold)
                Text("₡$total", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onPlaceOrder,
                modifier = Modifier.fillMaxWidth(),
                enabled = combos.isNotEmpty()
            ) {
                Text("Realizar Pedido")
            }
        }
    }
}