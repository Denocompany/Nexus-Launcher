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
import androidx.compose.material.Text
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
    val name: String,
    val version: String,
    val modCount: Int,
    val ramAlloc: String,
    val needsUpdate: Boolean = false,
    val isFavorite: Boolean = false
)

@Composable
fun InstancesScreen() {
    val instances = remember {
        listOf(
            GameInstance("Survival 1.18.1",    "1.18.1", 8,  "1.8 GB Aloc."),
            GameInstance("Tech Modpack 1.16.5", "1.16.5", 15, "26 MB Aloc.", needsUpdate = true),
            GameInstance("SkyBlock 1.12.2",     "1.12.2", 4,  "5.03 MB Aloc.", isFavorite = true),
            GameInstance("Create Mod Pack",     "1.18.2", 22, "3.5 GB Aloc."),
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Instâncias", "Favoritas", "Nova Instância")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("INSTÂNCIAS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("${instances.size} instâncias · 1 favorita", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(16.dp))

        // Abas
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == i) NexusCyan.copy(alpha = 0.15f) else Color(0xFF111120))
                        .border(1.dp, if (selectedTab == i) NexusCyan else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { selectedTab = i }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(tab, color = if (selectedTab == i) NexusCyan else TextSecondary,
                        fontSize = 11.sp, fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        val displayedInstances = when (selectedTab) {
            1    -> instances.filter { it.isFavorite }
            else -> instances
        }

        if (selectedTab == 2) {
            // Nova instância
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("+ Criar Nova Instância", color = NexusCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Selecione versão e configurações", color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan)
                ) {
                    Text("CRIAR INSTÂNCIA", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Obsidian),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                displayedInstances.forEach { instance ->
                    InstanceRow(instance)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = NexusCyan)
                ) {
                    Text("▶ Iniciar Selecionada", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1C1C2E))
                ) {
                    Text("📤 Exportar", color = NexusCyan, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun InstanceRow(instance: GameInstance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF141420))
            .clickable { }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NexusCyan.copy(alpha = 0.1f))
                    .border(1.dp, NexusCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("⛏", fontSize = 20.sp)
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(instance.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    if (instance.isFavorite) Text("⭐", fontSize = 10.sp)
                    if (instance.needsUpdate) {
                        Box(Modifier.clip(RoundedCornerShape(4.dp)).background(NexusOrange.copy(0.2f)).padding(horizontal = 5.dp, vertical = 1.dp)) {
                            Text("UPDATE", color = NexusOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("${instance.modCount} Mods Ativ. · ${instance.ramAlloc}", color = TextSecondary, fontSize = 10.sp)
            }
        }
        Text("MC ${instance.version}", color = NexusCyan.copy(alpha = 0.6f), fontSize = 10.sp)
    }
}
