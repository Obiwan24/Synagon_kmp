package com.synagon.app.dominio

import com.synagon.app.datos.mappers.aDominio
import com.synagon.app.datos.mappers.aDto
import com.synagon.app.dominio.casosuso.ValidarConvocatoriaCasoUso
import com.synagon.app.dominio.modelos.ParejaPartido
import com.synagon.app.dominio.utilidades.Resultado
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JornadaDominioYMappersTest {

    private val validador = ValidarConvocatoriaCasoUso()

    @Test
    fun `detecta correctamente quorum de 6 o mas jugadores disponibles en las propuestas`() {
        val enfrentamiento = DatosSemillaJornada.crearEnfrentamientoInicial()
        val propuesta1 = enfrentamiento.propuestasFecha[0]
        val propuesta2 = enfrentamiento.propuestasFecha[1]

        assertEquals(6, enfrentamiento.contarDisponiblesEnPropuesta(propuesta1.id))
        assertTrue(enfrentamiento.alcanzaQuorumDisponibles(propuesta1.id))

        assertEquals(5, enfrentamiento.contarDisponiblesEnPropuesta(propuesta2.id))
        assertFalse(enfrentamiento.alcanzaQuorumDisponibles(propuesta2.id))
    }

    @Test
    fun `acepta convocatoria valida de 6 jugadores unicos en Parejas 1 2 y 3`() {
        val idsDisponibles = setOf("j1", "j2", "j3", "j4", "j5", "j6", "j7")
        val parejas = listOf(
            ParejaPartido(id = "p1", numeroPareja = 1, idJugadorUno = "j1", nombreJugadorUno = "Uno", idJugadorDos = "j2", nombreJugadorDos = "Dos"),
            ParejaPartido(id = "p2", numeroPareja = 2, idJugadorUno = "j3", nombreJugadorUno = "Tres", idJugadorDos = "j4", nombreJugadorDos = "Cuatro"),
            ParejaPartido(id = "p3", numeroPareja = 3, idJugadorUno = "j5", nombreJugadorUno = "Cinco", idJugadorDos = "j6", nombreJugadorDos = "Seis"),
        )

        val resultado = validador.ejecutar(parejas, idsDisponibles)
        assertIs<Resultado.Exito<List<ParejaPartido>>>(resultado)
        assertEquals(3, resultado.datos.size)
    }

    @Test
    fun `rechaza convocatoria cuando un jugador se repite en dos parejas`() {
        val idsDisponibles = setOf("j1", "j2", "j3", "j4", "j5", "j6")
        val parejasConDuplicado = listOf(
            ParejaPartido(id = "p1", numeroPareja = 1, idJugadorUno = "j1", nombreJugadorUno = "Uno", idJugadorDos = "j2", nombreJugadorDos = "Dos"),
            ParejaPartido(id = "p2", numeroPareja = 2, idJugadorUno = "j1", nombreJugadorUno = "Uno", idJugadorDos = "j4", nombreJugadorDos = "Cuatro"),
            ParejaPartido(id = "p3", numeroPareja = 3, idJugadorUno = "j5", nombreJugadorUno = "Cinco", idJugadorDos = "j6", nombreJugadorDos = "Seis"),
        )

        val resultado = validador.ejecutar(parejasConDuplicado, idsDisponibles)
        assertIs<Resultado.Error<String>>(resultado)
    }

    @Test
    fun `mapea correctamente Enfrentamiento entre DTO y Dominio`() {
        val original = DatosSemillaJornada.crearEnfrentamientoInicial()
        val dto = original.aDto()
        val reconstruido = dto.aDominio()

        assertEquals(original.id, reconstruido.id)
        assertEquals(original.numeroJornada, reconstruido.numeroJornada)
        assertEquals(original.nombreEquipoLocal, reconstruido.nombreEquipoLocal)
        assertEquals(original.nombreEquipoVisitante, reconstruido.nombreEquipoVisitante)
        assertEquals(2, reconstruido.propuestasFecha.size)
        assertEquals(original.disponibilidades.size, reconstruido.disponibilidades.size)
    }
}
