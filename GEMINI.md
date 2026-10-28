# Directrices Arquitectónicas del Cliente Móvil - Proyecto SYNAGON (KMP)

## 1. Idioma y Nomenclatura (ESTRICTO)
- **TODO el código debe escribirse en ESPAÑOL**: clases, funciones `@Composable`, estados, eventos, variables, paquetes y métodos.
- Ejemplo de Composable: `@Composable fun PantallaDetalleJornada(...)`.

## 2. Stack Tecnológico en `commonMain`
- **UI:** Compose Multiplatform (Material 3).
- **Arquitectura de Presentación:** MVI (Modelo-Vista-Intención) estricto con flujo de datos unidireccional (UDF).
- **Gestión de Estado y Ciclo de Vida:** `androidx.lifecycle.ViewModel` multiplataforma con `StateFlow` y `Channel` para efectos de navegación/mensajes.
- **Inyección de Dependencias:** Koin Multiplatform.
- **Cliente HTTP:** Ktor Client con `ContentNegotiation` y `kotlinx.serialization`.
- **Fechas:** `kotlinx-datetime`.

## 3. Estructura de Paquetes (`composeApp/src/commonMain/kotlin/com/synagon/app`)
- `dominio/`
    - `modelos/`: Clases de datos puras (`Enfrentamiento`, `EstadisticaJugador`, `ParejaPartido`, `FilaClasificacion`).
    - `repositorios/`: Interfaces de repositorios que devuelven `Resultado<T>` (clase sellada propia de éxito/error).
    - `casosuso/`: Lógica de negocio y validación en cliente.
- `datos/`
    - `remoto/dto/`: Clases con `@Serializable` que reflejan el JSON del backend.
    - `remoto/api/`: Servicios de Ktor Client.
    - `mappers/`: Funciones de extensión `.aDominio()` y `.aDto()`.
    - `repositorios/`: Implementación de las interfaces del dominio.
- `presentacion/`
    - `tema/`: Colores, tipografía y diseño del sistema Synagon.
    - `navegacion/`: Grafo de navegación tipado (`Navigation Compose`).
    - `pantallas/`: Subpaquetes por funcionalidad (`jornada/`, `convocatoria/`, `acta/`, `estadisticas/`, `clasificacion/`).

## 4. Contrato MVI Obligatorio por Pantalla
Cada pantalla debe estructurarse en 3 partes claras:
1. `ContratoXxx.kt`: Contiene `data class EstadoUi(...)`, `sealed interface EventoUi` (acciones del usuario) y `sealed interface EfectoUi` (navegación, Snackbar).
2. `XxxViewModel.kt`: Recibe eventos mediante `fun procesarEvento(evento: EventoUi)` y actualiza `_estadoUi`.
3. `PantallaXxx.kt`: Un Composable con estado que recolecta el `StateFlow` y delega en un Composable `ContenidoPantallaXxx` totalmente *stateless* (sin ViewModel) para facilitar previsualizaciones y tests.