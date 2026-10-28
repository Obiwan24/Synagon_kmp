package com.synagon.app.presentacion.pantallas.estadisticas

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.synagon.app.dominio.casosuso.CriterioOrdenEstadisticas
import com.synagon.app.dominio.casosuso.OrdenarEstadisticasEquipoCasoUso
import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composable con estado para las estadísticas de rendimiento y compromiso de los jugadores del equipo.
 * Recolecta [EstadoUi] desde [EstadisticasViewModel] y delega en [ContenidoPantallaEstadisticasEquipo].
 */
@Composable
fun PantallaEstadisticasEquipo(
    modifier: Modifier = Modifier,
    viewModel: EstadisticasViewModel = koinViewModel(),
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

    ContenidoPantallaEstadisticasEquipo(
        estadoUi = estadoUi,
        alProcesarEvento = viewModel::procesarEvento,
        estadoSnackbar = estadoSnackbar,
        modifier = modifier,
    )
}

/**
 * Composable completamente stateless (sin ViewModel) que presenta las 8 métricas obligatorias
 * de cada jugador y los chips superiores de ordenación del ranking interno.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPantallaEstadisticasEquipo(
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
                            text = "Estadísticas del Equipo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = estadoUi.nombreEquipo,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { alProcesarEvento(EventoUi.RecargarEstadisticas) },
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
        if (estadoUi.cargando && estadoUi.estadisticasJugadores.isEmpty()) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (estadoUi.mensajeEstadoConexion != null) {
                item {
                    AvisoEstadoEstadisticasConexion(
                        mensaje = estadoUi.mensajeEstadoConexion,
                        alReintentar = { alProcesarEvento(EventoUi.RecargarEstadisticas) },
                    )
                }
            }

            item {
                BarraChipsOrdenEstadisticas(
                    criterioActivo = estadoUi.criterioOrdenSeleccionado,
                    totalJugadores = estadoUi.estadisticasJugadores.size,
                    alSeleccionarCriterio = { criterio ->
                        alProcesarEvento(EventoUi.CambiarCriterioOrden(criterio))
                    },
                )
            }

            itemsIndexed(
                items = estadoUi.estadisticasJugadores,
                key = { _, jugador -> jugador.idJugador },
            ) { indice, estadisticaJugador ->
                TarjetaRendimientoJugador(
                    posicionRanking = indice + 1,
                    estadistica = estadisticaJugador,
                    criterioDestacado = estadoUi.criterioOrdenSeleccionado,
                )
            }
        }
    }
}

@Composable
private fun BarraChipsOrdenEstadisticas(
    criterioActivo: CriterioOrdenEstadisticas,
    totalJugadores: Int,
    alSeleccionarCriterio: (CriterioOrdenEstadisticas) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.SuperficieTarjeta),
        border = BorderStroke(1.dp, ColoresSynagon.BordeSuave),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ORDENAR RANKING INTERNO POR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColoresSynagon.AzulPrimario,
                )
                Text(
                    text = "$totalJugadores miembros",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColoresSynagon.TextoSecundario,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CriterioOrdenEstadisticas.entries.forEach { criterio ->
                    val seleccionado = criterio == criterioActivo
                    FilterChip(
                        selected = seleccionado,
                        onClick = { alSeleccionarCriterio(criterio) },
                        label = {
                            Text(
                                text = criterio.etiqueta,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColoresSynagon.AzulPrimario,
                            selectedLabelColor = Color.White,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaRendimientoJugador(
    posicionRanking: Int,
    estadistica: EstadisticaJugador,
    criterioDestacado: CriterioOrdenEstadisticas,
) {
    val esPodio = posicionRanking <= 3
    val colorMedallaFondo = when (posicionRanking) {
        1 -> ColoresSynagon.VerdeQuorumFondo
        2, 3 -> ColoresSynagon.AzulContenedor
        else -> ColoresSynagon.FondoPantalla
    }
    val colorMedallaTexto = when (posicionRanking) {
        1 -> ColoresSynagon.VerdeQuorum
        2, 3 -> ColoresSynagon.SobreAzulContenedor
        else -> ColoresSynagon.TextoSecundario
    }

    val etiquetaDestacada = when (criterioDestacado) {
        CriterioOrdenEstadisticas.VICTORIAS -> "${estadistica.partidosGanados} victorias (${estadistica.porcentajeVictoriasPartidos}%)"
        CriterioOrdenEstadisticas.PARTIDOS_JUGADOS -> "${estadistica.partidosJugados} jugados"
        CriterioOrdenEstadisticas.COMPROMISO -> "${estadistica.partidosApuntados} apuntados"
        CriterioOrdenEstadisticas.DIFERENCIA_JUEGOS -> "Dif. juegos ${formatearBalance(estadistica.diferenciaJuegos)}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.SuperficieTarjeta),
        border = BorderStroke(
            width = if (posicionRanking == 1) 1.5.dp else 1.dp,
            color = if (posicionRanking == 1) ColoresSynagon.VerdeQuorumBorde else ColoresSynagon.BordeSuave,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colorMedallaFondo),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "#$posicionRanking",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorMedallaTexto,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = estadistica.nombreJugador,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (esPodio) FontWeight.ExtraBold else FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (estadistica.esCapitan) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = ColoresSynagon.AzulContenedor,
                                ) {
                                    Text(
                                        text = "CAP",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ColoresSynagon.SobreAzulContenedor,
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Sets G/P: ${estadistica.setsGanados}/${estadistica.setsPerdidos} · Juegos G/P: ${estadistica.juegosGanados}/${estadistica.juegosPerdidos} (${formatearBalance(estadistica.diferenciaJuegos)})",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColoresSynagon.TextoSecundario,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColoresSynagon.AzulContenedor,
                ) {
                    Text(
                        text = etiquetaDestacada,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ColoresSynagon.SobreAzulContenedor,
                    )
                }
            }

            HorizontalDivider(color = ColoresSynagon.BordeSuave.copy(alpha = 0.55f))

            // Fila 1 de las 8 métricas obligatorias: Partidos (Apuntados, Jugados, Ganados, Perdidos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CeldaMetricaJugador(
                    titulo = "Apuntados",
                    valor = "${estadistica.partidosApuntados}",
                    resaltada = criterioDestacado == CriterioOrdenEstadisticas.COMPROMISO,
                    modifier = Modifier.weight(1f),
                )
                CeldaMetricaJugador(
                    titulo = "Jugados",
                    valor = "${estadistica.partidosJugados}",
                    resaltada = criterioDestacado == CriterioOrdenEstadisticas.PARTIDOS_JUGADOS,
                    modifier = Modifier.weight(1f),
                )
                CeldaMetricaJugador(
                    titulo = "Ganados",
                    valor = "${estadistica.partidosGanados}",
                    resaltada = criterioDestacado == CriterioOrdenEstadisticas.VICTORIAS,
                    colorValor = ColoresSynagon.VerdeQuorum,
                    modifier = Modifier.weight(1f),
                )
                CeldaMetricaJugador(
                    titulo = "Perdidos",
                    valor = "${estadistica.partidosPerdidos}",
                    colorValor = if (estadistica.partidosPerdidos > 0) ColoresSynagon.RojoBaja else ColoresSynagon.TextoPrincipal,
                    modifier = Modifier.weight(1f),
                )
            }

            // Fila 2 de las 8 métricas obligatorias: Sets G/P y Juegos G/P
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CeldaMetricaJugador(
                    titulo = "Sets G",
                    valor = "${estadistica.setsGanados}",
                    modifier = Modifier.weight(1f),
                )
                CeldaMetricaJugador(
                    titulo = "Sets P",
                    valor = "${estadistica.setsPerdidos}",
                    modifier = Modifier.weight(1f),
                )
                CeldaMetricaJugador(
                    titulo = "Juegos G",
                    valor = "${estadistica.juegosGanados}",
                    resaltada = criterioDestacado == CriterioOrdenEstadisticas.DIFERENCIA_JUEGOS,
                    modifier = Modifier.weight(1f),
                )
                CeldaMetricaJugador(
                    titulo = "Juegos P",
                    valor = "${estadistica.juegosPerdidos}",
                    resaltada = criterioDestacado == CriterioOrdenEstadisticas.DIFERENCIA_JUEGOS,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun CeldaMetricaJugador(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    resaltada: Boolean = false,
    colorValor: Color = ColoresSynagon.TextoPrincipal,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (resaltada) ColoresSynagon.AzulContenedor.copy(alpha = 0.65f) else ColoresSynagon.FondoPantalla,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colorValor,
                textAlign = TextAlign.Center,
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = ColoresSynagon.TextoSecundario,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AvisoEstadoEstadisticasConexion(
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

private fun formatearBalance(valor: Int): String = if (valor > 0) "+$valor" else "$valor"

@Preview
@Composable
private fun PrevisualizacionPantallaEstadisticasEquipo() {
    val ordenador = OrdenarEstadisticasEquipoCasoUso()
    TemaSynagon {
        ContenidoPantallaEstadisticasEquipo(
            estadoUi = EstadoUi(
                cargando = false,
                estadisticasJugadores = ordenador.ejecutar(
                    DatosSemillaJornada.crearEstadisticasEquipoSemilla(),
                    CriterioOrdenEstadisticas.VICTORIAS,
                ),
            ),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}
