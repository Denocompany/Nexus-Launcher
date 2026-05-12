package com.nexuslauncher.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Nexus Design System ── Cores principais
val DeepVoid     = Color(0xFF0A0A0D)  // Fundo principal
val Obsidian     = Color(0xFF121216)  // Cartões
val Graphite     = Color(0xFF1C1C22)  // Superfícies interativas
val TextPrimary  = Color(0xFFFFFFFF)  // Texto principal
val TextSecondary= Color(0xFFA0A0B0)  // Texto secundário
val NexusCyan    = Color(0xFF00E5FF)  // Acento principal
val NexusOrange  = Color(0xFFFF6D00)  // Acento secundário

private val NexusDarkColors = darkColors(
    primary          = NexusCyan,
    primaryVariant   = NexusOrange,
    secondary        = NexusOrange,
    background       = DeepVoid,
    surface          = Obsidian,
    onPrimary        = Color.Black,
    onSecondary      = Color.Black,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    error            = Color(0xFFCF6679)
)

// Tipografia padrão — deixada para customização futura
private val NexusTypography = Typography()

/**
 * Tema global do Nexus Launcher.
 * Envolva toda a UI com este composable para aplicar as cores e tipografia.
 */
@Composable
fun NexusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors     = NexusDarkColors,
        typography = NexusTypography,
        content    = content
    )
}
