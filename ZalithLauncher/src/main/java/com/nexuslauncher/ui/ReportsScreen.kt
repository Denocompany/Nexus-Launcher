package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
fun ReportsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("RELATÓRIOS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("Sessão atual · Histórico · Exportar", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(20.dp))

        // Sessão atual
        ReportSection("📊 Sessão Atual") {
            ReportStat("Duração",      "01:24:37",    NexusCyan)
            ReportStat("FPS Médio",    "58",          Color(0xFF00E676))
            ReportStat("FPS Mínimo",   "32",          NexusOrange)
            ReportStat("FPS Máximo",   "120",         NexusCyan)
            ReportStat("Crashes",      "0",           Color(0xFF00E676))
            Spacer(Modifier.height(8.dp))
            Text("Estabilidade de FPS", color = TextSecondary, fontSize = 11.sp)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = 0.87f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF00E676),
                backgroundColor = Color(0xFF1C2A1C)
            )
            Spacer(Modifier.height(2.dp))
            Text("87% — Excelente", color = Color(0xFF00E676), fontSize = 10.sp)
        }

        Spacer(Modifier.height(12.dp))

        // Histórico
        ReportSection("🕐 Histórico de Sessões") {
            HistoryRow("Ontem 22:10", "Survival 1.18.1", "02:13:00", "60 FPS")
            Divider(color = Color(0xFF222230), thickness = 1.dp)
            HistoryRow("15/05",       "Tech Modpack",    "00:45:22", "48 FPS")
            Divider(color = Color(0xFF222230), thickness = 1.dp)
            HistoryRow("14/05",       "SkyBlock",        "01:02:11", "75 FPS")
        }

        Spacer(Modifier.height(12.dp))

        // Recursos da sessão
        ReportSection("⚙ Recursos Utilizados") {
            ResourceBar("CPU Pico",   0.72f, NexusOrange, "72%")
            Spacer(Modifier.height(6.dp))
            ResourceBar("GPU Pico",   0.58f, Color(0xFF7B61FF), "58%")
            Spacer(Modifier.height(6.dp))
            ResourceBar("RAM Máx.",   0.65f, Color(0xFF00E676), "3.2GB")
        }

        Spacer(Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan)
            ) {
                Text("📤 EXPORTAR RELATÓRIO", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
            ) {
                Text("🗑 Limpar Histórico", color = NexusOrange, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ReportSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
private fun ReportStat(label: String, value: String, color: Color) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HistoryRow(date: String, instance: String, duration: String, fps: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(instance, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(date, color = TextSecondary, fontSize = 10.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(fps, color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(duration, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ResourceBar(label: String, progress: Float, color: Color, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.width(72.dp))
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            backgroundColor = color.copy(alpha = 0.15f)
        )
        Spacer(Modifier.width(8.dp))
        Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
    }
}
