package com.synagon.app.presentacion

import com.synagon.app.datos.mappers.aDominio
import com.synagon.app.datos.mappers.aDto
import com.synagon.app.datos.remoto.api.ProveedorUrlBase
import com.synagon.app.dominio.casosuso.CrearLigaAgoraCasoUso
import com.synagon.app.dominio.casosuso.GestionarPasswordPrimerAccesoCasoUso
import com.synagon.app.dominio.casosuso.InscribirEquipoEnLigaCasoUso
import com.synagon.app.dominio.casosuso.ObtenerDatosAgoraCasoUso
import com.synagon.app.dominio.casosuso.ValidarCodigoInvitacionCasoUso
import com.synagon.app.dominio.modelos.ConfiguracionPreviaLiga
import com.synagon.app.dominio.modelos.ContextoSesionAgora
import com.synagon.app.dominio.modelos.PerfilUsuarioAgora
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.modelos.RolMembresia
import com.synagon.app.dominio.repositorios.RepositorioAgora
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import com.synagon.app.presentacion.pantallas.agora.AgoraViewModel
import com.synagon.app.presentacion.pantallas.agora.EventoAgoraUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AgoraDominioYViewModelTest {

    @Test
    fun `mapea correctamente ResumenMembresiaUsuario y ConfiguracionPreviaLiga entre DTO y Dominio`() {
        val original = ResumenMembresiaUsuario(
            ligaId = "liga-1",
            nombreLiga = "LIGA OLIMPO SEVILLA",
            equipoId = "equipo-1",
            nombreEquipoOSubtitulo = "Real Club Pádel Triana",
            textoSedeDetalle = "Club Pádel Triana",
            partidosPorJornada = 3,
            rol = RolMembresia.CAPITAN,
        )

        val reconstruido = original.aDto().aDominio()
        assertEquals(original, reconstruido)

        val config = ConfiguracionPreviaLiga(
            ligaId = "liga-1",
            nombreLiga = "Liga Olimpo Sevilla",
            requiereSedeEquipo = true,
            partidosPorJornada = 3,
        )
        assertEquals(config, config.aDto().aDominio())
    }

    @Test
    fun `detecta primer acceso con DNI en ContextoSesionAgora y gestiona cambio opcional`() {
        val contexto = ContextoSesionAgora()
        contexto.registrarInicioSesion(
            identificador = "jugador@synagon.es",
            contrasenaIntroducida = "12345678Z",
        )
        assertTrue(contexto.requiereCambioPassword)

        val viewModel = crearViewModelFalso(contextoSesion = contexto, requiereCambioServidor = true)
        assertTrue(viewModel.estadoUi.value.mostrarModalPrimerAccesoPassword)
        assertFalse(viewModel.estadoUi.value.desplegarInputNuevaPassword)

        viewModel.procesarEvento(EventoAgoraUi.SolicitarFormularioCambioPasswordInicial)
        assertTrue(viewModel.estadoUi.value.desplegarInputNuevaPassword)

        viewModel.procesarEvento(EventoAgoraUi.CambiarNuevaPasswordInicial("123"))
        viewModel.procesarEvento(EventoAgoraUi.ConfirmarCambioPasswordInicial)
        assertNotNull(viewModel.estadoUi.value.errorPasswordInicial)

        viewModel.procesarEvento(EventoAgoraUi.CambiarNuevaPasswordInicial("claveSegura2026"))
        viewModel.procesarEvento(EventoAgoraUi.ConfirmarCambioPasswordInicial)
        assertNull(viewModel.estadoUi.value.errorPasswordInicial)
    }

    @Test
    fun `valida codigo de invitacion e inscribe nuevo equipo con rol CAPITAN`() {
        val viewModel = crearViewModelFalso()

        viewModel.procesarEvento(EventoAgoraUi.PulsarUnirEquipo)
        assertTrue(viewModel.estadoUi.value.mostrarModalUnirEquipo)

        viewModel.procesarEvento(EventoAgoraUi.CambiarCodigoInvitacion("olimpo-2026"))
        assertEquals("OLIMPO-2026", viewModel.estadoUi.value.codigoInvitacionInput)

        viewModel.procesarEvento(EventoAgoraUi.ValidarCodigoInvitacion)
        val ligaValidada = viewModel.estadoUi.value.configuracionLigaValidada
        assertNotNull(ligaValidada)
        assertTrue(ligaValidada.requiereSedeEquipo)

        viewModel.procesarEvento(EventoAgoraUi.CambiarNombreNuevoEquipo("Espartanos P.C."))
        viewModel.procesarEvento(EventoAgoraUi.CambiarSedeNuevoEquipo("Club Pádel Cartuja"))
        viewModel.procesarEvento(EventoAgoraUi.ConfirmarInscripcionEquipo)

        assertFalse(viewModel.estadoUi.value.mostrarModalUnirEquipo)
        assertTrue(
            viewModel.estadoUi.value.listaMembresias.any {
                it.nombreEquipoOSubtitulo == "Espartanos P.C." && it.rol == RolMembresia.CAPITAN
            }
        )
    }

    private fun crearViewModelFalso(
        contextoSesion: ContextoSesionAgora = ContextoSesionAgora(),
        requiereCambioServidor: Boolean = false,
    ): AgoraViewModel {
        val repositorioFalso = object : RepositorioAgora {
            private val lista = mutableListOf(
                ResumenMembresiaUsuario(
                    ligaId = "liga-base",
                    nombreLiga = "LIGA BASE",
                    equipoId = "eq-1",
                    nombreEquipoOSubtitulo = "Club Triana",
                    textoSedeDetalle = "Sede Triana",
                    partidosPorJornada = 3,
                    rol = RolMembresia.JUGADOR,
                )
            )

            override suspend fun obtenerPerfilUsuario(): Resultado<PerfilUsuarioAgora, ErrorRed> {
                return Resultado.Exito(
                    PerfilUsuarioAgora(
                        usuarioId = "u-1",
                        nombreUsuario = "Álvaro Iglesias",
                        emailUsuario = "alvaro@synagon.es",
                        telefonoUsuario = "612345678",
                        requiereCambioPassword = requiereCambioServidor,
                    )
                )
            }

            override suspend fun obtenerMembresiasUsuario(): Resultado<List<ResumenMembresiaUsuario>, ErrorRed> {
                return Resultado.Exito(lista.toList())
            }

            override suspend fun validarCodigoInvitacion(
                codigoInvitacion: String,
            ): Resultado<ConfiguracionPreviaLiga, ErrorRed> {
                return Resultado.Exito(
                    ConfiguracionPreviaLiga(
                        ligaId = "liga-olimpo",
                        nombreLiga = "Copa Olimpo 2026",
                        requiereSedeEquipo = true,
                        partidosPorJornada = 3,
                    )
                )
            }

            override suspend fun inscribirEquipoEnLiga(
                ligaId: String,
                codigoInvitacion: String,
                nombreEquipo: String,
                sedeEquipo: String?,
            ): Resultado<ResumenMembresiaUsuario, ErrorRed> {
                val nueva = ResumenMembresiaUsuario(
                    ligaId = ligaId,
                    nombreLiga = "COPA OLIMPO 2026",
                    equipoId = "eq-nuevo",
                    nombreEquipoOSubtitulo = nombreEquipo,
                    textoSedeDetalle = sedeEquipo ?: "Sede Liga",
                    partidosPorJornada = 3,
                    rol = RolMembresia.CAPITAN,
                )
                lista.add(0, nueva)
                return Resultado.Exito(nueva)
            }

            override suspend fun gestionarCambioPasswordPrimerAcceso(
                nuevaPassword: String?,
                omitirCambio: Boolean,
            ): Resultado<Unit, ErrorRed> = Resultado.Exito(Unit)

            override suspend fun crearLigaComoPresidente(
                nombreLiga: String,
                requiereSedeEquipo: Boolean,
                sedeUnicaLiga: String?,
                partidosPorJornada: Int,
            ): Resultado<ResumenMembresiaUsuario, ErrorRed> {
                val nueva = ResumenMembresiaUsuario(
                    ligaId = "liga-nueva",
                    nombreLiga = nombreLiga.uppercase(),
                    equipoId = null,
                    nombreEquipoOSubtitulo = "Presidencia",
                    textoSedeDetalle = sedeUnicaLiga ?: "Sede por equipo",
                    partidosPorJornada = partidosPorJornada,
                    rol = RolMembresia.PRESIDENTE,
                )
                lista.add(0, nueva)
                return Resultado.Exito(nueva)
            }

            override suspend fun actualizarPerfilUsuario(
                nombreUsuario: String,
                telefonoUsuario: String,
            ): Resultado<PerfilUsuarioAgora, ErrorRed> {
                return Resultado.Exito(
                    PerfilUsuarioAgora(
                        usuarioId = "u-1",
                        nombreUsuario = nombreUsuario,
                        emailUsuario = "alvaro@synagon.es",
                        telefonoUsuario = telefonoUsuario,
                        requiereCambioPassword = false,
                    )
                )
            }
        }

        val proveedorUrl = object : ProveedorUrlBase {
            override fun obtenerUrlBase(): String = "http://localhost:8080"
        }

        return AgoraViewModel(
            obtenerDatosAgoraCasoUso = ObtenerDatosAgoraCasoUso(repositorioFalso),
            validarCodigoInvitacionCasoUso = ValidarCodigoInvitacionCasoUso(repositorioFalso),
            inscribirEquipoEnLigaCasoUso = InscribirEquipoEnLigaCasoUso(repositorioFalso),
            gestionarPasswordPrimerAccesoCasoUso = GestionarPasswordPrimerAccesoCasoUso(repositorioFalso),
            crearLigaAgoraCasoUso = CrearLigaAgoraCasoUso(repositorioFalso),
            repositorioAgora = repositorioFalso,
            contextoSesionAgora = contextoSesion,
            proveedorUrlBase = proveedorUrl,
            alcanceCorrutinas = CoroutineScope(Dispatchers.Unconfined),
        )
    }
}
