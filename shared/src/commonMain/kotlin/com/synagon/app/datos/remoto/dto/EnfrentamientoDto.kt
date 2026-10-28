package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO serializable principal del enfrentamiento devuelto por ControladorJornada en Spring Boot.
 */
@Serializable
data class EnfrentamientoDto(
    @SerialName("id")
    val id: String,
    @SerialName("divisionId")
    val divisionId: String,
    @SerialName("nombreDivision")
    val nombreDivision: String = "1ª División",
    @SerialName("equipoLocalId")
    val equipoLocalId: String,
    @SerialName("nombreEquipoLocal")
    val nombreEquipoLocal: String = "Equipo Local",
    @SerialName("clubLocal")
    val clubLocal: String? = null,
    @SerialName("equipoVisitanteId")
    val equipoVisitanteId: String,
    @SerialName("nombreEquipoVisitante")
    val nombreEquipoVisitante: String = "Equipo Visitante",
    @SerialName("clubVisitante")
    val clubVisitante: String? = null,
    @SerialName("numeroJornada")
    val numeroJornada: Int,
    @SerialName("estado")
    val estado: String,
    @SerialName("fechaDefinitiva")
    val fechaDefinitiva: String? = null,
    @SerialName("pistas")
    val pistas: String? = null,
    @SerialName("puntosLocal")
    val puntosLocal: Int = 0,
    @SerialName("puntosVisitante")
    val puntosVisitante: Int = 0,
    @SerialName("actaConfirmada")
    val actaConfirmada: Boolean = false,
    @SerialName("propuestasFecha")
    val propuestasFecha: List<PropuestaFechaDto> = emptyList(),
    @SerialName("disponibilidades")
    val disponibilidades: List<DisponibilidadJugadorDto> = emptyList(),
    @SerialName("partidos")
    val partidos: List<ParejaPartidoDto> = emptyList(),
    @SerialName("jugadoresEquipo")
    val jugadoresEquipo: List<JugadorEquipoDto> = emptyList(),
    @SerialName("observaciones")
    val observaciones: String? = null,
)
