package com.una.cletaeats.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.una.cletaeats.viewmodel.ReportesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Módulo de Reportes CletaEats") }) }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Columna de Botones
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Listados Generales:", style = MaterialTheme.typography.titleSmall)
                Button(onClick = { viewModel.generarReporteClientesActivos() }) { Text("Clientes Activos") }
                Button(onClick = { viewModel.generarReporteClientesSuspendidos() }) { Text("Clientes Suspendidos") }
                Button(onClick = { viewModel.generarReporteRepartidoresSinAmonestaciones() }) { Text("Repartidores Top") }
                Button(onClick = { viewModel.generarReporteRestaurantes() }) { Text("Listado Restaurantes") }
                Button(onClick = { viewModel.generarReporteQuejasPorRepartidor() }) { Text("Quejas por Repartidor") }
                Button(onClick = { viewModel.generarReportePedidosPorCliente() }) { Text("Pedidos por Cliente") }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Análisis de Ventas:", style = MaterialTheme.typography.titleSmall)
                Button(onClick = { viewModel.generarReporteRestauranteMasPedidos() }) { Text("Rest. con Más Pedidos") }
                Button(onClick = { viewModel.generarReporteRestauranteMenosPedidos() }) { Text("Rest. con Menos Pedidos") }
                Button(onClick = { viewModel.generarReporteClienteConMasPedidos() }) { Text("Cliente con Más Pedidos") }
                Button(onClick = { viewModel.generarReporteMontoVendidoPorRestaurante() }) { Text("Ventas por Restaurante") }
                Button(onClick = { viewModel.generarReporteMontoTotalVendido() }) { Text("Venta Total") }
                Button(onClick = { viewModel.generarReporteHoraPico() }) { Text("Análisis Hora Pico") }

                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onVolver) { Text("Cerrar Sesión") }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Columna de Resultados
            Column(modifier = Modifier.weight(2f)) {
                Text(uiState.tituloResultado, style = MaterialTheme.typography.titleMedium)
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    LazyColumn {
                        items(uiState.resultados) { resultadoString ->
                            Text(resultadoString, modifier = Modifier.padding(bottom = 4.dp))
                        }
                    }
                }
            }
        }
    }
}