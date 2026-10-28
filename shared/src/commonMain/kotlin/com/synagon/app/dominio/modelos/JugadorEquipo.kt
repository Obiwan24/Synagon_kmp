package com.synagon.app.dominio.modelos

/**
 * Representa un miembro de la plantilla de un equipo participante en la liga.
 *
 * @property id Identificador único del jugador.
 * @property nombreCompleto Nombre y apellidos del jugador.
 * @property esCapitan Indica si ostenta la capitanía del equipo.
 */
data class JugadorEquipo(
    val id: String,
    val nombreCompleto: String,
    val esCapitan: Boolean = false,
)
