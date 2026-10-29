package com.synagon.app.presentacion.pantallas.acceso

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.synagon.app.presentacion.componentes.CenefaGriega
import com.synagon.app.presentacion.componentes.IconoCampoCandado
import com.synagon.app.presentacion.componentes.IconoCampoCorreo
import com.synagon.app.presentacion.componentes.IconoCampoPersona
import com.synagon.app.presentacion.componentes.IconoCampoTelefono
import com.synagon.app.presentacion.componentes.IconoMenu
import com.synagon.app.presentacion.componentes.IconoOjoVisibilidad
import com.synagon.app.presentacion.componentes.LogoSynagon
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composable con estado para la pantalla de Login y Registro bajo el sistema "Olimpo Moderno".
 * Recolecta [EstadoAccesoUi] desde [AccesoViewModel] y delega en [ContenidoPantallaAcceso].
 */
@Composable
fun PantallaAcceso(
    alAccederAlPanelPrincipal: () -> Unit,
    alIrASobreSynagon: () -> Unit,
    alIrAPrivacidad: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccesoViewModel = koinViewModel(),
) {
    val estadoUi by viewModel.estadoUi.collectAsStateWithLifecycle()
    val estadoSnackbar = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.efectosUi.collect { efecto ->
            when (efecto) {
                EfectoAccesoUi.NavegarAlPanelPrincipal -> alAccederAlPanelPrincipal()
                EfectoAccesoUi.NavegarASobreSynagon -> alIrASobreSynagon()
                EfectoAccesoUi.NavegarAPrivacidad -> alIrAPrivacidad()
                is EfectoAccesoUi.MostrarMensaje -> estadoSnackbar.showSnackbar(efecto.texto)
            }
        }
    }

    ContenidoPantallaAcceso(
        estadoUi = estadoUi,
        alProcesarEvento = viewModel::procesarEvento,
        estadoSnackbar = estadoSnackbar,
        modifier = modifier,
    )
}

/**
 * Composable completamente stateless (sin ViewModel) que dibuja la experiencia de acceso
 * en Azul Egeo, Pizarra Espartana y Oro Olímpico.
 */
@Composable
fun ContenidoPantallaAcceso(
    estadoUi: EstadoAccesoUi,
    alProcesarEvento: (EventoAccesoUi) -> Unit,
    estadoSnackbar: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ColoresSynagon.AzulEgeo,
        snackbarHost = { SnackbarHost(hostState = estadoSnackbar) },
    ) { rellenoInterno ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColoresSynagon.AzulEgeo)
                .padding(rellenoInterno)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Barra superior: Esquina izquierda con menú [≡] dorado y DropdownMenu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MenuSuperiorOlimpo(
                        desplegado = estadoUi.menuDesplegado,
                        alAlternarMenu = { alProcesarEvento(EventoAccesoUi.AlternarMenuDesplegable) },
                        alCerrarMenu = { alProcesarEvento(EventoAccesoUi.CerrarMenuDesplegable) },
                        alPulsarSobreSynagon = { alProcesarEvento(EventoAccesoUi.IrASobreSynagon) },
                        alPulsarPrivacidad = { alProcesarEvento(EventoAccesoUi.IrAPrivacidad) },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Zona central limitada en anchura para tablets/escritorio manteniendo estética móvil
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LogoSynagon(
                        tamanoEscudo = if (estadoUi.esModoRegistro) 96.dp else 118.dp,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CenefaGriega()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (estadoUi.esModoRegistro) {
                            "REGISTRO DE USUARIO"
                        } else {
                            "ACCESO A LA ARENA"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.5.sp),
                        color = ColoresSynagon.BlancoMarmol,
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Campos exclusivos del modo REGISTRO (Nombre y Teléfono)
                    AnimatedVisibility(visible = estadoUi.esModoRegistro) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            CampoTextoOlimpo(
                                etiqueta = "NOMBRE COMPLETO",
                                valor = estadoUi.nombre,
                                textoGuia = "Ej. Álvaro Iglesias Méndez",
                                mensajeError = estadoUi.errorNombre,
                                alCambiarValor = { alProcesarEvento(EventoAccesoUi.CambiarNombre(it)) },
                                iconoInicial = { IconoCampoPersona() },
                                opcionesTeclado = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next,
                                ),
                            )

                            CampoTextoOlimpo(
                                etiqueta = "TELÉFONO DE CONTACTO",
                                valor = estadoUi.telefono,
                                textoGuia = "Ej. +34 600 123 456",
                                mensajeError = estadoUi.errorTelefono,
                                alCambiarValor = { alProcesarEvento(EventoAccesoUi.CambiarTelefono(it)) },
                                iconoInicial = { IconoCampoTelefono() },
                                opcionesTeclado = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next,
                                ),
                            )

                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }

                    // Campos comunes: Usuario / Correo y Contraseña
                    CampoTextoOlimpo(
                        etiqueta = if (estadoUi.esModoRegistro) {
                            "CORREO ELECTRÓNICO"
                        } else {
                            "USUARIO O CORREO ELECTRÓNICO"
                        },
                        valor = estadoUi.usuario,
                        textoGuia = "capitan@clubpadel.es",
                        mensajeError = estadoUi.errorUsuario,
                        alCambiarValor = { alProcesarEvento(EventoAccesoUi.CambiarUsuario(it)) },
                        iconoInicial = { IconoCampoCorreo() },
                        opcionesTeclado = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoTextoOlimpo(
                        etiqueta = "CONTRASEÑA",
                        valor = estadoUi.contrasena,
                        textoGuia = "Introduce tu clave secreta",
                        mensajeError = estadoUi.errorContrasena,
                        alCambiarValor = { alProcesarEvento(EventoAccesoUi.CambiarContrasena(it)) },
                        iconoInicial = { IconoCampoCandado() },
                        iconoFinal = {
                            IconButton(
                                onClick = { alProcesarEvento(EventoAccesoUi.AlternarVisibilidadContrasena) },
                            ) {
                                IconoOjoVisibilidad(
                                    visible = estadoUi.contrasenaVisible,
                                    color = if (estadoUi.contrasenaVisible) {
                                        ColoresSynagon.OroOlimpico
                                    } else {
                                        ColoresSynagon.GrisPiedraJonica
                                    },
                                )
                            }
                        },
                        transformacionVisual = if (estadoUi.contrasenaVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        opcionesTeclado = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    )

                    // Casilla de aceptación RGPD en modo REGISTRO
                    AnimatedVisibility(visible = estadoUi.esModoRegistro) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            FilaAceptacionPrivacidad(
                                aceptado = estadoUi.aceptarPrivacidad,
                                mensajeError = estadoUi.errorPrivacidad,
                                alCambiarAceptacion = {
                                    alProcesarEvento(EventoAccesoUi.CambiarAceptarPrivacidad(it))
                                },
                                alLeerPolitica = {
                                    alProcesarEvento(EventoAccesoUi.IrAPrivacidad)
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón principal en Oro Olímpico con texto oscuro en Azul Egeo
                    Button(
                        onClick = {
                            if (estadoUi.esModoRegistro) {
                                alProcesarEvento(EventoAccesoUi.Registrarse)
                            } else {
                                alProcesarEvento(EventoAccesoUi.IniciarSesion)
                            }
                        },
                        enabled = !estadoUi.cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresSynagon.OroOlimpico,
                            contentColor = ColoresSynagon.AzulEgeo,
                        ),
                    ) {
                        if (estadoUi.cargando) {
                            CircularProgressIndicator(
                                color = ColoresSynagon.AzulEgeo,
                                strokeWidth = 2.5.dp,
                            )
                        } else {
                            Text(
                                text = if (estadoUi.esModoRegistro) "REGISTRARSE" else "ENTRAR",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp,
                                ),
                                color = ColoresSynagon.AzulEgeo,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón secundario transparente con borde Blanco Mármol
                    OutlinedButton(
                        onClick = {
                            val modoDestino = if (estadoUi.esModoRegistro) {
                                ModoAcceso.LOGIN
                            } else {
                                ModoAcceso.REGISTRO
                            }
                            alProcesarEvento(EventoAccesoUi.CambiarModo(modoDestino))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, ColoresSynagon.BlancoMarmol),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = ColoresSynagon.BlancoMarmol,
                        ),
                    ) {
                        Text(
                            text = if (estadoUi.esModoRegistro) "YA TENGO CUENTA · ENTRAR" else "REGISTRARSE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.8.sp,
                            ),
                            color = ColoresSynagon.BlancoMarmol,
                        )
                    }

                    if (!estadoUi.esModoRegistro) {
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(
                            onClick = {
                                alProcesarEvento(EventoAccesoUi.SolicitarRecuperacionContrasena)
                            },
                        ) {
                            Text(
                                text = "¿Has olvidado tu contraseña?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ColoresSynagon.GrisPiedraJonica,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MenuSuperiorOlimpo(
    desplegado: Boolean,
    alAlternarMenu: () -> Unit,
    alCerrarMenu: () -> Unit,
    alPulsarSobreSynagon: () -> Unit,
    alPulsarPrivacidad: () -> Unit,
) {
    Box {
        IconoMenu(alPulsar = alAlternarMenu)

        DropdownMenu(
            expanded = desplegado,
            onDismissRequest = alCerrarMenu,
            containerColor = ColoresSynagon.PizarraEspartana,
            border = BorderStroke(1.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.55f)),
            shape = RoundedCornerShape(12.dp),
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Sobre Synagon",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                onClick = alPulsarSobreSynagon,
                colors = MenuDefaults.itemColors(
                    textColor = ColoresSynagon.BlancoMarmol,
                ),
            )
            HorizontalDivider(color = ColoresSynagon.BordePiedra)
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Privacidad",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                onClick = alPulsarPrivacidad,
                colors = MenuDefaults.itemColors(
                    textColor = ColoresSynagon.BlancoMarmol,
                ),
            )
        }
    }
}

@Composable
private fun CampoTextoOlimpo(
    etiqueta: String,
    valor: String,
    textoGuia: String,
    mensajeError: String?,
    alCambiarValor: (String) -> Unit,
    iconoInicial: @Composable () -> Unit,
    iconoFinal: (@Composable () -> Unit)? = null,
    transformacionVisual: VisualTransformation = VisualTransformation.None,
    opcionesTeclado: KeyboardOptions = KeyboardOptions.Default,
) {
    val formaCampo = RoundedCornerShape(14.dp)
    val colorBorde = if (mensajeError != null) {
        ColoresSynagon.RojoTerracota
    } else {
        ColoresSynagon.BordePiedra
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
            ),
            color = ColoresSynagon.GrisPiedraJonica,
        )

        TextField(
            value = valor,
            onValueChange = alCambiarValor,
            singleLine = true,
            isError = mensajeError != null,
            placeholder = {
                Text(
                    text = textoGuia,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColoresSynagon.GrisPiedraJonica.copy(alpha = 0.6f),
                )
            },
            leadingIcon = iconoInicial,
            trailingIcon = iconoFinal,
            visualTransformation = transformacionVisual,
            keyboardOptions = opcionesTeclado,
            shape = formaCampo,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ColoresSynagon.PizarraEspartana,
                unfocusedContainerColor = ColoresSynagon.PizarraEspartana,
                errorContainerColor = ColoresSynagon.PizarraEspartana,
                focusedTextColor = ColoresSynagon.BlancoMarmol,
                unfocusedTextColor = ColoresSynagon.BlancoMarmol,
                errorTextColor = ColoresSynagon.BlancoMarmol,
                cursorColor = ColoresSynagon.OroOlimpico,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(formaCampo)
                .border(width = 1.dp, color = colorBorde, shape = formaCampo),
        )

        if (mensajeError != null) {
            Text(
                text = mensajeError,
                style = MaterialTheme.typography.bodySmall,
                color = ColoresSynagon.RojoTerracota,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun FilaAceptacionPrivacidad(
    aceptado: Boolean,
    mensajeError: String?,
    alCambiarAceptacion: (Boolean) -> Unit,
    alLeerPolitica: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = aceptado,
                onCheckedChange = alCambiarAceptacion,
                colors = CheckboxDefaults.colors(
                    checkedColor = ColoresSynagon.OroOlimpico,
                    uncheckedColor = if (mensajeError != null) {
                        ColoresSynagon.RojoTerracota
                    } else {
                        ColoresSynagon.OroOlimpico
                    },
                    checkmarkColor = ColoresSynagon.AzulEgeo,
                ),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "He leído y acepto la Política de Privacidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColoresSynagon.BlancoMarmol,
                    modifier = Modifier.clickable { alCambiarAceptacion(!aceptado) },
                )
                Text(
                    text = "Consultar cláusula RGPD / LOPDGDD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = ColoresSynagon.OroOlimpico,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clickable { alLeerPolitica() },
                )
            }
        }

        if (mensajeError != null) {
            Text(
                text = mensajeError,
                style = MaterialTheme.typography.bodySmall,
                color = ColoresSynagon.RojoTerracota,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PrevisualizacionPantallaAccesoLogin() {
    TemaSynagon {
        ContenidoPantallaAcceso(
            estadoUi = EstadoAccesoUi(modo = ModoAcceso.LOGIN),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}

@Preview
@Composable
private fun PrevisualizacionPantallaAccesoRegistro() {
    TemaSynagon {
        ContenidoPantallaAcceso(
            estadoUi = EstadoAccesoUi(modo = ModoAcceso.REGISTRO),
            alProcesarEvento = {},
            estadoSnackbar = remember { SnackbarHostState() },
        )
    }
}
