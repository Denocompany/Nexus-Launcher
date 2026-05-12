package com.nexuslauncher.ui.solar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslauncher.core.NexusBoostEngine
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.render.NexusBackground3D
import com.nexuslauncher.ui.AccountsScreen
import com.nexuslauncher.ui.HomeScreen
import com.nexuslauncher.ui.InstancesScreen
import com.nexuslauncher.ui.ModsScreen
import com.nexuslauncher.ui.PerformanceScreen
import com.nexuslauncher.ui.ReportsScreen
import com.nexuslauncher.ui.SettingsScreen
import com.nexuslauncher.ui.VisualScreen
import kotlin.math.cos
import kotlin.math.sin

/**
 * SolarSystemScreen — Tela principal do Nexus Launcher (Fase 3).
 *
 * Navegação baseada em planetas:
 *  • Toque em um planeta → painel lateral com métricas e Nexus Boost
 *  • "ENTRAR" → abre a tela correspondente em overlay sobre o fundo 3D
 *  • Botão Voltar → retorna ao Sistema Solar
 *
 * Mapeamento planeta → tela:
 *  nexus       → HomeScreen
 *  aetherion   → PerformanceScreen
 *  lumina      → VisualScreen
 *  modara      → ModsScreen
 *  curseforge  → ModsScreen
 *  instarrion  → InstancesScreen
 *  chronos     → ReportsScreen
 *  cloudnexus  → ReportsScreen
 *  persona     → AccountsScreen
 *  labx        → SettingsScreen
 *  helios      → SettingsScreen
 */
@Composable
fun SolarSystemScreen(vm: SolarSystemViewModel = viewModel()) {
    val selectedPlanet by vm.selectedPlanet.collectAsState()
    val metrics        by vm.systemMetrics.collectAsState()
    val tierResult     by vm.tierResult.collectAsState()
    val boostReport    by vm.boostReport.collectAsState()
    val glowIntensity  = vm.planetGlowIntensity()

    // Tracks which planet's full screen is currently open
    var currentScreen by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "solar")
    val time by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (Math.PI * 200).toFloat(),
        animationSpec = infiniteRepeatable(tween(200_000, easing = LinearEasing), RepeatMode.Restart),
        label         = "time"
    )

    Box(Modifier.fillMaxSize()) {

        // ── Animated 3D Background (always visible, behind all content) ─
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory  = { context -> NexusBackground3D(context) },
            update   = { view  -> view.updateTier(tierResult) }
        )

        if (currentScreen != null) {

            // ── Full-screen planet content ─────────────────────────────
            Box(Modifier.fillMaxSize().background(Color(0xF005050A))) {
                when (currentScreen) {
                    "nexus"      -> HomeScreen(
                        instanceName = "Survival 1.18.1",
                        metrics      = metrics
                    )
                    "aetherion"  -> PerformanceScreen(
                        metrics     = metrics,
                        boostReport = boostReport,
                        onBoost     = { vm.triggerBoost() }
                    )
                    "lumina"     -> VisualScreen()
                    "modara",
                    "curseforge" -> ModsScreen()
                    "instarrion" -> InstancesScreen()
                    "chronos",
                    "cloudnexus" -> ReportsScreen()
                    "persona"    -> AccountsScreen()
                    "labx",
                    "helios"     -> SettingsScreen()
                    else         -> HomeScreen(instanceName = "Survival 1.18.1", metrics = metrics)
                }

                // Back to solar system
                IconButton(
                    onClick  = { currentScreen = null; vm.selectPlanet(null) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.ArrowBack,
                        contentDescription = "Voltar ao Sistema Solar",
                        tint               = Color(0xFF00E5FF)
                    )
                }
            }

        } else {

            // ── Solar System Canvas ────────────────────────────────────
            Canvas(Modifier.fillMaxSize().clickable { vm.selectPlanet(null) }) {
                val cx    = size.width  / 2f
                val cy    = size.height / 2f
                val scale = minOf(size.width, size.height) / 1200f

                // Orbital rings
                SolarSystem.PLANETS.forEach { planet ->
                    val rx = planet.orbitRadius * scale
                    val ry = rx * 0.38f
                    val sel = selectedPlanet == planet.id
                    drawOval(
                        color   = if (sel) planet.color.copy(alpha = 0.7f)
                                  else     planet.color.copy(alpha = 0.15f),
                        topLeft = Offset(cx - rx, cy - ry),
                        size    = Size(rx * 2f, ry * 2f),
                        style   = Stroke(width = if (sel) 1.5f else 0.8f)
                    )
                }

                // Sun (pulsating + corona)
                val t        = time * 0.001f
                val sunPulse = 1f + sin(t * 2f) * 0.05f
                val sunR     = 40f * scale * sunPulse
                repeat(3) { i ->
                    drawCircle(
                        color  = Color(0xFFFF8F00).copy(alpha = 0.05f * (3 - i)),
                        radius = sunR * (1f + (i + 1) * 0.4f),
                        center = Offset(cx, cy)
                    )
                }
                drawCircle(color = Color(0xFFFFB300), radius = sunR, center = Offset(cx, cy))

                // Planets
                SolarSystem.PLANETS.forEach { planet ->
                    val angle = t * planet.orbitSpeed
                    val rx    = planet.orbitRadius * scale
                    val ry    = rx * 0.38f
                    val px    = (cx + cos(angle) * rx).toFloat()
                    val py    = (cy + sin(angle) * ry).toFloat()
                    val pr    = planet.size * scale
                    val sel   = selectedPlanet == planet.id

                    // Glow halo
                    drawCircle(
                        color  = planet.color.copy(alpha = glowIntensity * if (sel) 0.6f else 0.2f),
                        radius = pr * 2.5f,
                        center = Offset(px, py)
                    )
                    // Planet body
                    drawCircle(
                        color  = planet.color,
                        radius = pr * if (sel) 1.3f else 1f,
                        center = Offset(px, py)
                    )
                    // Selection ring
                    if (sel) {
                        val pulse = 1f + sin(t * 3f) * 0.15f
                        drawCircle(
                            color  = planet.color.copy(alpha = 0.5f),
                            radius = pr * 1.8f * pulse,
                            center = Offset(px, py),
                            style  = Stroke(width = 1.5f)
                        )
                    }
                }
            }

            // ── Top HUD ────────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Color(0xFF0A0A20).copy(alpha = 0.85f))
                    .padding(horizontal = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "⚡ NEXUS LAUNCHER",
                    color         = Color(0xFF00E5FF),
                    fontWeight    = FontWeight.Black,
                    fontSize      = 14.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = selectedPlanet?.let { id ->
                        SolarSystem.PLANETS.find { it.id == id }?.let { "🪐 ${it.name}" }
                    } ?: "☀ Home",
                    color    = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Text(
                    "FPS ${metrics.fpsCurrent} | CPU ${metrics.cpuPercent}% | RAM ${String.format("%.1f", metrics.ramGb)}GB",
                    color      = Color(0xFF00E5FF).copy(alpha = 0.8f),
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // ── Planet Side Panel (animated) ────────────────────────────
            AnimatedVisibility(
                visible  = selectedPlanet != null,
                enter    = slideInHorizontally { it } + fadeIn(),
                exit     = slideOutHorizontally { it } + fadeOut(),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                selectedPlanet?.let { id ->
                    SolarSystem.PLANETS.find { it.id == id }?.let { planet ->
                        PlanetSidePanel(
                            planet      = planet,
                            vm          = vm,
                            metrics     = metrics,
                            boostReport = boostReport,
                            onClose     = { vm.selectPlanet(null) },
                            onEnter     = { currentScreen = planet.id }
                        )
                    }
                }
            }

            // ── Bottom HUD ─────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFF0A0A20).copy(alpha = 0.85f))
                    .padding(horizontal = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    SolarSystem.PLANETS.forEach { planet ->
                        val sel = selectedPlanet == planet.id
                        Box(
                            Modifier
                                .size(if (sel) 12.dp else 8.dp)
                                .background(
                                    if (sel) planet.color else planet.color.copy(alpha = 0.4f),
                                    RoundedCornerShape(50)
                                )
                                .clickable { vm.selectPlanet(planet.id) }
                        )
                    }
                }
                tierResult?.let {
                    Text(
                        "${it.tier.label} | v1.0.0-nexus",
                        color    = Color.White.copy(alpha = 0.4f),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

// ── Planet Side Panel ──────────────────────────────────────────────────

@Composable
private fun PlanetSidePanel(
    planet:      PlanetNode,
    vm:          SolarSystemViewModel,
    metrics:     NexusSystemMonitor.SystemMetrics,
    boostReport: NexusBoostEngine.BoostReport,
    onClose:     () -> Unit,
    onEnter:     () -> Unit
) {
    val selectedMoon by vm.selectedMoon.collectAsState()

    Surface(
        modifier  = Modifier
            .fillMaxHeight()
            .width(340.dp),
        color     = Color(0xFF0D0D20).copy(alpha = 0.92f),
        shape     = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
        elevation = 8.dp
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // ── Header ────────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(planet.color.copy(alpha = 0.12f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        planet.name,
                        color         = planet.color,
                        fontWeight    = FontWeight.Black,
                        fontSize      = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        planet.description,
                        color    = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
                TextButton(onClick = onClose) {
                    Text("✕", color = Color.White.copy(alpha = 0.6f))
                }
            }

            Divider(color = planet.color.copy(alpha = 0.2f))

            // ── Moons (sub-modules) ────────────────────────────────────
            if (planet.moons.isNotEmpty()) {
                Text(
                    "MÓDULOS",
                    color         = Color.White.copy(alpha = 0.4f),
                    fontSize      = 9.sp,
                    letterSpacing = 2.sp,
                    modifier      = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    planet.moons.take(4).forEach { moon ->
                        val active = selectedMoon == moon
                        OutlinedButton(
                            onClick        = { vm.selectMoon(if (active) null else moon) },
                            modifier       = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            colors         = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (active) planet.color else Color.White.copy(alpha = 0.4f)
                            )
                        ) {
                            Text(moon, fontSize = 9.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Live metrics ───────────────────────────────────────────
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                MetricRow("FPS", "${metrics.fpsCurrent}", planet.color)
                MetricRow("CPU", "${metrics.cpuPercent}%", planet.color)
                MetricRow("GPU", "${metrics.gpuPercent}%", planet.color)
                MetricRow(
                    "RAM",
                    "${String.format("%.1f", metrics.ramGb)} / ${String.format("%.1f", metrics.ramTotalGb)} GB",
                    planet.color
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Enter screen button ────────────────────────────────────
            Button(
                onClick  = onEnter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp),
                colors   = ButtonDefaults.buttonColors(backgroundColor = planet.color)
            ) {
                Text(
                    "ENTRAR EM ${planet.name}",
                    fontWeight    = FontWeight.Bold,
                    fontSize      = 11.sp,
                    color         = Color(0xFF05050A),
                    letterSpacing = 0.5.sp
                )
            }

            // ── Nexus Boost (Aetherion only) ───────────────────────────
            if (planet.id == "aetherion") {
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick  = { vm.triggerBoost() },
                    enabled  = boostReport.state != NexusBoostEngine.BoostState.RUNNING,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6D00))
                ) {
                    Text(
                        when (boostReport.state) {
                            NexusBoostEngine.BoostState.RUNNING -> "⚡ Otimizando..."
                            NexusBoostEngine.BoostState.DONE    -> "✓ +${boostReport.estimatedGainPct}% aplicado"
                            else                                -> "⚡ NEXUS BOOST"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp
                    )
                }
                if (boostReport.state == NexusBoostEngine.BoostState.DONE) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
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

// ── Metric row helper ──────────────────────────────────────────────────

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
