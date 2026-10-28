package com.synagon.app.dominio.modelos

/**
 * Representa el resultado numérico de un set disputado en un partido.
 *
 * @property numeroSet Orden del set (1, 2, 3, etc.).
 * @property juegosLocal Cantidad de juegos ganados por la pareja o equipo local.
 * @property juegosVisitante Cantidad de juegos ganados por la pareja o equipo visitante.
 * @property puntosTieBreakLocal Puntos logrados en el desempate (tie-break) por el local, si existió.
 * @property puntosTieBreakVisitante Puntos logrados en el desempate (tie-break) por el visitante, si existió.
 */
data class ResultadoSet(
    val numeroSet: Int,
    val juegosLocal: Int,
    val juegosVisitante: Int,
    val puntosTieBreakLocal: Int? = null,
    val puntosTieBreakVisitante: Int? = null,
) {
    val esTieBreak: Boolean
        get() = (puntosTieBreakLocal != null) || (puntosTieBreakVisitante != null)

    val ganadorLocal: Boolean
        get() = juegosLocal > juegosVisitante

    val ganadorVisitante: Boolean
        get() = juegosVisitante > juegosLocal
}
