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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted

@Composable
fun AccountsScreen(
    onOpenAddAccount: () -> Unit
) {
    val accounts by FinTrackRepository.accounts.collectAsState()
    var selectedFilter by remember { mutableStateOf("Semua") }
    var showInterestCalc by remember { mutableStateOf(false) }

    val totalAssets = accounts.sumOf { it.amount }
    val bankTotal = accounts.filter { it.type == "Rekening Bank" }.sumOf { it.amount }
    val ewalletTotal = accounts.filter { it.type == "E-Wallet" }.sumOf { it.amount }
    val investTotal = accounts.filter { it.type == "Investasi" }.sumOf { it.amount }

    // Passive interest yield estimation (avg 3.5% p.a. on investments & savings)
    val monthlyYield = (investTotal * 0.05 / 12).toLong()

    val filteredAccounts = accounts.filter { acc ->
        when (selectedFilter) {
            "Semua" -> true
            "Bank" -> acc.type == "Rekening Bank"
            "E-Wallet" -> acc.type == "E-Wallet"
            "Investasi" -> acc.type == "Investasi"
            else -> true
        }
    }

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
                        text = "Rekening & Portofolio",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextForeground
                    )
                    Text(
                        text = "Pusat saldo bank, dompet digital, & aset",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = onOpenAddAccount,
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
            // Hero Portfolio Banner
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Text(
                    text = "TOTAL KEKAYAAN BERSIH (NET WORTH)",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCobalt,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = FinTrackRepository.formatRupiah(totalAssets),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${accounts.size} Rekening aktif terdaftar",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyBackground)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Bank", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(FinTrackRepository.formatRupiah(bankTotal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                    }
                    Column {
                        Text("E-Wallet", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(FinTrackRepository.formatRupiah(ewalletTotal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                    }
                    Column {
                        Text("Investasi", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(FinTrackRepository.formatRupiah(investTotal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SecondaryMint)
                    }
                }
            }
        }

        item {
            // Passive Income Card
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SecondaryMint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Estimasi Kas Pasif / Bulan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextForeground
                        )
                        Text(
                            text = "Proyeksi return portofolio reksadana & tabungan",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "+${FinTrackRepository.formatRupiah(monthlyYield)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryMint
                    )
                }
            }
        }

        item {
            // Filter Pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Semua", "Bank", "E-Wallet", "Investasi")) { f ->
                    val isSel = selectedFilter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSel) PrimaryCobalt else DarkNavyCard)
                            .border(1.dp, if (isSel) PrimaryCobalt else DarkNavyBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = f }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = f,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSel) Color.White else TextForeground
                        )
                    }
                }
            }
        }

        items(filteredAccounts, key = { it.id }) { acc ->
            val icon = when (acc.type) {
                "Rekening Bank" -> Icons.Default.AccountBalance
                "E-Wallet" -> Icons.Default.AccountBalanceWallet
                else -> Icons.Default.Savings
            }

            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = acc.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextForeground
                        )
                        Text(
                            text = "${acc.type} · ${acc.accountNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = FinTrackRepository.formatRupiah(acc.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
