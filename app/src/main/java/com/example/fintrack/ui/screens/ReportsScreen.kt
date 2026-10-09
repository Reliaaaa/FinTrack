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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.fintrack.ui.theme.WarningAmber

@Composable
fun ReportsScreen(
    onShowToast: (String) -> Unit
) {
    val transactions by FinTrackRepository.transactions.collectAsState()
    val selectedPeriod by FinTrackRepository.selectedPeriod.collectAsState()

    var showReportPreviewModal by remember { mutableStateOf(false) }
    var selectedPreviewPage by remember { mutableIntStateOf(1) } // 1: Ringkasan, 2: Alokasi, 3: Jurnal

    val monthlyTransactions = transactions.filter { it.date.startsWith(selectedPeriod) }
    val income = 15000000L
    val expense = 6250000L
    val netCashflow = income - expense
    val savingsRate = 58.3

    // 6 breakdown categories from HTML spec
    val categoriesBreakdown = listOf(
        Triple("Makanan & Minuman", 2450000L, 39.2),
        Triple("Belanja Bulanan", 1950000L, 31.2),
        Triple("Transportasi", 850000L, 13.6),
        Triple("Tagihan & Utilitas", 450000L, 7.2),
        Triple("Hiburan", 310000L, 5.0),
        Triple("Lainnya & Investasi", 240000L, 3.8)
    )

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
                        text = "Laporan Keuangan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                    Text(
                        text = "Analitik arus kas & evaluasi rasio 50/30/20",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            // Executive Cash Flow Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated, cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Text("ARUS KAS EKSEKUTIF", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Cash Flow Positif", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Net Tabungan Bersih", color = TextMuted, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = FinTrackRepository.formatRupiah(netCashflow),
                        style = MaterialTheme.typography.displayLarge,
                        color = TextForeground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("58.3% Rasio", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Inflow vs Outflow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyBackground)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Pemasukan", color = TextMuted, fontSize = 11.sp)
                        Text("+${FinTrackRepository.formatRupiah(income)}", color = SecondaryMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Target: 100% tercapai", color = TextMuted, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pengeluaran", color = TextMuted, fontSize = 11.sp)
                        Text("-${FinTrackRepository.formatRupiah(expense)}", color = DangerRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("-12% vs bulan lalu", color = SecondaryMint, fontSize = 10.sp)
                    }
                }
            }
        }

        item {
            // AI Financial Insight & 50/30/20 Rule
            FinTrackCard {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("AI FINANCIAL INSIGHT", color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SecondaryMint))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pengeluaran Makanan Anda turun 8% dibandingkan September. Tren pengeluaran akhir pekan masih menyumbang 42% dari total mingguan.",
                            color = TextForeground,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 50 / 30 / 20 Rule Box
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
                            Text("Evaluasi Formula 50 / 30 / 20", color = TextMuted, fontSize = 11.sp)
                            Text("Sangat Disiplin", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stacked progress bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Box(modifier = Modifier.weight(0.28f).fillMaxSize().background(PrimaryCobalt))
                            Box(modifier = Modifier.weight(0.14f).fillMaxSize().background(WarningAmber))
                            Box(modifier = Modifier.weight(0.58f).fillMaxSize().background(SecondaryMint))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Kebutuhan", color = TextMuted, fontSize = 10.sp)
                                Text("28% (≤50%)", color = PrimaryCobalt, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Keinginan", color = TextMuted, fontSize = 10.sp)
                                Text("14% (≤30%)", color = WarningAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Tabungan", color = TextMuted, fontSize = 10.sp)
                                Text("58% (≥20%)", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Breakdown Pengeluaran per Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
        }

        // Categories breakdown list
        items(categoriesBreakdown) { (catName, catAmount, catPct) ->
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconBadge(category = catName)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(catName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextForeground)
                        Text("$catPct% dari total beban", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Text(FinTrackRepository.formatRupiah(catAmount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                }
                Spacer(modifier = Modifier.height(8.dp))
                FinTrackProgressBar(percent = catPct.toInt(), threshold = 50)
            }
        }

        item {
            // Report Actions & Export Modal Trigger
            FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                Text("EKSPOR & DOKUMENTASI", color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Laporan Keuangan Resmi Siap Cetak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                Text("Unduh rekapitulasi finansial multi-halaman atau pratinjau langsung di aplikasi.", style = MaterialTheme.typography.labelSmall, color = TextMuted)

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showReportPreviewModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka Pratinjau", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onShowToast("FinTrack_Laporan_${FinTrackRepository.getCurrentMonth()}.pdf berhasil diunduh (~1.4 MB)!") },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", fontSize = 11.sp, color = TextForeground)
                    }

                    Button(
                        onClick = { onShowToast("FinTrack_Transaksi.csv siap dibuka di Excel!") },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.TableChart, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CSV", fontSize = 11.sp, color = TextForeground)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Full Multi-Page Report Preview Dialog (Hal 1: Ringkasan, Hal 2: Alokasi, Hal 3: Jurnal)
    if (showReportPreviewModal) {
        AlertDialog(
            onDismissRequest = { showReportPreviewModal = false },
            containerColor = DarkNavyCard,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("DOKUMEN RESMI", color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            val currentMonthFormatted = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date()).uppercase()
                            Text(currentMonthFormatted, color = SecondaryMint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Laporan Keuangan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextForeground)
                    }
                    IconButton(onClick = { showReportPreviewModal = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Segmented tabs (Hal 1, Hal 2, Hal 3)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyBackground)
                            .padding(2.dp)
                    ) {
                        listOf("Hal 1: Ringkasan" to 1, "Hal 2: Alokasi" to 2, "Hal 3: Jurnal" to 3).forEach { (lbl, pg) ->
                            val isSel = selectedPreviewPage == pg
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) PrimaryCobalt else Color.Transparent)
                                    .clickable { selectedPreviewPage = pg }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isSel) Color.White else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Page content
                    when (selectedPreviewPage) {
                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Ringkasan Neraca Eksekutif:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                                Text("• Pemasukan Terverifikasi: Rp 15.000.000", color = SecondaryMint, fontSize = 12.sp)
                                Text("• Total Beban Kas: Rp 6.250.000", color = DangerRed, fontSize = 12.sp)
                                Text("• Surplus Bersih: Rp 8.750.000 (58.3%)", color = SecondaryMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("• Audit Hash SHA-256: 4f98c21e... (Sah)", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        2 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Alokasi Formula 50 / 30 / 20:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                                Text("• Kebutuhan: 28% (Batas aman: ≤50%) ✓", color = PrimaryCobalt, fontSize = 12.sp)
                                Text("• Keinginan: 14% (Batas aman: ≤30%) ✓", color = WarningAmber, fontSize = 12.sp)
                                Text("• Tabungan & Investasi: 58% (Target: ≥20%) ✓", color = SecondaryMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Status Kepatuhan: Sangat Disiplin (A+)", color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Rekapitulasi Jurnal Kas (42 Transaksi):", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                                Text("• 28 Transaksi dilengkapi bukti struk OCR", color = TextMuted, fontSize = 11.sp)
                                Text("• 100% Cocok dengan mutasi rekening BCA", color = SecondaryMint, fontSize = 11.sp)
                                Text("• Lampiran bon telah dikompresi ke arsip PDF", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportPreviewModal = false
                        onShowToast("Mengunduh FinTrack_Laporan_${FinTrackRepository.getCurrentMonth()}.pdf...")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Unduh Dokumen PDF")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showReportPreviewModal = false }) {
                    Text("Tutup", color = TextMuted)
                }
            }
        )
    }
}
