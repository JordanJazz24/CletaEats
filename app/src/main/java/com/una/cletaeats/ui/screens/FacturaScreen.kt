package com.una.cletaeats.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.una.cletaeats.data.model.Pedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaScreen(pedido: Pedido, onVolver: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Factura del Pedido") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Factura para: ${pedido.cliente.nombre}", style = MaterialTheme.typography.titleLarge)
            Text("Cédula: ${pedido.cliente.cedula}")
            Text("Pedido ID: ${pedido.id}")
            Text("Fecha: ${pedido.fechaHoraPedido}")
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Detalle de la Compra:", style = MaterialTheme.typography.titleMedium)
            // Detalle de los combos
            pedido.combos.forEach { combo ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("- ${combo.nombre}")
                    Text("₡${combo.precio}")
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Resumen de Pago:", style = MaterialTheme.typography.titleMedium)
            // Detalle de costos
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sub-total:")
                Text("₡${pedido.subtotal}")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Costo de Envío:")
                Text("₡${pedido.costoTransporte}")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("IVA (13%):")
                Text("₡${String.format("%.2f", pedido.iva)}")
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Monto Total
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("MONTO TOTAL:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("₡${pedido.total}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
            Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}