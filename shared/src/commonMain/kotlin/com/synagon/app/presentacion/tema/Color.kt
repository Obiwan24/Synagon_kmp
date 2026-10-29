package com.synagon.app.presentacion.tema

import androidx.compose.ui.graphics.Color

/**
 * Paleta oficial del sistema de diseño "Olimpo Moderno" de Synagon.
 * Combina la sobriedad nocturna del Mar Egeo y la Pizarra Espartana con acentos nobles en Oro Olímpico.
 */
object ColoresSynagon {
    // Paleta base "Olimpo Moderno"
    val AzulEgeo = Color(0xFF0B132B)
    val PizarraEspartana = Color(0xFF1C2541)
    val OroOlimpico = Color(0xFFD4AF37)
    val BlancoMarmol = Color(0xFFF8F9FA)
    val GrisPiedraJonica = Color(0xFFA3B1C6)
    val VerdeLaurel = Color(0xFF2E7D32)
    val RojoTerracota = Color(0xFFD9534F)

    // Matices de apoyo para contenedores y bordes sobre fondo oscuro
    val OroOlimpicoSuave = Color(0xFF3A3118)
    val PizarraElevada = Color(0xFF253154)
    val BordePiedra = Color(0xFF334268)
    val VerdeLaurelClaro = Color(0xFF66BB6A)
    val VerdeLaurelFondo = Color(0xFF142E1D)
    val RojoTerracotaFondo = Color(0xFF3A1A1D)

    // Alias semánticos compartidos por los módulos de competición (Jornada, Acta, Clasificación, Estadísticas)
    val AzulPrimario = PizarraEspartana
    val AzulContenedor = PizarraElevada
    val SobreAzulContenedor = OroOlimpico

    val VerdeQuorum = VerdeLaurelClaro
    val VerdeQuorumIntenso = VerdeLaurel
    val VerdeQuorumFondo = VerdeLaurelFondo
    val VerdeQuorumBorde = VerdeLaurel

    val AmbarDuda = OroOlimpico
    val AmbarDudaFondo = OroOlimpicoSuave

    val RojoBaja = RojoTerracota
    val RojoBajaFondo = RojoTerracotaFondo

    val FondoPantalla = AzulEgeo
    val SuperficieTarjeta = PizarraEspartana
    val TextoPrincipal = BlancoMarmol
    val TextoSecundario = GrisPiedraJonica
    val BordeSuave = BordePiedra
}
