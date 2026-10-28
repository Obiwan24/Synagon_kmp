package com.synagon.app.dominio.modelos

/**
 * Representa el estado del ciclo de vida de un enfrentamiento en una jornada de liga.
 */
enum class EstadoEnfrentamiento(val etiqueta: String) {
    PENDIENTE_FECHA("Pendiente de fecha"),
    FECHA_PROPUESTA("Fechas en votación"),
    VOTACION_DISPONIBILIDAD_ABIERTA("Votación abierta"),
    FECHA_FIJADA("Fecha confirmada"),
    CONVOCATORIA_ABIERTA("Convocatoria abierta"),
    ALINEACION_CERRADA("Alineación cerrada"),
    EN_DISPUTA("En disputa"),
    FINALIZADO("Finalizado"),
    APLAZADO("Aplazado"),
    CANCELADO("Cancelado"),
}
