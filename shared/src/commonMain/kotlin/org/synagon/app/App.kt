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
import com.synagon.app.inyeccion.contenedorKoinSynagon
import com.synagon.app.presentacion.pantallas.acta.PantallaSubirActa
import com.synagon.app.presentacion.pantallas.clasificacion.PantallaClasificacionLiga
import com.synagon.app.presentacion.pantallas.estadisticas.PantallaEstadisticasEquipo
import com.synagon.app.presentacion.pantallas.jornada.PantallaDetalleJornada
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.KoinIsolatedContext

/**
 * Destinos principales del selector de navegación inferior temporal de Synagon.
 */
private enum class SeccionNavegacionSynagon(
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
            ContenedorPrincipalSynagon()
        }
    }
}

@Composable
private fun ContenedorPrincipalSynagon() {
    var seccionActual by rememberSaveable { mutableStateOf(SeccionNavegacionSynagon.JORNADA_ACTUAL) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BarraNavegacionInferiorTemporal(
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
                SeccionNavegacionSynagon.JORNADA_ACTUAL -> PantallaDetalleJornada()
                SeccionNavegacionSynagon.SUBIR_ACTA -> PantallaSubirActa()
                SeccionNavegacionSynagon.CLASIFICACION -> PantallaClasificacionLiga()
                SeccionNavegacionSynagon.ESTADISTICAS -> PantallaEstadisticasEquipo()
            }
        }
    }
}

@Composable
private fun BarraNavegacionInferiorTemporal(
    seccionSeleccionada: SeccionNavegacionSynagon,
    alSeleccionarSeccion: (SeccionNavegacionSynagon) -> Unit,
) {
    NavigationBar(
        containerColor = ColoresSynagon.SuperficieTarjeta,
        tonalElevation = 6.dp,
    ) {
        SeccionNavegacionSynagon.entries.forEach { seccion ->
            val activa = seccion == seccionSeleccionada
            NavigationBarItem(
                selected = activa,
                onClick = { alSeleccionarSeccion(seccion) },
                icon = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activa) ColoresSynagon.AzulPrimario else ColoresSynagon.AzulContenedor.copy(alpha = 0.5f),
                    ) {
                        Text(
                            text = seccion.insignia,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (activa) Color.White else ColoresSynagon.SobreAzulContenedor,
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
                    selectedTextColor = ColoresSynagon.AzulPrimario,
                    unselectedTextColor = ColoresSynagon.TextoSecundario,
                ),
            )
        }
    }
}
