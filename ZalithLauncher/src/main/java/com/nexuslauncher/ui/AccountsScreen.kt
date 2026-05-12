package com.nexuslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuslauncher.ui.theme.DeepVoid
import com.nexuslauncher.ui.theme.Graphite
import com.nexuslauncher.ui.theme.NexusCyan
import com.nexuslauncher.ui.theme.Obsidian
import com.nexuslauncher.ui.theme.TextSecondary

@Composable
fun AccountsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("CONTAS", color = NexusCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("Microsoft · Offline · Skins", color = TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(20.dp))

        // Conta ativa
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NexusCyan.copy(alpha = 0.06f))
                .border(1.dp, NexusCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text("CONTA ATIVA", color = NexusCyan, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NexusCyan.copy(0.15f))
                        .border(2.dp, NexusCyan.copy(0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎮", fontSize = 24.sp)
                }
                Column {
                    Text("Nenhuma conta configurada", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Adicione uma conta para jogar", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Adicionar conta
        Button(
            onClick = { /* TODO: autenticação OAuth2 Microsoft */ },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0078D4))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔷", fontSize = 16.sp)
                Text("Adicionar Conta Microsoft", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { /* TODO: conta offline local */ },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("👤", fontSize = 16.sp)
                Text("Adicionar Conta Offline", color = NexusCyan, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Divider(color = Graphite)

        Spacer(Modifier.height(16.dp))

        // Lista de contas (vazia)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Obsidian)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📭", fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Nenhuma conta configurada",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Após adicionar uma conta, ela aparecerá aqui.\nA conta ativa será usada ao iniciar o jogo.",
                color = TextSecondary.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Info de skins
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Obsidian)
                .padding(14.dp)
        ) {
            Text("🎭 Skins", color = NexusCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("As skins são sincronizadas automaticamente com a conta Microsoft.", color = TextSecondary, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))
            Text("Para contas offline, defina uma skin personalizada.", color = TextSecondary.copy(0.6f), fontSize = 10.sp)
        }
    }
}
