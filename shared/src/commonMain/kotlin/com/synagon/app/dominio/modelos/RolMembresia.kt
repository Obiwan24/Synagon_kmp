package com.synagon.app.dominio.modelos

/**
 * Rol que ostenta el usuario dentro de una liga o equipo inscrito en el Ágora de Synagon.
 */
enum class RolMembresia(val etiqueta: String) {
    PRESIDENTE("PRESIDENTE"),
    CAPITAN("CAPITÁN"),
    JUGADOR("JUGADOR"),
}
