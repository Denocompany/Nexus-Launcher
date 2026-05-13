package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.accounts.AccountsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * NexusSetupChecker — Verifica pré-requisitos para jogar.
 */
object NexusSetupChecker {

    data class SetupItem(
        val id         : String,
        val title      : String,
        val description: String,
        val isDone     : Boolean,
        val action     : String
    )

    data class SetupStatus(
        val items              : List<SetupItem>,
        val isCompletelyReady  : Boolean,
        val pendingCount       : Int
    )

    suspend fun check(context: Context): SetupStatus = withContext(Dispatchers.IO) {
        val items = mutableListOf<SetupItem>()

        // 1. Diretório base
        val baseDir = File(context.getExternalFilesDir(null), "NexusLauncher")
        val hasDir  = baseDir.exists() || baseDir.mkdirs()
        items += SetupItem(
            id          = "directory",
            title       = "Diretório Base",
            description = if (hasDir) "✓ ${baseDir.absolutePath}" else "Configurar diretório em HELIOS CONTROL → Jogo",
            isDone      = hasDir,
            action      = "directory"
        )

        // 2. Java runtime
        val runtimeDir = File(context.filesDir, "components")
        val hasRuntime = runtimeDir.exists() && runtimeDir.listFiles()?.isNotEmpty() == true
        items += SetupItem(
            id          = "runtime",
            title       = "Java Runtime",
            description = if (hasRuntime) "✓ Runtime disponível" else "Será baixado automaticamente na primeira instalação",
            isDone      = hasRuntime,
            action      = "runtime"
        )

        // 3. Conta ativa
        val hasAccount = runCatching {
            AccountsManager.reload()
            AccountsManager.allAccounts.isNotEmpty()
        }.getOrDefault(false)
        items += SetupItem(
            id          = "account",
            title       = "Conta de Jogo",
            description = if (hasAccount) "✓ ${AccountsManager.allAccounts.size} conta(s)" else "Adicione em PERSONA → + Conta Offline",
            isDone      = hasAccount,
            action      = "account"
        )

        // 4. Instância criada
        val hasInstance = NexusInstanceManager.instances.value.isNotEmpty()
        val instCount   = NexusInstanceManager.instances.value.size
        items += SetupItem(
            id          = "instance",
            title       = "Instância de Jogo",
            description = if (hasInstance) "✓ $instCount instância(s)" else "Crie em INSTARRION → + Nova",
            isDone      = hasInstance,
            action      = "instance"
        )

        val pending = items.count { !it.isDone }
        SetupStatus(items, pending == 0, pending)
    }

    fun statusSummary(status: SetupStatus): String =
        if (status.isCompletelyReady) "✓ Tudo pronto para jogar!"
        else "Pendente: ${status.items.filter { !it.isDone }.joinToString(", ") { it.title }}"
}