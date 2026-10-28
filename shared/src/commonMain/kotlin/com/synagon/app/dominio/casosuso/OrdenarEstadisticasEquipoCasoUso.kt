package com.synagon.app.dominio.casosuso

import com.synagon.app.dominio.modelos.EstadisticaJugador

/**
 * Criterios de ordenación disponibles para el ranking interno de estadísticas del equipo.
 */
enum class CriterioOrdenEstadisticas(val etiqueta: String) {
    VICTORIAS("Victorias"),
    PARTIDOS_JUGADOS("Partidos Jugados"),
    COMPROMISO("Compromiso (Apuntados)"),
    DIFERENCIA_JUEGOS("Diferencia de Juegos"),
}

/**
 * Caso de uso encargado de ordenar la plantilla del equipo según el criterio seleccionado
 * aplicando desempates coherentes de competición.
 */
class OrdenarEstadisticasEquipoCasoUso {

    fun ejecutar(
        estadisticas: List<EstadisticaJugador>,
        criterio: CriterioOrdenEstadisticas,
    ): List<EstadisticaJugador> {
        val comparador = when (criterio) {
            CriterioOrdenEstadisticas.VICTORIAS -> compareByDescending<EstadisticaJugador> { it.partidosGanados }
                .thenByDescending { it.diferenciaSets }
                .thenByDescending { it.diferenciaJuegos }
                .thenBy { it.nombreJugador }

            CriterioOrdenEstadisticas.PARTIDOS_JUGADOS -> compareByDescending<EstadisticaJugador> { it.partidosJugados }
                .thenByDescending { it.partidosGanados }
                .thenByDescending { it.diferenciaJuegos }
                .thenBy { it.nombreJugador }

            CriterioOrdenEstadisticas.COMPROMISO -> compareByDescending<EstadisticaJugador> { it.partidosApuntados }
                .thenByDescending { it.partidosJugados }
                .thenByDescending { it.partidosGanados }
                .thenBy { it.nombreJugador }

            CriterioOrdenEstadisticas.DIFERENCIA_JUEGOS -> compareByDescending<EstadisticaJugador> { it.diferenciaJuegos }
                .thenByDescending { it.partidosGanados }
                .thenByDescending { it.diferenciaSets }
                .thenBy { it.nombreJugador }
        }
        return estadisticas.sortedWith(comparador)
    }
}
