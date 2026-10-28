package com.synagon.app.dominio.modelos

import kotlinx.datetime.LocalDateTime

/**
 * Representa una de las dos fechas propuestas por el capitán local para disputar el enfrentamiento.
 *
 * @property id Identificador único de la propuesta.
 * @property idEnfrentamiento Identificador del enfrentamiento al que pertenece la propuesta.
 * @property orden Número de orden de la propuesta (1 o 2).
 * @property fechaHora Fecha y hora sugeridas para el enfrentamiento.
 * @property pistaLugar Instalación o pistas sugeridas opcionales.
 * @property esSeleccionada Indica si ha sido elegida como fecha definitiva.
 * @property notas Aclaraciones opcionales sobre la propuesta.
 */
data class PropuestaFecha(
    val id: String,
    val idEnfrentamiento: String,
    val orden: Int,
    val fechaHora: LocalDateTime,
    val pistaLugar: String? = null,
    val esSeleccionada: Boolean = false,
    val notas: String? = null,
)
