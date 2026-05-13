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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
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
import com.nexuslauncher.core.NexusDownloadManager
import com.nexuslauncher.core.NexusInstanceManager
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private val MC_VERSIONS = listOf(
    "1.21.4", "1.21.1", "1.20.4", "1.20.1",
    "1.19.4", "1.19.2", "1.18.2", "1.18.1",
    "1.17.1", "1.16.5", "1.12.2", "1.8.9", "1.7.10"
)
private val LOADERS = listOf("Vanilla", "Fabric", "Forge", "NeoForge", "Quilt")

/**
 * InstancesScreen — INSTARRION conectado ao NexusInstanceManager real.
 * Lê instâncias do disco, cria/remove/duplica via NexusInstanceManager.
 * Inicia download real via NexusDownloadManager.
 */
@Composable
fun InstancesScreen(
    onLaunchGame       : (String) -> Unit = {},
    onManageMods       : (String) -> Unit = {},
    onChangeDirectories: () -> Unit       = {},
    onBackToSolar      : () -> Unit       = {}
) {
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
    val instances    by NexusInstanceManager.instances.collectAsState()
    val downloadProg by NexusDownloadManager.progress.collectAsState()

    var selectedTab      by remember { mutableStateOf(0) }
    var selectedId       by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newName          by remember { mutableStateOf("") }
    var newVersionIdx    by remember { mutableStateOf(0) }
    var newLoaderIdx     by remember { mutableStateOf(0) }
    var editingId        by remember { mutableStateOf<String?>(null) }
    var editName         by remember { mutableStateOf("") }
    var statusMsg        by remember { mutableStateOf("") }

    val tabs = listOf("Todas", "Favoritas", "Recentes")

    val filteredInstances = when (selectedTab) {
        1    -> instances.filter { it.isFavorite }
        2    -> instances.sortedByDescending { it.createdAt }.take(5)
        else -> instances
    }

    Column(Modifier.fillMaxSize().background(DeepVoid).verticalScroll(rememberScrollState()).padding(16.dp)) {

        // Header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("INSTARRION", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("${instances.size} instância(s) • Minecraft Instances", color = TextSecondary, fontSize = 11.sp)
            }
            Button(
                onClick = { showCreateDialog = true },
                colors  = ButtonDefaults.buttonColors(backgroundColor = NexusOrange),
                shape   = RoundedCornerShape(8.dp)
            ) { Text("+ Nova", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        }

        Spacer(Modifier.height(12.dp))

        // Download progress bar
        if (downloadProg.state != NexusDownloadManager.DownloadState.IDLE) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Obsidian).border(1.dp, NexusCyan.copy(0.3f), RoundedCornerShape(10.dp)).padding(12.dp)) {
                Text(downloadProg.taskName, color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = downloadProg.percent / 100f,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color    = NexusCyan, backgroundColor = NexusCyan.copy(0.15f)
                )
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${downloadProg.percent}%", color = NexusCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(when (downloadProg.state) {
                        NexusDownloadManager.DownloadState.DONE  -> "✓ Concluído"
                        NexusDownloadManager.DownloadState.ERROR -> "✗ ${downloadProg.errorMsg.take(40)}"
                        else -> "${downloadProg.doneFiles}/${downloadProg.totalFiles} arquivos"
                    }, color = TextSecondary, fontSize = 10.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        // Status message
        if (statusMsg.isNotEmpty()) {
            Text(statusMsg, color = NexusCyan, fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                    .background(NexusCyan.copy(0.08f)).padding(8.dp))
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
        Spacer(Modifier.height(12.dp))

        // Create dialog
        if (showCreateDialog) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(Obsidian).border(1.dp, NexusOrange.copy(0.4f), RoundedCornerShape(12.dp)).padding(14.dp)) {
                Text("NOVA INSTÂNCIA", color = NexusOrange, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = newName, onValueChange = { newName = it },
                    label = { Text("Nome da instância", color = TextSecondary, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NexusCyan, unfocusedBorderColor = Color(0xFF333340),
                        textColor = Color.White, cursorColor = NexusCyan
                    ), singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Text("Versão do Minecraft", color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MC_VERSIONS.take(8).forEachIndexed { i, v ->
                        val sel = newVersionIdx == i
                        Box(Modifier.clip(RoundedCornerShape(6.dp))
                            .background(if (sel) NexusCyan.copy(0.2f) else Color(0xFF111120))
                            .border(1.dp, if (sel) NexusCyan else Color(0xFF222230), RoundedCornerShape(6.dp))
                            .clickable { newVersionIdx = i }.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(v, color = if (sel) NexusCyan else TextSecondary, fontSize = 10.sp,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Loader", color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LOADERS.forEachIndexed { i, loader ->
                        val sel = newLoaderIdx == i
                        Box(Modifier.clip(RoundedCornerShape(6.dp))
                            .background(if (sel) NexusOrange.copy(0.2f) else Color(0xFF111120))
                            .border(1.dp, if (sel) NexusOrange else Color(0xFF222230), RoundedCornerShape(6.dp))
                            .clickable { newLoaderIdx = i }.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(loader, color = if (sel) NexusOrange else TextSecondary, fontSize = 10.sp,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { showCreateDialog = false; newName = "" },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1A1A28)),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                        Text("Cancelar", color = TextSecondary, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            if (newName.isNotBlank()) {
                                val ver    = MC_VERSIONS[newVersionIdx]
                                val loader = LOADERS[newLoaderIdx]
                                val name   = newName.trim()
                                scope.launch {
                                    statusMsg = "Criando instância..."
                                    val inst = NexusInstanceManager.createInstance(name, ver, loader)
                                    showCreateDialog = false
                                    newName = ""
                                    statusMsg = "Instância '$name' criada! Instale a versão para jogar."
                                    // Iniciar download
                                    val ok = NexusDownloadManager.installVersion(
                                        context, ver, loader, "", inst.dirPath
                                    )
                                    if (ok) {
                                        NexusInstanceManager.markReady(inst.id)
                                        statusMsg = "✓ $name (${ver}) pronta para jogar!"
                                    }
                                }
                            }
                        },
                        colors  = ButtonDefaults.buttonColors(backgroundColor = NexusOrange),
                        shape   = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f),
                        enabled = newName.isNotBlank()
                    ) { Text("Criar & Instalar", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Instance list
        if (filteredInstances.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🪐", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Nenhuma instância encontrada", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Crie sua primeira instância acima", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            filteredInstances.forEach { inst ->
                val isSelected = selectedId == inst.id
                val loaderColor = when (inst.loader) {
                    "Fabric"   -> Color(0xFFB3E5FC)
                    "Forge"    -> NexusOrange
                    "NeoForge" -> Color(0xFFFF6D00)
                    "Quilt"    -> Color(0xFF7B61FF)
                    else       -> Color(0xFF00E676)
                }

                Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Obsidian else Color(0xFF0E0E16))
                    .border(1.dp, if (isSelected) NexusCyan.copy(0.4f) else Color(0xFF1A1A26), RoundedCornerShape(12.dp))
                    .clickable { selectedId = if (isSelected) null else inst.id }) {

                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Icon
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
                            .background(loaderColor.copy(0.15f))
                            .border(1.dp, loaderColor.copy(0.4f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center) {
                            Text(if (inst.loader == "Vanilla") "🌿" else if (inst.loader == "Fabric") "🪡" else "⚙", fontSize = 20.sp)
                        }

                        // Name & info
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(inst.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (inst.isFavorite) Text("★", color = Color(0xFFFFD600), fontSize = 12.sp)
                                if (!inst.isReady) Text("⬇", color = NexusOrange, fontSize = 11.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(inst.mcVersion, color = NexusCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Box(Modifier.clip(RoundedCornerShape(4.dp)).background(loaderColor.copy(0.15f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text(inst.loader, color = loaderColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                if (inst.isLastUsed) Text("Recente", color = TextSecondary, fontSize = 9.sp)
                            }
                        }

                        // Status & RAM
                        Column(horizontalAlignment = Alignment.End) {
                            Text(if (inst.isReady) "✓ Pronta" else "Não instalada", color = if (inst.isReady) Color(0xFF00E676) else NexusOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${inst.ramMb}MB RAM", color = TextSecondary, fontSize = 9.sp)
                        }
                    }

                    // Expanded actions
                    if (isSelected) {
                        Divider(color = NexusCyan.copy(0.1f))
                        if (editingId == inst.id) {
                            Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = editName, onValueChange = { editName = it },
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = NexusCyan, unfocusedBorderColor = Color(0xFF333340),
                                        textColor = Color.White, cursorColor = NexusCyan
                                    ), singleLine = true, label = { Text("Novo nome", color = TextSecondary, fontSize = 10.sp) }
                                )
                                Button(onClick = {
                                    if (editName.isNotBlank()) {
                                        NexusInstanceManager.renameInstance(inst.id, editName.trim())
                                        editingId = null; editName = ""
                                    }
                                }, colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan), shape = RoundedCornerShape(6.dp)) {
                                    Text("OK", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(onClick = { editingId = null; editName = "" },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1A1A28)), shape = RoundedCornerShape(6.dp)) {
                                    Text("✗", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        } else {
                            Row(Modifier.padding(10.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                InstanceActionBtn("🚀 Jogar", NexusOrange, inst.isReady) { onLaunchGame(inst.id) }
                                InstanceActionBtn("🧩 Mods", NexusCyan) { onManageMods(inst.id) }
                                InstanceActionBtn("★", if (inst.isFavorite) Color(0xFFFFD600) else TextSecondary) {
                                    NexusInstanceManager.toggleFavorite(inst.id)
                                }
                                InstanceActionBtn("✏ Renomear", TextSecondary) { editingId = inst.id; editName = inst.name }
                                InstanceActionBtn("📋 Duplicar", Color(0xFF7B61FF)) {
                                    scope.launch { NexusInstanceManager.duplicateInstance(inst.id) }
                                }
                                InstanceActionBtn("🗑 Remover", Color(0xFFCF4455)) {
                                    scope.launch {
                                        NexusInstanceManager.removeInstance(inst.id)
                                        selectedId = null
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Back button
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onChangeDirectories,
                colors = ButtonDefaults.buttonColors(backgroundColor = Obsidian),
                shape = RoundedCornerShape(8.dp)) {
                Text("📁 Diretórios", color = TextSecondary, fontSize = 12.sp)
            }
            Button(onClick = onBackToSolar,
                colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan.copy(0.15f)),
                shape = RoundedCornerShape(8.dp)) {
                Text("← Sistema Solar", color = NexusCyan, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun InstanceActionBtn(
    label  : String,
    color  : Color,
    enabled: Boolean  = true,
    onClick: () -> Unit
) {
    Box(Modifier.clip(RoundedCornerShape(6.dp))
        .background(color.copy(if (enabled) 0.12f else 0.05f))
        .border(1.dp, color.copy(if (enabled) 0.4f else 0.15f), RoundedCornerShape(6.dp))
        .clickable(enabled = enabled, onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 6.dp)) {
        Text(label, color = if (enabled) color else color.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}