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

data class GameInstance(
    val id         : String,
    val name       : String,
    val version    : String,
    val loader     : String,
    val modCount   : Int,
    val ramAlloc   : String,
    val needsUpdate: Boolean = false,
    val isFavorite : Boolean = false
)

private val MC_VERSIONS = listOf("1.21.4", "1.20.4", "1.20.1", "1.19.4", "1.18.2", "1.18.1", "1.16.5", "1.12.2", "1.8.9")
private val LOADERS     = listOf("Vanilla", "Fabric", "Forge", "NeoForge", "Quilt")

@Composable
fun InstancesScreen(
    onLaunchGame : () -> Unit = {},
    onNavigateTo : (String) -> Unit = {}
) {
    val instances = remember {
        mutableStateListOf(
            GameInstance("1", "Survival 1.18.1",    "1.18.1", "Fabric",  8,  "1.8 GB",  isFavorite = true),
            GameInstance("2", "Tech Modpack 1.16.5", "1.16.5", "Forge",  15, "2.6 GB",  needsUpdate = true),
            GameInstance("3", "SkyBlock 1.12.2",     "1.12.2", "Forge",  4,  "512 MB",  isFavorite = true),
            GameInstance("4", "Create Mod Pack",     "1.18.2", "Forge",  22, "3.5 GB"),
            GameInstance("5", "Vanilla 1.21.4",      "1.21.4", "Vanilla", 0, "1.0 GB"),
        )
    }

    var selectedTab       by remember { mutableStateOf(0) }
    var selectedId        by remember { mutableStateOf<String?>(null) }
    var editingId         by remember { mutableStateOf<String?>(null) }
    var editName          by remember { mutableStateOf("") }

    // Nova instância
    var newName           by remember { mutableStateOf("") }
    var newVersionIdx     by remember { mutableStateOf(0) }
    var newLoaderIdx      by remember { mutableStateOf(0) }
    var showVersionPicker by remember { mutableStateOf(false) }
    var showLoaderPicker  by remember { mutableStateOf(false) }

    val tabs = listOf("Instâncias", "Favoritas", "Nova Instância", "Diretórios")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("INSTÂNCIAS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("${instances.size} instâncias · ${instances.count { it.isFavorite }} favorita(s)", color = TextSecondary, fontSize = 11.sp)
            }
            SmallActionButton("+ Nova") { selectedTab = 2 }
        }

        Spacer(Modifier.height(16.dp))

        // ── Abas ────────────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == i) NexusCyan.copy(0.15f) else Color(0xFF111120))
                        .border(1.dp, if (selectedTab == i) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { selectedTab = i }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
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

        // ── Conteúdo por aba ─────────────────────────────────────────────
        when (selectedTab) {

            // ── Aba 0: Lista de Instâncias ───────────────────────────────
            0 -> {
                if (instances.isEmpty()) {
                    EmptyState("Nenhuma instância criada") { selectedTab = 2 }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        instances.forEachIndexed { idx, inst ->
                            val isSelected = selectedId == inst.id
                            val isEditing  = editingId == inst.id

                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) NexusCyan.copy(0.08f) else Color(0xFF141420))
                                        .clickable {
                                            selectedId = if (isSelected) null else inst.id
                                            editingId  = null
                                        }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(
                                            Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NexusCyan.copy(0.1f))
                                                .border(1.dp, NexusCyan.copy(0.3f), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("⛏", fontSize = 20.sp)
                                        }
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(inst.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                                if (inst.isFavorite) Text("⭐", fontSize = 10.sp)
                                                if (inst.needsUpdate) {
                                                    Box(Modifier.clip(RoundedCornerShape(4.dp)).background(NexusOrange.copy(0.2f)).padding(horizontal = 5.dp, vertical = 1.dp)) {
                                                        Text("UPDATE", color = NexusOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            Text("${inst.loader} · MC ${inst.version} · ${inst.modCount} mods · ${inst.ramAlloc}", color = TextSecondary, fontSize = 10.sp)
                                        }
                                    }
                                    Text(if (isSelected) "▲" else "▼", color = TextSecondary, fontSize = 10.sp)
                                }

                                // Painel expandido com ações
                                if (isSelected) {
                                    if (isEditing) {
                                        Column(Modifier.fillMaxWidth().background(Color(0xFF0E0E1C)).padding(14.dp)) {
                                            Text("Renomear instância", color = NexusCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Spacer(Modifier.height(8.dp))
                                            OutlinedTextField(
                                                value         = editName,
                                                onValueChange = { editName = it },
                                                modifier      = Modifier.fillMaxWidth(),
                                                label         = { Text("Nome", color = TextSecondary, fontSize = 11.sp) },
                                                singleLine    = true,
                                                colors        = TextFieldDefaults.outlinedTextFieldColors(
                                                    textColor           = Color.White,
                                                    focusedBorderColor  = NexusCyan,
                                                    unfocusedBorderColor = TextSecondary.copy(0.4f),
                                                    cursorColor         = NexusCyan
                                                )
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        if (editName.isNotBlank()) {
                                                            val i = instances.indexOfFirst { it.id == inst.id }
                                                            if (i >= 0) instances[i] = inst.copy(name = editName.trim())
                                                        }
                                                        editingId = null
                                                    },
                                                    modifier = Modifier.weight(1f).height(40.dp),
                                                    shape    = RoundedCornerShape(8.dp),
                                                    colors   = ButtonDefaults.buttonColors(backgroundColor = NexusCyan)
                                                ) { Text("Salvar", color = Color.Black, fontWeight = FontWeight.Bold) }
                                                Button(
                                                    onClick = { editingId = null },
                                                    modifier = Modifier.weight(1f).height(40.dp),
                                                    shape    = RoundedCornerShape(8.dp),
                                                    colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                                                ) { Text("Cancelar", color = TextSecondary) }
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier              = Modifier.fillMaxWidth().background(Color(0xFF0E0E1C)).padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            ActionBtn("▶ Iniciar",   NexusCyan)       { onLaunchGame() }
                                            ActionBtn("✏ Editar",    Color(0xFF7B61FF)) {
                                                editName  = inst.name
                                                editingId = inst.id
                                            }
                                            ActionBtn("⎘ Duplicar",  NexusOrange) {
                                                val copy = inst.copy(
                                                    id   = System.currentTimeMillis().toString(),
                                                    name = "${inst.name} (cópia)"
                                                )
                                                instances.add(idx + 1, copy)
                                            }
                                            ActionBtn(
                                                "⭐ ${if (inst.isFavorite) "Desfav." else "Favoritar"}",
                                                Color(0xFFFFCC00)
                                            ) {
                                                instances[idx] = inst.copy(isFavorite = !inst.isFavorite)
                                            }
                                            ActionBtn("🗑 Remover",   Color(0xFFFF5252)) {
                                                instances.removeAt(idx)
                                                selectedId = null
                                            }
                                        }
                                    }
                                }

                                if (idx < instances.size - 1) Divider(color = Color(0xFF1A1A28), thickness = 1.dp)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick  = { onNavigateTo("modara") },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) {
                        Text("🧩 Gerenciar Mods desta Instância", color = NexusCyan, fontSize = 12.sp)
                    }
                }
            }

            // ── Aba 1: Favoritas ─────────────────────────────────────────
            1 -> {
                val favs = instances.filter { it.isFavorite }
                if (favs.isEmpty()) {
                    EmptyState("Nenhuma instância favoritada") { selectedTab = 0 }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        favs.forEach { inst ->
                            Row(
                                modifier              = Modifier.fillMaxWidth().background(Color(0xFF141420)).clickable { onLaunchGame() }.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("⭐", fontSize = 12.sp)
                                        Text(inst.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("${inst.loader} · MC ${inst.version}", color = TextSecondary, fontSize = 10.sp)
                                }
                                Text("▶ Iniciar", color = NexusCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ── Aba 2: Nova Instância ─────────────────────────────────────
            2 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(20.dp)
                ) {
                    Text("CRIAR NOVA INSTÂNCIA", color = NexusCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(16.dp))

                    // Nome
                    OutlinedTextField(
                        value         = newName,
                        onValueChange = { newName = it },
                        modifier      = Modifier.fillMaxWidth(),
                        label         = { Text("Nome da instância *", color = TextSecondary, fontSize = 11.sp) },
                        singleLine    = true,
                        colors        = TextFieldDefaults.outlinedTextFieldColors(
                            textColor            = Color.White,
                            focusedBorderColor   = NexusCyan,
                            unfocusedBorderColor = TextSecondary.copy(0.4f),
                            cursorColor          = NexusCyan
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    // Versão
                    Text("Versão do Minecraft", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MC_VERSIONS.take(5).forEachIndexed { i, v ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (newVersionIdx == i) NexusCyan.copy(0.2f) else Color(0xFF111120))
                                    .border(1.dp, if (newVersionIdx == i) NexusCyan else TextSecondary.copy(0.3f), RoundedCornerShape(6.dp))
                                    .clickable { newVersionIdx = i }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(v, color = if (newVersionIdx == i) NexusCyan else TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MC_VERSIONS.drop(5).forEachIndexed { i, v ->
                            val realIdx = i + 5
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (newVersionIdx == realIdx) NexusCyan.copy(0.2f) else Color(0xFF111120))
                                    .border(1.dp, if (newVersionIdx == realIdx) NexusCyan else TextSecondary.copy(0.3f), RoundedCornerShape(6.dp))
                                    .clickable { newVersionIdx = realIdx }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(v, color = if (newVersionIdx == realIdx) NexusCyan else TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Loader
                    Text("Mod Loader", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LOADERS.forEachIndexed { i, loader ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (newLoaderIdx == i) NexusOrange.copy(0.2f) else Color(0xFF111120))
                                    .border(1.dp, if (newLoaderIdx == i) NexusOrange else TextSecondary.copy(0.3f), RoundedCornerShape(6.dp))
                                    .clickable { newLoaderIdx = i }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(loader, color = if (newLoaderIdx == i) NexusOrange else TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (newName.isNotBlank()) {
                                instances.add(
                                    GameInstance(
                                        id      = System.currentTimeMillis().toString(),
                                        name    = newName.trim(),
                                        version = MC_VERSIONS[newVersionIdx],
                                        loader  = LOADERS[newLoaderIdx],
                                        modCount = 0,
                                        ramAlloc = "1.0 GB"
                                    )
                                )
                                newName       = ""
                                newVersionIdx = 0
                                newLoaderIdx  = 0
                                selectedTab   = 0
                            }
                        },
                        enabled  = newName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(
                            backgroundColor = NexusCyan,
                            disabledBackgroundColor = NexusCyan.copy(0.3f)
                        )
                    ) {
                        Text("✓ CRIAR INSTÂNCIA", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // ── Aba 3: Diretórios ────────────────────────────────────────
            3 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(16.dp)
                ) {
                    Text("DIRETÓRIOS DO JOGO", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    DirectoryRow("📂 Jogo",          "/games/minecraft")
                    DirectoryRow("🧩 Mods",           "/games/minecraft/mods")
                    DirectoryRow("🎨 Resource Packs", "/games/minecraft/resourcepacks")
                    DirectoryRow("🌍 Saves",          "/games/minecraft/saves")
                    DirectoryRow("📸 Screenshots",    "/games/minecraft/screenshots")
                    DirectoryRow("☕ Java",            "/usr/lib/jvm/java-17")
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick  = { onNavigateTo("helios") },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) {
                        Text("⚙ Alterar Diretórios em Configurações", color = NexusCyan, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionBtn(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DirectoryRow(label: String, path: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(path, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Divider(color = Color(0xFF1A1A28), modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun EmptyState(message: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📭", fontSize = 32.sp)
        Spacer(Modifier.height(8.dp))
        Text(message, color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        SmallActionButton("+ Criar Nova Instância", onAction)
    }
}
