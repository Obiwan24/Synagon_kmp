package com.synagon.app.presentacion.pantallas.clasificacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.synagon.app.dominio.modelos.FilaClasificacion
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.viewmodel.koinViewModel

private val ANCHO_POSICION = 38.dp
private val ANCHO_EQUIPO = 176.dp
private val ANCHO_METRICA_CORTA = 38.dp
private val ANCHO_PARTIDOS_PAREJA = 78.dp
private val ANCHO_DIFERENCIA_JUEGOS = 68.dp
private val ANCHO_PUNTOS = 52.dp

/**
 * Composable con estado para la tabla oficial de clasificación de la liga.
 * Recolecta [EstadoUi] de [ClasificacionViewModel] y delega en [ContenidoPantallaClasificacionLiga].
 */
@Composable
fun PantallaClasificacionLiga(
    modifier: Modifier = Modifier,
    viewModel: ClasificacionViewModel = koinViewModel(),
) {
    val estadoUi by viewModel.estadoUi.collectAsStateWithLifecycle()
    val estadoSnackbar = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.efectosUi.collect { efecto ->
            when (efecto) {
                is EfectoUi.MostrarMensaje -> estadoSnackbar.showSnackbar(efecto.texto)
            }
        }
    }

    ContenidoPantallaClasificacionLiga(
        estadoUi = estadoUi,
        alProcesarEvento = viewModel::procesarEvento,
        estadoSnackbar = estadoSnackbar,
        modifier = modifier,
    )
}

/**
 * Composable completamente stateless (sin ViewModel) que muestra la clasificación oficial
 * con Posición, Equipo, PJ, PG, PP, Partidos Ganados/Perdidos, Diferencia de Juegos y Puntos totales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPantallaClasificacionLiga(
    estadoUi: EstadoUi,
    alProcesarEvento: (EventoUi) -> Unit,
    estadoSnackbar: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = estadoSnackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Clasificación de la Liga",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = estadoUi.nombreDivision,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { alProcesarEvento(EventoUi.RecargarClasificacion) },
                    ) {
                        Text(
                            text = "Actualizar",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColoresSynagon.AzulPrimario,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { rellenoInterno ->
        if (estadoUi.cargando && estadoUi.filasClasificacion.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(rellenoInterno),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = ColoresSynagon.AzulPrimario)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(rellenoInterno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (estadoUi.mensajeEstadoConexion != null) {
                item {
                    AvisoEstadoClasificacionConexion(
                        mensaje = estadoUi.mensajeEstadoConexion,
                        alReintentar = { alProcesarEvento(EventoUi.RecargarClasificacion) },
                    )
                }
            }

            estadoUi.filaEquipoPropio?.let { resumenPropio ->
                item {
                    TarjetaResumenEquipoPropio(fila = resumenPropio)
                }
            }

            item {
                TablaClasificacionOficial(
                    filas = estadoUi.filasClasificacion,
                    idEquipoPropio = estadoUi.idEquipoPropio,
                )
            }

            item {
                LeyendaTablaClasificacion()
            }
        }
    }
}

@Composable
private fun TarjetaResumenEquipoPropio(
    fila: FilaClasificacion,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.AzulContenedor),
        border = BorderStroke(1.dp, ColoresSynagon.AzulPrimario.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NUESTRO EQUIPO · PUESTO #${fila.posicion}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColoresSynagon.AzulPrimario,
                )
                Text(
                    text = fila.nombreEquipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColoresSynagon.SobreAzulContenedor,
                )
                Text(
                    text = "${fila.partidosGanados}G - ${fila.partidosPerdidos}P · Parejas ${fila.partidosParejaGanados}/${fila.partidosParejaPerdidos} · Dif. Juegos ${formatearConSigno(fila.diferenciaJuegos)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColoresSynagon.SobreAzulContenedor.copy(alpha = 0.85f),
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ColoresSynagon.AzulPrimario,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${fila.puntos}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    )
                    Text(
                        text = "PTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

@Composable
private fun TablaClasificacionOficial(
    filas: List<FilaClasificacion>,
    idEquipoPropio: String,
) {
    val desplazamientoHorizontal = rememberScrollState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.SuperficieTarjeta),
        border = BorderStroke(1.dp, ColoresSynagon.BordeSuave),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(desplazamientoHorizontal),
        ) {
            // Cabecera oficial de columnas
            Row(
                modifier = Modifier
                    .background(ColoresSynagon.AzulPrimario)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CeldaCabeceraTabla("Pos", ANCHO_POSICION, TextAlign.Center)
                CeldaCabeceraTabla("Equipo", ANCHO_EQUIPO, TextAlign.Start)
                CeldaCabeceraTabla("PJ", ANCHO_METRICA_CORTA, TextAlign.Center)
                CeldaCabeceraTabla("PG", ANCHO_METRICA_CORTA, TextAlign.Center)
                CeldaCabeceraTabla("PP", ANCHO_METRICA_CORTA, TextAlign.Center)
                CeldaCabeceraTabla("Partidos G/P", ANCHO_PARTIDOS_PAREJA, TextAlign.Center)
                CeldaCabeceraTabla("Dif. Juegos", ANCHO_DIFERENCIA_JUEGOS, TextAlign.Center)
                CeldaCabeceraTabla("Pts", ANCHO_PUNTOS, TextAlign.Center)
            }

            filas.forEachIndexed { indice, fila ->
                val esEquipoPropio = fila.idEquipo == idEquipoPropio
                val colorFondoFila = when {
                    esEquipoPropio -> ColoresSynagon.AzulContenedor.copy(alpha = 0.55f)
                    indice % 2 == 1 -> ColoresSynagon.FondoPantalla.copy(alpha = 0.65f)
                    else -> ColoresSynagon.SuperficieTarjeta
                }

                FilaTablaClasificacion(
                    fila = fila,
                    esEquipoPropio = esEquipoPropio,
                    totalEquipos = filas.size,
                    colorFondo = colorFondoFila,
                )

                if (indice < filas.lastIndex) {
                    HorizontalDivider(color = ColoresSynagon.BordeSuave.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun CeldaCabeceraTabla(
    texto: String,
    ancho: Dp,
    alineacion: TextAlign,
) {
    Text(
        text = texto,
        modifier = Modifier.width(ancho),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = alineacion,
    )
}

@Composable
private fun FilaTablaClasificacion(
    fila: FilaClasificacion,
    esEquipoPropio: Boolean,
    totalEquipos: Int,
    colorFondo: Color,
) {
    val colorInsigniaPosicion = when {
        fila.posicion <= 2 -> ColoresSynagon.VerdeQuorumFondo to ColoresSynagon.VerdeQuorum
        fila.posicion > totalEquipos - 2 && totalEquipos >= 6 -> ColoresSynagon.RojoBajaFondo to ColoresSynagon.RojoBaja
        else -> ColoresSynagon.FondoPantalla to ColoresSynagon.TextoPrincipal
    }

    val colorDiferenciaJuegos = when {
        fila.diferenciaJuegos > 0 -> ColoresSynagon.VerdeQuorum
        fila.diferenciaJuegos < 0 -> ColoresSynagon.RojoBaja
        else -> ColoresSynagon.TextoSecundario
    }

    Row(
        modifier = Modifier
            .background(colorFondo)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.width(ANCHO_POSICION),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(colorInsigniaPosicion.first),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${fila.posicion}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorInsigniaPosicion.second,
                )
            }
        }

        Column(
            modifier = Modifier
                .width(ANCHO_EQUIPO)
                .padding(end = 8.dp),
        ) {
            Text(
                text = fila.nombreEquipo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (esEquipoPropio) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = ColoresSynagon.TextoPrincipal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!fila.club.isNullOrBlank()) {
                Text(
                    text = fila.club,
                    style = MaterialTheme.typography.labelSmall,
                    color = ColoresSynagon.TextoSecundario,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        CeldaValorTabla("${fila.partidosJugados}", ANCHO_METRICA_CORTA)
        CeldaValorTabla("${fila.partidosGanados}", ANCHO_METRICA_CORTA, FontWeight.SemiBold)
        CeldaValorTabla("${fila.partidosPerdidos}", ANCHO_METRICA_CORTA)
        CeldaValorTabla(
            texto = "${fila.partidosParejaGanados}/${fila.partidosParejaPerdidos}",
            ancho = ANCHO_PARTIDOS_PAREJA,
            pesoFuente = FontWeight.Medium,
        )
        CeldaValorTabla(
            texto = formatearConSigno(fila.diferenciaJuegos),
            ancho = ANCHO_DIFERENCIA_JUEGOS,
            pesoFuente = FontWeight.Bold,
            colorTexto = colorDiferenciaJuegos,
        )

        Box(
            modifier = Modifier.width(ANCHO_PUNTOS),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (esEquipoPropio) ColoresSynagon.AzulPrimario else ColoresSynagon.AzulContenedor,
            ) {
                Text(
                    text = "${fila.puntos}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (esEquipoPropio) Color.White else ColoresSynagon.SobreAzulContenedor,
                )
            }
        }
    }
}

@Composable
private fun CeldaValorTabla(
    texto: String,
    ancho: Dp,
    pesoFuente: FontWeight = FontWeight.Normal,
    colorTexto: Color = ColoresSynagon.TextoPrincipal,
) {
    Text(
        text = texto,
        modifier = Modifier.width(ancho),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = pesoFuente,
        color = colorTexto,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun LeyendaTablaClasificacion() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ColoresSynagon.SuperficieTarjeta,
        border = BorderStroke(1.dp, ColoresSynagon.BordeSuave),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Leyenda Oficial Synagon",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ColoresSynagon.AzulPrimario,
            )
            Text(
                text = "PJ: Jornadas jugadas · PG/PP: Enfrentamientos ganados/perdidos · Partidos G/P: Partidos individuales de parejas ganados/perdidos · Dif. Juegos: Juegos a favor menos en contra.",
                style = MaterialTheme.typography.labelSmall,
                color = ColoresSynagon.TextoSecundario,
            )
            Text(
                text = "Desliza horizontalmente la tabla en pantallas compactas para ver todas las métricas.",
                style = MaterialTheme.typography.labelSmall,
                color = ColoresSynagon.TextoSecundario,
            )
        }
    }
}

@Composable
private fun AvisoEstadoClasificacionConexion(
    mensaje: String,
    alReintentar: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ColoresSynagon.AmbarDudaFondo,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = mensaje,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = ColoresSynagon.AmbarDuda,
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = alReintentar) {
                Text(
                    text = "Reintentar",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun formatearConSigno(valor: Int): String = if (valor > 0) "+$valor" else "$valor"

@Preview
@Composable
private fun PrevisualizacionPantallaClasificacionLiga() {
    TemaSynagon {
        ContenidoPantallaClasificacionLiga(
            estadoUi = EstadoUi(
                cargando = false,
                filasClasificacion = DatosSemillaJornada.crearClasificacionDivisionSemilla(),
            ),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}
