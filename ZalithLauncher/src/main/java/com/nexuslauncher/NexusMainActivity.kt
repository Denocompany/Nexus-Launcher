package com.nexuslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.navigation.NexusNavHost
import com.nexuslauncher.ui.theme.NexusTheme

/**
 * NexusMainActivity — Activity principal do Nexus Launcher (v1.5.0.0).
 *
 * Usa Jetpack Compose Navigation (NavHost + NexusRoute sealed class)
 * para navegação 100% type-safe entre todos os planetas do Sistema Solar.
 * Integra Jetpack DataStore (Preferences) via NexusDataStore para
 * persistência de todas as configurações do launcher.
 */
class NexusMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexusTheme {
                val nexusDataStore = remember { NexusDataStore(applicationContext) }
                val navController  = rememberNavController()
                NexusNavHost(
                    navController  = navController,
                    nexusDataStore = nexusDataStore
                )
            }
        }
    }
}
