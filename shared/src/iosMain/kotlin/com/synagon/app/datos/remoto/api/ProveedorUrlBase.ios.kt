package com.synagon.app.datos.remoto.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun obtenerUrlBasePorDefecto(): String = "http://localhost:8080"

actual fun crearMotorHttpPlataforma(): HttpClientEngine = Darwin.create()
