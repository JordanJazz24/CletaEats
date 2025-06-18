package com.una.cletaeats.data.repository

import android.content.Context
import com.una.cletaeats.data.model.Cliente
import com.una.cletaeats.data.model.EstadoCliente
import java.io.File
import java.io.IOException

// Necesitamos el contexto para poder acceder al directorio de archivos de la app
class ClienteDao(private val context: Context) {

    private val fileName = "clientes.txt"
    private val file: File = File(context.filesDir, fileName)

    // Función para convertir un objeto Cliente a una línea de CSV
    private fun clienteToCsv(cliente: Cliente): String {
        return "${cliente.cedula},${cliente.nombre},${cliente.direccion},${cliente.telefono},${cliente.numeroTarjeta},${cliente.estado},${cliente.correo},${cliente.password}\n"
    }

    // Función para convertir una línea de CSV a un objeto Cliente
    private fun csvToCliente(csv: String): Cliente {
        val fields = csv.split(",")
        return Cliente(
            cedula = fields[0],
            nombre = fields[1],
            direccion = fields[2],
            telefono = fields[3],
            numeroTarjeta = fields[4],
            estado = EstadoCliente.valueOf(fields[5]),
            correo = fields[6],
            password = fields[7]
        )
    }

    // Función para agregar un nuevo cliente al archivo
    fun agregarCliente(cliente: Cliente) {
        try {
            // "appendText" abre el archivo y permite añadir contenido al final
            file.appendText(clienteToCsv(cliente))
        } catch (e: IOException) {
            e.printStackTrace()
            // Aquí podríamos manejar el error, por ejemplo, con un log
        }
    }

    // Función para leer todos los clientes del archivo
    fun obtenerTodosLosClientes(): List<Cliente> {
        if (!file.exists()) {
            return emptyList() // Si el archivo no existe, no hay clientes
        }
        try {
            // "readLines" lee todas las líneas del archivo a una lista de Strings
            return file.readLines().map { csvToCliente(it) }
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    // OTRAS FUNCIONES NECESARIAS:
    // - fun actualizarCliente(...) { ... }
    // - fun eliminarCliente(...) { ... }
    // Estas son más complejas porque requieren leer todo, modificar la lista y reescribir el archivo completo.
}