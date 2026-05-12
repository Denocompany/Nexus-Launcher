package com.nexuslauncher.navigation

enum class PlanetId {
    NEXUS_PRIME,
    AETHERION,
    LUMINA,
    MODARA,
    INSTARRION,
    CHRONOS,
    PERSONA,
    HELIOS_CONTROL
}

sealed class NexusRoute(val route: String) {
    object SolarSystem : NexusRoute("solar_system")
    object Home        : NexusRoute("home")
    object Performance : NexusRoute("performance")
    object Visual      : NexusRoute("visual")
    object Mods        : NexusRoute("mods")
    object Instances   : NexusRoute("instances")
    object Reports     : NexusRoute("reports")
    object Accounts    : NexusRoute("accounts")
    object Settings    : NexusRoute("settings")
}
