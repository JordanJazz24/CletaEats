package com.una.cletaeats.ui.screens

import androidx.compose.material.icons.filled.Assessment
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.repository.UsuarioRepository
import com.una.cletaeats.viewmodel.CletaEatsViewModelFactory
import com.una.cletaeats.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

// Imports de Material 3
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen( viewModel: HomeViewModel, // Se recibe el viewModel como parámetro
                onLogout: () -> Unit,
                onRestaurantClick: (Restaurante) -> Unit,
                onMisPedidosClick: () -> Unit) {

    // El estado de la UI que contiene la lista de restaurantes
    val uiState by viewModel.uiState.collectAsState()
    Log.d("HomeScreenDebug", "HomeScreen se está recomponiendo con una lista de ${uiState.restaurantes.size} restaurantes.")
    // Estado para controlar si el NavDrawer está abierto o cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Usamos ModalNavigationDrawer como el contenedor principal de la pantalla
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Este es el contenido del menú lateral (el Drawer)
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Restaurantes") },
                    label = { Text("Restaurantes") },
                    selected = true, // Marcamos este como seleccionado
                    onClick = { scope.launch { drawerState.close() } } // Cierra el drawer
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Mi Perfil") },
                    label = { Text("Mi Perfil") },
                    selected = false,
                    onClick = { /* TODO: Navegar a la pantalla de perfil */ }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Mis Pedidos") }, // Necesitarás el import de ListAlt
                    label = { Text("Mis Pedidos") },
                    selected = false,
                    onClick = onMisPedidosClick // <-- USA EL NUEVO CALLBACK
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión") },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = { onLogout() } // Llama a la función de logout
                )
            }
        }
    ) {
        // Este es el contenido principal de la pantalla, a la derecha del Drawer
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Restaurantes Disponibles") },
                    // Ícono de menú para abrir el NavDrawer
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Si está cargando, muestra un indicador de progreso
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Si ya cargó, muestra la lista de restaurantes
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.restaurantes) { restaurante ->
                        RestaurantItem(restaurante = restaurante,
                            onClick = {
                                // TODO: Navegar a la pantalla del menú de este restaurante
                                // Pasaremos la cédula jurídica como identificador
                                onRestaurantClick(restaurante) // Llama al nuevo callback
                            }
                        )
                    }
                }
            }
        }
    }
}

// Composable personalizado para cada elemento de la lista de restaurantes
@Composable
fun RestaurantItem(restaurante: Restaurante, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = restaurante.nombre, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            // Requisito: mostrar el tipo de comida
            Text(text = "Tipo de comida: ${restaurante.tipoComida.name}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}