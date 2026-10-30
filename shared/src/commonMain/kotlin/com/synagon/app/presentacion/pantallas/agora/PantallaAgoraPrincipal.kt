package com.synagon.app.presentacion.pantallas.agora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.modelos.RolMembresia
import com.synagon.app.presentacion.componentes.CenefaGriega
import com.synagon.app.presentacion.componentes.IconoCampoCandado
import com.synagon.app.presentacion.componentes.IconoCampoCorreo
import com.synagon.app.presentacion.componentes.IconoCampoPersona
import com.synagon.app.presentacion.componentes.IconoCampoTelefono
import com.synagon.app.presentacion.componentes.IconoCoronaOlimpo
import com.synagon.app.presentacion.componentes.IconoEscudoEspartano
import com.synagon.app.presentacion.componentes.IconoFlechaDerechaOlimpo
import com.synagon.app.presentacion.componentes.IconoOjoVisibilidad
import com.synagon.app.presentacion.componentes.IconoPinUbicacion
import com.synagon.app.presentacion.componentes.IconoPuertaSalida
import com.synagon.app.presentacion.componentes.IconoTrofeoOlimpo
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla Principal con estado (Ágora de Synagon) tras el inicio de sesión.
 * Recolecta [EstadoAgoraUi] desde [AgoraViewModel] y delega en [ContenidoPantallaAgoraPrincipal].
 */
@Composable
fun PantallaAgoraPrincipal(
    alNavegarAGestionEquipoOLiga: (ligaId: String, equipoId: String?, rol: RolMembresia) -> Unit,
    alCerrarSesionYNavegarALogin: () -> Unit,
    modificador: Modifier = Modifier,
    viewModel: AgoraViewModel = koinViewModel(),
) {
    val estadoUi by viewModel.estadoUi.collectAsStateWithLifecycle()
    val estadoSnackbar = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.efectosUi.collect { efecto ->
            when (efecto) {
                is EfectoAgoraUi.NavegarAGestionEquipoOLiga -> {
                    alNavegarAGestionEquipoOLiga(
                        efecto.ligaId,
                        efecto.equipoId,
                        efecto.rol,
                    )
                }
                EfectoAgoraUi.NavegarALogin -> alCerrarSesionYNavegarALogin()
                is EfectoAgoraUi.MostrarMensaje -> estadoSnackbar.showSnackbar(efecto.texto)
            }
        }
    }

    ContenidoPantallaAgoraPrincipal(
        estadoUi = estadoUi,
        alProcesarEvento = viewModel::procesarEvento,
        estadoSnackbar = estadoSnackbar,
        modificador = modificador,
    )
}

/**
 * Composable completamente stateless (sin ViewModel) que renderiza el Ágora de Synagon
 * siguiendo fielmente el sistema de diseño "Olimpo Moderno".
 */
@Composable
fun ContenidoPantallaAgoraPrincipal(
    estadoUi: EstadoAgoraUi,
    alProcesarEvento: (EventoAgoraUi) -> Unit,
    estadoSnackbar: SnackbarHostState,
    modificador: Modifier = Modifier,
) {
    Scaffold(
        modifier = modificador.fillMaxSize(),
        containerColor = ColoresSynagon.AzulEgeo,
        snackbarHost = { SnackbarHost(hostState = estadoSnackbar) },
        topBar = {
            CabeceraSuperiorAgora(
                alPulsarSalir = { alProcesarEvento(EventoAgoraUi.SolicitarCerrarSesion) },
                alPulsarPerfil = { alProcesarEvento(EventoAgoraUi.AbrirPerfil) },
            )
        },
    ) { rellenoInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColoresSynagon.AzulEgeo)
                .padding(rellenoInterno)
                .navigationBarsPadding(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 28.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Sección 1: ACCIONES DE COMPETICIÓN
                item(key = "seccion_acciones_competicion") {
                    SeccionAccionesCompeticion(
                        alPulsarCrearLiga = { alProcesarEvento(EventoAgoraUi.PulsarCrearLiga) },
                        alPulsarUnirEquipo = { alProcesarEvento(EventoAgoraUi.PulsarUnirEquipo) },
                    )
                }

                // Encabezado Sección 2: MIS LIGAS Y EQUIPOS ACTIVOS (N)
                item(key = "cabecera_membresias") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "MIS LIGAS Y EQUIPOS ACTIVOS (${estadoUi.listaMembresias.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp,
                            color = ColoresSynagon.GrisPiedraJonica,
                        )
                        if (estadoUi.cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = ColoresSynagon.OroOlimpico,
                            )
                        }
                    }
                }

                if (estadoUi.listaMembresias.isEmpty() && !estadoUi.cargando) {
                    item(key = "estado_vacio_membresias") {
                        EstadoVacioMembresiasAgora()
                    }
                } else {
                    items(
                        items = estadoUi.listaMembresias,
                        key = { "${it.ligaId}_${it.equipoId ?: "presidencia"}_${it.rol.name}" },
                    ) { membresia ->
                        TarjetaMembresiaActiva(
                            membresia = membresia,
                            alSeleccionar = {
                                alProcesarEvento(EventoAgoraUi.SeleccionarTarjetaMembresia(membresia))
                            },
                        )
                    }
                }
            }
        }
    }

    // Modal interactivo 1: Unir Equipo a Liga (Paso 1 validar código -> Paso 2 datos del equipo)
    if (estadoUi.mostrarModalUnirEquipo) {
        ModalUnirEquipoALiga(
            estadoUi = estadoUi,
            alProcesarEvento = alProcesarEvento,
        )
    }

    // Modal interactivo 2: Primer Acceso (Cambio opcional de contraseña inicial DNI)
    if (estadoUi.mostrarModalPrimerAccesoPassword) {
        ModalPrimerAccesoPassword(
            estadoUi = estadoUi,
            alProcesarEvento = alProcesarEvento,
        )
    }

    // Modal interactivo 3: Crear Liga (Rol Presidente)
    if (estadoUi.mostrarModalCrearLiga) {
        ModalCrearLigaPresidente(
            estadoUi = estadoUi,
            alProcesarEvento = alProcesarEvento,
        )
    }

    // Modal interactivo 4: Perfil Personal
    if (estadoUi.mostrarModalPerfil) {
        ModalPerfilUsuarioAgora(
            estadoUi = estadoUi,
            alProcesarEvento = alProcesarEvento,
        )
    }

    // Diálogo de confirmación de cierre de sesión
    if (estadoUi.mostrarDialogoCerrarSesion) {
        DialogoConfirmarCerrarSesion(
            alConfirmar = { alProcesarEvento(EventoAgoraUi.ConfirmarCerrarSesion) },
            alCancelar = { alProcesarEvento(EventoAgoraUi.CancelarCerrarSesion) },
        )
    }
}

/**
 * Cabecera superior "Olimpo Moderno" sobre Azul Noche Egeo (`#0B132B`) con botón `[Salir]`,
 * emblema tipográfico central `SYNAGON` / `COMPETIR JUNTOS`, botón `[Perfil]` y `CenefaGriega()` inferior.
 */
@Composable
private fun CabeceraSuperiorAgora(
    alPulsarSalir: () -> Unit,
    alPulsarPerfil: () -> Unit,
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Izquierda: Botón estilizado [Salir] con puerta dorada y borde sutil
            BotonCabeceraAgora(
                texto = "Salir",
                alPulsar = alPulsarSalir,
                icono = {
                    IconoPuertaSalida(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 16.dp,
                    )
                },
            )

            // Centro: Título SYNAGON Serif blanca (letterSpacing = 4.sp) + COMPETIR JUNTOS dorado
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            ) {
                Text(
                    text = "SYNAGON",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 4.sp,
                    color = ColoresSynagon.BlancoMarmol,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "COMPETIR JUNTOS",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    letterSpacing = 2.4.sp,
                    color = ColoresSynagon.OroOlimpico,
                    maxLines = 1,
                )
            }

            // Derecha: Botón estilizado [Perfil] con icono de usuario en dorado
            BotonCabeceraAgora(
                texto = "Perfil",
                alPulsar = alPulsarPerfil,
                icono = {
                    IconoCampoPersona(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 16.dp,
                    )
                },
            )
        }

        // Justo debajo de la cabecera: CenefaGriega() de lado a lado
        CenefaGriega(
            modificador = Modifier.fillMaxWidth(),
            color = ColoresSynagon.OroOlimpico,
            alto = 5.dp,
        )
    }
}

@Composable
private fun BotonCabeceraAgora(
    texto: String,
    alPulsar: () -> Unit,
    icono: @Composable () -> Unit,
) {
    Surface(
        onClick = alPulsar,
        shape = RoundedCornerShape(10.dp),
        color = ColoresSynagon.PizarraEspartana,
        border = BorderStroke(1.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.42f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            icono()
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ColoresSynagon.OroOlimpico,
                letterSpacing = 0.5.sp,
            )
        }
    }
}

/**
 * Sección 1: `"ACCIONES DE COMPETICIÓN"` con las dos grandes tarjetas cuadradas al 50% de ancho
 * y `125.dp` de altura para Crear Liga y Unir Equipo a Liga.
 */
@Composable
private fun SeccionAccionesCompeticion(
    alPulsarCrearLiga: () -> Unit,
    alPulsarUnirEquipo: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "ACCIONES DE COMPETICIÓN",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp,
            color = ColoresSynagon.GrisPiedraJonica,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 1. Tarjeta Izquierda: CREAR LIGA (fondo sólido dorado #D4AF37, textos e icono en #0B132B)
            Surface(
                onClick = alPulsarCrearLiga,
                modifier = Modifier
                    .weight(1f)
                    .height(125.dp),
                shape = RoundedCornerShape(16.dp),
                color = ColoresSynagon.OroOlimpico,
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    IconoCoronaOlimpo(
                        color = ColoresSynagon.AzulEgeo,
                        tamano = 30.dp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "CREAR LIGA",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = ColoresSynagon.AzulEgeo,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "(Rol Presidente)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ColoresSynagon.AzulEgeo.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }

            // 2. Tarjeta Derecha: UNIR EQUIPO A LIGA (fondo Pizarra Espartana #1C2541, borde 1.5.dp #D4AF37)
            Surface(
                onClick = alPulsarUnirEquipo,
                modifier = Modifier
                    .weight(1f)
                    .height(125.dp),
                shape = RoundedCornerShape(16.dp),
                color = ColoresSynagon.PizarraEspartana,
                border = BorderStroke(1.5.dp, ColoresSynagon.OroOlimpico),
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    IconoEscudoEspartano(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 30.dp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "UNIR EQUIPO A LIGA",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        color = ColoresSynagon.BlancoMarmol,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "(Código Invitación)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColoresSynagon.GrisPiedraJonica,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de membresía activa en color Pizarra Espartana (`#1C2541`), esquinas redondeadas (`16.dp`)
 * y borde lateral izquierdo acentuado en Oro Olímpico (`#D4AF37`).
 */
@Composable
private fun TarjetaMembresiaActiva(
    membresia: ResumenMembresiaUsuario,
    alSeleccionar: () -> Unit,
) {
    val formaTarjeta = RoundedCornerShape(16.dp)

    Surface(
        onClick = alSeleccionar,
        modifier = Modifier
            .fillMaxWidth()
            .clip(formaTarjeta)
            .drawWithContent {
                drawContent()
                // Borde lateral izquierdo acentuado en dorado #D4AF37
                val anchoAcento = 5.dp.toPx()
                drawRect(
                    color = ColoresSynagon.OroOlimpico,
                    topLeft = Offset.Zero,
                    size = Size(width = anchoAcento, height = size.height),
                )
            },
        shape = formaTarjeta,
        color = ColoresSynagon.PizarraEspartana,
        border = BorderStroke(1.dp, ColoresSynagon.BordePiedra.copy(alpha = 0.55f)),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 14.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Línea 1: Icono de trofeo dorado + nombreLiga en mayúsculas y color #D4AF37
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    IconoTrofeoOlimpo(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 15.dp,
                    )
                    Text(
                        text = membresia.nombreLiga.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = ColoresSynagon.OroOlimpico,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Línea 2: nombreEquipoOSubtitulo en blanco #F8F9FA, tamaño 17.sp y FontWeight.Bold
                Text(
                    text = membresia.nombreEquipoOSubtitulo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColoresSynagon.BlancoMarmol,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Línea 3: Icono de pin de ubicación + textoSedeDetalle en color #A3B1C6
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    IconoPinUbicacion(
                        color = ColoresSynagon.GrisPiedraJonica,
                        tamano = 14.dp,
                    )
                    Text(
                        text = membresia.textoSedeDetalle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ColoresSynagon.GrisPiedraJonica,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Línea 4: Badge (pastilla redondeada) según el rol
                Spacer(modifier = Modifier.height(2.dp))
                InsigniaRolMembresia(rol = membresia.rol)
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Derecha de la tarjeta: icono de flecha > en dorado #D4AF37
            IconoFlechaDerechaOlimpo(
                color = ColoresSynagon.OroOlimpico,
                tamano = 22.dp,
            )
        }
    }
}

/**
 * Pastilla redondeada (`Badge`) con el esquema exacto de color según [RolMembresia].
 */
@Composable
private fun InsigniaRolMembresia(rol: RolMembresia) {
    val formaPastilla = RoundedCornerShape(50)

    when (rol) {
        RolMembresia.CAPITAN -> {
            Box(
                modifier = Modifier
                    .clip(formaPastilla)
                    .background(ColoresSynagon.OroOlimpico.copy(alpha = 0.20f))
                    .border(1.dp, ColoresSynagon.OroOlimpico, formaPastilla)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "CAPITÁN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ColoresSynagon.OroOlimpico,
                )
            }
        }

        RolMembresia.PRESIDENTE -> {
            Box(
                modifier = Modifier
                    .clip(formaPastilla)
                    .background(ColoresSynagon.VerdeLaurel)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "PRESIDENTE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ColoresSynagon.BlancoMarmol,
                )
            }
        }

        RolMembresia.JUGADOR -> {
            Box(
                modifier = Modifier
                    .clip(formaPastilla)
                    .background(ColoresSynagon.GrisPiedraJonica.copy(alpha = 0.25f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "JUGADOR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ColoresSynagon.BlancoMarmol,
                )
            }
        }
    }
}

@Composable
private fun EstadoVacioMembresiasAgora() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColoresSynagon.PizarraEspartana,
        border = BorderStroke(1.dp, ColoresSynagon.BordePiedra),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconoTrofeoOlimpo(
                color = ColoresSynagon.OroOlimpico,
                tamano = 28.dp,
            )
            Text(
                text = "Aún no participas en ninguna liga activa",
                style = MaterialTheme.typography.titleMedium,
                color = ColoresSynagon.BlancoMarmol,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Crea una competición como Presidente o introduce el código de invitación de una liga para inscribir tu equipo.",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresSynagon.GrisPiedraJonica,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Modal interactivo en dos pasos para "Unir Equipo a Liga":
 * - Paso 1: Código de invitación del presidente y botón "Validar Código".
 * - Paso 2: Muestra la liga validada, pide "Nombre de tu Equipo" y, si `requiereSedeEquipo`,
 *   el campo "Club / Sede Oficial Local".
 */
@Composable
private fun ModalUnirEquipoALiga(
    estadoUi: EstadoAgoraUi,
    alProcesarEvento: (EventoAgoraUi) -> Unit,
) {
    val configuracionLiga = estadoUi.configuracionLigaValidada

    Dialog(
        onDismissRequest = { alProcesarEvento(EventoAgoraUi.CerrarModalUnirEquipo) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = ColoresSynagon.PizarraEspartana,
            border = BorderStroke(1.5.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.7f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconoEscudoEspartano(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 24.dp,
                    )
                    Column {
                        Text(
                            text = "UNIR EQUIPO A LIGA",
                            style = MaterialTheme.typography.titleMedium,
                            color = ColoresSynagon.OroOlimpico,
                        )
                        Text(
                            text = if (configuracionLiga == null) {
                                "Paso 1 de 2 · Validación de código oficial"
                            } else {
                                "Paso 2 de 2 · Inscripción como Capitán"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = ColoresSynagon.GrisPiedraJonica,
                        )
                    }
                }

                CenefaGriega(alto = 3.dp)

                if (configuracionLiga == null) {
                    // PASO 1: Introducir y validar código de invitación del presidente
                    Text(
                        text = "Introduce el código de invitación proporcionado por el Presidente de la liga para verificar la competición.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColoresSynagon.BlancoMarmol,
                    )

                    OutlinedTextField(
                        value = estadoUi.codigoInvitacionInput,
                        onValueChange = { alProcesarEvento(EventoAgoraUi.CambiarCodigoInvitacion(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Código de Invitación del Presidente") },
                        placeholder = { Text("Ej: OLIMPO-2026") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done,
                        ),
                        colors = coloresCampoModalAgora(),
                    )

                    if (estadoUi.errorModalUnirEquipo != null) {
                        Text(
                            text = estadoUi.errorModalUnirEquipo,
                            style = MaterialTheme.typography.bodySmall,
                            color = ColoresSynagon.RojoTerracota,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = { alProcesarEvento(EventoAgoraUi.CerrarModalUnirEquipo) },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, ColoresSynagon.GrisPiedraJonica.copy(alpha = 0.5f)),
                        ) {
                            Text("CANCELAR", color = ColoresSynagon.GrisPiedraJonica)
                        }

                        Button(
                            onClick = { alProcesarEvento(EventoAgoraUi.ValidarCodigoInvitacion) },
                            enabled = !estadoUi.validandoCodigoInvitacion,
                            modifier = Modifier.weight(1.3f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColoresSynagon.OroOlimpico,
                                contentColor = ColoresSynagon.AzulEgeo,
                            ),
                        ) {
                            if (estadoUi.validandoCodigoInvitacion) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = ColoresSynagon.AzulEgeo,
                                )
                            } else {
                                Text(
                                    text = "VALIDAR CÓDIGO",
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }
                    }
                } else {
                    // PASO 2: Datos del nuevo equipo tras validar la liga
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = ColoresSynagon.AzulEgeo,
                        border = BorderStroke(1.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.45f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "LIGA VERIFICADA",
                                style = MaterialTheme.typography.labelSmall,
                                color = ColoresSynagon.OroOlimpico,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = configuracionLiga.nombreLiga,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ColoresSynagon.BlancoMarmol,
                            )
                            Text(
                                text = "${configuracionLiga.partidosPorJornada} partidos por jornada · Rol asignado: CAPITÁN",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresSynagon.GrisPiedraJonica,
                            )
                        }
                    }

                    OutlinedTextField(
                        value = estadoUi.nombreNuevoEquipoInput,
                        onValueChange = { alProcesarEvento(EventoAgoraUi.CambiarNombreNuevoEquipo(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre de tu Equipo") },
                        placeholder = { Text("Ej: Real Club Pádel Triana") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = if (configuracionLiga.requiereSedeEquipo) ImeAction.Next else ImeAction.Done,
                        ),
                        colors = coloresCampoModalAgora(),
                    )

                    if (configuracionLiga.requiereSedeEquipo) {
                        OutlinedTextField(
                            value = estadoUi.sedeNuevoEquipoInput,
                            onValueChange = { alProcesarEvento(EventoAgoraUi.CambiarSedeNuevoEquipo(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Club / Sede Oficial Local") },
                            placeholder = { Text("Ej: Pistas Centrales Pádel Triana") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done,
                            ),
                            colors = coloresCampoModalAgora(),
                        )
                    }

                    if (estadoUi.errorModalUnirEquipo != null) {
                        Text(
                            text = estadoUi.errorModalUnirEquipo,
                            style = MaterialTheme.typography.bodySmall,
                            color = ColoresSynagon.RojoTerracota,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = { alProcesarEvento(EventoAgoraUi.VolverAPasoCodigoInvitacion) },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, ColoresSynagon.GrisPiedraJonica.copy(alpha = 0.5f)),
                        ) {
                            Text("ATRÁS", color = ColoresSynagon.GrisPiedraJonica)
                        }

                        Button(
                            onClick = { alProcesarEvento(EventoAgoraUi.ConfirmarInscripcionEquipo) },
                            enabled = !estadoUi.inscribiendoEquipo,
                            modifier = Modifier.weight(1.4f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColoresSynagon.OroOlimpico,
                                contentColor = ColoresSynagon.AzulEgeo,
                            ),
                        ) {
                            if (estadoUi.inscribiendoEquipo) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = ColoresSynagon.AzulEgeo,
                                )
                            } else {
                                Text(
                                    text = "INSCRIBIR EQUIPO",
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modal automático de Primer Acceso cuando `requiereCambioPassword == true` (p. ej. acceso inicial con DNI):
 * pregunta *"¿Deseas cambiar tu contraseña inicial (DNI) por una personal?"* con los botones
 * *"SÍ, CAMBIAR CONTRASEÑA"* (despliega input de nueva contraseña) y *"NO, CONTINUAR AL EQUIPO"*.
 */
@Composable
private fun ModalPrimerAccesoPassword(
    estadoUi: EstadoAgoraUi,
    alProcesarEvento: (EventoAgoraUi) -> Unit,
) {
    Dialog(
        onDismissRequest = { alProcesarEvento(EventoAgoraUi.RechazarCambioPasswordInicial) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = ColoresSynagon.PizarraEspartana,
            border = BorderStroke(1.5.dp, ColoresSynagon.OroOlimpico),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconoCampoCandado(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 22.dp,
                    )
                    Text(
                        text = "PRIMER ACCESO A SYNAGON",
                        style = MaterialTheme.typography.titleMedium,
                        color = ColoresSynagon.OroOlimpico,
                    )
                }

                CenefaGriega(alto = 3.dp)

                Text(
                    text = "¿Deseas cambiar tu contraseña inicial (DNI) por una personal?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ColoresSynagon.BlancoMarmol,
                )

                Text(
                    text = "Tu capitán te dio de alta utilizando tu documento de identidad como clave provisional. Puedes personalizarla ahora o mantenerla y acceder directamente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColoresSynagon.GrisPiedraJonica,
                )

                AnimatedVisibility(visible = estadoUi.desplegarInputNuevaPassword) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = estadoUi.nuevaPasswordInicialInput,
                            onValueChange = {
                                alProcesarEvento(EventoAgoraUi.CambiarNuevaPasswordInicial(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nueva contraseña personal") },
                            placeholder = { Text("Mínimo 6 caracteres") },
                            singleLine = true,
                            visualTransformation = if (estadoUi.passwordInicialVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        alProcesarEvento(EventoAgoraUi.AlternarVisibilidadPasswordInicial)
                                    },
                                ) {
                                    IconoOjoVisibilidad(
                                        visible = estadoUi.passwordInicialVisible,
                                        color = ColoresSynagon.OroOlimpico,
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                            colors = coloresCampoModalAgora(),
                        )

                        if (estadoUi.errorPasswordInicial != null) {
                            Text(
                                text = estadoUi.errorPasswordInicial,
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresSynagon.RojoTerracota,
                            )
                        }

                        Button(
                            onClick = { alProcesarEvento(EventoAgoraUi.ConfirmarCambioPasswordInicial) },
                            enabled = !estadoUi.guardandoPasswordInicial,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColoresSynagon.OroOlimpico,
                                contentColor = ColoresSynagon.AzulEgeo,
                            ),
                        ) {
                            if (estadoUi.guardandoPasswordInicial) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = ColoresSynagon.AzulEgeo,
                                )
                            } else {
                                Text(
                                    text = "GUARDAR NUEVA CONTRASEÑA",
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }
                    }
                }

                if (!estadoUi.desplegarInputNuevaPassword) {
                    Button(
                        onClick = {
                            alProcesarEvento(EventoAgoraUi.SolicitarFormularioCambioPasswordInicial)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresSynagon.OroOlimpico,
                            contentColor = ColoresSynagon.AzulEgeo,
                        ),
                    ) {
                        Text(
                            text = "SÍ, CAMBIAR CONTRASEÑA",
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

                OutlinedButton(
                    onClick = { alProcesarEvento(EventoAgoraUi.RechazarCambioPasswordInicial) },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.55f)),
                ) {
                    Text(
                        text = "NO, CONTINUAR AL EQUIPO",
                        color = ColoresSynagon.BlancoMarmol,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

/**
 * Modal interactivo para fundar una nueva competición oficial desde la acción `"CREAR LIGA"` (Rol Presidente).
 */
@Composable
private fun ModalCrearLigaPresidente(
    estadoUi: EstadoAgoraUi,
    alProcesarEvento: (EventoAgoraUi) -> Unit,
) {
    Dialog(
        onDismissRequest = { alProcesarEvento(EventoAgoraUi.CerrarModalCrearLiga) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = ColoresSynagon.PizarraEspartana,
            border = BorderStroke(1.5.dp, ColoresSynagon.OroOlimpico),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconoCoronaOlimpo(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 24.dp,
                    )
                    Column {
                        Text(
                            text = "CREAR NUEVA LIGA",
                            style = MaterialTheme.typography.titleMedium,
                            color = ColoresSynagon.OroOlimpico,
                        )
                        Text(
                            text = "Quedarás acreditado con rol PRESIDENTE",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColoresSynagon.GrisPiedraJonica,
                        )
                    }
                }

                CenefaGriega(alto = 3.dp)

                OutlinedTextField(
                    value = estadoUi.nombreNuevaLigaInput,
                    onValueChange = { alProcesarEvento(EventoAgoraUi.CambiarNombreNuevaLiga(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre Oficial de la Liga") },
                    placeholder = { Text("Ej: Liga Interclubes Otoño 2026") },
                    singleLine = true,
                    colors = coloresCampoModalAgora(),
                )

                Text(
                    text = "MODALIDAD DE SEDES",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColoresSynagon.OroOlimpico,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = estadoUi.requiereSedeNuevaLiga,
                        onClick = { alProcesarEvento(EventoAgoraUi.CambiarRequiereSedeNuevaLiga(true)) },
                        label = { Text("Sede por Equipo") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColoresSynagon.OroOlimpico.copy(alpha = 0.22f),
                            selectedLabelColor = ColoresSynagon.OroOlimpico,
                        ),
                    )
                    FilterChip(
                        selected = !estadoUi.requiereSedeNuevaLiga,
                        onClick = { alProcesarEvento(EventoAgoraUi.CambiarRequiereSedeNuevaLiga(false)) },
                        label = { Text("Sede Única Central") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColoresSynagon.OroOlimpico.copy(alpha = 0.22f),
                            selectedLabelColor = ColoresSynagon.OroOlimpico,
                        ),
                    )
                }

                if (!estadoUi.requiereSedeNuevaLiga) {
                    OutlinedTextField(
                        value = estadoUi.sedeUnicaNuevaLigaInput,
                        onValueChange = { alProcesarEvento(EventoAgoraUi.CambiarSedeUnicaNuevaLiga(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Club / Instalación Central") },
                        placeholder = { Text("Ej: Centro Deportivo Cartuja Indoor") },
                        singleLine = true,
                        colors = coloresCampoModalAgora(),
                    )
                }

                Text(
                    text = "PARTIDOS POR JORNADA",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColoresSynagon.OroOlimpico,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(2, 3, 4).forEach { cantidad ->
                        FilterChip(
                            selected = estadoUi.partidosPorJornadaNuevaLiga == cantidad,
                            onClick = {
                                alProcesarEvento(EventoAgoraUi.CambiarPartidosPorJornadaNuevaLiga(cantidad))
                            },
                            label = { Text("$cantidad Parejas") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColoresSynagon.OroOlimpico.copy(alpha = 0.22f),
                                selectedLabelColor = ColoresSynagon.OroOlimpico,
                            ),
                        )
                    }
                }

                if (estadoUi.errorModalCrearLiga != null) {
                    Text(
                        text = estadoUi.errorModalCrearLiga,
                        style = MaterialTheme.typography.bodySmall,
                        color = ColoresSynagon.RojoTerracota,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = { alProcesarEvento(EventoAgoraUi.CerrarModalCrearLiga) },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, ColoresSynagon.GrisPiedraJonica.copy(alpha = 0.5f)),
                    ) {
                        Text("CANCELAR", color = ColoresSynagon.GrisPiedraJonica)
                    }

                    Button(
                        onClick = { alProcesarEvento(EventoAgoraUi.ConfirmarCrearLiga) },
                        enabled = !estadoUi.creandoLiga,
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresSynagon.OroOlimpico,
                            contentColor = ColoresSynagon.AzulEgeo,
                        ),
                    ) {
                        Text("FUNDAR LIGA", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

/**
 * Modal para consultar y actualizar los datos personales del usuario desde el botón `[Perfil]`.
 */
@Composable
private fun ModalPerfilUsuarioAgora(
    estadoUi: EstadoAgoraUi,
    alProcesarEvento: (EventoAgoraUi) -> Unit,
) {
    Dialog(
        onDismissRequest = { alProcesarEvento(EventoAgoraUi.CerrarModalPerfil) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = ColoresSynagon.PizarraEspartana,
            border = BorderStroke(1.5.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.7f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconoCampoPersona(
                        color = ColoresSynagon.OroOlimpico,
                        tamano = 22.dp,
                    )
                    Column {
                        Text(
                            text = "PERFIL DE JUGADOR",
                            style = MaterialTheme.typography.titleMedium,
                            color = ColoresSynagon.OroOlimpico,
                        )
                        Text(
                            text = "Ficha personal en el Ágora de Synagon",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColoresSynagon.GrisPiedraJonica,
                        )
                    }
                }

                CenefaGriega(alto = 3.dp)

                // Correo electrónico (identificador de cuenta)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColoresSynagon.AzulEgeo)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconoCampoCorreo(color = ColoresSynagon.OroOlimpico, tamano = 18.dp)
                    Column {
                        Text(
                            text = "CORREO VINCULADO",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColoresSynagon.GrisPiedraJonica,
                        )
                        Text(
                            text = estadoUi.emailUsuario,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ColoresSynagon.BlancoMarmol,
                        )
                    }
                }

                OutlinedTextField(
                    value = estadoUi.nombrePerfilInput,
                    onValueChange = { alProcesarEvento(EventoAgoraUi.CambiarNombrePerfil(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre y Apellidos") },
                    leadingIcon = { IconoCampoPersona(tamano = 18.dp) },
                    singleLine = true,
                    colors = coloresCampoModalAgora(),
                )

                OutlinedTextField(
                    value = estadoUi.telefonoPerfilInput,
                    onValueChange = { alProcesarEvento(EventoAgoraUi.CambiarTelefonoPerfil(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Teléfono de contacto") },
                    leadingIcon = { IconoCampoTelefono(tamano = 18.dp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = coloresCampoModalAgora(),
                )

                if (estadoUi.errorModalPerfil != null) {
                    Text(
                        text = estadoUi.errorModalPerfil,
                        style = MaterialTheme.typography.bodySmall,
                        color = ColoresSynagon.RojoTerracota,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = { alProcesarEvento(EventoAgoraUi.CerrarModalPerfil) },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, ColoresSynagon.GrisPiedraJonica.copy(alpha = 0.5f)),
                    ) {
                        Text("CERRAR", color = ColoresSynagon.GrisPiedraJonica)
                    }

                    Button(
                        onClick = { alProcesarEvento(EventoAgoraUi.GuardarDatosPerfil) },
                        enabled = !estadoUi.guardandoPerfil,
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresSynagon.OroOlimpico,
                            contentColor = ColoresSynagon.AzulEgeo,
                        ),
                    ) {
                        Text("GUARDAR", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

/**
 * Diálogo de confirmación desplegado al pulsar el botón `[Salir]` de la cabecera.
 */
@Composable
private fun DialogoConfirmarCerrarSesion(
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit,
) {
    Dialog(onDismissRequest = alCancelar) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ColoresSynagon.PizarraEspartana,
            border = BorderStroke(1.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.65f)),
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconoPuertaSalida(color = ColoresSynagon.OroOlimpico, tamano = 20.dp)
                    Text(
                        text = "CERRAR SESIÓN",
                        style = MaterialTheme.typography.titleMedium,
                        color = ColoresSynagon.OroOlimpico,
                    )
                }

                HorizontalDivider(color = ColoresSynagon.BordePiedra)

                Text(
                    text = "¿Seguro que deseas abandonar el Ágora de Synagon y volver a la pantalla de acceso?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColoresSynagon.BlancoMarmol,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = alCancelar) {
                        Text("CANCELAR", color = ColoresSynagon.GrisPiedraJonica)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = alConfirmar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresSynagon.OroOlimpico,
                            contentColor = ColoresSynagon.AzulEgeo,
                        ),
                    ) {
                        Text("SALIR", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun coloresCampoModalAgora() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ColoresSynagon.OroOlimpico,
    unfocusedBorderColor = ColoresSynagon.BordePiedra,
    focusedLabelColor = ColoresSynagon.OroOlimpico,
    unfocusedLabelColor = ColoresSynagon.GrisPiedraJonica,
    cursorColor = ColoresSynagon.OroOlimpico,
    focusedTextColor = ColoresSynagon.BlancoMarmol,
    unfocusedTextColor = ColoresSynagon.BlancoMarmol,
    focusedContainerColor = ColoresSynagon.AzulEgeo,
    unfocusedContainerColor = ColoresSynagon.AzulEgeo,
)

@Preview
@Composable
private fun PrevisualizacionPantallaAgoraPrincipal() {
    TemaSynagon {
        ContenidoPantallaAgoraPrincipal(
            estadoUi = EstadoAgoraUi(
                listaMembresias = DatosSemillaAgora.crearMembresiasIniciales(),
            ),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}

@Preview
@Composable
private fun PrevisualizacionModalPrimerAccesoAgora() {
    TemaSynagon {
        ContenidoPantallaAgoraPrincipal(
            estadoUi = EstadoAgoraUi(
                listaMembresias = DatosSemillaAgora.crearMembresiasIniciales(),
                mostrarModalPrimerAccesoPassword = true,
            ),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}
