package com.una.cletaeats.data.repository

import android.content.Context
import com.una.cletaeats.data.model.Restaurante
import com.una.cletaeats.data.model.TipoComida
import java.io.File
import java.io.IOException

class RestauranteDao(private val context: Context) {

    private val fileName = "restaurantes.txt"
    private val file: File = File(context.filesDir, fileName)

    // Convierte un objeto Restaurante a una línea de texto CSV
    private fun restauranteToCsv(restaurante: Restaurante): String {
        return "${restaurante.cedulaJuridica},${restaurante.nombre},${restaurante.direccion},${restaurante.telefono},${restaurante.tipoComida},${restaurante.calificacionPromedio},${restaurante.cantidadPedidos},${restaurante.correo},${restaurante.password}\n"
    }

    // Convierte una línea de texto CSV a un objeto Restaurante
    private fun csvToRestaurante(csv: String): Restaurante {
        val fields = csv.split(",")
        return Restaurante(
            cedulaJuridica = fields[0],
            nombre = fields[1],
            direccion = fields[2],
            telefono = fields[3],
            tipoComida = TipoComida.valueOf(fields[4]),
            calificacionPromedio = fields[5].toDouble(),
            cantidadPedidos = fields[6].toInt(),
            correo = fields[7],
            password = fields[8]
        )
    }

    // Agrega un nuevo restaurante al archivo
    fun agregarRestaurante(restaurante: Restaurante) {
        try {
            file.appendText(restauranteToCsv(restaurante))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Lee todos los restaurantes del archivo
    fun obtenerTodosLosRestaurantes(): List<Restaurante> {
        if (!file.exists()) {
            return emptyList()
        }
        try {
            return file.readLines().filter { it.isNotBlank() }.map { csvToRestaurante(it) }
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }
}