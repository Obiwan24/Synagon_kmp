package com.synagon.app.dominio.repositorios

import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado

/**
 * Contrato del repositorio para obtener las estadísticas individuales de los miembros de un equipo.
 */
interface RepositorioEstadisticas {

    suspend fun obtenerEstadisticasEquipo(
        idEquipo: String,
    ): Resultado<List<EstadisticaJugador>, ErrorRed>
}
