package com.nexuslauncher.ui.solar

import androidx.compose.ui.graphics.Color

/**
 * PlanetNode — Modelo de dados de um planeta no Sistema Solar Nexus.
 * Corresponde exatamente aos planetas da UIX web.
 */
data class PlanetNode(
    val id: String,
    val name: String,
    val description: String,
    val color: Color,
    val colorHex: String,
    val orbitRadius: Float,
    val orbitSpeed: Float,
    val size: Float,
    val moons: List<String>
)

object SolarSystem {

    val SUN = PlanetNode(
        id = "sun", name = "NÚCLEO NEXUS", description = "Home",
        color = Color(0xFFFFB300), colorHex = "#FFB300",
        orbitRadius = 0f, orbitSpeed = 0f, size = 48f, moons = emptyList()
    )

    val PLANETS = listOf(
        PlanetNode("nexus",      "NEXUS PRIME",       "Home & Status",  Color(0xFF00E5FF), "#00E5FF", 110f, 0.20f, 14f, listOf("Sessões","Alertas","Atalhos")),
        PlanetNode("aetherion",  "AETHERION",         "Performance",    Color(0xFFFF6D00), "#FF6D00", 155f, 0.15f, 12f, listOf("Perfis","CPU/GPU","Memória","Drivers")),
        PlanetNode("lumina",     "LUMINA",            "Visual",         Color(0xFF2979FF), "#2979FF", 200f, 0.10f, 13f, listOf("Tema","Fundos","Animações","Acessibilidade")),
        PlanetNode("modara",     "MODARA",            "Mods",           Color(0xFF00E676), "#00E676", 245f, 0.08f, 11f, listOf("Instalados","Atualizações","Dependências","Importar")),
        PlanetNode("curseforge", "CURSEFORGE ORBITAL","Baixar Mods",    Color(0xFFFF1744), "#FF1744", 290f, 0.07f, 14f, listOf("Mods","Modpacks","Shaders","Recursos","Mundos")),
        PlanetNode("instarrion", "INSTARRION",        "Instâncias",    Color(0xFF78909C), "#78909C", 335f, 0.06f, 12f, listOf("Lista","Caminhos","Exportar")),
        PlanetNode("chronos",    "CHRONOS",           "Relatórios",    Color(0xFFB0BEC5), "#B0BEC5", 380f, 0.05f, 10f, listOf("Sessão Atual","Histórico","Exportar")),
        PlanetNode("persona",    "PERSONA",           "Contas",        Color(0xFF1E88E5), "#1E88E5", 425f, 0.04f, 13f, listOf("Microsoft","Offline","Skins")),
        PlanetNode("cloudnexus", "CLOUD NEXUS",       "Backups",       Color(0xFF80D8FF), "#80D8FF", 468f, 0.035f,11f, listOf("Backup","Restauração","Histórico")),
        PlanetNode("labx",       "LAB-X",             "Experimental",  Color(0xFFAA00FF), "#AA00FF", 510f, 0.030f, 9f, listOf("Patches","Render Nativo","Suporte")),
        PlanetNode("helios",     "HELIOS CONTROL",    "Configurações", Color(0xFFFFD600), "#FFD600", 550f, 0.020f,14f, listOf("Vídeo","Controles","Jogo","Launcher","Java"))
    )
}
