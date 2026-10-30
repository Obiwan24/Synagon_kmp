package com.synagon.app.datos.repositorios

import com.synagon.app.datos.mappers.aDominio
import com.synagon.app.datos.remoto.api.ServicioAgoraApi
import com.synagon.app.datos.remoto.dto.ConfiguracionPreviaLigaDto
import com.synagon.app.datos.remoto.dto.PerfilUsuarioAgoraDto
import com.synagon.app.datos.remoto.dto.PeticionActualizarPerfilAgoraDto
import com.synagon.app.datos.remoto.dto.PeticionCambioPasswordInicialDto
import com.synagon.app.datos.remoto.dto.PeticionCrearLigaDto
import com.synagon.app.datos.remoto.dto.PeticionInscribirEquipoDto
import com.synagon.app.datos.remoto.dto.PeticionValidarCodigoInvitacionDto
import com.synagon.app.datos.remoto.dto.ResumenMembresiaUsuarioDto
import com.synagon.app.dominio.modelos.ConfiguracionPreviaLiga
import com.synagon.app.dominio.modelos.PerfilUsuarioAgora
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.repositorios.RepositorioAgora
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Implementación con Ktor Client de [RepositorioAgora] que encapsula todas las operaciones HTTP
 * en [Resultado] sin propagar excepciones de red no controladas.
 */
class RepositorioAgoraImpl(
    private val servicioAgoraApi: ServicioAgoraApi,
) : RepositorioAgora {

    override suspend fun obtenerPerfilUsuario(): Resultado<PerfilUsuarioAgora, ErrorRed> {
        return ejecutarLlamadaConCuerpo(
            llamada = { servicioAgoraApi.obtenerPerfilUsuario() },
            transformar = { dto: PerfilUsuarioAgoraDto -> dto.aDominio() },
        )
    }

    override suspend fun obtenerMembresiasUsuario(): Resultado<List<ResumenMembresiaUsuario>, ErrorRed> {
        return ejecutarLlamadaConCuerpo(
            llamada = { servicioAgoraApi.obtenerMembresiasUsuario() },
            transformar = { listaDto: List<ResumenMembresiaUsuarioDto> ->
                listaDto.map { it.aDominio() }
            },
        )
    }

    override suspend fun validarCodigoInvitacion(
        codigoInvitacion: String,
    ): Resultado<ConfiguracionPreviaLiga, ErrorRed> {
        val peticion = PeticionValidarCodigoInvitacionDto(
            codigoInvitacion = codigoInvitacion.trim().uppercase(),
        )
        return ejecutarLlamadaConCuerpo(
            llamada = { servicioAgoraApi.validarCodigoInvitacion(peticion) },
            transformar = { dto: ConfiguracionPreviaLigaDto -> dto.aDominio() },
        )
    }

    override suspend fun inscribirEquipoEnLiga(
        ligaId: String,
        codigoInvitacion: String,
        nombreEquipo: String,
        sedeEquipo: String?,
    ): Resultado<ResumenMembresiaUsuario, ErrorRed> {
        val peticion = PeticionInscribirEquipoDto(
            codigoInvitacion = codigoInvitacion.trim().uppercase(),
            nombreEquipo = nombreEquipo.trim(),
            sedeEquipo = sedeEquipo?.trim()?.takeIf { it.isNotBlank() },
        )
        return ejecutarLlamadaConCuerpo(
            llamada = {
                servicioAgoraApi.inscribirEquipoEnLiga(
                    ligaId = ligaId,
                    peticion = peticion,
                )
            },
            transformar = { dto: ResumenMembresiaUsuarioDto -> dto.aDominio() },
        )
    }

    override suspend fun gestionarCambioPasswordPrimerAcceso(
        nuevaPassword: String?,
        omitirCambio: Boolean,
    ): Resultado<Unit, ErrorRed> {
        val peticion = PeticionCambioPasswordInicialDto(
            nuevaPassword = nuevaPassword?.trim()?.takeIf { it.isNotBlank() },
            omitirCambio = omitirCambio,
        )
        return ejecutarLlamadaSinCuerpo {
            servicioAgoraApi.gestionarPasswordPrimerAcceso(peticion)
        }
    }

    override suspend fun crearLigaComoPresidente(
        nombreLiga: String,
        requiereSedeEquipo: Boolean,
        sedeUnicaLiga: String?,
        partidosPorJornada: Int,
    ): Resultado<ResumenMembresiaUsuario, ErrorRed> {
        val peticion = PeticionCrearLigaDto(
            nombreLiga = nombreLiga.trim(),
            requiereSedeEquipo = requiereSedeEquipo,
            sedeUnicaLiga = sedeUnicaLiga?.trim()?.takeIf { it.isNotBlank() },
            partidosPorJornada = partidosPorJornada,
        )
        return ejecutarLlamadaConCuerpo(
            llamada = { servicioAgoraApi.crearLiga(peticion) },
            transformar = { dto: ResumenMembresiaUsuarioDto -> dto.aDominio() },
        )
    }

    override suspend fun actualizarPerfilUsuario(
        nombreUsuario: String,
        telefonoUsuario: String,
    ): Resultado<PerfilUsuarioAgora, ErrorRed> {
        val peticion = PeticionActualizarPerfilAgoraDto(
            nombreUsuario = nombreUsuario.trim(),
            telefonoUsuario = telefonoUsuario.trim(),
        )
        return ejecutarLlamadaConCuerpo(
            llamada = { servicioAgoraApi.actualizarPerfilUsuario(peticion) },
            transformar = { dto: PerfilUsuarioAgoraDto -> dto.aDominio() },
        )
    }

    private suspend inline fun <reified DTO, DOM> ejecutarLlamadaConCuerpo(
        llamada: () -> HttpResponse,
        transformar: (DTO) -> DOM,
    ): Resultado<DOM, ErrorRed> {
        val respuesta = try {
            llamada()
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: SerializationException) {
            return Resultado.Error(ErrorRed.ErrorSerializacion)
        } catch (_: Throwable) {
            return Resultado.Error(ErrorRed.SinConexion)
        }

        if (respuesta.status.isSuccess()) {
            return try {
                val dto = respuesta.body<DTO>()
                Resultado.Exito(transformar(dto))
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: SerializationException) {
                Resultado.Error(ErrorRed.ErrorSerializacion)
            } catch (excepcion: Throwable) {
                Resultado.Error(ErrorRed.Desconocido(excepcion.message))
            }
        }

        return Resultado.Error(mapearErrorHttp(respuesta))
    }

    private suspend inline fun ejecutarLlamadaSinCuerpo(
        llamada: () -> HttpResponse,
    ): Resultado<Unit, ErrorRed> {
        val respuesta = try {
            llamada()
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: SerializationException) {
            return Resultado.Error(ErrorRed.ErrorSerializacion)
        } catch (_: Throwable) {
            return Resultado.Error(ErrorRed.SinConexion)
        }

        if (respuesta.status.isSuccess()) {
            return Resultado.Exito(Unit)
        }

        return Resultado.Error(mapearErrorHttp(respuesta))
    }

    private suspend fun mapearErrorHttp(respuesta: HttpResponse): ErrorRed {
        val detalleServidor = runCatching { respuesta.bodyAsText() }.getOrNull()
        return when (respuesta.status.value) {
            400, 422 -> ErrorRed.PeticionInvalida
            401 -> ErrorRed.NoAutorizado
            403 -> ErrorRed.AccesoProhibido
            404 -> ErrorRed.NoEncontrado
            409 -> ErrorRed.Conflicto
            in 500..599 -> ErrorRed.Servidor(
                codigo = respuesta.status.value,
                mensaje = detalleServidor,
            )
            else -> ErrorRed.Desconocido(detalleServidor)
        }
    }
}
