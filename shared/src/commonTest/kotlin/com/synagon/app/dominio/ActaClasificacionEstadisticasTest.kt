package com.synagon.app.dominio

import com.synagon.app.datos.mappers.aDominio
import com.synagon.app.datos.mappers.aDto
import com.synagon.app.dominio.casosuso.CriterioOrdenEstadisticas
import com.synagon.app.dominio.casosuso.OrdenarEstadisticasEquipoCasoUso
import com.synagon.app.dominio.casosuso.ValidarTanteoPadelCasoUso
import com.synagon.app.dominio.modelos.ActaEnfrentamiento
import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.dominio.modelos.GanadorPartido
import com.synagon.app.dominio.modelos.ResultadoParejaActa
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ActaClasificacionEstadisticasTest {

    private val validadorTanteo = ValidarTanteoPadelCasoUso()
    private val ordenadorEstadisticas = OrdenarEstadisticasEquipoCasoUso()

    @Test
    fun `valida correctamente tanteos reglamentarios e ilegales de padel en un set`() {
        assertTrue(validadorTanteo.esSetReglamentario(6, 0))
        assertTrue(validadorTanteo.esSetReglamentario(6, 4))
        assertTrue(validadorTanteo.esSetReglamentario(7, 5))
        assertTrue(validadorTanteo.esSetReglamentario(6, 7))

        assertFalse(validadorTanteo.esSetReglamentario(6, 5))
        assertFalse(validadorTanteo.esSetReglamentario(6, 6))
        assertFalse(validadorTanteo.esSetReglamentario(7, 4))
        assertFalse(validadorTanteo.esSetReglamentario(8, 6))
    }

    @Test
    fun `partido 2-0 valido prohibe tercer set y asigna ganador`() {
        val validacionValida = validadorTanteo.validarEntradaPartidoEnTiempoReal(
            textoSet1Local = "6",
            textoSet1Visitante = "3",
            textoSet2Local = "7",
            textoSet2Visitante = "6",
            textoSet3Local = "",
            textoSet3Visitante = "",
        )
        assertTrue(validacionValida.esPartidoValido)
        assertTrue(validacionValida.prohibeTercerSet)
        assertEquals(GanadorPartido.LOCAL, validacionValida.ganador)

        val conSet3Indebido = validadorTanteo.validarEntradaPartidoEnTiempoReal(
            textoSet1Local = "6",
            textoSet1Visitante = "3",
            textoSet2Local = "7",
            textoSet2Visitante = "6",
            textoSet3Local = "6",
            textoSet3Visitante = "2",
        )
        assertFalse(conSet3Indebido.esPartidoValido)
        assertNotNull(conSet3Indebido.errorSet3)
    }

    @Test
    fun `empate 1-1 exige tercer set reglamentario en tiempo real`() {
        val sinTercerSet = validadorTanteo.validarEntradaPartidoEnTiempoReal(
            textoSet1Local = "6",
            textoSet1Visitante = "4",
            textoSet2Local = "5",
            textoSet2Visitante = "7",
            textoSet3Local = "",
            textoSet3Visitante = "",
        )
        assertFalse(sinTercerSet.esPartidoValido)
        assertTrue(sinTercerSet.requiereTercerSet)
        assertNotNull(sinTercerSet.errorGeneralPartido)

        val conTercerSetValido = validadorTanteo.validarEntradaPartidoEnTiempoReal(
            textoSet1Local = "6",
            textoSet1Visitante = "4",
            textoSet2Local = "5",
            textoSet2Visitante = "7",
            textoSet3Local = "4",
            textoSet3Visitante = "6",
        )
        assertTrue(conTercerSetValido.esPartidoValido)
        assertEquals(GanadorPartido.VISITANTE, conTercerSetValido.ganador)
    }

    @Test
    fun `valida referencia de foto del acta oficial`() {
        assertNull(validadorTanteo.validarReferenciaFotoActa("https://cdn.synagon.es/actas/j5.jpg"))
        assertNull(validadorTanteo.validarReferenciaFotoActa("acta_firmada_jornada5.png"))
        assertNotNull(validadorTanteo.validarReferenciaFotoActa("abc"))
    }

    @Test
    fun `ordena estadisticas del equipo por los 4 criterios oficiales`() {
        val jugadorMasVictorias = EstadisticaJugador(
            idJugador = "j1",
            nombreJugador = "Jugador Victorias",
            partidosApuntados = 4,
            partidosJugados = 4,
            partidosGanados = 4,
            partidosPerdidos = 0,
            setsGanados = 8,
            setsPerdidos = 1,
            juegosGanados = 50,
            juegosPerdidos = 35,
        )
        val jugadorMasCompromiso = EstadisticaJugador(
            idJugador = "j2",
            nombreJugador = "Jugador Compromiso",
            partidosApuntados = 8,
            partidosJugados = 5,
            partidosGanados = 2,
            partidosPerdidos = 3,
            setsGanados = 5,
            setsPerdidos = 6,
            juegosGanados = 42,
            juegosPerdidos = 48,
        )
        val jugadorMejorDiferenciaJuegos = EstadisticaJugador(
            idJugador = "j3",
            nombreJugador = "Jugador Diferencia",
            partidosApuntados = 3,
            partidosJugados = 3,
            partidosGanados = 3,
            partidosPerdidos = 0,
            setsGanados = 6,
            setsPerdidos = 0,
            juegosGanados = 36,
            juegosPerdidos = 6,
        )

        val plantilla = listOf(jugadorMasCompromiso, jugadorMejorDiferenciaJuegos, jugadorMasVictorias)

        assertEquals(
            "j1",
            ordenadorEstadisticas.ejecutar(plantilla, CriterioOrdenEstadisticas.VICTORIAS).first().idJugador,
        )
        assertEquals(
            "j2",
            ordenadorEstadisticas.ejecutar(plantilla, CriterioOrdenEstadisticas.PARTIDOS_JUGADOS).first().idJugador,
        )
        assertEquals(
            "j2",
            ordenadorEstadisticas.ejecutar(plantilla, CriterioOrdenEstadisticas.COMPROMISO).first().idJugador,
        )
        assertEquals(
            "j3",
            ordenadorEstadisticas.ejecutar(plantilla, CriterioOrdenEstadisticas.DIFERENCIA_JUEGOS).first().idJugador,
        )
    }

    @Test
    fun `mapea correctamente Acta Clasificacion y Estadisticas entre Dominio y DTO`() {
        val acta = ActaEnfrentamiento(
            idEnfrentamiento = DatosSemillaJornada.ID_ENFRENTAMIENTO_INICIAL,
            urlFotoActa = "https://cdn.synagon.es/actas/acta_j5.jpg",
            observaciones = "Sin incidencias",
            resultadosParejas = listOf(
                ResultadoParejaActa(
                    numeroPareja = 1,
                    nombreParejaLocal = "Pareja Local 1",
                    nombreParejaVisitante = "Pareja Visitante 1",
                    set1Local = 6,
                    set1Visitante = 3,
                    set2Local = 6,
                    set2Visitante = 4,
                ),
            ),
        )
        val actaDto = acta.aDto()
        assertEquals(acta.urlFotoActa, actaDto.fotoActaUrl)
        assertEquals(1, actaDto.partidos.size)
        assertEquals(6, actaDto.partidos.first().set1Local)

        val filaOriginal = DatosSemillaJornada.crearClasificacionDivisionSemilla().first()
        val filaReconstruida = filaOriginal.aDto().aDominio()
        assertEquals(filaOriginal.posicion, filaReconstruida.posicion)
        assertEquals(filaOriginal.partidosParejaGanados, filaReconstruida.partidosParejaGanados)
        assertEquals(filaOriginal.diferenciaJuegos, filaReconstruida.diferenciaJuegos)
        assertEquals(filaOriginal.puntos, filaReconstruida.puntos)

        val estadisticaOriginal = DatosSemillaJornada.crearEstadisticasEquipoSemilla().first()
        val estadisticaReconstruida = estadisticaOriginal.aDto().aDominio()
        assertEquals(estadisticaOriginal.partidosApuntados, estadisticaReconstruida.partidosApuntados)
        assertEquals(estadisticaOriginal.partidosJugados, estadisticaReconstruida.partidosJugados)
        assertEquals(estadisticaOriginal.partidosGanados, estadisticaReconstruida.partidosGanados)
        assertEquals(estadisticaOriginal.partidosPerdidos, estadisticaReconstruida.partidosPerdidos)
        assertEquals(estadisticaOriginal.setsGanados, estadisticaReconstruida.setsGanados)
        assertEquals(estadisticaOriginal.setsPerdidos, estadisticaReconstruida.setsPerdidos)
        assertEquals(estadisticaOriginal.juegosGanados, estadisticaReconstruida.juegosGanados)
        assertEquals(estadisticaOriginal.juegosPerdidos, estadisticaReconstruida.juegosPerdidos)
    }
}
