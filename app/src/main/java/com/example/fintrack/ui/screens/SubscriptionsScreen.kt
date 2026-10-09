package com.example.fintrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.components.FinTrackProgressBar
import com.example.fintrack.ui.theme.DangerRed
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.DarkNavyMuted
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted
import com.example.fintrack.ui.theme.WarningAmber

@Composable
fun SubscriptionsScreen(
    onOpenAddSub: () -> Unit
) {
    val subscriptions by FinTrackRepository.subscriptions.collectAsState()
    val simCards by FinTrackRepository.simCards.collectAsState()

    var isDualSimMode by remember { mutableStateOf(true) }
    var selectedSimTab by remember { mutableStateOf(1) } // 1 or 2

    val activeSubscriptions = subscriptions.filter { it.active }
    val totalSubsMonthly = activeSubscriptions.sumOf { it.cost }
    val totalSimMonthly = simCards.sumOf { it.cost }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignItems = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pengingat Kuota, SIM & Tagihan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                    Text(
                        text = "Pantau paket data, tagihan bulanan & alarm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            // SIM Mode Toggle (Single vs Dual SIM)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkNavyCard)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isDualSimMode) PrimaryCobalt else Color.Transparent)
                        .clickable { isDualSimMode = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1 Kartu SIM", color = if (!isDualSimMode) Color.White else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDualSimMode) PrimaryCobalt else Color.Transparent)
                        .clickable { isDualSimMode = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2 Kartu (Dual SIM)", color = if (isDualSimMode) Color.White else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // SIM Cards List
        items(if (isDualSimMode) simCards else simCards.take(1)) { sim ->
            val isLowQuota = sim.remainingQuotaGb <= 10.0
            FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sim.simSlot == 1) DangerRed.copy(alpha = 0.2f) else PrimaryCobalt.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (sim.simSlot == 1) "T" else "XL",
                                color = if (sim.simSlot == 1) DangerRed else PrimaryCobalt,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("SIM ${sim.simSlot} • ${sim.provider}", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                if (isLowQuota) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Menipis", color = WarningAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(sim.planName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(FinTrackRepository.formatRupiah(sim.cost), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        Text("${sim.daysLeft} Hari Lagi", color = if (sim.daysLeft <= 3) DangerRed else SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quota Progress Meter
                val pct = ((sim.remainingQuotaGb * 100) / sim.totalQuotaGb).toInt()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sisa Kuota: ${sim.remainingQuotaGb} GB / ${sim.totalQuotaGb} GB", color = TextMuted, fontSize = 11.sp)
                    Text("$pct% Tersisa", color = if (isLowQuota) DangerRed else SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))
                FinTrackProgressBar(percent = 100 - pct, threshold = 75)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text("Perpanjangan Otomatis (Auto-Debit)", color = TextMuted, fontSize = 11.sp)
                    Switch(
                        checked = sim.autoDebit,
                        onCheckedChange = { FinTrackRepository.toggleSimDebit(sim.simSlot) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SecondaryMint)
                    )
                }
            }
        }

        item {
            // Section Header: Recurring Subscriptions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignItems = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Langganan & Tagihan Rutin",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                    Text(
                        text = "Total beban: ${FinTrackRepository.formatRupiah(totalSubsMonthly)} / bulan",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryMint
                    )
                }
                Button(
                    onClick = onOpenAddSub,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontSize = 11.sp)
                }
            }
        }

        items(subscriptions, key = { it.id }) { sub ->
            val isUrgent = sub.daysLeft <= 3
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isUrgent) DangerRed.copy(alpha = 0.2f) else PrimaryCobalt.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sub.name.take(1),
                            color = if (isUrgent) DangerRed else PrimaryCobalt,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(sub.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                            if (isUrgent) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DangerRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text("H-${sub.daysLeft}", color = DangerRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text("${sub.category} • ${sub.paymentSource}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(FinTrackRepository.formatRupiah(sub.cost), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        Switch(
                            checked = sub.active,
                            onCheckedChange = { FinTrackRepository.toggleSubscription(sub.id) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SecondaryMint)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
