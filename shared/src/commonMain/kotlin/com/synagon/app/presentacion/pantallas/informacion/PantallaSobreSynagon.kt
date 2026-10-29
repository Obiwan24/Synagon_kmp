package com.synagon.app.presentacion.pantallas.informacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synagon.app.presentacion.componentes.BotonVolverOlimpo
import com.synagon.app.presentacion.componentes.CenefaGriega
import com.synagon.app.presentacion.componentes.LogoSynagon
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon

/**
 * Pantalla informativa institucional "Sobre Synagon" con estética "Olimpo Moderno".
 */
@Composable
fun PantallaSobreSynagon(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ContenidoPantallaSobreSynagon(
        alVolver = alVolver,
        modifier = modifier,
    )
}

/**
 * Composable stateless de lectura sobre la etimología helénica y misión deportiva de Synagon.
 */
@Composable
fun ContenidoPantallaSobreSynagon(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ColoresSynagon.AzulEgeo,
    ) { rellenoInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColoresSynagon.AzulEgeo)
                .padding(rellenoInterno)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Cabecera con botón dorado de volver [←]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BotonVolverOlimpo(
                    alPulsar = alVolver,
                    descripcionAccesibilidad = "Volver a la pantalla de acceso",
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "SOBRE SYNAGON",
                    style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 3.sp),
                    color = ColoresSynagon.OroOlimpico,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LogoSynagon(tamanoEscudo = 104.dp)

            Spacer(modifier = Modifier.height(20.dp))

            CenefaGriega()

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "«El rival no es un enemigo, sino el compañero indispensable que despierta nuestra excelencia.»",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic,
                ),
                color = ColoresSynagon.OroOlimpico,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Spacer(modifier = Modifier.height(22.dp))

            TarjetaCapituloOlimpo(
                subtitulo = "ETIMOLOGÍA HELÉNICA",
                titulo = "El Significado de Synagōn (συναγών)",
                cuerpo = "El nombre Synagon nace de la unión de dos raíces del griego clásico: " +
                    "«syn-» (σύν), que significa «juntos, en comunión», y «agōn» (ἀγών), que designaba " +
                    "el certamen atlético, la contienda noble y el esfuerzo compartido en los antiguos " +
                    "Juegos Panhelénicos.\n\n" +
                    "Para el espíritu clásico, competir juntos («synagōnízesthai») expresaba tanto la " +
                    "hermandad inquebrantable de quienes defienden un mismo escudo como el respeto " +
                    "sagrado hacia el adversario en la pista.",
            )

            Spacer(modifier = Modifier.height(16.dp))

            TarjetaCapituloOlimpo(
                subtitulo = "NUESTRA MISIÓN DEPORTIVA",
                titulo = "Orden, Justicia y Hermandad de Equipo",
                cuerpo = "En el pádel por equipos moderno, el éxito de un club no se mide solo en los " +
                    "sets ganados el fin de semana, sino en la cohesión diaria de su plantilla.\n\n" +
                    "Synagon moderniza la competición amateur y federada eliminando la incertidumbre " +
                    "logística: coordina votaciones de disponibilidad con quórum transparente, asiste " +
                    "a los capitanes en la alineación de sus tres parejas, verifica en tiempo real el " +
                    "tanteo reglamentario de las actas arbitrales y reconoce tanto las victorias como " +
                    "el compromiso de cada jugador convocado.",
            )

            Spacer(modifier = Modifier.height(16.dp))

            TarjetaCapituloOlimpo(
                subtitulo = "LOS TRES PILARES",
                titulo = "El Código del Olimpo Moderno",
                cuerpo = "• Areté (Excelencia): Estadísticas veraces y clasificaciones actualizadas al instante.\n" +
                    "• Diké (Juego Limpio): Validación estricta de actas firmadas por ambos capitanes.\n" +
                    "• Koinonía (Comunidad): Todo miembro del equipo suma, desde la convocatoria hasta el último tie-break.",
            )

            Spacer(modifier = Modifier.height(24.dp))

            CenefaGriega()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SYNAGON · LIGA DE EQUIPOS DE PÁDEL",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                color = ColoresSynagon.GrisPiedraJonica,
            )
        }
    }
}

@Composable
private fun TarjetaCapituloOlimpo(
    subtitulo: String,
    titulo: String,
    cuerpo: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.PizarraEspartana),
        border = BorderStroke(1.dp, ColoresSynagon.OroOlimpico.copy(alpha = 0.32f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                ),
                color = ColoresSynagon.OroOlimpico,
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = ColoresSynagon.BlancoMarmol,
            )
            Text(
                text = cuerpo,
                style = MaterialTheme.typography.bodyMedium,
                color = ColoresSynagon.GrisPiedraJonica,
            )
        }
    }
}

@Preview
@Composable
private fun PrevisualizacionSobreSynagon() {
    TemaSynagon {
        ContenidoPantallaSobreSynagon(alVolver = {})
    }
}
