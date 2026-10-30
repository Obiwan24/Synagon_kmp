package com.synagon.app.presentacion.pantallas.agora

import com.synagon.app.dominio.modelos.ConfiguracionPreviaLiga
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.modelos.RolMembresia

/**
 * Estado único e inmutable de la interfaz para la Pantalla Principal (Ágora de Synagon)
 * bajo arquitectura MVI estricta y sistema de diseño "Olimpo Moderno".
 */
data class EstadoAgoraUi(
    val nombreUsuario: String = "Álvaro Iglesias Méndez",
    val emailUsuario: String = "alvaro.iglesias@synagon.es",
    val telefonoUsuario: String = "+34 612 345 678",
    val listaMembresias: List<ResumenMembresiaUsuario> = emptyList(),
    val cargando: Boolean = false,
    // Modal: Unir Equipo a Liga (Paso 1 y Paso 2)
    val mostrarModalUnirEquipo: Boolean = false,
    val codigoInvitacionInput: String = "",
    val configuracionLigaValidada: ConfiguracionPreviaLiga? = null,
    val nombreNuevoEquipoInput: String = "",
    val sedeNuevoEquipoInput: String = "",
    val validandoCodigoInvitacion: Boolean = false,
    val inscribiendoEquipo: Boolean = false,
    val errorModalUnirEquipo: String? = null,
    // Modal: Crear Liga (Rol Presidente)
    val mostrarModalCrearLiga: Boolean = false,
    val nombreNuevaLigaInput: String = "",
    val requiereSedeNuevaLiga: Boolean = true,
    val sedeUnicaNuevaLigaInput: String = "",
    val partidosPorJornadaNuevaLiga: Int = 3,
    val creandoLiga: Boolean = false,
    val errorModalCrearLiga: String? = null,
    // Modal: Perfil Personal
    val mostrarModalPerfil: Boolean = false,
    val nombrePerfilInput: String = "",
    val telefonoPerfilInput: String = "",
    val guardandoPerfil: Boolean = false,
    val errorModalPerfil: String? = null,
    // Diálogo: Confirmación Cerrar Sesión
    val mostrarDialogoCerrarSesion: Boolean = false,
    // Modal: Primer Acceso (Cambio opcional de contraseña inicial DNI)
    val mostrarModalPrimerAccesoPassword: Boolean = false,
    val desplegarInputNuevaPassword: Boolean = false,
    val nuevaPasswordInicialInput: String = "",
    val passwordInicialVisible: Boolean = false,
    val guardandoPasswordInicial: Boolean = false,
    val errorPasswordInicial: String? = null,
    // Estado informativo de conexión
    val usandoRespaldoLocal: Boolean = false,
    val mensajeEstadoConexion: String? = null,
)

/**
 * Intenciones y acciones emitidas por el usuario en la pantalla principal del Ágora.
 */
sealed interface EventoAgoraUi {
    data object RecargarAgora : EventoAgoraUi

    // Acciones: Crear Liga (Rol Presidente)
    data object PulsarCrearLiga : EventoAgoraUi
    data object CerrarModalCrearLiga : EventoAgoraUi
    data class CambiarNombreNuevaLiga(val valor: String) : EventoAgoraUi
    data class CambiarRequiereSedeNuevaLiga(val requiereSede: Boolean) : EventoAgoraUi
    data class CambiarSedeUnicaNuevaLiga(val valor: String) : EventoAgoraUi
    data class CambiarPartidosPorJornadaNuevaLiga(val cantidad: Int) : EventoAgoraUi
    data object ConfirmarCrearLiga : EventoAgoraUi

    // Acciones: Unir Equipo a Liga (Código de Invitación)
    data object PulsarUnirEquipo : EventoAgoraUi
    data object CerrarModalUnirEquipo : EventoAgoraUi
    data class CambiarCodigoInvitacion(val valor: String) : EventoAgoraUi
    data object ValidarCodigoInvitacion : EventoAgoraUi
    data object VolverAPasoCodigoInvitacion : EventoAgoraUi
    data class CambiarNombreNuevoEquipo(val valor: String) : EventoAgoraUi
    data class CambiarSedeNuevoEquipo(val valor: String) : EventoAgoraUi
    data object ConfirmarInscripcionEquipo : EventoAgoraUi

    // Selección de tarjeta de liga/equipo de la lista activa
    data class SeleccionarTarjetaMembresia(val membresia: ResumenMembresiaUsuario) : EventoAgoraUi

    // Acciones: Perfil de Usuario
    data object AbrirPerfil : EventoAgoraUi
    data object CerrarModalPerfil : EventoAgoraUi
    data class CambiarNombrePerfil(val valor: String) : EventoAgoraUi
    data class CambiarTelefonoPerfil(val valor: String) : EventoAgoraUi
    data object GuardarDatosPerfil : EventoAgoraUi

    // Acciones: Primer Acceso (Cambio opcional de contraseña DNI)
    data object SolicitarFormularioCambioPasswordInicial : EventoAgoraUi
    data class CambiarNuevaPasswordInicial(val valor: String) : EventoAgoraUi
    data object AlternarVisibilidadPasswordInicial : EventoAgoraUi
    data object ConfirmarCambioPasswordInicial : EventoAgoraUi
    data object RechazarCambioPasswordInicial : EventoAgoraUi

    // Acciones: Cerrar Sesión
    data object SolicitarCerrarSesion : EventoAgoraUi
    data object CancelarCerrarSesion : EventoAgoraUi
    data object ConfirmarCerrarSesion : EventoAgoraUi
}

/**
 * Efectos efímeros de navegación y notificación (Snackbar) emitidos desde `AgoraViewModel`.
 */
sealed interface EfectoAgoraUi {
    data class NavegarAGestionEquipoOLiga(
        val ligaId: String,
        val equipoId: String?,
        val rol: RolMembresia,
    ) : EfectoAgoraUi

    data object NavegarALogin : EfectoAgoraUi

    data class MostrarMensaje(val texto: String) : EfectoAgoraUi
}

typealias EstadoUi = EstadoAgoraUi
typealias EventoUi = EventoAgoraUi
typealias EfectoUi = EfectoAgoraUi
