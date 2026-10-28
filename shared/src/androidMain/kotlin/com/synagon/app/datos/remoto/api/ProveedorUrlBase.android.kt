package com.synagon.app.datos.remoto.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun obtenerUrlBasePorDefecto(): String = "http://10.0.2.2:8080"

actual fun crearMotorHttpPlataforma(): HttpClientEngine = OkHttp.create()
