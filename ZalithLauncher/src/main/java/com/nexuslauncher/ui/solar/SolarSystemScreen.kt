package com.nexuslauncher.ui.solar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.navigation.PlanetId
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary
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

    val planetHitAreas   = remember { mutableStateListOf<Triple<String, Float, Float>>() }
    var selectedPlanet   by remember { mutableStateOf<PlanetNode?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "solar")
    val time by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (Math.PI * 200).toFloat(),
        animationSpec = infiniteRepeatable(tween(200_000, easing = LinearEasing), RepeatMode.Restart),
        label         = "time"
    )

    // Sun pulse
    val sunPulse by infiniteTransition.animateFloat(
        initialValue  = 0.85f,
        targetValue   = 1.0f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label         = "sun"
    )

    Box(Modifier.fillMaxSize()) {

        // Background gradient
        Box(Modifier.fillMaxSize().background(
            Brush.radialGradient(
                colors = listOf(Color(0xFF060614), Color(0xFF020208)),
                radius = 1200f
            )
        ))

        // Metrics bar at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x99060614))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡ NEXUS", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricPill("FPS", "${metrics.fpsCurrent}", NexusCyan)
                MetricPill("CPU", "${metrics.cpuPercent}%", NexusOrange)
                MetricPill("RAM", "${"%.1f".format(metrics.ramGb)}GB", Color(0xFF00E676))
            }
            Text(tierResult?.tier?.label ?: "—", color = NexusCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        // Solar Canvas - orbits + planets (no text inside Canvas)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val hit = planetHitAreas.firstOrNull { (_, px, py) ->
                            sqrt((tapOffset.x - px) * (tapOffset.x - px) + (tapOffset.y - py) * (tapOffset.y - py)) < 40f
                        }
                        if (hit != null) {
                            val node = SolarSystem.PLANETS.firstOrNull { it.id == hit.first }
                            selectedPlanet = if (selectedPlanet?.id == hit.first) null else node
                            planetIdMap[hit.first]?.let { pid ->
                                if (selectedPlanet == null) onPlanetSelected(pid)
                            }
                        } else {
                            selectedPlanet = null
                        }
                    }
                }
        ) {
            planetHitAreas.clear()
            val cx = size.width / 2f
            val cy = size.height / 2f
            val scale = (size.width / 1200f).coerceIn(0.35f, 0.75f)

            // Sun glow
            drawCircle(
                brush  = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD700).copy(0.5f * sunPulse), Color.Transparent),
                    center = Offset(cx, cy), radius = SolarSystem.SUN.size * scale * 3f
                ),
                radius = SolarSystem.SUN.size * scale * 3f, center = Offset(cx, cy)
            )
            drawCircle(color = Color(0xFFFFB300), radius = SolarSystem.SUN.size * scale, center = Offset(cx, cy))

            // Planets
            SolarSystem.PLANETS.forEachIndexed { idx, planet ->
                val r   = planet.orbitRadius * scale
                val ang = time * planet.orbitSpeed + idx * 0.58f
                val px  = cx + r * cos(ang)
                val py  = cy + r * sin(ang)
                val sz  = planet.size * scale
                val isSelected = selectedPlanet?.id == planet.id

                // Orbit ring
                drawCircle(
                    color  = planet.color.copy(if (isSelected) 0.3f else 0.08f),
                    radius = r, center = Offset(cx, cy),
                    style  = Stroke(width = if (isSelected) 1.5f else 0.5f)
                )

                // Planet glow
                if (isSelected) {
                    drawCircle(
                        brush  = Brush.radialGradient(
                            colors = listOf(planet.color.copy(0.35f), Color.Transparent),
                            center = Offset(px, py), radius = sz * 2.5f
                        ),
                        radius = sz * 2.5f, center = Offset(px, py)
                    )
                }

                // Planet body
                drawCircle(color = planet.color, radius = sz, center = Offset(px, py))
                drawCircle(color = planet.color.copy(0.3f), radius = sz * 0.6f, center = Offset(px, py))

                // Register hit area
                planetHitAreas.add(Triple(planet.id, px, py))
            }
        }

        // Selected planet info panel (Compose overlay, NOT Canvas text)
        selectedPlanet?.let { planet ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xEE0A0A18))
                        .border(1.dp, planet.color.copy(0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(planet.name, color = planet.color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                            Text(planet.description, color = TextSecondary, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(planet.color.copy(0.2f))
                                .border(1.dp, planet.color.copy(0.6f), RoundedCornerShape(8.dp))
                                .clickable {
                                    planetIdMap[planet.id]?.let { onPlanetSelected(it) }
                                    selectedPlanet = null
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("ENTRAR →", color = planet.color, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    if (planet.moons.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            planet.moons.forEach { moon ->
                                Box(
                                    Modifier.clip(RoundedCornerShape(6.dp))
                                        .background(planet.color.copy(0.1f))
                                        .border(1.dp, planet.color.copy(0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(moon, color = planet.color.copy(0.8f), fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Planet labels overlay (always visible, no canvas text)
        // Only show label for the current approximate position of close planets
        // We do this in a separate pass using remember for positions
    }
}

@Composable
private fun MetricPill(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.1f))
            .border(1.dp, color.copy(0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(label, color = TextSecondary, fontSize = 9.sp)
        Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}