package com.synagon.app.presentacion.pantallas.agora

import com.synagon.app.dominio.modelos.ConfiguracionPreviaLiga
import com.synagon.app.dominio.modelos.ResumenMembresiaUsuario
import com.synagon.app.dominio.modelos.RolMembresia
import com.synagon.app.presentacion.pantallas.jornada.DatosSemillaJornada

/**
 * Datos semilla representativos del Ágora de Synagon empleados como respaldo local interactivo
 * cuando el servidor Spring Boot aún no se encuentra activo.
 */
object DatosSemillaAgora {

    const val ID_LIGA_SEVILLA = "20000000-0000-0000-0000-000000000001"
    const val ID_LIGA_METROPOLITANA = "20000000-0000-0000-0000-000000000002"
    const val ID_LIGA_NOCTURNA = "20000000-0000-0000-0000-000000000003"

    fun crearMembresiasIniciales(): List<ResumenMembresiaUsuario> = listOf(
        ResumenMembresiaUsuario(
            ligaId = ID_LIGA_SEVILLA,
            nombreLiga = "LIGA INTERCLUBES SEVILLA · 1ª DIVISIÓN",
            equipoId = DatosSemillaJornada.ID_EQUIPO_PROPIO,
            nombreEquipoOSubtitulo = DatosSemillaJornada.NOMBRE_EQUIPO_PROPIO,
            textoSedeDetalle = "Club Pádel Triana Sevilla · 3 parejas por jornada",
            partidosPorJornada = 3,
            rol = RolMembresia.CAPITAN,
        ),
        ResumenMembresiaUsuario(
            ligaId = ID_LIGA_METROPOLITANA,
            nombreLiga = "COPA OLIMPO METROPOLITANA 2026",
            equipoId = null,
            nombreEquipoOSubtitulo = "Dirección General de Competición (8 Equipos)",
            textoSedeDetalle = "Sedes por club local · Código activo: OLIMPO-2026",
            partidosPorJornada = 3,
            rol = RolMembresia.PRESIDENTE,
        ),
        ResumenMembresiaUsuario(
            ligaId = ID_LIGA_NOCTURNA,
            nombreLiga = "SERIE EGEO NOCTURNA DE OTOÑO",
            equipoId = "50000000-0000-0000-0000-000000000009",
            nombreEquipoOSubtitulo = "Espartanos del Aljarafe P.C.",
            textoSedeDetalle = "Sede Única: Centro Deportivo Cartuja Indoor",
            partidosPorJornada = 2,
            rol = RolMembresia.JUGADOR,
        ),
    )

    /**
     * Resuelve localmente la configuración previa de una liga a partir de un código de invitación
     * cuando el backend no está disponible, permitiendo probar flujos con y sin sede de equipo.
     */
    fun resolverCodigoInvitacionLocal(codigoNormalizado: String): ConfiguracionPreviaLiga? {
        if (codigoNormalizado == "INVALIDO" || codigoNormalizado == "ERROR") {
            return null
        }
        return when {
            codigoNormalizado.contains("UNICA") || codigoNormalizado.contains("CENTRAL") -> {
                ConfiguracionPreviaLiga(
                    ligaId = "20000000-0000-0000-0000-000000000010",
                    nombreLiga = "Liga Indoor Sede Central Cartuja",
                    requiereSedeEquipo = false,
                    partidosPorJornada = 3,
                )
            }
            codigoNormalizado == "OLIMPO-2026" -> {
                ConfiguracionPreviaLiga(
                    ligaId = ID_LIGA_METROPOLITANA,
                    nombreLiga = "Copa Olimpo Metropolitana 2026",
                    requiereSedeEquipo = true,
                    partidosPorJornada = 3,
                )
            }
            else -> {
                ConfiguracionPreviaLiga(
                    ligaId = "liga-${codigoNormalizado.lowercase()}",
                    nombreLiga = "Liga Oficial Synagon ($codigoNormalizado)",
                    requiereSedeEquipo = true,
                    partidosPorJornada = 3,
                )
            }
        }
    }
}
