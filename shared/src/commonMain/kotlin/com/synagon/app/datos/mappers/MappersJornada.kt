package com.synagon.app.datos.mappers

import com.synagon.app.datos.remoto.dto.DisponibilidadJugadorDto
import com.synagon.app.datos.remoto.dto.EnfrentamientoDto
import com.synagon.app.datos.remoto.dto.JugadorEquipoDto
import com.synagon.app.datos.remoto.dto.ParejaPartidoDto
import com.synagon.app.datos.remoto.dto.PropuestaFechaDto
import com.synagon.app.dominio.modelos.DisponibilidadJugador
import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.EstadoDisponibilidad
import com.synagon.app.dominio.modelos.EstadoEnfrentamiento
import com.synagon.app.dominio.modelos.JugadorEquipo
import com.synagon.app.dominio.modelos.ParejaPartido
import com.synagon.app.dominio.modelos.PropuestaFecha
import com.synagon.app.dominio.modelos.ResultadoSet
import kotlinx.datetime.LocalDateTime

/**
 * Convierte un [EnfrentamientoDto] recibido desde el backend en el modelo de dominio [Enfrentamiento].
 */
fun EnfrentamientoDto.aDominio(): Enfrentamiento {
    val mapaNombresPlantilla = jugadoresEquipo.associate { it.id to it.nombreCompleto }
    return Enfrentamiento(
        id = id,
        idDivision = divisionId,
        nombreDivision = nombreDivision,
        numeroJornada = numeroJornada,
        idEquipoLocal = equipoLocalId,
        nombreEquipoLocal = nombreEquipoLocal,
        clubLocal = clubLocal,
        idEquipoVisitante = equipoVisitanteId,
        nombreEquipoVisitante = nombreEquipoVisitante,
        clubVisitante = clubVisitante,
        estado = mapearEstadoEnfrentamiento(estado),
        fechaHoraDefinitiva = fechaDefinitiva?.let(::parsearFechaHoraIso),
        pistas = pistas,
        puntosLocal = puntosLocal,
        puntosVisitante = puntosVisitante,
        actaConfirmada = actaConfirmada,
        propuestasFecha = propuestasFecha.map { it.aDominio(id) }.sortedBy { it.orden },
        disponibilidades = disponibilidades.map { it.aDominio(id, mapaNombresPlantilla) },
        parejas = partidos.map { it.aDominio(mapaNombresPlantilla) }.sortedBy { it.numeroPareja },
        jugadoresEquipo = jugadoresEquipo.map { it.aDominio() },
        observaciones = observaciones,
    )
}

/**
 * Convierte un [Enfrentamiento] de dominio a [EnfrentamientoDto].
 */
fun Enfrentamiento.aDto(): EnfrentamientoDto {
    return EnfrentamientoDto(
        id = id,
        divisionId = idDivision,
        nombreDivision = nombreDivision,
        equipoLocalId = idEquipoLocal,
        nombreEquipoLocal = nombreEquipoLocal,
        clubLocal = clubLocal,
        equipoVisitanteId = idEquipoVisitante,
        nombreEquipoVisitante = nombreEquipoVisitante,
        clubVisitante = clubVisitante,
        numeroJornada = numeroJornada,
        estado = estado.name,
        fechaDefinitiva = fechaHoraDefinitiva?.toString(),
        pistas = pistas,
        puntosLocal = puntosLocal,
        puntosVisitante = puntosVisitante,
        actaConfirmada = actaConfirmada,
        propuestasFecha = propuestasFecha.map { it.aDto() },
        disponibilidades = disponibilidades.map { it.aDto() },
        partidos = parejas.map { it.aDto(id) },
        jugadoresEquipo = jugadoresEquipo.map { it.aDto() },
        observaciones = observaciones,
    )
}

fun PropuestaFechaDto.aDominio(idEnfrentamientoPadre: String = ""): PropuestaFecha {
    return PropuestaFecha(
        id = id,
        idEnfrentamiento = enfrentamientoId.ifBlank { idEnfrentamientoPadre },
        orden = orden,
        fechaHora = parsearFechaHoraIso(fechaPropuesta),
        pistaLugar = pistaLugar,
        esSeleccionada = esSeleccionada,
        notas = notas,
    )
}

fun PropuestaFecha.aDto(): PropuestaFechaDto {
    return PropuestaFechaDto(
        id = id,
        enfrentamientoId = idEnfrentamiento,
        fechaPropuesta = fechaHora.toString(),
        orden = orden,
        esSeleccionada = esSeleccionada,
        pistaLugar = pistaLugar,
        notas = notas,
    )
}

fun DisponibilidadJugadorDto.aDominio(
    idEnfrentamientoPadre: String = "",
    nombresPorId: Map<String, String> = emptyMap(),
): DisponibilidadJugador {
    val nombreResuelto = nombreJugador.ifBlank {
        nombresPorId[jugadorId] ?: "Jugador ${jugadorId.takeLast(4)}"
    }
    return DisponibilidadJugador(
        id = id,
        idEnfrentamiento = enfrentamientoId.ifBlank { idEnfrentamientoPadre },
        idPropuestaFecha = propuestaFechaId,
        idJugador = jugadorId,
        nombreJugador = nombreResuelto,
        estado = mapearEstadoDisponibilidad(estado),
        esDisponibilidadFinal = esDisponibilidadFinal,
    )
}

fun DisponibilidadJugador.aDto(): DisponibilidadJugadorDto {
    return DisponibilidadJugadorDto(
        id = id,
        enfrentamientoId = idEnfrentamiento,
        propuestaFechaId = idPropuestaFecha,
        jugadorId = idJugador,
        nombreJugador = nombreJugador,
        estado = estado.name,
        esDisponibilidadFinal = esDisponibilidadFinal,
    )
}

fun ParejaPartidoDto.aDominio(
    nombresPorId: Map<String, String> = emptyMap(),
): ParejaPartido {
    val setsConstruidos = buildList {
        if (set1Local != null && set1Visitante != null) {
            add(ResultadoSet(numeroSet = 1, juegosLocal = set1Local, juegosVisitante = set1Visitante))
        }
        if (set2Local != null && set2Visitante != null) {
            add(ResultadoSet(numeroSet = 2, juegosLocal = set2Local, juegosVisitante = set2Visitante))
        }
        if (set3Local != null && set3Visitante != null) {
            add(ResultadoSet(numeroSet = 3, juegosLocal = set3Local, juegosVisitante = set3Visitante))
        }
    }

    val idUno = jugadorLocal1Id.orEmpty()
    val idDos = jugadorLocal2Id.orEmpty()

    return ParejaPartido(
        id = id,
        numeroPareja = numeroPareja,
        idJugadorUno = idUno,
        nombreJugadorUno = nombreJugadorLocal1.ifBlank {
            nombresPorId[idUno] ?: "Jugador ${idUno.takeLast(4)}"
        },
        idJugadorDos = idDos,
        nombreJugadorDos = nombreJugadorLocal2.ifBlank {
            nombresPorId[idDos] ?: "Jugador ${idDos.takeLast(4)}"
        },
        idJugadorRivalUno = jugadorVisitante1Id,
        nombreJugadorRivalUno = nombreJugadorVisitante1,
        idJugadorRivalDos = jugadorVisitante2Id,
        nombreJugadorRivalDos = nombreJugadorVisitante2,
        sets = setsConstruidos,
        completado = completado,
    )
}

fun ParejaPartido.aDto(idEnfrentamiento: String = ""): ParejaPartidoDto {
    val set1 = sets.firstOrNull { it.numeroSet == 1 }
    val set2 = sets.firstOrNull { it.numeroSet == 2 }
    val set3 = sets.firstOrNull { it.numeroSet == 3 }

    return ParejaPartidoDto(
        id = id,
        enfrentamientoId = idEnfrentamiento,
        numeroPareja = numeroPareja,
        jugadorLocal1Id = idJugadorUno,
        nombreJugadorLocal1 = nombreJugadorUno,
        jugadorLocal2Id = idJugadorDos,
        nombreJugadorLocal2 = nombreJugadorDos,
        jugadorVisitante1Id = idJugadorRivalUno,
        nombreJugadorVisitante1 = nombreJugadorRivalUno,
        jugadorVisitante2Id = idJugadorRivalDos,
        nombreJugadorVisitante2 = nombreJugadorRivalDos,
        set1Local = set1?.juegosLocal,
        set1Visitante = set1?.juegosVisitante,
        set2Local = set2?.juegosLocal,
        set2Visitante = set2?.juegosVisitante,
        set3Local = set3?.juegosLocal,
        set3Visitante = set3?.juegosVisitante,
        completado = completado,
    )
}

fun JugadorEquipoDto.aDominio(): JugadorEquipo {
    return JugadorEquipo(
        id = id,
        nombreCompleto = nombreCompleto,
        esCapitan = esCapitan,
    )
}

fun JugadorEquipo.aDto(): JugadorEquipoDto {
    return JugadorEquipoDto(
        id = id,
        nombreCompleto = nombreCompleto,
        esCapitan = esCapitan,
    )
}

private fun mapearEstadoEnfrentamiento(valor: String): EstadoEnfrentamiento {
    return EstadoEnfrentamiento.entries.firstOrNull {
        it.name.equals(valor.trim(), ignoreCase = true)
    } ?: EstadoEnfrentamiento.PENDIENTE_FECHA
}

private fun mapearEstadoDisponibilidad(valor: String): EstadoDisponibilidad {
    return EstadoDisponibilidad.entries.firstOrNull {
        it.name.equals(valor.trim(), ignoreCase = true)
    } ?: EstadoDisponibilidad.DUDA
}

private fun parsearFechaHoraIso(texto: String): LocalDateTime {
    val limpio = texto.trim()
        .removeSuffix("Z")
        .substringBefore("+")
    val parteFechaHora = if (limpio.length > 19 && limpio[19] == '-') {
        limpio.substring(0, 19)
    } else {
        limpio
    }
    return runCatching {
        LocalDateTime.parse(parteFechaHora)
    }.getOrElse {
        LocalDateTime(2026, 11, 14, 10, 30)
    }
}
