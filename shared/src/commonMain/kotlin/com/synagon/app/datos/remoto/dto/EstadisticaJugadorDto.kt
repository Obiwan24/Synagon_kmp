package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO serializable con las estadísticas de rendimiento y compromiso de un jugador del equipo.
 */
@Serializable
data class EstadisticaJugadorDto(
    @SerialName("jugadorId")
    val jugadorId: String,
    @SerialName("nombreJugador")
    val nombreJugador: String = "",
    @SerialName("partidosApuntados")
    val partidosApuntados: Int = 0,
    @SerialName("partidosJugados")
    val partidosJugados: Int = 0,
    @SerialName("partidosGanados")
    val partidosGanados: Int = 0,
    @SerialName("partidosPerdidos")
    val partidosPerdidos: Int = 0,
    @SerialName("setsGanados")
    val setsGanados: Int = 0,
    @SerialName("setsPerdidos")
    val setsPerdidos: Int = 0,
    @SerialName("juegosGanados")
    val juegosGanados: Int = 0,
    @SerialName("juegosPerdidos")
    val juegosPerdidos: Int = 0,
    @SerialName("puntos")
    val puntos: Int = 0,
    @SerialName("esCapitan")
    val esCapitan: Boolean = false,
)
