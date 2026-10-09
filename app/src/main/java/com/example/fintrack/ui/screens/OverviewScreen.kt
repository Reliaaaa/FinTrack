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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.CategoryIconBadge
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.components.FinTrackProgressBar
import com.example.fintrack.ui.theme.DangerRed
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.DarkNavyMuted
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.SuccessGreen
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted
import com.example.fintrack.ui.theme.WarningAmber

@Composable
fun OverviewScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToInstant: () -> Unit,
    onOpenAddTransaction: () -> Unit
) {
    val userProfile by FinTrackRepository.userProfile.collectAsState()
    val transactions by FinTrackRepository.transactions.collectAsState()
    val accounts by FinTrackRepository.accounts.collectAsState()
    val budgets by FinTrackRepository.budgets.collectAsState()
    val hideBalance by FinTrackRepository.hideBalance.collectAsState()
    val selectedPeriod by FinTrackRepository.selectedPeriod.collectAsState()

    // Calculate totals
    val totalAccountsBalance = accounts.sumOf { it.amount }
    val monthlyTransactions = transactions.filter { it.date.startsWith(selectedPeriod) }
    val totalIncome = monthlyTransactions.filter { it.amount > 0 }.sumOf { it.amount }
    val totalExpense = monthlyTransactions.filter { it.amount < 0 }.sumOf { -it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header Greeting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignItems = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Halo, ${userProfile.nickname} 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextForeground
                    )
                    Text(
                        text = "Kelola arus kas & aset cerdas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyMuted)
                        .border(1.dp, DarkNavyBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Keamanan Biometrik",
                        tint = PrimaryCobalt,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        item {
            // Balance Card
            FinTrackCard(
                backgroundColor = DarkNavyCardElevated,
                cornerRadius = 20.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL SALDO TERSEDIA",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = { FinTrackRepository.toggleHideBalance() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (hideBalance) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Sembunyikan Saldo",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (hideBalance) "Rp ••••••••••" else FinTrackRepository.formatRupiah(totalAccountsBalance),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Income / Expense summary pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyBackground)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Income
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Pemasukan",
                                tint = SuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Pemasukan", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = if (hideBalance) "Rp •••" else "+${FinTrackRepository.formatRupiah(totalIncome)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    // Expense
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DangerRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Pengeluaran",
                                tint = DangerRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Pengeluaran", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = if (hideBalance) "Rp •••" else "-${FinTrackRepository.formatRupiah(totalExpense)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                        }
                    }
                }
            }
        }

        item {
            // Quick Action Buttons Grid
            Text(
                text = "Aksi Cepat",
                style = MaterialTheme.typography.titleMedium,
                color = TextForeground,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionItem(
                    title = "Catat Baru",
                    icon = Icons.Default.Add,
                    iconColor = PrimaryCobalt,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAddTransaction
                )
                QuickActionItem(
                    title = "Anggaran",
                    icon = Icons.Default.PieChart,
                    iconColor = SecondaryMint,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBudgets
                )
                QuickActionItem(
                    title = "Laporan",
                    icon = Icons.Default.BarChart,
                    iconColor = PrimaryCobalt,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToReports
                )
                QuickActionItem(
                    title = "Instan",
                    icon = Icons.Default.ElectricBolt,
                    iconColor = WarningAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToInstant
                )
            }
        }

        item {
            // 7-day spending trends chart preview
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tren Pengeluaran 7 Hari",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextForeground
                    )
                    Text(
                        text = "Harian",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCobalt,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bar chart simulation
                val days = listOf("Sen" to 450000L, "Sel" to 1200000L, "Rab" to 300000L, "Kam" to 850000L, "Jum" to 950000L, "Sab" to 1500000L, "Min" to 980000L)
                val maxSpending = days.maxOf { it.second }.toFloat()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.Bottom
                ) {
                    days.forEach { (day, amount) ->
                        val heightFraction = (amount / maxSpending).coerceIn(0.1f, 1f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height((90 * heightFraction).dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (day == "Sab") PrimaryCobalt else SecondaryMint
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            // Active Budgets
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Anggaran Berjalan ($selectedPeriod)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextForeground
                    )
                    Text(
                        text = "Kelola",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryMint,
                        modifier = Modifier
                            .clickable { onNavigateToBudgets() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                budgets.take(3).forEach { budget ->
                    val used = transactions
                        .filter { it.amount < 0 && it.category == budget.name && it.date.startsWith(selectedPeriod) }
                        .sumOf { -it.amount }
                    val percent = if (budget.limit > 0) ((used * 100) / budget.limit).toInt() else 0

                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = budget.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextForeground
                            )
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (percent >= budget.threshold) WarningAmber else TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        FinTrackProgressBar(percent = percent, threshold = budget.threshold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = FinTrackRepository.formatRupiah(used),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextForeground
                            )
                            Text(
                                text = "dari ${FinTrackRepository.formatRupiah(budget.limit)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        item {
            // Recent Transactions
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaksi Terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextForeground
                    )
                    Text(
                        text = "Lihat Semua",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCobalt,
                        modifier = Modifier
                            .clickable { onNavigateToTransactions() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                transactions.take(4).forEach { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryIconBadge(category = tx.category)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextForeground
                            )
                            Text(
                                text = "${tx.account} · ${tx.date}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = if (tx.amount > 0) "+${FinTrackRepository.formatRupiah(tx.amount)}" else "-${FinTrackRepository.formatRupiah(-tx.amount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (tx.amount > 0) SuccessGreen else DangerRed
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavyCard)
            .border(1.dp, DarkNavyBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextForeground,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
