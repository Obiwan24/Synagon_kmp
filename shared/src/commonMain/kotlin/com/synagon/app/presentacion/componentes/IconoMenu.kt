package com.synagon.app.presentacion.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.synagon.app.presentacion.tema.ColoresSynagon

/**
 * Botón de menú superior `[≡]` de tres líneas en color Oro Olímpico sobre Pizarra Espartana.
 */
@Composable
fun IconoMenu(
    alPulsar: () -> Unit,
    modificador: Modifier = Modifier,
    colorLineas: Color = ColoresSynagon.OroOlimpico,
    descripcionAccesibilidad: String = "Abrir menú de información de Synagon",
) {
    Surface(
        onClick = alPulsar,
        modifier = modificador
            .size(44.dp)
            .semantics {
                contentDescription = descripcionAccesibilidad
                role = Role.Button
            },
        shape = RoundedCornerShape(12.dp),
        color = ColoresSynagon.PizarraEspartana,
        border = BorderStroke(1.dp, colorLineas.copy(alpha = 0.45f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(20.dp)) {
                val grosor = 2.2.dp.toPx()
                val margenX = size.width * 0.12f
                val finX = size.width * 0.88f

                listOf(0.24f, 0.50f, 0.76f).forEach { proporcionY ->
                    val y = size.height * proporcionY
                    drawLine(
                        color = colorLineas,
                        start = Offset(margenX, y),
                        end = Offset(finX, y),
                        strokeWidth = grosor,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

/**
 * Botón dorado de retorno `[←]` utilizado en las pantallas informativas y legales.
 */
@Composable
fun BotonVolverOlimpo(
    alPulsar: () -> Unit,
    modificador: Modifier = Modifier,
    colorFlecha: Color = ColoresSynagon.OroOlimpico,
    descripcionAccesibilidad: String = "Volver atrás",
) {
    Surface(
        onClick = alPulsar,
        modifier = modificador
            .size(42.dp)
            .semantics {
                contentDescription = descripcionAccesibilidad
                role = Role.Button
            },
        shape = RoundedCornerShape(12.dp),
        color = ColoresSynagon.PizarraEspartana,
        border = BorderStroke(1.dp, colorFlecha.copy(alpha = 0.5f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(20.dp)) {
                val grosor = 2.2.dp.toPx()
                val centroY = size.height * 0.5f
                val inicioX = size.width * 0.18f
                val finX = size.width * 0.84f
                val punta = size.width * 0.44f

                drawLine(
                    color = colorFlecha,
                    start = Offset(inicioX, centroY),
                    end = Offset(finX, centroY),
                    strokeWidth = grosor,
                    cap = StrokeCap.Round,
                )
                val trazadoPunta = Path().apply {
                    moveTo(punta, size.height * 0.24f)
                    lineTo(inicioX, centroY)
                    lineTo(punta, size.height * 0.76f)
                }
                drawPath(
                    path = trazadoPunta,
                    color = colorFlecha,
                    style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
    }
}

/**
 * Icono vectorial minimalista de correo / usuario (sobre clásico).
 */
@Composable
fun IconoCampoCorreo(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 20.dp,
) {
    Canvas(modifier = Modifier.size(tamano)) {
        val grosor = 1.8.dp.toPx()
        val margenX = size.width * 0.10f
        val margenY = size.height * 0.20f
        val ancho = size.width * 0.80f
        val alto = size.height * 0.60f

        drawRoundRect(
            color = color,
            topLeft = Offset(margenX, margenY),
            size = Size(ancho, alto),
            cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx()),
            style = Stroke(width = grosor),
        )
        val solapa = Path().apply {
            moveTo(margenX + grosor * 0.4f, margenY + grosor * 0.4f)
            lineTo(size.width * 0.5f, size.height * 0.55f)
            lineTo(margenX + ancho - grosor * 0.4f, margenY + grosor * 0.4f)
        }
        drawPath(
            path = solapa,
            color = color,
            style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/**
 * Icono vectorial minimalista de candado para el campo de contraseña.
 */
@Composable
fun IconoCampoCandado(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 20.dp,
) {
    Canvas(modifier = Modifier.size(tamano)) {
        val grosor = 1.8.dp.toPx()
        val cuerpoIzq = size.width * 0.18f
        val cuerpoTop = size.height * 0.46f
        val cuerpoAncho = size.width * 0.64f
        val cuerpoAlto = size.height * 0.42f

        // Arco superior del candado
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.28f, size.height * 0.14f),
            size = Size(size.width * 0.44f, size.height * 0.56f),
            style = Stroke(width = grosor, cap = StrokeCap.Round),
        )
        // Cuerpo del candado
        drawRoundRect(
            color = color,
            topLeft = Offset(cuerpoIzq, cuerpoTop),
            size = Size(cuerpoAncho, cuerpoAlto),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = grosor),
        )
        // Cerradura central
        drawCircle(
            color = color,
            radius = 1.6.dp.toPx(),
            center = Offset(size.width * 0.5f, cuerpoTop + cuerpoAlto * 0.48f),
        )
    }
}

/**
 * Icono vectorial interactivo de ojo para mostrar u ocultar la contraseña.
 */
@Composable
fun IconoOjoVisibilidad(
    visible: Boolean,
    color: Color = ColoresSynagon.GrisPiedraJonica,
    tamano: Dp = 22.dp,
) {
    Canvas(modifier = Modifier.size(tamano)) {
        val grosor = 1.8.dp.toPx()
        val izq = size.width * 0.10f
        val der = size.width * 0.90f
        val centroY = size.height * 0.50f
        val apertura = size.height * 0.28f

        val contornoOjo = Path().apply {
            moveTo(izq, centroY)
            quadraticTo(size.width * 0.5f, centroY - apertura * 1.4f, der, centroY)
            quadraticTo(size.width * 0.5f, centroY + apertura * 1.4f, izq, centroY)
            close()
        }
        drawPath(
            path = contornoOjo,
            color = color,
            style = Stroke(width = grosor, join = StrokeJoin.Round),
        )
        drawCircle(
            color = color,
            radius = size.minDimension * 0.12f,
            center = Offset(size.width * 0.5f, centroY),
            style = Stroke(width = grosor),
        )

        if (!visible) {
            drawLine(
                color = color,
                start = Offset(size.width * 0.18f, size.height * 0.82f),
                end = Offset(size.width * 0.82f, size.height * 0.18f),
                strokeWidth = grosor,
                cap = StrokeCap.Round,
            )
        }
    }
}

/**
 * Icono vectorial minimalista de perfil de jugador para el campo Nombre.
 */
@Composable
fun IconoCampoPersona(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 20.dp,
) {
    Canvas(modifier = Modifier.size(tamano)) {
        val grosor = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = size.minDimension * 0.18f,
            center = Offset(size.width * 0.5f, size.height * 0.32f),
            style = Stroke(width = grosor),
        )
        val rectHombros = Rect(
            left = size.width * 0.18f,
            top = size.height * 0.56f,
            right = size.width * 0.82f,
            bottom = size.height * 1.12f,
        )
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = rectHombros.topLeft,
            size = rectHombros.size,
            style = Stroke(width = grosor, cap = StrokeCap.Round),
        )
    }
}

/**
 * Icono vectorial minimalista de dispositivo móvil para el campo Teléfono.
 */
@Composable
fun IconoCampoTelefono(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 20.dp,
) {
    Canvas(modifier = Modifier.size(tamano)) {
        val grosor = 1.8.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.26f, size.height * 0.12f),
            size = Size(size.width * 0.48f, size.height * 0.76f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = grosor),
        )
        drawCircle(
            color = color,
            radius = 1.3.dp.toPx(),
            center = Offset(size.width * 0.5f, size.height * 0.74f),
        )
    }
}
