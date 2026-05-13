package com.nexuslauncher.core

import android.content.Context
import android.content.Intent
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import com.movtery.zalithlauncher.feature.version.Version
import com.movtery.zalithlauncher.feature.version.VersionsManager
import com.movtery.zalithlauncher.launch.LaunchGame
import com.movtery.zalithlauncher.plugins.renderer.RendererPluginManager
import com.movtery.zalithlauncher.setting.AllSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * NexusLaunchManager — Inicia o Minecraft com todos os parâmetros corretos.
 *
 * Integra com o LaunchGame do ZalithLauncher para:
 * - Montar classpath + libraries + natives
 * - Passar conta ativa (offline ou Microsoft)
 * - Aplicar renderer selecionado (GL4ES / Vulkan)
 * - Configurar JVM args e RAM
 */
object NexusLaunchManager {

    enum class LaunchState { IDLE, CHECKING, LAUNCHING, RUNNING, STOPPED, ERROR }

    data class LaunchStatus(
        val state    : LaunchState = LaunchState.IDLE,
        val message  : String     = "",
        val errorMsg : String     = ""
    )

    private val _status = MutableStateFlow(LaunchStatus())
    val status: StateFlow<LaunchStatus> = _status

    /** Verifica pré-requisitos antes de lançar o jogo. */
    fun checkReadiness(instance: NexusInstanceManager.NexusInstance): ReadinessResult {
        val checks = mutableListOf<String>()
        var ready  = true

        // Verificar conta ativa
        val account = try { AccountsManager.getCurrentAccount() } catch (e: Exception) { null }
        if (account == null) {
            checks += "Nenhuma conta configurada. Vá em PERSONA e adicione uma conta."
            ready = false
        }

        // Verificar se instância está pronta
        if (!instance.isReady) {
            checks += "Versão ${instance.mcVersion} não instalada. Instale em INSTARRION."
            ready = false
        }

        // Verificar runtime Java
        val hasRuntime = try {
            com.movtery.zalithlauncher.feature.unpack.Jre.checkRuntimes()
            true
        } catch (e: Exception) { false }
        if (!hasRuntime) {
            checks += "Runtime Java não encontrado. O launcher baixará automaticamente."
            ready = false
        }

        return ReadinessResult(ready, checks)
    }

    data class ReadinessResult(val isReady: Boolean, val issues: List<String>)

    /** Lança o Minecraft para a instância especificada. */
    suspend fun launch(
        context    : Context,
        instance   : NexusInstanceManager.NexusInstance,
        renderer   : String = "auto"  // "auto", "gl4es", "vulkan", "virgl"
    ): Boolean = withContext(Dispatchers.Main) {
        try {
            _status.value = LaunchStatus(LaunchState.CHECKING, "Verificando pré-requisitos...")

            val readiness = checkReadiness(instance)
            if (!readiness.isReady) {
                _status.value = LaunchStatus(
                    LaunchState.ERROR,
                    errorMsg = readiness.issues.joinToString("\n")
                )
                return@withContext false
            }

            _status.value = LaunchStatus(LaunchState.LAUNCHING, "Iniciando Minecraft ${instance.mcVersion}...")

            // Configurar renderer
            applyRenderer(renderer)

            // Configurar diretório do jogo
            AllSettings.gamePath.put(instance.dirPath)

            // Obter versão
            val version: Version? = try {
                VersionsManager.getVersion(instance.mcVersion)
            } catch (e: Exception) { null }

            if (version == null) {
                _status.value = LaunchStatus(LaunchState.ERROR, errorMsg = "Versão ${instance.mcVersion} não encontrada.")
                return@withContext false
            }

            // Configurar conta
            val account = try { AccountsManager.getCurrentAccount() } catch (e: Exception) { null }

            // Lançar jogo via LaunchGame do ZalithLauncher
            withContext(Dispatchers.IO) {
                LaunchGame.launch(context, version, account)
            }

            NexusInstanceManager.setLastUsed(instance.id)
            _status.value = LaunchStatus(LaunchState.RUNNING, "Minecraft rodando!")
            true

        } catch (e: Exception) {
            _status.value = LaunchStatus(
                LaunchState.ERROR,
                errorMsg = "Falha ao iniciar: ${e.message}"
            )
            false
        }
    }

    /** Aplica o renderer selecionado nas configurações do ZalithLauncher. */
    private fun applyRenderer(renderer: String) {
        when (renderer.lowercase()) {
            "vulkan", "zink" -> {
                try {
                    val plugins = RendererPluginManager.getRenderers()
                    val zink = plugins.firstOrNull {
                        it.name.contains("zink", ignoreCase = true) ||
                        it.name.contains("vulkan", ignoreCase = true)
                    }
                    zink?.let { RendererPluginManager.setCurrentRenderer(it) }
                } catch (e: Exception) { /* fallback */ }
            }
            "virgl" -> {
                try {
                    val plugins = RendererPluginManager.getRenderers()
                    val virgl = plugins.firstOrNull { it.name.contains("virgl", ignoreCase = true) }
                    virgl?.let { RendererPluginManager.setCurrentRenderer(it) }
                } catch (e: Exception) { /* fallback */ }
            }
            "gl4es" -> {
                try {
                    val plugins = RendererPluginManager.getRenderers()
                    val gl4es = plugins.firstOrNull { it.name.contains("gl4es", ignoreCase = true) }
                    gl4es?.let { RendererPluginManager.setCurrentRenderer(it) }
                } catch (e: Exception) { /* fallback */ }
            }
            // "auto" usa o que estiver configurado
        }
    }

    fun reset() { _status.value = LaunchStatus() }
}