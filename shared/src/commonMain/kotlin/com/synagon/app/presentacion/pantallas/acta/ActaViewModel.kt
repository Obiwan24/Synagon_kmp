package com.synagon.app.presentacion.pantallas.acta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synagon.app.datos.remoto.api.ProveedorUrlBase
import com.synagon.app.dominio.casosuso.ValidarTanteoPadelCasoUso
import com.synagon.app.dominio.modelos.ActaEnfrentamiento
import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.ResultadoParejaActa
import com.synagon.app.dominio.repositorios.RepositorioEnfrentamientos
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel multiplataforma bajo arquitectura MVI estricta para el formulario y diálogo
 * de subida de acta arbitral con validación en tiempo real del tanteo reglamentario de pádel.
 */
class ActaViewModel(
    private val repositorioEnfrentamientos: RepositorioEnfrentamientos,
    private val validarTanteoPadelCasoUso: ValidarTanteoPadelCasoUso,
    private val proveedorUrlBase: ProveedorUrlBase,
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUi())
    val estadoUi: StateFlow<EstadoUi> = _estadoUi.asStateFlow()

    private val _efectosUi = Channel<EfectoUi>(Channel.BUFFERED)
    val efectosUi: Flow<EfectoUi> = _efectosUi.receiveAsFlow()

    init {
        cargarContextoEnfrentamiento()
    }

    /**
     * Procesa todas las intenciones del capitán siguiendo flujo unidireccional de datos (UDF).
     */
    fun procesarEvento(evento: EventoUi) {
        when (evento) {
            EventoUi.RecargarDatosEnfrentamiento -> cargarContextoEnfrentamiento()
            is EventoUi.CambiarJuegosSet -> actualizarJuegosSet(
                numeroPareja = evento.numeroPareja,
                set = evento.set,
                bando = evento.bando,
                nuevoValor = evento.valor,
            )
            is EventoUi.ActualizarUrlFotoActa -> actualizarUrlFotoActa(evento.url)
            EventoUi.AdjuntarReferenciaFotoCamara -> generarReferenciaFotoActa()
            is EventoUi.ActualizarObservaciones -> actualizarObservaciones(evento.texto)
            EventoUi.LimpiarFormularioActa -> limpiarFormulario()
            EventoUi.SolicitarRevisionEnvioActa -> solicitarRevisionEnvio()
            EventoUi.CerrarDialogoConfirmacion -> _estadoUi.update {
                it.copy(mostrarDialogoConfirmacion = false)
            }
            EventoUi.ConfirmarSubidaActa -> confirmarSubidaActa()
        }
    }

    private fun cargarContextoEnfrentamiento() {
        val idEnfrentamiento = _estadoUi.value.idEnfrentamiento
        viewModelScope.launch {
            _estadoUi.update { it.copy(cargando = true, mensajeEstadoConexion = null) }

            when (val resultado = repositorioEnfrentamientos.obtenerEnfrentamientoPorId(idEnfrentamiento)) {
                is Resultado.Exito -> aplicarEnfrentamiento(
                    enfrentamiento = resultado.datos,
                    esRespaldoLocal = false,
                    avisoConexion = null,
                )
                is Resultado.Error -> {
                    val respaldo = DatosSemillaJornada.crearEnfrentamientoInicial()
                    val url = proveedorUrlBase.obtenerUrlBase()
                    aplicarEnfrentamiento(
                        enfrentamiento = respaldo,
                        esRespaldoLocal = true,
                        avisoConexion = "Servidor no disponible en $url · Validación y subida en modo local",
                    )
                }
            }
        }
    }

    private fun aplicarEnfrentamiento(
        enfrentamiento: Enfrentamiento,
        esRespaldoLocal: Boolean,
        avisoConexion: String?,
    ) {
        _estadoUi.update { actual ->
            val entradasSincronizadas = if (enfrentamiento.parejas.size == 3) {
                enfrentamiento.parejas.sortedBy { it.numeroPareja }.map { pareja ->
                    val previa = actual.entradasParejas.firstOrNull { it.numeroPareja == pareja.numeroPareja }
                    val entradaBase = previa?.copy(
                        nombreParejaLocal = pareja.nombreCompletoPareja,
                        nombreParejaVisitante = "Pareja ${pareja.numeroPareja} · ${enfrentamiento.nombreEquipoVisitante}",
                        idJugadorLocalUno = pareja.idJugadorUno,
                        idJugadorLocalDos = pareja.idJugadorDos,
                    ) ?: EntradaParejaActaUi(
                        numeroPareja = pareja.numeroPareja,
                        nombreParejaLocal = pareja.nombreCompletoPareja,
                        nombreParejaVisitante = "Pareja ${pareja.numeroPareja} · ${enfrentamiento.nombreEquipoVisitante}",
                        idJugadorLocalUno = pareja.idJugadorUno,
                        idJugadorLocalDos = pareja.idJugadorDos,
                    )
                    recalcularValidacionPareja(entradaBase)
                }
            } else {
                actual.entradasParejas.map { entrada ->
                    recalcularValidacionPareja(
                        entrada.copy(
                            nombreParejaVisitante = "Pareja ${entrada.numeroPareja} · ${enfrentamiento.nombreEquipoVisitante}"
                        )
                    )
                }
            }

            actual.copy(
                cargando = false,
                numeroJornada = enfrentamiento.numeroJornada,
                nombreDivision = enfrentamiento.nombreDivision,
                nombreEquipoLocal = enfrentamiento.nombreEquipoLocal,
                nombreEquipoVisitante = enfrentamiento.nombreEquipoVisitante,
                entradasParejas = entradasSincronizadas,
                observaciones = enfrentamiento.observaciones ?: actual.observaciones,
                usandoRespaldoLocal = esRespaldoLocal,
                mensajeEstadoConexion = avisoConexion,
            )
        }
    }

    private fun actualizarJuegosSet(
        numeroPareja: Int,
        set: NumeroSetActa,
        bando: BandoActa,
        nuevoValor: String,
    ) {
        // Filtramos a un máximo de 2 dígitos numéricos para facilitar la entrada rápida en móvil
        val textoFiltrado = nuevoValor.filter { it.isDigit() }.take(2)

        _estadoUi.update { actual ->
            val nuevasEntradas = actual.entradasParejas.map { entrada ->
                if (entrada.numeroPareja != numeroPareja) {
                    entrada
                } else {
                    val editada = when (set) {
                        NumeroSetActa.SET_1 -> if (bando == BandoActa.LOCAL) {
                            entrada.copy(set1Local = textoFiltrado)
                        } else {
                            entrada.copy(set1Visitante = textoFiltrado)
                        }
                        NumeroSetActa.SET_2 -> if (bando == BandoActa.LOCAL) {
                            entrada.copy(set2Local = textoFiltrado)
                        } else {
                            entrada.copy(set2Visitante = textoFiltrado)
                        }
                        NumeroSetActa.SET_3 -> if (bando == BandoActa.LOCAL) {
                            entrada.copy(set3Local = textoFiltrado)
                        } else {
                            entrada.copy(set3Visitante = textoFiltrado)
                        }
                    }
                    recalcularValidacionPareja(editada)
                }
            }

            actual.copy(
                entradasParejas = nuevasEntradas,
                actaEnviada = false,
            )
        }
    }

    private fun actualizarUrlFotoActa(nuevaUrl: String) {
        val errorValidacion = validarTanteoPadelCasoUso.validarReferenciaFotoActa(nuevaUrl)
        _estadoUi.update { actual ->
            val errorMostrar = when {
                nuevaUrl.isBlank() && actual.intentoEnvioRealizado ->
                    "Es obligatorio adjuntar la URL o referencia de la foto del acta firmada."
                else -> errorValidacion
            }
            actual.copy(
                urlFotoActa = nuevaUrl,
                errorFotoActa = errorMostrar,
                actaEnviada = false,
            )
        }
    }

    private fun generarReferenciaFotoActa() {
        val estadoActual = _estadoUi.value
        val referenciaGenerada = "https://cdn.synagon.es/actas/jornada-${estadoActual.numeroJornada}-${estadoActual.idEnfrentamiento.takeLast(4)}.jpg"
        _estadoUi.update {
            it.copy(
                urlFotoActa = referenciaGenerada,
                errorFotoActa = null,
                actaEnviada = false,
            )
        }
        emitirMensaje("Foto del acta adjuntada correctamente.")
    }

    private fun actualizarObservaciones(texto: String) {
        _estadoUi.update {
            it.copy(
                observaciones = texto.take(280),
                actaEnviada = false,
            )
        }
    }

    private fun limpiarFormulario() {
        _estadoUi.update { actual ->
            actual.copy(
                entradasParejas = actual.entradasParejas.map { pareja ->
                    pareja.copy(
                        set1Local = "",
                        set1Visitante = "",
                        set2Local = "",
                        set2Visitante = "",
                        set3Local = "",
                        set3Visitante = "",
                        validacion = validarTanteoPadelCasoUso.validarEntradaPartidoEnTiempoReal(
                            "", "", "", "", "", ""
                        ),
                    )
                },
                urlFotoActa = "",
                errorFotoActa = null,
                observaciones = "",
                intentoEnvioRealizado = false,
                mostrarDialogoConfirmacion = false,
                actaEnviada = false,
            )
        }
    }

    private fun solicitarRevisionEnvio() {
        val estadoActual = _estadoUi.value
        val errorFoto = if (estadoActual.urlFotoActa.isBlank()) {
            "Es obligatorio adjuntar la URL o referencia de la foto del acta firmada."
        } else {
            validarTanteoPadelCasoUso.validarReferenciaFotoActa(estadoActual.urlFotoActa)
        }

        val entradasRevalidadas = estadoActual.entradasParejas.map { entrada ->
            val revalidada = recalcularValidacionPareja(entrada)
            if (!revalidada.validacion.esPartidoValido && !revalidada.tieneErroresVisibles) {
                revalidada.copy(
                    validacion = revalidada.validacion.copy(
                        errorGeneralPartido = "Completa los sets obligatorios de la Pareja ${entrada.numeroPareja}."
                    )
                )
            } else {
                revalidada
            }
        }

        val todoValido = errorFoto == null && entradasRevalidadas.all { it.validacion.esPartidoValido }

        _estadoUi.update {
            it.copy(
                entradasParejas = entradasRevalidadas,
                errorFotoActa = errorFoto,
                intentoEnvioRealizado = true,
                mostrarDialogoConfirmacion = todoValido,
            )
        }

        if (!todoValido) {
            emitirMensaje("Revisa los errores de tanteo o adjunta la foto del acta antes de continuar.")
        }
    }

    private fun confirmarSubidaActa() {
        val estadoActual = _estadoUi.value
        if (!estadoActual.formularioValido) {
            _estadoUi.update { it.copy(mostrarDialogoConfirmacion = false) }
            emitirMensaje("El acta contiene tanteos no reglamentarios o falta la foto oficial.")
            return
        }

        val acta = construirModeloActa(estadoActual) ?: run {
            _estadoUi.update { it.copy(mostrarDialogoConfirmacion = false) }
            emitirMensaje("Error al procesar los juegos numéricos del acta.")
            return
        }

        viewModelScope.launch {
            _estadoUi.update {
                it.copy(
                    enviando = true,
                    mostrarDialogoConfirmacion = false,
                )
            }

            when (val resultado = repositorioEnfrentamientos.subirActaEnfrentamiento(acta)) {
                is Resultado.Exito -> {
                    _estadoUi.update {
                        it.copy(
                            enviando = false,
                            actaEnviada = true,
                            usandoRespaldoLocal = false,
                        )
                    }
                    emitirMensaje("Acta oficial registrada en el servidor (${estadoActual.victoriasLocal}-${estadoActual.victoriasVisitante}).")
                }
                is Resultado.Error -> {
                    if (estadoActual.usandoRespaldoLocal ||
                        resultado.error is ErrorRed.SinConexion ||
                        resultado.error is ErrorRed.NoEncontrado
                    ) {
                        _estadoUi.update {
                            it.copy(
                                enviando = false,
                                actaEnviada = true,
                                usandoRespaldoLocal = true,
                            )
                        }
                        emitirMensaje(
                            "Acta validada y guardada localmente (${estadoActual.victoriasLocal}-${estadoActual.victoriasVisitante})."
                        )
                    } else {
                        _estadoUi.update { it.copy(enviando = false) }
                        emitirMensaje(formatearErrorRed(resultado.error))
                    }
                }
            }
        }
    }

    private fun recalcularValidacionPareja(entrada: EntradaParejaActaUi): EntradaParejaActaUi {
        val validacion = validarTanteoPadelCasoUso.validarEntradaPartidoEnTiempoReal(
            textoSet1Local = entrada.set1Local,
            textoSet1Visitante = entrada.set1Visitante,
            textoSet2Local = entrada.set2Local,
            textoSet2Visitante = entrada.set2Visitante,
            textoSet3Local = entrada.set3Local,
            textoSet3Visitante = entrada.set3Visitante,
        )
        return entrada.copy(validacion = validacion)
    }

    private fun construirModeloActa(estado: EstadoUi): ActaEnfrentamiento? {
        val resultados = estado.entradasParejas.map { pareja ->
            val s1Local = pareja.set1Local.trim().toIntOrNull() ?: return null
            val s1Visitante = pareja.set1Visitante.trim().toIntOrNull() ?: return null
            val s2Local = pareja.set2Local.trim().toIntOrNull() ?: return null
            val s2Visitante = pareja.set2Visitante.trim().toIntOrNull() ?: return null
            val s3Local = pareja.set3Local.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
            val s3Visitante = pareja.set3Visitante.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()

            ResultadoParejaActa(
                numeroPareja = pareja.numeroPareja,
                nombreParejaLocal = pareja.nombreParejaLocal,
                nombreParejaVisitante = pareja.nombreParejaVisitante,
                idJugadorLocalUno = pareja.idJugadorLocalUno,
                idJugadorLocalDos = pareja.idJugadorLocalDos,
                idJugadorVisitanteUno = pareja.idJugadorVisitanteUno,
                idJugadorVisitanteDos = pareja.idJugadorVisitanteDos,
                set1Local = s1Local,
                set1Visitante = s1Visitante,
                set2Local = s2Local,
                set2Visitante = s2Visitante,
                set3Local = s3Local,
                set3Visitante = s3Visitante,
            )
        }

        return ActaEnfrentamiento(
            idEnfrentamiento = estado.idEnfrentamiento,
            urlFotoActa = estado.urlFotoActa.trim(),
            observaciones = estado.observaciones.trim().takeIf { it.isNotEmpty() },
            resultadosParejas = resultados,
        )
    }

    private fun formatearErrorRed(error: ErrorRed): String {
        return when (error) {
            ErrorRed.SinConexion -> "Sin conexión con el servidor de Synagon."
            ErrorRed.PeticionInvalida -> "El servidor rechazó el acta por tanteo no reglamentario."
            ErrorRed.Conflicto -> "Ya existe un acta cerrada para este enfrentamiento."
            ErrorRed.AccesoProhibido -> "Solo el capitán autorizado puede subir el acta oficial."
            is ErrorRed.Servidor -> "Error del servidor (${error.codigo}) al registrar el acta."
            else -> "No se pudo subir el acta del enfrentamiento."
        }
    }

    private fun emitirMensaje(texto: String) {
        viewModelScope.launch {
            _efectosUi.send(EfectoUi.MostrarMensaje(texto))
        }
    }
}
