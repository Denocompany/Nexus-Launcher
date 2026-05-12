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
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.Graphite
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.TextSecondary

/** Enum com todas as abas disponíveis no Nexus Launcher. */
enum class NexusTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    PERFORMANCE("Performance", Icons.Filled.Star),
    VISUAL("Visual", Icons.Filled.Build),
    MODS("Mods", Icons.Filled.List),
    INSTANCES("Instâncias", Icons.Filled.Info),
    REPORTS("Relatórios", Icons.Filled.Settings)
}

/**
 * Barra de navegação inferior do Nexus Launcher.
 *
 * @param currentTab  Aba atualmente selecionada.
 * @param onTabSelected Callback chamado ao selecionar uma aba.
 */
@Composable
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
                selected         = selected,
                onClick          = { onTabSelected(tab) },
                label            = {
                    Text(
                        text     = tab.label,
                        fontSize = 10.sp,
                        color    = if (selected) NexusCyan else TextSecondary
                    )
                },
                icon             = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) NexusCyan else TextSecondary
                    )
                },
                alwaysShowLabel  = true,
                selectedContentColor   = NexusCyan,
                unselectedContentColor = TextSecondary
            )
        }
    }
}
