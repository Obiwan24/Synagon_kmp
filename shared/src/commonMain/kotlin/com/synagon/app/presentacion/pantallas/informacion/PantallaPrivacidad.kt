package com.synagon.app.presentacion.pantallas.informacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synagon.app.presentacion.componentes.BotonVolverOlimpo
import com.synagon.app.presentacion.componentes.CenefaGriega
import com.synagon.app.presentacion.tema.ColoresSynagon
import com.synagon.app.presentacion.tema.TemaSynagon

/**
 * Pantalla informativa legal de Política de Privacidad y Protección de Datos (RGPD / LOPDGDD).
 */
@Composable
fun PantallaPrivacidad(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ContenidoPantallaPrivacidad(
        alVolver = alVolver,
        modifier = modifier,
    )
}

/**
 * Composable stateless desplazable con las cláusulas legales conforme al Reglamento (UE) 2016/679 (RGPD)
 * y la Ley Orgánica 3/2018 (LOPDGDD) vigentes en España.
 */
@Composable
fun ContenidoPantallaPrivacidad(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ColoresSynagon.AzulEgeo,
    ) { rellenoInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColoresSynagon.AzulEgeo)
                .padding(rellenoInterno)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Cabecera con botón dorado [←]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BotonVolverOlimpo(
                    alPulsar = alVolver,
                    descripcionAccesibilidad = "Volver atrás desde Política de Privacidad",
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "PRIVACIDAD Y DATOS",
                        style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 3.sp),
                        color = ColoresSynagon.OroOlimpico,
                    )
                    Text(
                        text = "Normativa Española RGPD y LOPDGDD",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColoresSynagon.GrisPiedraJonica,
                    )
                }
            }

            CenefaGriega()

            SeccionClausulaLegal(
                numero = "1. MARCO NORMATIVO Y RESPONSABLE DEL TRATAMIENTO",
                contenido = "En cumplimiento del Reglamento (UE) 2016/679 del Parlamento Europeo y del " +
                    "Consejo, de 27 de abril de 2016 (RGPD), y de la Ley Orgánica 3/2018, de 5 de diciembre, " +
                    "de Protección de Datos Personales y garantía de los derechos digitales (LOPDGDD), se " +
                    "informa a los usuarios de que los datos personales recabados a través de la plataforma " +
                    "móvil Synagon son tratados por la Organización Deportiva Synagon España (contacto del " +
                    "Delegado de Protección de Datos: privacidad@synagon.es), con la exclusiva finalidad de " +
                    "gestionar las competiciones oficiales y amateurs de pádel por equipos.",
            )

            SeccionClausulaLegal(
                numero = "2. CATEGORÍAS DE DATOS Y TRATAMIENTO EN ACTAS Y ESTADÍSTICAS",
                contenido = "Synagon trata únicamente los datos estrictamente necesarios para el desarrollo " +
                    "reglamentario de la competición:\n\n" +
                    "• Datos identificativos y de contacto: nombre y apellidos, correo electrónico, teléfono " +
                    "de contacto (para coordinación de capitanía) y club o equipo de adscripción.\n" +
                    "• Gestión de jornada y convocatorias: registro de disponibilidad horaria en las fechas " +
                    "propuestas y alineación en Pareja 1, Pareja 2 o Pareja 3.\n" +
                    "• Actas arbitrales y evidencia documental: resultados numéricos por set, observaciones " +
                    "arbitrales e imagen fotográfica del acta física firmada por los capitanes para su " +
                    "verificación ante el Comité de Competición.\n" +
                    "• Estadísticas individuales y clasificación: cálculo automatizado de partidos apuntados, " +
                    "jugados, ganados y perdidos, así como balances de sets y juegos obtenidos durante la temporada.",
            )

            SeccionClausulaLegal(
                numero = "3. BASE JURÍDICA DE LEGITIMACIÓN",
                contenido = "La base legal para el tratamiento de sus datos personales reside en:\n" +
                    "a) La ejecución de la relación deportiva y las normas de competición aceptadas al " +
                    "inscribirse en la liga (artículo 6.1.b del RGPD).\n" +
                    "b) El consentimiento libre, específico, informado e inequívoco prestado por el jugador " +
                    "o capitán mediante la casilla de aceptación en el registro (artículo 6.1.a del RGPD y " +
                    "artículo 6 de la LOPDGDD).\n" +
                    "c) El interés legítimo de la organización y de los clubes participantes en garantizar " +
                    "la transparencia de las clasificaciones, actas y resultados oficiales (artículo 6.1.f del RGPD).",
            )

            SeccionClausulaLegal(
                numero = "4. DESTINATARIOS Y PUBLICIDAD DE LOS RESULTADOS DEPORTIVOS",
                contenido = "Por la propia naturaleza pública y competitiva de una liga deportiva por equipos, " +
                    "el nombre y apellidos de los jugadores convocados, los resultados reflejados en las actas " +
                    "de enfrentamiento, las tablas de clasificación de cada división y las métricas deportivas " +
                    "serán visibles para los integrantes de los clubes inscritos y los órganos arbitrales de " +
                    "la competición. Los números de teléfono y correos electrónicos privados no se exhiben " +
                    "públicamente ni se ceden a terceros con fines comerciales o publicitarios.",
            )

            SeccionClausulaLegal(
                numero = "5. PLAZO DE CONSERVACIÓN DE LA INFORMACIÓN",
                contenido = "Los datos personales, fotografías de actas arbitrales y registros estadísticos se " +
                    "conservarán mientras el usuario mantenga activa su cuenta deportiva y durante los plazos " +
                    "de revisión arbitral de la temporada en curso. Una vez solicitada la baja, los datos " +
                    "quedarán debidamente bloqueados conforme al artículo 32 de la LOPDGDD únicamente durante " +
                    "los plazos legales de prescripción de posibles responsabilidades.",
            )

            SeccionClausulaLegal(
                numero = "6. EJERCICIO DE DERECHOS Y TUTELA ANTE LA AEPD",
                contenido = "Cualquier jugador o capitán puede ejercer en todo momento sus derechos de Acceso, " +
                    "Rectificación, Supresión, Oposición, Limitación del tratamiento y Portabilidad de sus " +
                    "datos dirigiendo una comunicación escrita a privacidad@synagon.es indicando su identificador " +
                    "de usuario.\n\n" +
                    "Asimismo, en caso de considerar vulnerados sus derechos en materia de protección de datos, " +
                    "tiene derecho a presentar una reclamación ante la Agencia Española de Protección de Datos " +
                    "(AEPD) a través de su sede electrónica oficial (www.aepd.es).",
            )

            CenefaGriega()

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SeccionClausulaLegal(
    numero: String,
    contenido: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresSynagon.PizarraEspartana),
        border = BorderStroke(1.dp, ColoresSynagon.BordePiedra),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = numero,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                ),
                color = ColoresSynagon.OroOlimpico,
            )
            Text(
                text = contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = ColoresSynagon.BlancoMarmol.copy(alpha = 0.92f),
            )
        }
    }
}

@Preview
@Composable
private fun PrevisualizacionPantallaPrivacidad() {
    TemaSynagon {
        ContenidoPantallaPrivacidad(alVolver = {})
    }
}
