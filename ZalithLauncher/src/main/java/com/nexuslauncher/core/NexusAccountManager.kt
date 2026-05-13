package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import com.movtery.zalithlauncher.setting.AllSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import net.kdt.pojavlaunch.value.MinecraftAccount

/**
 * NexusAccountManager — Ponte UI Nexus ↔ AccountsManager do ZalithLauncher.
 *
 * API real: AccountsManager.allAccounts, AccountsManager.currentAccount,
 *           MinecraftAccount.uniqueUUID, MinecraftAccount.username
 */
object NexusAccountManager {

    data class NexusProfile(
        val id       : String,
        val username : String,
        val type     : ProfileType,
        val isActive : Boolean = false
    )

    enum class ProfileType { MICROSOFT, OFFLINE, OTHER }

    private val _profiles      = MutableStateFlow<List<NexusProfile>>(emptyList())
    val profiles: StateFlow<List<NexusProfile>> = _profiles

    private val _activeProfile = MutableStateFlow<NexusProfile?>(null)
    val activeProfile: StateFlow<NexusProfile?> = _activeProfile

    /** Carrega todas as contas do AccountsManager. */
    fun loadAccounts() {
        runCatching {
            AccountsManager.reload()
            val all      = AccountsManager.allAccounts
            val current  = AccountsManager.currentAccount
            val list     = all.map { acc ->
                val type = when {
                    acc.accountType?.contains("microsoft", ignoreCase = true) == true ||
                    acc.msaRefreshToken?.isNotBlank() == true &&
                    acc.msaRefreshToken != "0" -> ProfileType.MICROSOFT
                    else -> ProfileType.OFFLINE
                }
                NexusProfile(
                    id       = acc.uniqueUUID ?: acc.username ?: "offline",
                    username = acc.username   ?: "Jogador",
                    type     = type,
                    isActive = acc.uniqueUUID == current?.uniqueUUID
                )
            }
            _profiles.value      = list
            _activeProfile.value = list.firstOrNull { it.isActive }
        }.onFailure {
            _profiles.value = emptyList()
        }
    }

    /** Cria conta offline diretamente via MinecraftAccount. */
    suspend fun createOfflineAccount(username: String): Boolean = withContext(Dispatchers.IO) {
        if (username.isBlank() || username.length < 3 || username.length > 16) return@withContext false
        runCatching {
            val uuid = java.util.UUID.nameUUIDFromBytes(
                "OfflinePlayer:$username".toByteArray()
            ).toString().lowercase()

            val account = MinecraftAccount().also { a ->
                a.username    = username
                a.accessToken = "0"
                a.clientToken = "0"
                a.profileId   = uuid
                a.accountType = "Local"
            }
            // Salvar no diretório de contas do ZalithLauncher
            account.save()
            AccountsManager.reload()

            // Se primeira conta, define como ativa
            if (AccountsManager.allAccounts.size == 1) {
                AccountsManager.currentAccount = account
            }
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    /** Define conta ativa via AccountsManager.currentAccount. */
    suspend fun setActiveAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val account = AccountsManager.allAccounts.firstOrNull {
                it.uniqueUUID == id || it.username == id
            } ?: return@withContext false
            AccountsManager.currentAccount = account
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    /** Remove conta deletando o arquivo JSON no diretório de contas. */
    suspend fun removeAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val account = AccountsManager.allAccounts.firstOrNull {
                it.uniqueUUID == id || it.username == id
            } ?: return@withContext false
            account.deleteAccount()
            AccountsManager.reload()
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    fun getActiveUsername(): String =
        _activeProfile.value?.username ?: AccountsManager.currentAccount?.username ?: "Jogador"

    interface MicrosoftLoginCallback {
        fun onSuccess(profile: NexusProfile)
        fun onError(message: String)
        fun onCancelled()
    }

    fun startMicrosoftLogin(context: Context, callback: MicrosoftLoginCallback) {
        callback.onError(
            "Para login Microsoft, vá em:\nConta → Adicionar Conta → Login Microsoft\n(usa o fluxo padrão do ZalithLauncher)"
        )
    }
}