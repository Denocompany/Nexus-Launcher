package com.nexuslauncher.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextPrimary

/**
 * Cartão padrão do Nexus Launcher.
 *
 * @param title   Título exibido em ciano na parte superior do card.
 * @param content Conteúdo/descrição exibido em branco abaixo do título.
 * @param modifier Modifier opcional para personalização externa.
 * @param extra   Slot composable opcional para conteúdo adicional (botões, linhas, etc).
 */
@Composable
fun NexusCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    extra: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Obsidian)
            .padding(16.dp)
    ) {
        Text(
            text       = title,
            color      = NexusCyan,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        Text(
            text     = content,
            color    = TextPrimary,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        extra?.invoke()
    }
}

/**
 * Versão do NexusCard com título grande — para cards de atalho menores.
 */
@Composable
fun NexusSmallCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Obsidian)
            .padding(12.dp)
    ) {
        Text(
            text       = title,
            color      = NexusCyan,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
        Text(
            text     = subtitle,
            color    = Color(0xFFA0A0B0),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
