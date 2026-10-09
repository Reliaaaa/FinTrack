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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.fintrack.ui.theme.WarningAmber

@Composable
fun InvestmentsScreen() {
    val marketAssets by FinTrackRepository.marketAssets.collectAsState()
    var selectedTab by remember { mutableStateOf("semua") }

    // Gold denomination selection (1gr, 5gr, 10gr, 50gr, 100gr, 1000gr)
    var selectedGoldMultiplier by remember { mutableDoubleStateOf(1.0) }
    val baseGoldPrice = 1485000L
    val calculatedGoldPrice = (baseGoldPrice * selectedGoldMultiplier).toLong()

    // USD quick converter
    var usdAmountText by remember { mutableStateOf("100") }
    val usdSpotRate = 15845L
    val convertedIdr = (usdAmountText.toLongOrNull() ?: 0L) * usdSpotRate

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Live Market Status Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignItems = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SecondaryMint.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SecondaryMint)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PASAR BUKA • IHSG 7.182,45 (+0,68%)", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Live IDX & Antam", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        item {
            // Emas Murni 24K Hero Showcase
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(WarningAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Emas Murni 24K", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ANTAM / UBS", color = WarningAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Sertifikasi London Bullion Market (LBMA)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("+0,95%", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("HARGA SPOT (PER GRAM / BATANGAN)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text(
                    text = FinTrackRepository.formatRupiah(calculatedGoldPrice),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )
                Text("+Rp 14.000 hari ini • Buyback: Rp 1.334.000/gr (Spread wajar 10.1%)", color = SecondaryMint, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(14.dp))

                // Denomination Switcher (1gr, 5gr, 10gr, 50gr, 100gr, 1kg)
                Text("PILIH BOBOT BATANGAN:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyBackground)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "1 gr" to 1.0,
                        "5 gr" to 5.0,
                        "10 gr" to 10.0,
                        "50 gr" to 50.0,
                        "100 gr" to 100.0,
                        "1 kg" to 1000.0
                    ).forEach { (label, mult) ->
                        val isSelected = selectedGoldMultiplier == mult
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimaryCobalt else Color.Transparent)
                                .clickable { selectedGoldMultiplier = mult }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        item {
            // USD / IDR Spotlight Currency Card
            FinTrackCard {
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
                                .background(PrimaryCobalt.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$", color = PrimaryCobalt, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("USD / IDR Spot Realtime", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                            Text("Dolar Amerika terhadap Rupiah", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                    Text("-0,16% (Rupiah Menguat)", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Rp 15.845,00", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = TextForeground)

                Spacer(modifier = Modifier.height(10.dp))

                // Rates grid (Bid, JISDOR, Offer)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyBackground)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Kurs Beli (Bid)", color = TextMuted, fontSize = 10.sp)
                        Text("Rp 15.780", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("JISDOR BI", color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Rp 15.838", color = PrimaryCobalt, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Kurs Jual (Offer)", color = TextMuted, fontSize = 10.sp)
                        Text("Rp 15.910", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Currency Calculator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = usdAmountText,
                        onValueChange = { usdAmountText = it.filter { c -> c.isDigit() } },
                        label = { Text("USD ($)", color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = SecondaryMint,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyBackground)
                            .border(1.dp, DarkNavyBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("IDR (Rupiah)", color = TextMuted, fontSize = 10.sp)
                            Text(FinTrackRepository.formatRupiah(convertedIdr), color = SecondaryMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // BBCA Live Candlestick & Technicals Showcase
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("BBCA", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextForeground)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("IDX • KOMPAS100", color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("PT Bank Central Asia Tbk", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Rp 10.150", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SecondaryMint)
                        Text("+175 (+1,75%)", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Bar (Open, High, Low, Vol)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavyBackground)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Open", color = TextMuted, fontSize = 10.sp)
                        Text("9.975", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("High", color = TextMuted, fontSize = 10.sp)
                        Text("10.200", color = SecondaryMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Low", color = TextMuted, fontSize = 10.sp)
                        Text("9.950", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Vol (Lot)", color = TextMuted, fontSize = 10.sp)
                        Text("84,2M", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Technical Indicators Box (RSI, MACD, Net Foreign)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(DarkNavyBackground).padding(8.dp)) {
                        Column {
                            Text("RSI (14)", color = TextMuted, fontSize = 10.sp)
                            Text("58,4", color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Beli Akumulatif", color = SecondaryMint, fontSize = 9.sp)
                        }
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(DarkNavyBackground).padding(8.dp)) {
                        Column {
                            Text("MACD Trend", color = TextMuted, fontSize = 10.sp)
                            Text("+42,1", color = SecondaryMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Golden Cross", color = SecondaryMint, fontSize = 9.sp)
                        }
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(DarkNavyBackground).padding(8.dp)) {
                        Column {
                            Text("Net Foreign", color = TextMuted, fontSize = 10.sp)
                            Text("+214,8 M", color = SecondaryMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Inflow Masif", color = SecondaryMint, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        item {
            // Other Markets Watchlist
            Text("Mata Uang & Saham Lainnya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
        }

        items(marketAssets.filter { it.symbol != "ANTAM" && it.symbol != "USD/IDR" && it.symbol != "BBCA" }) { asset ->
            val isPos = asset.changePercent >= 0
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = asset.symbol.take(3),
                            color = PrimaryCobalt,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(asset.symbol, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        Text(asset.name, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(FinTrackRepository.formatRupiah(asset.price), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                        Text(
                            "${if (isPos) "+" else ""}${asset.changePercent}%",
                            color = if (isPos) SecondaryMint else DangerRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
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
