package com.synagon.app.dominio.modelos

/**
 * Representa una de las tres parejas alineadas en un enfrentamiento (Pareja 1, Pareja 2 o Pareja 3)
 * junto con su partido asociado.
 *
 * @property id Identificador único del partido de pareja.
 * @property numeroPareja Orden de la pareja en el acta (1, 2 o 3).
 * @property idJugadorUno Identificador del primer jugador convocado.
 * @property nombreJugadorUno Nombre completo del primer jugador convocado.
 * @property idJugadorDos Identificador del segundo jugador convocado.
 * @property nombreJugadorDos Nombre completo del segundo jugador convocado.
 * @property idJugadorRivalUno Identificador del primer jugador rival (opcional si aún no hay acta rival).
 * @property nombreJugadorRivalUno Nombre del primer jugador rival.
 * @property idJugadorRivalDos Identificador del segundo jugador rival.
 * @property nombreJugadorRivalDos Nombre del segundo jugador rival.
 * @property sets Lista ordenada de sets disputados en este partido.
 * @property completado Indica si el partido de esta pareja ha concluido.
 */
data class ParejaPartido(
    val id: String,
    val numeroPareja: Int,
    val idJugadorUno: String,
    val nombreJugadorUno: String,
    val idJugadorDos: String,
    val nombreJugadorDos: String,
    val idJugadorRivalUno: String? = null,
    val nombreJugadorRivalUno: String? = null,
    val idJugadorRivalDos: String? = null,
    val nombreJugadorRivalDos: String? = null,
    val sets: List<ResultadoSet> = emptyList(),
    val completado: Boolean = false,
) {
    val nombreCompletoPareja: String
        get() = "$nombreJugadorUno / $nombreJugadorDos"
}
