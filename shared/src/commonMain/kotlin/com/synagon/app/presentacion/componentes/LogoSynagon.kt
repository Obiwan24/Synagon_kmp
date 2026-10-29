package com.synagon.app.presentacion.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Emblema institucional de Synagon bajo la estética "Olimpo Moderno".
 * Dibuja un escudo circular espartano abrazado por una corona de laurel dorada
 * y presidido por una columna dórica clásica en su centro, acompañado por
 * la inscripción "SYNAGON" y el lema "COMPETIR JUNTOS".
 */
@Composable
fun LogoSynagon(
    modificador: Modifier = Modifier,
    tamanoEscudo: Dp = 116.dp,
    mostrarLema: Boolean = true,
) {
    Column(
        modifier = modificador,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(modifier = Modifier.size(tamanoEscudo)) {
            val centro = Offset(size.width / 2f, size.height / 2f)
            val radioMaximo = size.minDimension / 2f
            val colorOro = ColoresSynagon.OroOlimpico
            val colorEscudo = ColoresSynagon.PizarraEspartana
            val colorFondoInterior = ColoresSynagon.AzulEgeo

            // 1. Escudo circular central con doble anillo helénico
            val radioEscudo = radioMaximo * 0.66f
            drawCircle(
                color = colorEscudo,
                radius = radioEscudo,
                center = centro,
            )
            drawCircle(
                color = colorFondoInterior,
                radius = radioEscudo * 0.88f,
                center = centro,
            )
            drawCircle(
                color = colorOro,
                radius = radioEscudo,
                center = centro,
                style = Stroke(width = radioMaximo * 0.032f),
            )
            drawCircle(
                color = colorOro.copy(alpha = 0.55f),
                radius = radioEscudo * 0.86f,
                center = centro,
                style = Stroke(width = radioMaximo * 0.016f),
            )

            // 2. Corona de laurel dorada exterior (ramas izquierda y derecha)
            dibujarCoronaLaurel(
                centro = centro,
                radioCorona = radioMaximo * 0.84f,
                colorOro = colorOro,
            )

            // 3. Columna dórica clásica en el corazón del escudo
            dibujarColumnaDorica(
                centro = centro,
                escala = radioEscudo * 0.62f,
                colorOro = colorOro,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "SYNAGON",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 5.sp,
            ),
            color = ColoresSynagon.BlancoMarmol,
        )
    }
}

private fun DrawScope.dibujarCoronaLaurel(
    centro: Offset,
    radioCorona: Float,
    colorOro: Color,
) {
    val grosorTallo = radioCorona * 0.03f
    val rectArco = Rect(
        left = centro.x - radioCorona,
        top = centro.y - radioCorona,
        right = centro.x + radioCorona,
        bottom = centro.y + radioCorona,
    )

    // Tallos curvos izquierdo y derecho (desde la base hacia la cúspide)
    drawArc(
        color = colorOro,
        startAngle = 98f,
        sweepAngle = 148f,
        useCenter = false,
        topLeft = rectArco.topLeft,
        size = rectArco.size,
        style = Stroke(width = grosorTallo, cap = StrokeCap.Round),
    )
    drawArc(
        color = colorOro,
        startAngle = 82f,
        sweepAngle = -148f,
        useCenter = false,
        topLeft = rectArco.topLeft,
        size = rectArco.size,
        style = Stroke(width = grosorTallo, cap = StrokeCap.Round),
    )

    // Lazo cruzado en la base de la corona
    val baseY = centro.y + radioCorona * 0.98f
    val anchoLazo = radioCorona * 0.16f
    drawLine(
        color = colorOro,
        start = Offset(centro.x - anchoLazo, baseY + anchoLazo * 0.45f),
        end = Offset(centro.x + anchoLazo, baseY - anchoLazo * 0.35f),
        strokeWidth = grosorTallo,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = colorOro,
        start = Offset(centro.x + anchoLazo, baseY + anchoLazo * 0.45f),
        end = Offset(centro.x - anchoLazo, baseY - anchoLazo * 0.35f),
        strokeWidth = grosorTallo,
        cap = StrokeCap.Round,
    )

    // Hojas de laurel almendradas a lo largo de ambas ramas
    val totalParesHojas = 7
    val longitudHoja = radioCorona * 0.21f
    val anchoHoja = longitudHoja * 0.36f

    for (indice in 0 until totalParesHojas) {
        val fraccion = indice.toFloat() / (totalParesHojas - 1).toFloat()

        // Rama izquierda: ángulo desde 112° (abajo-izq) hasta 240° (arriba-izq)
        val anguloIzqGrados = 112f + fraccion * 128f
        val radIzq = (anguloIzqGrados * PI / 180.0).toFloat()
        val baseHojaIzq = Offset(
            x = centro.x + radioCorona * cos(radIzq),
            y = centro.y + radioCorona * sin(radIzq),
        )
        val tangenteIzq = radIzq + (PI / 2.0).toFloat()
        dibujarHojaLaurel(baseHojaIzq, tangenteIzq - 0.38f, longitudHoja, anchoHoja, colorOro)
        if (indice < totalParesHojas - 1) {
            dibujarHojaLaurel(baseHojaIzq, tangenteIzq + 0.38f, longitudHoja * 0.85f, anchoHoja * 0.85f, colorOro)
        }

        // Rama derecha: ángulo simétrico desde 68° (abajo-der) hasta -60° (arriba-der)
        val anguloDerGrados = 68f - fraccion * 128f
        val radDer = (anguloDerGrados * PI / 180.0).toFloat()
        val baseHojaDer = Offset(
            x = centro.x + radioCorona * cos(radDer),
            y = centro.y + radioCorona * sin(radDer),
        )
        val tangenteDer = radDer - (PI / 2.0).toFloat()
        dibujarHojaLaurel(baseHojaDer, tangenteDer + 0.38f, longitudHoja, anchoHoja, colorOro)
        if (indice < totalParesHojas - 1) {
            dibujarHojaLaurel(baseHojaDer, tangenteDer - 0.38f, longitudHoja * 0.85f, anchoHoja * 0.85f, colorOro)
        }
    }
}

private fun DrawScope.dibujarHojaLaurel(
    origen: Offset,
    anguloDireccionRad: Float,
    longitud: Float,
    semiancho: Float,
    color: Color,
) {
    val dirX = cos(anguloDireccionRad)
    val dirY = sin(anguloDireccionRad)
    val perpX = -dirY
    val perpY = dirX

    val punta = Offset(origen.x + dirX * longitud, origen.y + dirY * longitud)
    val controlUno = Offset(
        x = origen.x + dirX * (longitud * 0.5f) + perpX * semiancho,
        y = origen.y + dirY * (longitud * 0.5f) + perpY * semiancho,
    )
    val controlDos = Offset(
        x = origen.x + dirX * (longitud * 0.5f) - perpX * semiancho,
        y = origen.y + dirY * (longitud * 0.5f) - perpY * semiancho,
    )

    val trazadoHoja = Path().apply {
        moveTo(origen.x, origen.y)
        quadraticTo(controlUno.x, controlUno.y, punta.x, punta.y)
        quadraticTo(controlDos.x, controlDos.y, origen.x, origen.y)
        close()
    }
    drawPath(path = trazadoHoja, color = color)
}

private fun DrawScope.dibujarColumnaDorica(
    centro: Offset,
    escala: Float,
    colorOro: Color,
) {
    val arribaColumna = centro.y - escala * 0.82f
    val abajoColumna = centro.y + escala * 0.82f
    val altoTotal = abajoColumna - arribaColumna

    // 1. Ábaco superior (losa horizontal del capitel dórico)
    val anchoAbaco = escala * 1.04f
    val altoAbaco = altoTotal * 0.10f
    drawRoundRect(
        color = colorOro,
        topLeft = Offset(centro.x - anchoAbaco / 2f, arribaColumna),
        size = Size(anchoAbaco, altoAbaco),
        cornerRadius = CornerRadius(altoAbaco * 0.25f, altoAbaco * 0.25f),
    )

    // 2. Equino trapezoidal bajo el ábaco
    val arribaEquino = arribaColumna + altoAbaco + altoTotal * 0.02f
    val abajoEquino = arribaEquino + altoTotal * 0.11f
    val anchoFusteSuperior = escala * 0.64f
    val trazadoEquino = Path().apply {
        moveTo(centro.x - anchoAbaco * 0.46f, arribaEquino)
        lineTo(centro.x + anchoAbaco * 0.46f, arribaEquino)
        lineTo(centro.x + anchoFusteSuperior / 2f, abajoEquino)
        lineTo(centro.x - anchoFusteSuperior / 2f, abajoEquino)
        close()
    }
    drawPath(path = trazadoEquino, color = colorOro)

    // 3. Basamento escalonado (estilóbato y estereóbato)
    val altoPeldano = altoTotal * 0.08f
    val anchoBaseInferior = escala * 1.12f
    val anchoBaseSuperior = escala * 0.90f
    val yPeldanoInferior = abajoColumna - altoPeldano
    val yPeldanoSuperior = yPeldanoInferior - altoPeldano - altoTotal * 0.02f

    drawRoundRect(
        color = colorOro,
        topLeft = Offset(centro.x - anchoBaseSuperior / 2f, yPeldanoSuperior),
        size = Size(anchoBaseSuperior, altoPeldano),
        cornerRadius = CornerRadius(altoPeldano * 0.2f, altoPeldano * 0.2f),
    )
    drawRoundRect(
        color = colorOro,
        topLeft = Offset(centro.x - anchoBaseInferior / 2f, yPeldanoInferior),
        size = Size(anchoBaseInferior, altoPeldano),
        cornerRadius = CornerRadius(altoPeldano * 0.2f, altoPeldano * 0.2f),
    )

    // 4. Fuste dórico con ligero éntasis y estrías verticales
    val arribaFuste = abajoEquino + altoTotal * 0.03f
    val abajoFuste = yPeldanoSuperior - altoTotal * 0.03f
    val anchoFusteInferior = escala * 0.74f

    val contornoFuste = Path().apply {
        moveTo(centro.x - anchoFusteSuperior / 2f, arribaFuste)
        lineTo(centro.x + anchoFusteSuperior / 2f, arribaFuste)
        lineTo(centro.x + anchoFusteInferior / 2f, abajoFuste)
        lineTo(centro.x - anchoFusteInferior / 2f, abajoFuste)
        close()
    }
    drawPath(
        path = contornoFuste,
        color = colorOro,
        style = Stroke(width = escala * 0.055f),
    )

    // Estrías clásicas del fuste
    val fraccionesEstrias = listOf(-0.24f, 0f, 0.24f)
    fraccionesEstrias.forEach { factor ->
        drawLine(
            color = colorOro,
            start = Offset(centro.x + anchoFusteSuperior * factor, arribaFuste + altoTotal * 0.03f),
            end = Offset(centro.x + anchoFusteInferior * factor, abajoFuste - altoTotal * 0.03f),
            strokeWidth = escala * 0.042f,
            cap = StrokeCap.Round,
        )
    }
}

@Preview
@Composable
private fun PrevisualizacionLogoSynagon() {
    TemaSynagon {
        LogoSynagon()
    }
}
