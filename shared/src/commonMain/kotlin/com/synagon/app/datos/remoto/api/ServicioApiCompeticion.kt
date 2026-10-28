package com.synagon.app.datos.remoto.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

/**
 * Servicio HTTP con Ktor Client para consultar la clasificación oficial de la división
 * y las estadísticas individuales de rendimiento y compromiso de los jugadores del equipo.
 */
class ServicioApiCompeticion(
    private val clienteHttp: HttpClient,
    private val proveedorUrlBase: ProveedorUrlBase,
) {
    private val urlApiBase: String
        get() = "${proveedorUrlBase.obtenerUrlBase().trimEnd('/')}/api/v1"

    /**
     * Obtiene la tabla de clasificación oficial de una división.
     * Endpoint: GET /api/v1/divisiones/{idDivision}/clasificacion
     */
    suspend fun obtenerClasificacionDivision(idDivision: String): HttpResponse {
        return clienteHttp.get("$urlApiBase/divisiones/$idDivision/clasificacion")
    }

    /**
     * Obtiene las estadísticas individuales de los miembros de un equipo.
     * Endpoint: GET /api/v1/equipos/{idEquipo}/estadisticas
     */
    suspend fun obtenerEstadisticasJugadoresEquipo(idEquipo: String): HttpResponse {
        return clienteHttp.get("$urlApiBase/equipos/$idEquipo/estadisticas")
    }
}
