package com.synagon.app.presentacion.pantallas.estadisticas

import com.synagon.app.dominio.casosuso.CriterioOrdenEstadisticas
import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada

/**
 * Estado único e inmutable de la interfaz para la pantalla de estadísticas individuales del equipo.
 */
data class EstadoUi(
    val cargando: Boolean = true,
    val idEquipo: String = DatosSemillaJornada.ID_EQUIPO_PROPIO,
    val nombreEquipo: String = DatosSemillaJornada.NOMBRE_EQUIPO_PROPIO,
    val criterioOrdenSeleccionado: CriterioOrdenEstadisticas = CriterioOrdenEstadisticas.VICTORIAS,
    val estadisticasJugadores: List<EstadisticaJugador> = emptyList(),
    val usandoRespaldoLocal: Boolean = false,
    val mensajeEstadoConexion: String? = null,
) {
    val totalPartidosGanadosEquipo: Int
        get() = estadisticasJugadores.sumOf { it.partidosGanados }

    val mediaCompromisoApuntados: Int
        get() = if (estadisticasJugadores.isNotEmpty()) {
            estadisticasJugadores.sumOf { it.partidosApuntados } / estadisticasJugadores.size
        } else {
            0
        }
}

/**
 * Intenciones del usuario sobre la pantalla de estadísticas del equipo.
 */
sealed interface EventoUi {
    data object RecargarEstadisticas : EventoUi
    data class CambiarCriterioOrden(val criterio: CriterioOrdenEstadisticas) : EventoUi
}

/**
 * Efectos efímeros de un solo uso para la pantalla de estadísticas.
 */
sealed interface EfectoUi {
    data class MostrarMensaje(val texto: String) : EfectoUi
}
