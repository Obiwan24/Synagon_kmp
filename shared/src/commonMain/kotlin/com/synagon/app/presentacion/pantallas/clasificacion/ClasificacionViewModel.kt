package com.synagon.app.presentacion.pantallas.clasificacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synagon.app.datos.remoto.api.ProveedorUrlBase
import com.synagon.app.dominio.repositorios.RepositorioClasificacion
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
 * ViewModel multiplataforma con arquitectura MVI estricta para consultar la clasificación oficial de la división.
 */
class ClasificacionViewModel(
    private val repositorioClasificacion: RepositorioClasificacion,
    private val proveedorUrlBase: ProveedorUrlBase,
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUi())
    val estadoUi: StateFlow<EstadoUi> = _estadoUi.asStateFlow()

    private val _efectosUi = Channel<EfectoUi>(Channel.BUFFERED)
    val efectosUi: Flow<EfectoUi> = _efectosUi.receiveAsFlow()

    init {
        cargarClasificacion()
    }

    /**
     * Recibe eventos de la vista y actualiza el estado siguiendo UDF.
     */
    fun procesarEvento(evento: EventoUi) {
        when (evento) {
            EventoUi.RecargarClasificacion -> cargarClasificacion()
        }
    }

    private fun cargarClasificacion() {
        val idDivision = _estadoUi.value.idDivision
        viewModelScope.launch {
            _estadoUi.update { it.copy(cargando = true, mensajeEstadoConexion = null) }

            when (val resultado = repositorioClasificacion.obtenerClasificacionDivision(idDivision)) {
                is Resultado.Exito -> {
                    val filas = if (resultado.datos.isNotEmpty()) {
                        resultado.datos.sortedBy { it.posicion }
                    } else {
                        DatosSemillaJornada.crearClasificacionDivisionSemilla()
                    }
                    _estadoUi.update {
                        it.copy(
                            cargando = false,
                            filasClasificacion = filas,
                            usandoRespaldoLocal = false,
                            mensajeEstadoConexion = null,
                        )
                    }
                }
                is Resultado.Error -> {
                    val url = proveedorUrlBase.obtenerUrlBase()
                    _estadoUi.update {
                        it.copy(
                            cargando = false,
                            filasClasificacion = DatosSemillaJornada.crearClasificacionDivisionSemilla(),
                            usandoRespaldoLocal = true,
                            mensajeEstadoConexion = describirAvisoConexion(resultado.error, url),
                        )
                    }
                }
            }
        }
    }

    private fun describirAvisoConexion(error: ErrorRed, urlBase: String): String {
        return when (error) {
            ErrorRed.SinConexion -> "Servidor no disponible en $urlBase · Mostrando clasificación local"
            ErrorRed.NoEncontrado -> "Endpoint de clasificación pendiente en $urlBase · Datos semilla activos"
            else -> "No se pudo sincronizar con $urlBase · Mostrando clasificación local"
        }
    }
}
