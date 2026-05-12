package com.nexuslauncher.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.ui.theme.NexusCyan

/**
 * Botão primário do Nexus Launcher.
 *
 * @param label   Texto exibido no botão.
 * @param onClick Ação disparada ao clicar.
 * @param modifier Modifier opcional.
 */
@Composable
fun NexusButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick  = onClick,
        modifier = modifier,
        shape    = RoundedCornerShape(8.dp),
        colors   = ButtonDefaults.buttonColors(
            backgroundColor = NexusCyan,
            contentColor    = Color.Black
        )
    ) {
        Text(
            text       = label,
            fontWeight = FontWeight.Bold,
            fontSize   = 14.sp,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Botão de texto secundário — usado em ações dentro de cards.
 *
 * @param label   Texto do botão.
 * @param onClick Ação disparada ao clicar.
 */
@Composable
fun NexusTextButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick  = onClick,
        modifier = modifier,
        colors   = ButtonDefaults.textButtonColors(contentColor = NexusCyan)
    ) {
        Text(
            text     = label,
            fontSize = 13.sp
        )
    }
}
