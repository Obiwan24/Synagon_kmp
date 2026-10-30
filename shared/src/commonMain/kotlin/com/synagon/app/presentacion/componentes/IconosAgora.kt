package com.synagon.app.presentacion.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.synagon.app.presentacion.tema.ColoresSynagon

/**
 * Icono vectorial minimalista de puerta con flecha de salida para el botón `[Salir]` del Ágora.
 */
@Composable
fun IconoPuertaSalida(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 18.dp,
    modificador: Modifier = Modifier,
) {
    Canvas(modifier = modificador.size(tamano)) {
        val grosor = 1.8.dp.toPx()
        val w = size.width
        val h = size.height

        // Marco de puerta a la izquierda
        val marco = Path().apply {
            moveTo(w * 0.52f, h * 0.14f)
            lineTo(w * 0.18f, h * 0.14f)
            lineTo(w * 0.18f, h * 0.86f)
            lineTo(w * 0.52f, h * 0.86f)
        }
        drawPath(
            path = marco,
            color = color,
            style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        // Flecha saliendo hacia la derecha
        drawLine(
            color = color,
            start = Offset(w * 0.38f, h * 0.50f),
            end = Offset(w * 0.88f, h * 0.50f),
            strokeWidth = grosor,
            cap = StrokeCap.Round,
        )
        val punta = Path().apply {
            moveTo(w * 0.68f, h * 0.30f)
            lineTo(w * 0.88f, h * 0.50f)
            lineTo(w * 0.68f, h * 0.70f)
        }
        drawPath(
            path = punta,
            color = color,
            style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/**
 * Icono vectorial de corona clásica para la tarjeta de acción `"CREAR LIGA"` (Rol Presidente).
 */
@Composable
fun IconoCoronaOlimpo(
    color: Color = ColoresSynagon.AzulEgeo,
    tamano: Dp = 30.dp,
    modificador: Modifier = Modifier,
) {
    Canvas(modifier = modificador.size(tamano)) {
        val grosor = 2.1.dp.toPx()
        val w = size.width
        val h = size.height

        val corona = Path().apply {
            moveTo(w * 0.14f, h * 0.72f)
            lineTo(w * 0.10f, h * 0.30f)
            lineTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.20f)
            lineTo(w * 0.68f, h * 0.50f)
            lineTo(w * 0.90f, h * 0.30f)
            lineTo(w * 0.86f, h * 0.72f)
            close()
        }
        drawPath(
            path = corona,
            color = color,
            style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        // Base de la corona
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.14f, h * 0.78f),
            size = Size(w * 0.72f, h * 0.09f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        )
        // Joyas superiores en las tres puntas
        drawCircle(color = color, radius = 1.8.dp.toPx(), center = Offset(w * 0.10f, h * 0.24f))
        drawCircle(color = color, radius = 2.0.dp.toPx(), center = Offset(w * 0.50f, h * 0.14f))
        drawCircle(color = color, radius = 1.8.dp.toPx(), center = Offset(w * 0.90f, h * 0.24f))
    }
}

/**
 * Icono vectorial de escudo espartano dorado para la tarjeta `"UNIR EQUIPO A LIGA"`.
 */
@Composable
fun IconoEscudoEspartano(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 30.dp,
    modificador: Modifier = Modifier,
) {
    Canvas(modifier = modificador.size(tamano)) {
        val grosor = 2.0.dp.toPx()
        val w = size.width
        val h = size.height

        val escudo = Path().apply {
            moveTo(w * 0.50f, h * 0.10f)
            lineTo(w * 0.86f, h * 0.22f)
            lineTo(w * 0.86f, h * 0.50f)
            quadraticTo(w * 0.84f, h * 0.78f, w * 0.50f, h * 0.92f)
            quadraticTo(w * 0.16f, h * 0.78f, w * 0.14f, h * 0.50f)
            lineTo(w * 0.14f, h * 0.22f)
            close()
        }
        drawPath(
            path = escudo,
            color = color,
            style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        // Emblema lambda (Λ) espartano en el corazón del escudo
        val lambda = Path().apply {
            moveTo(w * 0.35f, h * 0.64f)
            lineTo(w * 0.50f, h * 0.36f)
            lineTo(w * 0.65f, h * 0.64f)
        }
        drawPath(
            path = lambda,
            color = color,
            style = Stroke(width = grosor * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/**
 * Icono vectorial de trofeo / copa dorada para la línea de liga en las tarjetas de membresía.
 */
@Composable
fun IconoTrofeoOlimpo(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 16.dp,
    modificador: Modifier = Modifier,
) {
    Canvas(modifier = modificador.size(tamano)) {
        val grosor = 1.6.dp.toPx()
        val w = size.width
        val h = size.height

        // Cáliz central
        val caliz = Path().apply {
            moveTo(w * 0.26f, h * 0.14f)
            lineTo(w * 0.74f, h * 0.14f)
            lineTo(w * 0.68f, h * 0.46f)
            quadraticTo(w * 0.50f, h * 0.64f, w * 0.32f, h * 0.46f)
            close()
        }
        drawPath(
            path = caliz,
            color = color,
            style = Stroke(width = grosor, join = StrokeJoin.Round),
        )

        // Asas laterales
        drawArc(
            color = color,
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.10f, h * 0.18f),
            size = Size(w * 0.22f, h * 0.24f),
            style = Stroke(width = grosor, cap = StrokeCap.Round),
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.68f, h * 0.18f),
            size = Size(w * 0.22f, h * 0.24f),
            style = Stroke(width = grosor, cap = StrokeCap.Round),
        )

        // Fuste y pedestal
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.58f),
            end = Offset(w * 0.50f, h * 0.84f),
            strokeWidth = grosor,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.30f, h * 0.86f),
            end = Offset(w * 0.70f, h * 0.86f),
            strokeWidth = grosor,
            cap = StrokeCap.Round,
        )
    }
}

/**
 * Icono vectorial de pin de ubicación para la sede del club en las tarjetas de membresía.
 */
@Composable
fun IconoPinUbicacion(
    color: Color = ColoresSynagon.GrisPiedraJonica,
    tamano: Dp = 15.dp,
    modificador: Modifier = Modifier,
) {
    Canvas(modifier = modificador.size(tamano)) {
        val grosor = 1.5.dp.toPx()
        val w = size.width
        val h = size.height

        val pin = Path().apply {
            moveTo(w * 0.50f, h * 0.90f)
            quadraticTo(w * 0.18f, h * 0.56f, w * 0.20f, h * 0.38f)
            quadraticTo(w * 0.20f, h * 0.12f, w * 0.50f, h * 0.12f)
            quadraticTo(w * 0.80f, h * 0.12f, w * 0.80f, h * 0.38f)
            quadraticTo(w * 0.82f, h * 0.56f, w * 0.50f, h * 0.90f)
            close()
        }
        drawPath(
            path = pin,
            color = color,
            style = Stroke(width = grosor, join = StrokeJoin.Round),
        )
        drawCircle(
            color = color,
            radius = w * 0.11f,
            center = Offset(w * 0.50f, h * 0.38f),
        )
    }
}

/**
 * Icono vectorial de flecha derecha `>` en Oro Olímpico para las tarjetas navegables del Ágora.
 */
@Composable
fun IconoFlechaDerechaOlimpo(
    color: Color = ColoresSynagon.OroOlimpico,
    tamano: Dp = 20.dp,
    modificador: Modifier = Modifier,
) {
    Canvas(modifier = modificador.size(tamano)) {
        val grosor = 2.2.dp.toPx()
        val chevron = Path().apply {
            moveTo(size.width * 0.34f, size.height * 0.20f)
            lineTo(size.width * 0.68f, size.height * 0.50f)
            lineTo(size.width * 0.34f, size.height * 0.80f)
        }
        drawPath(
            path = chevron,
            color = color,
            style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
