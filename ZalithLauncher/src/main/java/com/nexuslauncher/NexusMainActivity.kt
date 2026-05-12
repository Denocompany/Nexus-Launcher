package com.nexuslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.nexuslauncher.navigation.NexusNavHost
import com.nexuslauncher.ui.theme.NexusTheme

/**
 * NexusMainActivity — Activity principal do Nexus Launcher (Fase 4).
 *
 * Usa Jetpack Compose Navigation (NavHost + NexusRoute sealed class)
 * para navegação 100% type-safe entre todos os planetas do Sistema Solar.
 */
class NexusMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexusTheme {
                val navController = rememberNavController()
                NexusNavHost(navController = navController)
            }
        }
    }
}
