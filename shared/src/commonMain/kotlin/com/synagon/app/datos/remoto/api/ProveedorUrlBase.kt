package com.synagon.app.datos.remoto.api

import io.ktor.client.engine.HttpClientEngine

/**
 * Proveedor multiplataforma de la URL base del servidor backend Spring Boot de Synagon.
 */
interface ProveedorUrlBase {
    fun obtenerUrlBase(): String
}

/**
 * Implementación por defecto que delega en la configuración nativa de cada plataforma:
 * - Android: http://10.0.2.2:8080
 * - iOS: http://localhost:8080
 */
class ProveedorUrlBasePlataforma : ProveedorUrlBase {
    override fun obtenerUrlBase(): String = obtenerUrlBasePorDefecto()
}

/**
 * Obtiene la URL base del backend según el entorno nativo de ejecución.
 */
expect fun obtenerUrlBasePorDefecto(): String

/**
 * Crea el motor nativo de Ktor Client correspondiente a cada plataforma (OkHttp en Android, Darwin en iOS).
 */
expect fun crearMotorHttpPlataforma(): HttpClientEngine
