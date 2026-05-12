package com.nexuslauncher.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.nexuslauncher.ui.theme.Graphite
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.TextSecondary

/**
 * NexusTab — abas da navegação clássica (Fase 1).
 *
 * @deprecated Substituído pelo Sistema Solar (SolarSystemScreen — Fase 3).
 *             Mantido para referência histórica. Não utilizar em código novo.
 */
@Deprecated(
    message = "Substituído por SolarSystemScreen (Fase 3). Use a navegação por planetas.",
    level   = DeprecationLevel.WARNING
)
enum class NexusTab(val label: String, val icon: ImageVector) {
    HOME("Home",            Icons.Filled.Home),
    PERFORMANCE("Performance", Icons.Filled.Star),
    VISUAL("Visual",        Icons.Filled.Build),
    MODS("Mods",            Icons.Filled.List),
    INSTANCES("Instâncias", Icons.Filled.Info),
    REPORTS("Relatórios",   Icons.Filled.Settings)
}

/**
 * NexusBottomNav — barra de navegação inferior clássica (Fase 1).
 *
 * @deprecated Substituído pelo Sistema Solar (SolarSystemScreen — Fase 3).
 */
@Deprecated(
    message = "Substituído por SolarSystemScreen (Fase 3). Use a navegação por planetas.",
    level   = DeprecationLevel.WARNING
)
@Composable
@Suppress("DEPRECATION")
fun NexusBottomNav(
    currentTab: NexusTab,
    onTabSelected: (NexusTab) -> Unit
) {
    BottomNavigation(
        backgroundColor = Graphite,
        contentColor    = NexusCyan
    ) {
        NexusTab.values().forEach { tab ->
            val selected = tab == currentTab
            BottomNavigationItem(
                selected               = selected,
                onClick                = { onTabSelected(tab) },
                label                  = {
                    Text(tab.label, fontSize = 10.sp,
                        color = if (selected) NexusCyan else TextSecondary)
                },
                icon                   = {
                    Icon(tab.icon, contentDescription = tab.label,
                        tint = if (selected) NexusCyan else TextSecondary)
                },
                alwaysShowLabel        = true,
                selectedContentColor   = NexusCyan,
                unselectedContentColor = TextSecondary
            )
        }
    }
}
