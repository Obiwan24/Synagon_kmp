package com.synagon.app.datos.mappers

import com.synagon.app.datos.remoto.dto.ConfiguracionPreviaLigaDto
import com.synagon.app.datos.remoto.dto.PerfilUsuarioAgoraDto
import com.synagon.app.datos.remoto.dto.ResumenMembresiaUsuarioDto
import com.synagon.app.dominio.modelos.ConfiguracionPreviaLiga
import com.synagon.app.dominio.modelos.PerfilUsuarioAgora
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.modelos.RolMembresia

/**
 * Transforma un [PerfilUsuarioAgoraDto] en el modelo de dominio [PerfilUsuarioAgora].
 */
fun PerfilUsuarioAgoraDto.aDominio(): PerfilUsuarioAgora {
    return PerfilUsuarioAgora(
        usuarioId = usuarioId,
        nombreUsuario = nombreUsuario,
        emailUsuario = emailUsuario,
        telefonoUsuario = telefonoUsuario,
        requiereCambioPassword = requiereCambioPassword,
    )
}

/**
 * Transforma un [ResumenMembresiaUsuarioDto] en el modelo puro de dominio [ResumenMembresiaUsuario].
 */
fun ResumenMembresiaUsuarioDto.aDominio(): ResumenMembresiaUsuario {
    val rolDominio = when (rol.trim().uppercase()) {
        "PRESIDENTE" -> RolMembresia.PRESIDENTE
        "CAPITAN", "CAPITÁN" -> RolMembresia.CAPITAN
        else -> RolMembresia.JUGADOR
    }
    return ResumenMembresiaUsuario(
        ligaId = ligaId,
        nombreLiga = nombreLiga,
        equipoId = equipoId?.takeIf { it.isNotBlank() },
        nombreEquipoOSubtitulo = nombreEquipoOSubtitulo,
        textoSedeDetalle = textoSedeDetalle,
        partidosPorJornada = partidosPorJornada.coerceAtLeast(1),
        rol = rolDominio,
    )
}

/**
 * Transforma un [ResumenMembresiaUsuario] de dominio en su representación serializable [ResumenMembresiaUsuarioDto].
 */
fun ResumenMembresiaUsuario.aDto(): ResumenMembresiaUsuarioDto {
    return ResumenMembresiaUsuarioDto(
        ligaId = ligaId,
        nombreLiga = nombreLiga,
        equipoId = equipoId,
        nombreEquipoOSubtitulo = nombreEquipoOSubtitulo,
        textoSedeDetalle = textoSedeDetalle,
        partidosPorJornada = partidosPorJornada,
        rol = rol.name,
    )
}

/**
 * Transforma un [ConfiguracionPreviaLigaDto] devuelto al validar un código de invitación en [ConfiguracionPreviaLiga].
 */
fun ConfiguracionPreviaLigaDto.aDominio(): ConfiguracionPreviaLiga {
    return ConfiguracionPreviaLiga(
        ligaId = ligaId,
        nombreLiga = nombreLiga,
        requiereSedeEquipo = requiereSedeEquipo,
        partidosPorJornada = partidosPorJornada.coerceAtLeast(1),
    )
}

/**
 * Transforma un [ConfiguracionPreviaLiga] de dominio en [ConfiguracionPreviaLigaDto].
 */
fun ConfiguracionPreviaLiga.aDto(): ConfiguracionPreviaLigaDto {
    return ConfiguracionPreviaLigaDto(
        ligaId = ligaId,
        nombreLiga = nombreLiga,
        requiereSedeEquipo = requiereSedeEquipo,
        partidosPorJornada = partidosPorJornada,
    )
}
