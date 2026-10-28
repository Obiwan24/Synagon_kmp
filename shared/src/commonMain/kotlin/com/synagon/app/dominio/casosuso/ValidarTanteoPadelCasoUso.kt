package com.synagon.app.dominio.casosuso

import com.synagon.app.dominio.modelos.GanadorPartido

/**
 * Resultado detallado de la validación en tiempo real del tanteo de pádel de una pareja.
 */
data class ValidacionPartidoActa(
    val errorSet1: String? = null,
    val errorSet2: String? = null,
    val errorSet3: String? = null,
    val errorGeneralPartido: String? = null,
    val requiereTercerSet: Boolean = false,
    val prohibeTercerSet: Boolean = false,
    val esPartidoValido: Boolean = false,
    val ganador: GanadorPartido? = null,
)

/**
 * Caso de uso que aplica estrictamente las reglas oficiales de tanteo de pádel de Synagon:
 * - Al mejor de 3 sets (el primero en ganar 2 sets gana el partido).
 * - Tanteos legales por set: 6-0 a 6-4, 7-5 o 7-6 (y sus inversos para el visitante).
 * - Si una pareja gana los dos primeros sets (2-0), no se permite registrar un tercer set.
 * - Si empatan a un set (1-1), el tercer set es obligatorio y debe tener tanteo reglamentario.
 */
class ValidarTanteoPadelCasoUso {

    /**
     * Comprueba si los juegos de un único set cumplen el reglamento oficial de pádel.
     */
    fun esSetReglamentario(juegosLocal: Int, juegosVisitante: Int): Boolean {
        if (juegosLocal < 0 || juegosVisitante < 0) return false
        val ganador = maxOf(juegosLocal, juegosVisitante)
        val perdedor = minOf(juegosLocal, juegosVisitante)

        return when (ganador) {
            6 -> perdedor in 0..4
            7 -> perdedor == 5 || perdedor == 6
            else -> false
        }
    }

    /**
     * Valida en tiempo real las cadenas introducidas por el usuario para Set 1, Set 2 y Set 3 opcional.
     */
    fun validarEntradaPartidoEnTiempoReal(
        textoSet1Local: String,
        textoSet1Visitante: String,
        textoSet2Local: String,
        textoSet2Visitante: String,
        textoSet3Local: String,
        textoSet3Visitante: String,
    ): ValidacionPartidoActa {
        val evalSet1 = evaluarSetObligatorio(numeroSet = 1, textoLocal = textoSet1Local, textoVisitante = textoSet1Visitante)
        val evalSet2 = evaluarSetObligatorio(numeroSet = 2, textoLocal = textoSet2Local, textoVisitante = textoSet2Visitante)

        val set1Valido = evalSet1.parJuegos != null && evalSet1.mensajeError == null
        val set2Valido = evalSet2.parJuegos != null && evalSet2.mensajeError == null

        val hayTextoEnSet3 = textoSet3Local.isNotBlank() || textoSet3Visitante.isNotBlank()

        val juegosSet1 = evalSet1.parJuegos
        val juegosSet2 = evalSet2.parJuegos
        if (!set1Valido || !set2Valido || juegosSet1 == null || juegosSet2 == null) {
            val errorSet3Parcial = if (hayTextoEnSet3) {
                evaluarSetOpcionalCuandoHayTexto(textoSet3Local, textoSet3Visitante)
            } else {
                null
            }
            return ValidacionPartidoActa(
                errorSet1 = evalSet1.mensajeError,
                errorSet2 = evalSet2.mensajeError,
                errorSet3 = errorSet3Parcial,
                esPartidoValido = false,
            )
        }

        val (s1Local, s1Visitante) = juegosSet1
        val (s2Local, s2Visitante) = juegosSet2

        val localGanaSet1 = s1Local > s1Visitante
        val localGanaSet2 = s2Local > s2Visitante
        val mismoGanadorEnDosSets = localGanaSet1 == localGanaSet2

        if (mismoGanadorEnDosSets) {
            // El partido terminó 2-0 o 0-2: prohibido disputar Set 3
            if (hayTextoEnSet3) {
                return ValidacionPartidoActa(
                    errorSet3 = "Con resultado 2-0 en sets no está permitido registrar un 3er set.",
                    prohibeTercerSet = true,
                    esPartidoValido = false,
                )
            }
            return ValidacionPartidoActa(
                prohibeTercerSet = true,
                esPartidoValido = true,
                ganador = if (localGanaSet1) GanadorPartido.LOCAL else GanadorPartido.VISITANTE,
            )
        }

        // Empate 1-1 en sets: el tercer set es obligatorio
        if (!hayTextoEnSet3) {
            return ValidacionPartidoActa(
                errorGeneralPartido = "Empate 1-1 en sets: es obligatorio introducir el resultado del Set 3.",
                requiereTercerSet = true,
                esPartidoValido = false,
            )
        }

        val evalSet3 = evaluarSetObligatorio(numeroSet = 3, textoLocal = textoSet3Local, textoVisitante = textoSet3Visitante)
        if (evalSet3.parJuegos == null || evalSet3.mensajeError != null) {
            return ValidacionPartidoActa(
                errorSet3 = evalSet3.mensajeError ?: "Completa ambos marcadores del Set 3.",
                requiereTercerSet = true,
                esPartidoValido = false,
            )
        }

        val (s3Local, s3Visitante) = evalSet3.parJuegos
        val ganadorFinal = if (s3Local > s3Visitante) GanadorPartido.LOCAL else GanadorPartido.VISITANTE

        return ValidacionPartidoActa(
            requiereTercerSet = true,
            esPartidoValido = true,
            ganador = ganadorFinal,
        )
    }

    /**
     * Valida la URL o referencia adjunta de la foto del acta arbitral.
     */
    fun validarReferenciaFotoActa(referencia: String): String? {
        val limpia = referencia.trim()
        if (limpia.isEmpty()) {
            return null // Vacío inicial sin mostrar error intrusivo hasta que edita o intenta enviar
        }
        if (limpia.length < 6) {
            return "Introduce una URL o referencia de archivo válida (mín. 6 caracteres)."
        }
        val formatoValido = limpia.startsWith("http://") ||
            limpia.startsWith("https://") ||
            limpia.startsWith("content://") ||
            limpia.startsWith("file://") ||
            limpia.startsWith("acta://") ||
            limpia.endsWith(".jpg", ignoreCase = true) ||
            limpia.endsWith(".jpeg", ignoreCase = true) ||
            limpia.endsWith(".png", ignoreCase = true) ||
            limpia.endsWith(".webp", ignoreCase = true) ||
            limpia.endsWith(".pdf", ignoreCase = true)

        return if (formatoValido) {
            null
        } else {
            "Usa una URL (https://...) o nombre de imagen (.jpg, .png, .pdf) válido."
        }
    }

    private data class EvaluacionSet(
        val parJuegos: Pair<Int, Int>? = null,
        val mensajeError: String? = null,
    )

    private fun evaluarSetObligatorio(
        numeroSet: Int,
        textoLocal: String,
        textoVisitante: String,
    ): EvaluacionSet {
        if (textoLocal.isBlank() && textoVisitante.isBlank()) {
            return EvaluacionSet(parJuegos = null, mensajeError = null)
        }
        val local = textoLocal.trim().toIntOrNull()
        val visitante = textoVisitante.trim().toIntOrNull()

        if (textoLocal.isNotBlank() && local == null) {
            return EvaluacionSet(mensajeError = "Set $numeroSet: valor local numérico inválido.")
        }
        if (textoVisitante.isNotBlank() && visitante == null) {
            return EvaluacionSet(mensajeError = "Set $numeroSet: valor visitante numérico inválido.")
        }
        if (local == null || visitante == null) {
            return EvaluacionSet(parJuegos = null, mensajeError = null)
        }
        if (!esSetReglamentario(local, visitante)) {
            return EvaluacionSet(
                parJuegos = local to visitante,
                mensajeError = explicarSetIlegal(numeroSet, local, visitante),
            )
        }
        return EvaluacionSet(parJuegos = local to visitante, mensajeError = null)
    }

    private fun evaluarSetOpcionalCuandoHayTexto(
        textoLocal: String,
        textoVisitante: String,
    ): String? {
        val local = textoLocal.trim().toIntOrNull()
        val visitante = textoVisitante.trim().toIntOrNull()
        if (textoLocal.isNotBlank() && local == null) return "Set 3: valor local inválido."
        if (textoVisitante.isNotBlank() && visitante == null) return "Set 3: valor visitante inválido."
        if (local != null && visitante != null && !esSetReglamentario(local, visitante)) {
            return explicarSetIlegal(3, local, visitante)
        }
        return null
    }

    private fun explicarSetIlegal(numeroSet: Int, local: Int, visitante: Int): String {
        return when {
            local == 6 && visitante == 5 || local == 5 && visitante == 6 ->
                "Set $numeroSet ($local-$visitante) ilegal: con 5-5 se juega hasta 7-5 o 7-6."
            local == 6 && visitante == 6 ->
                "Set $numeroSet (6-6) incompleto: debe resolverse en tie-break (7-6 o 6-7)."
            local == 7 && visitante < 5 || visitante == 7 && local < 5 ->
                "Set $numeroSet ($local-$visitante) ilegal: solo se llega a 7 juegos con 7-5 o 7-6."
            local > 7 || visitante > 7 ->
                "Set $numeroSet ($local-$visitante) ilegal: el máximo en pádel oficial es 7 juegos."
            else ->
                "Set $numeroSet ($local-$visitante) no reglamentario. Válidos: 6-0 a 6-4, 7-5 o 7-6."
        }
    }
}
