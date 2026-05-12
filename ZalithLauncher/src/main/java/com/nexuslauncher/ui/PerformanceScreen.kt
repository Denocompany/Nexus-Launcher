package com.nexuslauncher.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslauncher.core.NexusBoostEngine
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary
import com.nexuslauncher.viewmodel.PerformanceViewModel
import kotlinx.coroutines.delay

private const val FPS_HISTORY_SIZE = 60

@Composable
fun PerformanceScreen(
    nexusDataStore        : NexusDataStore? = null,
    metrics               : NexusSystemMonitor.SystemMetrics = NexusSystemMonitor.SystemMetrics(),
    boostReport           : NexusBoostEngine.BoostReport = NexusBoostEngine.BoostReport(
        NexusBoostEngine.BoostState.IDLE, emptyList(), 0, 0, 0L
    ),
    onBoost               : () -> Unit = {},
    onOpenReports         : () -> Unit = {},
    onOpenAdvancedSettings: () -> Unit = {},
    onBackToSolar         : () -> Unit = {}
) {
    val vm: PerformanceViewModel? = nexusDataStore?.let {
        viewModel(factory = PerformanceViewModel.factory(it))
    }
    val savedPreset by (vm?.perfPreset ?: kotlinx.coroutines.flow.flowOf(2)).collectAsState(initial = 2)

    var disableRender by remember { mutableStateOf(true) }
    var shadowStream  by remember { mutableStateOf(true) }
    var effectBloom   by remember { mutableStateOf(false) }

    val fpsHistory = remember { mutableStateListOf<Int>() }
    val currentMetrics by rememberUpdatedState(metrics)

    LaunchedEffect(Unit) {
        while (true) {
            fpsHistory.add(currentMetrics.fpsCurrent)
            if (fpsHistory.size > FPS_HISTORY_SIZE) fpsHistory.removeAt(0)
            delay(1_000L)
        }
    }

    val fpsMin = fpsHistory.minOrNull() ?: 0
    val fpsMax = fpsHistory.maxOrNull() ?: 0
    val fpsAvg = if (fpsHistory.isEmpty()) 0 else fpsHistory.average().toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("PERFORMANCE", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("Classificação TI · Performance", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("FPS",  "${metrics.fpsCurrent}",                                    NexusCyan,            Modifier.weight(1f))
            MetricCard("CPU",  "${metrics.cpuPercent}%",                                   NexusOrange,          Modifier.weight(1f))
            MetricCard("GPU",  "${metrics.gpuPercent}%",                                   Color(0xFF7B61FF),    Modifier.weight(1f))
            MetricCard("RAM",  "${String.format("%.1f", metrics.ramGb)}GB",                Color(0xFF00E676),    Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        PerfSection("📈 Gráfico FPS — Últimos ${fpsHistory.size}s") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatBadge("MÍN",  "$fpsMin",  NexusOrange)
                StatBadge("MÉD",  "$fpsAvg",  Color(0xFFB0BEC5))
                StatBadge("MÁX",  "$fpsMax",  Color(0xFF00E676))
                StatBadge("ATUAL","${metrics.fpsCurrent}", NexusCyan)
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF080814)).border(1.dp, NexusCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            ) {
                FpsLineChart(fpsHistory = fpsHistory, currentFps = metrics.fpsCurrent, modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("-${fpsHistory.size}s", color = TextSecondary, fontSize = 9.sp)
                Text("── 60 FPS target ──", color = NexusOrange.copy(alpha = 0.6f), fontSize = 9.sp)
                Text("agora", color = TextSecondary, fontSize = 9.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        PerfSection("Desempenho") {
            ProgressRow("FPS", metrics.fpsCurrent / 120f,                    NexusCyan,         "${metrics.fpsCurrent} FPS")
            Spacer(Modifier.height(8.dp))
            ProgressRow("CPU", metrics.cpuPercent / 100f,                    NexusOrange,       "${metrics.cpuPercent}%")
            Spacer(Modifier.height(8.dp))
            ProgressRow("GPU", metrics.gpuPercent / 100f,                    Color(0xFF7B61FF), "${metrics.gpuPercent}%")
            Spacer(Modifier.height(8.dp))
            ProgressRow(
                "RAM",
                if (metrics.ramTotalGb > 0f) metrics.ramGb / metrics.ramTotalGb else 0f,
                Color(0xFF00E676),
                "${String.format("%.1f", metrics.ramGb)}/${String.format("%.0f", metrics.ramTotalGb)}GB"
            )
        }

        Spacer(Modifier.height(16.dp))

        PerfSection("Configurações de Render") {
            CheckRow("Desativar Render de Sombras", disableRender, NexusCyan) { disableRender = it }
            CheckRow("Sombras Streaming",           shadowStream,  NexusCyan) { shadowStream  = it }
            CheckRow("Efeito Bloom",                effectBloom,   NexusCyan) { effectBloom   = it }
        }

        Spacer(Modifier.height(20.dp))

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

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick  = onOpenReports,
                modifier = Modifier.weight(1f).height(44.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
            ) {
                Text("📊 Ver Relatórios", color = NexusCyan, fontSize = 11.sp)
            }
            Button(
                onClick  = onOpenAdvancedSettings,
                modifier = Modifier.weight(1f).height(44.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
            ) {
                Text("⚙ Configurações Avançadas", color = NexusCyan, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun FpsLineChart(fpsHistory: List<Int>, currentFps: Int, modifier: Modifier = Modifier) {
    val yMax = if (fpsHistory.isEmpty()) 120
               else maxOf(120, (((fpsHistory.maxOrNull() ?: 0) / 30 + 1) * 30))

    Canvas(modifier = modifier) {
        val nativeCanvas = drawContext.canvas.nativeCanvas
        if (fpsHistory.size < 2) return@Canvas
        val w = size.width; val h = size.height
        val padL = 36f; val padR = 8f; val padT = 12f; val padB = 12f
        val chartW = w - padL - padR; val chartH = h - padT - padB

        fun xOf(i: Int) = padL + (i.toFloat() / (fpsHistory.size - 1)) * chartW
        fun yOf(fps: Int) = padT + chartH - (fps.toFloat() / yMax) * chartH

        val gridPaint = android.graphics.Paint().apply { color = android.graphics.Color.argb(40, 100, 150, 255); strokeWidth = 1f; isAntiAlias = true }
        val labelPaint = android.graphics.Paint().apply { color = android.graphics.Color.argb(140, 160, 170, 200); textSize = 22f; isAntiAlias = true; textAlign = android.graphics.Paint.Align.RIGHT }

        listOf(0, 30, 60, 90, 120).filter { it <= yMax }.forEach { fps ->
            val y = yOf(fps)
            nativeCanvas.drawLine(padL, y, w - padR, y, gridPaint)
            nativeCanvas.drawText("$fps", padL - 4f, y + 8f, labelPaint)
        }

        val targetY = yOf(60)
        val targetPaint = android.graphics.Paint().apply { color = android.graphics.Color.argb(120, 255, 109, 0); strokeWidth = 2f; isAntiAlias = true; pathEffect = android.graphics.DashPathEffect(floatArrayOf(8f, 6f), 0f) }
        nativeCanvas.drawLine(padL, targetY, w - padR, targetY, targetPaint)

        val fillPath = Path().apply {
            moveTo(xOf(0), yOf(fpsHistory[0]))
            for (i in 1 until fpsHistory.size) {
                val x0 = xOf(i - 1); val y0 = yOf(fpsHistory[i - 1]); val x1 = xOf(i); val y1 = yOf(fpsHistory[i]); val cpX = (x0 + x1) / 2f
                cubicTo(cpX, y0, cpX, y1, x1, y1)
            }
            lineTo(xOf(fpsHistory.size - 1), padT + chartH); lineTo(xOf(0), padT + chartH); close()
        }
        drawPath(fillPath, brush = Brush.verticalGradient(listOf(NexusCyan.copy(alpha = 0.35f), NexusCyan.copy(alpha = 0.02f)), startY = padT, endY = padT + chartH))

        val linePath = Path().apply {
            moveTo(xOf(0), yOf(fpsHistory[0]))
            for (i in 1 until fpsHistory.size) {
                val x0 = xOf(i - 1); val y0 = yOf(fpsHistory[i - 1]); val x1 = xOf(i); val y1 = yOf(fpsHistory[i]); val cpX = (x0 + x1) / 2f
                cubicTo(cpX, y0, cpX, y1, x1, y1)
            }
        }
        drawPath(linePath, color = NexusCyan, style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val lastX = xOf(fpsHistory.size - 1); val lastY = yOf(fpsHistory.last())
        drawCircle(color = NexusCyan.copy(alpha = 0.25f), radius = 9f, center = Offset(lastX, lastY))
        drawCircle(color = NexusCyan, radius = 4.5f, center = Offset(lastX, lastY))
        drawCircle(color = Color.White, radius = 2f, center = Offset(lastX, lastY))

        val floatLabelPaint = android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 24f; isFakeBoldText = true; isAntiAlias = true; textAlign = android.graphics.Paint.Align.CENTER }
        nativeCanvas.drawText("${fpsHistory.last()} FPS", lastX, (lastY - 14f).coerceAtLeast(padT + 24f), floatLabelPaint)

        val dropPaint = android.graphics.Paint().apply { color = android.graphics.Color.argb(180, 255, 50, 50); strokeWidth = 2f; isAntiAlias = true }
        fpsHistory.forEachIndexed { i, fps ->
            if (fps < 30) nativeCanvas.drawCircle(xOf(i), yOf(fps), 4f, dropPaint)
        }
    }
}

@Composable private fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 9.sp, letterSpacing = 0.5.sp)
    }
}
@Composable private fun MetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFF121218)).border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(10.dp)).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}
@Composable private fun PerfSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
        Text(title, color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(12.dp)); content()
    }
}
@Composable private fun ProgressRow(label: String, progress: Float, color: Color, valueText: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.width(40.dp))
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(progress = progress.coerceIn(0f, 1f), modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)), color = color, backgroundColor = color.copy(alpha = 0.15f))
        Spacer(Modifier.width(8.dp))
        Text(valueText, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
    }
}
@Composable private fun CheckRow(label: String, checked: Boolean, color: Color, onCheck: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheck, colors = CheckboxDefaults.colors(checkedColor = color))
        Spacer(Modifier.width(4.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}
