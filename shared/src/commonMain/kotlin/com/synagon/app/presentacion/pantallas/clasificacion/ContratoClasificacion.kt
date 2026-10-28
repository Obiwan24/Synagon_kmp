package com.synagon.app.presentacion.pantallas.clasificacion

import com.synagon.app.dominio.modelos.FilaClasificacion
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada

/**
 * Estado único e inmutable de la interfaz para [PantallaClasificacionLiga].
 */
data class EstadoUi(
    val cargando: Boolean = true,
    val idDivision: String = DatosSemillaJornada.ID_DIVISION_INICIAL,
    val nombreDivision: String = "Primera División Sevilla · Temporada 2026",
    val idEquipoPropio: String = DatosSemillaJornada.ID_EQUIPO_PROPIO,
    val filasClasificacion: List<FilaClasificacion> = emptyList(),
    val usandoRespaldoLocal: Boolean = false,
    val mensajeEstadoConexion: String? = null,
) {
    val filaEquipoPropio: FilaClasificacion?
        get() = filasClasificacion.firstOrNull { it.idEquipo == idEquipoPropio }
}

/**
 * Intenciones del usuario sobre la pantalla de clasificación oficial de la liga.
 */
sealed interface EventoUi {
    data object RecargarClasificacion : EventoUi
}

/**
 * Efectos efímeros de un solo uso para la pantalla de clasificación.
 */
sealed interface EfectoUi {
    data class MostrarMensaje(val texto: String) : EfectoUi
}
