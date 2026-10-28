package com.synagon.app.dominio.modelos

/**
 * Estados posibles en la votación de disponibilidad de un jugador para una fecha propuesta.
 */
enum class EstadoDisponibilidad(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    DUDA("Duda"),
    NO_DISPONIBLE("No disponible"),
}
