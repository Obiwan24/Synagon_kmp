package com.synagon.app.presentacion.pantallas.agora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synagon.app.datos.remoto.api.ProveedorUrlBase
import com.synagon.app.dominio.casosuso.CrearLigaAgoraCasoUso
import com.synagon.app.dominio.casosuso.GestionarPasswordPrimerAccesoCasoUso
import com.synagon.app.dominio.casosuso.InscribirEquipoEnLigaCasoUso
import com.synagon.app.dominio.casosuso.ObtenerDatosAgoraCasoUso
import com.synagon.app.dominio.casosuso.ValidarCodigoInvitacionCasoUso
import com.synagon.app.dominio.modelos.ContextoSesionAgora
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.modelos.RolMembresia
import com.synagon.app.dominio.repositorios.RepositorioAgora
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel multiplataforma bajo arquitectura MVI estricta y flujo unidireccional (UDF)
 * para la Pantalla Principal (Ágora de Synagon).
 */
class AgoraViewModel(
    private val obtenerDatosAgoraCasoUso: ObtenerDatosAgoraCasoUso,
    private val validarCodigoInvitacionCasoUso: ValidarCodigoInvitacionCasoUso,
    private val inscribirEquipoEnLigaCasoUso: InscribirEquipoEnLigaCasoUso,
    private val gestionarPasswordPrimerAccesoCasoUso: GestionarPasswordPrimerAccesoCasoUso,
    private val crearLigaAgoraCasoUso: CrearLigaAgoraCasoUso,
    private val repositorioAgora: RepositorioAgora,
    private val contextoSesionAgora: ContextoSesionAgora,
    private val proveedorUrlBase: ProveedorUrlBase,
    alcanceCorrutinas: CoroutineScope? = null,
) : ViewModel() {

    private val alcance: CoroutineScope = alcanceCorrutinas ?: viewModelScope

    private val membresiasCreadasEnSesion = mutableListOf<ResumenMembresiaUsuario>()

    private val _estadoUi = MutableStateFlow(
        EstadoAgoraUi(
            nombreUsuario = contextoSesionAgora.nombreUsuario,
            emailUsuario = contextoSesionAgora.emailUsuario,
            telefonoUsuario = contextoSesionAgora.telefonoUsuario,
            mostrarModalPrimerAccesoPassword = contextoSesionAgora.requiereCambioPassword,
        )
    )
    val estadoUi: StateFlow<EstadoAgoraUi> = _estadoUi.asStateFlow()

    private val _efectosUi = Channel<EfectoAgoraUi>(Channel.BUFFERED)
    val efectosUi: Flow<EfectoAgoraUi> = _efectosUi.receiveAsFlow()

    init {
        cargarDatosAgora()
    }

    /**
     * Punto de entrada único para procesar las intenciones del usuario desde `PantallaAgoraPrincipal`.
     */
    fun procesarEvento(evento: EventoAgoraUi) {
        when (evento) {
            EventoAgoraUi.RecargarAgora -> cargarDatosAgora()

            // Crear Liga (Rol Presidente)
            EventoAgoraUi.PulsarCrearLiga -> _estadoUi.update {
                it.copy(
                    mostrarModalCrearLiga = true,
                    nombreNuevaLigaInput = "",
                    requiereSedeNuevaLiga = true,
                    sedeUnicaNuevaLigaInput = "",
                    partidosPorJornadaNuevaLiga = 3,
                    errorModalCrearLiga = null,
                )
            }

            EventoAgoraUi.CerrarModalCrearLiga -> _estadoUi.update {
                it.copy(
                    mostrarModalCrearLiga = false,
                    creandoLiga = false,
                    errorModalCrearLiga = null,
                )
            }

            is EventoAgoraUi.CambiarNombreNuevaLiga -> _estadoUi.update {
                it.copy(nombreNuevaLigaInput = evento.valor, errorModalCrearLiga = null)
            }

            is EventoAgoraUi.CambiarRequiereSedeNuevaLiga -> _estadoUi.update {
                it.copy(requiereSedeNuevaLiga = evento.requiereSede, errorModalCrearLiga = null)
            }

            is EventoAgoraUi.CambiarSedeUnicaNuevaLiga -> _estadoUi.update {
                it.copy(sedeUnicaNuevaLigaInput = evento.valor, errorModalCrearLiga = null)
            }

            is EventoAgoraUi.CambiarPartidosPorJornadaNuevaLiga -> _estadoUi.update {
                it.copy(
                    partidosPorJornadaNuevaLiga = evento.cantidad.coerceIn(1, 5),
                    errorModalCrearLiga = null,
                )
            }

            EventoAgoraUi.ConfirmarCrearLiga -> confirmarCreacionLiga()

            // Unir Equipo a Liga (Código de Invitación)
            EventoAgoraUi.PulsarUnirEquipo -> _estadoUi.update {
                it.copy(
                    mostrarModalUnirEquipo = true,
                    codigoInvitacionInput = "",
                    configuracionLigaValidada = null,
                    nombreNuevoEquipoInput = "",
                    sedeNuevoEquipoInput = "",
                    validandoCodigoInvitacion = false,
                    inscribiendoEquipo = false,
                    errorModalUnirEquipo = null,
                )
            }

            EventoAgoraUi.CerrarModalUnirEquipo -> _estadoUi.update {
                it.copy(
                    mostrarModalUnirEquipo = false,
                    validandoCodigoInvitacion = false,
                    inscribiendoEquipo = false,
                    errorModalUnirEquipo = null,
                )
            }

            is EventoAgoraUi.CambiarCodigoInvitacion -> _estadoUi.update {
                it.copy(
                    codigoInvitacionInput = evento.valor.uppercase().take(24),
                    errorModalUnirEquipo = null,
                )
            }

            EventoAgoraUi.ValidarCodigoInvitacion -> validarCodigoInvitacion()

            EventoAgoraUi.VolverAPasoCodigoInvitacion -> _estadoUi.update {
                it.copy(
                    configuracionLigaValidada = null,
                    errorModalUnirEquipo = null,
                )
            }

            is EventoAgoraUi.CambiarNombreNuevoEquipo -> _estadoUi.update {
                it.copy(nombreNuevoEquipoInput = evento.valor, errorModalUnirEquipo = null)
            }

            is EventoAgoraUi.CambiarSedeNuevoEquipo -> _estadoUi.update {
                it.copy(sedeNuevoEquipoInput = evento.valor, errorModalUnirEquipo = null)
            }

            EventoAgoraUi.ConfirmarInscripcionEquipo -> confirmarInscripcionEquipo()

            // Selección de tarjeta de membresía activa
            is EventoAgoraUi.SeleccionarTarjetaMembresia -> {
                val m = evento.membresia
                emitirEfecto(
                    EfectoAgoraUi.NavegarAGestionEquipoOLiga(
                        ligaId = m.ligaId,
                        equipoId = m.equipoId,
                        rol = m.rol,
                    )
                )
            }

            // Modal de Perfil Personal
            EventoAgoraUi.AbrirPerfil -> _estadoUi.update {
                it.copy(
                    mostrarModalPerfil = true,
                    nombrePerfilInput = it.nombreUsuario,
                    telefonoPerfilInput = it.telefonoUsuario,
                    errorModalPerfil = null,
                )
            }

            EventoAgoraUi.CerrarModalPerfil -> _estadoUi.update {
                it.copy(
                    mostrarModalPerfil = false,
                    guardandoPerfil = false,
                    errorModalPerfil = null,
                )
            }

            is EventoAgoraUi.CambiarNombrePerfil -> _estadoUi.update {
                it.copy(nombrePerfilInput = evento.valor, errorModalPerfil = null)
            }

            is EventoAgoraUi.CambiarTelefonoPerfil -> {
                val telefonoLimpio = evento.valor.filter { c ->
                    c.isDigit() || c == '+' || c == ' '
                }.take(16)
                _estadoUi.update {
                    it.copy(telefonoPerfilInput = telefonoLimpio, errorModalPerfil = null)
                }
            }

            EventoAgoraUi.GuardarDatosPerfil -> guardarPerfilPersonal()

            // Modal de Primer Acceso (Cambio opcional de contraseña inicial DNI)
            EventoAgoraUi.SolicitarFormularioCambioPasswordInicial -> _estadoUi.update {
                it.copy(
                    desplegarInputNuevaPassword = true,
                    errorPasswordInicial = null,
                )
            }

            is EventoAgoraUi.CambiarNuevaPasswordInicial -> _estadoUi.update {
                it.copy(
                    nuevaPasswordInicialInput = evento.valor,
                    errorPasswordInicial = null,
                )
            }

            EventoAgoraUi.AlternarVisibilidadPasswordInicial -> _estadoUi.update {
                it.copy(passwordInicialVisible = !it.passwordInicialVisible)
            }

            EventoAgoraUi.ConfirmarCambioPasswordInicial -> confirmarCambioPasswordInicial()

            EventoAgoraUi.RechazarCambioPasswordInicial -> rechazarCambioPasswordInicial()

            // Diálogo de Cerrar Sesión
            EventoAgoraUi.SolicitarCerrarSesion -> _estadoUi.update {
                it.copy(mostrarDialogoCerrarSesion = true)
            }

            EventoAgoraUi.CancelarCerrarSesion -> _estadoUi.update {
                it.copy(mostrarDialogoCerrarSesion = false)
            }

            EventoAgoraUi.ConfirmarCerrarSesion -> {
                contextoSesionAgora.limpiarSesion()
                _estadoUi.update { it.copy(mostrarDialogoCerrarSesion = false) }
                emitirEfecto(EfectoAgoraUi.NavegarALogin)
            }
        }
    }

    private fun cargarDatosAgora() {
        alcance.launch {
            _estadoUi.update { it.copy(cargando = true, mensajeEstadoConexion = null) }

            val datos = obtenerDatosAgoraCasoUso()
            val perfil = datos.perfil

            if (datos.errorRed == null) {
                val listaFinal = combinarConMembresiasEnSesion(datos.membresias)
                val mostrarPrimerAcceso = (perfil?.requiereCambioPassword == true) ||
                    contextoSesionAgora.requiereCambioPassword

                _estadoUi.update { actual ->
                    actual.copy(
                        cargando = false,
                        nombreUsuario = perfil?.nombreUsuario?.takeIf { it.isNotBlank() } ?: actual.nombreUsuario,
                        emailUsuario = perfil?.emailUsuario?.takeIf { it.isNotBlank() } ?: actual.emailUsuario,
                        telefonoUsuario = perfil?.telefonoUsuario?.takeIf { it.isNotBlank() } ?: actual.telefonoUsuario,
                        listaMembresias = listaFinal,
                        mostrarModalPrimerAccesoPassword = mostrarPrimerAcceso,
                        usandoRespaldoLocal = false,
                        mensajeEstadoConexion = null,
                    )
                }
            } else {
                val url = proveedorUrlBase.obtenerUrlBase()
                val listaRespaldo = combinarConMembresiasEnSesion(
                    DatosSemillaAgora.crearMembresiasIniciales()
                )
                _estadoUi.update { actual ->
                    actual.copy(
                        cargando = false,
                        listaMembresias = listaRespaldo,
                        mostrarModalPrimerAccesoPassword = actual.mostrarModalPrimerAccesoPassword ||
                            contextoSesionAgora.requiereCambioPassword,
                        usandoRespaldoLocal = true,
                        mensajeEstadoConexion = describirAvisoConexion(datos.errorRed, url),
                    )
                }
            }
        }
    }

    private fun validarCodigoInvitacion() {
        val codigo = _estadoUi.value.codigoInvitacionInput
        alcance.launch {
            _estadoUi.update {
                it.copy(validandoCodigoInvitacion = true, errorModalUnirEquipo = null)
            }

            when (val resultado = validarCodigoInvitacionCasoUso(codigo)) {
                is ValidarCodigoInvitacionCasoUso.ResultadoValidacionCodigo.CodigoValido -> {
                    _estadoUi.update {
                        it.copy(
                            validandoCodigoInvitacion = false,
                            configuracionLigaValidada = resultado.configuracion,
                            errorModalUnirEquipo = null,
                        )
                    }
                }

                is ValidarCodigoInvitacionCasoUso.ResultadoValidacionCodigo.ErrorFormato -> {
                    _estadoUi.update {
                        it.copy(
                            validandoCodigoInvitacion = false,
                            errorModalUnirEquipo = resultado.mensaje,
                        )
                    }
                }

                is ValidarCodigoInvitacionCasoUso.ResultadoValidacionCodigo.ErrorServidor -> {
                    when (resultado.error) {
                        ErrorRed.SinConexion -> {
                            val local = DatosSemillaAgora.resolverCodigoInvitacionLocal(codigo.trim().uppercase())
                            if (local != null) {
                                _estadoUi.update {
                                    it.copy(
                                        validandoCodigoInvitacion = false,
                                        configuracionLigaValidada = local,
                                        errorModalUnirEquipo = null,
                                    )
                                }
                            } else {
                                _estadoUi.update {
                                    it.copy(
                                        validandoCodigoInvitacion = false,
                                        errorModalUnirEquipo = "El código de invitación introducido no es válido.",
                                    )
                                }
                            }
                        }
                        ErrorRed.NoEncontrado, ErrorRed.PeticionInvalida -> {
                            _estadoUi.update {
                                it.copy(
                                    validandoCodigoInvitacion = false,
                                    errorModalUnirEquipo = "No se encontró ninguna liga activa con ese código de invitación.",
                                )
                            }
                        }
                        else -> {
                            _estadoUi.update {
                                it.copy(
                                    validandoCodigoInvitacion = false,
                                    errorModalUnirEquipo = "No se pudo verificar el código en este momento. Inténtalo de nuevo.",
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun confirmarInscripcionEquipo() {
        val actual = _estadoUi.value
        val configuracion = actual.configuracionLigaValidada ?: return

        alcance.launch {
            _estadoUi.update {
                it.copy(inscribiendoEquipo = true, errorModalUnirEquipo = null)
            }

            val resultado = inscribirEquipoEnLigaCasoUso(
                configuracionLiga = configuracion,
                codigoInvitacion = actual.codigoInvitacionInput,
                nombreEquipo = actual.nombreNuevoEquipoInput,
                sedeEquipo = actual.sedeNuevoEquipoInput,
            )

            when (resultado) {
                is InscribirEquipoEnLigaCasoUso.ResultadoInscripcionEquipo.Inscrito -> {
                    registrarMembresiaYRefrescar(
                        nuevaMembresia = resultado.nuevaMembresia,
                        recargarRemoto = true,
                    )
                    emitirEfecto(
                        EfectoAgoraUi.MostrarMensaje(
                            "Equipo '${resultado.nuevaMembresia.nombreEquipoOSubtitulo}' inscrito como CAPITÁN."
                        )
                    )
                }

                is InscribirEquipoEnLigaCasoUso.ResultadoInscripcionEquipo.ErrorValidacion -> {
                    _estadoUi.update {
                        it.copy(
                            inscribiendoEquipo = false,
                            errorModalUnirEquipo = resultado.mensaje,
                        )
                    }
                }

                is InscribirEquipoEnLigaCasoUso.ResultadoInscripcionEquipo.ErrorServidor -> {
                    if (resultado.error == ErrorRed.SinConexion) {
                        val sedeDetalle = if (configuracion.requiereSedeEquipo) {
                            "${actual.sedeNuevoEquipoInput.trim()} · ${configuracion.partidosPorJornada} parejas por jornada"
                        } else {
                            "Sede oficial de la liga · ${configuracion.partidosPorJornada} parejas por jornada"
                        }
                        val nuevaMembresiaLocal = ResumenMembresiaUsuario(
                            ligaId = configuracion.ligaId,
                            nombreLiga = configuracion.nombreLiga.uppercase(),
                            equipoId = "eq-${actual.nombreNuevoEquipoInput.trim().lowercase().hashCode().toUInt()}",
                            nombreEquipoOSubtitulo = actual.nombreNuevoEquipoInput.trim(),
                            textoSedeDetalle = sedeDetalle,
                            partidosPorJornada = configuracion.partidosPorJornada,
                            rol = RolMembresia.CAPITAN,
                        )
                        registrarMembresiaYRefrescar(
                            nuevaMembresia = nuevaMembresiaLocal,
                            recargarRemoto = false,
                        )
                        emitirEfecto(
                            EfectoAgoraUi.MostrarMensaje(
                                "Equipo '${nuevaMembresiaLocal.nombreEquipoOSubtitulo}' inscrito como CAPITÁN."
                            )
                        )
                    } else {
                        _estadoUi.update {
                            it.copy(
                                inscribiendoEquipo = false,
                                errorModalUnirEquipo = "No se pudo completar la inscripción del equipo en el servidor.",
                            )
                        }
                    }
                }
            }
        }
    }

    private fun confirmarCreacionLiga() {
        val actual = _estadoUi.value
        alcance.launch {
            _estadoUi.update {
                it.copy(creandoLiga = true, errorModalCrearLiga = null)
            }

            val resultado = crearLigaAgoraCasoUso(
                nombreLiga = actual.nombreNuevaLigaInput,
                requiereSedeEquipo = actual.requiereSedeNuevaLiga,
                sedeUnicaLiga = actual.sedeUnicaNuevaLigaInput,
                partidosPorJornada = actual.partidosPorJornadaNuevaLiga,
            )

            when (resultado) {
                is CrearLigaAgoraCasoUso.ResultadoCreacionLiga.Creada -> {
                    membresiasCreadasEnSesion.add(0, resultado.membresiaPresidente)
                    _estadoUi.update {
                        it.copy(
                            mostrarModalCrearLiga = false,
                            creandoLiga = false,
                            listaMembresias = combinarConMembresiasEnSesion(it.listaMembresias),
                        )
                    }
                    emitirEfecto(
                        EfectoAgoraUi.MostrarMensaje(
                            "Liga '${resultado.membresiaPresidente.nombreLiga}' creada con rol PRESIDENTE."
                        )
                    )
                    cargarDatosAgora()
                }

                is CrearLigaAgoraCasoUso.ResultadoCreacionLiga.ErrorValidacion -> {
                    _estadoUi.update {
                        it.copy(creandoLiga = false, errorModalCrearLiga = resultado.mensaje)
                    }
                }

                is CrearLigaAgoraCasoUso.ResultadoCreacionLiga.ErrorServidor -> {
                    if (resultado.error == ErrorRed.SinConexion) {
                        val codigoGenerado = actual.nombreNuevaLigaInput.trim()
                            .uppercase()
                            .filter { it.isLetterOrDigit() }
                            .take(6)
                            .ifEmpty { "SYN" } + "-2026"
                        val detalleSede = if (actual.requiereSedeNuevaLiga) {
                            "Sedes por club local · Código activo: $codigoGenerado"
                        } else {
                            "Sede Única: ${actual.sedeUnicaNuevaLigaInput.trim()} · Código: $codigoGenerado"
                        }
                        val nuevaLigaLocal = ResumenMembresiaUsuario(
                            ligaId = "liga-${codigoGenerado.lowercase()}",
                            nombreLiga = actual.nombreNuevaLigaInput.trim().uppercase(),
                            equipoId = null,
                            nombreEquipoOSubtitulo = "Dirección General de Competición (Inscripción abierta)",
                            textoSedeDetalle = detalleSede,
                            partidosPorJornada = actual.partidosPorJornadaNuevaLiga,
                            rol = RolMembresia.PRESIDENTE,
                        )
                        membresiasCreadasEnSesion.add(0, nuevaLigaLocal)
                        _estadoUi.update {
                            it.copy(
                                mostrarModalCrearLiga = false,
                                creandoLiga = false,
                                listaMembresias = combinarConMembresiasEnSesion(it.listaMembresias),
                            )
                        }
                        emitirEfecto(
                            EfectoAgoraUi.MostrarMensaje(
                                "Liga creada como PRESIDENTE · Código de invitación: $codigoGenerado"
                            )
                        )
                    } else {
                        _estadoUi.update {
                            it.copy(
                                creandoLiga = false,
                                errorModalCrearLiga = "Error al registrar la liga en el servidor.",
                            )
                        }
                    }
                }
            }
        }
    }

    private fun guardarPerfilPersonal() {
        val actual = _estadoUi.value
        val nombreLimpio = actual.nombrePerfilInput.trim()
        val telefonoLimpio = actual.telefonoPerfilInput.trim()
        val digitos = telefonoLimpio.count { it.isDigit() }

        if (nombreLimpio.length < 3) {
            _estadoUi.update {
                it.copy(errorModalPerfil = "El nombre y apellidos deben tener al menos 3 caracteres.")
            }
            return
        }
        if (digitos < 9) {
            _estadoUi.update {
                it.copy(errorModalPerfil = "Introduce un teléfono válido con al menos 9 dígitos.")
            }
            return
        }

        alcance.launch {
            _estadoUi.update { it.copy(guardandoPerfil = true, errorModalPerfil = null) }

            val resultado = repositorioAgora.actualizarPerfilUsuario(
                nombreUsuario = nombreLimpio,
                telefonoUsuario = telefonoLimpio,
            )

            when (resultado) {
                is Resultado.Exito -> {
                    contextoSesionAgora.actualizarDatosPerfil(
                        nombre = resultado.datos.nombreUsuario,
                        telefono = resultado.datos.telefonoUsuario,
                    )
                    _estadoUi.update {
                        it.copy(
                            nombreUsuario = resultado.datos.nombreUsuario,
                            telefonoUsuario = resultado.datos.telefonoUsuario,
                            mostrarModalPerfil = false,
                            guardandoPerfil = false,
                        )
                    }
                    emitirEfecto(EfectoAgoraUi.MostrarMensaje("Datos de perfil actualizados correctamente."))
                }

                is Resultado.Error -> {
                    if (resultado.error == ErrorRed.SinConexion) {
                        contextoSesionAgora.actualizarDatosPerfil(nombreLimpio, telefonoLimpio)
                        _estadoUi.update {
                            it.copy(
                                nombreUsuario = nombreLimpio,
                                telefonoUsuario = telefonoLimpio,
                                mostrarModalPerfil = false,
                                guardandoPerfil = false,
                            )
                        }
                        emitirEfecto(EfectoAgoraUi.MostrarMensaje("Perfil actualizado en tu sesión activa."))
                    } else {
                        _estadoUi.update {
                            it.copy(
                                guardandoPerfil = false,
                                errorModalPerfil = "No se pudieron guardar los cambios en el servidor.",
                            )
                        }
                    }
                }
            }
        }
    }

    private fun confirmarCambioPasswordInicial() {
        val nuevaPassword = _estadoUi.value.nuevaPasswordInicialInput
        alcance.launch {
            _estadoUi.update {
                it.copy(guardandoPasswordInicial = true, errorPasswordInicial = null)
            }

            when (val resultado = gestionarPasswordPrimerAccesoCasoUso.cambiarPassword(nuevaPassword)) {
                GestionarPasswordPrimerAccesoCasoUso.ResultadoCambioPassword.Completado -> {
                    contextoSesionAgora.marcarCambioPasswordGestionado()
                    _estadoUi.update {
                        it.copy(
                            mostrarModalPrimerAccesoPassword = false,
                            desplegarInputNuevaPassword = false,
                            nuevaPasswordInicialInput = "",
                            guardandoPasswordInicial = false,
                            errorPasswordInicial = null,
                        )
                    }
                    emitirEfecto(
                        EfectoAgoraUi.MostrarMensaje("Contraseña personal establecida con éxito.")
                    )
                }

                is GestionarPasswordPrimerAccesoCasoUso.ResultadoCambioPassword.ErrorValidacion -> {
                    _estadoUi.update {
                        it.copy(
                            guardandoPasswordInicial = false,
                            errorPasswordInicial = resultado.mensaje,
                        )
                    }
                }

                is GestionarPasswordPrimerAccesoCasoUso.ResultadoCambioPassword.ErrorServidor -> {
                    if (resultado.error == ErrorRed.SinConexion) {
                        contextoSesionAgora.marcarCambioPasswordGestionado()
                        _estadoUi.update {
                            it.copy(
                                mostrarModalPrimerAccesoPassword = false,
                                desplegarInputNuevaPassword = false,
                                nuevaPasswordInicialInput = "",
                                guardandoPasswordInicial = false,
                            )
                        }
                        emitirEfecto(
                            EfectoAgoraUi.MostrarMensaje("Contraseña personal actualizada para tu cuenta.")
                        )
                    } else {
                        _estadoUi.update {
                            it.copy(
                                guardandoPasswordInicial = false,
                                errorPasswordInicial = "No se pudo actualizar la contraseña en el servidor.",
                            )
                        }
                    }
                }
            }
        }
    }

    private fun rechazarCambioPasswordInicial() {
        viewModelScope.launch {
            gestionarPasswordPrimerAccesoCasoUso.conservarPasswordInicial()
            contextoSesionAgora.marcarCambioPasswordGestionado()
            _estadoUi.update {
                it.copy(
                    mostrarModalPrimerAccesoPassword = false,
                    desplegarInputNuevaPassword = false,
                    nuevaPasswordInicialInput = "",
                    errorPasswordInicial = null,
                )
            }
            emitirEfecto(
                EfectoAgoraUi.MostrarMensaje("Bienvenido al Ágora. Puedes cambiar tu contraseña desde Perfil cuando quieras.")
            )
        }
    }

    private fun registrarMembresiaYRefrescar(
        nuevaMembresia: ResumenMembresiaUsuario,
        recargarRemoto: Boolean,
    ) {
        membresiasCreadasEnSesion.removeAll {
            it.ligaId == nuevaMembresia.ligaId && it.equipoId == nuevaMembresia.equipoId
        }
        membresiasCreadasEnSesion.add(0, nuevaMembresia)
        _estadoUi.update {
            it.copy(
                mostrarModalUnirEquipo = false,
                codigoInvitacionInput = "",
                configuracionLigaValidada = null,
                nombreNuevoEquipoInput = "",
                sedeNuevoEquipoInput = "",
                inscribiendoEquipo = false,
                errorModalUnirEquipo = null,
                listaMembresias = combinarConMembresiasEnSesion(it.listaMembresias),
            )
        }
        if (recargarRemoto) {
            cargarDatosAgora()
        }
    }

    private fun combinarConMembresiasEnSesion(
        base: List<ResumenMembresiaUsuario>,
    ): List<ResumenMembresiaUsuario> {
        if (membresiasCreadasEnSesion.isEmpty()) return base
        val clavesEnSesion = membresiasCreadasEnSesion.map { "${it.ligaId}|${it.equipoId}" }.toSet()
        return membresiasCreadasEnSesion + base.filterNot { "${it.ligaId}|${it.equipoId}" in clavesEnSesion }
    }

    private fun describirAvisoConexion(error: ErrorRed, urlBase: String): String {
        return when (error) {
            ErrorRed.SinConexion -> "Modo local activo ($urlBase) · Mostrando membresías de demostración"
            ErrorRed.NoEncontrado -> "Endpoints de Ágora pendientes en $urlBase · Datos semilla activos"
            else -> "Sin sincronización con $urlBase · Datos semilla activos"
        }
    }

    private fun emitirEfecto(efecto: EfectoAgoraUi) {
        if (_efectosUi.trySend(efecto).isFailure) {
            viewModelScope.launch {
                _efectosUi.send(efecto)
            }
        }
    }
}
