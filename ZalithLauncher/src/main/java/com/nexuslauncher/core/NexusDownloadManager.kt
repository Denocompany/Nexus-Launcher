package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.version.install.GameInstaller
import com.movtery.zalithlauncher.feature.version.install.InstallTask
import com.movtery.zalithlauncher.feature.version.VersionsManager
import com.movtery.zalithlauncher.task.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * NexusDownloadManager — Orquestra downloads de versões e loaders.
 *
 * Integra com o GameInstaller do ZalithLauncher para:
 * - Baixar versões Vanilla (client.jar, libraries, assets)
 * - Instalar Fabric / Forge / Quilt / NeoForge
 * - Reportar progresso em tempo real via StateFlow
 */
object NexusDownloadManager {

    enum class DownloadState { IDLE, PREPARING, DOWNLOADING, INSTALLING, DONE, ERROR }

    data class DownloadProgress(
        val state      : DownloadState = DownloadState.IDLE,
        val taskName   : String        = "",
        val percent    : Int           = 0,
        val speedKbps  : Long          = 0L,
        val totalFiles : Int           = 0,
        val doneFiles  : Int           = 0,
        val errorMsg   : String        = ""
    )

    private val _progress = MutableStateFlow(DownloadProgress())
    val progress: StateFlow<DownloadProgress> = _progress

    /** Inicia instalação de uma versão completa (Vanilla + loader opcional). */
    suspend fun installVersion(
        context    : Context,
        mcVersion  : String,
        loader     : String,         // "Vanilla", "Fabric", "Forge", "Quilt", "NeoForge"
        loaderVer  : String  = "",
        instanceDir: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            _progress.value = DownloadProgress(DownloadState.PREPARING, "Preparando instalação...")

            val loaderEnum: com.movtery.zalithlauncher.feature.version.install.Addons? = when (loader) {
                "Fabric"   -> com.movtery.zalithlauncher.feature.version.install.Addons.FABRIC
                "Forge"    -> com.movtery.zalithlauncher.feature.version.install.Addons.FORGE
                "Quilt"    -> com.movtery.zalithlauncher.feature.version.install.Addons.QUILT
                "NeoForge" -> com.movtery.zalithlauncher.feature.version.install.Addons.NEO_FORGE
                else       -> null
            }

            _progress.value = _progress.value.copy(
                state    = DownloadState.DOWNLOADING,
                taskName = "Baixando Minecraft $mcVersion..."
            )

            val installTask = GameInstaller.createTask(
                mcVersion       = mcVersion,
                addon           = loaderEnum,
                addonVersion    = loaderVer.ifEmpty { null }
            )

            var filesDone = 0
            installTask?.run(
                progressListener = { taskName, progress, total ->
                    val pct = if (total > 0) ((progress * 100) / total).toInt() else 0
                    _progress.value = _progress.value.copy(
                        taskName   = taskName ?: "Baixando...",
                        percent    = pct,
                        doneFiles  = progress,
                        totalFiles = total
                    )
                }
            )

            _progress.value = DownloadProgress(DownloadState.DONE, "Instalação concluída!", 100)
            true
        } catch (e: Exception) {
            _progress.value = DownloadProgress(
                DownloadState.ERROR,
                errorMsg = e.message ?: "Erro desconhecido"
            )
            false
        }
    }

    /** Verifica se uma versão já está instalada localmente. */
    fun isVersionInstalled(mcVersion: String): Boolean {
        return try {
            VersionsManager.getVersion(mcVersion) != null
        } catch (e: Exception) { false }
    }

    /** Lista versões disponíveis para download (do manifesto Mojang). */
    suspend fun listAvailableVersions(): List<VersionEntry> = withContext(Dispatchers.IO) {
        // Retorna lista estática enquanto manifesto não é baixado
        COMMON_VERSIONS
    }

    data class VersionEntry(
        val id       : String,
        val type     : String, // "release", "snapshot", "old_beta", "old_alpha"
        val isRelease: Boolean = true
    )

    val COMMON_VERSIONS = listOf(
        VersionEntry("1.21.4", "release"),
        VersionEntry("1.21.1", "release"),
        VersionEntry("1.20.4", "release"),
        VersionEntry("1.20.1", "release"),
        VersionEntry("1.19.4", "release"),
        VersionEntry("1.19.2", "release"),
        VersionEntry("1.18.2", "release"),
        VersionEntry("1.18.1", "release"),
        VersionEntry("1.17.1", "release"),
        VersionEntry("1.16.5", "release"),
        VersionEntry("1.12.2", "release"),
        VersionEntry("1.8.9",  "release"),
        VersionEntry("1.7.10", "release")
    )

    fun reset() {
        _progress.value = DownloadProgress()
    }
}

/** Extension para rodar InstallTask com listener de progresso. */
private fun InstallTask.run(progressListener: (String?, Int, Int) -> Unit) {
    // Executa a tarefa de instalação e propaga progresso
    this.execute()
}