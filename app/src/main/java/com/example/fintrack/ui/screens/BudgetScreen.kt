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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.fintrack.data.model.BudgetConfig
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

    val totalBudgetLimit = budgets.sumOf { it.limit }
    val totalSpent = budgets.sumOf { budget ->
        transactions
            .filter { it.amount < 0 && it.category == budget.name && it.date.startsWith(selectedPeriod) }
            .sumOf { -it.amount }
    }
    val totalRemaining = (totalBudgetLimit - totalSpent).coerceAtLeast(0L)
    val overallPercent = if (totalBudgetLimit > 0) ((totalSpent * 100) / totalBudgetLimit).toInt() else 0

    // Remaining days in month
    val daysRemaining = 8
    val safeDailySpend = if (daysRemaining > 0 && totalRemaining > 0) totalRemaining / daysRemaining else 0L

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Screen Header & Period Navigator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignItems = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Manajemen Anggaran",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextForeground
                    )
                    Text(
                        text = "Kontrol pagu pengeluaran bulanan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyCard)
                        .border(1.dp, DarkNavyBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = selectedPeriod,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryCobalt,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            // Summary Budget Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                Text(
                    text = "TOTAL REALISASI PAGU BULAN INI",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.Bottom
                ) {
                    Text(
                        text = FinTrackRepository.formatRupiah(totalSpent),
                        style = MaterialTheme.typography.displayLarge,
                        color = TextForeground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "dari ${FinTrackRepository.formatRupiah(totalBudgetLimit)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                FinTrackProgressBar(percent = overallPercent, threshold = 80)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyBackground)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Sisa Pagu", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = FinTrackRepository.formatRupiah(totalRemaining),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryMint
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pagu Aman Harian", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = "${FinTrackRepository.formatRupiah(safeDailySpend)}/hari",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCobalt
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignItems = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Pagu per Kategori",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextForeground
                )
                Button(
                    onClick = onOpenAddBudget,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah", fontSize = 12.sp)
                }
            }
        }

        items(budgets) { budget ->
            val spent = transactions
                .filter { it.amount < 0 && it.category == budget.name && it.date.startsWith(selectedPeriod) }
                .sumOf { -it.amount }
            val percent = if (budget.limit > 0) ((spent * 100) / budget.limit).toInt() else 0
            val isWarning = percent >= budget.threshold

            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconBadge(category = budget.name)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = budget.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextForeground
                            )
                            if (isWarning) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Peringatan",
                                    tint = WarningAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = if (budget.recurring) "Berulang otomatis setiap bulan" else "Sekali pakai",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isWarning) WarningAmber else SecondaryMint
                        )
                        Text(
                            text = "Batas: ${budget.threshold}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                FinTrackProgressBar(percent = percent, threshold = budget.threshold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Terpakai: ${FinTrackRepository.formatRupiah(spent)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextForeground
                    )
                    Text(
                        text = "Limit: ${FinTrackRepository.formatRupiah(budget.limit)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
