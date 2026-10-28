package com.synagon.app.presentacion.pantallas.jornada

import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.EstadoDisponibilidad
import com.synagon.app.dominio.modelos.JugadorEquipo
import com.synagon.app.dominio.modelos.RolUsuario

/**
 * Representa el estado editable de una pareja (Pareja 1, Pareja 2 o Pareja 3) en el panel de capitán.
 */
data class BorradorParejaConvocatoria(
    val numeroPareja: Int,
    val idJugadorUno: String? = null,
    val idJugadorDos: String? = null,
) {
    val estaCompleta: Boolean
        get() = idJugadorUno != null && idJugadorDos != null

    fun contieneJugador(idJugador: String): Boolean {
        return idJugadorUno == idJugador || idJugadorDos == idJugador
    }
}

/**
 * Estado inmutable único de la interfaz de usuario para [PantallaDetalleJornada].
 */
data class EstadoUi(
    val cargando: Boolean = true,
    val procesandoAccion: Boolean = false,
    val idEnfrentamientoActual: String = DatosSemillaJornada.ID_ENFRENTAMIENTO_INICIAL,
    val enfrentamiento: Enfrentamiento? = null,
    val idJugadorActual: String = DatosSemillaJornada.ID_JUGADOR_ACTUAL,
    val nombreJugadorActual: String = DatosSemillaJornada.NOMBRE_JUGADOR_ACTUAL,
    val rolUsuario: RolUsuario = RolUsuario.CAPITAN,
    val idPropuestaSeleccionadaCapitan: String? = null,
    val pistasConfirmacion: String = "Club Pádel Retiro · Pistas 1, 2 y 3",
    val parejasBorrador: List<BorradorParejaConvocatoria> = crearBorradorParejasVacio(),
    val numeroParejaActiva: Int = 1,
    val usandoRespaldoLocal: Boolean = false,
    val urlBaseBackend: String = "",
    val mensajeEstadoConexion: String? = null,
) {
    val esCapitan: Boolean
        get() = rolUsuario == RolUsuario.CAPITAN

    val idsJugadoresConvocados: Set<String>
        get() = parejasBorrador
            .flatMap { listOfNotNull(it.idJugadorUno, it.idJugadorDos) }
            .toSet()

    val totalConvocados: Int
        get() = idsJugadoresConvocados.size

    val convocatoriaCompleta: Boolean
        get() = totalConvocados == Enfrentamiento.MINIMO_JUGADORES_CONVOCATORIA &&
            parejasBorrador.all { it.estaCompleta }

    val jugadoresDisponiblesConvocatoria: List<JugadorEquipo>
        get() = enfrentamiento?.obtenerJugadoresDisponibles(idPropuestaSeleccionadaCapitan).orEmpty()

    fun obtenerNumeroParejaDeJugador(idJugador: String): Int? {
        return parejasBorrador.firstOrNull { it.contieneJugador(idJugador) }?.numeroPareja
    }

    fun obtenerNombreJugador(idJugador: String?): String? {
        if (idJugador == null) return null
        val enPlantilla = enfrentamiento?.jugadoresEquipo
            ?.firstOrNull { it.id == idJugador }
            ?.nombreCompleto
        if (enPlantilla != null) return enPlantilla
        return enfrentamiento?.disponibilidades
            ?.firstOrNull { it.idJugador == idJugador }
            ?.nombreJugador
    }

    companion object {
        fun crearBorradorParejasVacio(): List<BorradorParejaConvocatoria> = listOf(
            BorradorParejaConvocatoria(numeroPareja = 1),
            BorradorParejaConvocatoria(numeroPareja = 2),
            BorradorParejaConvocatoria(numeroPareja = 3),
        )
    }
}

/**
 * Intenciones y acciones del usuario sobre la pantalla de detalle de jornada.
 */
sealed interface EventoUi {
    data object RecargarJornada : EventoUi
    data class CambiarRolVista(val nuevoRol: RolUsuario) : EventoUi
    data class VotarDisponibilidad(
        val idPropuestaFecha: String,
        val estado: EstadoDisponibilidad,
    ) : EventoUi
    data class SeleccionarPropuestaParaConfirmar(val idPropuestaFecha: String) : EventoUi
    data class ActualizarPistasConfirmacion(val pistas: String) : EventoUi
    data object ConfirmarFechaDefinitiva : EventoUi
    data class SeleccionarParejaActiva(val numeroPareja: Int) : EventoUi
    data class AlternarConvocatoriaJugador(val idJugador: String) : EventoUi
    data class QuitarJugadorDePareja(val numeroPareja: Int, val posicion: Int) : EventoUi
    data object LimpiarConvocatoria : EventoUi
    data object ConfirmarConvocatoriaCapitan : EventoUi
}

/**
 * Efectos efímeros de un solo uso (mensajes informativos o navegación).
 */
sealed interface EfectoUi {
    data class MostrarMensaje(val texto: String) : EfectoUi
}
