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
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

data class SessionReport(
    val date       : String,
    val instance   : String,
    val duration   : String,
    val fpsAvg     : Int,
    val fpsMin     : Int,
    val fpsMax     : Int,
    val cpuPeak    : Int,
    val ramPeak    : String,
    val crashCount : Int = 0,
    val stability  : Float = 0.87f
)

@Composable
fun ReportsScreen(onNavigateTo: (String) -> Unit = {}) {

    val sessions = remember {
        listOf(
            SessionReport("Hoje 15:00",  "Survival 1.18.1",    "01:24:37", 58, 32, 120, 72, "3.2GB", 0, 0.87f),
            SessionReport("Ontem 22:10", "Survival 1.18.1",    "02:13:00", 60, 28, 118, 68, "3.0GB", 0, 0.91f),
            SessionReport("15/05",       "Tech Modpack 1.16.5", "00:45:22", 48, 18, 90,  85, "3.8GB", 1, 0.62f),
            SessionReport("14/05",       "SkyBlock 1.12.2",     "01:02:11", 75, 45, 120, 55, "1.8GB", 0, 0.95f),
            SessionReport("13/05",       "Create Mod Pack",     "00:30:04", 42, 15, 88,  90, "4.1GB", 2, 0.48f),
        )
    }

    var selectedTab     by remember { mutableStateOf(0) }
    var selectedSession by remember { mutableStateOf(0) }

    val tabs = listOf("Sessões Recentes", "Relatório Detalhado", "Exportar")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("RELATÓRIOS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("${sessions.size} sessões · CHRONOS DATA CENTER", color = TextSecondary, fontSize = 11.sp)
            }
            SmallActionButton("Performance") { onNavigateTo("aetherion") }
        }

        Spacer(Modifier.height(16.dp))

        // ── Abas ─────────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == i) NexusCyan.copy(0.15f) else Color(0xFF111120))
                        .border(1.dp, if (selectedTab == i) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { selectedTab = i }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        tab,
                        color      = if (selectedTab == i) NexusCyan else TextSecondary,
                        fontSize   = 11.sp,
                        fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        when (selectedTab) {

            // ── Aba 0: Sessões Recentes ──────────────────────────────────
            0 -> {
                // Resumo geral
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard("FPS Médio", "${sessions.map { it.fpsAvg }.average().toInt()}", NexusCyan, Modifier.weight(1f))
                    SummaryCard("Sessões",   "${sessions.size}", Color(0xFF7B61FF), Modifier.weight(1f))
                    SummaryCard("Crashes",   "${sessions.sumOf { it.crashCount }}", NexusOrange, Modifier.weight(1f))
                    SummaryCard("Estab.",    "${(sessions.map { it.stability }.average() * 100).toInt()}%", Color(0xFF00E676), Modifier.weight(1f))
                }

                Spacer(Modifier.height(12.dp))

                // Lista de sessões
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    sessions.forEachIndexed { idx, session ->
                        val isSel = selectedSession == idx
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSel) NexusCyan.copy(0.06f) else Color(0xFF141420))
                                    .clickable { selectedSession = idx; selectedTab = 1 }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(session.instance, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        if (session.crashCount > 0) {
                                            Box(Modifier.clip(RoundedCornerShape(4.dp)).background(NexusOrange.copy(0.2f)).padding(horizontal = 5.dp, vertical = 1.dp)) {
                                                Text("${session.crashCount} crash", color = NexusOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text("${session.date} · ${session.duration}", color = TextSecondary, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${session.fpsAvg} FPS", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    val stabColor = when {
                                        session.stability >= 0.85f -> Color(0xFF00E676)
                                        session.stability >= 0.65f -> NexusOrange
                                        else                       -> Color(0xFFFF5252)
                                    }
                                    Text("${(session.stability * 100).toInt()}% estável", color = stabColor, fontSize = 10.sp)
                                }
                            }
                            if (idx < sessions.size - 1) Divider(color = Color(0xFF1A1A28), thickness = 1.dp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick  = {},
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                ) { Text("🗑 Limpar Histórico", color = NexusOrange, fontSize = 12.sp) }
            }

            // ── Aba 1: Relatório Detalhado ────────────────────────────────
            1 -> {
                val session = sessions.getOrNull(selectedSession) ?: sessions.first()

                // Seletor de sessão
                Text("Sessão:", color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    sessions.take(5).forEachIndexed { i, s ->
                        val sel = selectedSession == i
                        Box(
                            Modifier.clip(RoundedCornerShape(6.dp)).background(if (sel) NexusCyan.copy(0.15f) else Color(0xFF111120)).border(1.dp, if (sel) NexusCyan else TextSecondary.copy(0.3f), RoundedCornerShape(6.dp)).clickable { selectedSession = i }.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(s.date, color = if (sel) NexusCyan else TextSecondary, fontSize = 9.sp)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Cabeçalho da sessão
                ReportSection("📊 ${session.instance} · ${session.date}") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryCard("FPS Méd.",  "${session.fpsAvg}",  NexusCyan,         Modifier.weight(1f))
                        SummaryCard("FPS Mín.",  "${session.fpsMin}",  NexusOrange,       Modifier.weight(1f))
                        SummaryCard("FPS Máx.",  "${session.fpsMax}",  Color(0xFF00E676), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    ReportStat("Duração",  session.duration,            NexusCyan)
                    ReportStat("Crashes",  "${session.crashCount}",      if (session.crashCount == 0) Color(0xFF00E676) else NexusOrange)
                }

                Spacer(Modifier.height(10.dp))

                // Estabilidade de FPS
                ReportSection("📈 Estabilidade de FPS") {
                    val stabColor = when {
                        session.stability >= 0.85f -> Color(0xFF00E676)
                        session.stability >= 0.65f -> NexusOrange
                        else                       -> Color(0xFFFF5252)
                    }
                    LinearProgressIndicator(
                        progress         = session.stability,
                        modifier         = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color            = stabColor,
                        backgroundColor  = stabColor.copy(0.15f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${(session.stability * 100).toInt()}% — ${when { session.stability >= 0.85f -> "Excelente" ; session.stability >= 0.70f -> "Boa" ; session.stability >= 0.50f -> "Regular" ; else -> "Ruim" }}",
                        color = stabColor, fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Recursos
                ReportSection("⚙ Picos de Recurso") {
                    ResourceBar("CPU",  session.cpuPeak / 100f,    NexusOrange,       "${session.cpuPeak}%")
                    Spacer(Modifier.height(6.dp))
                    ResourceBar("GPU",  0.58f,                      Color(0xFF7B61FF), "58%")
                    Spacer(Modifier.height(6.dp))
                    ResourceBar("RAM",  0.65f,                      Color(0xFF00E676), session.ramPeak)
                }

                Spacer(Modifier.height(10.dp))

                if (session.crashCount > 0) {
                    ReportSection("🔴 Crashes Detectados") {
                        repeat(session.crashCount) { i ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Crash #${i + 1}", color = NexusOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("ConcurrentModificationException", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                Button(
                    onClick  = { onNavigateTo("aetherion") },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                ) { Text("⚡ Otimizar com Nexus Boost", color = NexusCyan, fontSize = 12.sp) }
            }

            // ── Aba 2: Exportar ───────────────────────────────────────────
            2 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(16.dp)
                ) {
                    Text("EXPORTAR RELATÓRIO", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Escolha o formato e o período para exportar.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(16.dp))

                    listOf(
                        "📄 TXT — Relatório legível" to NexusCyan,
                        "📊 CSV — Dados de sessões" to Color(0xFF00E676),
                        "📑 JSON — Dados brutos completos" to Color(0xFF7B61FF),
                        "📧 Compartilhar por e-mail" to NexusOrange,
                    ).forEach { (label, color) ->
                        Button(
                            onClick  = { /* export */ },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(backgroundColor = color.copy(0.12f))
                        ) { Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.height(8.dp))
                    }

                    Spacer(Modifier.height(8.dp))
                    Divider(color = Color(0xFF1A1A28))
                    Spacer(Modifier.height(12.dp))

                    Text("Enviar log de debug", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "O log de debug inclui informações técnicas completas do dispositivo, métricas de sessão e stack traces de crashes.",
                        color    = TextSecondary.copy(0.6f),
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick  = { /* send debug log */ },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) { Text("🐞 Enviar Log para Equipe Nexus", color = NexusCyan, fontSize = 12.sp) }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier         = modifier.clip(RoundedCornerShape(10.dp)).background(Obsidian).border(1.dp, color.copy(0.2f), RoundedCornerShape(10.dp)).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = TextSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun ReportSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
        Text(title, color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ReportStat(label: String, value: String, color: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ResourceBar(label: String, progress: Float, color: Color, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.width(40.dp))
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            progress        = progress,
            modifier        = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color           = color,
            backgroundColor = color.copy(0.15f)
        )
        Spacer(Modifier.width(8.dp))
        Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp))
    }
}
