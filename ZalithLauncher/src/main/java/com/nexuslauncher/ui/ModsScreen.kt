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
import com.nexuslauncher.core.NexusModManager
import com.nexuslauncher.datastore.NexusDataStore
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.NexusOrange
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * ModsScreen — MODARA conectado ao NexusModManager real.
 * Lê mods reais do disco da instância selecionada.
 * Ativa/desativa movendo entre /mods e /mods.disabled.
 */
@Composable
fun ModsScreen(
    nexusDataStore   : NexusDataStore? = null,
    instanceDir      : String          = "",
    onBackToInstances: () -> Unit       = {},
    onOpenVisual     : () -> Unit       = {},
    onBackToSolar    : () -> Unit       = {}
) {
    val scope    = rememberCoroutineScope()
    val mods     by NexusModManager.mods.collectAsState()
    val loading  by NexusModManager.loading.collectAsState()

    var selectedTab  by remember { mutableStateOf(0) }
    var searchQuery  by remember { mutableStateOf("") }
    val tabs = listOf("Mods", "Resource Packs")

    // Load mods when instance dir changes
    LaunchedEffect(instanceDir) {
        if (instanceDir.isNotEmpty()) {
            NexusModManager.loadMods(instanceDir)
        }
    }

    val filteredMods = mods.filter {
        searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) ||
        it.author.contains(searchQuery, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize().background(DeepVoid).verticalScroll(rememberScrollState()).padding(16.dp)) {

        // Header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("MODARA", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text(NexusModManager.activeCountLabel(), color = TextSecondary, fontSize = 11.sp)
            }
            if (loading) {
                CircularProgressIndicator(color = NexusCyan, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        }
        Spacer(Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            label = { Text("Buscar mods...", color = TextSecondary, fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = NexusCyan, unfocusedBorderColor = Color(0xFF333340),
                textColor = Color.White, cursorColor = NexusCyan
            ), singleLine = true
        )
        Spacer(Modifier.height(10.dp))

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

        when (selectedTab) {
            0 -> {
                // Mods tab
                if (instanceDir.isEmpty()) {
                    InfoBox("Selecione uma instância em INSTARRION para ver os mods.", NexusOrange)
                } else if (filteredMods.isEmpty() && !loading) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 30.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🧩", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Nenhum mod encontrado", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Instale mods na pasta /mods da instância", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                } else {
                    // Group by loader
                    val byLoader = filteredMods.groupBy { it.loader.replaceFirstChar { c -> c.uppercase() } }
                    byLoader.forEach { (loader, loaderMods) ->
                        val loaderColor = when (loader.lowercase()) {
                            "fabric"   -> Color(0xFFB3E5FC)
                            "forge"    -> NexusOrange
                            "quilt"    -> Color(0xFF7B61FF)
                            "neoforge" -> Color(0xFFFF6D00)
                            else       -> TextSecondary
                        }
                        Text(loader.uppercase(), color = loaderColor, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        loaderMods.forEach { mod ->
                            ModItem(mod, instanceDir, scope)
                            Spacer(Modifier.height(4.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
            1 -> {
                // Resource packs
                val rpDir = "$instanceDir/resourcepacks"
                InfoBox("Pasta de resource packs: $rpDir\nInstale arquivos .zip nessa pasta e reinicie o launcher.", NexusCyan)
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBackToInstances, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Obsidian), shape = RoundedCornerShape(8.dp)) {
                Text("← Instâncias", color = TextSecondary, fontSize = 12.sp)
            }
            Button(onClick = onBackToSolar, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan.copy(0.15f)), shape = RoundedCornerShape(8.dp)) {
                Text("← Solar", color = NexusCyan, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ModItem(
    mod         : NexusModManager.NexusMod,
    instanceDir : String,
    scope       : kotlinx.coroutines.CoroutineScope
) {
    val loaderColor = when (mod.loader.lowercase()) {
        "fabric"   -> Color(0xFFB3E5FC)
        "forge"    -> NexusOrange
        "quilt"    -> Color(0xFF7B61FF)
        "neoforge" -> Color(0xFFFF6D00)
        else       -> TextSecondary
    }

    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF0E0E16))
        .border(1.dp, if (mod.isEnabled) Color(0xFF1A1A26) else Color(0xFF222222), RoundedCornerShape(10.dp))
        .padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {

        // Status dot
        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp))
            .background(if (mod.isEnabled) Color(0xFF00E676) else Color(0xFF444455)))

        // Mod info
        Column(Modifier.weight(1f)) {
            Text(mod.name, color = if (mod.isEnabled) Color.White else TextSecondary,
                fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("v${mod.version}", color = TextSecondary, fontSize = 10.sp)
                if (mod.author != "Desconhecido") Text("· ${mod.author}", color = TextSecondary, fontSize = 10.sp)
                if (mod.fileSizeKb > 0) Text("· ${mod.fileSizeKb}KB", color = TextSecondary, fontSize = 9.sp)
            }
        }

        // Toggle
        Switch(
            checked  = mod.isEnabled,
            onCheckedChange = { enabled ->
                scope.launch {
                    if (enabled) NexusModManager.enableMod(mod.filePath, instanceDir)
                    else NexusModManager.disableMod(mod.filePath, instanceDir)
                }
            },
            colors = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(0.5f),
                uncheckedThumbColor = TextSecondary, uncheckedTrackColor = TextSecondary.copy(0.3f))
        )

        // Remove
        Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFCF4455).copy(0.15f))
            .border(1.dp, Color(0xFFCF4455).copy(0.3f), RoundedCornerShape(6.dp))
            .clickable { scope.launch { NexusModManager.removeMod(mod.filePath, instanceDir) } }
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text("🗑", fontSize = 12.sp)
        }
    }
}

@Composable
private fun InfoBox(text: String, color: Color) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
        .background(color.copy(0.08f)).border(1.dp, color.copy(0.3f), RoundedCornerShape(10.dp))
        .padding(12.dp)) {
        Text(text, color = color, fontSize = 12.sp)
    }
}