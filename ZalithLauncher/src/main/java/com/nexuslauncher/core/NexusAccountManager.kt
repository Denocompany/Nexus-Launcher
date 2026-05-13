package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import com.movtery.zalithlauncher.utils.path.PathManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import net.kdt.pojavlaunch.value.MinecraftAccount
import java.io.File

/**
 * NexusAccountManager — Ponte UI Nexus ↔ AccountsManager do ZalithLauncher.
 *
 * APIs reais confirmadas:
 * - AccountsManager.allAccounts         (List<MinecraftAccount>)
 * - AccountsManager.currentAccount      (var — get/set)
 * - AccountsManager.reload()
 * - MinecraftAccount.save()             (salva JSON em DIR_ACCOUNT_NEW/<uniqueUUID>)
 * - MinecraftAccount.getUniqueUUID()    (string)
 * - AllSettings.currentAccount          (StringSettingUnit — armazena UUID ativo)
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

    /** Carrega todas as contas via AccountsManager. */
    fun loadAccounts() {
        runCatching {
            AccountsManager.reload()
            val all     = AccountsManager.allAccounts
            val current = AccountsManager.currentAccount
            val list    = all.map { acc ->
                val isMicrosoft = acc.msaRefreshToken?.isNotBlank() == true && acc.msaRefreshToken != "0"
                NexusProfile(
                    id       = acc.getUniqueUUID(),
                    username = acc.username ?: "Jogador",
                    type     = if (isMicrosoft) ProfileType.MICROSOFT else ProfileType.OFFLINE,
                    isActive = acc.getUniqueUUID() == current?.getUniqueUUID()
                )
            }
            _profiles.value      = list
            _activeProfile.value = list.firstOrNull { it.isActive }
        }.onFailure { _profiles.value = emptyList() }
    }

    /** Cria conta offline via MinecraftAccount.save(). */
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
            account.save()
            AccountsManager.reload()

            if (AccountsManager.allAccounts.size == 1) {
                AccountsManager.currentAccount = account
            }
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    /** Define conta ativa. */
    suspend fun setActiveAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val account = AccountsManager.allAccounts.firstOrNull {
                it.getUniqueUUID() == id || it.username == id
            } ?: return@withContext false
            AccountsManager.currentAccount = account
            loadAccounts()
            true
        }.getOrDefault(false)
    }

    /** Remove conta deletando arquivo JSON do diretório de contas. */
    suspend fun removeAccount(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val accountFile = File(PathManager.DIR_ACCOUNT_NEW, id)
            if (accountFile.exists()) accountFile.delete()
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
        callback.onError("Para login Microsoft, use o Gerenciador de Contas do ZalithLauncher.")
    }
}