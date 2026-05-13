package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import com.movtery.zalithlauncher.feature.version.Version
import com.movtery.zalithlauncher.feature.version.VersionsManager
import com.movtery.zalithlauncher.launch.LaunchGame
import com.movtery.zalithlauncher.setting.AllSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * NexusLaunchManager — Inicia o Minecraft via LaunchGame.preLaunch().
 *
 * API real: LaunchGame.preLaunch(context, version) — launches via ContextAwareDoneListener.
 */
object NexusLaunchManager {

    enum class LaunchState { IDLE, CHECKING, LAUNCHING, RUNNING, STOPPED, ERROR }

    data class LaunchStatus(
        val state   : LaunchState = LaunchState.IDLE,
        val message : String      = "",
        val errorMsg: String      = ""
    )

    private val _status = MutableStateFlow(LaunchStatus())
    val status: StateFlow<LaunchStatus> = _status

    /** Verifica pré-requisitos antes de lançar. */
    fun checkReadiness(instance: NexusInstanceManager.NexusInstance): ReadinessResult {
        val issues = mutableListOf<String>()

        val account = try { AccountsManager.currentAccount } catch (e: Exception) { null }
        if (account == null) {
            issues += "Sem conta configurada. Vá em PERSONA e adicione uma conta."
        }

        if (!instance.isReady) {
            issues += "Versão ${instance.mcVersion} não instalada. Instale em INSTARRION."
        }

        return ReadinessResult(issues.isEmpty(), issues)
    }

    data class ReadinessResult(val isReady: Boolean, val issues: List<String>)

    /** Lança o Minecraft para a instância especificada. */
    suspend fun launch(
        context : Context,
        instance: NexusInstanceManager.NexusInstance
    ): Boolean = withContext(Dispatchers.Main) {
        try {
            _status.value = LaunchStatus(LaunchState.CHECKING, "Verificando...")

            val readiness = checkReadiness(instance)
            if (!readiness.isReady) {
                _status.value = LaunchStatus(LaunchState.ERROR, errorMsg = readiness.issues.joinToString("\n"))
                return@withContext false
            }

            _status.value = LaunchStatus(LaunchState.LAUNCHING, "Iniciando Minecraft ${instance.mcVersion}...")

            // Configurar diretório do jogo para esta instância
            withContext(Dispatchers.IO) {
                AllSettings.gamePath.put(instance.dirPath).save()
            }

            // Obter objeto Version do ZalithLauncher
            val version: Version? = withContext(Dispatchers.IO) {
                try { VersionsManager.getVersion(instance.mcVersion) }
                catch (e: Exception) { null }
            }

            if (version == null) {
                _status.value = LaunchStatus(LaunchState.ERROR, errorMsg = "Versão ${instance.mcVersion} não encontrada.")
                return@withContext false
            }

            // Marcar como última usada
            NexusInstanceManager.setLastUsed(instance.id)

            // Lançar via LaunchGame.preLaunch() — API real do ZalithLauncher
            LaunchGame.preLaunch(context, version)

            _status.value = LaunchStatus(LaunchState.RUNNING, "Minecraft rodando!")
            true

        } catch (e: Exception) {
            _status.value = LaunchStatus(LaunchState.ERROR, errorMsg = "Falha ao iniciar: ${e.message}")
            false
        }
    }

    fun reset() { _status.value = LaunchStatus() }
}