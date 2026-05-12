package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
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

@Composable
fun SettingsScreen(onNavigateTo: (String) -> Unit = {}) {

    // ── Jogo ────────────────────────────────────────────────────────────
    var language          by remember { mutableStateOf("Português (Brasil)") }
    var autoSave          by remember { mutableStateOf(true) }
    var crashReport       by remember { mutableStateOf(true) }

    // ── Vídeo ────────────────────────────────────────────────────────────
    var fullscreen        by remember { mutableStateOf(false) }
    var vsync             by remember { mutableStateOf(true) }
    var selectedRes       by remember { mutableStateOf(0) }
    val resolutions = listOf("1280x720", "1920x1080", "2560x1440", "Nativa do dispositivo")

    // ── Controles ────────────────────────────────────────────────────────
    var touchSensitivity  by remember { mutableStateOf(1) }
    val sensitivityLabels = listOf("Baixa", "Média", "Alta", "Máxima")
    var hapticFeedback    by remember { mutableStateOf(true) }
    var gamepadSupport    by remember { mutableStateOf(false) }

    // ── Launcher ─────────────────────────────────────────────────────────
    var bgLoad            by remember { mutableStateOf(true) }
    var autoUpdate        by remember { mutableStateOf(true) }
    var analytics         by remember { mutableStateOf(false) }
    var selectedTheme     by remember { mutableStateOf(0) }
    val themes = listOf("Nexus Dark", "Deep Space", "Aetherion")

    // ── Experimental ─────────────────────────────────────────────────────
    var nexusAI           by remember { mutableStateOf(false) }
    var predictiveBoost   by remember { mutableStateOf(false) }
    var gpuOverclock      by remember { mutableStateOf(false) }
    var betaFeatures      by remember { mutableStateOf(false) }

    // ── Java / Args ───────────────────────────────────────────────────────
    var jvmArgs           by remember { mutableStateOf("-XX:+UseG1GC -Xmx4G") }

    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Jogo", "Vídeo", "Controles", "Launcher", "Experimental", "Arquivos", "Sobre")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("CONFIGURAÇÕES", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("HELIOS CONTROL · Todas as seções do launcher", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(16.dp))

        // ── Tabs ─────────────────────────────────────────────────────────
        // Linha 1 de tabs
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tabs.take(4).forEachIndexed { i, tab ->
                TabPill(tab, selectedTab == i) { selectedTab = i }
            }
        }
        Spacer(Modifier.height(6.dp))
        // Linha 2 de tabs
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tabs.drop(4).forEachIndexed { i, tab ->
                TabPill(tab, selectedTab == i + 4) { selectedTab = i + 4 }
            }
        }

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {

            // ── Aba 0: Jogo ──────────────────────────────────────────────
            0 -> {
                SettingsSection("🎮 Opções Gerais") {
                    // Idioma
                    SettingsRow("Idioma", language)
                    Divider(color = Color(0xFF1A1A28))
                    Spacer(Modifier.height(8.dp))
                    Text("Idioma", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Português (Brasil)", "English (US)", "Español").forEachIndexed { i, lang ->
                            val sel = language == lang
                            Box(
                                Modifier.clip(RoundedCornerShape(6.dp)).background(if (sel) NexusCyan.copy(0.15f) else Color(0xFF111120)).border(1.dp, if (sel) NexusCyan else TextSecondary.copy(0.3f), RoundedCornerShape(6.dp)).clickable { language = lang }.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) { Text(lang, color = if (sel) NexusCyan else TextSecondary, fontSize = 9.sp) }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("⚙ Comportamento") {
                    ToggleSettingsRow("Salvar automaticamente", autoSave)      { autoSave    = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("Enviar relatório de crash", crashReport) { crashReport = it }
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("☕ Java") {
                    SettingsRow("Versão Java",  "Java 17 (OpenJDK)")
                    Divider(color = Color(0xFF1A1A28))
                    SettingsRow("RAM Mínima",  "512 MB")
                    Divider(color = Color(0xFF1A1A28))
                    SettingsRow("RAM Máxima",  "4096 MB")
                    Divider(color = Color(0xFF1A1A28))
                    SettingsRow("Args JVM",    jvmArgs)
                }
            }

            // ── Aba 1: Vídeo ─────────────────────────────────────────────
            1 -> {
                SettingsSection("🖥 Resolução & Modo") {
                    ToggleSettingsRow("Tela cheia", fullscreen) { fullscreen = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("V-Sync", vsync) { vsync = it }
                    Spacer(Modifier.height(10.dp))
                    Text("Resolução", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(6.dp))
                    resolutions.forEachIndexed { i, res ->
                        val sel = selectedRes == i
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(if (sel) NexusCyan.copy(0.1f) else Color.Transparent).border(1.dp, if (sel) NexusCyan else Color(0xFF1A1A28), RoundedCornerShape(6.dp)).clickable { selectedRes = i }.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(res, color = if (sel) NexusCyan else Color.White, fontSize = 12.sp)
                            if (sel) Text("✓", color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("🎨 Visual do Launcher") {
                    Button(
                        onClick  = { onNavigateTo("lumina") },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) { Text("🖥 Abrir Configurações Visuais (LUMINA)", color = NexusCyan, fontSize = 12.sp) }
                }
            }

            // ── Aba 2: Controles ─────────────────────────────────────────
            2 -> {
                SettingsSection("👆 Toque & Sensibilidade") {
                    Text("Sensibilidade de toque", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sensitivityLabels.forEachIndexed { i, label ->
                            val sel = touchSensitivity == i
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(if (sel) NexusCyan.copy(0.15f) else Color(0xFF111120)).border(1.dp, if (sel) NexusCyan else TextSecondary.copy(0.3f), RoundedCornerShape(6.dp)).clickable { touchSensitivity = i }.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) { Text(label, color = if (sel) NexusCyan else TextSecondary, fontSize = 10.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    ToggleSettingsRow("Feedback háptico (vibração)", hapticFeedback) { hapticFeedback = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("Suporte a controle gamepad", gamepadSupport) { gamepadSupport = it }
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("⌨ Mapeamento de Teclas") {
                    listOf("Atacar" to "Toque simples", "Usar item" to "Toque longo", "Agachar" to "Botão esquerdo", "Pular" to "Botão direito", "Inventário" to "Deslizar cima").forEach { (action, key) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(action, color = TextSecondary, fontSize = 12.sp)
                            Text(key, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Divider(color = Color(0xFF1A1A28))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Personalização de teclas — Fase 5", color = TextSecondary.copy(0.4f), fontSize = 10.sp)
                }
            }

            // ── Aba 3: Launcher ──────────────────────────────────────────
            3 -> {
                SettingsSection("🚀 Inicialização") {
                    ToggleSettingsRow("Carregar em background", bgLoad)             { bgLoad      = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("Atualizações automáticas", autoUpdate)       { autoUpdate  = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("Enviar telemetria anônima", analytics)       { analytics   = it }
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("🎨 Tema do Launcher") {
                    themes.forEachIndexed { i, theme ->
                        val sel = selectedTheme == i
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(if (sel) NexusCyan.copy(0.1f) else Color.Transparent).clickable { selectedTheme = i }.padding(horizontal = 8.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(theme, color = if (sel) NexusCyan else Color.White, fontSize = 13.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                            if (sel) Text("✓", color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (i < themes.size - 1) Divider(color = Color(0xFF1A1A28))
                    }
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("🔧 Ferramentas") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick  = {},
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                        ) { Text("Debug do Launcher", color = NexusCyan, fontSize = 11.sp) }
                        Button(
                            onClick  = { onNavigateTo("chronos") },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                        ) { Text("📊 Ver Relatórios", color = NexusOrange, fontSize = 11.sp) }
                    }
                }
            }

            // ── Aba 4: Experimental ──────────────────────────────────────
            4 -> {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(NexusOrange.copy(0.1f)).border(1.dp, NexusOrange.copy(0.4f), RoundedCornerShape(8.dp)).padding(12.dp)
                ) {
                    Text("⚠ Recursos experimentais podem causar instabilidade. Use com cautela.", color = NexusOrange, fontSize = 11.sp)
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("🧪 Features em Teste") {
                    ToggleSettingsRow("Nexus AI (análise preditiva de lag)", nexusAI)          { nexusAI         = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("Nexus Boost preditivo",               predictiveBoost)  { predictiveBoost = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("GPU Overclock (root necessário)",      gpuOverclock)     { gpuOverclock    = it }
                    Divider(color = Color(0xFF1A1A28))
                    ToggleSettingsRow("Acesso antecipado (Beta)",             betaFeatures)     { betaFeatures    = it }
                }
                if (betaFeatures) {
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF00E676).copy(0.08f)).border(1.dp, Color(0xFF00E676).copy(0.3f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                        Text("✓ Beta ativado. Atualizações de teste serão disponibilizadas automaticamente.", color = Color(0xFF00E676), fontSize = 11.sp)
                    }
                }
            }

            // ── Aba 5: Arquivos ──────────────────────────────────────────
            5 -> {
                SettingsSection("📁 Diretórios") {
                    listOf(
                        "Jogo"          to "/games/minecraft",
                        "Mods"          to "/games/minecraft/mods",
                        "Resource Packs" to "/games/minecraft/resourcepacks",
                        "Saves"         to "/games/minecraft/saves",
                        "Java"          to "/usr/lib/jvm/java-17",
                        "Cache"         to "/data/cache/nexus"
                    ).forEachIndexed { i, (label, path) ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(label, color = TextSecondary, fontSize = 11.sp)
                            Text(path, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        if (i < 5) Divider(color = Color(0xFF1A1A28))
                    }
                }
                Spacer(Modifier.height(12.dp))
                SettingsSection("🧹 Limpeza de Cache") {
                    SettingsRow("Cache do launcher", "128 MB")
                    Divider(color = Color(0xFF1A1A28))
                    SettingsRow("Cache de assets", "256 MB")
                    Divider(color = Color(0xFF1A1A28))
                    SettingsRow("Logs antigos", "48 MB")
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick  = {},
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = NexusOrange.copy(0.15f))
                    ) { Text("🗑 Limpar Cache (432 MB)", color = NexusOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                }
            }

            // ── Aba 6: Sobre ─────────────────────────────────────────────
            6 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚡", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("NEXUS LAUNCHER", color = NexusCyan, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("Versão 1.4.1.5 · Build 42", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(20.dp))
                    Divider(color = Color(0xFF1A1A28))
                    Spacer(Modifier.height(16.dp))
                    listOf(
                        "Desenvolvedor"   to "Nexus Team",
                        "Plataforma"      to "Android 8.0+",
                        "Kotlin"          to "1.9.0",
                        "Jetpack Compose" to "1.5.4",
                        "Base"            to "ZalithLauncher 1.3.x",
                        "Licença"         to "GPL-3.0",
                    ).forEach { (k, v) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(k, color = TextSecondary, fontSize = 12.sp)
                            Text(v, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Divider(color = Color(0xFF1A1A28))
                    Spacer(Modifier.height(16.dp))
                    Text("© 2025 Nexus Team. Todos os direitos reservados.", color = TextSecondary.copy(0.5f), fontSize = 10.sp)
                    Text("Minecraft é marca registrada da Mojang Studios.", color = TextSecondary.copy(0.3f), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun TabPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) NexusCyan.copy(0.15f) else Color(0xFF111120))
            .border(1.dp, if (selected) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            color      = if (selected) NexusCyan else TextSecondary,
            fontSize   = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)
    ) {
        Text(title, color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ToggleSettingsRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(0.4f))
        )
    }
}
