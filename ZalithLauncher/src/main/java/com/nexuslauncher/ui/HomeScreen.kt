package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.components.NexusButton
import com.nexuslauncher.components.NexusCard
import com.nexuslauncher.components.NexusSmallCard
import com.nexuslauncher.components.NexusTextButton
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * Tela inicial do Nexus Launcher.
 * Exibe: título, última instância, status do sistema,
 * botão INICIAR JOGO e linha de atalhos rápidos.
 */
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Título principal ──────────────────────────────────────────────
        Text(
            text          = "NEXUS LAUNCHER",
            color         = NexusCyan,
            fontSize      = 26.sp,
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            textAlign     = TextAlign.Center
        )
        Text(
            text      = "Minecraft Java Edition",
            color     = TextSecondary,
            fontSize  = 13.sp,
            modifier  = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // ── Card: Última Instância ────────────────────────────────────────
        NexusCard(
            title   = "Última Instância",
            content = "Survival 1.18.1",
            extra   = {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    NexusTextButton(label = "Configurar",  onClick = { /* TODO Fase 2 */ })
                    Spacer(modifier = Modifier.width(8.dp))
                    NexusTextButton(label = "Gerenciar",   onClick = { /* TODO Fase 2 */ })
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Card: Status do Sistema ───────────────────────────────────────
        NexusCard(
            title   = "Status do Sistema",
            content = "",
            extra   = {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SystemStat(label = "FPS", value = "75")
                    SystemStat(label = "CPU", value = "28%")
                    SystemStat(label = "GPU", value = "47%")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Botão INICIAR JOGO ────────────────────────────────────────────
        NexusButton(
            label    = "🚀  INICIAR JOGO",
            onClick  = { /* TODO Fase 2: lançar o Minecraft */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Atalhos rápidos ───────────────────────────────────────────────
        Text(
            text      = "ATALHOS RÁPIDOS",
            color     = TextSecondary,
            fontSize  = 11.sp,
            letterSpacing = 1.5.sp,
            modifier  = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NexusSmallCard(
                title    = "Otimização",
                subtitle = "Modo Equilíbrio",
                modifier = Modifier.weight(1f)
            )
            NexusSmallCard(
                title    = "Texturas",
                subtitle = "Shaders HDR",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NexusSmallCard(
                title    = "Mods & Plugins",
                subtitle = "5 ativos",
                modifier = Modifier.weight(1f)
            )
            NexusSmallCard(
                title    = "Desempenho",
                subtitle = "FPS médio 76",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** Exibe um indicador de estatística (FPS, CPU, GPU). */
@Composable
private fun SystemStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = value,
            color      = NexusCyan,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text     = label,
            color    = TextSecondary,
            fontSize = 12.sp
        )
    }
}
