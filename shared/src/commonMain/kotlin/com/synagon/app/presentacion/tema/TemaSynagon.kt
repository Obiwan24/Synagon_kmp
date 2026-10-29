package com.synagon.app.presentacion.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Esquema cromático oscuro "Olimpo Moderno" para Material 3.
 */
private val EsquemaColoresOlimpoModerno = darkColorScheme(
    primary = ColoresSynagon.OroOlimpico,
    onPrimary = ColoresSynagon.AzulEgeo,
    primaryContainer = ColoresSynagon.PizarraElevada,
    onPrimaryContainer = ColoresSynagon.OroOlimpico,
    secondary = ColoresSynagon.VerdeLaurel,
    onSecondary = ColoresSynagon.BlancoMarmol,
    secondaryContainer = ColoresSynagon.VerdeLaurelFondo,
    onSecondaryContainer = ColoresSynagon.BlancoMarmol,
    tertiary = ColoresSynagon.GrisPiedraJonica,
    onTertiary = ColoresSynagon.AzulEgeo,
    tertiaryContainer = ColoresSynagon.OroOlimpicoSuave,
    onTertiaryContainer = ColoresSynagon.OroOlimpico,
    error = ColoresSynagon.RojoTerracota,
    onError = ColoresSynagon.BlancoMarmol,
    errorContainer = ColoresSynagon.RojoTerracotaFondo,
    onErrorContainer = ColoresSynagon.RojoTerracota,
    background = ColoresSynagon.AzulEgeo,
    onBackground = ColoresSynagon.BlancoMarmol,
    surface = ColoresSynagon.PizarraEspartana,
    onSurface = ColoresSynagon.BlancoMarmol,
    surfaceVariant = ColoresSynagon.PizarraElevada,
    onSurfaceVariant = ColoresSynagon.GrisPiedraJonica,
    outline = ColoresSynagon.BordePiedra,
)

/**
 * Tipografía "Olimpo Moderno":
 * - Títulos y cabeceras con familia clásica con serifa (`FontFamily.Serif`) y espaciado noble (`letterSpacing = 3.sp` en titulares).
 * - Cuerpo de lectura y etiquetas funcionales sin serifa (`FontFamily.SansSerif`) para máxima legibilidad en móvil.
 */
val TipografiaOlimpoModerno = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 3.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 3.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = 3.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 3.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 3.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 2.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 2.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 1.2.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.3.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.5.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.8.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.6.sp,
    ),
)

@Composable
fun TemaSynagon(
    contenido: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = EsquemaColoresOlimpoModerno,
        typography = TipografiaOlimpoModerno,
        content = contenido,
    )
}
