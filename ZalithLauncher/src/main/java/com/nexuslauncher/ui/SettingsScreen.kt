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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslauncher.core.NexusInstanceManager
import com.nexuslauncher.core.NexusSetupChecker
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary
import com.nexuslauncher.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

/**
 * SettingsScreen — HELIOS CONTROL.
 * Configurações completas: diretório base, performance, visual, controles, launcher.
 * Inclui verificador de setup e configuração de caminho personalizado.
 */
@Composable
fun SettingsScreen(
    nexusDataStore      : NexusDataStore? = null,
    onOpenVisualSettings: () -> Unit = {},
    onOpenReports       : () -> Unit = {},
    onBackToSolar       : () -> Unit = {}
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val vm: SettingsViewModel? = nexusDataStore?.let {
        viewModel(factory = SettingsViewModel.factory(it))
    }

    val savedLanguage        by (vm?.language        ?: kotlinx.coroutines.flow.flowOf("Português (Brasil)")).collectAsState(initial = "Português (Brasil)")
    val savedAutoSave        by (vm?.autoSave         ?: kotlinx.coroutines.flow.flowOf(true)).collectAsState(initial = true)
    val savedCrashReport     by (vm?.crashReport      ?: kotlinx.coroutines.flow.flowOf(true)).collectAsState(initial = true)
    val savedFullscreen      by (vm?.fullscreen       ?: kotlinx.coroutines.flow.flowOf(false)).collectAsState(initial = false)
    val savedVsync           by (vm?.vsync            ?: kotlinx.coroutines.flow.flowOf(true)).collectAsState(initial = true)
    val savedResolution      by (vm?.resolution       ?: kotlinx.coroutines.flow.flowOf(0)).collectAsState(initial = 0)
    val savedAutoUpdate      by (vm?.autoUpdate       ?: kotlinx.coroutines.flow.flowOf(true)).collectAsState(initial = true)
    val savedGamePath        by (vm?.gamePath         ?: kotlinx.coroutines.flow.flowOf("")).collectAsState(initial = "")

    var selectedTab      by remember { mutableStateOf(0) }
    var gamePathInput    by remember(savedGamePath) { mutableStateOf(savedGamePath) }
    var pathSaveStatus   by remember { mutableStateOf("") }
    var setupStatus      by remember { mutableStateOf<NexusSetupChecker.SetupStatus?>(null) }

    val tabs = listOf("Geral", "Jogo", "Avançado", "Sobre")

    // Load setup status on first load
    LaunchedEffect(Unit) {
        setupStatus = NexusSetupChecker.check(context)
    }

    Column(Modifier.fillMaxSize().background(DeepVoid).verticalScroll(rememberScrollState()).padding(16.dp)) {

        Text("HELIOS CONTROL", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("CONFIGURAÇÕES · Launcher · Jogo · Avançado", color = TextSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(16.dp))

        // Setup checklist
        setupStatus?.let { status ->
            if (!status.isCompletelyReady) {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(NexusOrange.copy(0.08f)).border(1.dp, NexusOrange.copy(0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)) {
                    Text("⚙ Configuração Inicial (${status.pendingCount} pendente(s))", color = NexusOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    status.items.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(if (item.isDone) "✓" else "○", color = if (item.isDone) Color(0xFF00E676) else NexusOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Column {
                                Text(item.title, color = if (item.isDone) TextSecondary else Color.White, fontSize = 12.sp, fontWeight = if (item.isDone) FontWeight.Normal else FontWeight.Bold)
                                if (!item.isDone) Text(item.description, color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // Tabs
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(Modifier.clip(RoundedCornerShape(20.dp))
                    .background(if (selectedTab == i) NexusCyan.copy(0.15f) else Color(0xFF111120))
                    .border(1.dp, if (selectedTab == i) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
                    .clickable { selectedTab = i }.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Text(tab, color = if (selectedTab == i) NexusCyan else TextSecondary, fontSize = 11.sp,
                        fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
        Spacer(Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // General settings
                SettingsSection("🌍 Idioma & Região") {
                    val languages = listOf("Português (Brasil)", "English (US)", "Español", "中文", "日本語")
                    Column(Modifier.fillMaxWidth()) {
                        languages.forEach { lang ->
                            val sel = savedLanguage == lang
                            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                                .background(if (sel) NexusCyan.copy(0.1f) else Color.Transparent)
                                .clickable { vm?.updateLanguage(lang) }.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(lang, color = if (sel) NexusCyan else Color.White, fontSize = 13.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                if (sel) Text("✓", color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                SettingsSection("🔔 Atualizações & Notificações") {
                    SettingsToggle("Atualização automática",   savedAutoUpdate) { vm?.updateAutoUpdate(it) }
                    Spacer(Modifier.height(6.dp))
                    SettingsToggle("Relatório de crashes",     savedCrashReport) { vm?.updateCrashReport(it) }
                    Spacer(Modifier.height(6.dp))
                    SettingsToggle("Salvar automaticamente",   savedAutoSave)    { vm?.updateAutoSave(it) }
                }
            }

            1 -> {
                // Game settings
                SettingsSection("📁 Diretório do Jogo") {
                    Text("Caminho onde instâncias e dados do Minecraft são armazenados.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gamePathInput, onValueChange = { gamePathInput = it },
                        label = { Text("Caminho (ex: /storage/emulated/0/NexusLauncher)", color = TextSecondary, fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = NexusCyan, unfocusedBorderColor = Color(0xFF333340),
                            textColor = Color.White, cursorColor = NexusCyan
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    if (pathSaveStatus.isNotEmpty()) {
                        Text(pathSaveStatus, color = NexusCyan, fontSize = 11.sp)
                        Spacer(Modifier.height(6.dp))
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                vm?.updateGamePath(gamePathInput)
                                NexusInstanceManager.changeBaseDir(context, gamePathInput)
                                pathSaveStatus = "✓ Caminho salvo: $gamePathInput"
                                setupStatus = NexusSetupChecker.check(context)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan.copy(0.2f)),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth(),
                        enabled = gamePathInput.isNotBlank()
                    ) { Text("Salvar Caminho", color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(6.dp))
                    Text("Instâncias atuais: ${NexusInstanceManager.instances.value.size}", color = TextSecondary, fontSize = 11.sp)
                }
                Spacer(Modifier.height(10.dp))
                SettingsSection("🖥 Gráficos") {
                    SettingsToggle("Tela cheia", savedFullscreen) { vm?.updateFullscreen(it) }
                    Spacer(Modifier.height(6.dp))
                    SettingsToggle("V-Sync", savedVsync) { vm?.updateVsync(it) }
                    Spacer(Modifier.height(8.dp))
                    Text("Resolução de renderização", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(6.dp))
                    val resolutions = listOf("Nativa", "75%", "50%", "25%")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        resolutions.forEachIndexed { i, res ->
                            Box(Modifier.clip(RoundedCornerShape(6.dp))
                                .background(if (savedResolution == i) NexusCyan.copy(0.2f) else Color(0xFF111120))
                                .border(1.dp, if (savedResolution == i) NexusCyan else Color(0xFF222230), RoundedCornerShape(6.dp))
                                .clickable { vm?.updateResolution(i) }.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(res, color = if (savedResolution == i) NexusCyan else TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick = onOpenVisualSettings, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Obsidian), shape = RoundedCornerShape(8.dp)) {
                    Text("🎨 Configurações Visuais Avançadas (LUMINA)", color = NexusCyan, fontSize = 12.sp)
                }
            }

            2 -> {
                // Advanced settings
                SettingsSection("🧪 Experimental") {
                    Text("Funcionalidades em desenvolvimento. Use com cautela.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(10.dp))
                    val advToggles = listOf(
                        Triple("Nexus AI Predict",      "Prediz configurações ótimas automaticamente", false),
                        Triple("Boost Preditivo",       "Analisa padrão de uso para otimizar", false),
                        Triple("GPU Overclock (beta)",  "Aumenta clock da GPU. Pode aquecer o device", false),
                        Triple("Canal Beta",            "Recebe atualizações antecipadas", false)
                    )
                    advToggles.forEach { (name, desc, _) ->
                        var enabled by remember { mutableStateOf(false) }
                        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(name, color = Color.White, fontSize = 13.sp)
                                    Text(desc, color = TextSecondary, fontSize = 10.sp)
                                }
                                Switch(checked = enabled, onCheckedChange = { enabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(0.5f)))
                            }
                        }
                        Divider(color = Color(0xFF1A1A28), modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick = onOpenReports, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Obsidian), shape = RoundedCornerShape(8.dp)) {
                    Text("📊 Ver Relatórios de Performance", color = TextSecondary, fontSize = 12.sp)
                }
            }

            3 -> {
                // About
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚡", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("NEXUS LAUNCHER", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Text("v2.0.0.0", color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Divider(color = NexusCyan.copy(0.15f))
                    Spacer(Modifier.height(12.dp))
                    listOf(
                        "Engine" to "ZalithLauncher (PojavLauncher fork)",
                        "UI" to "Jetpack Compose",
                        "Renderer" to "OpenGL ES / Vulkan (Zink)",
                        "Package" to "com.denocompany.nexuslauncher",
                        "Target" to "Android 8.0+ (API 26+)"
                    ).forEach { (label, value) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, color = TextSecondary, fontSize = 12.sp)
                            Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Divider(color = Color(0xFF1A1A28))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Desenvolvido por Deno Company", color = TextSecondary, fontSize = 11.sp)
                    Text("github.com/Denocompany/Nexus-Launcher", color = NexusCyan, fontSize = 10.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onBackToSolar, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan.copy(0.15f)),
            shape = RoundedCornerShape(8.dp)) {
            Text("← Voltar ao Sistema Solar", color = NexusCyan, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
        Text(title, color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun SettingsToggle(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(0.5f),
                uncheckedThumbColor = TextSecondary, uncheckedTrackColor = TextSecondary.copy(0.3f)))
    }
}