package org.synagon.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.synagon.app.dominio.modelos.RolMembresia
import com.synagon.app.dominio.modelos.RolUsuario
import com.synagon.app.inyeccion.contenedorKoinSynagon
import com.synagon.app.presentacion.componentes.BotonVolverOlimpo
import com.synagon.app.presentacion.componentes.CenefaGriega
import com.synagon.app.presentacion.navegacion.RutaSynagon
import com.synagon.app.presentacion.pantallas.acceso.PantallaAcceso
import com.synagon.app.presentacion.pantallas.acta.PantallaSubirActa
import com.synagon.app.presentacion.pantallas.agora.PantallaAgoraPrincipal
import com.synagon.app.presentacion.pantallas.clasificacion.PantallaClasificacionLiga
import com.synagon.app.presentacion.pantallas.estadisticas.PantallaEstadisticasEquipo
import com.synagon.app.presentacion.pantallas.informacion.PantallaPrivacidad
import com.synagon.app.presentacion.pantallas.informacion.PantallaSobreSynagon
import com.synagon.app.presentacion.pantallas.jornada.EventoUi as EventoJornadaUi
import com.synagon.app.presentacion.pantallas.jornada.JornadaViewModel
import com.synagon.app.presentacion.pantallas.jornada.PantallaDetalleJornada
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.KoinIsolatedContext
import org.koin.compose.viewmodel.koinViewModel

/**
 * Destinos internos del panel de detalle de competición de equipo o liga.
 */
private enum class SeccionPanelCompeticion(
    val etiqueta: String,
    val insignia: String,
) {
    JORNADA_ACTUAL(etiqueta = "Jornada Actual", insignia = "JRN"),
    SUBIR_ACTA(etiqueta = "Subir Acta", insignia = "ACT"),
    CLASIFICACION(etiqueta = "Clasificación", insignia = "CLS"),
    ESTADISTICAS(etiqueta = "Estadísticas", insignia = "EST"),
}

@Composable
@Preview
fun App() {
    KoinIsolatedContext(context = contenedorKoinSynagon) {
        TemaSynagon {
            GrafoNavegacionSynagon()
        }
    }
}

/**
 * Grafo de navegación principal tipado con Compose Navigation:
 * `PantallaAcceso` -> `PantallaAgoraPrincipal` -> `PanelDetalleEquipoOLiga`.
 */
@Composable
private fun GrafoNavegacionSynagon() {
    val controladorNavegacion = rememberNavController()

    NavHost(
        navController = controladorNavegacion,
        startDestination = RutaSynagon.Acceso,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<RutaSynagon.Acceso> {
            PantallaAcceso(
                alAccederAlPanelPrincipal = {
                    controladorNavegacion.navigate(RutaSynagon.AgoraPrincipal) {
                        popUpTo(RutaSynagon.Acceso) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                alIrASobreSynagon = {
                    controladorNavegacion.navigate(RutaSynagon.SobreSynagon) {
                        launchSingleTop = true
                    }
                },
                alIrAPrivacidad = {
                    controladorNavegacion.navigate(RutaSynagon.Privacidad) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<RutaSynagon.SobreSynagon> {
            PantallaSobreSynagon(
                alVolver = { controladorNavegacion.popBackStack() },
            )
        }

        composable<RutaSynagon.Privacidad> {
            PantallaPrivacidad(
                alVolver = { controladorNavegacion.popBackStack() },
            )
        }

        composable<RutaSynagon.AgoraPrincipal> {
            PantallaAgoraPrincipal(
                alNavegarAGestionEquipoOLiga = { ligaId, equipoId, rol ->
                    controladorNavegacion.navigate(
                        RutaSynagon.DetalleEquipoOLiga(
                            ligaId = ligaId,
                            equipoId = equipoId,
                            rol = rol.name,
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                alCerrarSesionYNavegarALogin = {
                    controladorNavegacion.navigate(RutaSynagon.Acceso) {
                        popUpTo(RutaSynagon.AgoraPrincipal) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<RutaSynagon.DetalleEquipoOLiga> { entradaRuta ->
            val rutaDetalle = entradaRuta.toRoute<RutaSynagon.DetalleEquipoOLiga>()
            val rolMembresia = runCatching {
                RolMembresia.valueOf(rutaDetalle.rol)
            }.getOrDefault(RolMembresia.JUGADOR)

            PanelDetalleEquipoOLiga(
                ligaId = rutaDetalle.ligaId,
                equipoId = rutaDetalle.equipoId,
                rolMembresia = rolMembresia,
                alVolverAlAgora = { controladorNavegacion.popBackStack() },
            )
        }
    }
}

@Composable
private fun PanelDetalleEquipoOLiga(
    ligaId: String,
    equipoId: String?,
    rolMembresia: RolMembresia,
    alVolverAlAgora: () -> Unit,
) {
    var seccionActual by rememberSaveable {
        mutableStateOf(
            if (rolMembresia == RolMembresia.PRESIDENTE) {
                SeccionPanelCompeticion.CLASIFICACION
            } else {
                SeccionPanelCompeticion.JORNADA_ACTUAL
            }
        )
    }

    val jornadaViewModel: JornadaViewModel = koinViewModel()

    LaunchedEffect(rolMembresia) {
        val rolJornada = when (rolMembresia) {
            RolMembresia.CAPITAN, RolMembresia.PRESIDENTE -> RolUsuario.CAPITAN
            RolMembresia.JUGADOR -> RolUsuario.JUGADOR
        }
        jornadaViewModel.procesarEvento(EventoJornadaUi.CambiarRolVista(rolJornada))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColoresSynagon.AzulEgeo,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CabeceraContextoDetalleAgora(
                ligaId = ligaId,
                equipoId = equipoId,
                rolMembresia = rolMembresia,
                alVolverAlAgora = alVolverAlAgora,
            )
        },
        bottomBar = {
            BarraInferiorOlimpo(
                seccionSeleccionada = seccionActual,
                alSeleccionarSeccion = { seccionActual = it },
            )
        },
    ) { rellenoInterno ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = rellenoInterno.calculateTopPadding(),
                    bottom = rellenoInterno.calculateBottomPadding(),
                ),
        ) {
            when (seccionActual) {
                SeccionPanelCompeticion.JORNADA_ACTUAL -> PantallaDetalleJornada(
                    viewModel = jornadaViewModel,
                )
                SeccionPanelCompeticion.SUBIR_ACTA -> PantallaSubirActa()
                SeccionPanelCompeticion.CLASIFICACION -> PantallaClasificacionLiga()
                SeccionPanelCompeticion.ESTADISTICAS -> PantallaEstadisticasEquipo()
            }
        }
    }
}

@Composable
private fun CabeceraContextoDetalleAgora(
    ligaId: String,
    equipoId: String?,
    rolMembresia: RolMembresia,
    alVolverAlAgora: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColoresSynagon.AzulEgeo)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BotonVolverOlimpo(
                alPulsar = alVolverAlAgora,
                descripcionAccesibilidad = "Volver al Ágora de Synagon",
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (equipoId != null) "GESTIÓN DE EQUIPO" else "DIRECCIÓN DE LIGA",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColoresSynagon.BlancoMarmol,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Liga: ${ligaId.take(12)} · ${equipoId?.let { "Equipo: ${it.take(12)}" } ?: "Presidencia"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColoresSynagon.GrisPiedraJonica,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = when (rolMembresia) {
                    RolMembresia.CAPITAN -> ColoresSynagon.OroOlimpico.copy(alpha = 0.20f)
                    RolMembresia.PRESIDENTE -> ColoresSynagon.VerdeLaurel
                    RolMembresia.JUGADOR -> ColoresSynagon.GrisPiedraJonica.copy(alpha = 0.25f)
                },
            ) {
                Text(
                    text = rolMembresia.etiqueta,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (rolMembresia == RolMembresia.CAPITAN) {
                        ColoresSynagon.OroOlimpico
                    } else {
                        ColoresSynagon.BlancoMarmol
                    },
                )
            }
        }
        CenefaGriega(alto = 3.dp)
    }
}

@Composable
private fun BarraInferiorOlimpo(
    seccionSeleccionada: SeccionPanelCompeticion,
    alSeleccionarSeccion: (SeccionPanelCompeticion) -> Unit,
) {
    NavigationBar(
        containerColor = ColoresSynagon.PizarraEspartana,
        tonalElevation = 8.dp,
    ) {
        SeccionPanelCompeticion.entries.forEach { seccion ->
            val activa = seccion == seccionSeleccionada
            NavigationBarItem(
                selected = activa,
                onClick = { alSeleccionarSeccion(seccion) },
                icon = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activa) {
                            ColoresSynagon.OroOlimpico
                        } else {
                            ColoresSynagon.PizarraElevada
                        },
                    ) {
                        Text(
                            text = seccion.insignia,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (activa) ColoresSynagon.AzulEgeo else ColoresSynagon.GrisPiedraJonica,
                        )
                    }
                },
                label = {
                    Text(
                        text = seccion.etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedTextColor = ColoresSynagon.OroOlimpico,
                    unselectedTextColor = ColoresSynagon.GrisPiedraJonica,
                ),
            )
        }
    }
}
