package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
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
import com.nexuslauncher.core.NexusBoostEngine
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

@Composable
fun PerformanceScreen(
    metrics: NexusSystemMonitor.SystemMetrics = NexusSystemMonitor.SystemMetrics(),
    boostReport: NexusBoostEngine.BoostReport = NexusBoostEngine.BoostReport(
        NexusBoostEngine.BoostState.IDLE, emptyList(), 0, 0, 0L
    ),
    onBoost: () -> Unit = {}
) {
    var disableRender   by remember { mutableStateOf(true) }
    var shadowStream    by remember { mutableStateOf(true) }
    var effectBloom     by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Título
        Text("PERFORMANCE", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("Classificação TI · Performance", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(20.dp))

        // Métricas em tempo real
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("FPS", "${metrics.fpsCurrent}", NexusCyan, Modifier.weight(1f))
            MetricCard("CPU", "${metrics.cpuPercent}%", NexusOrange, Modifier.weight(1f))
            MetricCard("GPU", "${metrics.gpuPercent}%", Color(0xFF7B61FF), Modifier.weight(1f))
            MetricCard("RAM", "${String.format("%.1f", metrics.ramGb)}GB", Color(0xFF00E676), Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Barras de progresso
        PerfSection("Desempenho") {
            ProgressRow("FPS", metrics.fpsCurrent / 120f, NexusCyan, "${metrics.fpsCurrent} FPS")
            Spacer(Modifier.height(8.dp))
            ProgressRow("CPU", metrics.cpuPercent / 100f, NexusOrange, "${metrics.cpuPercent}%")
            Spacer(Modifier.height(8.dp))
            ProgressRow("GPU", metrics.gpuPercent / 100f, Color(0xFF7B61FF), "${metrics.gpuPercent}%")
            Spacer(Modifier.height(8.dp))
            ProgressRow("RAM", metrics.ramGb / metrics.ramTotalGb, Color(0xFF00E676),
                "${String.format("%.1f", metrics.ramGb)}/${String.format("%.0f", metrics.ramTotalGb)}GB")
        }

        Spacer(Modifier.height(16.dp))

        // Configurações de render
        PerfSection("Configurações de Render") {
            CheckRow("Desativar Render de Sombras", disableRender, NexusCyan) { disableRender = it }
            CheckRow("Sombras Streaming", shadowStream, NexusCyan) { shadowStream = it }
            CheckRow("Efeito Bloom", effectBloom, NexusCyan) { effectBloom = it }
        }

        Spacer(Modifier.height(20.dp))

        // Nexus Boost
        Button(
            onClick  = onBoost,
            enabled  = boostReport.state != NexusBoostEngine.BoostState.RUNNING,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(10.dp),
            colors   = ButtonDefaults.buttonColors(backgroundColor = NexusOrange)
        ) {
            Text(
                when (boostReport.state) {
                    NexusBoostEngine.BoostState.RUNNING -> "⚡ Otimizando..."
                    NexusBoostEngine.BoostState.DONE    -> "✓ +${boostReport.estimatedGainPct}% Aplicado"
                    else                                -> "⚡ OTIMIZAR EM 1 CLIQUE"
                },
                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp
            )
        }

        if (boostReport.state == NexusBoostEngine.BoostState.DONE) {
            Spacer(Modifier.height(10.dp))
            boostReport.stepsDone.forEach { step ->
                Text("• $step", color = Color(0xFF00E676), fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Configurações Avançadas →", color = NexusCyan.copy(alpha = 0.6f), fontSize = 11.sp,
            modifier = Modifier.align(Alignment.End))
    }
}

@Composable
private fun MetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF121218))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
private fun PerfSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Obsidian)
            .padding(14.dp)
    ) {
        Text(title, color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ProgressRow(label: String, progress: Float, color: Color, valueText: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.width(40.dp))
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            backgroundColor = color.copy(alpha = 0.15f)
        )
        Spacer(Modifier.width(8.dp))
        Text(valueText, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp))
    }
}

@Composable
private fun CheckRow(label: String, checked: Boolean, color: Color, onCheck: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheck,
            colors = CheckboxDefaults.colors(checkedColor = color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}
