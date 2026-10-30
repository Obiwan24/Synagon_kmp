package com.synagon.app.datos.remoto.api

import com.synagon.app.datos.remoto.dto.PeticionActualizarPerfilAgoraDto
import com.synagon.app.datos.remoto.dto.PeticionCambioPasswordInicialDto
import com.synagon.app.datos.remoto.dto.PeticionCrearLigaDto
import com.synagon.app.datos.remoto.dto.PeticionInscribirEquipoDto
import com.synagon.app.datos.remoto.dto.PeticionValidarCodigoInvitacionDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Servicio HTTP con Ktor Client encargado de comunicar la Pantalla Principal (Ágora de Synagon)
 * con los endpoints del backend para membresías activas, validación de códigos de invitación,
 * inscripción de equipos, creación de ligas, perfil y cambio opcional de contraseña en el primer acceso.
 */
class ServicioAgoraApi(
    private val clienteHttp: HttpClient,
    private val proveedorUrlBase: ProveedorUrlBase,
) {
    private val urlAgoraBase: String
        get() = "${proveedorUrlBase.obtenerUrlBase().trimEnd('/')}/api/v1/agora"

    /**
     * Obtiene el perfil del usuario autenticado y si requiere cambio opcional de contraseña inicial.
     * Endpoint: GET /api/v1/agora/perfil
     */
    suspend fun obtenerPerfilUsuario(): HttpResponse {
        return clienteHttp.get("$urlAgoraBase/perfil")
    }

    /**
     * Obtiene la lista de membresías activas del usuario (Presidente, Capitán y Jugador).
     * Endpoint: GET /api/v1/agora/membresias
     */
    suspend fun obtenerMembresiasUsuario(): HttpResponse {
        return clienteHttp.get("$urlAgoraBase/membresias")
    }

    /**
     * Valida el código de invitación facilitado por el presidente y devuelve la configuración previa de la liga.
     * Endpoint: POST /api/v1/agora/invitaciones/validar
     */
    suspend fun validarCodigoInvitacion(
        peticion: PeticionValidarCodigoInvitacionDto,
    ): HttpResponse {
        return clienteHttp.post("$urlAgoraBase/invitaciones/validar") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }

    /**
     * Inscribe un nuevo equipo en la liga validada otorgando el rol de Capitán al usuario actual.
     * Endpoint: POST /api/v1/agora/ligas/{ligaId}/equipos
     */
    suspend fun inscribirEquipoEnLiga(
        ligaId: String,
        peticion: PeticionInscribirEquipoDto,
    ): HttpResponse {
        return clienteHttp.post("$urlAgoraBase/ligas/$ligaId/equipos") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }

    /**
     * Gestiona el cambio opcional de la contraseña inicial (DNI) en el primer acceso del jugador.
     * Endpoint: PUT /api/v1/agora/perfil/password-inicial
     */
    suspend fun gestionarPasswordPrimerAcceso(
        peticion: PeticionCambioPasswordInicialDto,
    ): HttpResponse {
        return clienteHttp.put("$urlAgoraBase/perfil/password-inicial") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }

    /**
     * Crea una nueva liga otorgando el rol de Presidente al usuario creador.
     * Endpoint: POST /api/v1/agora/ligas
     */
    suspend fun crearLiga(
        peticion: PeticionCrearLigaDto,
    ): HttpResponse {
        return clienteHttp.post("$urlAgoraBase/ligas") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }

    /**
     * Actualiza los datos personales del usuario desde el modal de perfil del Ágora.
     * Endpoint: PUT /api/v1/agora/perfil
     */
    suspend fun actualizarPerfilUsuario(
        peticion: PeticionActualizarPerfilAgoraDto,
    ): HttpResponse {
        return clienteHttp.put("$urlAgoraBase/perfil") {
            contentType(ContentType.Application.Json)
            setBody(peticion)
        }
    }
}
