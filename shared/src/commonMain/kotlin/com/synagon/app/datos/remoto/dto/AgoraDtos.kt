package com.synagon.app.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO de red para el perfil del usuario autenticado en el Ágora y su estado de primer acceso.
 */
@Serializable
data class PerfilUsuarioAgoraDto(
    @SerialName("usuarioId")
    val usuarioId: String = "",
    @SerialName("nombreUsuario")
    val nombreUsuario: String = "",
    @SerialName("emailUsuario")
    val emailUsuario: String = "",
    @SerialName("telefonoUsuario")
    val telefonoUsuario: String = "",
    @SerialName("requiereCambioPassword")
    val requiereCambioPassword: Boolean = false,
)

/**
 * DTO de red que representa el resumen de una membresía activa del usuario en una liga o equipo.
 */
@Serializable
data class ResumenMembresiaUsuarioDto(
    @SerialName("ligaId")
    val ligaId: String,
    @SerialName("nombreLiga")
    val nombreLiga: String,
    @SerialName("equipoId")
    val equipoId: String? = null,
    @SerialName("nombreEquipoOSubtitulo")
    val nombreEquipoOSubtitulo: String,
    @SerialName("textoSedeDetalle")
    val textoSedeDetalle: String = "",
    @SerialName("partidosPorJornada")
    val partidosPorJornada: Int = 3,
    @SerialName("rol")
    val rol: String,
)

/**
 * DTO de red devuelto al validar el código de invitación de una liga proporcionado por su presidente.
 */
@Serializable
data class ConfiguracionPreviaLigaDto(
    @SerialName("ligaId")
    val ligaId: String,
    @SerialName("nombreLiga")
    val nombreLiga: String,
    @SerialName("requiereSedeEquipo")
    val requiereSedeEquipo: Boolean = true,
    @SerialName("partidosPorJornada")
    val partidosPorJornada: Int = 3,
)

/**
 * Cuerpo de petición para validar un código de invitación de liga.
 */
@Serializable
data class PeticionValidarCodigoInvitacionDto(
    @SerialName("codigoInvitacion")
    val codigoInvitacion: String,
)

/**
 * Cuerpo de petición para inscribir un nuevo equipo como capitán dentro de una liga validada.
 */
@Serializable
data class PeticionInscribirEquipoDto(
    @SerialName("codigoInvitacion")
    val codigoInvitacion: String,
    @SerialName("nombreEquipo")
    val nombreEquipo: String,
    @SerialName("sedeEquipo")
    val sedeEquipo: String? = null,
)

/**
 * Cuerpo de petición para confirmar u omitir el cambio de contraseña inicial (DNI) en el primer acceso.
 */
@Serializable
data class PeticionCambioPasswordInicialDto(
    @SerialName("nuevaPassword")
    val nuevaPassword: String? = null,
    @SerialName("omitirCambio")
    val omitirCambio: Boolean = false,
)

/**
 * Cuerpo de petición para crear una nueva competición con rol `PRESIDENTE`.
 */
@Serializable
data class PeticionCrearLigaDto(
    @SerialName("nombreLiga")
    val nombreLiga: String,
    @SerialName("requiereSedeEquipo")
    val requiereSedeEquipo: Boolean,
    @SerialName("sedeUnicaLiga")
    val sedeUnicaLiga: String? = null,
    @SerialName("partidosPorJornada")
    val partidosPorJornada: Int,
)

/**
 * Cuerpo de petición para actualizar los datos personales del usuario desde el modal de perfil.
 */
@Serializable
data class PeticionActualizarPerfilAgoraDto(
    @SerialName("nombreUsuario")
    val nombreUsuario: String,
    @SerialName("telefonoUsuario")
    val telefonoUsuario: String,
)
