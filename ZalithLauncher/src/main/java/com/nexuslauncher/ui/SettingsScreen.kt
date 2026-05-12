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
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * Tela de Configurações — Fase 1 (esqueleto).
 * A lógica completa será implementada na Fase 2.
 */
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text          = "Configurações".uppercase(),
            color         = NexusCyan,
            fontSize      = 22.sp,
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text     = "Em construção — Fase 2",
            color    = TextSecondary,
            fontSize = 14.sp
        )
    }
}
