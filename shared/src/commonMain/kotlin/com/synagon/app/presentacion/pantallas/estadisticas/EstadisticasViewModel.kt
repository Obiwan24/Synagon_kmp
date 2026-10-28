package com.synagon.app.presentacion.pantallas.estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synagon.app.datos.remoto.api.ProveedorUrlBase
import com.synagon.app.dominio.casosuso.CriterioOrdenEstadisticas
import com.synagon.app.dominio.casosuso.OrdenarEstadisticasEquipoCasoUso
import com.synagon.app.dominio.modelos.EstadisticaJugador
import com.synagon.app.dominio.repositorios.RepositorioEstadisticas
import com.synagon.app.dominio.utilidades.ErrorRed
import com.synagon.app.dominio.utilidades.Resultado
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel multiplataforma bajo arquitectura MVI estricta para consultar y ordenar
 * el ranking interno de estadísticas individuales del equipo.
 */
class EstadisticasViewModel(
    private val repositorioEstadisticas: RepositorioEstadisticas,
    private val ordenarEstadisticasEquipoCasoUso: OrdenarEstadisticasEquipoCasoUso,
    private val proveedorUrlBase: ProveedorUrlBase,
) : ViewModel() {

    private var catalogoBaseJugadores: List<EstadisticaJugador> = emptyList()

    private val _estadoUi = MutableStateFlow(EstadoUi())
    val estadoUi: StateFlow<EstadoUi> = _estadoUi.asStateFlow()

    private val _efectosUi = Channel<EfectoUi>(Channel.BUFFERED)
    val efectosUi: Flow<EfectoUi> = _efectosUi.receiveAsFlow()

    init {
        cargarEstadisticasEquipo()
    }

    /**
     * Punto de entrada único de eventos de usuario siguiendo flujo de datos unidireccional (UDF).
     */
    fun procesarEvento(evento: EventoUi) {
        when (evento) {
            EventoUi.RecargarEstadisticas -> cargarEstadisticasEquipo()
            is EventoUi.CambiarCriterioOrden -> cambiarCriterioOrden(evento.criterio)
        }
    }

    private fun cargarEstadisticasEquipo() {
        val idEquipo = _estadoUi.value.idEquipo
        viewModelScope.launch {
            _estadoUi.update { it.copy(cargando = true, mensajeEstadoConexion = null) }

            when (val resultado = repositorioEstadisticas.obtenerEstadisticasEquipo(idEquipo)) {
                is Resultado.Exito -> {
                    val base = if (resultado.datos.isNotEmpty()) {
                        resultado.datos
                    } else {
                        DatosSemillaJornada.crearEstadisticasEquipoSemilla()
                    }
                    catalogoBaseJugadores = base
                    _estadoUi.update { actual ->
                        actual.copy(
                            cargando = false,
                            estadisticasJugadores = ordenarEstadisticasEquipoCasoUso.ejecutar(
                                estadisticas = base,
                                criterio = actual.criterioOrdenSeleccionado,
                            ),
                            usandoRespaldoLocal = false,
                            mensajeEstadoConexion = null,
                        )
                    }
                }
                is Resultado.Error -> {
                    val respaldo = DatosSemillaJornada.crearEstadisticasEquipoSemilla()
                    catalogoBaseJugadores = respaldo
                    val url = proveedorUrlBase.obtenerUrlBase()
                    _estadoUi.update { actual ->
                        actual.copy(
                            cargando = false,
                            estadisticasJugadores = ordenarEstadisticasEquipoCasoUso.ejecutar(
                                estadisticas = respaldo,
                                criterio = actual.criterioOrdenSeleccionado,
                            ),
                            usandoRespaldoLocal = true,
                            mensajeEstadoConexion = describirAvisoConexion(resultado.error, url),
                        )
                    }
                }
            }
        }
    }

    private fun cambiarCriterioOrden(nuevoCriterio: CriterioOrdenEstadisticas) {
        val fuente = catalogoBaseJugadores.ifEmpty { _estadoUi.value.estadisticasJugadores }
        val listaOrdenada = ordenarEstadisticasEquipoCasoUso.ejecutar(
            estadisticas = fuente,
            criterio = nuevoCriterio,
        )
        _estadoUi.update {
            it.copy(
                criterioOrdenSeleccionado = nuevoCriterio,
                estadisticasJugadores = listaOrdenada,
            )
        }
    }

    private fun describirAvisoConexion(error: ErrorRed, urlBase: String): String {
        return when (error) {
            ErrorRed.SinConexion -> "Servidor no disponible en $urlBase · Mostrando estadísticas locales"
            ErrorRed.NoEncontrado -> "Endpoint de estadísticas pendiente en $urlBase · Datos semilla activos"
            else -> "No se pudo sincronizar con $urlBase · Mostrando estadísticas locales"
        }
    }
}
