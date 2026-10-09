package com.example.fintrack.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Subscriptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
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

    val activeSubscriptions = subscriptions.filter { it.active }
    val totalMonthlyCost = activeSubscriptions.sumOf { it.cost }

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
                        text = "Pengingat Langganan & SIM",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextForeground
                    )
                    Text(
                        text = "Kelola tagihan rutin bulanan & kuota",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = onOpenAddSub,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontSize = 12.sp)
                }
            }
        }

        item {
            // Burn rate card
            FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarningAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TOTAL BEBAN LANGGANAN / BULAN",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = FinTrackRepository.formatRupiah(totalMonthlyCost),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        Text(
                            text = "${activeSubscriptions.size} Langganan aktif berjalan",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryMint
                        )
                    }
                }
            }
        }

        items(subscriptions, key = { it.id }) { sub ->
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Subscriptions, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sub.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextForeground
                        )
                        Text(
                            text = "${sub.category} · Jatuh tempo: ${sub.nextDueDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = FinTrackRepository.formatRupiah(sub.cost),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        Switch(
                            checked = sub.active,
                            onCheckedChange = { FinTrackRepository.toggleSubscription(sub.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SecondaryMint,
                                uncheckedTrackColor = Color.Gray
                            )
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
