package com.synagon.app.datos.repositorios

import com.synagon.app.datos.mappers.aDominio
import com.synagon.app.datos.remoto.api.ServicioApiCompeticion
import com.synagon.app.datos.remoto.dto.FilaClasificacionDto
import com.synagon.app.dominio.modelos.FilaClasificacion
import com.synagon.app.dominio.repositorios.RepositorioClasificacion
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Implementación con Ktor Client de [RepositorioClasificacion].
 */
class RepositorioClasificacionImpl(
    private val servicioApiCompeticion: ServicioApiCompeticion,
) : RepositorioClasificacion {

    override suspend fun obtenerClasificacionDivision(
        idDivision: String,
    ): Resultado<List<FilaClasificacion>, ErrorRed> {
        val respuesta = try {
            servicioApiCompeticion.obtenerClasificacionDivision(idDivision)
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: SerializationException) {
            return Resultado.Error(ErrorRed.ErrorSerializacion)
        } catch (_: Throwable) {
            return Resultado.Error(ErrorRed.SinConexion)
        }

        if (respuesta.status.isSuccess()) {
            return try {
                val listaDto = respuesta.body<List<FilaClasificacionDto>>()
                Resultado.Exito(
                    listaDto
                        .map { it.aDominio() }
                        .sortedBy { it.posicion }
                )
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
