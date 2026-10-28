package com.synagon.app.presentacion.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ColoresSynagon {
    val AzulPrimario = Color(0xFF0E3A5D)
    val AzulContenedor = Color(0xFFD7E8F7)
    val SobreAzulContenedor = Color(0xFF07233B)

    val VerdeQuorum = Color(0xFF15803D)
    val VerdeQuorumIntenso = Color(0xFF16A34A)
    val VerdeQuorumFondo = Color(0xFFDCFCE7)
    val VerdeQuorumBorde = Color(0xFF22C55E)

    val AmbarDuda = Color(0xFFB45309)
    val AmbarDudaFondo = Color(0xFFFEF3C7)

    val RojoBaja = Color(0xFFB91C1C)
    val RojoBajaFondo = Color(0xFFFEE2E2)

    val FondoPantalla = Color(0xFFF3F6FA)
    val SuperficieTarjeta = Color(0xFFFFFFFF)
    val TextoPrincipal = Color(0xFF0F172A)
    val TextoSecundario = Color(0xFF475569)
    val BordeSuave = Color(0xFFCBD5E1)
}

private val EsquemaColoresClaroSynagon = lightColorScheme(
    primary = ColoresSynagon.AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = ColoresSynagon.AzulContenedor,
    onPrimaryContainer = ColoresSynagon.SobreAzulContenedor,
    secondary = ColoresSynagon.VerdeQuorum,
    onSecondary = Color.White,
    secondaryContainer = ColoresSynagon.VerdeQuorumFondo,
    onSecondaryContainer = Color(0xFF052E16),
    tertiary = ColoresSynagon.AmbarDuda,
    tertiaryContainer = ColoresSynagon.AmbarDudaFondo,
    error = ColoresSynagon.RojoBaja,
    errorContainer = ColoresSynagon.RojoBajaFondo,
    background = ColoresSynagon.FondoPantalla,
    onBackground = ColoresSynagon.TextoPrincipal,
    surface = ColoresSynagon.SuperficieTarjeta,
    onSurface = ColoresSynagon.TextoPrincipal,
    surfaceVariant = Color(0xFFE8EEF5),
    onSurfaceVariant = ColoresSynagon.TextoSecundario,
    outline = ColoresSynagon.BordeSuave,
)

@Composable
fun TemaSynagon(
    contenido: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = EsquemaColoresClaroSynagon,
        content = contenido,
    )
}
