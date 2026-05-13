package com.nexuslauncher.ui.solar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.navigation.PlanetId
import com.nexuslauncher.viewmodel.SolarViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun SolarSystemScreen(
    nexusDataStore   : NexusDataStore? = null,
    vm               : SolarSystemViewModel = viewModel(),
    onPlanetSelected : (PlanetId) -> Unit = {}
) {
    val solarVm: SolarViewModel? = nexusDataStore?.let {
        viewModel(factory = SolarViewModel.factory(it))
    }

    val metrics    by vm.systemMetrics.collectAsState()
    val tierResult by vm.tierResult.collectAsState()

    val planetIdMap = remember {
        mapOf(
            "nexus"      to PlanetId.NEXUS_PRIME,
            "aetherion"  to PlanetId.AETHERION,
            "lumina"     to PlanetId.LUMINA,
            "modara"     to PlanetId.MODARA,
            "curseforge" to PlanetId.MODARA,
            "instarrion" to PlanetId.INSTARRION,
            "chronos"    to PlanetId.CHRONOS,
            "cloudnexus" to PlanetId.CHRONOS,
            "persona"    to PlanetId.PERSONA,
            "labx"       to PlanetId.HELIOS_CONTROL,
            "helios"     to PlanetId.HELIOS_CONTROL
        )
    }

    val planetHitAreas = remember { mutableStateListOf<Triple<String, Float, Float>>() }

    val infiniteTransition = rememberInfiniteTransition(label = "solar")
    val time by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (Math.PI * 200).toFloat(),
        animationSpec = infiniteRepeatable(tween(200_000, easing = LinearEasing), RepeatMode.Restart),
        label         = "time"
    )

    val textMeasurer = rememberTextMeasurer()

    Box(Modifier.fillMaxSize()) {

        // Fundo gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF0D0A1A), Color(0xFF05050A)),
                        radius = 1200f
                    )
                )
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val hit = planetHitAreas.firstOrNull { (_, px, py) ->
                            val dx = tapOffset.x - px
                            val dy = tapOffset.y - py
                            sqrt(dx * dx + dy * dy) < 80f
                        }
                        hit?.let { (id, _, _) ->
                            planetIdMap[id]?.let { planetId ->
                                solarVm?.updateLastPlanet(id)
                                onPlanetSelected(planetId)
                            }
                        }
                    }
                }
        ) {
            val w     = size.width
            val h     = size.height
            val cx    = w / 2f
            val cy    = h / 2f
            val scale = minOf(w, h) / 1200f
            val t     = time * 0.001f

            // Névoas (nebulae) em Canvas Compose puro
            listOf(
                Triple(cx - w * 0.2f,  cy - h * 0.25f, Color(0x281E0040)),
                Triple(cx + w * 0.25f, cy + h * 0.15f, Color(0x28001540)),
                Triple(cx - w * 0.1f,  cy + h * 0.2f,  Color(0x28000F2A))
            ).forEach { (nx, ny, color) ->
                val nRadius = w * 0.22f
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors  = listOf(color, Color.Transparent),
                        center  = Offset(nx, ny),
                        radius  = nRadius
                    ),
                    radius = nRadius,
                    center = Offset(nx, ny)
                )
            }

            // Órbitas elípticas
            SolarSystem.PLANETS.forEach { planet ->
                val rx = planet.orbitRadius * scale
                val ry = rx * 0.40f
                drawOval(
                    color   = planet.color.copy(alpha = 0.12f),
                    topLeft = Offset(cx - rx, cy - ry),
                    size    = Size(rx * 2f, ry * 2f),
                    style   = Stroke(width = 0.8f)
                )
            }

            // Sol com pulso
            val sunPulse = 1f + sin(t * 1.5f) * 0.04f
            val sunR     = 52f * scale * sunPulse
            repeat(4) { i ->
                drawCircle(
                    color  = Color(0xFFFF8F00).copy(alpha = 0.06f * (4 - i)),
                    radius = sunR * (1f + (i + 1) * 0.45f),
                    center = Offset(cx, cy)
                )
            }
            drawCircle(color = Color(0xFFFFCC00), radius = sunR * 1.1f, center = Offset(cx, cy))
            drawCircle(color = Color(0xFFFFB300), radius = sunR,         center = Offset(cx, cy))
            drawCircle(color = Color(0xFFFF8F00), radius = sunR * 0.7f,  center = Offset(cx, cy))

            // Label do sol
            val sunNameStyle = TextStyle(
                color      = Color.White,
                fontSize   = (14f * scale * 80f).coerceIn(11f, 19f).sp,
                fontWeight = FontWeight.Bold
            )
            val sunLabelMeasured = textMeasurer.measure("NÚCLEO NEXUS", sunNameStyle)
            drawText(
                textLayoutResult = sunLabelMeasured,
                topLeft          = Offset(
                    x = cx - sunLabelMeasured.size.width / 2f,
                    y = cy - sunR - sunLabelMeasured.size.height - 4.dp.toPx()
                )
            )

            // Métricas centrais abaixo do sol
            val metricsText = "CPU ${metrics.cpuPercent}%   FPS ${metrics.fpsCurrent}   GPU ${metrics.gpuPercent}%"
            val metricsStyle = TextStyle(
                color      = Color(0xFF00E5FF).copy(alpha = 0.85f),
                fontSize   = (9f * scale * 80f).coerceIn(9f, 14f).sp,
                fontWeight = FontWeight.Medium
            )
            val metricsMeasured = textMeasurer.measure(metricsText, metricsStyle)
            drawText(
                textLayoutResult = metricsMeasured,
                topLeft          = Offset(
                    x = cx - metricsMeasured.size.width / 2f,
                    y = cy + sunR + 8.dp.toPx()
                )
            )

            // Planetas
            planetHitAreas.clear()
            SolarSystem.PLANETS.forEach { planet ->
                val angle = t * planet.orbitSpeed
                val rx    = planet.orbitRadius * scale
                val ry    = rx * 0.40f
                val px    = cx + cos(angle) * rx
                val py    = cy + sin(angle) * ry
                val pr    = planet.size * scale

                planetHitAreas.add(Triple(planet.id, px, py))

                // Halo / glow
                drawCircle(
                    color  = planet.color.copy(alpha = 0.18f),
                    radius = pr * 2.8f,
                    center = Offset(px, py)
                )
                // Corpo do planeta
                drawCircle(
                    color  = planet.color,
                    radius = pr,
                    center = Offset(px, py)
                )
                // Brilho especular
                drawCircle(
                    color  = Color.White.copy(alpha = 0.15f),
                    radius = pr * 0.5f,
                    center = Offset(px - pr * 0.2f, py - pr * 0.2f)
                )
                // Anel do planeta
                drawOval(
                    color   = planet.color.copy(alpha = 0.3f),
                    topLeft = Offset(px - pr * 1.6f, py - pr * 0.22f),
                    size    = Size(pr * 3.2f, pr * 0.44f),
                    style   = Stroke(width = 1.2f)
                )

                // Nome do planeta acima
                val nameStyle = TextStyle(
                    color      = planet.color,
                    fontSize   = (12f * scale * 80f).coerceIn(11f, 19f).sp,
                    fontWeight = FontWeight.Bold
                )
                val nameMeasured = textMeasurer.measure(planet.name, nameStyle)
                drawText(
                    textLayoutResult = nameMeasured,
                    topLeft          = Offset(
                        x = px - nameMeasured.size.width / 2f,
                        y = py - pr - nameMeasured.size.height - 7.dp.toPx()
                    )
                )

                // Descrição abaixo
                val descStyle = TextStyle(
                    color    = Color(0xFFB4CCFF).copy(alpha = 0.7f),
                    fontSize = (9f * scale * 80f).coerceIn(9f, 14f).sp
                )
                val descMeasured = textMeasurer.measure(planet.description, descStyle)
                drawText(
                    textLayoutResult = descMeasured,
                    topLeft          = Offset(
                        x = px - descMeasured.size.width / 2f,
                        y = py + pr + 5.dp.toPx()
                    )
                )
            }
        }

        // Barra de topo
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF050510).copy(alpha = 0.88f))
                .padding(horizontal = 20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.material.Text(
                "NEXUS LAUNCHER — SISTEMA SOLAR",
                color         = Color(0xFF00E5FF),
                fontWeight    = FontWeight.Black,
                fontSize      = 13.sp,
                letterSpacing = 1.5.sp
            )
            androidx.compose.material.Text(
                "FPS ${metrics.fpsCurrent} · CPU ${metrics.cpuPercent}% · RAM ${String.format("%.1f", metrics.ramGb)}GB",
                color      = Color(0xFF00E5FF).copy(alpha = 0.7f),
                fontSize   = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Barra de rodapé
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF050510).copy(alpha = 0.88f))
                .padding(horizontal = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.material.Text(
                "Nexus Boost   Shaders HDR Ativado   Mods Ativos: 5   Relatorio FPS",
                color    = Color.White.copy(alpha = 0.45f),
                fontSize = 9.sp
            )
            tierResult?.let {
                androidx.compose.material.Text(
                    "${it.tier.label} · v1.5.0.0",
                    color    = Color.White.copy(alpha = 0.3f),
                    fontSize = 9.sp
                )
            }
        }
    }
}
