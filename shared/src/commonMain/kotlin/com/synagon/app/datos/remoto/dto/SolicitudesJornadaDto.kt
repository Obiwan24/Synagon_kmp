package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO de un miembro de la plantilla del equipo.
 */
@Serializable
data class JugadorEquipoDto(
    @SerialName("id")
    val id: String,
    @SerialName("nombreCompleto")
    val nombreCompleto: String,
    @SerialName("esCapitan")
    val esCapitan: Boolean = false,
)

/**
 * Cuerpo JSON para registrar el voto de disponibilidad de un jugador en ControladorJornada.
 */
@Serializable
data class PeticionVotarDisponibilidadDto(
    @SerialName("jugadorId")
    val jugadorId: String,
    @SerialName("propuestaFechaId")
    val propuestaFechaId: String,
    @SerialName("tipoDisponibilidad")
    val tipoDisponibilidad: String,
)

/**
 * Cuerpo JSON para que el capitán fije la fecha definitiva del enfrentamiento.
 */
@Serializable
data class PeticionConfirmarFechaDto(
    @SerialName("propuestaFechaElegidaId")
    val propuestaFechaElegidaId: String,
    @SerialName("pistas")
    val pistas: String? = null,
)

/**
 * Pareja individual convocada enviada en [PeticionConfirmarConvocatoriaDto].
 */
@Serializable
data class PeticionParejaAlineadaDto(
    @SerialName("numeroPareja")
    val numeroPareja: Int,
    @SerialName("jugador1Id")
    val jugador1Id: String,
    @SerialName("jugador2Id")
    val jugador2Id: String,
)

/**
 * Cuerpo JSON para cerrar la convocatoria de los 6 convocados en Pareja 1, Pareja 2 y Pareja 3.
 */
@Serializable
data class PeticionConfirmarConvocatoriaDto(
    @SerialName("parejas")
    val parejas: List<PeticionParejaAlineadaDto>,
)
