package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO serializable para la propuesta de fecha del enfrentamiento recibida o enviada a ControladorJornada.
 */
@Serializable
data class PropuestaFechaDto(
    @SerialName("id")
    val id: String,
    @SerialName("enfrentamientoId")
    val enfrentamientoId: String = "",
    @SerialName("fechaPropuesta")
    val fechaPropuesta: String,
    @SerialName("orden")
    val orden: Int,
    @SerialName("esSeleccionada")
    val esSeleccionada: Boolean = false,
    @SerialName("pistaLugar")
    val pistaLugar: String? = null,
    @SerialName("notas")
    val notas: String? = null,
)
