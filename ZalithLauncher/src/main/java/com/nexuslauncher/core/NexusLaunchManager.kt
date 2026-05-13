package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import com.movtery.zalithlauncher.feature.version.Version
import com.movtery.zalithlauncher.feature.version.VersionsManager
import com.movtery.zalithlauncher.launch.LaunchGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * NexusLaunchManager — Lança o Minecraft via LaunchGame.preLaunch().
 *
 * APIs reais:
 * - LaunchGame.preLaunch(context: Context, version: Version)
 * - VersionsManager.getVersions()          → List<Version>
 * - VersionsManager.getCurrentVersion()    → Version?
 * - VersionsManager.isVersionExists(name)  → Boolean
 * - AccountsManager.allAccounts            → List<MinecraftAccount>
 * - AccountsManager.currentAccount         → MinecraftAccount? (var)
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

    data class ReadinessResult(val isReady: Boolean, val issues: List<String>)

    /** Verifica pré-requisitos antes de lançar. */
    fun checkReadiness(instance: NexusInstanceManager.NexusInstance): ReadinessResult {
        val issues = mutableListOf<String>()
        try {
            if (AccountsManager.allAccounts.isEmpty()) {
                issues += "Sem conta. Vá em PERSONA e adicione uma conta offline."
            }
        } catch (_: Exception) {
            issues += "Erro ao verificar conta."
        }
        if (!instance.isReady) {
            issues += "Versão ${instance.mcVersion} não instalada. Instale em INSTARRION."
        }
        return ReadinessResult(issues.isEmpty(), issues)
    }

    /** Lança o Minecraft para a instância especificada. */
    suspend fun launch(
        context : Context,
        instance: NexusInstanceManager.NexusInstance
    ): Boolean = withContext(Dispatchers.Main) {
        try {
            _status.value = LaunchStatus(LaunchState.CHECKING, "Verificando pré-requisitos...")

            val readiness = withContext(Dispatchers.IO) { checkReadiness(instance) }
            if (!readiness.isReady) {
                _status.value = LaunchStatus(LaunchState.ERROR, errorMsg = readiness.issues.joinToString("\n"))
                return@withContext false
            }

            _status.value = LaunchStatus(LaunchState.LAUNCHING, "Iniciando ${instance.mcVersion}...")

            // Refresh lista de versões no fundo
            withContext(Dispatchers.IO) {
                VersionsManager.refresh("NexusLaunchManager")
            }

            // Busca versão pelo nome na lista de versões carregadas
            val version: Version? = withContext(Dispatchers.IO) {
                VersionsManager.getVersions().firstOrNull { v ->
                    v.getVersionName() == instance.mcVersion && v.isValid()
                }
            }

            if (version == null) {
                _status.value = LaunchStatus(
                    LaunchState.ERROR,
                    errorMsg = "Versão '${instance.mcVersion}' não encontrada. Instale-a primeiro."
                )
                return@withContext false
            }

            // Marca como última usada
            withContext(Dispatchers.IO) { NexusInstanceManager.setLastUsed(instance.id) }

            // Lança via API real do ZalithLauncher
            LaunchGame.preLaunch(context, version)

            _status.value = LaunchStatus(LaunchState.RUNNING, "Minecraft rodando!")
            true

        } catch (e: Exception) {
            _status.value = LaunchStatus(LaunchState.ERROR, errorMsg = "Falha: ${e.message}")
            false
        }
    }

    fun reset() { _status.value = LaunchStatus() }
}