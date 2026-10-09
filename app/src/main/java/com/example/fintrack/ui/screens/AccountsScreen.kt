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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.fintrack.data.model.Account
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.FinTrackCard
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

@Composable
fun AccountsScreen(
    onOpenAddAccount: () -> Unit
) {
    val accounts by FinTrackRepository.accounts.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }
    var showInterestDialog by remember { mutableStateOf(false) }

    val bankAccounts = accounts.filter { it.type == "Bank" }
    val ewalletAccounts = accounts.filter { it.type == "E-Wallet" }
    val stockAccounts = accounts.filter { it.type == "Sekuritas" }

    val totalLiquidAssets = accounts.sumOf { it.amount }
    val totalBank = bankAccounts.sumOf { it.amount }
    val totalEwallet = ewalletAccounts.sumOf { it.amount }
    val totalSecurities = stockAccounts.sumOf { it.amount }

    val filteredAccounts = when (selectedFilter) {
        "bank" -> bankAccounts
        "wallet" -> ewalletAccounts
        "stock" -> stockAccounts
        else -> accounts
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
                        text = "Portofolio & Aset",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextForeground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Konsolidasi rekening, e-wallet & sekuritas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            // Section 1: Total Aset Likuid Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Total Kas Likuid Terkonsolidasi",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+3.8% bln ini", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = FinTrackRepository.formatRupiah(totalLiquidAssets),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BreakdownChip(
                        icon = Icons.Default.AccountBalance,
                        iconTint = PrimaryCobalt,
                        label = "Bank:",
                        amount = FinTrackRepository.formatRupiah(totalBank),
                        modifier = Modifier.weight(1f)
                    )
                    BreakdownChip(
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = SecondaryMint,
                        label = "E-Wallet:",
                        amount = FinTrackRepository.formatRupiah(totalEwallet),
                        modifier = Modifier.weight(1f)
                    )
                    BreakdownChip(
                        icon = Icons.Default.TrendingUp,
                        iconTint = SecondaryMint,
                        label = "Saham:",
                        amount = FinTrackRepository.formatRupiah(totalSecurities),
                        amountColor = SecondaryMint,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenAddAccount,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah Rekening", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { showInterestDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulasi", fontSize = 11.sp, color = TextForeground)
                    }

                    Button(
                        onClick = { /* Sync action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rekonsiliasi", fontSize = 11.sp, color = TextForeground)
                    }
                }
            }
        }

        item {
            // Section 2: Proyeksi Kas Pasif & Beban Finansial
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryCobalt.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.QueryStats, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Proyeksi Finansial Bulanan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                            Text("Bunga Pasif Bersih vs Biaya Administrasi", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyMuted)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("PPh 20%", color = TextMuted, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mini Analytical Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkNavyBackground)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.SouthWest, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bunga Bersih", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("+Rp 142.800", color = SecondaryMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("dari 5 instrumen aktif", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkNavyBackground)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.NorthEast, contentDescription = null, tint = DangerRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Beban Admin", color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("-Rp 42.500", color = DangerRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("3 rekening konvensional", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Net Yield Strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SecondaryMint.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        alignItems = Alignment.CenterVertically
                    ) {
                        Text("Net Imbal Pasif Bersih", style = MaterialTheme.typography.bodyMedium, color = TextForeground)
                        Text("+Rp 100.300 / bln", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SecondaryMint)
                    }
                }
            }
        }

        item {
            // Section 3: Tab Switcher / Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkNavyCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabButton(text = "Semua (${accounts.size})", isSelected = selectedFilter == "all", modifier = Modifier.weight(1f)) { selectedFilter = "all" }
                TabButton(text = "Bank (${bankAccounts.size})", isSelected = selectedFilter == "bank", modifier = Modifier.weight(1f)) { selectedFilter = "bank" }
                TabButton(text = "E-Wallet (${ewalletAccounts.size})", isSelected = selectedFilter == "wallet", modifier = Modifier.weight(1f)) { selectedFilter = "wallet" }
                TabButton(text = "Sekuritas (${stockAccounts.size})", isSelected = selectedFilter == "stock", modifier = Modifier.weight(1f)) { selectedFilter = "stock" }
            }
        }

        // Section 4: Detailed Accounts List
        items(filteredAccounts, key = { it.id }) { acc ->
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (acc.type) {
                                    "Sekuritas" -> SecondaryMint.copy(alpha = 0.2f)
                                    "E-Wallet" -> SecondaryMint.copy(alpha = 0.15f)
                                    else -> PrimaryCobalt.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (acc.type) {
                                "Sekuritas" -> Icons.Default.ShowChart
                                "E-Wallet" -> Icons.Default.AccountBalanceWallet
                                else -> Icons.Default.AccountBalance
                            },
                            contentDescription = null,
                            tint = if (acc.type == "Bank") PrimaryCobalt else SecondaryMint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = acc.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextForeground
                            )
                            if (acc.isPrimary) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Utama", tint = SecondaryMint, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(
                            text = "${acc.type} • ${acc.accountNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = FinTrackRepository.formatRupiah(acc.amount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        if (acc.type == "Sekuritas") {
                            Text("+Rp 3.650.000 (+9.5% P/L)", color = SecondaryMint, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        } else if (acc.adminFee > 0) {
                            Text("Admin: -${FinTrackRepository.formatRupiah(acc.adminFee)}/bln", color = DangerRed, fontSize = 10.sp)
                        } else {
                            Text("Bebas Biaya Admin", color = SecondaryMint, fontSize = 10.sp)
                        }
                    }
                }

                if (acc.type == "Sekuritas") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyBackground)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Kas RDN (BCA)", color = TextMuted, fontSize = 10.sp)
                            Text(FinTrackRepository.formatRupiah(acc.cashRdn), color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Portofolio Saham", color = TextMuted, fontSize = 10.sp)
                            Text(FinTrackRepository.formatRupiah(acc.stockValue), color = SecondaryMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun BreakdownChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    amount: String,
    amountColor: Color = TextForeground,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkNavyBackground)
            .padding(6.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, color = TextMuted, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(amount, color = amountColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PrimaryCobalt else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
