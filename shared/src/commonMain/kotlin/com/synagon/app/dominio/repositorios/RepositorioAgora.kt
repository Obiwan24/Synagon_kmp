package com.synagon.app.dominio.repositorios

import com.synagon.app.dominio.modelos.ConfiguracionPreviaLiga
import com.synagon.app.dominio.modelos.PerfilUsuarioAgora
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado

/**
 * Contrato de acceso a datos para la Pantalla Principal (Ágora de Synagon):
 * membresías activas del usuario, validación de código de invitación, inscripción de equipo,
 * creación de liga, perfil personal y cambio opcional de contraseña en el primer acceso.
 */
interface RepositorioAgora {

    suspend fun obtenerPerfilUsuario(): Resultado<PerfilUsuarioAgora, ErrorRed>

    suspend fun obtenerMembresiasUsuario(): Resultado<List<ResumenMembresiaUsuario>, ErrorRed>

    suspend fun validarCodigoInvitacion(
        codigoInvitacion: String,
    ): Resultado<ConfiguracionPreviaLiga, ErrorRed>

    suspend fun inscribirEquipoEnLiga(
        ligaId: String,
        codigoInvitacion: String,
        nombreEquipo: String,
        sedeEquipo: String?,
    ): Resultado<ResumenMembresiaUsuario, ErrorRed>

    suspend fun gestionarCambioPasswordPrimerAcceso(
        nuevaPassword: String?,
        omitirCambio: Boolean,
    ): Resultado<Unit, ErrorRed>

    suspend fun crearLigaComoPresidente(
        nombreLiga: String,
        requiereSedeEquipo: Boolean,
        sedeUnicaLiga: String?,
        partidosPorJornada: Int,
    ): Resultado<ResumenMembresiaUsuario, ErrorRed>

    suspend fun actualizarPerfilUsuario(
        nombreUsuario: String,
        telefonoUsuario: String,
    ): Resultado<PerfilUsuarioAgora, ErrorRed>
}
