package com.nexuslauncher.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nexuslauncher.core.NexusBoostEngine
import com.nexuslauncher.core.NexusInstanceManager
import com.nexuslauncher.core.NexusSystemMonitor
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.ui.*
import com.nexuslauncher.ui.solar.SolarSystemScreen
import com.nexuslauncher.ui.solar.SolarSystemViewModel

/**
 * NexusNavHost — Sistema de navegação completo.
 *
 * Rota raiz: SolarSystem (hub central).
 * Todas as screens recebem callbacks nomeados e dados reais via ViewModels.
 * InstancesScreen e ModsScreen compartilham instanceDir de estado.
 */
@Composable
fun NexusNavHost(
    navController : NavHostController,
    nexusDataStore: NexusDataStore
) {
    val solarVm: SolarSystemViewModel = viewModel()
    val metrics     by solarVm.systemMetrics.collectAsState()
    val tierResult  by solarVm.tierResult.collectAsState()
    val boostReport by solarVm.boostReport.collectAsState()

    // Shared state for instance<->mods drill-down
    var activeInstanceId  by remember { mutableStateOf("") }
    var activeInstanceDir by remember { mutableStateOf("") }
    var activeInstanceName by remember { mutableStateOf("") }

    NavHost(
        navController    = navController,
        startDestination = NexusRoute.SolarSystem.route
    ) {

        composable(NexusRoute.SolarSystem.route) {
            SolarSystemScreen(
                nexusDataStore   = nexusDataStore,
                vm               = solarVm,
                onPlanetSelected = { planetId ->
                    val route = when (planetId) {
                        PlanetId.NEXUS_PRIME    -> NexusRoute.Home.route
                        PlanetId.AETHERION      -> NexusRoute.Performance.route
                        PlanetId.LUMINA         -> NexusRoute.Visual.route
                        PlanetId.MODARA         -> NexusRoute.Mods.route
                        PlanetId.INSTARRION     -> NexusRoute.Instances.route
                        PlanetId.CHRONOS        -> NexusRoute.Reports.route
                        PlanetId.PERSONA        -> NexusRoute.Accounts.route
                        PlanetId.HELIOS_CONTROL -> NexusRoute.Settings.route
                    }
                    navController.navigate(route)
                }
            )
        }

        composable(NexusRoute.Home.route) {
            val lastInst = NexusInstanceManager.getLastUsed()
            HomeScreen(
                instanceName     = lastInst?.name ?: "Nenhuma instância",
                metrics          = metrics,
                onLaunchGame     = {
                    lastInst?.let { inst ->
                        // set active and navigate back to solar (game will launch from there)
                        NexusInstanceManager.setLastUsed(inst.id)
                    }
                },
                onConfigInstance = { navController.navigate(NexusRoute.Settings.route) },
                onManageInstance = { navController.navigate(NexusRoute.Instances.route) },
                onBoost          = { solarVm.triggerBoost(); navController.navigate(NexusRoute.Performance.route) },
                onTextures       = { navController.navigate(NexusRoute.Visual.route) },
                onMods           = { navController.navigate(NexusRoute.Mods.route) },
                onReports        = { navController.navigate(NexusRoute.Reports.route) },
                onGoInstances    = { navController.navigate(NexusRoute.Instances.route) },
                onGoAccounts     = { navController.navigate(NexusRoute.Accounts.route) },
                onGoVisual       = { navController.navigate(NexusRoute.Visual.route) },
                onGoSettings     = { navController.navigate(NexusRoute.Settings.route) },
                onBackToSolar    = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Performance.route) {
            PerformanceScreen(
                nexusDataStore         = nexusDataStore,
                metrics                = metrics,
                boostReport            = boostReport,
                onBoost                = { solarVm.triggerBoost() },
                onOpenReports          = { navController.navigate(NexusRoute.Reports.route) },
                onOpenAdvancedSettings = { navController.navigate(NexusRoute.Settings.route) },
                onBackToSolar          = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Visual.route) {
            VisualScreen(
                nexusDataStore       = nexusDataStore,
                onOpenSettings       = { navController.navigate(NexusRoute.Settings.route) },
                onOpenModsForShaders = { navController.navigate(NexusRoute.Mods.route) },
                onBackToSolar        = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Mods.route) {
            ModsScreen(
                nexusDataStore    = nexusDataStore,
                instanceDir       = activeInstanceDir,
                onBackToInstances = { navController.navigate(NexusRoute.Instances.route) },
                onOpenVisual      = { navController.navigate(NexusRoute.Visual.route) },
                onBackToSolar     = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Instances.route) {
            InstancesScreen(
                onLaunchGame        = { instanceId ->
                    val inst = NexusInstanceManager.instances.value.firstOrNull { it.id == instanceId }
                    if (inst != null) {
                        activeInstanceId  = inst.id
                        activeInstanceDir = inst.dirPath
                        activeInstanceName = inst.name
                        NexusInstanceManager.setLastUsed(instanceId)
                    }
                },
                onManageMods        = { instanceId ->
                    val inst = NexusInstanceManager.instances.value.firstOrNull { it.id == instanceId }
                    if (inst != null) {
                        activeInstanceId  = inst.id
                        activeInstanceDir = inst.dirPath
                        activeInstanceName = inst.name
                    }
                    navController.navigate(NexusRoute.Mods.route)
                },
                onChangeDirectories = { navController.navigate(NexusRoute.Settings.route) },
                onBackToSolar       = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Reports.route) {
            ReportsScreen(
                onOpenPerformance = { navController.navigate(NexusRoute.Performance.route) },
                onBoostFromReport = { solarVm.triggerBoost(); navController.navigate(NexusRoute.Performance.route) },
                onBackToSolar     = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Accounts.route) {
            AccountsScreen(
                nexusDataStore = nexusDataStore,
                onBackToHome   = { navController.navigate(NexusRoute.Home.route) },
                onGoInstances  = { navController.navigate(NexusRoute.Instances.route) },
                onBackToSolar  = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Settings.route) {
            SettingsScreen(
                nexusDataStore       = nexusDataStore,
                onOpenVisualSettings = { navController.navigate(NexusRoute.Visual.route) },
                onOpenReports        = { navController.navigate(NexusRoute.Reports.route) },
                onBackToSolar        = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }
    }
}