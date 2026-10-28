package com.synagon.app.dominio.casosuso

import com.synagon.app.dominio.modelos.ParejaPartido
import com.synagon.app.dominio.utilidades.Resultado

/**
 * Valida en cliente las reglas de negocio de Synagon para la convocatoria del capitán:
 * - Exactamente 3 parejas (Pareja 1, Pareja 2 y Pareja 3).
 * - Exactamente 6 jugadores convocados en total (2 por pareja).
 * - Ningún jugador puede repetirse en dos parejas de la misma jornada.
 * - Todos los convocados deben pertenecer al conjunto de jugadores disponibles.
 */
class ValidarConvocatoriaCasoUso {

    fun ejecutar(
        parejas: List<ParejaPartido>,
        idsJugadoresDisponibles: Set<String>,
    ): Resultado<List<ParejaPartido>, String> {
        if (parejas.size != 3) {
            return Resultado.Error("Debes configurar exactamente las Parejas 1, 2 y 3.")
        }

        val numerosEsperados = setOf(1, 2, 3)
        if (parejas.map { it.numeroPareja }.toSet() != numerosEsperados) {
            return Resultado.Error("La alineación debe incluir obligatoriamente Pareja 1, Pareja 2 y Pareja 3.")
        }

        val jugadoresUnicos = mutableSetOf<String>()
        for (pareja in parejas.sortedBy { it.numeroPareja }) {
            if (pareja.idJugadorUno.isBlank() || pareja.idJugadorDos.isBlank()) {
                return Resultado.Error("Faltan jugadores por asignar en la Pareja ${pareja.numeroPareja}.")
            }
            if (pareja.idJugadorUno == pareja.idJugadorDos) {
                return Resultado.Error("Un mismo jugador no puede ocupar las dos plazas de la Pareja ${pareja.numeroPareja}.")
            }
            if (!jugadoresUnicos.add(pareja.idJugadorUno) || !jugadoresUnicos.add(pareja.idJugadorDos)) {
                return Resultado.Error("Un jugador no puede repetirse en más de una pareja en la misma jornada.")
            }
            if (idsJugadoresDisponibles.isNotEmpty() &&
                (pareja.idJugadorUno !in idsJugadoresDisponibles || pareja.idJugadorDos !in idsJugadoresDisponibles)
            ) {
                return Resultado.Error("Todos los jugadores convocados deben haber confirmado disponibilidad.")
            }
        }

        if (jugadoresUnicos.size != 6) {
            return Resultado.Error("Debes convocar exactamente a 6 jugadores disponibles.")
        }

        return Resultado.Exito(parejas.sortedBy { it.numeroPareja })
    }
}
