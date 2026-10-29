package com.synagon.app.presentacion.pantallas.acceso

/**
 * Modalidad activa en la pantalla de acceso de Synagon.
 */
enum class ModoAcceso {
    LOGIN,
    REGISTRO,
}

/**
 * Estado único e inmutable de la interfaz para el flujo de acceso (Login y Registro).
 */
data class EstadoAccesoUi(
    val modo: ModoAcceso = ModoAcceso.LOGIN,
    val usuario: String = "",
    val contrasena: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val aceptarPrivacidad: Boolean = false,
    val contrasenaVisible: Boolean = false,
    val menuDesplegado: Boolean = false,
    val cargando: Boolean = false,
    val errorUsuario: String? = null,
    val errorContrasena: String? = null,
    val errorNombre: String? = null,
    val errorTelefono: String? = null,
    val errorPrivacidad: String? = null,
    val mensajeErrorGeneral: String? = null,
) {
    val esModoRegistro: Boolean
        get() = modo == ModoAcceso.REGISTRO
}

/**
 * Intenciones y acciones emitidas por el usuario en la pantalla de acceso.
 */
sealed interface EventoAccesoUi {
    data class CambiarUsuario(val valor: String) : EventoAccesoUi
    data class CambiarContrasena(val valor: String) : EventoAccesoUi
    data class CambiarNombre(val valor: String) : EventoAccesoUi
    data class CambiarTelefono(val valor: String) : EventoAccesoUi
    data class CambiarAceptarPrivacidad(val aceptado: Boolean) : EventoAccesoUi
    data object AlternarVisibilidadContrasena : EventoAccesoUi
    data class CambiarModo(val nuevoModo: ModoAcceso) : EventoAccesoUi
    data object AlternarMenuDesplegable : EventoAccesoUi
    data object CerrarMenuDesplegable : EventoAccesoUi
    data object IniciarSesion : EventoAccesoUi
    data object Registrarse : EventoAccesoUi
    data object SolicitarRecuperacionContrasena : EventoAccesoUi
    data object IrASobreSynagon : EventoAccesoUi
    data object IrAPrivacidad : EventoAccesoUi
}

/**
 * Efectos efímeros de un solo uso (navegación y mensajes Snackbar) para el flujo de acceso.
 */
sealed interface EfectoAccesoUi {
    data object NavegarAlPanelPrincipal : EfectoAccesoUi
    data object NavegarASobreSynagon : EfectoAccesoUi
    data object NavegarAPrivacidad : EfectoAccesoUi
    data class MostrarMensaje(val texto: String) : EfectoAccesoUi
}

typealias EstadoUi = EstadoAccesoUi
typealias EventoUi = EventoAccesoUi
typealias EfectoUi = EfectoAccesoUi
