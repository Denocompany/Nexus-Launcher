package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import com.movtery.zalithlauncher.feature.accounts.AccountUtils
import com.movtery.zalithlauncher.feature.accounts.LocalAccountUtils
import com.movtery.zalithlauncher.value.MinecraftAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * NexusAccountManager — Ponte entre UI Nexus e AccountsManager do ZalithLauncher.
 *
 * Fornece:
 * - Criação de contas offline com username
 * - Listagem de contas existentes
 * - Definição de conta ativa
 * - Estrutura para OAuth Microsoft (stub com interface pronta)
 */
object NexusAccountManager {

    data class NexusProfile(
        val id        : String,
        val username  : String,
        val type      : ProfileType,
        val isActive  : Boolean = false,
        val skinUrl   : String  = "",
        val capeUrl   : String  = ""
    )

    enum class ProfileType { MICROSOFT, OFFLINE, OTHER }

    private val _profiles = MutableStateFlow<List<NexusProfile>>(emptyList())
    val profiles: StateFlow<List<NexusProfile>> = _profiles

    private val _activeProfile = MutableStateFlow<NexusProfile?>(null)
    val activeProfile: StateFlow<NexusProfile?> = _activeProfile

    /** Carrega todas as contas do AccountsManager. */
    fun loadAccounts() {
        runCatching {
            val accounts  = AccountsManager.getAllAccounts()
            val active    = AccountsManager.getCurrentAccount()
            val profiles  = accounts.map { acc -> acc.toNexusProfile(active?.clientToken) }
            _profiles.value      = profiles
            _activeProfile.value = profiles.firstOrNull { it.isActive }
        }.onFailure {
            _profiles.value = emptyList()
        }
    }

    private fun MinecraftAccount.toNexusProfile(activeToken: String?): NexusProfile {
        val type = when {
            isMicrosoft == true -> ProfileType.MICROSOFT
            else                -> ProfileType.OFFLINE
        }
        return NexusProfile(
            id       = clientToken ?: username ?: "offline",
            username = username  ?: "Jogador",
            type     = type,
            isActive = clientToken == activeToken,
            skinUrl  = ""
        )
    }

    /** Cria conta offline. */
    suspend fun createOfflineAccount(username: String): Boolean = withContext(Dispatchers.IO) {
        if (username.isBlank() || username.length < 3) return@withContext false
        runCatching {
            LocalAccountUtils.createLocalAccount(username)
            loadAccounts()
            // Define como ativa se for a primeira
            if (_profiles.value.size == 1) {
                setActiveAccount(_profiles.value.first().id)
            }
            true
        }.getOrDefault(false)
    }

    /** Define conta ativa. */
    suspend fun setActiveAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val account = AccountsManager.getAllAccounts().firstOrNull {
                it.clientToken == id || it.username == id
            } ?: return@withContext false
            AccountsManager.setCurrentAccount(account)
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    /** Remove conta. */
    suspend fun removeAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val account = AccountsManager.getAllAccounts().firstOrNull {
                it.clientToken == id || it.username == id
            } ?: return@withContext false
            AccountsManager.removeAccount(account)
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    /** Retorna nome do jogador ativo (para argumentos de launch). */
    fun getActiveUsername(): String =
        _activeProfile.value?.username ?: "Jogador"

    /** Stub para OAuth Microsoft — estrutura pronta para implementação futura. */
    interface MicrosoftLoginCallback {
        fun onSuccess(profile: NexusProfile)
        fun onError(message: String)
        fun onCancelled()
    }

    /**
     * Inicia fluxo de login Microsoft.
     * Por ora chama o fluxo existente do ZalithLauncher.
     */
    fun startMicrosoftLogin(context: Context, callback: MicrosoftLoginCallback) {
        // O ZalithLauncher tem MicrosoftLoginFragment e MicrosoftBackgroundLogin
        // Este stub expõe a interface para ser chamado da UI Nexus
        callback.onError("Login Microsoft: use a tela de Contas do ZalithLauncher por enquanto.")
    }
}