package com.example.fintrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.fintrack.ui.theme.DarkNavyMuted
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.SuccessGreen
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted

@Composable
fun ReportsScreen(
    onShowToast: (String) -> Unit
) {
    val transactions by FinTrackRepository.transactions.collectAsState()
    val selectedPeriod by FinTrackRepository.selectedPeriod.collectAsState()

    val monthlyTransactions = transactions.filter { it.date.startsWith(selectedPeriod) }
    val income = monthlyTransactions.filter { it.amount > 0 }.sumOf { it.amount }
    val expense = monthlyTransactions.filter { it.amount < 0 }.sumOf { -it.amount }
    val netCashflow = income - expense
    val savingsRate = if (income > 0) ((netCashflow.coerceAtLeast(0L) * 100) / income).toInt() else 0

    // Group expenses by category
    val expensesByCategory = monthlyTransactions
        .filter { it.amount < 0 }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { -it.amount } }
        .toList()
        .sortedByDescending { it.second }

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
                        text = "Laporan & Analitik",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextForeground
                    )
                    Text(
                        text = "Evaluasi arus kas periode $selectedPeriod",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            // Net Cashflow Hero
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Text(
                    text = "ARUS KAS BERSIH (NET CASHFLOW)",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCobalt,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = FinTrackRepository.formatRupiah(netCashflow),
                    style = MaterialTheme.typography.displayLarge,
                    color = if (netCashflow >= 0) SuccessGreen else DangerRed,
                    fontWeight = FontWeight.Bold
                )
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
                        Text("Pemasukan", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("+${FinTrackRepository.formatRupiah(income)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Column {
                        Text("Pengeluaran", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("-${FinTrackRepository.formatRupiah(expense)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = DangerRed)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Rasio Tabungan", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("$savingsRate%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = SecondaryMint)
                    }
                }
            }
        }

        item {
            // Financial Health Score
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SecondaryMint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Skor Kesehatan Finansial: 92/100",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        Text(
                            text = "Status: Sangat Sehat (Rasio tabungan > 30% dan pagu terkendali)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        item {
            // Category Breakdown Title
            Text(
                text = "Komposisi Pengeluaran per Kategori",
                style = MaterialTheme.typography.titleMedium,
                color = TextForeground
            )
        }

        items(expensesByCategory) { (category, amount) ->
            val percent = if (expense > 0) ((amount * 100) / expense).toInt() else 0

            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconBadge(category = category)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextForeground
                        )
                        Text(
                            text = "$percent% dari total pengeluaran",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = FinTrackRepository.formatRupiah(amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FinTrackProgressBar(percent = percent, threshold = 40)
            }
        }

        item {
            // Export Action Buttons
            Text(
                text = "Ekspor Laporan Keuangan",
                style = MaterialTheme.typography.titleMedium,
                color = TextForeground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onShowToast("Laporan CSV FinTrack berhasil diunduh!") },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ekspor CSV")
                }

                Button(
                    onClick = { onShowToast("Laporan PDF FinTrack siap dicetak!") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ekspor PDF", color = TextForeground)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
