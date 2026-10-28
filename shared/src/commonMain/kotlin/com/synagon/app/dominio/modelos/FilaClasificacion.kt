package com.synagon.app.dominio.modelos

/**
 * Representa una fila oficial en la tabla de clasificación de una división de liga por equipos.
 *
 * @property posicion Posición ordinal en la tabla clasificatoria.
 * @property idEquipo Identificador único del equipo.
 * @property nombreEquipo Nombre oficial del equipo.
 * @property club Nombre del club o sede deportiva del equipo.
 * @property partidosJugados Enfrentamientos (jornadas) disputados (PJ).
 * @property partidosGanados Enfrentamientos ganados (PG).
 * @property partidosPerdidos Enfrentamientos perdidos (PP).
 * @property partidosParejaGanados Total de partidos individuales de parejas ganados (3 en juego por jornada).
 * @property partidosParejaPerdidos Total de partidos individuales de parejas perdidos.
 * @property setsAFavor Sets ganados acumulados.
 * @property setsEnContra Sets perdidos acumulados.
 * @property juegosAFavor Juegos ganados acumulados.
 * @property juegosEnContra Juegos perdidos acumulados.
 * @property diferenciaJuegos Balance neto de juegos (juegosAFavor - juegosEnContra).
 * @property puntos Puntos totales en la clasificación oficial.
 */
data class FilaClasificacion(
    val posicion: Int,
    val idEquipo: String,
    val nombreEquipo: String,
    val club: String? = null,
    val partidosJugados: Int,
    val partidosGanados: Int,
    val partidosEmpatados: Int = 0,
    val partidosPerdidos: Int,
    val partidosParejaGanados: Int = 0,
    val partidosParejaPerdidos: Int = 0,
    val setsAFavor: Int = 0,
    val setsEnContra: Int = 0,
    val diferenciaSets: Int = setsAFavor - setsEnContra,
    val juegosAFavor: Int = 0,
    val juegosEnContra: Int = 0,
    val diferenciaJuegos: Int = juegosAFavor - juegosEnContra,
    val puntos: Int,
)
