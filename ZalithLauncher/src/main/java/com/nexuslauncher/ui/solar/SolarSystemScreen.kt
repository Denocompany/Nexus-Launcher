package com.nexuslauncher.ui.solar

import android.graphics.Paint as NativePaint
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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
    nexusDataStore  : NexusDataStore? = null,
    vm: SolarSystemViewModel = viewModel(),
    onPlanetSelected: (PlanetId) -> Unit = {}
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

    val planetHitAreas = remember { mutableListOf<Triple<String, Float, Float>>() }

    val infiniteTransition = rememberInfiniteTransition(label = "solar")
    val time by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (Math.PI * 200).toFloat(),
        animationSpec = infiniteRepeatable(tween(200_000, easing = LinearEasing), RepeatMode.Restart),
        label         = "time"
    )

    Box(Modifier.fillMaxSize()) {

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

        val textPaintName = remember {
            NativePaint().apply {
                color          = android.graphics.Color.WHITE
                textAlign      = NativePaint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias    = true
            }
        }
        val textPaintDesc = remember {
            NativePaint().apply {
                color       = android.graphics.Color.argb(180, 180, 220, 255)
                textAlign   = NativePaint.Align.CENTER
                isAntiAlias = true
            }
        }

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
            val nativeCanvas = drawContext.canvas.nativeCanvas

            textPaintName.textSize = (12f * scale * 80f).coerceIn(22f, 38f)
            textPaintDesc.textSize = (9f  * scale * 80f).coerceIn(16f, 28f)

            val nebulaPaint = android.graphics.Paint().apply { isAntiAlias = true }
            listOf(
                Triple(cx - w * 0.2f,  cy - h * 0.25f, "#1E0040"),
                Triple(cx + w * 0.25f, cy + h * 0.15f, "#001540"),
                Triple(cx - w * 0.1f,  cy + h * 0.2f,  "#000F2A")
            ).forEach { (nx, ny, hex) ->
                val nRadius = w * 0.22f
                val shader  = android.graphics.RadialGradient(
                    nx, ny, nRadius,
                    intArrayOf(
                        android.graphics.Color.parseColor(hex) and 0x00FFFFFF or 0x28000000,
                        android.graphics.Color.TRANSPARENT
                    ),
                    null,
                    android.graphics.Shader.TileMode.CLAMP
                )
                nebulaPaint.shader = shader
                nativeCanvas.drawCircle(nx, ny, nRadius, nebulaPaint)
            }

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

            nativeCanvas.drawText("NUCLEO NEXUS", cx, cy - sunR - 10f, textPaintName)

            val metricPaint = NativePaint().apply {
                color       = android.graphics.Color.argb(200, 0, 229, 255)
                textAlign   = NativePaint.Align.CENTER
                isAntiAlias = true
                textSize    = textPaintDesc.textSize * 0.9f
            }
            nativeCanvas.drawText(
                "CPU ${metrics.cpuPercent}%   FPS ${metrics.fpsCurrent}   GPU ${metrics.gpuPercent}%",
                cx, cy + sunR + 26f, metricPaint
            )

            planetHitAreas.clear()
            SolarSystem.PLANETS.forEach { planet ->
                val angle = t * planet.orbitSpeed
                val rx    = planet.orbitRadius * scale
                val ry    = rx * 0.40f
                val px    = cx + cos(angle) * rx
                val py    = cy + sin(angle) * ry
                val pr    = planet.size * scale

                planetHitAreas.add(Triple(planet.id, px, py))

                drawCircle(color = planet.color.copy(alpha = 0.18f), radius = pr * 2.8f, center = Offset(px, py))
                drawCircle(color = planet.color,                      radius = pr,         center = Offset(px, py))
                drawCircle(color = Color.White.copy(alpha = 0.15f),   radius = pr * 0.5f,  center = Offset(px - pr * 0.2f, py - pr * 0.2f))
                drawOval(
                    color   = planet.color.copy(alpha = 0.3f),
                    topLeft = Offset(px - pr * 1.6f, py - pr * 0.22f),
                    size    = Size(pr * 3.2f, pr * 0.44f),
                    style   = Stroke(width = 1.2f)
                )

                textPaintName.color = planet.color.toArgb()
                nativeCanvas.drawText(planet.name,        px, py - pr - 14f, textPaintName)
                nativeCanvas.drawText(planet.description, px, py + pr + 20f, textPaintDesc)
            }
        }

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
