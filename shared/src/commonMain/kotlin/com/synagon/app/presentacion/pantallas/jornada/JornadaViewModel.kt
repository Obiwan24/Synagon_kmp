package com.synagon.app.presentacion.pantallas.jornada

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synagon.app.datos.remoto.api.ProveedorUrlBase
import com.synagon.app.dominio.casosuso.ValidarConvocatoriaCasoUso
import com.synagon.app.dominio.modelos.DisponibilidadJugador
import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.EstadoDisponibilidad
import com.synagon.app.dominio.modelos.EstadoEnfrentamiento
import com.synagon.app.dominio.modelos.ParejaPartido
import com.synagon.app.dominio.modelos.RolUsuario
import com.synagon.app.dominio.repositorios.RepositorioEnfrentamientos
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel multiplataforma de gestión de jornada bajo arquitectura MVI estricta y flujo unidireccional (UDF).
 */
class JornadaViewModel(
    private val repositorioEnfrentamientos: RepositorioEnfrentamientos,
    private val validarConvocatoriaCasoUso: ValidarConvocatoriaCasoUso,
    private val proveedorUrlBase: ProveedorUrlBase,
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(
        EstadoUi(
            urlBaseBackend = proveedorUrlBase.obtenerUrlBase(),
        )
    )
    val estadoUi: StateFlow<EstadoUi> = _estadoUi.asStateFlow()

    private val _efectosUi = Channel<EfectoUi>(Channel.BUFFERED)
    val efectosUi: Flow<EfectoUi> = _efectosUi.receiveAsFlow()

    init {
        cargarDetalleJornada()
    }

    /**
     * Punto único de entrada para procesar las intenciones del usuario desde la interfaz.
     */
    fun procesarEvento(evento: EventoUi) {
        when (evento) {
            EventoUi.RecargarJornada -> cargarDetalleJornada()
            is EventoUi.CambiarRolVista -> cambiarRolUsuario(evento.nuevoRol)
            is EventoUi.VotarDisponibilidad -> votarDisponibilidad(
                idPropuestaFecha = evento.idPropuestaFecha,
                nuevoEstado = evento.estado,
            )
            is EventoUi.SeleccionarPropuestaParaConfirmar -> seleccionarPropuestaCapitan(evento.idPropuestaFecha)
            is EventoUi.ActualizarPistasConfirmacion -> actualizarPistas(evento.pistas)
            EventoUi.ConfirmarFechaDefinitiva -> confirmarFechaDefinitiva()
            is EventoUi.SeleccionarParejaActiva -> seleccionarParejaActiva(evento.numeroPareja)
            is EventoUi.AlternarConvocatoriaJugador -> alternarJugadorEnConvocatoria(evento.idJugador)
            is EventoUi.QuitarJugadorDePareja -> quitarJugadorDePareja(
                numeroPareja = evento.numeroPareja,
                posicion = evento.posicion,
            )
            EventoUi.LimpiarConvocatoria -> limpiarConvocatoria()
            EventoUi.ConfirmarConvocatoriaCapitan -> confirmarConvocatoriaCapitan()
        }
    }

    private fun cargarDetalleJornada() {
        val idEnfrentamiento = _estadoUi.value.idEnfrentamientoActual
        viewModelScope.launch {
            _estadoUi.update { it.copy(cargando = true, mensajeEstadoConexion = null) }

            when (val resultado = repositorioEnfrentamientos.obtenerEnfrentamientoPorId(idEnfrentamiento)) {
                is Resultado.Exito -> {
                    aplicarEnfrentamientoCargado(
                        enfrentamiento = resultado.datos,
                        esRespaldoLocal = false,
                        avisoConexion = null,
                    )
                }
                is Resultado.Error -> {
                    val enfrentamientoRespaldo = _estadoUi.value.enfrentamiento
                        ?: DatosSemillaJornada.crearEnfrentamientoInicial()
                    val url = proveedorUrlBase.obtenerUrlBase()
                    aplicarEnfrentamientoCargado(
                        enfrentamiento = enfrentamientoRespaldo,
                        esRespaldoLocal = true,
                        avisoConexion = describirAvisoConexion(resultado.error, url),
                    )
                }
            }
        }
    }

    private fun aplicarEnfrentamientoCargado(
        enfrentamiento: Enfrentamiento,
        esRespaldoLocal: Boolean,
        avisoConexion: String?,
    ) {
        val propuestaInicial = enfrentamiento.propuestasFecha.firstOrNull { it.esSeleccionada }
            ?: enfrentamiento.propuestasFecha.firstOrNull {
                enfrentamiento.alcanzaQuorumDisponibles(it.id)
            }
            ?: enfrentamiento.propuestasFecha.firstOrNull()

        val borradorDesdeEnfrentamiento = if (enfrentamiento.parejas.size == 3) {
            enfrentamiento.parejas.sortedBy { it.numeroPareja }.map { pareja ->
                BorradorParejaConvocatoria(
                    numeroPareja = pareja.numeroPareja,
                    idJugadorUno = pareja.idJugadorUno,
                    idJugadorDos = pareja.idJugadorDos,
                )
            }
        } else {
            EstadoUi.crearBorradorParejasVacio()
        }

        _estadoUi.update { actual ->
            actual.copy(
                cargando = false,
                procesandoAccion = false,
                enfrentamiento = enfrentamiento,
                idPropuestaSeleccionadaCapitan = propuestaInicial?.id,
                pistasConfirmacion = enfrentamiento.pistas
                    ?: propuestaInicial?.pistaLugar
                    ?: actual.pistasConfirmacion,
                parejasBorrador = borradorDesdeEnfrentamiento,
                usandoRespaldoLocal = esRespaldoLocal,
                mensajeEstadoConexion = avisoConexion,
            )
        }
    }

    private fun cambiarRolUsuario(nuevoRol: RolUsuario) {
        _estadoUi.update { it.copy(rolUsuario = nuevoRol) }
    }

    private fun votarDisponibilidad(
        idPropuestaFecha: String,
        nuevoEstado: EstadoDisponibilidad,
    ) {
        val estadoActual = _estadoUi.value
        val enfrentamientoActual = estadoActual.enfrentamiento ?: return

        viewModelScope.launch {
            _estadoUi.update { it.copy(procesandoAccion = true) }

            val resultadoRemoto = repositorioEnfrentamientos.registrarDisponibilidadJugador(
                idEnfrentamiento = enfrentamientoActual.id,
                idPropuestaFecha = idPropuestaFecha,
                idJugador = estadoActual.idJugadorActual,
                nombreJugador = estadoActual.nombreJugadorActual,
                estado = nuevoEstado,
            )

            when (resultadoRemoto) {
                is Resultado.Exito -> {
                    actualizarTrasCambioDisponibilidad(
                        enfrentamientoActualizado = resultadoRemoto.datos,
                        idPropuestaVotada = idPropuestaFecha,
                        nuevoEstado = nuevoEstado,
                        esRespaldoLocal = false,
                    )
                }
                is Resultado.Error -> {
                    if (debeAplicarEnLocal(resultadoRemoto.error, estadoActual.usandoRespaldoLocal)) {
                        val enfrentamientoLocal = aplicarVotoEnMemoria(
                            enfrentamiento = enfrentamientoActual,
                            idPropuestaFecha = idPropuestaFecha,
                            idJugador = estadoActual.idJugadorActual,
                            nombreJugador = estadoActual.nombreJugadorActual,
                            nuevoEstado = nuevoEstado,
                        )
                        actualizarTrasCambioDisponibilidad(
                            enfrentamientoActualizado = enfrentamientoLocal,
                            idPropuestaVotada = idPropuestaFecha,
                            nuevoEstado = nuevoEstado,
                            esRespaldoLocal = true,
                        )
                    } else {
                        _estadoUi.update { it.copy(procesandoAccion = false) }
                        emitirMensaje(formatearMensajeErrorRed(resultadoRemoto.error))
                    }
                }
            }
        }
    }

    private fun actualizarTrasCambioDisponibilidad(
        enfrentamientoActualizado: Enfrentamiento,
        idPropuestaVotada: String,
        nuevoEstado: EstadoDisponibilidad,
        esRespaldoLocal: Boolean,
    ) {
        _estadoUi.update { actual ->
            val idsDisponiblesEnPropuestaActiva = enfrentamientoActualizado
                .obtenerJugadoresDisponibles(actual.idPropuestaSeleccionadaCapitan)
                .map { it.id }
                .toSet()

            val borradorDepurado = actual.parejasBorrador.map { pareja ->
                pareja.copy(
                    idJugadorUno = pareja.idJugadorUno?.takeIf { it in idsDisponiblesEnPropuestaActiva },
                    idJugadorDos = pareja.idJugadorDos?.takeIf { it in idsDisponiblesEnPropuestaActiva },
                )
            }

            actual.copy(
                procesandoAccion = false,
                enfrentamiento = enfrentamientoActualizado,
                parejasBorrador = borradorDepurado,
                usandoRespaldoLocal = esRespaldoLocal,
            )
        }

        val ordenPropuesta = enfrentamientoActualizado.propuestasFecha
            .firstOrNull { it.id == idPropuestaVotada }
            ?.orden ?: 1
        emitirMensaje("Voto registrado en Opción $ordenPropuesta: ${nuevoEstado.etiqueta}")
    }

    private fun seleccionarPropuestaCapitan(idPropuestaFecha: String) {
        _estadoUi.update { actual ->
            val enfrentamiento = actual.enfrentamiento ?: return@update actual
            val propuesta = enfrentamiento.propuestasFecha.firstOrNull { it.id == idPropuestaFecha }
            val idsDisponibles = enfrentamiento
                .obtenerJugadoresDisponibles(idPropuestaFecha)
                .map { it.id }
                .toSet()

            val borradorFiltrado = actual.parejasBorrador.map { pareja ->
                pareja.copy(
                    idJugadorUno = pareja.idJugadorUno?.takeIf { it in idsDisponibles },
                    idJugadorDos = pareja.idJugadorDos?.takeIf { it in idsDisponibles },
                )
            }

            actual.copy(
                idPropuestaSeleccionadaCapitan = idPropuestaFecha,
                pistasConfirmacion = propuesta?.pistaLugar ?: actual.pistasConfirmacion,
                parejasBorrador = borradorFiltrado,
            )
        }
    }

    private fun actualizarPistas(pistas: String) {
        _estadoUi.update { it.copy(pistasConfirmacion = pistas) }
    }

    private fun confirmarFechaDefinitiva() {
        val estadoActual = _estadoUi.value
        if (!estadoActual.esCapitan) {
            emitirMensaje("Solo el capitán puede confirmar la fecha definitiva.")
            return
        }

        val enfrentamiento = estadoActual.enfrentamiento ?: return
        val idPropuesta = estadoActual.idPropuestaSeleccionadaCapitan
        val propuestaElegida = enfrentamiento.propuestasFecha.firstOrNull { it.id == idPropuesta }
        if (propuestaElegida == null) {
            emitirMensaje("Selecciona primero una de las dos fechas propuestas.")
            return
        }

        val pistasLimpias = estadoActual.pistasConfirmacion.trim().ifBlank {
            propuestaElegida.pistaLugar ?: "Pistas por asignar"
        }

        viewModelScope.launch {
            _estadoUi.update { it.copy(procesandoAccion = true) }

            val resultado = repositorioEnfrentamientos.confirmarFechaDefinitiva(
                idEnfrentamiento = enfrentamiento.id,
                idPropuestaFecha = propuestaElegida.id,
                fechaDefinitiva = propuestaElegida.fechaHora,
                pistas = pistasLimpias,
            )

            when (resultado) {
                is Resultado.Exito -> {
                    _estadoUi.update {
                        it.copy(
                            procesandoAccion = false,
                            enfrentamiento = resultado.datos,
                            usandoRespaldoLocal = false,
                        )
                    }
                    emitirMensaje("Fecha definitiva confirmada en el servidor.")
                }
                is Resultado.Error -> {
                    if (debeAplicarEnLocal(resultado.error, estadoActual.usandoRespaldoLocal)) {
                        val propuestasActualizadas = enfrentamiento.propuestasFecha.map {
                            it.copy(esSeleccionada = it.id == propuestaElegida.id)
                        }
                        val enfrentamientoConfirmado = enfrentamiento.copy(
                            estado = if (enfrentamiento.parejas.size == 3) {
                                EstadoEnfrentamiento.EN_DISPUTA
                            } else {
                                EstadoEnfrentamiento.FECHA_FIJADA
                            },
                            fechaHoraDefinitiva = propuestaElegida.fechaHora,
                            pistas = pistasLimpias,
                            propuestasFecha = propuestasActualizadas,
                        )
                        _estadoUi.update {
                            it.copy(
                                procesandoAccion = false,
                                enfrentamiento = enfrentamientoConfirmado,
                                usandoRespaldoLocal = true,
                            )
                        }
                        emitirMensaje("Fecha definitiva confirmada (Opción ${propuestaElegida.orden}).")
                    } else {
                        _estadoUi.update { it.copy(procesandoAccion = false) }
                        emitirMensaje(formatearMensajeErrorRed(resultado.error))
                    }
                }
            }
        }
    }

    private fun seleccionarParejaActiva(numeroPareja: Int) {
        if (numeroPareja in 1..3) {
            _estadoUi.update { it.copy(numeroParejaActiva = numeroPareja) }
        }
    }

    private fun alternarJugadorEnConvocatoria(idJugador: String) {
        val estadoActual = _estadoUi.value
        if (!estadoActual.esCapitan) return

        // Si el jugador ya está convocado en alguna pareja, lo retiramos de su hueco
        if (idJugador in estadoActual.idsJugadoresConvocados) {
            val nuevoBorrador = estadoActual.parejasBorrador.map { pareja ->
                pareja.copy(
                    idJugadorUno = pareja.idJugadorUno?.takeIf { it != idJugador },
                    idJugadorDos = pareja.idJugadorDos?.takeIf { it != idJugador },
                )
            }
            val parejaLiberada = estadoActual.obtenerNumeroParejaDeJugador(idJugador)
                ?: estadoActual.numeroParejaActiva
            _estadoUi.update {
                it.copy(
                    parejasBorrador = nuevoBorrador,
                    numeroParejaActiva = parejaLiberada,
                )
            }
            return
        }

        if (estadoActual.totalConvocados >= Enfrentamiento.MINIMO_JUGADORES_CONVOCATORIA) {
            emitirMensaje("Ya hay 6 jugadores convocados. Pulsa sobre un convocado para sustituirlo.")
            return
        }

        // Orden de búsqueda de hueco: primero la pareja activa y después el resto en orden 1..3
        val ordenParejas = listOf(estadoActual.numeroParejaActiva) +
            listOf(1, 2, 3).filter { it != estadoActual.numeroParejaActiva }

        val parejaConHueco = ordenParejas
            .mapNotNull { numero -> estadoActual.parejasBorrador.firstOrNull { it.numeroPareja == numero } }
            .firstOrNull { !it.estaCompleta }
            ?: return

        val nuevoBorrador = estadoActual.parejasBorrador.map { pareja ->
            if (pareja.numeroPareja != parejaConHueco.numeroPareja) {
                pareja
            } else if (pareja.idJugadorUno == null) {
                pareja.copy(idJugadorUno = idJugador)
            } else {
                pareja.copy(idJugadorDos = idJugador)
            }
        }

        // Avanzar automáticamente a la siguiente pareja con plazas libres si la actual acaba de completarse
        val siguienteParejaConHueco = nuevoBorrador
            .firstOrNull { it.numeroPareja == estadoActual.numeroParejaActiva && !it.estaCompleta }
            ?.numeroPareja
            ?: nuevoBorrador.firstOrNull { !it.estaCompleta }?.numeroPareja
            ?: estadoActual.numeroParejaActiva

        _estadoUi.update {
            it.copy(
                parejasBorrador = nuevoBorrador,
                numeroParejaActiva = siguienteParejaConHueco,
            )
        }
    }

    private fun quitarJugadorDePareja(numeroPareja: Int, posicion: Int) {
        _estadoUi.update { actual ->
            val nuevoBorrador = actual.parejasBorrador.map { pareja ->
                if (pareja.numeroPareja != numeroPareja) {
                    pareja
                } else if (posicion == 1) {
                    pareja.copy(idJugadorUno = null)
                } else {
                    pareja.copy(idJugadorDos = null)
                }
            }
            actual.copy(
                parejasBorrador = nuevoBorrador,
                numeroParejaActiva = numeroPareja,
            )
        }
    }

    private fun limpiarConvocatoria() {
        _estadoUi.update {
            it.copy(
                parejasBorrador = EstadoUi.crearBorradorParejasVacio(),
                numeroParejaActiva = 1,
            )
        }
    }

    private fun confirmarConvocatoriaCapitan() {
        val estadoActual = _estadoUi.value
        if (!estadoActual.esCapitan) {
            emitirMensaje("Solo el capitán puede confirmar la convocatoria del equipo.")
            return
        }

        val enfrentamiento = estadoActual.enfrentamiento ?: return
        val idsDisponibles = estadoActual.jugadoresDisponiblesConvocatoria.map { it.id }.toSet()

        val parejasConstruidas = estadoActual.parejasBorrador.map { borrador ->
            val idUno = borrador.idJugadorUno.orEmpty()
            val idDos = borrador.idJugadorDos.orEmpty()
            ParejaPartido(
                id = "${enfrentamiento.id}-pareja-${borrador.numeroPareja}",
                numeroPareja = borrador.numeroPareja,
                idJugadorUno = idUno,
                nombreJugadorUno = estadoActual.obtenerNombreJugador(idUno) ?: "",
                idJugadorDos = idDos,
                nombreJugadorDos = estadoActual.obtenerNombreJugador(idDos) ?: "",
            )
        }

        when (val validacion = validarConvocatoriaCasoUso.ejecutar(parejasConstruidas, idsDisponibles)) {
            is Resultado.Error -> {
                emitirMensaje(validacion.error)
                return
            }
            is Resultado.Exito -> {
                enviarConvocatoriaValidada(
                    enfrentamiento = enfrentamiento,
                    parejasValidadas = validacion.datos,
                    usandoRespaldoLocal = estadoActual.usandoRespaldoLocal,
                )
            }
        }
    }

    private fun enviarConvocatoriaValidada(
        enfrentamiento: Enfrentamiento,
        parejasValidadas: List<ParejaPartido>,
        usandoRespaldoLocal: Boolean,
    ) {
        viewModelScope.launch {
            _estadoUi.update { it.copy(procesandoAccion = true) }

            val resultado = repositorioEnfrentamientos.confirmarConvocatoriaYParejas(
                idEnfrentamiento = enfrentamiento.id,
                idEquipo = enfrentamiento.idEquipoLocal,
                parejas = parejasValidadas,
            )

            when (resultado) {
                is Resultado.Exito -> {
                    _estadoUi.update {
                        it.copy(
                            procesandoAccion = false,
                            enfrentamiento = resultado.datos,
                            usandoRespaldoLocal = false,
                        )
                    }
                    emitirMensaje("Convocatoria oficial guardada en el servidor.")
                }
                is Resultado.Error -> {
                    if (debeAplicarEnLocal(resultado.error, usandoRespaldoLocal)) {
                        val enfrentamientoActualizado = enfrentamiento.copy(
                            estado = EstadoEnfrentamiento.EN_DISPUTA,
                            parejas = parejasValidadas,
                        )
                        _estadoUi.update {
                            it.copy(
                                procesandoAccion = false,
                                enfrentamiento = enfrentamientoActualizado,
                                usandoRespaldoLocal = true,
                            )
                        }
                        emitirMensaje("Convocatoria confirmada: Pareja 1, Pareja 2 y Pareja 3 listas.")
                    } else {
                        _estadoUi.update { it.copy(procesandoAccion = false) }
                        emitirMensaje(formatearMensajeErrorRed(resultado.error))
                    }
                }
            }
        }
    }

    private fun aplicarVotoEnMemoria(
        enfrentamiento: Enfrentamiento,
        idPropuestaFecha: String,
        idJugador: String,
        nombreJugador: String,
        nuevoEstado: EstadoDisponibilidad,
    ): Enfrentamiento {
        val votoExistente = enfrentamiento.disponibilidades.firstOrNull {
            it.idPropuestaFecha == idPropuestaFecha && it.idJugador == idJugador
        }

        val nuevasDisponibilidades = if (votoExistente != null) {
            enfrentamiento.disponibilidades.map { voto ->
                if (voto.id == votoExistente.id) {
                    voto.copy(estado = nuevoEstado)
                } else {
                    voto
                }
            }
        } else {
            enfrentamiento.disponibilidades + DisponibilidadJugador(
                id = "voto-local-$idPropuestaFecha-$idJugador",
                idEnfrentamiento = enfrentamiento.id,
                idPropuestaFecha = idPropuestaFecha,
                idJugador = idJugador,
                nombreJugador = nombreJugador,
                estado = nuevoEstado,
            )
        }

        return enfrentamiento.copy(disponibilidades = nuevasDisponibilidades)
    }

    private fun debeAplicarEnLocal(error: ErrorRed, yaEnModoLocal: Boolean): Boolean {
        return yaEnModoLocal || error is ErrorRed.SinConexion || error is ErrorRed.NoEncontrado
    }

    private fun describirAvisoConexion(error: ErrorRed, urlBase: String): String {
        return when (error) {
            ErrorRed.SinConexion -> "Servidor no disponible en $urlBase · Modo interactivo local activo"
            ErrorRed.NoEncontrado -> "Endpoint pendiente en $urlBase · Modo interactivo local activo"
            else -> "${formatearMensajeErrorRed(error)} · Modo interactivo local activo"
        }
    }

    private fun formatearMensajeErrorRed(error: ErrorRed): String {
        return when (error) {
            ErrorRed.SinConexion -> "No se pudo conectar con el servidor de Synagon."
            ErrorRed.PeticionInvalida -> "Los datos enviados no cumplen las reglas de la jornada."
            ErrorRed.NoAutorizado -> "Sesión no autorizada."
            ErrorRed.AccesoProhibido -> "No tienes permisos para realizar esta acción."
            ErrorRed.NoEncontrado -> "No se encontró el enfrentamiento solicitado."
            ErrorRed.Conflicto -> "Conflicto con el estado actual de la jornada."
            ErrorRed.ErrorSerializacion -> "Error al procesar la respuesta del servidor."
            is ErrorRed.Servidor -> "Error en el servidor (${error.codigo})."
            is ErrorRed.Desconocido -> error.mensaje ?: "Ocurrió un error inesperado."
        }
    }

    private fun emitirMensaje(texto: String) {
        viewModelScope.launch {
            _efectosUi.send(EfectoUi.MostrarMensaje(texto))
        }
    }
}
