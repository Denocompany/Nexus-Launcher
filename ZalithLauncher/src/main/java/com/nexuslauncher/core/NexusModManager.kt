package com.nexuslauncher.core

import java.io.File

/**
 * NexusModManager — gerencia a lista de mods ativos para uma instância.
 * Fase 1: estrutura de dados e esqueleto de operações.
 * A integração real com o sistema de arquivos será feita na Fase 2.
 */
data class NexusMod(
    val id: String,
    val name: String,
    val version: String,
    val isEnabled: Boolean = true,
    val filePath: String = ""
)

object NexusModManager {

    private val loadedMods = mutableListOf<NexusMod>()

    /** Retorna a lista de mods carregados na instância atual. */
    fun getMods(): List<NexusMod> = loadedMods.toList()

    /** Retorna apenas os mods ativos. */
    fun getActiveMods(): List<NexusMod> = loadedMods.filter { it.isEnabled }

    /** Adiciona um mod à lista (sem persistência ainda — Fase 2). */
    fun addMod(mod: NexusMod) {
        if (loadedMods.none { it.id == mod.id }) {
            loadedMods.add(mod)
        }
    }

    /** Remove um mod pelo ID. */
    fun removeMod(id: String) {
        loadedMods.removeAll { it.id == id }
    }

    /** Retorna o total de mods ativos como string formatada. */
    fun activeCountLabel(): String {
        val count = getActiveMods().size
        return "$count ativo${if (count != 1) "s" else ""}"
    }
}
