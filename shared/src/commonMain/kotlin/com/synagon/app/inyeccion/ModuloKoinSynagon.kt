package com.synagon.app.inyeccion

import com.synagon.app.datos.remoto.api.ProveedorUrlBase
import com.synagon.app.datos.remoto.api.ProveedorUrlBasePlataforma
import com.synagon.app.datos.remoto.api.ServicioApiCompeticion
import com.synagon.app.datos.remoto.api.ServicioApiJornada
import com.synagon.app.datos.remoto.api.crearClienteHttpSynagon
import com.synagon.app.datos.repositorios.RepositorioClasificacionImpl
import com.synagon.app.datos.repositorios.RepositorioEnfrentamientosImpl
import com.synagon.app.datos.repositorios.RepositorioEstadisticasImpl
import com.synagon.app.dominio.casosuso.OrdenarEstadisticasEquipoCasoUso
import com.synagon.app.dominio.casosuso.ValidarConvocatoriaCasoUso
import com.synagon.app.dominio.casosuso.ValidarTanteoPadelCasoUso
import com.synagon.app.dominio.repositorios.RepositorioClasificacion
import com.synagon.app.dominio.repositorios.RepositorioEnfrentamientos
import com.synagon.app.dominio.repositorios.RepositorioEstadisticas
import com.synagon.app.presentacion.pantallas.acceso.AccesoViewModel
import com.synagon.app.presentacion.pantallas.acta.ActaViewModel
import com.synagon.app.presentacion.pantallas.clasificacion.ClasificacionViewModel
import com.synagon.app.presentacion.pantallas.estadisticas.EstadisticasViewModel
import com.synagon.app.presentacion.pantallas.jornada.JornadaViewModel
import io.ktor.client.HttpClient
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

/**
 * Módulo principal de inyección de dependencias de Synagon (red, repositorios, casos de uso y ViewModels).
 */
val moduloSynagon: Module = module {
    // Proveedor multiplataforma de URL base (10.0.2.2:8080 en Android / localhost:8080 en iOS)
    singleOf(::ProveedorUrlBasePlataforma) bind ProveedorUrlBase::class

    // Cliente HTTP Ktor multiplataforma
    single<HttpClient> { crearClienteHttpSynagon() }

    // Servicios de API y Repositorios
    singleOf(::ServicioApiJornada)
    singleOf(::ServicioApiCompeticion)
    singleOf(::RepositorioEnfrentamientosImpl) bind RepositorioEnfrentamientos::class
    singleOf(::RepositorioClasificacionImpl) bind RepositorioClasificacion::class
    singleOf(::RepositorioEstadisticasImpl) bind RepositorioEstadisticas::class

    // Casos de uso del dominio
    factoryOf(::ValidarConvocatoriaCasoUso)
    factoryOf(::ValidarTanteoPadelCasoUso)
    factoryOf(::OrdenarEstadisticasEquipoCasoUso)

    // ViewModels de presentación MVI
    viewModelOf(::AccesoViewModel)
    viewModelOf(::JornadaViewModel)
    viewModelOf(::ActaViewModel)
    viewModelOf(::ClasificacionViewModel)
    viewModelOf(::EstadisticasViewModel)
}

/**
 * Instancia aislada de Koin para Compose Multiplatform, resistente a recreaciones de Activity en Android
 * y previsualizaciones en el IDE.
 */
val contenedorKoinSynagon: KoinApplication by lazy {
    koinApplication {
        modules(moduloSynagon)
    }
}
