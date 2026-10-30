package com.synagon.app.presentacion.navegacion

import kotlinx.serialization.Serializable

/**
 * Definición tipada y serializable de las rutas del grafo de navegación de Synagon
 * empleando JetBrains Compose Navigation.
 */
@Serializable
sealed interface RutaSynagon {

    /**
     * Destino inicial de autenticación e inscripción (Login y Registro).
     */
    @Serializable
    data object Acceso : RutaSynagon

    /**
     * Pantalla institucional con el origen helénico y misión deportiva de Synagon.
     */
    @Serializable
    data object SobreSynagon : RutaSynagon

    /**
     * Pantalla informativa legal RGPD / LOPDGDD sobre privacidad y tratamiento de actas y estadísticas.
     */
    @Serializable
    data object Privacidad : RutaSynagon

    /**
     * Pantalla Principal (Ágora de Synagon) tras el inicio de sesión con acciones de competición
     * y listado de ligas y equipos activos.
     */
    @Serializable
    data object AgoraPrincipal : RutaSynagon

    /**
     * Pantalla de detalle y gestión de competición para un equipo o liga seleccionada desde el Ágora,
     * recibiendo `ligaId`, `equipoId` y el `rol` correspondiente (`PRESIDENTE`, `CAPITAN` o `JUGADOR`).
     */
    @Serializable
    data class DetalleEquipoOLiga(
        val ligaId: String,
        val equipoId: String? = null,
        val rol: String,
    ) : RutaSynagon
}
