package com.synagon.app.datos.repositorios

import com.synagon.app.datos.mappers.aDominio
import com.synagon.app.datos.mappers.aDto
import com.synagon.app.datos.remoto.api.ServicioApiJornada
import com.synagon.app.datos.remoto.dto.EnfrentamientoDto
import com.synagon.app.datos.remoto.dto.PeticionConfirmarConvocatoriaDto
import com.synagon.app.datos.remoto.dto.PeticionConfirmarFechaDto
import com.synagon.app.datos.remoto.dto.PeticionParejaAlineadaDto
import com.synagon.app.datos.remoto.dto.PeticionVotarDisponibilidadDto
import com.synagon.app.dominio.modelos.ActaEnfrentamiento
import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.EstadoDisponibilidad
import com.synagon.app.dominio.modelos.ParejaPartido
import com.synagon.app.dominio.repositorios.RepositorioEnfrentamientos
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerializationException

/**
 * Implementación del repositorio de enfrentamientos que se comunica con `ControladorJornada`
 * mediante Ktor Client y encapsula todas las respuestas en [Resultado] sin propagar excepciones de red.
 */
class RepositorioEnfrentamientosImpl(
    private val servicioApiJornada: ServicioApiJornada,
) : RepositorioEnfrentamientos {

    override suspend fun obtenerEnfrentamientoPorId(
        idEnfrentamiento: String,
    ): Resultado<Enfrentamiento, ErrorRed> {
        return ejecutarLlamadaSegura {
            servicioApiJornada.obtenerEnfrentamiento(idEnfrentamiento)
        }
    }

    override suspend fun registrarDisponibilidadJugador(
        idEnfrentamiento: String,
        idPropuestaFecha: String,
        idJugador: String,
        nombreJugador: String,
        estado: EstadoDisponibilidad,
    ): Resultado<Enfrentamiento, ErrorRed> {
        val peticion = PeticionVotarDisponibilidadDto(
            jugadorId = idJugador,
            propuestaFechaId = idPropuestaFecha,
            tipoDisponibilidad = estado.name,
        )
        return ejecutarLlamadaSegura {
            servicioApiJornada.votarDisponibilidad(
                idEnfrentamiento = idEnfrentamiento,
                peticion = peticion,
            )
        }
    }

    override suspend fun confirmarFechaDefinitiva(
        idEnfrentamiento: String,
        idPropuestaFecha: String,
        fechaDefinitiva: LocalDateTime,
        pistas: String,
    ): Resultado<Enfrentamiento, ErrorRed> {
        val peticion = PeticionConfirmarFechaDto(
            propuestaFechaElegidaId = idPropuestaFecha,
            pistas = pistas,
        )
        return ejecutarLlamadaSegura {
            servicioApiJornada.confirmarFechaDefinitiva(
                idEnfrentamiento = idEnfrentamiento,
                peticion = peticion,
            )
        }
    }

    override suspend fun confirmarConvocatoriaYParejas(
        idEnfrentamiento: String,
        idEquipo: String,
        parejas: List<ParejaPartido>,
    ): Resultado<Enfrentamiento, ErrorRed> {
        val peticion = PeticionConfirmarConvocatoriaDto(
            parejas = parejas.map { pareja ->
                PeticionParejaAlineadaDto(
                    numeroPareja = pareja.numeroPareja,
                    jugador1Id = pareja.idJugadorUno,
                    jugador2Id = pareja.idJugadorDos,
                )
            },
        )
        return ejecutarLlamadaSegura {
            servicioApiJornada.confirmarConvocatoria(
                idEnfrentamiento = idEnfrentamiento,
                peticion = peticion,
            )
        }
    }

    override suspend fun subirActaEnfrentamiento(
        acta: ActaEnfrentamiento,
    ): Resultado<Enfrentamiento, ErrorRed> {
        val peticion = acta.aDto()
        return ejecutarLlamadaSegura {
            servicioApiJornada.subirActaEnfrentamiento(
                idEnfrentamiento = acta.idEnfrentamiento,
                peticion = peticion,
            )
        }
    }

    private suspend inline fun ejecutarLlamadaSegura(
        llamada: () -> HttpResponse,
    ): Resultado<Enfrentamiento, ErrorRed> {
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
                val dto = respuesta.body<EnfrentamientoDto>()
                Resultado.Exito(dto.aDominio())
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: SerializationException) {
                Resultado.Error(ErrorRed.ErrorSerializacion)
            } catch (excepcion: Throwable) {
                Resultado.Error(ErrorRed.Desconocido(excepcion.message))
            }
        }

        val detalleServidor = runCatching { respuesta.bodyAsText() }.getOrNull()
        val errorMapeado = when (respuesta.status.value) {
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
        return Resultado.Error(errorMapeado)
    }
}
