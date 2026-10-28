package com.synagon.app.dominio.modelos

/**
 * Estadísticas acumuladas de rendimiento individual de un miembro del equipo en la temporada.
 * Incluye las 8 métricas obligatorias de Synagon:
 * 1. Partidos Apuntados (compromiso en convocatorias/disponibilidad)
 * 2. Partidos Jugados
 * 3. Partidos Ganados
 * 4. Partidos Perdidos
 * 5. Sets Ganados
 * 6. Sets Perdidos
 * 7. Juegos Ganados
 * 8. Juegos Perdidos
 */
data class EstadisticaJugador(
    val idJugador: String,
    val nombreJugador: String,
    val partidosApuntados: Int = 0,
    val partidosJugados: Int,
    val partidosGanados: Int,
    val partidosPerdidos: Int,
    val setsGanados: Int,
    val setsPerdidos: Int,
    val juegosGanados: Int,
    val juegosPerdidos: Int,
    val puntos: Int = 0,
    val esCapitan: Boolean = false,
) {
    val diferenciaSets: Int
        get() = setsGanados - setsPerdidos

    val diferenciaJuegos: Int
        get() = juegosGanados - juegosPerdidos

    val porcentajeVictoriasPartidos: Int
        get() = if (partidosJugados > 0) ((partidosGanados * 100.0) / partidosJugados).toInt() else 0

    val porcentajeCompromiso: Int
        get() = if (partidosApuntados > 0 && partidosJugados <= partidosApuntados) {
            ((partidosJugados * 100.0) / partidosApuntados).toInt()
        } else {
            100
        }
}
