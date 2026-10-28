package com.synagon.app.dominio.repositorios

import com.synagon.app.dominio.modelos.ActaEnfrentamiento
import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.EstadoDisponibilidad
import com.synagon.app.dominio.modelos.ParejaPartido
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import kotlinx.datetime.LocalDateTime

/**
 * Contrato de acceso a datos de enfrentamientos, gestión de jornada y subida de acta arbitral.
 */
interface RepositorioEnfrentamientos {

    suspend fun obtenerEnfrentamientoPorId(
        idEnfrentamiento: String,
    ): Resultado<Enfrentamiento, ErrorRed>

    suspend fun registrarDisponibilidadJugador(
        idEnfrentamiento: String,
        idPropuestaFecha: String,
        idJugador: String,
        nombreJugador: String,
        estado: EstadoDisponibilidad,
    ): Resultado<Enfrentamiento, ErrorRed>

    suspend fun confirmarFechaDefinitiva(
        idEnfrentamiento: String,
        idPropuestaFecha: String,
        fechaDefinitiva: LocalDateTime,
        pistas: String,
    ): Resultado<Enfrentamiento, ErrorRed>

    suspend fun confirmarConvocatoriaYParejas(
        idEnfrentamiento: String,
        idEquipo: String,
        parejas: List<ParejaPartido>,
    ): Resultado<Enfrentamiento, ErrorRed>

    suspend fun subirActaEnfrentamiento(
        acta: ActaEnfrentamiento,
    ): Resultado<Enfrentamiento, ErrorRed>
}
