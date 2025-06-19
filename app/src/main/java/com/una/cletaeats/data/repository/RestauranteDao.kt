// En RestauranteDao.kt
package com.una.cletaeats.data.repository

import android.content.Context
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.model.TipoComida
import java.io.File
import java.io.IOException

class RestauranteDao(private val context: Context) {

    private val fileName = "restaurantes.txt"
    private val file: File = File(context.filesDir, fileName)

    // Convierte el mapa del menú a un String para guardarlo
    private fun menuToString(menu: Map<Int, String>): String {
        return menu.map { "${it.key}:${it.value}" }.joinToString(";")
    }

    // Convierte el String del menú de vuelta a un Mapa
    private fun stringToMenu(menuString: String): MutableMap<Int, String> {
        if (menuString.isBlank()) return mutableMapOf()
        return menuString.split(";")
            .mapNotNull {
                val parts = it.split(":", limit = 2)
                if (parts.size == 2) parts[0].toInt() to parts[1] else null
            }
            .toMap()
            .toMutableMap()
    }

    private fun restauranteToCsv(restaurante: Restaurante): String {
        // Envolvemos el menú en comillas para manejar cualquier caracter especial
        val menuCsv = "\"${menuToString(restaurante.menu)}\""
        return "${restaurante.cedulaJuridica},${restaurante.nombre},${restaurante.direccion},${restaurante.telefono},${restaurante.tipoComida},${restaurante.calificacionPromedio},${restaurante.cantidadPedidos},${menuCsv},${restaurante.correo},${restaurante.password}\n"
    }

    private fun csvToRestaurante(csv: String): Restaurante {
        val fields = csv.split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).map { it.removeSurrounding("\"") }
        return Restaurante(
            cedulaJuridica = fields[0],
            nombre = fields[1],
            direccion = fields[2],
            telefono = fields[3],
            tipoComida = TipoComida.valueOf(fields[4]),
            calificacionPromedio = fields[5].toDouble(),
            cantidadPedidos = fields[6].toInt(),
            menu = stringToMenu(fields[7]), // Leemos el menú
            correo = fields[8],
            password = fields[9]
        )
    }

    fun agregarRestaurante(restaurante: Restaurante) {
        try { file.appendText(restauranteToCsv(restaurante)) }
        catch (e: IOException) { e.printStackTrace() }
    }

    fun obtenerTodosLosRestaurantes(): List<Restaurante> {
        if (!file.exists()) return emptyList()
        try {
            return file.readLines().filter { it.isNotBlank() }.map { csvToRestaurante(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    // NUEVA FUNCIÓN para actualizar un restaurante (la necesitaremos para guardar el menú)
    fun actualizarRestaurante(restauranteActualizado: Restaurante) {
        try {
            val todosLosRestaurantes = obtenerTodosLosRestaurantes().toMutableList()
            val index = todosLosRestaurantes.indexOfFirst { it.cedulaJuridica == restauranteActualizado.cedulaJuridica }
            if (index != -1) {
                todosLosRestaurantes[index] = restauranteActualizado
                file.writeText(todosLosRestaurantes.joinToString(separator = "") { restauranteToCsv(it) })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}