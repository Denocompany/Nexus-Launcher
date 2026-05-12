package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.core.NexusBoostEngine
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * Tela de Performance (Fase 3).
 * Exibe métricas ao vivo e status do Nexus Boost.
 * Controle de boost disponível no painel lateral do planeta Aetherion.
 */
@Composable
fun PerformanceScreen(
    metrics:     NexusSystemMonitor.SystemMetrics = NexusSystemMonitor.SystemMetrics(),
    boostReport: NexusBoostEngine.BoostReport     = NexusBoostEngine.BoostReport(
        NexusBoostEngine.BoostState.IDLE, emptyList(), 0, 0, 0L
    ),
    onBoost: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text          = "PERFORMANCE",
            color         = NexusCyan,
            fontSize      = 22.sp,
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text     = "FPS: ${metrics.fpsCurrent}",
            color    = NexusCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text     = "CPU: ${metrics.cpuPercent}%   |   GPU: ${metrics.gpuPercent}%",
            color    = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text     = "RAM: ${String.format("%.1f", metrics.ramGb)} / ${String.format("%.1f", metrics.ramTotalGb)} GB",
            color    = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text     = "Sessão: ${metrics.sessionSec}s",
            color    = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (boostReport.state) {
            NexusBoostEngine.BoostState.DONE -> {
                Text(
                    text       = "✓ Boost +${boostReport.estimatedGainPct}% aplicado",
                    color      = NexusCyan,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                boostReport.stepsDone.forEach { step ->
                    Text(
                        text     = "  • $step",
                        color    = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            NexusBoostEngine.BoostState.RUNNING ->
                Text(
                    text     = "⚡ Otimizando sistema...",
                    color    = NexusCyan,
                    fontSize = 14.sp
                )
            else ->
                Text(
                    text     = "Use o painel do planeta Aetherion para ativar o Nexus Boost",
                    color    = TextSecondary,
                    fontSize = 13.sp
                )
        }
    }
}
