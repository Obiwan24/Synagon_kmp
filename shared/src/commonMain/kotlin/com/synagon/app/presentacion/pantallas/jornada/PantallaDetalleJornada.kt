package com.synagon.app.presentacion.pantallas.jornada

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.synagon.app.dominio.modelos.Enfrentamiento
import com.synagon.app.dominio.modelos.EstadoDisponibilidad
import com.synagon.app.dominio.modelos.EstadoEnfrentamiento
import com.synagon.app.dominio.modelos.JugadorEquipo
import com.synagon.app.dominio.modelos.PropuestaFecha
import com.synagon.app.dominio.modelos.RolUsuario
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composable con estado para la pantalla principal de gestión de jornada.
 * Recolecta el [EstadoUi] del [JornadaViewModel] y delega el renderizado en [ContenidoPantallaDetalleJornada].
 */
@Composable
fun PantallaDetalleJornada(
    modifier: Modifier = Modifier,
    viewModel: JornadaViewModel = koinViewModel(),
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

    ContenidoPantallaDetalleJornada(
        estadoUi = estadoUi,
        alProcesarEvento = viewModel::procesarEvento,
        estadoSnackbar = estadoSnackbar,
        modifier = modifier,
    )
}

/**
 * Composable completamente stateless (sin ViewModel) que dibuja el detalle de la jornada,
 * la encuesta de fechas propuestas y el panel de capitán.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPantallaDetalleJornada(
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
                            text = "SYNAGON · Gestión de Jornada",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "${estadoUi.nombreJugadorActual} (${estadoUi.rolUsuario.etiqueta})",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColoresSynagon.GrisPiedraJonica,
                        )
                    }
                },
                actions = {
                    SelectorRolPrueba(
                        rolActual = estadoUi.rolUsuario,
                        alCambiarRol = { alProcesarEvento(EventoUi.CambiarRolVista(it)) },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColoresSynagon.PizarraEspartana,
                    titleContentColor = ColoresSynagon.BlancoMarmol,
                ),
            )
        },
    ) { rellenoInterno ->
        when {
            estadoUi.cargando && estadoUi.enfrentamiento == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoInterno),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Conectando con servidor Synagon...",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            estadoUi.enfrentamiento != null -> {
                val enfrentamiento = estadoUi.enfrentamiento
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoInterno),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (estadoUi.mensajeEstadoConexion != null) {
                        item {
                            BannerEstadoServidor(
                                mensaje = estadoUi.mensajeEstadoConexion,
                                alReintentar = { alProcesarEvento(EventoUi.RecargarJornada) },
                            )
                        }
                    }

                    item {
                        TarjetaCabeceraEnfrentamiento(enfrentamiento = enfrentamiento)
                    }

                    item {
                        SeccionEncuestaFechas(
                            enfrentamiento = enfrentamiento,
                            idJugadorActual = estadoUi.idJugadorActual,
                            procesando = estadoUi.procesandoAccion,
                            alVotar = { idPropuesta, estado ->
                                alProcesarEvento(
                                    EventoUi.VotarDisponibilidad(
                                        idPropuestaFecha = idPropuesta,
                                        estado = estado,
                                    )
                                )
                            },
                        )
                    }

                    if (estadoUi.esCapitan) {
                        item {
                            SeccionPanelCapitan(
                                estadoUi = estadoUi,
                                enfrentamiento = enfrentamiento,
                                alProcesarEvento = alProcesarEvento,
                            )
                        }
                    }

                    if (enfrentamiento.parejas.isNotEmpty()) {
                        item {
                            TarjetaResumenAlineacionConfirmada(enfrentamiento = enfrentamiento)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorRolPrueba(
    rolActual: RolUsuario,
    alCambiarRol: (RolUsuario) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RolUsuario.entries.forEach { rol ->
            val seleccionado = rol == rolActual
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (seleccionado) ColoresSynagon.OroOlimpico else Color.Transparent)
                    .clickable { alCambiarRol(rol) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = rol.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                    color = if (seleccionado) ColoresSynagon.AzulEgeo else ColoresSynagon.BlancoMarmol,
                )
            }
        }
    }
}

@Composable
private fun BannerEstadoServidor(
    mensaje: String,
    alReintentar: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = alReintentar) {
                Text(
                    text = "Conectar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun TarjetaCabeceraEnfrentamiento(
    enfrentamiento: Enfrentamiento,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "JORNADA ${enfrentamiento.numeroJornada}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = enfrentamiento.nombreDivision,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                ChipEstadoEnfrentamiento(estado = enfrentamiento.estado)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BloqueEquipoCabecera(
                    etiquetaCondicion = "LOCAL",
                    nombreEquipo = enfrentamiento.nombreEquipoLocal,
                    nombreClub = enfrentamiento.clubLocal,
                    alineacion = Alignment.Start,
                    modifier = Modifier.weight(1f),
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }

                BloqueEquipoCabecera(
                    etiquetaCondicion = "VISITANTE",
                    nombreEquipo = enfrentamiento.nombreEquipoVisitante,
                    nombreClub = enfrentamiento.clubVisitante,
                    alineacion = Alignment.End,
                    modifier = Modifier.weight(1f),
                )
            }

            if (enfrentamiento.fechaHoraDefinitiva != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColoresSynagon.VerdeQuorumFondo,
                    border = BorderStroke(1.dp, ColoresSynagon.VerdeQuorumBorde),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = "Horario oficial confirmado: ${formatearFechaHoraEspanol(enfrentamiento.fechaHoraDefinitiva)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ColoresSynagon.VerdeQuorum,
                        )
                        if (!enfrentamiento.pistas.isNullOrBlank()) {
                            Text(
                                text = "Sede: ${enfrentamiento.pistas}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresSynagon.VerdeQuorum,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BloqueEquipoCabecera(
    etiquetaCondicion: String,
    nombreEquipo: String,
    nombreClub: String?,
    alineacion: Alignment.Horizontal,
    modifier: Modifier = Modifier,
) {
    val alineacionTexto = if (alineacion == Alignment.End) TextAlign.End else TextAlign.Start
    Column(
        modifier = modifier,
        horizontalAlignment = alineacion,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = etiquetaCondicion,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = nombreEquipo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = alineacionTexto,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (!nombreClub.isNullOrBlank()) {
            Text(
                text = nombreClub,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = alineacionTexto,
            )
        }
    }
}

@Composable
private fun ChipEstadoEnfrentamiento(
    estado: EstadoEnfrentamiento,
) {
    val (colorFondo, colorTexto) = when (estado) {
        EstadoEnfrentamiento.PENDIENTE_FECHA -> ColoresSynagon.AmbarDudaFondo to ColoresSynagon.AmbarDuda
        EstadoEnfrentamiento.FECHA_PROPUESTA,
        EstadoEnfrentamiento.VOTACION_DISPONIBILIDAD_ABIERTA -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        EstadoEnfrentamiento.FECHA_FIJADA,
        EstadoEnfrentamiento.CONVOCATORIA_ABIERTA -> ColoresSynagon.VerdeQuorumFondo to ColoresSynagon.VerdeQuorum
        EstadoEnfrentamiento.ALINEACION_CERRADA,
        EstadoEnfrentamiento.EN_DISPUTA -> Color(0xFFE0E7FF) to Color(0xFF3730A3)
        EstadoEnfrentamiento.FINALIZADO -> Color(0xFFE2E8F0) to Color(0xFF1E293B)
        EstadoEnfrentamiento.APLAZADO -> ColoresSynagon.AmbarDudaFondo to ColoresSynagon.AmbarDuda
        EstadoEnfrentamiento.CANCELADO -> ColoresSynagon.RojoBajaFondo to ColoresSynagon.RojoBaja
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = colorFondo,
    ) {
        Text(
            text = estado.etiqueta.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colorTexto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun SeccionEncuestaFechas(
    enfrentamiento: Enfrentamiento,
    idJugadorActual: String,
    procesando: Boolean,
    alVotar: (String, EstadoDisponibilidad) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Encuesta de Fechas Propuestas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Indica tu disponibilidad en las 2 opciones propuestas. Se requieren al menos 6 jugadores disponibles para formar las 3 parejas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        enfrentamiento.propuestasFecha.take(2).forEach { propuesta ->
            val disponibles = enfrentamiento.contarDisponiblesEnPropuesta(propuesta.id)
            val dudas = enfrentamiento.contarDudasEnPropuesta(propuesta.id)
            val bajas = enfrentamiento.contarNoDisponiblesEnPropuesta(propuesta.id)
            val alcanzaQuorum = enfrentamiento.alcanzaQuorumDisponibles(propuesta.id)
            val votoActual = enfrentamiento.obtenerEstadoJugadorEnPropuesta(
                idPropuestaFecha = propuesta.id,
                idJugador = idJugadorActual,
            )

            TarjetaPropuestaFecha(
                propuesta = propuesta,
                totalDisponibles = disponibles,
                totalDudas = dudas,
                totalNoDisponibles = bajas,
                alcanzaQuorum = alcanzaQuorum,
                votoJugadorActual = votoActual,
                habilitado = !procesando,
                alSeleccionarEstado = { estado -> alVotar(propuesta.id, estado) },
            )
        }
    }
}

@Composable
private fun TarjetaPropuestaFecha(
    propuesta: PropuestaFecha,
    totalDisponibles: Int,
    totalDudas: Int,
    totalNoDisponibles: Int,
    alcanzaQuorum: Boolean,
    votoJugadorActual: EstadoDisponibilidad?,
    habilitado: Boolean,
    alSeleccionarEstado: (EstadoDisponibilidad) -> Unit,
) {
    val bordeTarjeta = if (alcanzaQuorum) {
        BorderStroke(2.dp, ColoresSynagon.VerdeQuorumBorde)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alcanzaQuorum) {
                Color(0xFFF6FEF9)
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        border = bordeTarjeta,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "OPCIÓN ${propuesta.orden}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        if (propuesta.esSeleccionada) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary,
                            ) {
                                Text(
                                    text = "CONFIRMADA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text = formatearFechaHoraEspanol(propuesta.fechaHora),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (!propuesta.pistaLugar.isNullOrBlank()) {
                        Text(
                            text = propuesta.pistaLugar,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                IndicadorDisponiblesPropuesta(
                    totalDisponibles = totalDisponibles,
                    alcanzaQuorum = alcanzaQuorum,
                )
            }

            val progresoQuorum = (totalDisponibles.toFloat() / Enfrentamiento.MINIMO_JUGADORES_CONVOCATORIA.toFloat())
                .coerceIn(0f, 1f)

            LinearProgressIndicator(
                progress = { progresoQuorum },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = if (alcanzaQuorum) {
                    ColoresSynagon.VerdeQuorumIntenso
                } else {
                    MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (alcanzaQuorum) {
                        "Quórum alcanzado ($totalDisponibles disponibles · mín. 6)"
                    } else {
                        "Faltan ${Enfrentamiento.MINIMO_JUGADORES_CONVOCATORIA - totalDisponibles} disponibles para llegar a 6"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (alcanzaQuorum) FontWeight.Bold else FontWeight.Normal,
                    color = if (alcanzaQuorum) {
                        ColoresSynagon.VerdeQuorum
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Text(
                    text = "Dudas: $totalDudas · Bajas: $totalNoDisponibles",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

            Text(
                text = "Tu disponibilidad:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BotonVotoDisponibilidad(
                    etiqueta = "DISPONIBLE",
                    seleccionado = votoJugadorActual == EstadoDisponibilidad.DISPONIBLE,
                    colorActivoFondo = ColoresSynagon.VerdeQuorumIntenso,
                    colorActivoTexto = Color.White,
                    habilitado = habilitado,
                    alPulsar = { alSeleccionarEstado(EstadoDisponibilidad.DISPONIBLE) },
                    modifier = Modifier.weight(1f),
                )
                BotonVotoDisponibilidad(
                    etiqueta = "DUDA",
                    seleccionado = votoJugadorActual == EstadoDisponibilidad.DUDA,
                    colorActivoFondo = ColoresSynagon.AmbarDuda,
                    colorActivoTexto = Color.White,
                    habilitado = habilitado,
                    alPulsar = { alSeleccionarEstado(EstadoDisponibilidad.DUDA) },
                    modifier = Modifier.weight(1f),
                )
                BotonVotoDisponibilidad(
                    etiqueta = "NO DISP.",
                    seleccionado = votoJugadorActual == EstadoDisponibilidad.NO_DISPONIBLE,
                    colorActivoFondo = ColoresSynagon.RojoBaja,
                    colorActivoTexto = Color.White,
                    habilitado = habilitado,
                    alPulsar = { alSeleccionarEstado(EstadoDisponibilidad.NO_DISPONIBLE) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun IndicadorDisponiblesPropuesta(
    totalDisponibles: Int,
    alcanzaQuorum: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (alcanzaQuorum) {
            ColoresSynagon.VerdeQuorumFondo
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = if (alcanzaQuorum) {
            BorderStroke(1.5.dp, ColoresSynagon.VerdeQuorumIntenso)
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$totalDisponibles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (alcanzaQuorum) {
                    ColoresSynagon.VerdeQuorum
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            Text(
                text = "DISPONIBLES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (alcanzaQuorum) {
                    ColoresSynagon.VerdeQuorum
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
private fun BotonVotoDisponibilidad(
    etiqueta: String,
    seleccionado: Boolean,
    colorActivoFondo: Color,
    colorActivoTexto: Color,
    habilitado: Boolean,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (seleccionado) {
        Button(
            onClick = alPulsar,
            enabled = habilitado,
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorActivoFondo,
                contentColor = colorActivoTexto,
            ),
        ) {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    } else {
        OutlinedButton(
            onClick = alPulsar,
            enabled = habilitado,
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
        ) {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SeccionPanelCapitan(
    estadoUi: EstadoUi,
    enfrentamiento: Enfrentamiento,
    alProcesarEvento: (EventoUi) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Panel de Capitán",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Cierre de horario oficial y convocatoria de las 3 parejas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = "CAPITÁN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            // 1. Confirmación de fecha definitiva
            Text(
                text = "1. Confirmar fecha definitiva",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                enfrentamiento.propuestasFecha.take(2).forEach { propuesta ->
                    val seleccionada = estadoUi.idPropuestaSeleccionadaCapitan == propuesta.id
                    val disponibles = enfrentamiento.contarDisponiblesEnPropuesta(propuesta.id)
                    val tieneQuorum = enfrentamiento.alcanzaQuorumDisponibles(propuesta.id)

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (seleccionada) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                        border = BorderStroke(
                            width = if (seleccionada) 1.5.dp else 1.dp,
                            color = if (seleccionada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            },
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                alProcesarEvento(EventoUi.SeleccionarPropuestaParaConfirmar(propuesta.id))
                            },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = seleccionada,
                                onClick = {
                                    alProcesarEvento(EventoUi.SeleccionarPropuestaParaConfirmar(propuesta.id))
                                },
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Opción ${propuesta.orden}: ${formatearFechaHoraEspanol(propuesta.fechaHora)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "$disponibles jugadores disponibles",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (tieneQuorum) {
                                        ColoresSynagon.VerdeQuorum
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = if (tieneQuorum) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = estadoUi.pistasConfirmacion,
                onValueChange = { alProcesarEvento(EventoUi.ActualizarPistasConfirmacion(it)) },
                label = { Text("Instalación y pistas asignadas") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { alProcesarEvento(EventoUi.ConfirmarFechaDefinitiva) },
                enabled = !estadoUi.procesandoAccion && estadoUi.idPropuestaSeleccionadaCapitan != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Confirmar fecha definitiva",
                    fontWeight = FontWeight.Bold,
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

            // 2. Selector interactivo de 6 convocados y distribución en Pareja 1, Pareja 2 y Pareja 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "2. Convocatoria y Parejas (${estadoUi.totalConvocados}/6)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Elige una pareja destino y pulsa sobre los jugadores disponibles para asignarlos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (estadoUi.totalConvocados > 0) {
                    TextButton(
                        onClick = { alProcesarEvento(EventoUi.LimpiarConvocatoria) },
                    ) {
                        Text("Limpiar")
                    }
                }
            }

            // Tarjetas interactivas de Pareja 1, Pareja 2 y Pareja 3
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                estadoUi.parejasBorrador.forEach { borradorPareja ->
                    TarjetaBorradorPareja(
                        borrador = borradorPareja,
                        esParejaActiva = estadoUi.numeroParejaActiva == borradorPareja.numeroPareja,
                        nombreJugadorUno = estadoUi.obtenerNombreJugador(borradorPareja.idJugadorUno),
                        nombreJugadorDos = estadoUi.obtenerNombreJugador(borradorPareja.idJugadorDos),
                        alActivarPareja = {
                            alProcesarEvento(EventoUi.SeleccionarParejaActiva(borradorPareja.numeroPareja))
                        },
                        alQuitarJugador = { posicion ->
                            alProcesarEvento(
                                EventoUi.QuitarJugadorDePareja(
                                    numeroPareja = borradorPareja.numeroPareja,
                                    posicion = posicion,
                                )
                            )
                        },
                    )
                }
            }

            // Selector de jugadores disponibles
            val jugadoresDisponibles = estadoUi.jugadoresDisponiblesConvocatoria
            Text(
                text = "Jugadores disponibles (${jugadoresDisponibles.size}):",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if (jugadoresDisponibles.isEmpty()) {
                Text(
                    text = "Aún no hay jugadores que hayan votado DISPONIBLE en la opción seleccionada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    jugadoresDisponibles.forEach { jugador ->
                        ChipJugadorDisponible(
                            jugador = jugador,
                            parejaAsignada = estadoUi.obtenerNumeroParejaDeJugador(jugador.id),
                            alPulsar = {
                                alProcesarEvento(EventoUi.AlternarConvocatoriaJugador(jugador.id))
                            },
                        )
                    }
                }
            }

            Button(
                onClick = { alProcesarEvento(EventoUi.ConfirmarConvocatoriaCapitan) },
                enabled = !estadoUi.procesandoAccion && estadoUi.convocatoriaCompleta,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColoresSynagon.VerdeQuorumIntenso,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = if (estadoUi.convocatoriaCompleta) {
                        "Confirmar alineación de las 3 parejas"
                    } else {
                        "Completa los 6 convocados (${estadoUi.totalConvocados}/6)"
                    },
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun TarjetaBorradorPareja(
    borrador: BorradorParejaConvocatoria,
    esParejaActiva: Boolean,
    nombreJugadorUno: String?,
    nombreJugadorDos: String?,
    alActivarPareja: () -> Unit,
    alQuitarJugador: (Int) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (esParejaActiva) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        border = BorderStroke(
            width = if (esParejaActiva) 2.dp else 1.dp,
            color = if (borrador.estaCompleta) {
                ColoresSynagon.VerdeQuorumIntenso
            } else if (esParejaActiva) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            },
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { alActivarPareja() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Pareja ${borrador.numeroPareja}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = when {
                        borrador.estaCompleta -> "Completa (2/2)"
                        esParejaActiva -> "Asignando aquí..."
                        else -> "Pulsa para editar"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (borrador.estaCompleta) {
                        ColoresSynagon.VerdeQuorum
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HuecoJugadorPareja(
                    etiquetaVacia = "Plaza 1 libre",
                    nombreJugador = nombreJugadorUno,
                    alQuitar = { alQuitarJugador(1) },
                    modifier = Modifier.weight(1f),
                )
                HuecoJugadorPareja(
                    etiquetaVacia = "Plaza 2 libre",
                    nombreJugador = nombreJugadorDos,
                    alQuitar = { alQuitarJugador(2) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun HuecoJugadorPareja(
    etiquetaVacia: String,
    nombreJugador: String?,
    alQuitar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (nombreJugador != null) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.Transparent
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (nombreJugador != null) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = nombreJugador ?: etiquetaVacia,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (nombreJugador != null) FontWeight.SemiBold else FontWeight.Normal,
                color = if (nombreJugador != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (nombreJugador != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .clickable { alQuitar() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipJugadorDisponible(
    jugador: JugadorEquipo,
    parejaAsignada: Int?,
    alPulsar: () -> Unit,
) {
    val convocado = parejaAsignada != null
    FilterChip(
        selected = convocado,
        onClick = alPulsar,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = jugador.nombreCompleto,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (convocado) FontWeight.Bold else FontWeight.Medium,
                )
                if (parejaAsignada != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(
                            text = "P$parejaAsignada",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        )
                    }
                }
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ColoresSynagon.VerdeQuorumFondo,
            selectedLabelColor = ColoresSynagon.VerdeQuorum,
        ),
    )
}

@Composable
private fun TarjetaResumenAlineacionConfirmada(
    enfrentamiento: Enfrentamiento,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.VerdeQuorumFondo),
        border = BorderStroke(1.dp, ColoresSynagon.VerdeQuorumBorde),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Alineación Oficial Confirmada",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ColoresSynagon.VerdeQuorum,
            )
            enfrentamiento.parejas.sortedBy { it.numeroPareja }.forEach { pareja ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Pareja ${pareja.numeroPareja}:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = ColoresSynagon.VerdeQuorum,
                    )
                    Text(
                        text = pareja.nombreCompletoPareja,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

private fun formatearFechaHoraEspanol(fechaHora: LocalDateTime): String {
    val diaSemana = when (fechaHora.dayOfWeek) {
        DayOfWeek.MONDAY -> "Lun"
        DayOfWeek.TUESDAY -> "Mar"
        DayOfWeek.WEDNESDAY -> "Mié"
        DayOfWeek.THURSDAY -> "Jue"
        DayOfWeek.FRIDAY -> "Vie"
        DayOfWeek.SATURDAY -> "Sáb"
        DayOfWeek.SUNDAY -> "Dom"
    }
    val mes = when (fechaHora.month) {
        Month.JANUARY -> "Ene"
        Month.FEBRUARY -> "Feb"
        Month.MARCH -> "Mar"
        Month.APRIL -> "Abr"
        Month.MAY -> "May"
        Month.JUNE -> "Jun"
        Month.JULY -> "Jul"
        Month.AUGUST -> "Ago"
        Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Oct"
        Month.NOVEMBER -> "Nov"
        Month.DECEMBER -> "Dic"
    }
    val hora = fechaHora.hour.toString().padStart(2, '0')
    val minuto = fechaHora.minute.toString().padStart(2, '0')
    return "$diaSemana ${fechaHora.day} $mes · $hora:$minuto h"
}

@Preview
@Composable
private fun PrevisualizacionContenidoPantallaDetalleJornada() {
    val enfrentamientoDemo = DatosSemillaJornada.crearEnfrentamientoInicial()
    TemaSynagon {
        ContenidoPantallaDetalleJornada(
            estadoUi = EstadoUi(
                cargando = false,
                enfrentamiento = enfrentamientoDemo,
                rolUsuario = RolUsuario.CAPITAN,
                idPropuestaSeleccionadaCapitan = enfrentamientoDemo.propuestasFecha.first().id,
            ),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}
