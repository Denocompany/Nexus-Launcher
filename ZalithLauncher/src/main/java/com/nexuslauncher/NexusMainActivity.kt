package com.nexuslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.nexuslauncher.components.NexusBottomNav
import com.nexuslauncher.components.NexusTab
import com.nexuslauncher.ui.*
import com.nexuslauncher.ui.theme.NexusTheme

/**
 * NexusMainActivity — Activity principal do Nexus Launcher.
 *
 * Ponto de entrada da nova UI em Jetpack Compose.
 * Não interfere com a MainActivity original (net.kdt.pojavlaunch.MainActivity),
 * que continua sendo usada pelo runtime do Minecraft.
 *
 * Para ativar esta Activity como launcher, adicione ao AndroidManifest.xml:
 *   <activity android:name="com.nexuslauncher.NexusMainActivity"
 *             android:exported="true">
 *     <intent-filter>
 *       <action android:name="android.intent.action.MAIN"/>
 *       <category android:name="android.intent.category.LAUNCHER"/>
 *     </intent-filter>
 *   </activity>
 */
class NexusMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexusRoot()
        }
    }
}

/**
 * NexusRoot — composable raiz que gerencia o estado de navegação entre abas.
 * Usa um Scaffold com BottomNavigation + estado interno (sem Navigation lib ainda).
 */
@Composable
fun NexusRoot() {
    // Estado da aba atual
    var currentTab by remember { mutableStateOf(NexusTab.HOME) }

    NexusTheme {
        Scaffold(
            modifier     = Modifier.fillMaxSize(),
            bottomBar    = {
                NexusBottomNav(
                    currentTab    = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                // Navegação simples por when — sem biblioteca Navigation ainda
                when (currentTab) {
                    NexusTab.HOME        -> HomeScreen()
                    NexusTab.PERFORMANCE -> PerformanceScreen()
                    NexusTab.VISUAL      -> VisualScreen()
                    NexusTab.MODS        -> ModsScreen()
                    NexusTab.INSTANCES   -> InstancesScreen()
                    NexusTab.REPORTS     -> ReportsScreen()
                }
            }
        }
    }
}
