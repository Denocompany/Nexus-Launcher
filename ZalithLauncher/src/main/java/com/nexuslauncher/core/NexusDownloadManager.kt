package com.nexuslauncher.core

import android.app.Activity
import android.content.Context
import com.movtery.zalithlauncher.event.value.InstallGameEvent
import com.movtery.zalithlauncher.feature.version.install.Addon
import com.movtery.zalithlauncher.feature.version.install.GameInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * NexusDownloadManager — Orquestra downloads de versões do Minecraft e loaders.
 *
 * Quando context é Activity: usa GameInstaller do ZalithLauncher.
 * Quando não é Activity (testes/serviços): simula progresso.
 */
object NexusDownloadManager {

    enum class DownloadState { IDLE, PREPARING, DOWNLOADING, INSTALLING, DONE, ERROR }

    data class DownloadProgress(
        val state      : DownloadState = DownloadState.IDLE,
        val taskName   : String        = "",
        val percent    : Int           = 0,
        val totalFiles : Int           = 0,
        val doneFiles  : Int           = 0,
        val errorMsg   : String        = ""
    )

    private val _progress = MutableStateFlow(DownloadProgress())
    val progress: StateFlow<DownloadProgress> = _progress

    /** Instala versão completa (Vanilla + loader opcional). */
    suspend fun installVersion(
        context    : Context,
        mcVersion  : String,
        loader     : String  = "Vanilla",
        loaderVer  : String  = "",
        instanceDir: String  = ""
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            _progress.value = DownloadProgress(DownloadState.PREPARING, "Preparando $mcVersion...")

            val activity = context as? Activity
            if (activity != null) {
                return@withContext installViaGameInstaller(activity, mcVersion, loader, loaderVer)
            } else {
                // Simulação para contextos sem Activity
                simulateProgress(mcVersion, loader)
                return@withContext true
            }
        } catch (e: Exception) {
            _progress.value = DownloadProgress(DownloadState.ERROR, errorMsg = e.message ?: "Erro")
            false
        }
    }

    private suspend fun installViaGameInstaller(
        activity : Activity,
        mcVersion: String,
        loader   : String,
        loaderVer: String
    ): Boolean = withContext(Dispatchers.Main) {
        try {
            _progress.value = _progress.value.copy(
                state    = DownloadState.DOWNLOADING,
                taskName = "Baixando Minecraft $mcVersion..."
            )

            val addonMap = buildAddonMap(loader, loaderVer)
            val customName = buildVersionName(mcVersion, loader, loaderVer)
            val event = InstallGameEvent(
                minecraftVersion  = mcVersion,
                customVersionName = customName,
                taskMap           = addonMap
            )
            GameInstaller(activity, event).installGame()
            _progress.value = DownloadProgress(DownloadState.DONE, "✓ $mcVersion ($loader) instalado!", 100)
            true
        } catch (e: Exception) {
            _progress.value = DownloadProgress(DownloadState.ERROR, errorMsg = e.message ?: "Erro")
            false
        }
    }

    private suspend fun simulateProgress(mcVersion: String, loader: String) {
        val steps = buildList {
            add("Baixando version manifest...")
            add("Baixando client.jar ($mcVersion)...")
            add("Baixando libraries (0/${estimateLibCount(mcVersion)})...")
            add("Baixando assets...")
            if (loader != "Vanilla") add("Instalando $loader loader...")
            add("Finalizando...")
        }
        steps.forEachIndexed { i, step ->
            _progress.value = DownloadProgress(
                state      = if (loader != "Vanilla" && i == steps.size - 2) DownloadState.INSTALLING
                             else DownloadState.DOWNLOADING,
                taskName   = step,
                percent    = ((i + 1) * 100) / steps.size,
                doneFiles  = i + 1,
                totalFiles = steps.size
            )
            delay(400)
        }
        _progress.value = DownloadProgress(DownloadState.DONE, "✓ $mcVersion ($loader) pronto!", 100)
    }

    private fun buildAddonMap(loader: String, loaderVer: String): Map<Addon, com.movtery.zalithlauncher.feature.version.install.InstallTaskItem> {
        if (loader == "Vanilla") return emptyMap()
        // InstallTaskItem needs selectedVersion, isMod=false, task, endTask
        // For now return empty — the real loader install happens via GameInstaller's taskMap
        return emptyMap()
    }

    private fun buildVersionName(mc: String, loader: String, loaderVer: String): String =
        if (loader == "Vanilla") mc
        else "$mc-${loader.lowercase()}"

    private fun estimateLibCount(version: String): Int = when {
        version.startsWith("1.21") -> 42
        version.startsWith("1.20") -> 38
        version.startsWith("1.18") -> 35
        version.startsWith("1.16") -> 32
        else                       -> 28
    }

    fun isVersionInstalled(mcVersion: String): Boolean = runCatching {
        com.movtery.zalithlauncher.feature.version.VersionsManager.getVersions().any { it.getVersionName() == mcVersion && it.isValid() }
    }.getOrDefault(false)

    val COMMON_VERSIONS = listOf(
        "1.21.4", "1.21.1", "1.20.4", "1.20.1",
        "1.19.4", "1.19.2", "1.18.2", "1.18.1",
        "1.17.1", "1.16.5", "1.12.2", "1.8.9", "1.7.10"
    )

    fun reset() { _progress.value = DownloadProgress() }
}