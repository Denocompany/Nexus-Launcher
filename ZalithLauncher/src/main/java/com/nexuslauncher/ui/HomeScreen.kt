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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.core.NexusAccountManager
import com.nexuslauncher.core.NexusInstanceManager
import com.nexuslauncher.core.NexusLaunchManager
import com.nexuslauncher.core.NexusModManager
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * HomeScreen — NEXUS PRIME.
 * Mostra a última instância, métricas reais do sistema,
 * e lança o jogo via NexusLaunchManager.
 */
@Composable
fun HomeScreen(
    instanceName    : String                             = "",
    metrics         : NexusSystemMonitor.SystemMetrics   = NexusSystemMonitor.SystemMetrics(),
    onLaunchGame    : () -> Unit = {},
    onConfigInstance: () -> Unit = {},
    onManageInstance: () -> Unit = {},
    onBoost         : () -> Unit = {},
    onTextures      : () -> Unit = {},
    onMods          : () -> Unit = {},
    onReports       : () -> Unit = {},
    onGoInstances   : () -> Unit = {},
    onGoAccounts    : () -> Unit = {},
    onGoVisual      : () -> Unit = {},
    onGoSettings    : () -> Unit = {},
    onBackToSolar   : () -> Unit = {}
) {
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
    val instances    by NexusInstanceManager.instances.collectAsState()
    val activeProfile by NexusAccountManager.activeProfile.collectAsState()
    val launchStatus  by NexusLaunchManager.status.collectAsState()

    val lastInst       = remember(instances) { NexusInstanceManager.getLastUsed() }
    val displayName    = lastInst?.name ?: instanceName.ifEmpty { "Nenhuma instância" }
    val modCount       = remember(lastInst) { lastInst?.let { NexusInstanceManager.getModCount(it.id) } ?: 0 }
    val isReadyToPlay  = lastInst?.isReady == true && activeProfile != null

    var launchError    by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Top Bar ──────────────────────────────────────────────────────────
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
                color         = NexusCyan,
                fontWeight    = FontWeight.Black,
                fontSize      = 15.sp,
                letterSpacing = 2.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatusChip("RAM", "${String.format("%.1f", metrics.ramGb)}/${String.format("%.0f", metrics.ramTotalGb)}GB", Color(0xFF00E676))
                StatusChip("CPU", "${metrics.cpuPercent}%", NexusCyan)
                StatusChip("FPS", "${metrics.fpsCurrent}", NexusOrange)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Launch error / status message ────────────────────────────────────
        if (launchStatus.state == NexusLaunchManager.LaunchState.ERROR) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFCF4455).copy(0.12f))
                .border(1.dp, Color(0xFFCF4455).copy(0.4f), RoundedCornerShape(8.dp))
                .padding(10.dp)) {
                Text(launchStatus.errorMsg, color = Color(0xFFCF4455), fontSize = 11.sp)
            }
            Spacer(Modifier.height(8.dp))
        }

        // ── Main cards row ───────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Last instance card
            NexusHomeCard(modifier = Modifier.weight(1f)) {
                Text("ÚLTIMA INSTÂNCIA", color = NexusCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Text(displayName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    when {
                        lastInst == null     -> "Crie uma instância"
                        !lastInst.isReady    -> "⬇ Aguardando instalação"
                        modCount > 0         -> "$modCount mod(s) · ${lastInst.loader}"
                        else                 -> "${lastInst.mcVersion} · ${lastInst.loader}"
                    },
                    color    = TextSecondary, fontSize = 10.sp
                )
                if (activeProfile == null) {
                    Spacer(Modifier.height(6.dp))
                    Text("⚠ Sem conta ativa", color = NexusOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallActionButton("Configurar") { onConfigInstance() }
                    SmallActionButton("Gerenciar")  { onManageInstance() }
                }
            }

            // Launch button column
            Column(
                modifier            = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick   = {
                        val inst = lastInst
                        if (inst != null) {
                            scope.launch {
                                launchError = ""
                                val ok = NexusLaunchManager.launch(context, inst)
                                if (!ok) launchError = launchStatus.errorMsg
                                else onLaunchGame()
                            }
                        } else {
                            onGoInstances()
                        }
                    },
                    modifier  = Modifier.fillMaxWidth().height(80.dp),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = ButtonDefaults.buttonColors(
                        backgroundColor = if (isReadyToPlay) NexusOrange else NexusCyan.copy(0.5f)
                    ),
                    elevation = ButtonDefaults.elevation(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(when (launchStatus.state) {
                            NexusLaunchManager.LaunchState.CHECKING   -> "⏳"
                            NexusLaunchManager.LaunchState.LAUNCHING  -> "🚀"
                            NexusLaunchManager.LaunchState.RUNNING    -> "🎮"
                            else -> if (lastInst == null) "📂" else "🚀"
                        }, fontSize = 22.sp)
                        Text(
                            when (launchStatus.state) {
                                NexusLaunchManager.LaunchState.CHECKING  -> "VERIFICANDO..."
                                NexusLaunchManager.LaunchState.LAUNCHING -> "INICIANDO..."
                                NexusLaunchManager.LaunchState.RUNNING   -> "RODANDO"
                                else -> if (lastInst == null) "CRIAR INSTÂNCIA" else "INICIAR JOGO"
                            },
                            color         = Color.White,
                            fontWeight    = FontWeight.Black,
                            fontSize      = 12.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    lastInst?.let { "${it.mcVersion} · ${it.loader}" } ?: "Nenhuma instância",
                    color = TextSecondary, fontSize = 10.sp
                )
            }

            // System status card
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
                SmallActionButton("Performance") { onBoost() }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Quick actions ────────────────────────────────────────────────────
        Text(
            "ATALHOS RÁPIDOS",
            color         = TextSecondary,
            fontSize      = 10.sp,
            letterSpacing = 1.5.sp,
            modifier      = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickCard("⚡", "Nexus Boost",   "Otimizar agora",       NexusCyan,         Modifier.weight(1f)) { onBoost() }
            QuickCard("🎨", "Visual",        "Shaders & Tema",       Color(0xFF7B61FF), Modifier.weight(1f)) { onTextures() }
            QuickCard("🧩", "Mods",          "$modCount mod(s)",     NexusOrange,       Modifier.weight(1f)) { onMods() }
            QuickCard("📊", "Relatórios",    "${metrics.fpsCurrent} FPS avg", Color(0xFF00E676), Modifier.weight(1f)) { onReports() }
        }

        Spacer(Modifier.height(16.dp))

        // ── Quick navigation ─────────────────────────────────────────────────
        Text(
            "NAVEGAÇÃO RÁPIDA",
            color         = TextSecondary,
            fontSize      = 10.sp,
            letterSpacing = 1.5.sp,
            modifier      = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("🌐", "Instâncias",   onGoInstances),
                Triple("👤", "Contas",        onGoAccounts),
                Triple("🖥",  "Visual",       onGoVisual),
                Triple("⚙",  "Config",        onGoSettings),
            ).forEach { (icon, label, action) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Obsidian)
                        .border(1.dp, NexusCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .clickable { action() }
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

        // ── Bottom status bar ─────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth().background(Color(0xFF080812)).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            BottomStatusItem("👤", activeProfile?.username ?: "Sem conta")
            BottomStatusItem("🧩", "$modCount mods")
            BottomStatusItem("🪐", "${instances.size} instâncias")
            BottomStatusItem("⚡", launchStatus.state.name.lowercase().replaceFirstChar { it.uppercase() })
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