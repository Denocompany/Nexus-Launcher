package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.Graphite
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * AccountsScreen — Gerenciamento de contas Microsoft e Offline (Fase 3).
 * Planeta: PERSONA
 */
@Composable
fun AccountsScreen() {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text          = "CONTAS",
            color         = NexusCyan,
            fontSize      = 22.sp,
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text     = "Gerencie suas contas Microsoft e Offline",
            color    = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Microsoft ────────────────────────────────────────────────────
        androidx.compose.material.Button(
            onClick  = { /* TODO Fase 3: autenticação OAuth2 Microsoft */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors   = ButtonDefaults.buttonColors(backgroundColor = NexusCyan)
        ) {
            Text(
                text       = "Adicionar Conta Microsoft",
                color      = DeepVoid,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Offline ───────────────────────────────────────────────────────
        OutlinedButton(
            onClick  = { /* TODO Fase 3: conta offline local */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = NexusCyan)
        ) {
            Text(
                text     = "Adicionar Conta Offline",
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Divider(color = Graphite)

        Spacer(modifier = Modifier.height(20.dp))

        // ── Placeholder lista de contas ────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Graphite),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = "Nenhuma conta configurada",
                color    = TextSecondary.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text     = "Após adicionar uma conta, ela aparecerá aqui.\nA conta ativa será usada ao iniciar o jogo.",
            color    = TextSecondary.copy(alpha = 0.4f),
            fontSize = 11.sp
        )
    }
}
