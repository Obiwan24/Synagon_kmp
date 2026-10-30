package com.synagon.app.dominio.modelos

/**
 * Almacén ligero de sesión en memoria para propagar la identidad del usuario autenticado
 * y la detección de primer acceso (p. ej. acceso inicial mediante DNI asignado por el capitán)
 * entre `PantallaAcceso` y `PantallaAgoraPrincipal`.
 */
class ContextoSesionAgora {

    var nombreUsuario: String = "Álvaro Iglesias Méndez"
        private set

    var emailUsuario: String = "alvaro.iglesias@synagon.es"
        private set

    var telefonoUsuario: String = "+34 612 345 678"
        private set

    var requiereCambioPassword: Boolean = false
        private set

    fun registrarInicioSesion(
        identificador: String,
        contrasenaIntroducida: String,
        nombreRegistro: String? = null,
        telefonoRegistro: String? = null,
    ) {
        val limpio = identificador.trim()
        if (limpio.contains("@")) {
            emailUsuario = limpio
            if (!nombreRegistro.isNullOrBlank()) {
                nombreUsuario = nombreRegistro.trim()
            } else {
                val alias = limpio.substringBefore("@")
                    .replace('.', ' ')
                    .replace('_', ' ')
                    .split(' ')
                    .filter { it.isNotBlank() }
                    .joinToString(" ") { palabra ->
                        palabra.replaceFirstChar { it.uppercaseChar() }
                    }
                if (alias.isNotBlank()) {
                    nombreUsuario = alias
                }
            }
        } else if (limpio.isNotBlank()) {
            nombreUsuario = nombreRegistro?.trim()?.takeIf { it.isNotBlank() } ?: limpio
        }

        if (!telefonoRegistro.isNullOrBlank()) {
            telefonoUsuario = telefonoRegistro.trim()
        }

        // Detecta si la contraseña coincide con un formato de DNI/NIE inicial asignado por el capitán
        requiereCambioPassword = nombreRegistro.isNullOrBlank() && esFormatoDniONie(contrasenaIntroducida.trim())
    }

    fun actualizarDatosPerfil(nombre: String, telefono: String) {
        nombreUsuario = nombre.trim()
        telefonoUsuario = telefono.trim()
    }

    fun marcarCambioPasswordGestionado() {
        requiereCambioPassword = false
    }

    fun limpiarSesion() {
        requiereCambioPassword = false
    }

    private fun esFormatoDniONie(texto: String): Boolean {
        val mayusculas = texto.uppercase()
        val patronDni = Regex("^[0-9]{8}[A-Z]$")
        val patronNie = Regex("^[XYZ][0-9]{7}[A-Z]$")
        return patronDni.matches(mayusculas) || patronNie.matches(mayusculas)
    }
}
