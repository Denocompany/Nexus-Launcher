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
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
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

data class ModItem(
    val id          : String,
    val name        : String,
    val version     : String,
    val author      : String  = "Autor desconhecido",
    val needsUpdate : Boolean = false,
    val enabled     : Boolean = true
)

data class ResourcePack(
    val id      : String,
    val name    : String,
    val version : String,
    val enabled : Boolean = false,
    val priority: Int     = 0
)

@Composable
fun ModsScreen(onNavigateTo: (String) -> Unit = {}) {

    val mods = remember {
        mutableStateListOf(
            ModItem("1", "Nexus Textures",  "v2.3",    "NexusTeam", enabled = true),
            ModItem("2", "Shader HDR",      "1.3",     "ShaderDev",  needsUpdate = true, enabled = true),
            ModItem("3", "FeatCraft Pro",   "v1.22",   "FeatLabs",  enabled = true),
            ModItem("4", "Dynamic Lights",  "v1.0",    "SpeedyCom", enabled = true),
            ModItem("5", "OptiFine HD",     "v1.18.2", "sp614x",    enabled = false),
            ModItem("6", "JEI",             "v11.6.0", "mezz",      enabled = true),
        )
    }

    val resourcePacks = remember {
        mutableStateListOf(
            ResourcePack("1", "Faithful 32x",    "1.20.x", priority = 1),
            ResourcePack("2", "Sphax PureBDCraft","1.16.x", enabled = true, priority = 2),
            ResourcePack("3", "Default Tweaked", "1.x",    priority = 3),
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf("Instalados", "Downloads", "Pacotes de Textura")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("MODS & PLUGINS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("${mods.count { it.enabled }} ativos · ${mods.size} instalados · ${resourcePacks.count { it.enabled }} resource pack(s)", color = TextSecondary, fontSize = 11.sp)
            }
            if (mods.any { it.needsUpdate }) {
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp)).background(NexusOrange.copy(0.15f)).border(1.dp, NexusOrange, RoundedCornerShape(6.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("${mods.count { it.needsUpdate }} update(s)", color = NexusOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Abas ────────────────────────────────────────────────────────────
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

            // ── Aba 0: Instalados ────────────────────────────────────────
            0 -> {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    mods.forEachIndexed { idx, mod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (mod.enabled) Color(0xFF141420) else Color(0xFF0E0E18))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        mod.name,
                                        color      = if (mod.enabled) Color.White else TextSecondary,
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (mod.needsUpdate) {
                                        Box(Modifier.clip(RoundedCornerShape(4.dp)).background(NexusOrange.copy(0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text("ATUALIZAR", color = NexusOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text("${mod.version} · por ${mod.author}", color = TextSecondary, fontSize = 10.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (mod.needsUpdate) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NexusOrange.copy(0.15f))
                                            .clickable { mods[idx] = mod.copy(needsUpdate = false, version = "${mod.version}+1") }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("⬆", color = NexusOrange, fontSize = 12.sp)
                                    }
                                }
                                Switch(
                                    checked         = mod.enabled,
                                    onCheckedChange = { mods[idx] = mod.copy(enabled = it) },
                                    colors          = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(0.4f))
                                )
                            }
                        }
                        if (idx < mods.size - 1) Divider(color = Color(0xFF1A1A28), thickness = 1.dp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick  = { mods.indices.forEach { mods[it] = mods[it].copy(needsUpdate = false) } },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(backgroundColor = NexusCyan)
                ) {
                    Text("⬆ ATUALIZAR TUDO", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick  = { selectedTab = 1 },
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) { Text("+ Adicionar Mod", color = NexusCyan, fontSize = 11.sp) }
                    Button(
                        onClick  = { /* importar mod */ },
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) { Text("📂 Importar .jar", color = NexusCyan, fontSize = 11.sp) }
                }
            }

            // ── Aba 1: Downloads (CurseForge) ────────────────────────────
            1 -> {
                val searchResults = remember {
                    listOf(
                        ModItem("d1", "Just Enough Items (JEI)", "v13.0",  "mezz"),
                        ModItem("d2", "Waystones",               "v14.1",  "BlayTheNinth"),
                        ModItem("d3", "Biomes O' Plenty",        "v18.0",  "Forstride"),
                        ModItem("d4", "Applied Energistics 2",   "v15.0",  "AlgorithmX2"),
                        ModItem("d5", "Tinkers' Construct",      "v3.8",   "mDiyo"),
                        ModItem("d6", "Create",                  "v0.5.1", "simibubi"),
                    )
                }

                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier      = Modifier.fillMaxWidth(),
                    label         = { Text("🔍 Buscar no CurseForge Orbital...", color = TextSecondary, fontSize = 11.sp) },
                    singleLine    = true,
                    colors        = TextFieldDefaults.outlinedTextFieldColors(
                        textColor            = Color.White,
                        focusedBorderColor   = NexusCyan,
                        unfocusedBorderColor = TextSecondary.copy(0.4f),
                        cursorColor          = NexusCyan
                    )
                )

                Spacer(Modifier.height(12.dp))

                Text("Mods populares para 1.18.2", color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))

                val filtered = if (searchQuery.isBlank()) searchResults
                               else searchResults.filter { it.name.contains(searchQuery, ignoreCase = true) }

                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    filtered.forEach { mod ->
                        val alreadyInstalled = mods.any { it.name == mod.name }
                        Row(
                            modifier              = Modifier.fillMaxWidth().background(Color(0xFF141420)).padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(mod.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("${mod.version} · ${mod.author}", color = TextSecondary, fontSize = 10.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (alreadyInstalled) Color(0xFF00E676).copy(0.1f) else NexusCyan.copy(0.1f))
                                    .border(1.dp, if (alreadyInstalled) Color(0xFF00E676).copy(0.4f) else NexusCyan.copy(0.4f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        if (!alreadyInstalled) {
                                            mods.add(mod.copy(id = System.currentTimeMillis().toString()))
                                            selectedTab = 0
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    if (alreadyInstalled) "✓ Instalado" else "⬇ Instalar",
                                    color      = if (alreadyInstalled) Color(0xFF00E676) else NexusCyan,
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Nenhum resultado para \"$searchQuery\"", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // ── Aba 2: Pacotes de Textura ─────────────────────────────────
            2 -> {
                Text("RESOURCE PACKS INSTALADOS", color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    resourcePacks.forEachIndexed { idx, pack ->
                        Row(
                            modifier              = Modifier.fillMaxWidth().background(Color(0xFF141420)).padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFF7B61FF).copy(0.15f)).border(1.dp, Color(0xFF7B61FF).copy(0.3f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) { Text("🎨", fontSize = 18.sp) }
                                Column {
                                    Text(pack.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("${pack.version} · Prioridade ${pack.priority}", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                            Switch(
                                checked         = pack.enabled,
                                onCheckedChange = { resourcePacks[idx] = pack.copy(enabled = it) },
                                colors          = SwitchDefaults.colors(checkedThumbColor = Color(0xFF7B61FF), checkedTrackColor = Color(0xFF7B61FF).copy(0.4f))
                            )
                        }
                        if (idx < resourcePacks.size - 1) Divider(color = Color(0xFF1A1A28), thickness = 1.dp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick  = { /* abrir seletor de arquivo */ },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) { Text("📂 Importar Pack (.zip)", color = Color(0xFF7B61FF), fontSize = 11.sp) }
                    Button(
                        onClick  = { selectedTab = 1 },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                    ) { Text("🔍 Buscar Online", color = NexusCyan, fontSize = 11.sp) }
                }
            }
        }
    }
}
