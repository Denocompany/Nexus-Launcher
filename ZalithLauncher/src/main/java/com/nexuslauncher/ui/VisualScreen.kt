package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

@Composable
fun VisualScreen() {
    var shadersEnabled  by remember { mutableStateOf(true) }
    var bloomEnabled    by remember { mutableStateOf(false) }
    var particlesEnabled by remember { mutableStateOf(true) }
    var brightnessSlider by remember { mutableStateOf(0.7f) }
    var saturationSlider by remember { mutableStateOf(0.5f) }
    var selectedTheme   by remember { mutableStateOf("Nexus Dark") }

    val themes = listOf("Nexus Dark", "Deep Space", "Aetherion", "Lumina")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("VISUAL", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("Tema · Shaders · Animações · Acessibilidade", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(20.dp))

        // Tema
        VisualSection("🎨 Tema do Launcher") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                themes.forEach { theme ->
                    val sel = theme == selectedTheme
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sel) NexusCyan.copy(0.15f) else Color(0xFF111120))
                            .border(1.dp, if (sel) NexusCyan else Color(0xFF333345), RoundedCornerShape(8.dp))
                            .clickable { selectedTheme = theme }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(theme, color = if (sel) NexusCyan else TextSecondary, fontSize = 11.sp,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Shaders e efeitos
        VisualSection("✨ Shaders & Efeitos") {
            ToggleRow("Shaders HDR", shadersEnabled)    { shadersEnabled    = it }
            Spacer(Modifier.height(4.dp))
            ToggleRow("Bloom",        bloomEnabled)     { bloomEnabled      = it }
            Spacer(Modifier.height(4.dp))
            ToggleRow("Partículas",   particlesEnabled) { particlesEnabled  = it }
        }

        Spacer(Modifier.height(12.dp))

        // Ajustes de tela
        VisualSection("🖥 Ajustes de Tela") {
            Text("Brilho", color = TextSecondary, fontSize = 11.sp)
            Slider(
                value = brightnessSlider,
                onValueChange = { brightnessSlider = it },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(thumbColor = NexusCyan, activeTrackColor = NexusCyan)
            )
            Text("${(brightnessSlider * 100).toInt()}%", color = NexusCyan, fontSize = 11.sp)

            Spacer(Modifier.height(8.dp))

            Text("Saturação", color = TextSecondary, fontSize = 11.sp)
            Slider(
                value = saturationSlider,
                onValueChange = { saturationSlider = it },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(thumbColor = Color(0xFF7B61FF), activeTrackColor = Color(0xFF7B61FF))
            )
            Text("${(saturationSlider * 100).toInt()}%", color = Color(0xFF7B61FF), fontSize = 11.sp)
        }

        Spacer(Modifier.height(12.dp))

        // Acessibilidade
        VisualSection("♿ Acessibilidade") {
            Text("• Modo Alto Contraste", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text("• Reduzir Animações", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text("• Aumentar Texto UI", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Text("(Configurações de acessibilidade — Fase 4)", color = TextSecondary.copy(0.4f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun VisualSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Obsidian)
            .padding(14.dp)
    ) {
        Text(title, color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 13.sp)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(0.4f))
        )
    }
}
