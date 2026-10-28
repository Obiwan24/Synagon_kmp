package com.synagon.app.dominio.repositorios

import com.synagon.app.dominio.modelos.FilaClasificacion
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado

/**
 * Contrato del repositorio para consultar la tabla de clasificación oficial de una división.
 */
interface RepositorioClasificacion {

    suspend fun obtenerClasificacionDivision(
        idDivision: String,
    ): Resultado<List<FilaClasificacion>, ErrorRed>
}
