package com.synagon.app.presentacion

import com.synagon.app.presentacion.pantallas.acceso.AccesoViewModel
import com.synagon.app.presentacion.pantallas.acceso.EventoAccesoUi
import com.synagon.app.presentacion.pantallas.acceso.ModoAcceso
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccesoViewModelTest {

    @Test
    fun `alterna correctamente entre modo LOGIN y REGISTRO y visibilidad de contrasena`() {
        val viewModel = AccesoViewModel()
        assertEquals(ModoAcceso.LOGIN, viewModel.estadoUi.value.modo)
        assertFalse(viewModel.estadoUi.value.contrasenaVisible)

        viewModel.procesarEvento(EventoAccesoUi.CambiarModo(ModoAcceso.REGISTRO))
        assertTrue(viewModel.estadoUi.value.esModoRegistro)

        viewModel.procesarEvento(EventoAccesoUi.AlternarVisibilidadContrasena)
        assertTrue(viewModel.estadoUi.value.contrasenaVisible)

        viewModel.procesarEvento(EventoAccesoUi.AlternarMenuDesplegable)
        assertTrue(viewModel.estadoUi.value.menuDesplegado)

        viewModel.procesarEvento(EventoAccesoUi.CerrarMenuDesplegable)
        assertFalse(viewModel.estadoUi.value.menuDesplegado)
    }

    @Test
    fun `valida credenciales vacias en LOGIN y limpia errores al editar`() {
        val viewModel = AccesoViewModel()

        viewModel.procesarEvento(EventoAccesoUi.IniciarSesion)
        assertNotNull(viewModel.estadoUi.value.errorUsuario)
        assertNotNull(viewModel.estadoUi.value.errorContrasena)

        viewModel.procesarEvento(EventoAccesoUi.CambiarUsuario("capitan@synagon.es"))
        viewModel.procesarEvento(EventoAccesoUi.CambiarContrasena("123456"))
        assertNull(viewModel.estadoUi.value.errorUsuario)
        assertNull(viewModel.estadoUi.value.errorContrasena)

        viewModel.procesarEvento(EventoAccesoUi.IniciarSesion)
        assertNull(viewModel.estadoUi.value.errorUsuario)
        assertNull(viewModel.estadoUi.value.errorContrasena)
    }

    @Test
    fun `exige aceptacion de Politica de Privacidad RGPD en modo REGISTRO`() {
        val viewModel = AccesoViewModel()
        viewModel.procesarEvento(EventoAccesoUi.CambiarModo(ModoAcceso.REGISTRO))
        viewModel.procesarEvento(EventoAccesoUi.CambiarNombre("Álvaro Iglesias"))
        viewModel.procesarEvento(EventoAccesoUi.CambiarTelefono("+34 612345678"))
        viewModel.procesarEvento(EventoAccesoUi.CambiarUsuario("alvaro@synagon.es"))
        viewModel.procesarEvento(EventoAccesoUi.CambiarContrasena("olimpo2026"))

        viewModel.procesarEvento(EventoAccesoUi.Registrarse)
        assertNotNull(viewModel.estadoUi.value.errorPrivacidad)

        viewModel.procesarEvento(EventoAccesoUi.CambiarAceptarPrivacidad(true))
        assertNull(viewModel.estadoUi.value.errorPrivacidad)

        viewModel.procesarEvento(EventoAccesoUi.Registrarse)
        assertNull(viewModel.estadoUi.value.errorNombre)
        assertNull(viewModel.estadoUi.value.errorTelefono)
        assertNull(viewModel.estadoUi.value.errorUsuario)
        assertNull(viewModel.estadoUi.value.errorContrasena)
        assertNull(viewModel.estadoUi.value.errorPrivacidad)
    }
}
