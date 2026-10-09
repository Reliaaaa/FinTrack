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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.fintrack.data.model.Transaction
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.SuccessGreen
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted

@Composable
fun ReceiptScannerScreen(
    onShowToast: (String) -> Unit
) {
    var scanned by remember { mutableStateOf(true) }

    val receiptItems = listOf(
        "Iced Caramel Macchiato" to 65000L,
        "Almond Croissant" to 38000L,
        "Cheese Bagel with Cream" to 32000L
    )
    val subtotal = receiptItems.sumOf { it.second }
    val tax = (subtotal * 0.11).toLong()
    val total = subtotal + tax

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Text(
                text = "Struk & Koreksi OCR",
                style = MaterialTheme.typography.headlineMedium,
                color = TextForeground,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Pindai struk fisik & ekstrak rincian belanja otomatis",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        item {
            // Scanner Action Banner
            FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryCobalt.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OCR Receipt Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        Text(
                            text = "Koreksi item per baris sebelum disimpan ke jurnal",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        item {
            // Parsed Receipt Details Card
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Starbucks Reserve Indonesia",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                        Text(
                            text = "Cabang Grand Indonesia · ${FinTrackRepository.getDateOffsetFormatted(0)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SuccessGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("OCR Akurat (99%)", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item rows
                receiptItems.forEach { (item, price) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item, style = MaterialTheme.typography.bodyMedium, color = TextForeground)
                        Text(text = FinTrackRepository.formatRupiah(price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextForeground)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DarkNavyBorder)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "PPN Restoran (11%)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(text = FinTrackRepository.formatRupiah(tax), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Akhir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                    Text(text = FinTrackRepository.formatRupiah(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SecondaryMint)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val newTx = Transaction(
                            id = System.currentTimeMillis(),
                            name = "Starbucks Grand Indonesia (OCR)",
                            category = "Makanan & Minuman",
                            amount = -total,
                            date = FinTrackRepository.getTodayDate(),
                            account = "BCA",
                            note = "3 item diimpor via Receipt Studio"
                        )
                        FinTrackRepository.addTransaction(newTx)
                        onShowToast("Struk berhasil dicatat ke transaksi FinTrack!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan ke Transaksi FinTrack", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
