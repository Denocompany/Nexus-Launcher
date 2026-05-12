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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * HomeScreen — NEXUS PRIME (Fase 4).
 * Todos os botões e atalhos rápidos navegam para o planeta correspondente.
 */
@Composable
fun HomeScreen(
    instanceName : String = "Survival 1.18.1",
    metrics      : NexusSystemMonitor.SystemMetrics = NexusSystemMonitor.SystemMetrics(),
    onLaunchGame : () -> Unit = {},
    onNavigateTo : (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Top Bar ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF080812))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "⚡ NEXUS PRIME",
                color      = NexusCyan,
                fontWeight = FontWeight.Black,
                fontSize   = 15.sp,
                letterSpacing = 2.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatusChip("RAM", "${String.format("%.1f", metrics.ramGb)}/${String.format("%.0f", metrics.ramTotalGb)}GB", Color(0xFF00E676))
                StatusChip("CPU", "${metrics.cpuPercent}%", NexusCyan)
                StatusChip("FPS", "${metrics.fpsCurrent}", NexusOrange)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Linha principal: 3 cards ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Card Esquerdo — Última Instância
            NexusHomeCard(modifier = Modifier.weight(1f)) {
                Text("ÚLTIMA INSTÂNCIA", color = NexusCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Text(instanceName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Shaders Ativos", color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallActionButton("Configurar") { onNavigateTo("instarrion") }
                    SmallActionButton("Gerenciar")  { onNavigateTo("instarrion") }
                }
            }

            // Card Central — INICIAR JOGO
            Column(
                modifier                = Modifier.weight(1f),
                horizontalAlignment     = Alignment.CenterHorizontally,
                verticalArrangement     = Arrangement.Center
            ) {
                Button(
                    onClick   = onLaunchGame,
                    modifier  = Modifier.fillMaxWidth().height(80.dp),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = ButtonDefaults.buttonColors(backgroundColor = NexusOrange),
                    elevation = ButtonDefaults.elevation(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚀", fontSize = 22.sp)
                        Text(
                            "INICIAR JOGO",
                            color      = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize   = 13.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("v1.18.1 • Survival", color = TextSecondary, fontSize = 10.sp)
            }

            // Card Direito — Status do Sistema
            NexusHomeCard(modifier = Modifier.weight(1f)) {
                Text("STATUS DO SISTEMA", color = NexusCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                StatusRow("FPS", "${metrics.fpsCurrent}", NexusCyan)
                Spacer(Modifier.height(4.dp))
                StatusRow("CPU", "${metrics.cpuPercent}%", NexusOrange)
                Spacer(Modifier.height(4.dp))
                StatusRow("GPU", "${metrics.gpuPercent}%", Color(0xFF7B61FF))
                Spacer(Modifier.height(4.dp))
                StatusRow("RAM", "${String.format("%.1f", metrics.ramGb)}/${String.format("%.0f", metrics.ramTotalGb)}GB", Color(0xFF00E676))
                Spacer(Modifier.height(8.dp))
                SmallActionButton("Ver Performance") { onNavigateTo("aetherion") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Atalhos Rápidos ───────────────────────────────────────────────
        Text(
            "ATALHOS RÁPIDOS",
            color      = TextSecondary,
            fontSize   = 10.sp,
            letterSpacing = 1.5.sp,
            modifier   = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickCard(
                icon     = "⚡",
                title    = "Nexus Boost",
                subtitle = "Otimizar agora",
                color    = NexusCyan,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("aetherion") }

            QuickCard(
                icon     = "🎨",
                title    = "Pacotes de Textura",
                subtitle = "Shaders HDR Ativado",
                color    = Color(0xFF7B61FF),
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("modara") }

            QuickCard(
                icon     = "🧩",
                title    = "Mods & Plugins",
                subtitle = "5 ativos",
                color    = NexusOrange,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("modara") }

            QuickCard(
                icon     = "📊",
                title    = "Relatório de Desempenho",
                subtitle = "Média: ${metrics.fpsCurrent} FPS",
                color    = Color(0xFF00E676),
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("chronos") }
        }

        Spacer(Modifier.height(16.dp))

        // ── Acesso rápido a planetas ──────────────────────────────────────
        Text(
            "NAVEGAÇÃO RÁPIDA",
            color     = TextSecondary,
            fontSize  = 10.sp,
            letterSpacing = 1.5.sp,
            modifier  = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("🌐", "Instâncias",    "instarrion"),
                Triple("👤", "Contas",         "persona"),
                Triple("🖥", "Visual",          "lumina"),
                Triple("⚙", "Configurações",  "helios"),
            ).forEach { (icon, label, dest) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Obsidian)
                        .border(1.dp, NexusCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .clickable { onNavigateTo(dest) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(icon, fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Status bar inferior ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF080812))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            BottomStatusItem("🚀", "Nexus Boost")
            BottomStatusItem("🎨", "Shaders HDR")
            BottomStatusItem("🧩", "5 Mods Ativos")
            BottomStatusItem("📊", "Relatório FPS")
        }
    }
}

@Composable
private fun NexusHomeCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Obsidian)
            .border(1.dp, NexusCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        content = content
    )
}

@Composable
private fun StatusChip(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = TextSecondary, fontSize = 9.sp)
        Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatusRow(label: String, value: String, color: Color) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SmallActionButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, NexusCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = NexusCyan, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun QuickCard(
    icon     : String,
    title    : String,
    subtitle : String,
    color    : Color,
    modifier : Modifier = Modifier,
    onClick  : () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Obsidian)
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.height(6.dp))
        Text(title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
        Text(subtitle, color = TextSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun BottomStatusItem(icon: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(icon, fontSize = 11.sp)
        Text(label, color = TextSecondary, fontSize = 9.sp)
    }
}
