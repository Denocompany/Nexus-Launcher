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
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.core.NexusSessionTracker
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * ReportsScreen — CHRONOS conectado ao NexusSessionTracker real.
 * Exibe sessões persistidas no disco, estatísticas globais,
 * e permite exportação de relatórios.
 */
@Composable
fun ReportsScreen(
    onOpenPerformance: () -> Unit = {},
    onBoostFromReport: () -> Unit = {},
    onBackToSolar    : () -> Unit = {}
) {
    val sessions      by NexusSessionTracker.sessions.collectAsState()
    val activeSession by NexusSessionTracker.activeSession.collectAsState()
    val globalStats    = remember(sessions) { NexusSessionTracker.getGlobalStats() }

    var selectedTab     by remember { mutableStateOf(0) }
    var selectedIdx     by remember { mutableStateOf(0) }
    val tabs = listOf("Sessões", "Estatísticas", "Exportar")

    Column(Modifier.fillMaxSize().background(DeepVoid).verticalScroll(rememberScrollState()).padding(16.dp)) {

        // Header
        Text("CHRONOS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("RELATÓRIOS · Histórico de Sessões · Performance", color = TextSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))

        // Active session indicator
        if (activeSession != null) {
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(NexusOrange.copy(0.12f)).border(1.dp, NexusOrange.copy(0.3f), RoundedCornerShape(10.dp))
                .padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🎮", fontSize = 20.sp)
                Column(Modifier.weight(1f)) {
                    Text("Sessão em andamento: ${activeSession!!.instanceName}", color = NexusOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Iniciada: ${activeSession!!.dateLabel}", color = TextSecondary, fontSize = 10.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        // Global stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Sessões", "${globalStats.totalSessions}", Color(0xFF7B61FF), Modifier.weight(1f))
            StatCard("Horas", "${"%.1f".format(globalStats.totalHours)}h", NexusCyan, Modifier.weight(1f))
            StatCard("FPS Médio", "${globalStats.avgFps}", NexusOrange, Modifier.weight(1f))
            StatCard("Melhor FPS", "${globalStats.bestFps}", Color(0xFF00E676), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))

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
        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                // Session list
                if (sessions.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 30.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Nenhuma sessão registrada ainda", color = TextSecondary, fontSize = 14.sp)
                            Text("Jogue para ver seu histórico aqui", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                } else {
                    // Session detail panel
                    val sel = sessions.getOrNull(selectedIdx)
                    if (sel != null) {
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(Obsidian).padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(sel.instanceName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(sel.dateLabel, color = TextSecondary, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(sel.durationLabel, color = NexusCyan, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("duração", color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Divider(color = NexusCyan.copy(0.1f))
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MetricBox("FPS Médio", "${sel.fpsAvg}", NexusCyan, Modifier.weight(1f))
                                MetricBox("FPS Mín", "${sel.fpsMin}", Color(0xFFCF4455), Modifier.weight(1f))
                                MetricBox("FPS Máx", "${sel.fpsMax}", Color(0xFF00E676), Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MetricBox("CPU Médio", "${sel.cpuAvg}%", NexusOrange, Modifier.weight(1f))
                                MetricBox("RAM Pico", "${"%.1f".format(sel.ramPeakGb)}GB", Color(0xFF7B61FF), Modifier.weight(1f))
                                MetricBox("Crashes", "${sel.crashCount}", if (sel.crashCount > 0) Color(0xFFCF4455) else Color(0xFF00E676), Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(10.dp))
                            Text("Estabilidade", color = TextSecondary, fontSize = 11.sp)
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = sel.stability,
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = when {
                                    sel.stability >= 0.85f -> Color(0xFF00E676)
                                    sel.stability >= 0.6f  -> NexusOrange
                                    else                   -> Color(0xFFCF4455)
                                },
                                backgroundColor = Color(0xFF1A1A28)
                            )
                            Text("${"%.0f".format(sel.stability * 100)}%", color = TextSecondary, fontSize = 10.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    // Session list items
                    sessions.forEachIndexed { idx, session ->
                        val isSel = selectedIdx == idx
                        Row(Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) NexusCyan.copy(0.08f) else Color(0xFF0E0E16))
                            .border(1.dp, if (isSel) NexusCyan.copy(0.3f) else Color(0xFF1A1A26), RoundedCornerShape(8.dp))
                            .clickable { selectedIdx = idx }.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(session.instanceName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(session.dateLabel, color = TextSecondary, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(session.durationLabel, color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${session.fpsAvg} FPS avg", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Statistics
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
                    Text("📈 Estatísticas Gerais", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    val stats = listOf(
                        Triple("Total de sessões",  "${globalStats.totalSessions}", NexusCyan),
                        Triple("Horas jogadas",     "${"%.1f".format(globalStats.totalHours)}h", Color(0xFF7B61FF)),
                        Triple("FPS médio geral",   "${globalStats.avgFps}", NexusOrange),
                        Triple("Melhor FPS",        "${globalStats.bestFps}", Color(0xFF00E676)),
                        Triple("Total de crashes",  "${globalStats.totalCrashes}", if (globalStats.totalCrashes > 0) Color(0xFFCF4455) else Color(0xFF00E676))
                    )
                    stats.forEach { (label, value, color) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, color = TextSecondary, fontSize = 12.sp)
                            Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Divider(color = Color(0xFF1A1A28))
                    }
                }
            }

            2 -> {
                // Export
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
                    Text("📤 Exportar Relatório", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Gera um arquivo de texto com todas as sessões registradas.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { /* NexusSessionTracker.exportAsText() salvar em arquivo */ },
                        colors  = ButtonDefaults.buttonColors(backgroundColor = NexusCyan.copy(0.15f)),
                        shape   = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()
                    ) { Text("📄 Exportar como TXT", color = NexusCyan, fontSize = 12.sp) }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onBoostFromReport,
                        colors  = ButtonDefaults.buttonColors(backgroundColor = NexusOrange.copy(0.15f)),
                        shape   = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()
                    ) { Text("⚡ Nexus Boost → Otimizar", color = NexusOrange, fontSize = 12.sp) }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onOpenPerformance, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Obsidian), shape = RoundedCornerShape(8.dp)) {
                Text("📊 Performance", color = TextSecondary, fontSize = 12.sp)
            }
            Button(onClick = onBackToSolar, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan.copy(0.15f)), shape = RoundedCornerShape(8.dp)) {
                Text("← Solar", color = NexusCyan, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(10.dp)).background(Obsidian).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = TextSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun MetricBox(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(0.1f)).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 9.sp)
    }
}