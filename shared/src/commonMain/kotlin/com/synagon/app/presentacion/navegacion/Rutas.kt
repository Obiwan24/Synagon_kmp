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
     * Panel principal de competición (Jornada Actual, Subir Acta, Clasificación y Estadísticas).
     */
    @Serializable
    data object PanelPrincipal : RutaSynagon
}
