package com.synagon.app.dominio.modelos

/**
 * Resumen de una membresía activa del usuario (como Presidente de liga, Capitán o Jugador de equipo)
 * mostrada en la sección "MIS LIGAS Y EQUIPOS ACTIVOS" del Ágora de Synagon.
 */
data class ResumenMembresiaUsuario(
    val ligaId: String,
    val nombreLiga: String,
    val equipoId: String?,
    val nombreEquipoOSubtitulo: String,
    val textoSedeDetalle: String,
    val partidosPorJornada: Int,
    val rol: RolMembresia,
)
