package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.customprofilepath.ProfilePathManager
import com.movtery.zalithlauncher.feature.unpack.Jre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * NexusSetupChecker — Verifica estado do launcher na primeira abertura.
 *
 * Retorna lista de itens pendentes que o usuário precisa configurar
 * para poder jogar:
 * 1. Diretório base configurado
 * 2. Java runtime disponível
 * 3. Pelo menos uma conta configurada
 * 4. Pelo menos uma instância criada
 */
object NexusSetupChecker {

    data class SetupItem(
        val id         : String,
        val title      : String,
        val description: String,
        val isDone     : Boolean,
        val action     : String  // "directory", "runtime", "account", "instance"
    )

    data class SetupStatus(
        val items          : List<SetupItem>,
        val isCompletelyReady: Boolean,
        val pendingCount   : Int
    )

    suspend fun check(context: Context): SetupStatus = withContext(Dispatchers.IO) {
        val items = mutableListOf<SetupItem>()

        // 1. Verificar diretório base
        val hasDir = try {
            val profile = ProfilePathManager.getCurrentProfile()
            profile != null && File(profile.profilePath).exists()
        } catch (e: Exception) { false }

        items += SetupItem(
            id          = "directory",
            title       = "Diretório Base",
            description = if (hasDir) "Diretório configurado" else "Configure onde o launcher armazenará os dados",
            isDone      = hasDir,
            action      = "directory"
        )

        // 2. Verificar Java runtime
        val hasRuntime = try {
            Jre.checkRuntimes()
            true
        } catch (e: Exception) { false }

        items += SetupItem(
            id          = "runtime",
            title       = "Java Runtime",
            description = if (hasRuntime) "Runtime disponível" else "Runtime será baixado automaticamente na primeira instalação",
            isDone      = hasRuntime,
            action      = "runtime"
        )

        // 3. Verificar conta
        val hasAccount = try {
            NexusAccountManager.profiles.value.isNotEmpty() ||
            com.movtery.zalithlauncher.feature.accounts.AccountsManager.getAllAccounts().isNotEmpty()
        } catch (e: Exception) { false }

        items += SetupItem(
            id          = "account",
            title       = "Conta de Jogo",
            description = if (hasAccount) "Conta configurada" else "Adicione uma conta offline ou Microsoft em PERSONA",
            isDone      = hasAccount,
            action      = "account"
        )

        // 4. Verificar instância
        val hasInstance = NexusInstanceManager.instances.value.isNotEmpty()
        items += SetupItem(
            id          = "instance",
            title       = "Instância de Jogo",
            description = if (hasInstance) "${NexusInstanceManager.instances.value.size} instância(s) criada(s)" else "Crie uma instância em INSTARRION para começar",
            isDone      = hasInstance,
            action      = "instance"
        )

        val pending = items.count { !it.isDone }
        SetupStatus(
            items               = items,
            isCompletelyReady   = pending == 0,
            pendingCount        = pending
        )
    }

    /** Retorna texto do status para a HomeScreen. */
    fun statusSummary(status: SetupStatus): String {
        if (status.isCompletelyReady) return "✓ Tudo pronto para jogar!"
        val pending = status.items.filter { !it.isDone }
        return "Pendente: ${pending.joinToString(", ") { it.title }}"
    }
}