package com.synagon.app.presentacion.pantallas.jornada

import com.synagon.app.dominio.modelos.DisponibilidadJugador
import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.dominio.modelos.EstadoDisponibilidad
import com.synagon.app.dominio.modelos.EstadoEnfrentamiento
import com.synagon.app.dominio.modelos.FilaClasificacion
import com.synagon.app.dominio.modelos.JugadorEquipo
import com.synagon.app.dominio.modelos.PropuestaFecha
import kotlinx.datetime.LocalDateTime

/**
 * Datos semilla alineados con los identificadores oficiales del backend (`V2__datos_semilla_prueba.sql`)
 * utilizados como respaldo interactivo cuando el servidor local aún no se encuentra activo.
 */
object DatosSemillaJornada {

    const val ID_DIVISION_INICIAL = "30000000-0000-0000-0000-000000000001"
    const val ID_EQUIPO_PROPIO = "50000000-0000-0000-0000-000000000001"
    const val ID_EQUIPO_RIVAL = "50000000-0000-0000-0000-000000000002"
    const val ID_ENFRENTAMIENTO_INICIAL = "70000000-0000-0000-0000-000000000001"
    const val ID_JUGADOR_ACTUAL = "40000000-0000-0000-0000-000000000001"
    const val NOMBRE_JUGADOR_ACTUAL = "Álvaro Iglesias Méndez"
    const val NOMBRE_EQUIPO_PROPIO = "Real Club Pádel Triana"
    const val NOMBRE_EQUIPO_RIVAL = "Club Pádel Los Remedios"

    private const val ID_PROPUESTA_1 = "80000000-0000-0000-0000-000000000001"
    private const val ID_PROPUESTA_2 = "80000000-0000-0000-0000-000000000002"

    fun obtenerPlantillaEquipoPropio(): List<JugadorEquipo> = listOf(
        JugadorEquipo(id = ID_JUGADOR_ACTUAL, nombreCompleto = NOMBRE_JUGADOR_ACTUAL, esCapitan = true),
        JugadorEquipo(id = "40000000-0000-0000-0000-000000000002", nombreCompleto = "Javier Morales Ruiz"),
        JugadorEquipo(id = "40000000-0000-0000-0000-000000000003", nombreCompleto = "Carlos Sánchez Domínguez"),
        JugadorEquipo(id = "40000000-0000-0000-0000-000000000004", nombreCompleto = "Pablo Navarro Herrera"),
        JugadorEquipo(id = "40000000-0000-0000-0000-000000000005", nombreCompleto = "Alejandro Romero Gil"),
        JugadorEquipo(id = "40000000-0000-0000-0000-000000000006", nombreCompleto = "Manuel Torres Vargas"),
        JugadorEquipo(id = "40000000-0000-0000-0000-000000000007", nombreCompleto = "Sergio Delgado Castro"),
        JugadorEquipo(id = "40000000-0000-0000-0000-000000000008", nombreCompleto = "Fernando Ortega León"),
    )

    fun crearEnfrentamientoInicial(): Enfrentamiento {
        val plantillaLocal = obtenerPlantillaEquipoPropio()

        val propuestaSabado = PropuestaFecha(
            id = ID_PROPUESTA_1,
            idEnfrentamiento = ID_ENFRENTAMIENTO_INICIAL,
            orden = 1,
            fechaHora = LocalDateTime(2026, 11, 14, 10, 30),
            pistaLugar = "Pistas Centrales Club Pádel Triana",
            esSeleccionada = false,
            notas = "Turno matinal con 3 pistas cubiertas reservadas",
        )

        val propuestaDomingo = PropuestaFecha(
            id = ID_PROPUESTA_2,
            idEnfrentamiento = ID_ENFRENTAMIENTO_INICIAL,
            orden = 2,
            fechaHora = LocalDateTime(2026, 11, 15, 18, 0),
            pistaLugar = "Club Pádel Triana · Pistas Exteriores",
            esSeleccionada = false,
            notas = "Alternativa de tarde",
        )

        val votosPropuesta1 = listOf(
            crearVoto("v1-1", ID_PROPUESTA_1, plantillaLocal[0], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v1-2", ID_PROPUESTA_1, plantillaLocal[1], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v1-3", ID_PROPUESTA_1, plantillaLocal[2], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v1-4", ID_PROPUESTA_1, plantillaLocal[3], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v1-5", ID_PROPUESTA_1, plantillaLocal[4], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v1-6", ID_PROPUESTA_1, plantillaLocal[5], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v1-7", ID_PROPUESTA_1, plantillaLocal[6], EstadoDisponibilidad.DUDA),
            crearVoto("v1-8", ID_PROPUESTA_1, plantillaLocal[7], EstadoDisponibilidad.NO_DISPONIBLE),
        )

        val votosPropuesta2 = listOf(
            crearVoto("v2-1", ID_PROPUESTA_2, plantillaLocal[0], EstadoDisponibilidad.DUDA),
            crearVoto("v2-2", ID_PROPUESTA_2, plantillaLocal[1], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v2-3", ID_PROPUESTA_2, plantillaLocal[2], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v2-4", ID_PROPUESTA_2, plantillaLocal[4], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v2-5", ID_PROPUESTA_2, plantillaLocal[6], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v2-6", ID_PROPUESTA_2, plantillaLocal[7], EstadoDisponibilidad.DISPONIBLE),
            crearVoto("v2-7", ID_PROPUESTA_2, plantillaLocal[3], EstadoDisponibilidad.NO_DISPONIBLE),
            crearVoto("v2-8", ID_PROPUESTA_2, plantillaLocal[5], EstadoDisponibilidad.NO_DISPONIBLE),
        )

        return Enfrentamiento(
            id = ID_ENFRENTAMIENTO_INICIAL,
            idDivision = ID_DIVISION_INICIAL,
            nombreDivision = "Primera División Sevilla",
            numeroJornada = 5,
            idEquipoLocal = ID_EQUIPO_PROPIO,
            nombreEquipoLocal = NOMBRE_EQUIPO_PROPIO,
            clubLocal = "Club Pádel Triana Sevilla",
            idEquipoVisitante = ID_EQUIPO_RIVAL,
            nombreEquipoVisitante = NOMBRE_EQUIPO_RIVAL,
            clubVisitante = "Instalaciones Deportivas Los Remedios",
            estado = EstadoEnfrentamiento.VOTACION_DISPONIBILIDAD_ABIERTA,
            pistas = "Pistas Centrales Club Pádel Triana",
            propuestasFecha = listOf(propuestaSabado, propuestaDomingo),
            disponibilidades = votosPropuesta1 + votosPropuesta2,
            jugadoresEquipo = plantillaLocal,
        )
    }

    fun crearClasificacionDivisionSemilla(): List<FilaClasificacion> = listOf(
        FilaClasificacion(
            posicion = 1,
            idEquipo = ID_EQUIPO_PROPIO,
            nombreEquipo = NOMBRE_EQUIPO_PROPIO,
            club = "Club Pádel Triana Sevilla",
            partidosJugados = 4,
            partidosGanados = 4,
            partidosPerdidos = 0,
            partidosParejaGanados = 10,
            partidosParejaPerdidos = 2,
            setsAFavor = 21,
            setsEnContra = 6,
            juegosAFavor = 152,
            juegosEnContra = 98,
            diferenciaJuegos = 54,
            puntos = 10,
        ),
        FilaClasificacion(
            posicion = 2,
            idEquipo = ID_EQUIPO_RIVAL,
            nombreEquipo = NOMBRE_EQUIPO_RIVAL,
            club = "I.D. Los Remedios",
            partidosJugados = 4,
            partidosGanados = 3,
            partidosPerdidos = 1,
            partidosParejaGanados = 9,
            partidosParejaPerdidos = 3,
            setsAFavor = 19,
            setsEnContra = 8,
            juegosAFavor = 144,
            juegosEnContra = 106,
            diferenciaJuegos = 38,
            puntos = 9,
        ),
        FilaClasificacion(
            posicion = 3,
            idEquipo = "50000000-0000-0000-0000-000000000003",
            nombreEquipo = "Nervión Pádel Indoor A",
            club = "Nervión Pádel Center",
            partidosJugados = 4,
            partidosGanados = 3,
            partidosPerdidos = 1,
            partidosParejaGanados = 8,
            partidosParejaPerdidos = 4,
            setsAFavor = 17,
            setsEnContra = 10,
            juegosAFavor = 138,
            juegosEnContra = 114,
            diferenciaJuegos = 24,
            puntos = 8,
        ),
        FilaClasificacion(
            posicion = 4,
            idEquipo = "50000000-0000-0000-0000-000000000004",
            nombreEquipo = "Aljarafe Smash Club",
            club = "Mairena Pádel Arena",
            partidosJugados = 4,
            partidosGanados = 2,
            partidosPerdidos = 2,
            partidosParejaGanados = 6,
            partidosParejaPerdidos = 6,
            setsAFavor = 13,
            setsEnContra = 13,
            juegosAFavor = 126,
            juegosEnContra = 122,
            diferenciaJuegos = 4,
            puntos = 6,
        ),
        FilaClasificacion(
            posicion = 5,
            idEquipo = "50000000-0000-0000-0000-000000000005",
            nombreEquipo = "Cartuja Sport Pádel",
            club = "Isla de la Cartuja",
            partidosJugados = 4,
            partidosGanados = 2,
            partidosPerdidos = 2,
            partidosParejaGanados = 5,
            partidosParejaPerdidos = 7,
            setsAFavor = 11,
            setsEnContra = 15,
            juegosAFavor = 112,
            juegosEnContra = 129,
            diferenciaJuegos = -17,
            puntos = 5,
        ),
        FilaClasificacion(
            posicion = 6,
            idEquipo = "50000000-0000-0000-0000-000000000006",
            nombreEquipo = "Heliópolis Pádel Team",
            club = "Real Club Heliópolis",
            partidosJugados = 4,
            partidosGanados = 1,
            partidosPerdidos = 3,
            partidosParejaGanados = 4,
            partidosParejaPerdidos = 8,
            setsAFavor = 9,
            setsEnContra = 17,
            juegosAFavor = 104,
            juegosEnContra = 136,
            diferenciaJuegos = -32,
            puntos = 4,
        ),
        FilaClasificacion(
            posicion = 7,
            idEquipo = "50000000-0000-0000-0000-000000000007",
            nombreEquipo = "Santa Justa Pádel B",
            club = "Pádel Santa Justa",
            partidosJugados = 4,
            partidosGanados = 1,
            partidosPerdidos = 3,
            partidosParejaGanados = 3,
            partidosParejaPerdidos = 9,
            setsAFavor = 7,
            setsEnContra = 19,
            juegosAFavor = 96,
            juegosEnContra = 141,
            diferenciaJuegos = -45,
            puntos = 3,
        ),
        FilaClasificacion(
            posicion = 8,
            idEquipo = "50000000-0000-0000-0000-000000000008",
            nombreEquipo = "Montequinto Raqueta Club",
            club = "Dos Hermanas Pádel",
            partidosJugados = 4,
            partidosGanados = 0,
            partidosPerdidos = 4,
            partidosParejaGanados = 3,
            partidosParejaPerdidos = 9,
            setsAFavor = 8,
            setsEnContra = 17,
            juegosAFavor = 99,
            juegosEnContra = 125,
            diferenciaJuegos = -26,
            puntos = 3,
        ),
    )

    fun crearEstadisticasEquipoSemilla(): List<EstadisticaJugador> = listOf(
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000001",
            nombreJugador = "Álvaro Iglesias Méndez",
            partidosApuntados = 4,
            partidosJugados = 4,
            partidosGanados = 4,
            partidosPerdidos = 0,
            setsGanados = 8,
            setsPerdidos = 1,
            juegosGanados = 54,
            juegosPerdidos = 29,
            puntos = 4,
            esCapitan = true,
        ),
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000002",
            nombreJugador = "Javier Morales Ruiz",
            partidosApuntados = 4,
            partidosJugados = 4,
            partidosGanados = 3,
            partidosPerdidos = 1,
            setsGanados = 7,
            setsPerdidos = 3,
            juegosGanados = 56,
            juegosPerdidos = 39,
            puntos = 3,
        ),
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000003",
            nombreJugador = "Carlos Sánchez Domínguez",
            partidosApuntados = 4,
            partidosJugados = 3,
            partidosGanados = 3,
            partidosPerdidos = 0,
            setsGanados = 6,
            setsPerdidos = 1,
            juegosGanados = 42,
            juegosPerdidos = 22,
            puntos = 3,
        ),
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000004",
            nombreJugador = "Pablo Navarro Herrera",
            partidosApuntados = 4,
            partidosJugados = 3,
            partidosGanados = 2,
            partidosPerdidos = 1,
            setsGanados = 5,
            setsPerdidos = 3,
            juegosGanados = 44,
            juegosPerdidos = 36,
            puntos = 2,
        ),
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000005",
            nombreJugador = "Alejandro Romero Gil",
            partidosApuntados = 3,
            partidosJugados = 3,
            partidosGanados = 3,
            partidosPerdidos = 0,
            setsGanados = 6,
            setsPerdidos = 2,
            juegosGanados = 47,
            juegosPerdidos = 31,
            puntos = 3,
        ),
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000006",
            nombreJugador = "Manuel Torres Vargas",
            partidosApuntados = 4,
            partidosJugados = 3,
            partidosGanados = 2,
            partidosPerdidos = 1,
            setsGanados = 4,
            setsPerdidos = 3,
            juegosGanados = 37,
            juegosPerdidos = 33,
            puntos = 2,
        ),
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000007",
            nombreJugador = "Sergio Delgado Castro",
            partidosApuntados = 3,
            partidosJugados = 2,
            partidosGanados = 2,
            partidosPerdidos = 0,
            setsGanados = 4,
            setsPerdidos = 0,
            juegosGanados = 25,
            juegosPerdidos = 12,
            puntos = 2,
        ),
        EstadisticaJugador(
            idJugador = "40000000-0000-0000-0000-000000000008",
            nombreJugador = "Fernando Ortega León",
            partidosApuntados = 2,
            partidosJugados = 2,
            partidosGanados = 1,
            partidosPerdidos = 1,
            setsGanados = 2,
            setsPerdidos = 3,
            juegosGanados = 22,
            juegosPerdidos = 26,
            puntos = 1,
        ),
    )

    private fun crearVoto(
        id: String,
        idPropuesta: String,
        jugador: JugadorEquipo,
        estado: EstadoDisponibilidad,
    ): DisponibilidadJugador {
        return DisponibilidadJugador(
            id = id,
            idEnfrentamiento = ID_ENFRENTAMIENTO_INICIAL,
            idPropuestaFecha = idPropuesta,
            idJugador = jugador.id,
            nombreJugador = jugador.nombreCompleto,
            estado = estado,
        )
    }
}
