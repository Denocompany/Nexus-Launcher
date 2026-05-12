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
fun SettingsScreen() {
    var bgLoad     by remember { mutableStateOf(true) }
    var autoUpdate by remember { mutableStateOf(true) }
    var analytics  by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("CONFIGURAÇÕES", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("Vídeo · Controles · Jogo · Launcher · Java", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(20.dp))

        // Conta
        SettingsSection("👤 Conta") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Conta Microsoft", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Logado", color = Color(0xFF00E676), fontSize = 10.sp)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF1A2A1A))
                        .border(1.dp, Color(0xFF00E676).copy(0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Gerenciar", color = Color(0xFF00E676), fontSize = 10.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Diretórios
        SettingsSection("📁 Diretórios") {
            SettingsRow("Diretório de Jogo", "/games/minecraft")
            SettingsRow("Diretório Java",   "/usr/lib/jvm/java-17")
            SettingsRow("Pasta de Mods",    "/games/mods")
        }

        Spacer(Modifier.height(12.dp))

        // Launcher
        SettingsSection("🚀 Inicialização do Launcher") {
            ToggleSettingsRow("Carregar em Background", bgLoad)  { bgLoad     = it }
            Divider(color = Color(0xFF222230))
            ToggleSettingsRow("Atualizações Automáticas", autoUpdate) { autoUpdate = it }
            Divider(color = Color(0xFF222230))
            ToggleSettingsRow("Enviar Telemetria", analytics) { analytics = it }
        }

        Spacer(Modifier.height(12.dp))

        // Java
        SettingsSection("☕ Java") {
            SettingsRow("Versão Java",     "Java 17 (OpenJDK)")
            SettingsRow("RAM Mínima",      "512 MB")
            SettingsRow("RAM Máxima",      "4096 MB")
            SettingsRow("Args JVM",        "-XX:+UseG1GC -Xmx4G")
        }

        Spacer(Modifier.height(20.dp))

        // Depuração
        SettingsSection("🔧 Depurações") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                ) {
                    Text("Debug do Launcher", color = NexusCyan, fontSize = 11.sp)
                }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                ) {
                    Text("EXPORTAR RELATÓRIO", color = NexusOrange, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Nexus Launcher v1.4.1.5 · Build 42 · © 2025 Nexus Team",
            color = TextSecondary.copy(0.35f),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
private fun SettingsRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
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
