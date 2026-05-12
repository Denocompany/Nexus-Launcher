package com.nexuslauncher.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nexuslauncher.ui.AccountsScreen
import com.nexuslauncher.ui.HomeScreen
import com.nexuslauncher.ui.InstancesScreen
import com.nexuslauncher.ui.ModsScreen
import com.nexuslauncher.ui.PerformanceScreen
import com.nexuslauncher.ui.ReportsScreen
import com.nexuslauncher.ui.SettingsScreen
import com.nexuslauncher.ui.VisualScreen
import com.nexuslauncher.ui.solar.SolarSystemScreen

@Composable
fun NexusNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController    = navController,
        startDestination = NexusRoute.SolarSystem.route,
        modifier         = modifier
    ) {
        composable(NexusRoute.SolarSystem.route) {
            SolarSystemScreen(
                onPlanetSelected = { planet ->
                    when (planet) {
                        PlanetId.NEXUS_PRIME    -> navController.navigate(NexusRoute.Home.route)
                        PlanetId.AETHERION      -> navController.navigate(NexusRoute.Performance.route)
                        PlanetId.LUMINA         -> navController.navigate(NexusRoute.Visual.route)
                        PlanetId.MODARA         -> navController.navigate(NexusRoute.Mods.route)
                        PlanetId.INSTARRION     -> navController.navigate(NexusRoute.Instances.route)
                        PlanetId.CHRONOS        -> navController.navigate(NexusRoute.Reports.route)
                        PlanetId.PERSONA        -> navController.navigate(NexusRoute.Accounts.route)
                        PlanetId.HELIOS_CONTROL -> navController.navigate(NexusRoute.Settings.route)
                    }
                }
            )
        }

        composable(NexusRoute.Home.route) {
            HomeScreen(
                onConfigInstance = { navController.navigate(NexusRoute.Instances.route) },
                onManageInstance = { navController.navigate(NexusRoute.Instances.route) },
                onBoost          = { navController.navigate(NexusRoute.Performance.route) },
                onTextures       = { navController.navigate(NexusRoute.Mods.route) },
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
                onManageMods        = { navController.navigate(NexusRoute.Mods.route) },
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
                onBoostFromReport = { navController.navigate(NexusRoute.Performance.route) },
                onBackToSolar     = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Accounts.route) {
            AccountsScreen(
                onBackToHome  = { navController.navigate(NexusRoute.Home.route) },
                onGoInstances = { navController.navigate(NexusRoute.Instances.route) },
                onBackToSolar = {
                    navController.navigate(NexusRoute.SolarSystem.route) {
                        popUpTo(NexusRoute.SolarSystem.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NexusRoute.Settings.route) {
            SettingsScreen(
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
