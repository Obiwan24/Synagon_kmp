package org.synagon.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.synagon.app.inyeccion.contenedorKoinSynagon
import com.synagon.app.presentacion.navegacion.RutaSynagon
import com.synagon.app.presentacion.pantallas.acceso.PantallaAcceso
import com.synagon.app.presentacion.pantallas.acta.PantallaSubirActa
import com.synagon.app.presentacion.pantallas.clasificacion.PantallaClasificacionLiga
import com.synagon.app.presentacion.pantallas.estadisticas.PantallaEstadisticasEquipo
import com.synagon.app.presentacion.pantallas.informacion.PantallaPrivacidad
import com.synagon.app.presentacion.pantallas.informacion.PantallaSobreSynagon
import com.synagon.app.presentacion.pantallas.jornada.PantallaDetalleJornada
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.KoinIsolatedContext

/**
 * Destinos internos del panel principal de competición tras iniciar sesión.
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
 * Grafo de navegación principal tipado con Compose Navigation.
 * Define [RutaSynagon.Acceso] como destino inicial.
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
                    controladorNavegacion.navigate(RutaSynagon.PanelPrincipal) {
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

        composable<RutaSynagon.PanelPrincipal> {
            PanelPrincipalSynagon()
        }
    }
}

@Composable
private fun PanelPrincipalSynagon() {
    var seccionActual by rememberSaveable {
        mutableStateOf(SeccionPanelCompeticion.JORNADA_ACTUAL)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColoresSynagon.AzulEgeo,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BarraInferiorOlimpo(
                seccionSeleccionada = seccionActual,
                alSeleccionarSeccion = { seccionActual = it },
            )
        },
    ) { rellenoInferior ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = rellenoInferior.calculateBottomPadding()),
        ) {
            when (seccionActual) {
                SeccionPanelCompeticion.JORNADA_ACTUAL -> PantallaDetalleJornada()
                SeccionPanelCompeticion.SUBIR_ACTA -> PantallaSubirActa()
                SeccionPanelCompeticion.CLASIFICACION -> PantallaClasificacionLiga()
                SeccionPanelCompeticion.ESTADISTICAS -> PantallaEstadisticasEquipo()
            }
        }
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
