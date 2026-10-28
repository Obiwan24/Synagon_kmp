package com.synagon.app.datos.remoto.api

import com.synagon.app.datos.remoto.dto.PeticionConfirmarConvocatoriaDto
import com.synagon.app.datos.remoto.dto.PeticionConfirmarFechaDto
import com.synagon.app.datos.remoto.dto.PeticionSubirActaDto
import com.synagon.app.datos.remoto.dto.PeticionVotarDisponibilidadDto
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Construye una instancia configurada de [HttpClient] usando el motor nativo de la plataforma
 * y serialización JSON tolerante a campos adicionales.
 */
fun crearClienteHttpSynagon(): HttpClient {
    return HttpClient(crearMotorHttpPlataforma()) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                    explicitNulls = false
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 8_000
            connectTimeoutMillis = 4_000
            socketTimeoutMillis = 8_000
        }
    }
}

/**
 * Servicio HTTP con Ktor Client encargado de consumir los endpoints REST de `ControladorJornada`
 * y subida de acta arbitral en el backend Spring Boot.
 */
class ServicioApiJornada(
    private val clienteHttp: HttpClient,
    private val proveedorUrlBase: ProveedorUrlBase,
) {
    private val urlEnfrentamientos: String
        get() = "${proveedorUrlBase.obtenerUrlBase().trimEnd('/')}/api/v1/jornadas/enfrentamientos"

    /**
     * Obtiene el detalle completo de un enfrentamiento.
     * Endpoint: GET /api/v1/jornadas/enfrentamientos/{idEnfrentamiento}
     */
    suspend fun obtenerEnfrentamiento(idEnfrentamiento: String): HttpResponse {
        return clienteHttp.get("$urlEnfrentamientos/$idEnfrentamiento")
    }

    /**
     * Registra o actualiza el voto de disponibilidad de un jugador para una fecha propuesta.
     * Endpoint: POST /api/v1/jornadas/enfrentamientos/{idEnfrentamiento}/disponibilidades
     */
    suspend fun votarDisponibilidad(
        idEnfrentamiento: String,
        peticion: PeticionVotarDisponibilidadDto,
    ): HttpResponse {
        return clienteHttp.post("$urlEnfrentamientos/$idEnfrentamiento/disponibilidades") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }

    /**
     * Fija por parte del capitán la fecha definitiva del enfrentamiento.
     * Endpoint: PUT /api/v1/jornadas/enfrentamientos/{idEnfrentamiento}/fecha-definitiva
     */
    suspend fun confirmarFechaDefinitiva(
        idEnfrentamiento: String,
        peticion: PeticionConfirmarFechaDto,
    ): HttpResponse {
        return clienteHttp.put("$urlEnfrentamientos/$idEnfrentamiento/fecha-definitiva") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }

    /**
     * Cierra la convocatoria y alineación de las 3 parejas de la jornada.
     * Endpoint: POST /api/v1/jornadas/enfrentamientos/{idEnfrentamiento}/alineacion
     */
    suspend fun confirmarConvocatoria(
        idEnfrentamiento: String,
        peticion: PeticionConfirmarConvocatoriaDto,
    ): HttpResponse {
        return clienteHttp.post("$urlEnfrentamientos/$idEnfrentamiento/alineacion") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }

    /**
     * Envía el acta oficial del enfrentamiento con los sets de las 3 parejas y la foto adjunta.
     * Endpoint: POST /api/v1/jornadas/enfrentamientos/{idEnfrentamiento}/acta
     */
    suspend fun subirActaEnfrentamiento(
        idEnfrentamiento: String,
        peticion: PeticionSubirActaDto,
    ): HttpResponse {
        return clienteHttp.post("$urlEnfrentamientos/$idEnfrentamiento/acta") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }
}
