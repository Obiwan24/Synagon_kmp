package com.synagon.app.dominio.modelos

/**
 * Información previa de configuración de una liga devuelta al validar el código de invitación
 * proporcionado por el presidente antes de inscribir un equipo.
 */
data class ConfiguracionPreviaLiga(
    val ligaId: String,
    val nombreLiga: String,
    val requiereSedeEquipo: Boolean,
    val partidosPorJornada: Int,
)
