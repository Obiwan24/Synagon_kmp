package com.synagon.app.presentacion.pantallas.acceso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synagon.app.dominio.modelos.ContextoSesionAgora
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
 * encargado de gestionar las transiciones de Login, Registro y navegación informativa.
 */
class AccesoViewModel(
    private val contextoSesionAgora: ContextoSesionAgora? = null,
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoAccesoUi())
    val estadoUi: StateFlow<EstadoAccesoUi> = _estadoUi.asStateFlow()

    private val _efectosUi = Channel<EfectoAccesoUi>(Channel.BUFFERED)
    val efectosUi: Flow<EfectoAccesoUi> = _efectosUi.receiveAsFlow()

    /**
     * Punto de entrada único para procesar los eventos de usuario desde [PantallaAcceso].
     */
    fun procesarEvento(evento: EventoAccesoUi) {
        when (evento) {
            is EventoAccesoUi.CambiarUsuario -> _estadoUi.update {
                it.copy(
                    usuario = evento.valor,
                    errorUsuario = null,
                    mensajeErrorGeneral = null,
                )
            }

            is EventoAccesoUi.CambiarContrasena -> _estadoUi.update {
                it.copy(
                    contrasena = evento.valor,
                    errorContrasena = null,
                    mensajeErrorGeneral = null,
                )
            }

            is EventoAccesoUi.CambiarNombre -> _estadoUi.update {
                it.copy(
                    nombre = evento.valor,
                    errorNombre = null,
                    mensajeErrorGeneral = null,
                )
            }

            is EventoAccesoUi.CambiarTelefono -> {
                val telefonoLimpio = evento.valor.filter { caracter ->
                    caracter.isDigit() || caracter == '+' || caracter == ' '
                }.take(16)
                _estadoUi.update {
                    it.copy(
                        telefono = telefonoLimpio,
                        errorTelefono = null,
                        mensajeErrorGeneral = null,
                    )
                }
            }

            is EventoAccesoUi.CambiarAceptarPrivacidad -> _estadoUi.update {
                it.copy(
                    aceptarPrivacidad = evento.aceptado,
                    errorPrivacidad = null,
                    mensajeErrorGeneral = null,
                )
            }

            EventoAccesoUi.AlternarVisibilidadContrasena -> _estadoUi.update {
                it.copy(contrasenaVisible = !it.contrasenaVisible)
            }

            is EventoAccesoUi.CambiarModo -> _estadoUi.update {
                it.copy(
                    modo = evento.nuevoModo,
                    errorUsuario = null,
                    errorContrasena = null,
                    errorNombre = null,
                    errorTelefono = null,
                    errorPrivacidad = null,
                    mensajeErrorGeneral = null,
                )
            }

            EventoAccesoUi.AlternarMenuDesplegable -> _estadoUi.update {
                it.copy(menuDesplegado = !it.menuDesplegado)
            }

            EventoAccesoUi.CerrarMenuDesplegable -> _estadoUi.update {
                it.copy(menuDesplegado = false)
            }

            EventoAccesoUi.IniciarSesion -> procesarInicioSesion()

            EventoAccesoUi.Registrarse -> procesarRegistroJugador()

            EventoAccesoUi.SolicitarRecuperacionContrasena -> procesarRecuperacionContrasena()

            EventoAccesoUi.IrASobreSynagon -> {
                _estadoUi.update { it.copy(menuDesplegado = false) }
                emitirEfecto(EfectoAccesoUi.NavegarASobreSynagon)
            }

            EventoAccesoUi.IrAPrivacidad -> {
                _estadoUi.update { it.copy(menuDesplegado = false) }
                emitirEfecto(EfectoAccesoUi.NavegarAPrivacidad)
            }
        }
    }

    private fun procesarInicioSesion() {
        val actual = _estadoUi.value
        val usuarioLimpio = actual.usuario.trim()

        val errorUsuario = when {
            usuarioLimpio.isEmpty() -> "Introduce tu correo electrónico o nombre de usuario."
            usuarioLimpio.length < 3 -> "El identificador debe tener al menos 3 caracteres."
            else -> null
        }

        val errorContrasena = when {
            actual.contrasena.isBlank() -> "Introduce tu contraseña de acceso."
            actual.contrasena.length < 4 -> "La contraseña debe tener al menos 4 caracteres."
            else -> null
        }

        if (errorUsuario != null || errorContrasena != null) {
            _estadoUi.update {
                it.copy(
                    errorUsuario = errorUsuario,
                    errorContrasena = errorContrasena,
                )
            }
            emitirEfecto(EfectoAccesoUi.MostrarMensaje("Revisa tus credenciales para acceder a Synagon."))
            return
        }

        contextoSesionAgora?.registrarInicioSesion(
            identificador = usuarioLimpio,
            contrasenaIntroducida = actual.contrasena,
        )

        _estadoUi.update {
            it.copy(
                cargando = false,
                errorUsuario = null,
                errorContrasena = null,
                mensajeErrorGeneral = null,
            )
        }
        emitirEfecto(EfectoAccesoUi.NavegarAlPanelPrincipal)
    }

    private fun procesarRegistroJugador() {
        val actual = _estadoUi.value
        val nombreLimpio = actual.nombre.trim()
        val usuarioLimpio = actual.usuario.trim()
        val digitosTelefono = actual.telefono.count { it.isDigit() }

        val errorNombre = if (nombreLimpio.length < 3) {
            "Introduce tu nombre y apellidos (mín. 3 caracteres)."
        } else {
            null
        }

        val errorTelefono = if (digitosTelefono < 9) {
            "Introduce un teléfono de contacto válido (mín. 9 dígitos)."
        } else {
            null
        }

        val errorUsuario = when {
            usuarioLimpio.isEmpty() -> "Introduce tu correo electrónico oficial."
            !usuarioLimpio.contains("@") || !usuarioLimpio.contains(".") ->
                "Introduce un correo electrónico con formato válido."
            else -> null
        }

        val errorContrasena = if (actual.contrasena.length < 6) {
            "La contraseña debe tener al menos 6 caracteres."
        } else {
            null
        }

        val errorPrivacidad = if (!actual.aceptarPrivacidad) {
            "Debes leer y aceptar la Política de Privacidad para completar el registro."
        } else {
            null
        }

        val hayErrores = listOf(
            errorNombre,
            errorTelefono,
            errorUsuario,
            errorContrasena,
            errorPrivacidad,
        ).any { it != null }

        if (hayErrores) {
            _estadoUi.update {
                it.copy(
                    errorNombre = errorNombre,
                    errorTelefono = errorTelefono,
                    errorUsuario = errorUsuario,
                    errorContrasena = errorContrasena,
                    errorPrivacidad = errorPrivacidad,
                )
            }
            emitirEfecto(
                EfectoAccesoUi.MostrarMensaje(
                    errorPrivacidad ?: "Completa todos los campos obligatorios del registro."
                )
            )
            return
        }

        contextoSesionAgora?.registrarInicioSesion(
            identificador = usuarioLimpio,
            contrasenaIntroducida = actual.contrasena,
            nombreRegistro = nombreLimpio,
            telefonoRegistro = actual.telefono,
        )

        _estadoUi.update {
            it.copy(
                cargando = false,
                errorNombre = null,
                errorTelefono = null,
                errorUsuario = null,
                errorContrasena = null,
                errorPrivacidad = null,
                mensajeErrorGeneral = null,
            )
        }
        emitirEfecto(EfectoAccesoUi.NavegarAlPanelPrincipal)
    }

    private fun procesarRecuperacionContrasena() {
        val usuarioLimpio = _estadoUi.value.usuario.trim()
        if (usuarioLimpio.isEmpty()) {
            _estadoUi.update {
                it.copy(errorUsuario = "Indica tu correo o usuario para enviarte el enlace de recuperación.")
            }
            emitirEfecto(
                EfectoAccesoUi.MostrarMensaje("Introduce primero tu correo electrónico o usuario.")
            )
        } else {
            emitirEfecto(
                EfectoAccesoUi.MostrarMensaje(
                    "Instrucciones de restablecimiento enviadas a $usuarioLimpio."
                )
            )
        }
    }

    private fun emitirEfecto(efecto: EfectoAccesoUi) {
        if (_efectosUi.trySend(efecto).isFailure) {
            viewModelScope.launch {
                _efectosUi.send(efecto)
            }
        }
    }
}
