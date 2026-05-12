package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
    val name: String,
    val version: String,
    val needsUpdate: Boolean = false,
    var enabled: Boolean = true
)

@Composable
fun ModsScreen() {
    val mods = remember {
        mutableStateListOf(
            ModItem("Nexus Textures", "v2.3", needsUpdate = false, enabled = true),
            ModItem("Shader HDR", "1.3", needsUpdate = true, enabled = true),
            ModItem("FeatCraft Pro", "v1.22", needsUpdate = false, enabled = true),
            ModItem("Dynamic Lights", "v1.0", needsUpdate = false, enabled = true),
            ModItem("OptiFine HD", "v1.18.2", needsUpdate = false, enabled = false),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Título
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("MODS & PLUGINS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("${mods.count { it.enabled }} ativos de ${mods.size} instalados", color = TextSecondary, fontSize = 11.sp)
            }
            if (mods.any { it.needsUpdate }) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NexusOrange.copy(alpha = 0.15f))
                        .border(1.dp, NexusOrange, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("${mods.count { it.needsUpdate }} update(s)", color = NexusOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Abas
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabChip("Instalados", true)
            TabChip("Atualizar Logo", false)
            TabChip("Relatórios", false)
        }

        Spacer(Modifier.height(14.dp))

        // Lista de mods
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Obsidian),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            mods.forEachIndexed { index, mod ->
                ModRow(
                    mod = mod,
                    onToggle = { mods[index] = mod.copy(enabled = !mod.enabled) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Ações
        Button(
            onClick = { /* atualizar todos */ },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan)
        ) {
            Text("⬆ ATUALIZAR TUDO", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
            ) {
                Text("+ Adicionar Mod", color = NexusCyan, fontSize = 11.sp)
            }
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
            ) {
                Text("📂 Importar", color = NexusCyan, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ModRow(mod: ModItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (mod.enabled) Color(0xFF141420) else Color(0xFF0E0E18))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(mod.name, color = if (mod.enabled) Color.White else TextSecondary,
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (mod.needsUpdate) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(NexusOrange.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ATUALIZAR", color = NexusOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(mod.version, color = TextSecondary, fontSize = 10.sp)
        }
        Switch(
            checked = mod.enabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) NexusCyan.copy(alpha = 0.15f) else Color(0xFF111120))
            .border(1.dp, if (selected) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, color = if (selected) NexusCyan else TextSecondary, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
