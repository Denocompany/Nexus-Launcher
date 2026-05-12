package com.nexuslauncher.ui.solar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslauncher.core.NexusBoostEngine
import kotlin.math.cos
import kotlin.math.sin

/**
 * SolarSystemScreen — Tela principal do Nexus Launcher.
 *
 * Renderiza o Sistema Solar 2D em Compose Canvas com:
 *  - Sol animado (pulsação baseada no Tier)
 *  - Planetas em órbita elíptica
 *  - Painel lateral ao selecionar planeta
 *  - HUD superior com métricas em tempo real
 *  - HUD inferior com seletor rápido de planetas
 */
@Composable
fun SolarSystemScreen(vm: SolarSystemViewModel = viewModel()) {
    val selectedPlanet by vm.selectedPlanet.collectAsState()
    val metrics        by vm.systemMetrics.collectAsState()
    val tierResult     by vm.tierResult.collectAsState()
    val boostReport    by vm.boostReport.collectAsState()
    val glowIntensity  = vm.planetGlowIntensity()

    val infiniteTransition = rememberInfiniteTransition(label = "solar")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (Math.PI * 200).toFloat(),
        animationSpec = infiniteRepeatable(tween(200000, easing = LinearEasing), RepeatMode.Restart),
        label = "time"
    )

    Box(Modifier.fillMaxSize().background(Color(0xFF05050A))) {

        // ── Solar System Canvas ──────────────────────────────────────────
        Canvas(Modifier.fillMaxSize().clickable { vm.selectPlanet(null) }) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val scale = minOf(size.width, size.height) / 1200f

            // Orbital rings
            SolarSystem.PLANETS.forEach { planet ->
                val rx = planet.orbitRadius * scale
                val ry = rx * 0.38f
                val isSelected = selectedPlanet == planet.id
                drawOval(
                    color = if (isSelected) planet.color.copy(alpha = 0.7f) else planet.color.copy(alpha = 0.15f),
                    topLeft = Offset(cx - rx, cy - ry),
                    size = androidx.compose.ui.geometry.Size(rx * 2f, ry * 2f),
                    style = Stroke(width = if (isSelected) 1.5f else 0.8f)
                )
            }

            // Sun
            val t = time * 0.001f
            val sunPulse = 1f + sin(t * 2f) * 0.05f
            val sunR = 40f * scale * sunPulse

            // Sun corona
            for (i in 3 downTo 1) {
                drawCircle(
                    color = Color(0xFFFF8F00).copy(alpha = 0.05f * i),
                    radius = sunR * (1f + i * 0.4f),
                    center = Offset(cx, cy)
                )
            }
            drawCircle(
                color = Color(0xFFFFB300),
                radius = sunR,
                center = Offset(cx, cy)
            )

            // Planets
            SolarSystem.PLANETS.forEachIndexed { _, planet ->
                val angle = t * planet.orbitSpeed
                val rx    = planet.orbitRadius * scale
                val ry    = rx * 0.38f
                val px    = cx + cos(angle) * rx
                val py    = cy + sin(angle) * ry
                val pr    = planet.size * scale

                val isSelected = selectedPlanet == planet.id

                // Planet glow
                drawCircle(
                    color = planet.color.copy(alpha = glowIntensity * if (isSelected) 0.6f else 0.2f),
                    radius = pr * 2.5f,
                    center = Offset(px, py)
                )
                // Planet body
                drawCircle(
                    color = planet.color,
                    radius = pr * if (isSelected) 1.3f else 1f,
                    center = Offset(px, py)
                )
                // Selection ring
                if (isSelected) {
                    val pulse = 1f + sin(t * 3f) * 0.15f
                    drawCircle(
                        color = planet.color.copy(alpha = 0.5f),
                        radius = pr * 1.8f * pulse,
                        center = Offset(px, py),
                        style  = Stroke(width = 1.5f)
                    )
                }
            }
        }

        // ── Top HUD ──────────────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0xFF0A0A20).copy(alpha = 0.85f))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("⚡ NEXUS LAUNCHER",
                color = Color(0xFF00E5FF),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )
            Text(
                text = selectedPlanet?.let {
                    SolarSystem.PLANETS.find { p -> p.id == it }?.let { p -> "🪐 ${p.name}" }
                } ?: "☀️ Home",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
            Text(
                "FPS ${metrics.fpsCurrent} | CPU ${metrics.cpuPercent}% | RAM ${String.format("%.1f", metrics.ramGb)}GB",
                color = Color(0xFF00E5FF).copy(alpha = 0.8f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // ── Side Panel ───────────────────────────────────────────────────
        AnimatedVisibility(
            visible = selectedPlanet != null,
            enter   = slideInHorizontally { it } + fadeIn(),
            exit    = slideOutHorizontally { it } + fadeOut(),
            modifier= Modifier.align(Alignment.CenterEnd)
        ) {
            selectedPlanet?.let { id ->
                val planet = SolarSystem.PLANETS.find { it.id == id }
                if (planet != null) {
                    PlanetSidePanel(
                        planet      = planet,
                        vm          = vm,
                        metrics     = metrics,
                        boostReport = boostReport,
                        onClose     = { vm.selectPlanet(null) }
                    )
                }
            }
        }

        // ── Bottom HUD ───────────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .height(44.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF0A0A20).copy(alpha = 0.85f))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SolarSystem.PLANETS.forEach { planet ->
                    val isSelected = selectedPlanet == planet.id
                    Box(
                        Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .background(
                                if (isSelected) planet.color else planet.color.copy(alpha = 0.4f),
                                RoundedCornerShape(50)
                            )
                            .clickable { vm.selectPlanet(planet.id) }
                    )
                }
            }
            tierResult?.let {
                Text("${it.tier.label} | v1.4.1",
                    color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun PlanetSidePanel(
    planet: PlanetNode,
    vm: SolarSystemViewModel,
    metrics: NexusSystemMonitor.SystemMetrics,
    boostReport: NexusBoostEngine.BoostReport,
    onClose: () -> Unit
) {
    val selectedMoon by vm.selectedMoon.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(340.dp),
        color    = Color(0xFF0D0D20).copy(alpha = 0.92f),
        shape    = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
        tonalElevation = 8.dp
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Header
            Row(
                Modifier.fillMaxWidth()
                    .background(planet.color.copy(alpha = 0.12f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(planet.name,
                        color = planet.color,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp)
                    Text(planet.description,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp)
                }
                TextButton(onClick = onClose) {
                    Text("✕", color = Color.White.copy(alpha = 0.6f))
                }
            }

            Divider(color = planet.color.copy(alpha = 0.2f))

            // Moons
            if (planet.moons.isNotEmpty()) {
                Text("MÓDULOS",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp))
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    planet.moons.forEach { moon ->
                        val isActive = selectedMoon == moon
                        FilterChip(
                            selected = isActive,
                            onClick  = { vm.selectMoon(if (isActive) null else moon) },
                            label    = { Text(moon, fontSize = 9.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = planet.color.copy(alpha = 0.25f),
                                selectedLabelColor = planet.color
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Live metrics card
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                MetricRow("FPS",  "${metrics.fpsCurrent}", planet.color)
                MetricRow("CPU",  "${metrics.cpuPercent}%", planet.color)
                MetricRow("GPU",  "${metrics.gpuPercent}%", planet.color)
                MetricRow("RAM",  "${String.format("%.1f", metrics.ramGb)} / ${String.format("%.1f", metrics.ramTotalGb)} GB", planet.color)
            }

            Spacer(Modifier.height(8.dp))

            // Boost button for Aetherion
            if (planet.id == "aetherion") {
                Button(
                    onClick   = { vm.triggerBoost() },
                    enabled   = boostReport.state != NexusBoostEngine.BoostState.RUNNING,
                    modifier  = Modifier.fillMaxWidth().padding(16.dp).height(48.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
                ) {
                    Text(
                        when (boostReport.state) {
                            NexusBoostEngine.BoostState.RUNNING -> "⚡ Otimizando..."
                            NexusBoostEngine.BoostState.DONE    -> "✓ OTIMIZADO +${boostReport.estimatedGainPct}%"
                            else                                -> "⚡ NEXUS BOOST"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (boostReport.state == NexusBoostEngine.BoostState.DONE) {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        boostReport.stepsDone.forEach { step ->
                            Text(step, color = Color(0xFF00E676), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, accentColor: Color) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
        Text(value, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
