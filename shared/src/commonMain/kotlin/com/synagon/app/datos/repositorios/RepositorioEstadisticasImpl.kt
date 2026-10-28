package com.synagon.app.datos.repositorios

import com.synagon.app.datos.mappers.aDominio
import com.synagon.app.datos.remoto.api.ServicioApiCompeticion
import com.synagon.app.datos.remoto.dto.EstadisticaJugadorDto
import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.dominio.repositorios.RepositorioEstadisticas
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Implementación con Ktor Client de [RepositorioEstadisticas].
 */
class RepositorioEstadisticasImpl(
    private val servicioApiCompeticion: ServicioApiCompeticion,
) : RepositorioEstadisticas {

    override suspend fun obtenerEstadisticasEquipo(
        idEquipo: String,
    ): Resultado<List<EstadisticaJugador>, ErrorRed> {
        val respuesta = try {
            servicioApiCompeticion.obtenerEstadisticasJugadoresEquipo(idEquipo)
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: SerializationException) {
            return Resultado.Error(ErrorRed.ErrorSerializacion)
        } catch (_: Throwable) {
            return Resultado.Error(ErrorRed.SinConexion)
        }

        if (respuesta.status.isSuccess()) {
            return try {
                val listaDto = respuesta.body<List<EstadisticaJugadorDto>>()
                Resultado.Exito(listaDto.map { it.aDominio() })
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: SerializationException) {
                Resultado.Error(ErrorRed.ErrorSerializacion)
            } catch (excepcion: Throwable) {
                Resultado.Error(ErrorRed.Desconocido(excepcion.message))
            }
        }

        val detalle = runCatching { respuesta.bodyAsText() }.getOrNull()
        val error = when (respuesta.status.value) {
            400, 422 -> ErrorRed.PeticionInvalida
            401 -> ErrorRed.NoAutorizado
            403 -> ErrorRed.AccesoProhibido
            404 -> ErrorRed.NoEncontrado
            409 -> ErrorRed.Conflicto
            in 500..599 -> ErrorRed.Servidor(respuesta.status.value, detalle)
            else -> ErrorRed.Desconocido(detalle)
        }
        return Resultado.Error(error)
    }
}
