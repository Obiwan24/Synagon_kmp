package com.synagon.app.dominio.casosuso

import com.synagon.app.dominio.modelos.ConfiguracionPreviaLiga
import com.synagon.app.dominio.modelos.PerfilUsuarioAgora
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.repositorios.RepositorioAgora
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado

/**
 * Resultado consolidado de la carga inicial de datos del usuario en el Ágora.
 */
data class DatosInicialesAgora(
    val perfil: PerfilUsuarioAgora?,
    val membresias: List<ResumenMembresiaUsuario>,
    val errorRed: ErrorRed? = null,
)

/**
 * Obtiene el perfil del usuario autenticado y la lista de membresías activas en ligas y equipos.
 */
class ObtenerDatosAgoraCasoUso(
    private val repositorioAgora: RepositorioAgora,
) {
    suspend operator fun invoke(): DatosInicialesAgora {
        val resultadoPerfil = repositorioAgora.obtenerPerfilUsuario()
        val resultadoMembresias = repositorioAgora.obtenerMembresiasUsuario()

        val perfil = (resultadoPerfil as? Resultado.Exito)?.datos
        val membresias = (resultadoMembresias as? Resultado.Exito)?.datos.orEmpty()
        val error = (resultadoMembresias as? Resultado.Error)?.error
            ?: (resultadoPerfil as? Resultado.Error)?.error

        return DatosInicialesAgora(
            perfil = perfil,
            membresias = membresias,
            errorRed = error,
        )
    }
}

/**
 * Valida en cliente el formato del código de invitación del presidente y consulta la liga asociada.
 */
class ValidarCodigoInvitacionCasoUso(
    private val repositorioAgora: RepositorioAgora,
) {
    sealed interface ResultadoValidacionCodigo {
        data class CodigoValido(val configuracion: ConfiguracionPreviaLiga) : ResultadoValidacionCodigo
        data class ErrorFormato(val mensaje: String) : ResultadoValidacionCodigo
        data class ErrorServidor(val error: ErrorRed) : ResultadoValidacionCodigo
    }

    suspend operator fun invoke(codigoInvitacion: String): ResultadoValidacionCodigo {
        val codigoNormalizado = codigoInvitacion.trim().uppercase()
        if (codigoNormalizado.isEmpty()) {
            return ResultadoValidacionCodigo.ErrorFormato(
                "Introduce el código de invitación facilitado por el presidente de la liga."
            )
        }
        if (codigoNormalizado.length < 4) {
            return ResultadoValidacionCodigo.ErrorFormato(
                "El código de invitación debe tener al menos 4 caracteres."
            )
        }

        return when (val respuesta = repositorioAgora.validarCodigoInvitacion(codigoNormalizado)) {
            is Resultado.Exito -> ResultadoValidacionCodigo.CodigoValido(respuesta.datos)
            is Resultado.Error -> ResultadoValidacionCodigo.ErrorServidor(respuesta.error)
        }
    }
}

/**
 * Valida los campos obligatorios e inscribe un nuevo equipo en la liga otorgando el rol `CAPITAN`.
 */
class InscribirEquipoEnLigaCasoUso(
    private val repositorioAgora: RepositorioAgora,
) {
    sealed interface ResultadoInscripcionEquipo {
        data class Inscrito(val nuevaMembresia: ResumenMembresiaUsuario) : ResultadoInscripcionEquipo
        data class ErrorValidacion(val mensaje: String) : ResultadoInscripcionEquipo
        data class ErrorServidor(val error: ErrorRed) : ResultadoInscripcionEquipo
    }

    suspend operator fun invoke(
        configuracionLiga: ConfiguracionPreviaLiga,
        codigoInvitacion: String,
        nombreEquipo: String,
        sedeEquipo: String,
    ): ResultadoInscripcionEquipo {
        val nombreLimpio = nombreEquipo.trim()
        val sedeLimpia = sedeEquipo.trim()

        if (nombreLimpio.length < 3) {
            return ResultadoInscripcionEquipo.ErrorValidacion(
                "El nombre de tu equipo debe tener al menos 3 caracteres."
            )
        }

        if (configuracionLiga.requiereSedeEquipo && sedeLimpia.length < 3) {
            return ResultadoInscripcionEquipo.ErrorValidacion(
                "Esta liga requiere indicar el Club o Sede Oficial Local de tu equipo."
            )
        }

        val resultado = repositorioAgora.inscribirEquipoEnLiga(
            ligaId = configuracionLiga.ligaId,
            codigoInvitacion = codigoInvitacion.trim().uppercase(),
            nombreEquipo = nombreLimpio,
            sedeEquipo = sedeLimpia.takeIf { configuracionLiga.requiereSedeEquipo && it.isNotBlank() },
        )

        return when (resultado) {
            is Resultado.Exito -> ResultadoInscripcionEquipo.Inscrito(resultado.datos)
            is Resultado.Error -> ResultadoInscripcionEquipo.ErrorServidor(resultado.error)
        }
    }
}

/**
 * Gestiona el cambio opcional de la contraseña inicial (DNI asignado por el capitán) en el primer acceso.
 */
class GestionarPasswordPrimerAccesoCasoUso(
    private val repositorioAgora: RepositorioAgora,
) {
    sealed interface ResultadoCambioPassword {
        data object Completado : ResultadoCambioPassword
        data class ErrorValidacion(val mensaje: String) : ResultadoCambioPassword
        data class ErrorServidor(val error: ErrorRed) : ResultadoCambioPassword
    }

    suspend fun cambiarPassword(nuevaPassword: String): ResultadoCambioPassword {
        val limpia = nuevaPassword.trim()
        if (limpia.length < 6) {
            return ResultadoCambioPassword.ErrorValidacion(
                "La nueva contraseña personal debe tener al menos 6 caracteres."
            )
        }
        return when (
            val resultado = repositorioAgora.gestionarCambioPasswordPrimerAcceso(
                nuevaPassword = limpia,
                omitirCambio = false,
            )
        ) {
            is Resultado.Exito -> ResultadoCambioPassword.Completado
            is Resultado.Error -> ResultadoCambioPassword.ErrorServidor(resultado.error)
        }
    }

    suspend fun conservarPasswordInicial(): Resultado<Unit, ErrorRed> {
        return repositorioAgora.gestionarCambioPasswordPrimerAcceso(
            nuevaPassword = null,
            omitirCambio = true,
        )
    }
}

/**
 * Valida y registra una nueva competición otorgando al usuario el rol `PRESIDENTE`.
 */
class CrearLigaAgoraCasoUso(
    private val repositorioAgora: RepositorioAgora,
) {
    sealed interface ResultadoCreacionLiga {
        data class Creada(val membresiaPresidente: ResumenMembresiaUsuario) : ResultadoCreacionLiga
        data class ErrorValidacion(val mensaje: String) : ResultadoCreacionLiga
        data class ErrorServidor(val error: ErrorRed) : ResultadoCreacionLiga
    }

    suspend operator fun invoke(
        nombreLiga: String,
        requiereSedeEquipo: Boolean,
        sedeUnicaLiga: String,
        partidosPorJornada: Int,
    ): ResultadoCreacionLiga {
        val nombreLimpio = nombreLiga.trim()
        val sedeUnicaLimpia = sedeUnicaLiga.trim()

        if (nombreLimpio.length < 4) {
            return ResultadoCreacionLiga.ErrorValidacion(
                "El nombre de la liga debe tener al menos 4 caracteres."
            )
        }
        if (!requiereSedeEquipo && sedeUnicaLimpia.length < 3) {
            return ResultadoCreacionLiga.ErrorValidacion(
                "Indica el club o instalación central donde se disputará la liga."
            )
        }
        if (partidosPorJornada !in 1..5) {
            return ResultadoCreacionLiga.ErrorValidacion(
                "El número de partidos por jornada debe estar entre 1 y 5."
            )
        }

        val resultado = repositorioAgora.crearLigaComoPresidente(
            nombreLiga = nombreLimpio,
            requiereSedeEquipo = requiereSedeEquipo,
            sedeUnicaLiga = sedeUnicaLimpia.takeIf { !requiereSedeEquipo && it.isNotBlank() },
            partidosPorJornada = partidosPorJornada,
        )

        return when (resultado) {
            is Resultado.Exito -> ResultadoCreacionLiga.Creada(resultado.datos)
            is Resultado.Error -> ResultadoCreacionLiga.ErrorServidor(resultado.error)
        }
    }
}
