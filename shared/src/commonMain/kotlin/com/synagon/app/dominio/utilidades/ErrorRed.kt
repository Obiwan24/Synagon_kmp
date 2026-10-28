package com.synagon.app.dominio.utilidades

/**
 * Modela los posibles errores originados en operaciones de red o comunicación con el servidor.
 */
sealed interface ErrorRed {
    data object SinConexion : ErrorRed
    data object PeticionInvalida : ErrorRed
    data object NoAutorizado : ErrorRed
    data object AccesoProhibido : ErrorRed
    data object NoEncontrado : ErrorRed
    data object Conflicto : ErrorRed
    data object ErrorSerializacion : ErrorRed
    data class Servidor(val codigo: Int, val mensaje: String? = null) : ErrorRed
    data class Desconocido(val mensaje: String? = null) : ErrorRed
}
