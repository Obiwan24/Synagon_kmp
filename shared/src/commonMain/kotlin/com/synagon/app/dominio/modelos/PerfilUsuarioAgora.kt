package com.synagon.app.dominio.modelos

/**
 * Datos personales y estado de primer acceso del usuario autenticado en el Ágora de Synagon.
 */
data class PerfilUsuarioAgora(
    val usuarioId: String,
    val nombreUsuario: String,
    val emailUsuario: String,
    val telefonoUsuario: String = "",
    val requiereCambioPassword: Boolean = false,
)
