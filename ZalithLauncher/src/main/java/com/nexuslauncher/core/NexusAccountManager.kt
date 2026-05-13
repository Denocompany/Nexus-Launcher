package com.nexuslauncher.core

import android.app.Activity
import android.content.Context
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import net.kdt.pojavlaunch.value.MinecraftAccount
import org.json.JSONObject
import java.io.File

/**
 * NexusAccountManager — Ponte UI Nexus ↔ AccountsManager do ZalithLauncher.
 *
 * Suporta:
 * - Contas offline (username direto)
 * - Microsoft (bridge para fluxo existente do ZalithLauncher)
 * - Listagem e seleção de conta ativa
 */
object NexusAccountManager {

    data class NexusProfile(
        val id        : String,
        val username  : String,
        val type      : ProfileType,
        val isActive  : Boolean = false,
        val skinUrl   : String  = ""
    )

    enum class ProfileType { MICROSOFT, OFFLINE, OTHER }

    private val _profiles      = MutableStateFlow<List<NexusProfile>>(emptyList())
    val profiles: StateFlow<List<NexusProfile>> = _profiles

    private val _activeProfile = MutableStateFlow<NexusProfile?>(null)
    val activeProfile: StateFlow<NexusProfile?> = _activeProfile

    /** Carrega todas as contas do AccountsManager do ZalithLauncher. */
    fun loadAccounts() {
        runCatching {
            val allAccounts = AccountsManager.getAccounts()
            val current     = AccountsManager.currentAccount
            val list        = allAccounts.map { acc ->
                NexusProfile(
                    id       = acc.uniqueUUID ?: acc.username ?: "offline_${acc.username}",
                    username = acc.username   ?: "Jogador",
                    type     = when {
                        AccountsManager.isMicrosoftAccount(acc) -> ProfileType.MICROSOFT
                        else                                     -> ProfileType.OFFLINE
                    },
                    isActive = acc.uniqueUUID == current?.uniqueUUID
                )
            }
            _profiles.value      = list
            _activeProfile.value = list.firstOrNull { it.isActive }
        }.onFailure {
            _profiles.value = emptyList()
        }
    }

    /**
     * Cria conta offline (local) via MinecraftAccount diretamente.
     * ZalithLauncher salva contas em JSON no PathManager.DIR_GAME_HOME.
     */
    suspend fun createOfflineAccount(username: String): Boolean = withContext(Dispatchers.IO) {
        if (username.isBlank() || username.length < 3 || username.length > 16) return@withContext false
        runCatching {
            val account = MinecraftAccount().also { acc ->
                acc.username    = username
                acc.accessToken = "0"
                acc.clientToken = "0"
                acc.uniqueUUID  = java.util.UUID.nameUUIDFromBytes(
                    "OfflinePlayer:$username".toByteArray()
                ).toString().replace("-", "")
            }
            AccountsManager.addAccount(account)
            loadAccounts()
            // Set as current if first account
            if (_profiles.value.size == 1) {
                setActiveAccount(_profiles.value.first().id)
            }
            true
        }.getOrDefault(false)
    }

    /** Define conta ativa. */
    suspend fun setActiveAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val account = AccountsManager.getAccounts().firstOrNull {
                it.uniqueUUID == id || it.username == id
            } ?: return@withContext false
            AccountsManager.currentAccount = account
            AccountsManager.saveAccount(account)
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    /** Remove conta. */
    suspend fun removeAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val account = AccountsManager.getAccounts().firstOrNull {
                it.uniqueUUID == id || it.username == id
            } ?: return@withContext false
            AccountsManager.deleteAccount(account)
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    fun getActiveUsername(): String =
        _activeProfile.value?.username ?: AccountsManager.currentAccount?.username ?: "Jogador"

    /** Stub para Microsoft OAuth — usa o fluxo do ZalithLauncher. */
    interface MicrosoftLoginCallback {
        fun onSuccess(profile: NexusProfile)
        fun onError(message: String)
        fun onCancelled()
    }

    fun startMicrosoftLogin(context: Context, callback: MicrosoftLoginCallback) {
        // Delega para o fluxo Microsoft existente do ZalithLauncher
        // (MicrosoftBackgroundLogin / AccountsManager.login)
        callback.onError("Abra o gerenciador de contas do ZalithLauncher para login Microsoft.")
    }
}