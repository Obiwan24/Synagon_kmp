package com.synagon.app.presentacion.pantallas.acta

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.synagon.app.dominio.modelos.GanadorPartido
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composable con estado para la pantalla y diálogo de subida de acta arbitral.
 * Recolecta [EstadoUi] desde [ActaViewModel] y delega en [ContenidoPantallaSubirActa].
 */
@Composable
fun PantallaSubirActa(
    modifier: Modifier = Modifier,
    viewModel: ActaViewModel = koinViewModel(),
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

    ContenidoPantallaSubirActa(
        estadoUi = estadoUi,
        alProcesarEvento = viewModel::procesarEvento,
        estadoSnackbar = estadoSnackbar,
        modifier = modifier,
    )
}

/**
 * Composable completamente stateless (sin ViewModel) para introducir los sets de las 3 parejas,
 * adjuntar la foto del acta y validar en tiempo real el reglamento de pádel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPantallaSubirActa(
    estadoUi: EstadoUi,
    alProcesarEvento: (EventoUi) -> Unit,
    estadoSnackbar: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    if (estadoUi.mostrarDialogoConfirmacion) {
        DialogoConfirmarSubidaActa(
            estadoUi = estadoUi,
            alConfirmar = { alProcesarEvento(EventoUi.ConfirmarSubidaActa) },
            alCancelar = { alProcesarEvento(EventoUi.CerrarDialogoConfirmacion) },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = estadoSnackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Subida de Acta Arbitral",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Jornada ${estadoUi.numeroJornada} · ${estadoUi.nombreDivision}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { alProcesarEvento(EventoUi.LimpiarFormularioActa) },
                    ) {
                        Text(
                            text = "Limpiar",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(rellenoInterno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (estadoUi.mensajeEstadoConexion != null) {
                item {
                    AvisoEstadoActaConexion(
                        mensaje = estadoUi.mensajeEstadoConexion,
                        alReintentar = { alProcesarEvento(EventoUi.RecargarDatosEnfrentamiento) },
                    )
                }
            }

            if (estadoUi.actaEnviada) {
                item {
                    BannerActaRegistradaExito(
                        victoriasLocal = estadoUi.victoriasLocal,
                        victoriasVisitante = estadoUi.victoriasVisitante,
                        nombreLocal = estadoUi.nombreEquipoLocal,
                        nombreVisitante = estadoUi.nombreEquipoVisitante,
                    )
                }
            }

            item {
                TarjetaMarcadorGlobalEnVivo(estadoUi = estadoUi)
            }

            items(
                items = estadoUi.entradasParejas,
                key = { it.numeroPareja },
            ) { entradaPareja ->
                TarjetaResultadoParejaActa(
                    entrada = entradaPareja,
                    nombreEquipoLocal = estadoUi.nombreEquipoLocal,
                    nombreEquipoVisitante = estadoUi.nombreEquipoVisitante,
                    alCambiarSet = { set, bando, valor ->
                        alProcesarEvento(
                            EventoUi.CambiarJuegosSet(
                                numeroPareja = entradaPareja.numeroPareja,
                                set = set,
                                bando = bando,
                                valor = valor,
                            )
                        )
                    },
                )
            }

            item {
                TarjetaFotoYObservacionesActa(
                    urlFotoActa = estadoUi.urlFotoActa,
                    errorFotoActa = estadoUi.errorFotoActa,
                    observaciones = estadoUi.observaciones,
                    alCambiarUrlFoto = { alProcesarEvento(EventoUi.ActualizarUrlFotoActa(it)) },
                    alAdjuntarFotoCamara = { alProcesarEvento(EventoUi.AdjuntarReferenciaFotoCamara) },
                    alCambiarObservaciones = { alProcesarEvento(EventoUi.ActualizarObservaciones(it)) },
                )
            }

            item {
                Button(
                    onClick = { alProcesarEvento(EventoUi.SolicitarRevisionEnvioActa) },
                    enabled = !estadoUi.enviando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (estadoUi.formularioValido) {
                            ColoresSynagon.VerdeQuorum
                        } else {
                            ColoresSynagon.AzulPrimario
                        },
                    ),
                ) {
                    Text(
                        text = if (estadoUi.enviando) {
                            "Subiendo acta oficial..."
                        } else {
                            "Revisar y Subir Acta (${estadoUi.partidosValidosCompletados}/3 parejas válidas)"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TarjetaMarcadorGlobalEnVivo(
    estadoUi: EstadoUi,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.SuperficieTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "MARCADOR PROYECTADO DEL ENFRENTAMIENTO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColoresSynagon.AzulPrimario,
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (estadoUi.formularioValido) {
                        ColoresSynagon.VerdeQuorumFondo
                    } else {
                        ColoresSynagon.AzulContenedor
                    },
                ) {
                    Text(
                        text = if (estadoUi.formularioValido) "ACTA VÁLIDA" else "${estadoUi.partidosValidosCompletados}/3 PAREJAS",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (estadoUi.formularioValido) {
                            ColoresSynagon.VerdeQuorum
                        } else {
                            ColoresSynagon.SobreAzulContenedor
                        },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = estadoUi.nombreEquipoLocal,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Equipo Local",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColoresSynagon.TextoSecundario,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ColoresSynagon.AzulContenedor,
                    modifier = Modifier.padding(horizontal = 12.dp),
                ) {
                    Text(
                        text = "${estadoUi.victoriasLocal} - ${estadoUi.victoriasVisitante}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColoresSynagon.SobreAzulContenedor,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = estadoUi.nombreEquipoVisitante,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Equipo Visitante",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColoresSynagon.TextoSecundario,
                    )
                }
            }

            Text(
                text = "Reglamento: sets válidos 6-0 a 6-4, 7-5 o 7-6. Con empate 1-1 el 3er set es obligatorio; con 2-0 no se disputa.",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresSynagon.TextoSecundario,
            )
        }
    }
}

@Composable
private fun TarjetaResultadoParejaActa(
    entrada: EntradaParejaActaUi,
    nombreEquipoLocal: String,
    nombreEquipoVisitante: String,
    alCambiarSet: (NumeroSetActa, BandoActa, String) -> Unit,
) {
    val colorBorde = when {
        entrada.tieneErroresVisibles -> ColoresSynagon.RojoBaja
        entrada.validacion.esPartidoValido -> ColoresSynagon.VerdeQuorumBorde
        else -> ColoresSynagon.BordeSuave
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.SuperficieTarjeta),
        border = BorderStroke(1.5.dp, colorBorde),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Pareja ${entrada.numeroPareja}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColoresSynagon.AzulPrimario,
                )
                EtiquetaEstadoPartidoPareja(entrada = entrada)
            }

            Text(
                text = "Local: ${entrada.nombreParejaLocal}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = ColoresSynagon.TextoPrincipal,
            )
            Text(
                text = "Visitante: ${entrada.nombreParejaVisitante}",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresSynagon.TextoSecundario,
            )

            HorizontalDivider(color = ColoresSynagon.BordeSuave.copy(alpha = 0.6f))

            // Cabecera de columnas de Sets
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Bando",
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ColoresSynagon.TextoSecundario,
                )
                NumeroSetActa.entries.forEach { set ->
                    Text(
                        text = set.etiqueta,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = if (set == NumeroSetActa.SET_3 && entrada.validacion.requiereTercerSet) {
                            ColoresSynagon.AmbarDuda
                        } else {
                            ColoresSynagon.TextoSecundario
                        },
                    )
                }
            }

            // Fila Equipo Local
            FilaEntradaJuegosBando(
                etiquetaBando = nombreEquipoLocal,
                valorSet1 = entrada.set1Local,
                valorSet2 = entrada.set2Local,
                valorSet3 = entrada.set3Local,
                errorSet1 = entrada.validacion.errorSet1 != null,
                errorSet2 = entrada.validacion.errorSet2 != null,
                errorSet3 = entrada.validacion.errorSet3 != null || entrada.validacion.requiereTercerSet && entrada.set3Local.isBlank(),
                habilitarSet3 = !entrada.validacion.prohibeTercerSet || entrada.set3Local.isNotBlank() || entrada.set3Visitante.isNotBlank(),
                alCambiarSet1 = { alCambiarSet(NumeroSetActa.SET_1, BandoActa.LOCAL, it) },
                alCambiarSet2 = { alCambiarSet(NumeroSetActa.SET_2, BandoActa.LOCAL, it) },
                alCambiarSet3 = { alCambiarSet(NumeroSetActa.SET_3, BandoActa.LOCAL, it) },
            )

            // Fila Equipo Visitante
            FilaEntradaJuegosBando(
                etiquetaBando = nombreEquipoVisitante,
                valorSet1 = entrada.set1Visitante,
                valorSet2 = entrada.set2Visitante,
                valorSet3 = entrada.set3Visitante,
                errorSet1 = entrada.validacion.errorSet1 != null,
                errorSet2 = entrada.validacion.errorSet2 != null,
                errorSet3 = entrada.validacion.errorSet3 != null || entrada.validacion.requiereTercerSet && entrada.set3Visitante.isBlank(),
                habilitarSet3 = !entrada.validacion.prohibeTercerSet || entrada.set3Local.isNotBlank() || entrada.set3Visitante.isNotBlank(),
                alCambiarSet1 = { alCambiarSet(NumeroSetActa.SET_1, BandoActa.VISITANTE, it) },
                alCambiarSet2 = { alCambiarSet(NumeroSetActa.SET_2, BandoActa.VISITANTE, it) },
                alCambiarSet3 = { alCambiarSet(NumeroSetActa.SET_3, BandoActa.VISITANTE, it) },
            )

            // Mensajes de error de validación en tiempo real
            val mensajesError = listOfNotNull(
                entrada.validacion.errorSet1,
                entrada.validacion.errorSet2,
                entrada.validacion.errorSet3,
                entrada.validacion.errorGeneralPartido,
            )

            if (mensajesError.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = ColoresSynagon.RojoBajaFondo,
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        mensajesError.forEach { mensaje ->
                            Text(
                                text = "• $mensaje",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = ColoresSynagon.RojoBaja,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetaEstadoPartidoPareja(
    entrada: EntradaParejaActaUi,
) {
    val (texto, colorFondo, colorTexto) = when {
        entrada.tieneErroresVisibles -> Triple(
            "Tanteo ilegal",
            ColoresSynagon.RojoBajaFondo,
            ColoresSynagon.RojoBaja,
        )
        entrada.validacion.esPartidoValido -> {
            val textoGanador = when (entrada.validacion.ganador) {
                GanadorPartido.LOCAL -> "Punto Local"
                GanadorPartido.VISITANTE -> "Punto Visitante"
                null -> "Válido"
            }
            Triple(
                textoGanador,
                ColoresSynagon.VerdeQuorumFondo,
                ColoresSynagon.VerdeQuorum,
            )
        }
        else -> Triple(
            "Pendiente",
            ColoresSynagon.AzulContenedor,
            ColoresSynagon.SobreAzulContenedor,
        )
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = colorFondo,
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colorTexto,
        )
    }
}

@Composable
private fun FilaEntradaJuegosBando(
    etiquetaBando: String,
    valorSet1: String,
    valorSet2: String,
    valorSet3: String,
    errorSet1: Boolean,
    errorSet2: Boolean,
    errorSet3: Boolean,
    habilitarSet3: Boolean,
    alCambiarSet1: (String) -> Unit,
    alCambiarSet2: (String) -> Unit,
    alCambiarSet3: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = etiquetaBando,
            modifier = Modifier.weight(1.3f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        CampoJuegoSet(
            valor = valorSet1,
            esError = errorSet1,
            habilitado = true,
            marcador = "0",
            alCambiar = alCambiarSet1,
            modifier = Modifier.weight(1f),
        )
        CampoJuegoSet(
            valor = valorSet2,
            esError = errorSet2,
            habilitado = true,
            marcador = "0",
            alCambiar = alCambiarSet2,
            modifier = Modifier.weight(1f),
        )
        CampoJuegoSet(
            valor = valorSet3,
            esError = errorSet3,
            habilitado = habilitarSet3,
            marcador = "-",
            alCambiar = alCambiarSet3,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CampoJuegoSet(
    valor: String,
    esError: Boolean,
    habilitado: Boolean,
    marcador: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        enabled = habilitado,
        isError = esError,
        singleLine = true,
        placeholder = {
            Text(
                text = marcador,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier,
    )
}

@Composable
private fun TarjetaFotoYObservacionesActa(
    urlFotoActa: String,
    errorFotoActa: String?,
    observaciones: String,
    alCambiarUrlFoto: (String) -> Unit,
    alAdjuntarFotoCamara: () -> Unit,
    alCambiarObservaciones: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.SuperficieTarjeta),
        border = BorderStroke(1.dp, ColoresSynagon.BordeSuave),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Evidencia Fotográfica del Acta Firmada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ColoresSynagon.AzulPrimario,
            )
            Text(
                text = "Adjunta la referencia o URL de la fotografía del acta oficial firmada por ambos capitanes.",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresSynagon.TextoSecundario,
            )

            OutlinedTextField(
                value = urlFotoActa,
                onValueChange = alCambiarUrlFoto,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorFotoActa != null,
                label = { Text("URL o referencia de la foto del acta") },
                placeholder = { Text("https://cdn.synagon.es/actas/acta_jornada5.jpg") },
                supportingText = if (errorFotoActa != null) {
                    {
                        Text(
                            text = errorFotoActa,
                            color = ColoresSynagon.RojoBaja,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(12.dp),
            )

            OutlinedButton(
                onClick = alAdjuntarFotoCamara,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Capturar / Adjuntar Foto Oficial del Acta",
                    fontWeight = FontWeight.SemiBold,
                )
            }

            OutlinedTextField(
                value = observaciones,
                onValueChange = alCambiarObservaciones,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                label = { Text("Observaciones arbitrales (opcional)") },
                placeholder = { Text("Incidencias de pista, sustituciones o conformidad...") },
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}

@Composable
private fun DialogoConfirmarSubidaActa(
    estadoUi: EstadoUi,
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        title = {
            Text(
                text = "Confirmar Subida de Acta Oficial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Resultado final: ${estadoUi.nombreEquipoLocal} ${estadoUi.victoriasLocal} - ${estadoUi.victoriasVisitante} ${estadoUi.nombreEquipoVisitante}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColoresSynagon.AzulPrimario,
                )
                HorizontalDivider()
                estadoUi.entradasParejas.forEach { pareja ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Pareja ${pareja.numeroPareja}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = pareja.resumenMarcador(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                HorizontalDivider()
                Text(
                    text = "Foto adjunta: ${estadoUi.urlFotoActa}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColoresSynagon.TextoSecundario,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = alConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = ColoresSynagon.VerdeQuorum),
            ) {
                Text("Confirmar y Cerrar Acta")
            }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) {
                Text("Seguir editando")
            }
        },
    )
}

@Composable
private fun BannerActaRegistradaExito(
    victoriasLocal: Int,
    victoriasVisitante: Int,
    nombreLocal: String,
    nombreVisitante: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ColoresSynagon.VerdeQuorumFondo,
        border = BorderStroke(1.dp, ColoresSynagon.VerdeQuorumBorde),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "ACTA OFICIAL SUBIDA CORRECTAMENTE",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ColoresSynagon.VerdeQuorum,
            )
            Text(
                text = "$nombreLocal $victoriasLocal - $victoriasVisitante $nombreVisitante",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = ColoresSynagon.TextoPrincipal,
            )
        }
    }
}

@Composable
private fun AvisoEstadoActaConexion(
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

@Preview
@Composable
private fun PrevisualizacionPantallaSubirActa() {
    TemaSynagon {
        ContenidoPantallaSubirActa(
            estadoUi = EstadoUi(cargando = false),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}
