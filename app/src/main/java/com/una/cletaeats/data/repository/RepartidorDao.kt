package com.una.cletaeats.data.repository

import android.content.Context
import android.util.Log
import com.una.cletaeats.data.model.Repartidor
import com.una.cletaeats.data.model.EstadoRepartidor
import java.io.File
import java.io.IOException

class RepartidorDao(private val context: Context) {

    private val fileName = "repartidores.txt"
    private val file: File = File(context.filesDir, fileName)

    // Convierte un objeto Repartidor a una línea de texto CSV
    private fun repartidorToCsv(repartidor: Repartidor): String {
        // Unimos la lista de quejas en un solo string, separado por un carácter que no sea una coma (ej: ';')
        val quejasCsv = repartidor.quejas.joinToString(";")
        return "${repartidor.cedula},${repartidor.nombre},${repartidor.direccion},${repartidor.telefono},${repartidor.numeroTarjeta},${repartidor.estado},${repartidor.distanciaPedido},${repartidor.kmRecorridosDiarios},${repartidor.amonestaciones},${repartidor.costoKmHabiles},${repartidor.costoKmFeriados},\"${quejasCsv}\",${repartidor.correo},${repartidor.password}\n"
    }

    // Convierte una línea de texto CSV a un objeto Repartidor
    private fun csvToRepartidor(csv: String): Repartidor {
        // Usamos una expresión regular para manejar correctamente las quejas que pueden estar entre comillas
        val fields = csv.split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).map { it.removeSurrounding("\"") }

        // Si no hay quejas, la lista estará vacía
        val quejas = if (fields[11].isBlank()) mutableListOf() else fields[11].split(";").toMutableList()

        return Repartidor(
            cedula = fields[0],
            nombre = fields[1],
            direccion = fields[2],
            telefono = fields[3],
            numeroTarjeta = fields[4],
            estado = EstadoRepartidor.valueOf(fields[5]),
            distanciaPedido = fields[6].toDouble(),
            kmRecorridosDiarios = fields[7].toDouble(),
            amonestaciones = fields[8].toInt(),
            costoKmHabiles = fields[9].toInt(),
            costoKmFeriados = fields[10].toInt(),
            quejas = quejas,
            correo = fields[12],
            password = fields[13]
        )
    }

    // Agrega un nuevo repartidor al archivo
    fun agregarRepartidor(repartidor: Repartidor) {
        try {
            Log.d("CletaEatsDebug", "DAO está intentando escribir en el archivo: ${file.absolutePath}") // <-- AÑADE ESTA LÍNEA
            file.appendText(repartidorToCsv(repartidor))
            Log.d("CletaEatsDebug", "DAO: ¡Escritura en archivo exitosa!") // <-- AÑADE ESTA LÍNEA
        } catch (e: IOException) {
            Log.e("CletaEatsDebug", "DAO: ¡ERROR al escribir en el archivo!", e) // <-- AÑADE ESTA LÍNEA (muy importante)
            e.printStackTrace()
        }
    }

    // Lee todos los repartidores del archivo
    fun obtenerTodosLosRepartidores(): List<Repartidor> {
        if (!file.exists()) {
            return emptyList()
        }
        try {
            val lines = file.readLines()
            // Filtramos líneas en blanco para evitar errores al parsear
            return lines.filter { it.isNotBlank() }.map { csvToRepartidor(it) }
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    fun actualizarRepartidor(repartidorActualizado: Repartidor) {
        try {
            val todosLosRepartidores = obtenerTodosLosRepartidores().toMutableList()
            val index = todosLosRepartidores.indexOfFirst { it.cedula == repartidorActualizado.cedula }

            if (index != -1) {
                todosLosRepartidores[index] = repartidorActualizado
                // Reescribimos el archivo completo con la lista actualizada
                file.writeText(todosLosRepartidores.joinToString(separator = "") { repartidorToCsv(it) })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}