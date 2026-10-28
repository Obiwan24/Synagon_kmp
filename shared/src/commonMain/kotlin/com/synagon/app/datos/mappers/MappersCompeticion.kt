package com.synagon.app.datos.mappers

import com.synagon.app.datos.remoto.dto.EstadisticaJugadorDto
import com.synagon.app.datos.remoto.dto.FilaClasificacionDto
import com.synagon.app.datos.remoto.dto.PeticionSubirActaDto
import com.synagon.app.datos.remoto.dto.ResultadoParejaActaDto
import com.synagon.app.dominio.modelos.ActaEnfrentamiento
import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.dominio.modelos.FilaClasificacion
import com.synagon.app.dominio.modelos.ResultadoParejaActa

/**
 * Convierte un [ActaEnfrentamiento] de dominio al cuerpo JSON [PeticionSubirActaDto] del backend.
 */
fun ActaEnfrentamiento.aDto(): PeticionSubirActaDto {
    return PeticionSubirActaDto(
        fotoActaUrl = urlFotoActa,
        observaciones = observaciones,
        partidos = resultadosParejas
            .sortedBy { it.numeroPareja }
            .map { it.aDto() },
    )
}

fun ResultadoParejaActa.aDto(): ResultadoParejaActaDto {
    return ResultadoParejaActaDto(
        numeroPareja = numeroPareja,
        jugadorLocal1Id = idJugadorLocalUno,
        jugadorLocal2Id = idJugadorLocalDos,
        jugadorVisitante1Id = idJugadorVisitanteUno,
        jugadorVisitante2Id = idJugadorVisitanteDos,
        set1Local = set1Local,
        set1Visitante = set1Visitante,
        set2Local = set2Local,
        set2Visitante = set2Visitante,
        set3Local = set3Local,
        set3Visitante = set3Visitante,
    )
}

/**
 * Convierte un [FilaClasificacionDto] recibido desde el backend en [FilaClasificacion] de dominio.
 */
fun FilaClasificacionDto.aDominio(): FilaClasificacion {
    val difCalculada = diferenciaJuegos ?: (juegosAFavor - juegosEnContra)
    return FilaClasificacion(
        posicion = posicion,
        idEquipo = equipoId,
        nombreEquipo = nombreEquipo,
        club = club,
        partidosJugados = partidosJugados,
        partidosGanados = partidosGanados,
        partidosEmpatados = partidosEmpatados,
        partidosPerdidos = partidosPerdidos,
        partidosParejaGanados = partidosParejaGanados,
        partidosParejaPerdidos = partidosParejaPerdidos,
        setsAFavor = setsAFavor,
        setsEnContra = setsEnContra,
        diferenciaSets = setsAFavor - setsEnContra,
        juegosAFavor = juegosAFavor,
        juegosEnContra = juegosEnContra,
        diferenciaJuegos = difCalculada,
        puntos = puntos,
    )
}

fun FilaClasificacion.aDto(): FilaClasificacionDto {
    return FilaClasificacionDto(
        posicion = posicion,
        equipoId = idEquipo,
        nombreEquipo = nombreEquipo,
        club = club,
        partidosJugados = partidosJugados,
        partidosGanados = partidosGanados,
        partidosEmpatados = partidosEmpatados,
        partidosPerdidos = partidosPerdidos,
        partidosParejaGanados = partidosParejaGanados,
        partidosParejaPerdidos = partidosParejaPerdidos,
        setsAFavor = setsAFavor,
        setsEnContra = setsEnContra,
        juegosAFavor = juegosAFavor,
        juegosEnContra = juegosEnContra,
        diferenciaJuegos = diferenciaJuegos,
        puntos = puntos,
    )
}

/**
 * Convierte un [EstadisticaJugadorDto] recibido desde el backend en [EstadisticaJugador] de dominio.
 */
fun EstadisticaJugadorDto.aDominio(): EstadisticaJugador {
    return EstadisticaJugador(
        idJugador = jugadorId,
        nombreJugador = nombreJugador.ifBlank { "Jugador ${jugadorId.takeLast(4)}" },
        partidosApuntados = partidosApuntados,
        partidosJugados = partidosJugados,
        partidosGanados = partidosGanados,
        partidosPerdidos = partidosPerdidos,
        setsGanados = setsGanados,
        setsPerdidos = setsPerdidos,
        juegosGanados = juegosGanados,
        juegosPerdidos = juegosPerdidos,
        puntos = puntos,
        esCapitan = esCapitan,
    )
}

fun EstadisticaJugador.aDto(): EstadisticaJugadorDto {
    return EstadisticaJugadorDto(
        jugadorId = idJugador,
        nombreJugador = nombreJugador,
        partidosApuntados = partidosApuntados,
        partidosJugados = partidosJugados,
        partidosGanados = partidosGanados,
        partidosPerdidos = partidosPerdidos,
        setsGanados = setsGanados,
        setsPerdidos = setsPerdidos,
        juegosGanados = juegosGanados,
        juegosPerdidos = juegosPerdidos,
        puntos = puntos,
        esCapitan = esCapitan,
    )
}
