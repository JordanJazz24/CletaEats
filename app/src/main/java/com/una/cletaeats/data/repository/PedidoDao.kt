package com.una.cletaeats.data.repository

import android.content.Context
import com.una.cletaeats.data.model.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PedidoDao(private val context: Context) {

    private val fileName = "pedidos.txt"
    private val file: File = File(context.filesDir, fileName)

    // Para esta operación, el PedidoDao necesita poder encontrar usuarios por su ID.
    // Le pasamos el repositorio de usuarios para que pueda hacerlo.
    private val usuarioRepository = UsuarioRepository(context)

    // Convierte un objeto Pedido a una línea de texto CSV
    private fun pedidoToCsv(pedido: Pedido): String {
        // Guardamos solo los números de los combos, separados por ';'
        val comboNumbers = pedido.combos.joinToString(";") { it.numero.toString() }
        // Guardamos el ID del repartidor, o "null" si no hay uno
        val repartidorId = pedido.repartidor?.cedula ?: "null"
        val fechaEntrega = pedido.fechaHoraEntrega ?: "null"

        return "${pedido.id},${pedido.cliente.cedula},${pedido.restaurante.cedulaJuridica},\"${comboNumbers}\",${repartidorId},${pedido.estado},${pedido.fechaHoraPedido},${fechaEntrega}\n"
    }

    // Convierte una línea de texto CSV a un objeto Pedido
    private fun csvToPedido(csv: String): Pedido? {
        try {
            val fields = csv.split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).map { it.removeSurrounding("\"") }
            val clienteId = fields[1]
            val restauranteId = fields[2]
            val repartidorId = fields[4]

            // Usamos el repositorio para encontrar los objetos completos por su ID
            val cliente = usuarioRepository.obtenerClientes().find { it.cedula == clienteId } ?: return null
            val restaurante = usuarioRepository.obtenerRestaurantes().find { it.cedulaJuridica == restauranteId } ?: return null
            val repartidor = if (repartidorId != "null") usuarioRepository.obtenerRepartidores().find { it.cedula == repartidorId } else null

            // Reconstruimos la lista de combos
            val comboNumbers = fields[3].split(";").map { it.toInt() }
            val combos = comboNumbers.map { numero ->
                val precio = 3000.0 + (numero * 1000.0)
                Combo(numero, "Combo No. $numero", precio)
            }

            return Pedido(
                id = fields[0],
                cliente = cliente,
                restaurante = restaurante,
                combos = combos,
                repartidor = repartidor,
                estado = EstadoPedido.valueOf(fields[5]),
                fechaHoraPedido = fields[6],
                fechaHoraEntrega = if (fields[7] != "null") fields[7] else null
            )
        } catch (e: Exception) {
            // Si hay algún error al parsear la línea, la ignoramos
            e.printStackTrace()
            return null
        }
    }

    // Genera un ID único para cada pedido
    fun generarNuevoId(): String {
        return "PEDIDO-" + SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(Date())
    }

    // Agrega un nuevo pedido al archivo
    fun agregarPedido(pedido: Pedido) {
        try {
            file.appendText(pedidoToCsv(pedido))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Lee todos los pedidos del archivo
    fun obtenerTodosLosPedidos(): List<Pedido> {
        if (!file.exists()) {
            return emptyList()
        }
        try {
            // Usamos mapNotNull para descartar cualquier línea que falle al ser parseada
            return file.readLines().filter { it.isNotBlank() }.mapNotNull { csvToPedido(it) }
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    fun actualizarPedido(pedidoActualizado: Pedido) {
        try {
            val todosLosPedidos = obtenerTodosLosPedidos().toMutableList()
            val index = todosLosPedidos.indexOfFirst { it.id == pedidoActualizado.id }

            if (index != -1) {
                todosLosPedidos[index] = pedidoActualizado
                // Reescribimos el archivo completo con la lista actualizada
                file.writeText(todosLosPedidos.joinToString(separator = "") { pedidoToCsv(it) })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}