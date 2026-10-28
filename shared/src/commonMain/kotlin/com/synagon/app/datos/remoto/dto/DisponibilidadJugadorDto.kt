package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO serializable que representa el voto de disponibilidad de un jugador en el backend.
 */
@Serializable
data class DisponibilidadJugadorDto(
    @SerialName("id")
    val id: String,
    @SerialName("enfrentamientoId")
    val enfrentamientoId: String = "",
    @SerialName("propuestaFechaId")
    val propuestaFechaId: String? = null,
    @SerialName("jugadorId")
    val jugadorId: String,
    @SerialName("nombreJugador")
    val nombreJugador: String = "",
    @SerialName("estado")
    val estado: String,
    @SerialName("esDisponibilidadFinal")
    val esDisponibilidadFinal: Boolean = false,
)
