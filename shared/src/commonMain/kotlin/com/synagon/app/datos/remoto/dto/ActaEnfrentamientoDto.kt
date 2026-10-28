package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO serializable con el tanteo de sets de una pareja dentro del acta arbitral.
 */
@Serializable
data class ResultadoParejaActaDto(
    @SerialName("numeroPareja")
    val numeroPareja: Int,
    @SerialName("jugadorLocal1Id")
    val jugadorLocal1Id: String? = null,
    @SerialName("jugadorLocal2Id")
    val jugadorLocal2Id: String? = null,
    @SerialName("jugadorVisitante1Id")
    val jugadorVisitante1Id: String? = null,
    @SerialName("jugadorVisitante2Id")
    val jugadorVisitante2Id: String? = null,
    @SerialName("set1Local")
    val set1Local: Int,
    @SerialName("set1Visitante")
    val set1Visitante: Int,
    @SerialName("set2Local")
    val set2Local: Int,
    @SerialName("set2Visitante")
    val set2Visitante: Int,
    @SerialName("set3Local")
    val set3Local: Int? = null,
    @SerialName("set3Visitante")
    val set3Visitante: Int? = null,
)

/**
 * Cuerpo JSON enviado al backend para confirmar y subir el acta del enfrentamiento.
 */
@Serializable
data class PeticionSubirActaDto(
    @SerialName("fotoActaUrl")
    val fotoActaUrl: String,
    @SerialName("observaciones")
    val observaciones: String? = null,
    @SerialName("partidos")
    val partidos: List<ResultadoParejaActaDto>,
)
