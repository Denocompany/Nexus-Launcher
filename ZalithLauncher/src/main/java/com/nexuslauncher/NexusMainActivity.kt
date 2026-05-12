package com.nexuslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nexuslauncher.ui.solar.SolarSystemScreen

/**
 * NexusMainActivity — Activity principal do Nexus Launcher (Fase 3).
 *
 * Exibe o Sistema Solar como tela de navegação principal.
 *
 * Para ativar como launcher principal, substitua o intent-filter
 * de SplashActivity por este no AndroidManifest.xml quando pronto.
 */
class NexusMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SolarSystemScreen()
        }
    }
}
