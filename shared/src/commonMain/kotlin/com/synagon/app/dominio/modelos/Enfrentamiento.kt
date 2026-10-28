package com.synagon.app.dominio.modelos

import kotlinx.datetime.LocalDateTime

/**
 * Modelo de dominio inmutable que representa un enfrentamiento oficial entre dos equipos en una jornada.
 * Centraliza la negociación de las 2 fechas propuestas, la encuesta de disponibilidad de jugadores
 * y la convocatoria de 6 jugadores distribuidos en Pareja 1, Pareja 2 y Pareja 3.
 */
data class Enfrentamiento(
    val id: String,
    val idDivision: String,
    val nombreDivision: String = "1ª División",
    val numeroJornada: Int,
    val idEquipoLocal: String,
    val nombreEquipoLocal: String,
    val clubLocal: String? = null,
    val idEquipoVisitante: String,
    val nombreEquipoVisitante: String,
    val clubVisitante: String? = null,
    val estado: EstadoEnfrentamiento,
    val fechaHoraDefinitiva: LocalDateTime? = null,
    val pistas: String? = null,
    val puntosLocal: Int = 0,
    val puntosVisitante: Int = 0,
    val actaConfirmada: Boolean = false,
    val propuestasFecha: List<PropuestaFecha> = emptyList(),
    val disponibilidades: List<DisponibilidadJugador> = emptyList(),
    val parejas: List<ParejaPartido> = emptyList(),
    val jugadoresEquipo: List<JugadorEquipo> = emptyList(),
    val observaciones: String? = null,
) {
    companion object {
        const val MINIMO_JUGADORES_CONVOCATORIA: Int = 6
    }

    /**
     * Cuenta cuántos jugadores han votado [EstadoDisponibilidad.DISPONIBLE] en la propuesta indicada.
     */
    fun contarDisponiblesEnPropuesta(idPropuestaFecha: String): Int {
        return disponibilidades.count {
            it.idPropuestaFecha == idPropuestaFecha && it.estado == EstadoDisponibilidad.DISPONIBLE
        }
    }

    /**
     * Cuenta cuántos jugadores han votado [EstadoDisponibilidad.DUDA] en la propuesta indicada.
     */
    fun contarDudasEnPropuesta(idPropuestaFecha: String): Int {
        return disponibilidades.count {
            it.idPropuestaFecha == idPropuestaFecha && it.estado == EstadoDisponibilidad.DUDA
        }
    }

    /**
     * Cuenta cuántos jugadores han votado [EstadoDisponibilidad.NO_DISPONIBLE] en la propuesta indicada.
     */
    fun contarNoDisponiblesEnPropuesta(idPropuestaFecha: String): Int {
        return disponibilidades.count {
            it.idPropuestaFecha == idPropuestaFecha && it.estado == EstadoDisponibilidad.NO_DISPONIBLE
        }
    }

    /**
     * Indica si la propuesta alcanza o supera el quórum mínimo de 6 jugadores disponibles.
     */
    fun alcanzaQuorumDisponibles(idPropuestaFecha: String): Boolean {
        return contarDisponiblesEnPropuesta(idPropuestaFecha) >= MINIMO_JUGADORES_CONVOCATORIA
    }

    /**
     * Obtiene el voto emitido por un jugador concreto para una propuesta de fecha, o null si aún no votó.
     */
    fun obtenerEstadoJugadorEnPropuesta(
        idPropuestaFecha: String,
        idJugador: String,
    ): EstadoDisponibilidad? {
        return disponibilidades.firstOrNull {
            it.idPropuestaFecha == idPropuestaFecha && it.idJugador == idJugador
        }?.estado
    }

    /**
     * Devuelve la lista de jugadores del equipo que han marcado [EstadoDisponibilidad.DISPONIBLE]
     * para la propuesta seleccionada (o en alguna propuesta si aún no hay una marcada).
     */
    fun obtenerJugadoresDisponibles(idPropuestaReferencia: String?): List<JugadorEquipo> {
        val idsDisponibles: Set<String> = if (idPropuestaReferencia != null) {
            disponibilidades
                .filter {
                    it.idPropuestaFecha == idPropuestaReferencia &&
                        it.estado == EstadoDisponibilidad.DISPONIBLE
                }
                .map { it.idJugador }
                .toSet()
        } else {
            disponibilidades
                .filter { it.estado == EstadoDisponibilidad.DISPONIBLE }
                .map { it.idJugador }
                .toSet()
        }

        val jugadoresEnPlantilla = jugadoresEquipo.filter { it.id in idsDisponibles }
        if (jugadoresEnPlantilla.isNotEmpty()) {
            return jugadoresEnPlantilla
        }

        // Si el backend envía disponibilidades sin catálogo separado de plantilla, reconstruimos desde los votos
        return disponibilidades
            .filter {
                (idPropuestaReferencia == null || it.idPropuestaFecha == idPropuestaReferencia) &&
                    it.estado == EstadoDisponibilidad.DISPONIBLE
            }
            .distinctBy { it.idJugador }
            .map { JugadorEquipo(id = it.idJugador, nombreCompleto = it.nombreJugador) }
    }
}
