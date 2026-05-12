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
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * VisualScreen — LUMINA (Fase 4 / NavHost).
 * Callbacks explícitos: onOpenSettings, onOpenModsForShaders, onBackToSolar.
 */
@Composable
fun VisualScreen(
    onOpenSettings      : () -> Unit = {},
    onOpenModsForShaders: () -> Unit = {},
    onBackToSolar       : () -> Unit = {}
) {
    var selectedPreset   by remember { mutableStateOf(2) }
    val presets = listOf("Ultra", "Alto", "Médio", "Baixo")
    val presetColors = listOf(Color(0xFFFF5252), NexusCyan, Color(0xFF00E676), TextSecondary)
    var renderDistance   by remember { mutableStateOf(0.5f) }
    var mipmapLevel      by remember { mutableStateOf(2) }
    var particlesEnabled by remember { mutableStateOf(true) }
    var entityShadows    by remember { mutableStateOf(true) }
    var smoothLighting   by remember { mutableStateOf(true) }
    var shadersEnabled   by remember { mutableStateOf(true) }
    var bloomEnabled     by remember { mutableStateOf(false) }
    var hdrEnabled       by remember { mutableStateOf(false) }
    var ambientOcclusion by remember { mutableStateOf(true) }
    var selectedShader   by remember { mutableStateOf(0) }
    val shaders = listOf("Sem shader (Vanilla)", "SFLP Shaders", "Complementary Reimagined", "BSL Shaders")
    var brightnessSlider by remember { mutableStateOf(0.7f) }
    var saturationSlider by remember { mutableStateOf(0.5f) }
    var contrastSlider   by remember { mutableStateOf(0.5f) }
    var selectedTheme    by remember { mutableStateOf("Nexus Dark") }
    val themes = listOf("Nexus Dark", "Deep Space", "Aetherion", "Lumina")
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Qualidade Gráfica", "Shaders & HDR", "Tela", "Tema")

    Column(modifier = Modifier.fillMaxSize().background(DeepVoid).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("VISUAL", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("LUMINA · Qualidade · Shaders · Animações · Tema", color = TextSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (selectedTab == i) NexusCyan.copy(0.15f) else Color(0xFF111120)).border(1.dp, if (selectedTab == i) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp)).clickable { selectedTab = i }.padding(horizontal = 12.dp, vertical = 7.dp)
                ) { Text(tab, color = if (selectedTab == i) NexusCyan else TextSecondary, fontSize = 10.sp, fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal) }
            }
        }
        Spacer(Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                VisualSection("🎮 Preset de Qualidade") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.forEachIndexed { i, preset ->
                            val sel = selectedPreset == i; val color = presetColors[i]
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (sel) color.copy(0.2f) else Color(0xFF111120)).border(2.dp, if (sel) color else Color(0xFF222230), RoundedCornerShape(8.dp)).clickable { selectedPreset = i }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (i == 0) "🔥" else if (i == 1) "⚡" else if (i == 2) "✅" else "🔋", fontSize = 14.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(preset, color = if (sel) color else TextSecondary, fontSize = 10.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                VisualSection("🌍 Renderização") {
                    Text("Distância de renderização: ${(renderDistance * 32 + 4).toInt()} chunks", color = TextSecondary, fontSize = 11.sp)
                    Slider(value = renderDistance, onValueChange = { renderDistance = it }, modifier = Modifier.fillMaxWidth(), colors = SliderDefaults.colors(thumbColor = NexusCyan, activeTrackColor = NexusCyan))
                    Spacer(Modifier.height(8.dp))
                    Text("Nível de Mipmap", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (0..4).forEach { lvl ->
                            val sel = mipmapLevel == lvl
                            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(if (sel) NexusCyan.copy(0.2f) else Color(0xFF111120)).border(1.dp, if (sel) NexusCyan else TextSecondary.copy(0.3f), RoundedCornerShape(6.dp)).clickable { mipmapLevel = lvl }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text("$lvl", color = if (sel) NexusCyan else TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                VisualSection("✨ Efeitos") {
                    ToggleRow("Partículas",           particlesEnabled) { particlesEnabled = it }
                    Spacer(Modifier.height(4.dp))
                    ToggleRow("Sombras de entidades", entityShadows)    { entityShadows    = it }
                    Spacer(Modifier.height(4.dp))
                    ToggleRow("Iluminação suave",     smoothLighting)   { smoothLighting   = it }
                }
            }
            1 -> {
                VisualSection("🌟 Shaders") {
                    ToggleRow("Ativar Shaders", shadersEnabled) { shadersEnabled = it }
                    if (shadersEnabled) {
                        Spacer(Modifier.height(12.dp))
                        Text("Shader ativo", color = TextSecondary, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        shaders.forEachIndexed { i, shader ->
                            val sel = selectedShader == i
                            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(if (sel) NexusCyan.copy(0.1f) else Color.Transparent).border(1.dp, if (sel) NexusCyan else Color(0xFF1A1A28), RoundedCornerShape(6.dp)).clickable { selectedShader = i }.padding(horizontal = 12.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(shader, color = if (sel) NexusCyan else Color.White, fontSize = 12.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                if (sel) Text("✓", color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                VisualSection("🎨 Efeitos Avançados") {
                    ToggleRow("Bloom",             bloomEnabled)     { bloomEnabled      = it }
                    Spacer(Modifier.height(4.dp))
                    ToggleRow("HDR",               hdrEnabled)       { hdrEnabled        = it }
                    Spacer(Modifier.height(4.dp))
                    ToggleRow("Ambient Occlusion", ambientOcclusion) { ambientOcclusion  = it }
                    if (hdrEnabled) {
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(NexusCyan.copy(0.06f)).border(1.dp, NexusCyan.copy(0.2f), RoundedCornerShape(6.dp)).padding(10.dp)) {
                            Text("HDR ativo — melhor qualidade visual em telas OLED.", color = NexusCyan, fontSize = 11.sp)
                        }
                    }
                }
            }
            2 -> {
                VisualSection("🖥 Ajustes de Tela") {
                    Text("Brilho: ${(brightnessSlider * 100).toInt()}%", color = TextSecondary, fontSize = 11.sp)
                    Slider(value = brightnessSlider, onValueChange = { brightnessSlider = it }, modifier = Modifier.fillMaxWidth(), colors = SliderDefaults.colors(thumbColor = NexusCyan, activeTrackColor = NexusCyan))
                    Spacer(Modifier.height(8.dp))
                    Text("Saturação: ${(saturationSlider * 100).toInt()}%", color = TextSecondary, fontSize = 11.sp)
                    Slider(value = saturationSlider, onValueChange = { saturationSlider = it }, modifier = Modifier.fillMaxWidth(), colors = SliderDefaults.colors(thumbColor = Color(0xFF7B61FF), activeTrackColor = Color(0xFF7B61FF)))
                    Spacer(Modifier.height(8.dp))
                    Text("Contraste: ${(contrastSlider * 100).toInt()}%", color = TextSecondary, fontSize = 11.sp)
                    Slider(value = contrastSlider, onValueChange = { contrastSlider = it }, modifier = Modifier.fillMaxWidth(), colors = SliderDefaults.colors(thumbColor = NexusOrange, activeTrackColor = NexusOrange))
                }
                Spacer(Modifier.height(10.dp))
                VisualSection("♿ Acessibilidade") {
                    listOf("Modo Alto Contraste", "Reduzir Animações", "Aumentar Texto UI").forEachIndexed { i, label ->
                        var enabled by remember { mutableStateOf(false) }
                        ToggleRow(label, enabled) { enabled = it }
                        if (i < 2) Spacer(Modifier.height(4.dp))
                    }
                }
            }
            3 -> {
                VisualSection("🎨 Tema do Launcher") {
                    themes.forEachIndexed { i, theme ->
                        val sel = selectedTheme == theme
                        val themeColors = listOf(Color(0xFF0D0A1A), Color(0xFF050520), Color(0xFF0A1020), Color(0xFF1A0A00))
                        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(if (sel) NexusCyan.copy(0.1f) else Color.Transparent).border(1.dp, if (sel) NexusCyan else Color(0xFF1A1A28), RoundedCornerShape(8.dp)).clickable { selectedTheme = theme }.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)).background(themeColors[i]).border(1.dp, NexusCyan.copy(0.3f), RoundedCornerShape(6.dp)))
                                Text(theme, color = if (sel) NexusCyan else Color.White, fontSize = 13.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                            }
                            if (sel) Text("✓", color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))
                VisualSection("🎨 Cor de Destaque") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        var selectedAccent by remember { mutableStateOf(0) }
                        listOf(NexusCyan to "Cyan", NexusOrange to "Laranja", Color(0xFF7B61FF) to "Roxo", Color(0xFF00E676) to "Verde", Color(0xFFFF5252) to "Vermelho").forEachIndexed { i, (color, name) ->
                            Column(modifier = Modifier.weight(1f).clickable { selectedAccent = i }, horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(color).border(if (selectedAccent == i) 2.dp else 0.dp, Color.White, RoundedCornerShape(8.dp)))
                                Spacer(Modifier.height(4.dp))
                                Text(name, color = if (selectedAccent == i) color else TextSecondary, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
        Text(title, color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp)); content()
    }
}
@Composable
private fun ToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(0.4f)))
    }
}
