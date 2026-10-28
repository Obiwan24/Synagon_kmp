package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO serializable para cada pareja alineada en la jornada (Pareja 1, Pareja 2 y Pareja 3) y sus resultados.
 */
@Serializable
data class ParejaPartidoDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("enfrentamientoId")
    val enfrentamientoId: String = "",
    @SerialName("numeroPareja")
    val numeroPareja: Int,
    @SerialName("jugadorLocal1Id")
    val jugadorLocal1Id: String? = null,
    @SerialName("nombreJugadorLocal1")
    val nombreJugadorLocal1: String = "",
    @SerialName("jugadorLocal2Id")
    val jugadorLocal2Id: String? = null,
    @SerialName("nombreJugadorLocal2")
    val nombreJugadorLocal2: String = "",
    @SerialName("jugadorVisitante1Id")
    val jugadorVisitante1Id: String? = null,
    @SerialName("nombreJugadorVisitante1")
    val nombreJugadorVisitante1: String? = null,
    @SerialName("jugadorVisitante2Id")
    val jugadorVisitante2Id: String? = null,
    @SerialName("nombreJugadorVisitante2")
    val nombreJugadorVisitante2: String? = null,
    @SerialName("set1Local")
    val set1Local: Int? = null,
    @SerialName("set1Visitante")
    val set1Visitante: Int? = null,
    @SerialName("set2Local")
    val set2Local: Int? = null,
    @SerialName("set2Visitante")
    val set2Visitante: Int? = null,
    @SerialName("set3Local")
    val set3Local: Int? = null,
    @SerialName("set3Visitante")
    val set3Visitante: Int? = null,
    @SerialName("completado")
    val completado: Boolean = false,
)
