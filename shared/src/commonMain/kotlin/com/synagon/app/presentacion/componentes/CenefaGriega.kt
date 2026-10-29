package com.synagon.app.presentacion.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon

/**
 * Cenefa o greca griega minimalista (`╔═══╦═══╗`) de 4.dp de alto en color Oro Olímpico
 * diseñada para separar secciones con elegancia arquitectónica helénica.
 */
@Composable
fun CenefaGriega(
    modificador: Modifier = Modifier,
    color: Color = ColoresSynagon.OroOlimpico,
    alto: Dp = 4.dp,
) {
    Canvas(
        modifier = modificador
            .fillMaxWidth()
            .height(alto),
    ) {
        val anchoTotal = size.width
        val altoTotal = size.height
        if (anchoTotal <= 0f || altoTotal <= 0f) return@Canvas

        val grosor = (altoTotal * 0.26f).coerceAtLeast(1f)
        val arribaY = grosor / 2f
        val abajoY = altoTotal - grosor / 2f
        val medioY = altoTotal * 0.52f

        // Línea superior continua del entablamento (`╔═══╦═══╗`)
        drawLine(
            color = color,
            start = Offset(0f, arribaY),
            end = Offset(anchoTotal, arribaY),
            strokeWidth = grosor,
            cap = StrokeCap.Square,
        )

        // Remates laterales verticales (`╔` y `╗`)
        drawLine(
            color = color,
            start = Offset(grosor / 2f, arribaY),
            end = Offset(grosor / 2f, abajoY),
            strokeWidth = grosor,
            cap = StrokeCap.Square,
        )
        drawLine(
            color = color,
            start = Offset(anchoTotal - grosor / 2f, arribaY),
            end = Offset(anchoTotal - grosor / 2f, abajoY),
            strokeWidth = grosor,
            cap = StrokeCap.Square,
        )

        // Módulos geométricos de greca (`╦═══`) repetidos uniformemente
        val pasoModulo = (altoTotal * 4.5f).coerceAtLeast(16.dp.toPx())
        val numeroModulos = (anchoTotal / pasoModulo).toInt().coerceAtLeast(2)
        val anchoModulo = anchoTotal / numeroModulos

        for (indice in 0 until numeroModulos) {
            val inicioModuloX = indice * anchoModulo
            val centroModuloX = inicioModuloX + anchoModulo * 0.5f

            // Pilar T central (`╦`)
            drawLine(
                color = color,
                start = Offset(centroModuloX, arribaY),
                end = Offset(centroModuloX, abajoY),
                strokeWidth = grosor,
                cap = StrokeCap.Square,
            )

            // Tramo inferior de meandro clásico a ambos lados del pilar
            val margenMeandro = anchoModulo * 0.18f
            drawLine(
                color = color.copy(alpha = 0.72f),
                start = Offset(inicioModuloX + margenMeandro, abajoY),
                end = Offset(inicioModuloX + anchoModulo - margenMeandro, abajoY),
                strokeWidth = grosor * 0.85f,
                cap = StrokeCap.Square,
            )

            // Sutil línea paralela intermedia (`═`) que evoca la doble moldura jónica
            drawLine(
                color = color.copy(alpha = 0.45f),
                start = Offset(inicioModuloX + grosor, medioY),
                end = Offset(inicioModuloX + anchoModulo - grosor, medioY),
                strokeWidth = grosor * 0.65f,
                cap = StrokeCap.Square,
            )
        }
    }
}

@Preview
@Composable
private fun PrevisualizacionCenefaGriega() {
    TemaSynagon {
        CenefaGriega()
    }
}
