package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO serializable de una fila en la tabla de clasificación oficial de la división.
 */
@Serializable
data class FilaClasificacionDto(
    @SerialName("posicion")
    val posicion: Int,
    @SerialName("equipoId")
    val equipoId: String,
    @SerialName("nombreEquipo")
    val nombreEquipo: String,
    @SerialName("club")
    val club: String? = null,
    @SerialName("partidosJugados")
    val partidosJugados: Int = 0,
    @SerialName("partidosGanados")
    val partidosGanados: Int = 0,
    @SerialName("partidosEmpatados")
    val partidosEmpatados: Int = 0,
    @SerialName("partidosPerdidos")
    val partidosPerdidos: Int = 0,
    @SerialName("partidosParejaGanados")
    val partidosParejaGanados: Int = 0,
    @SerialName("partidosParejaPerdidos")
    val partidosParejaPerdidos: Int = 0,
    @SerialName("setsAFavor")
    val setsAFavor: Int = 0,
    @SerialName("setsEnContra")
    val setsEnContra: Int = 0,
    @SerialName("juegosAFavor")
    val juegosAFavor: Int = 0,
    @SerialName("juegosEnContra")
    val juegosEnContra: Int = 0,
    @SerialName("diferenciaJuegos")
    val diferenciaJuegos: Int? = null,
    @SerialName("puntos")
    val puntos: Int = 0,
)
