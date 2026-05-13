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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.core.NexusAccountManager
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private val SKIN_PRESETS = listOf(
    "Steve (Padrão)", "Alex (Padrão)", "Slim Steve", "Enderman", "Creeper", "Personalizada"
)

/**
 * AccountsScreen — PERSONA.
 * Conectado ao NexusAccountManager que ponte com o AccountsManager do ZalithLauncher.
 * Suporta contas Offline (criar/remover) e stub Microsoft.
 */
@Composable
fun AccountsScreen(
    nexusDataStore: NexusDataStore? = null,
    onBackToHome  : () -> Unit = {},
    onGoInstances : () -> Unit = {},
    onBackToSolar : () -> Unit = {}
) {
    val context  = LocalContext.current
    val scope    = rememberCoroutineScope()
    val profiles by NexusAccountManager.profiles.collectAsState()
    val active   by NexusAccountManager.activeProfile.collectAsState()

    var selectedTab    by remember { mutableStateOf(0) }
    var newUsername    by remember { mutableStateOf("") }
    var statusMsg      by remember { mutableStateOf("") }
    var statusIsError  by remember { mutableStateOf(false) }
    var selectedSkin   by remember { mutableStateOf(0) }
    val tabs = listOf("Contas", "Skin & Visual")

    LaunchedEffect(Unit) { NexusAccountManager.loadAccounts() }

    Column(Modifier.fillMaxSize().background(DeepVoid).verticalScroll(rememberScrollState()).padding(16.dp)) {

        // Header
        Text("PERSONA", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("CONTAS · Microsoft · Offline · Skins", color = TextSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(16.dp))

        // Active account banner
        if (active != null) {
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(NexusCyan.copy(0.1f)).border(1.dp, NexusCyan.copy(0.3f), RoundedCornerShape(10.dp))
                .padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(NexusCyan.copy(0.2f)), contentAlignment = Alignment.Center) {
                    Text(if (active!!.type == NexusAccountManager.ProfileType.MICROSOFT) "🪟" else "👤", fontSize = 20.sp)
                }
                Column(Modifier.weight(1f)) {
                    Text(active!!.username, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(if (active!!.type == NexusAccountManager.ProfileType.MICROSOFT) "Microsoft" else "Offline", color = NexusCyan, fontSize = 11.sp)
                }
                Text("✓ Ativo", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
        } else {
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(NexusOrange.copy(0.1f)).border(1.dp, NexusOrange.copy(0.3f), RoundedCornerShape(10.dp))
                .padding(12.dp)) {
                Text("⚠ Nenhuma conta ativa — adicione uma conta para jogar", color = NexusOrange, fontSize = 12.sp)
            }
            Spacer(Modifier.height(12.dp))
        }

        // Status message
        if (statusMsg.isNotEmpty()) {
            Text(statusMsg, color = if (statusIsError) Color(0xFFCF4455) else NexusCyan,
                fontSize = 11.sp, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                    .background((if (statusIsError) Color(0xFFCF4455) else NexusCyan).copy(0.08f)).padding(8.dp))
            Spacer(Modifier.height(8.dp))
        }

        // Tabs
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(Modifier.clip(RoundedCornerShape(20.dp))
                    .background(if (selectedTab == i) NexusCyan.copy(0.15f) else Color(0xFF111120))
                    .border(1.dp, if (selectedTab == i) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
                    .clickable { selectedTab = i }.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Text(tab, color = if (selectedTab == i) NexusCyan else TextSecondary, fontSize = 11.sp,
                        fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
        Spacer(Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // === Accounts tab ===

                // Add offline account
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
                    Text("➕ Adicionar Conta Offline", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newUsername, onValueChange = { newUsername = it },
                            label = { Text("Nome de jogador (3-16 chars)", color = TextSecondary, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = NexusCyan, unfocusedBorderColor = Color(0xFF333340),
                                textColor = Color.White, cursorColor = NexusCyan
                            ), singleLine = true
                        )
                        Button(
                            onClick = {
                                val name = newUsername.trim()
                                if (name.length < 3 || name.length > 16) {
                                    statusMsg = "Nome deve ter 3-16 caracteres"; statusIsError = true; return@Button
                                }
                                scope.launch {
                                    val ok = NexusAccountManager.createOfflineAccount(name)
                                    if (ok) {
                                        newUsername = ""; statusIsError = false
                                        statusMsg = "Conta offline '$name' criada!"
                                    } else {
                                        statusMsg = "Erro ao criar conta"; statusIsError = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan),
                            shape = RoundedCornerShape(8.dp), enabled = newUsername.length >= 3
                        ) { Text("Criar", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Microsoft login (stub)
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).border(1.dp, Color(0xFF0078D4).copy(0.4f), RoundedCornerShape(12.dp)).padding(14.dp)) {
                    Text("🪟 Login Microsoft", color = Color(0xFF0078D4), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Faça login com sua conta da Microsoft para acesso completo ao Minecraft.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            NexusAccountManager.startMicrosoftLogin(context, object : NexusAccountManager.MicrosoftLoginCallback {
                                override fun onSuccess(profile: NexusAccountManager.NexusProfile) { statusMsg = "✓ Login Microsoft: ${profile.username}"; statusIsError = false }
                                override fun onError(message: String) { statusMsg = message; statusIsError = true }
                                override fun onCancelled() { statusMsg = "Login cancelado" }
                            })
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0078D4)),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()
                    ) { Text("Entrar com Microsoft", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                }

                Spacer(Modifier.height(10.dp))

                // Account list
                if (profiles.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhuma conta cadastrada", color = TextSecondary, fontSize = 13.sp)
                    }
                } else {
                    Text("CONTAS CADASTRADAS", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(8.dp))
                    profiles.forEach { profile ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (profile.isActive) NexusCyan.copy(0.1f) else Color(0xFF0E0E16))
                            .border(1.dp, if (profile.isActive) NexusCyan.copy(0.4f) else Color(0xFF1A1A26), RoundedCornerShape(10.dp))
                            .padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                                .background((if (profile.type == NexusAccountManager.ProfileType.MICROSOFT) Color(0xFF0078D4) else NexusCyan).copy(0.15f)),
                                contentAlignment = Alignment.Center) {
                                Text(if (profile.type == NexusAccountManager.ProfileType.MICROSOFT) "🪟" else "👤", fontSize = 18.sp)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(profile.username, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(if (profile.type == NexusAccountManager.ProfileType.MICROSOFT) "Microsoft" else "Offline", color = TextSecondary, fontSize = 10.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (!profile.isActive) {
                                    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(NexusCyan.copy(0.15f))
                                        .border(1.dp, NexusCyan.copy(0.4f), RoundedCornerShape(6.dp))
                                        .clickable { scope.launch { NexusAccountManager.setActiveAccount(profile.id) } }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        Text("Selecionar", color = NexusCyan, fontSize = 10.sp)
                                    }
                                } else {
                                    Text("✓ Ativo", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFCF4455).copy(0.15f))
                                    .border(1.dp, Color(0xFFCF4455).copy(0.4f), RoundedCornerShape(6.dp))
                                    .clickable { scope.launch { NexusAccountManager.removeAccount(profile.id) } }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("🗑", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // === Skin & Visual tab ===
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(14.dp)) {
                    Text("🎨 Skin do Jogador", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    SKIN_PRESETS.chunked(3).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEachIndexed { i, skin ->
                                val globalIdx = SKIN_PRESETS.indexOf(skin)
                                val sel = selectedSkin == globalIdx
                                Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) NexusCyan.copy(0.2f) else Color(0xFF111120))
                                    .border(1.dp, if (sel) NexusCyan else Color(0xFF222230), RoundedCornerShape(8.dp))
                                    .clickable { selectedSkin = globalIdx }.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center) {
                                    Text(skin, color = if (sel) NexusCyan else TextSecondary, fontSize = 10.sp,
                                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onBackToSolar, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan.copy(0.15f)),
            shape = RoundedCornerShape(8.dp)) {
            Text("← Voltar ao Sistema Solar", color = NexusCyan, fontSize = 12.sp)
        }
    }
}