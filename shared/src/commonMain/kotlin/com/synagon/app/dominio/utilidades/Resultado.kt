package com.synagon.app.dominio.utilidades

/**
 * Representa el resultado de una operación que puede culminar con éxito [Exito] o en fallo [Error].
 * Diseñado para evitar el uso de excepciones no controladas en las capas de datos y dominio.
 *
 * @param D Tipo de dato resultante en caso de éxito.
 * @param E Tipo de error en caso de fallo.
 */
sealed interface Resultado<out D, out E> {
    data class Exito<out D>(val datos: D) : Resultado<D, Nothing>
    data class Error<out E>(val error: E) : Resultado<Nothing, E>
}

/**
 * Transforma el valor de éxito si el resultado es [Resultado.Exito].
 */
inline fun <D, E, R> Resultado<D, E>.mapear(transformar: (D) -> R): Resultado<R, E> {
    return when (this) {
        is Resultado.Exito -> Resultado.Exito(transformar(datos))
        is Resultado.Error -> this
    }
}

/**
 * Ejecuta la acción indicada si el resultado es [Resultado.Exito].
 */
inline fun <D, E> Resultado<D, E>.enExito(accion: (D) -> Unit): Resultado<D, E> {
    if (this is Resultado.Exito) {
        accion(datos)
    }
    return this
}

/**
 * Ejecuta la acción indicada si el resultado es [Resultado.Error].
 */
inline fun <D, E> Resultado<D, E>.enError(accion: (E) -> Unit): Resultado<D, E> {
    if (this is Resultado.Error) {
        accion(error)
    }
    return this
}

/**
 * Obtiene los datos en caso de éxito o un valor por defecto si ocurrió un error.
 */
fun <D, E> Resultado<D, E>.obtenerOValorPorDefecto(valorPorDefecto: D): D {
    return when (this) {
        is Resultado.Exito -> datos
        is Resultado.Error -> valorPorDefecto
    }
}

/**
 * Obtiene los datos en caso de éxito o null si ocurrió un error.
 */
fun <D, E> Resultado<D, E>.obtenerONulo(): D? {
    return when (this) {
        is Resultado.Exito -> datos
        is Resultado.Error -> null
    }
}
