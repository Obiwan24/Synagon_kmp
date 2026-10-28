package com.synagon.app.dominio.modelos

/**
 * Identifica al bando ganador de un partido de pareja tras validar sus sets.
 */
enum class GanadorPartido(val etiqueta: String) {
    LOCAL("Equipo Local"),
    VISITANTE("Equipo Visitante"),
}

/**
 * Representa el tanteo por sets introducido en el acta para una de las 3 parejas del enfrentamiento.
 */
data class ResultadoParejaActa(
    val numeroPareja: Int,
    val nombreParejaLocal: String,
    val nombreParejaVisitante: String,
    val idJugadorLocalUno: String? = null,
    val idJugadorLocalDos: String? = null,
    val idJugadorVisitanteUno: String? = null,
    val idJugadorVisitanteDos: String? = null,
    val set1Local: Int,
    val set1Visitante: Int,
    val set2Local: Int,
    val set2Visitante: Int,
    val set3Local: Int? = null,
    val set3Visitante: Int? = null,
)

/**
 * Modelo de dominio inmutable que representa el envío oficial del acta arbitral de un enfrentamiento.
 */
data class ActaEnfrentamiento(
    val idEnfrentamiento: String,
    val urlFotoActa: String,
    val observaciones: String? = null,
    val resultadosParejas: List<ResultadoParejaActa>,
)
