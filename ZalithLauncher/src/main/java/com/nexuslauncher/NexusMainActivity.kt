package com.nexuslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.nexuslauncher.core.NexusAccountManager
import com.nexuslauncher.core.NexusInstanceManager
import com.nexuslauncher.core.NexusSessionTracker
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.navigation.NexusNavHost
import com.nexuslauncher.ui.theme.NexusTheme
import java.io.File

/**
 * NexusMainActivity — Activity principal do Nexus Launcher (v2.0.0.0).
 *
 * Inicializa:
 * - NexusInstanceManager (lê/cria instâncias em disco)
 * - NexusAccountManager (sincroniza com AccountsManager do ZalithLauncher)
 * - NexusSessionTracker (carrega histórico de sessões)
 * - NexusDataStore (preferências via Jetpack DataStore)
 * - NexusNavHost (navegação Compose completa)
 */
class NexusMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar subsistemas
        NexusInstanceManager.init(this)
        NexusAccountManager.loadAccounts()

        val baseDir = File(getExternalFilesDir(null), "NexusLauncher").also { it.mkdirs() }
        NexusSessionTracker.init(baseDir)

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