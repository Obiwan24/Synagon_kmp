package com.synagon.app.presentacion.pantallas.acta

import com.synagon.app.dominio.casosuso.ValidacionPartidoActa
import com.synagon.app.dominio.modelos.GanadorPartido
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada

/**
 * Identifica el set sobre el que el capitán está editando juegos en el acta.
 */
enum class NumeroSetActa(val numero: Int, val etiqueta: String) {
    SET_1(1, "Set 1"),
    SET_2(2, "Set 2"),
    SET_3(3, "Set 3 (Opc.)"),
}

/**
 * Identifica el bando (equipo local o visitante) al introducir los juegos de un set.
 */
enum class BandoActa {
    LOCAL,
    VISITANTE,
}

/**
 * Estado editable y validado en tiempo real del partido de una pareja dentro del formulario de acta.
 */
data class EntradaParejaActaUi(
    val numeroPareja: Int,
    val nombreParejaLocal: String,
    val nombreParejaVisitante: String,
    val idJugadorLocalUno: String? = null,
    val idJugadorLocalDos: String? = null,
    val idJugadorVisitanteUno: String? = null,
    val idJugadorVisitanteDos: String? = null,
    val set1Local: String = "",
    val set1Visitante: String = "",
    val set2Local: String = "",
    val set2Visitante: String = "",
    val set3Local: String = "",
    val set3Visitante: String = "",
    val validacion: ValidacionPartidoActa = ValidacionPartidoActa(),
) {
    val tieneErroresVisibles: Boolean
        get() = validacion.errorSet1 != null ||
            validacion.errorSet2 != null ||
            validacion.errorSet3 != null ||
            validacion.errorGeneralPartido != null

    fun resumenMarcador(): String {
        val tramos = buildList {
            if (set1Local.isNotBlank() || set1Visitante.isNotBlank()) {
                add("${set1Local.ifBlank { "?" }}-${set1Visitante.ifBlank { "?" }}")
            }
            if (set2Local.isNotBlank() || set2Visitante.isNotBlank()) {
                add("${set2Local.ifBlank { "?" }}-${set2Visitante.ifBlank { "?" }}")
            }
            if (set3Local.isNotBlank() || set3Visitante.isNotBlank()) {
                add("${set3Local.ifBlank { "?" }}-${set3Visitante.ifBlank { "?" }}")
            }
        }
        return if (tramos.isEmpty()) "Sin marcador" else tramos.joinToString(" / ")
    }
}

/**
 * Estado único e inmutable de la interfaz para [PantallaSubirActa].
 */
data class EstadoUi(
    val cargando: Boolean = true,
    val enviando: Boolean = false,
    val idEnfrentamiento: String = DatosSemillaJornada.ID_ENFRENTAMIENTO_INICIAL,
    val numeroJornada: Int = 5,
    val nombreDivision: String = "Primera División Sevilla",
    val nombreEquipoLocal: String = DatosSemillaJornada.NOMBRE_EQUIPO_PROPIO,
    val nombreEquipoVisitante: String = DatosSemillaJornada.NOMBRE_EQUIPO_RIVAL,
    val entradasParejas: List<EntradaParejaActaUi> = crearEntradasInicialesPorDefecto(),
    val urlFotoActa: String = "",
    val errorFotoActa: String? = null,
    val observaciones: String = "",
    val intentoEnvioRealizado: Boolean = false,
    val mostrarDialogoConfirmacion: Boolean = false,
    val actaEnviada: Boolean = false,
    val usandoRespaldoLocal: Boolean = false,
    val mensajeEstadoConexion: String? = null,
) {
    val victoriasLocal: Int
        get() = entradasParejas.count { it.validacion.ganador == GanadorPartido.LOCAL }

    val victoriasVisitante: Int
        get() = entradasParejas.count { it.validacion.ganador == GanadorPartido.VISITANTE }

    val partidosValidosCompletados: Int
        get() = entradasParejas.count { it.validacion.esPartidoValido }

    val fotoActaValida: Boolean
        get() = urlFotoActa.trim().isNotEmpty() && errorFotoActa == null

    val formularioValido: Boolean
        get() = entradasParejas.size == 3 &&
            entradasParejas.all { it.validacion.esPartidoValido } &&
            fotoActaValida

    companion object {
        fun crearEntradasInicialesPorDefecto(): List<EntradaParejaActaUi> = listOf(
            EntradaParejaActaUi(
                numeroPareja = 1,
                nombreParejaLocal = "Álvaro Iglesias / Javier Morales",
                nombreParejaVisitante = "Pareja 1 · Club Pádel Los Remedios",
                idJugadorLocalUno = "40000000-0000-0000-0000-000000000001",
                idJugadorLocalDos = "40000000-0000-0000-0000-000000000002",
            ),
            EntradaParejaActaUi(
                numeroPareja = 2,
                nombreParejaLocal = "Carlos Sánchez / Pablo Navarro",
                nombreParejaVisitante = "Pareja 2 · Club Pádel Los Remedios",
                idJugadorLocalUno = "40000000-0000-0000-0000-000000000003",
                idJugadorLocalDos = "40000000-0000-0000-0000-000000000004",
            ),
            EntradaParejaActaUi(
                numeroPareja = 3,
                nombreParejaLocal = "Alejandro Romero / Manuel Torres",
                nombreParejaVisitante = "Pareja 3 · Club Pádel Los Remedios",
                idJugadorLocalUno = "40000000-0000-0000-0000-000000000005",
                idJugadorLocalDos = "40000000-0000-0000-0000-000000000006",
            ),
        )
    }
}

/**
 * Intenciones del usuario sobre la pantalla/diálogo de subida de acta arbitral.
 */
sealed interface EventoUi {
    data object RecargarDatosEnfrentamiento : EventoUi
    data class CambiarJuegosSet(
        val numeroPareja: Int,
        val set: NumeroSetActa,
        val bando: BandoActa,
        val valor: String,
    ) : EventoUi
    data class ActualizarUrlFotoActa(val url: String) : EventoUi
    data object AdjuntarReferenciaFotoCamara : EventoUi
    data class ActualizarObservaciones(val texto: String) : EventoUi
    data object LimpiarFormularioActa : EventoUi
    data object SolicitarRevisionEnvioActa : EventoUi
    data object CerrarDialogoConfirmacion : EventoUi
    data object ConfirmarSubidaActa : EventoUi
}

/**
 * Efectos efímeros de un solo uso para mostrar notificaciones o confirmar acciones.
 */
sealed interface EfectoUi {
    data class MostrarMensaje(val texto: String) : EfectoUi
}
