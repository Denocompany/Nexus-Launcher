package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

data class NexusAccount(
    val id       : String,
    val username : String,
    val type     : AccountType,
    val isActive : Boolean = false
)

enum class AccountType { MICROSOFT, OFFLINE }

private val SKIN_PRESETS = listOf(
    "Steve (Padrão)", "Alex (Padrão)", "Slim Steve", "Enderman", "Creeper", "Personalizada"
)

@Composable
fun AccountsScreen(onNavigateTo: (String) -> Unit = {}) {

    val accounts = remember {
        mutableStateListOf<NexusAccount>()
    }

    var selectedTab      by remember { mutableStateOf(0) }
    var activeAccountId  by remember { mutableStateOf<String?>(null) }
    var offlineName      by remember { mutableStateOf("") }
    var offlineNameError by remember { mutableStateOf(false) }
    var selectedSkin     by remember { mutableStateOf(0) }

    val tabs = listOf("Microsoft", "Offline", "Skins")

    val activeAccount = accounts.firstOrNull { it.id == activeAccountId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("CONTAS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("${accounts.size} conta(s) · Microsoft · Offline · Skins", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(16.dp))

        // ── Conta ativa ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NexusCyan.copy(0.06f))
                .border(1.dp, NexusCyan.copy(0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text("CONTA ATIVA", color = NexusCyan, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier         = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(NexusCyan.copy(0.15f)).border(2.dp, NexusCyan.copy(0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (activeAccount != null) "🎮" else "👤", fontSize = 24.sp)
                }
                if (activeAccount != null) {
                    Column {
                        Text(activeAccount.username, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (activeAccount.type == AccountType.MICROSOFT) "Conta Microsoft" else "Conta Offline",
                            color    = if (activeAccount.type == AccountType.MICROSOFT) Color(0xFF4DA6FF) else Color(0xFF00E676),
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Column {
                        Text("Nenhuma conta ativa", color = TextSecondary, fontSize = 14.sp)
                        Text("Adicione uma conta nas abas abaixo", color = TextSecondary.copy(0.5f), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Abas ──────────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == i) NexusCyan.copy(0.15f) else Color(0xFF111120))
                        .border(1.dp, if (selectedTab == i) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { selectedTab = i }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        tab,
                        color      = if (selectedTab == i) NexusCyan else TextSecondary,
                        fontSize   = 11.sp,
                        fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        when (selectedTab) {

            // ── Aba 0: Microsoft ─────────────────────────────────────────
            0 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(16.dp)
                ) {
                    Text("CONTA MICROSOFT", color = Color(0xFF4DA6FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "A autenticação Microsoft requer conexão com os servidores da Mojang. O fluxo OAuth2 será implementado na próxima atualização do Nexus.",
                        color    = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(16.dp))

                    val msAccounts = accounts.filter { it.type == AccountType.MICROSOFT }
                    if (msAccounts.isNotEmpty()) {
                        msAccounts.forEach { acc ->
                            AccountRow(
                                account         = acc,
                                isActive        = acc.id == activeAccountId,
                                onSetActive     = { activeAccountId = acc.id },
                                onRemove        = { accounts.remove(acc); if (activeAccountId == acc.id) activeAccountId = null }
                            )
                            Divider(color = Color(0xFF1A1A28))
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Button(
                        onClick  = { /* OAuth2 Microsoft — Fase 5 */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0078D4))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔷", fontSize = 16.sp)
                            Text("Adicionar Conta Microsoft", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Em breve: login automático com browser integrado.",
                        color    = TextSecondary.copy(0.5f),
                        fontSize = 10.sp
                    )
                }
            }

            // ── Aba 1: Offline (FUNCIONAL) ────────────────────────────────
            1 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(16.dp)
                ) {
                    Text("CONTA OFFLINE", color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Jogue sem conexão com a internet. Sem verificação de licença.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value         = offlineName,
                        onValueChange = {
                            offlineName      = it.take(16)
                            offlineNameError = false
                        },
                        modifier      = Modifier.fillMaxWidth(),
                        label         = { Text("Nome do jogador (máx. 16 caracteres)", color = TextSecondary, fontSize = 11.sp) },
                        isError       = offlineNameError,
                        singleLine    = true,
                        colors        = TextFieldDefaults.outlinedTextFieldColors(
                            textColor            = Color.White,
                            focusedBorderColor   = Color(0xFF00E676),
                            unfocusedBorderColor = TextSecondary.copy(0.4f),
                            errorBorderColor     = NexusOrange,
                            cursorColor          = Color(0xFF00E676)
                        )
                    )
                    if (offlineNameError) {
                        Text("Nome inválido ou já existe", color = NexusOrange, fontSize = 10.sp)
                    }

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val trimmed = offlineName.trim()
                            if (trimmed.length < 3) {
                                offlineNameError = true
                            } else if (accounts.any { it.username.equals(trimmed, ignoreCase = true) }) {
                                offlineNameError = true
                            } else {
                                val newAcc = NexusAccount(
                                    id       = System.currentTimeMillis().toString(),
                                    username = trimmed,
                                    type     = AccountType.OFFLINE,
                                    isActive = accounts.isEmpty()
                                )
                                accounts.add(newAcc)
                                if (accounts.size == 1) activeAccountId = newAcc.id
                                offlineName      = ""
                                offlineNameError = false
                            }
                        },
                        enabled  = offlineName.trim().length >= 3,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(
                            backgroundColor         = Color(0xFF00E676),
                            disabledBackgroundColor = Color(0xFF00E676).copy(0.3f)
                        )
                    ) {
                        Text("+ ADICIONAR CONTA OFFLINE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    val offlineAccounts = accounts.filter { it.type == AccountType.OFFLINE }
                    if (offlineAccounts.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Divider(color = Color(0xFF1A1A28))
                        Spacer(Modifier.height(8.dp))
                        Text("${offlineAccounts.size} conta(s) offline", color = TextSecondary, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        offlineAccounts.forEach { acc ->
                            AccountRow(
                                account     = acc,
                                isActive    = acc.id == activeAccountId,
                                onSetActive = { activeAccountId = acc.id },
                                onRemove    = {
                                    accounts.remove(acc)
                                    if (activeAccountId == acc.id) {
                                        activeAccountId = accounts.firstOrNull()?.id
                                    }
                                }
                            )
                            Divider(color = Color(0xFF1A1A28))
                        }
                    }
                }
            }

            // ── Aba 2: Skins ─────────────────────────────────────────────
            2 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(16.dp)
                ) {
                    Text("SKINS", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Escolha uma skin para a sua conta ativa.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(14.dp))

                    if (activeAccount == null) {
                        Text("Configure uma conta ativa primeiro.", color = NexusOrange, fontSize = 12.sp)
                    } else {
                        Text("Skin ativa: ${SKIN_PRESETS[selectedSkin]}", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        SKIN_PRESETS.forEachIndexed { i, skin ->
                            val isSel = selectedSkin == i
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) NexusCyan.copy(0.1f) else Color.Transparent)
                                    .border(1.dp, if (isSel) NexusCyan else Color(0xFF222230), RoundedCornerShape(8.dp))
                                    .clickable { selectedSkin = i }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier         = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(NexusCyan.copy(0.08f)).border(1.dp, NexusCyan.copy(0.2f), RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(if (i == 5) "📤" else "🧍", fontSize = 16.sp)
                                    }
                                    Text(skin, color = if (isSel) NexusCyan else Color.White, fontSize = 13.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                }
                                if (isSel) Text("✓", color = NexusCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(6.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick  = { /* Upload skin — Fase 5 */ },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                        ) {
                            Text("📤 Fazer upload de skin personalizada", color = NexusCyan, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountRow(
    account    : NexusAccount,
    isActive   : Boolean,
    onSetActive: () -> Unit,
    onRemove   : () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(
                    if (account.type == AccountType.MICROSOFT) Color(0xFF0078D4).copy(0.15f) else Color(0xFF00E676).copy(0.15f)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (account.type == AccountType.MICROSOFT) "🔷" else "👤", fontSize = 18.sp)
            }
            Column {
                Text(account.username, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        if (account.type == AccountType.MICROSOFT) "Microsoft" else "Offline",
                        color    = if (account.type == AccountType.MICROSOFT) Color(0xFF4DA6FF) else Color(0xFF00E676),
                        fontSize = 10.sp
                    )
                    if (isActive) {
                        Box(Modifier.clip(RoundedCornerShape(3.dp)).background(Color(0xFF00E676).copy(0.2f)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                            Text("ATIVA", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (!isActive) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NexusCyan.copy(0.1f))
                        .border(1.dp, NexusCyan.copy(0.4f), RoundedCornerShape(6.dp))
                        .clickable(onClick = onSetActive)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Usar", color = NexusCyan, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF5252).copy(0.1f))
                    .border(1.dp, Color(0xFFFF5252).copy(0.4f), RoundedCornerShape(6.dp))
                    .clickable(onClick = onRemove)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Remover", color = Color(0xFFFF5252), fontSize = 10.sp)
            }
        }
    }
}
