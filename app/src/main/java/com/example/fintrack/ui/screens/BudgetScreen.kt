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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.fintrack.ui.components.CategoryIconBadge
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.components.FinTrackProgressBar
import com.example.fintrack.ui.theme.DangerRed
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted
import com.example.fintrack.ui.theme.WarningAmber

@Composable
fun BudgetScreen(
    onOpenAddBudget: () -> Unit
) {
    val budgets by FinTrackRepository.budgets.collectAsState()
    val transactions by FinTrackRepository.transactions.collectAsState()
    val selectedPeriod by FinTrackRepository.selectedPeriod.collectAsState()

    var selectedFilter by remember { mutableStateOf("all") } // "all", "attention", "safe", "savings"

    val totalBudgetLimit = budgets.sumOf { it.limit }
    val totalSpent = 6820000L
    val totalRemaining = (totalBudgetLimit - totalSpent).coerceAtLeast(0L)
    val overallPercent = 68 // 68.2%
    val safeDailySpend = 159000L

    val recentDays = FinTrackRepository.getRecentDays()
    var selectedDayIndex by remember { mutableStateOf(3) } // Kamis (Active)

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
                        text = "Anggaran & Pagu Pengeluaran",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                    Text(
                        text = "Terkontrol (68,2% terpakai) • 20 hari tersisa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryMint
                    )
                }
                Button(
                    onClick = onOpenAddBudget,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Alokasi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            // Hero Budget Overview Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text("TOTAL PAGU PENGELUARAN", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Status Sehat", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = FinTrackRepository.formatRupiah(totalBudgetLimit),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Meter visualization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Realisasi Akumulatif", color = TextMuted, fontSize = 11.sp)
                    Text("68,2%", color = PrimaryCobalt, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                FinTrackProgressBar(percent = overallPercent, threshold = 80)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Terpakai: Rp 6.820.000", color = TextForeground, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("Sisa: Rp 3.180.000", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Daily Burn Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyBackground)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("PAGU HARIAN AMAN", color = TextMuted, fontSize = 10.sp)
                            Text("Rp 159.000 / hari", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("20 hari lagi", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        item {
            // Smart Warning Alert Card
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(WarningAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("2 Kategori Mendekati Batas Pagu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        Text("Realisasi pengeluaran Belanja (97%) & Makanan (81%) melampaui batas 80%", color = WarningAmber, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            // Section: Kalender & Jadwal Pengeluaran Harian
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kalender & Jadwal Pengeluaran Harian", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal date strip
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentDays.size) { idx ->
                        val (dayLabel, dateStr) = recentDays[idx]
                        val isSelected = selectedDayIndex == idx
                        val dayNum = dateStr.takeLast(2)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryCobalt else DarkNavyBackground)
                                .clickable { selectedDayIndex = idx }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(dayLabel, color = if (isSelected) Color.White else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(dayNum, color = if (isSelected) Color.White else TextForeground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) SecondaryMint else PrimaryCobalt)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selected date detail card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyBackground)
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.EventAvailable, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pengeluaran Tanggal Terpilih", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Total: Rp 320.000", color = WarningAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("• Supermarket Grand Lucky: -Rp 185.000 (Belanja Bulanan)", color = TextMuted, fontSize = 11.sp)
                        Text("• Kopi Kenangan & Makan Siang: -Rp 65.000 (Makanan & Minuman)", color = TextMuted, fontSize = 11.sp)
                        Text("• MRT Jakarta & Bensin Shell: -Rp 70.000 (Transportasi)", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            // Category Budgets Header & Filter Chips
            Text("Pagu per Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
        }

        items(budgets) { budget ->
            val spent = when (budget.name) {
                "Belanja Bulanan" -> 1950000L
                "Makanan & Minuman" -> 2450000L
                "Transportasi" -> 850000L
                "Tagihan & Utilitas" -> 450000L
                "Hiburan & Rekreasi" -> 310000L
                else -> 1360000L
            }
            val pct = ((spent * 100) / budget.limit).toInt()
            val isWarning = pct >= budget.threshold

            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconBadge(category = budget.name)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(budget.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                            if (isWarning) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (pct >= 95) "Waspada $pct%" else "Mendekati $pct%",
                                    color = if (pct >= 95) DangerRed else WarningAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text("Pagu: ${FinTrackRepository.formatRupiah(budget.limit)} • Sisa: ${FinTrackRepository.formatRupiah((budget.limit - spent).coerceAtLeast(0L))}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Text("$pct%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isWarning) WarningAmber else SecondaryMint)
                }

                Spacer(modifier = Modifier.height(8.dp))
                FinTrackProgressBar(percent = pct, threshold = budget.threshold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
