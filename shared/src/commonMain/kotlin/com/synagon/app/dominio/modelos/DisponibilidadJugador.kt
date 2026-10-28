package com.synagon.app.dominio.modelos

/**
 * Representa la disponibilidad comunicada por un jugador para una propuesta de fecha o jornada.
 *
 * @property id Identificador único del registro de disponibilidad.
 * @property idEnfrentamiento Identificador del enfrentamiento asociado.
 * @property idPropuestaFecha Identificador de la propuesta votada (nulo si aplica a fecha definitiva).
 * @property idJugador Identificador del jugador que emite su disponibilidad.
 * @property nombreJugador Nombre visible del jugador.
 * @property estado Estado elegido: DISPONIBLE, DUDA o NO_DISPONIBLE.
 * @property esDisponibilidadFinal Indica si corresponde a la confirmación tras fijar fecha definitiva.
 */
data class DisponibilidadJugador(
    val id: String,
    val idEnfrentamiento: String,
    val idPropuestaFecha: String?,
    val idJugador: String,
    val nombreJugador: String,
    val estado: EstadoDisponibilidad,
    val esDisponibilidadFinal: Boolean = false,
)
