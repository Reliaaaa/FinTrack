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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.fintrack.data.model.ReceiptItem
import com.example.fintrack.data.model.Transaction
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
import com.example.fintrack.ui.theme.WarningAmber

@Composable
fun ReceiptScannerScreen(
    onShowToast: (String) -> Unit
) {
    val receiptItems by FinTrackRepository.receiptItems.collectAsState()
    val targetTotal = 680000L

    var showManualAddCard by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("Telur Ayam Omega 3 (10 btr)") }
    var newItemCategory by remember { mutableStateOf("Kebutuhan Pokok") }
    var newItemPriceText by remember { mutableStateOf("35000") }
    var newItemQty by remember { mutableIntStateOf(1) }

    val currentItemsTotal = receiptItems.sumOf { it.unitPrice * it.quantity }
    val diff = currentItemsTotal - targetTotal
    val pph11Tax = (currentItemsTotal * 0.11).toLong()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SecondaryMint.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("MODE KOREKSI OCR", color = SecondaryMint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Koreksi Item Struk OCR",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextForeground
                    )
                    Text(
                        text = "#TX-8921 • Grand Lucky SCBD • ${FinTrackRepository.getDateOffsetFormatted(0)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            // Target OCR vs Hasil Koreksi Metric Card
            FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TARGET NOTA OCR", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(FinTrackRepository.formatRupiah(targetTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextForeground)
                        Text("Sesuai Nota Fisik", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("HASIL KOREKSI (${receiptItems.size} ITEM)", style = MaterialTheme.typography.labelSmall, color = PrimaryCobalt)
                        Text(FinTrackRepository.formatRupiah(currentItemsTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (diff == 0L) SecondaryMint else PrimaryCobalt)
                        Text(
                            if (diff == 0L) "Cocok 100%" else "+${FinTrackRepository.formatRupiah(diff)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (diff == 0L) SecondaryMint else WarningAmber
                        )
                    }
                }

                if (diff != 0L) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarningAmber.copy(alpha = 0.15f))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Selisih: +${FinTrackRepository.formatRupiah(diff)}. Total melebihi hasil pindai OCR awal.",
                                color = WarningAmber,
                                fontSize = 11.sp
                            )
                        }
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
                    text = "Rincian Item Terpindai (${receiptItems.size} Item)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextForeground
                )
            }
        }

        // Receipt items list
        items(receiptItems, key = { it.id }) { item ->
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextForeground
                        )
                        Text(
                            text = "${item.category} • @ ${FinTrackRepository.formatRupiah(item.unitPrice)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    IconButton(
                        onClick = { FinTrackRepository.removeReceiptItem(item.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = DangerRed, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    alignItems = Alignment.CenterVertically
                ) {
                    // Stepper (- qty +)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyBackground)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { FinTrackRepository.updateReceiptItemQty(item.id, -1) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = TextForeground, modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = "${item.quantity}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { FinTrackRepository.updateReceiptItemQty(item.id, 1) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = TextForeground, modifier = Modifier.size(14.dp))
                        }
                    }

                    Text(
                        text = FinTrackRepository.formatRupiah(item.unitPrice * item.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryMint
                    )
                }
            }
        }

        item {
            // Button to toggle manual addition card
            Button(
                onClick = { showManualAddCard = !showManualAddCard },
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah Item Baru Manual", color = PrimaryCobalt, fontWeight = FontWeight.Bold)
            }
        }

        if (showManualAddCard) {
            item {
                FinTrackCard(backgroundColor = DarkNavyCardElevated) {
                    Text("TAMBAH ITEM MANUAL", color = PrimaryCobalt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Nama Item Belanja", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newItemPriceText,
                        onValueChange = { newItemPriceText = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga Satuan (Rp)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val price = newItemPriceText.toLongOrNull() ?: 0L
                            if (newItemName.isNotBlank() && price > 0L) {
                                FinTrackRepository.addReceiptItem(
                                    ReceiptItem(
                                        id = System.currentTimeMillis().toString(),
                                        name = newItemName,
                                        category = newItemCategory,
                                        unitPrice = price,
                                        quantity = newItemQty
                                    )
                                )
                                showManualAddCard = false
                                onShowToast("Item '${newItemName}' dimasukkan ke struk!")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Masukkan ke Struk")
                    }
                }
            }
        }

        item {
            // Calculation Summary Card
            FinTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal Item Terverifikasi", color = TextMuted, fontSize = 12.sp)
                    Text(FinTrackRepository.formatRupiah(currentItemsTotal), color = TextForeground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("PPN 11% (Termasuk di harga)", color = TextMuted, fontSize = 12.sp)
                    Text(FinTrackRepository.formatRupiah(pph11Tax), color = TextMuted, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DarkNavyBorder)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Transaksi Akhir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextForeground)
                    Text(FinTrackRepository.formatRupiah(currentItemsTotal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SecondaryMint)
                }
            }
        }

        item {
            // Bottom Action CTAs
            Button(
                onClick = {
                    val newTx = Transaction(
                        id = System.currentTimeMillis(),
                        name = "Grand Lucky SCBD (OCR)",
                        category = "Makanan & Minuman",
                        amount = -currentItemsTotal,
                        date = FinTrackRepository.getTodayDate(),
                        account = "BCA Utama (•••• 4821)",
                        note = "${receiptItems.size} item struk tervalidasi OCR",
                        receiptImageAttached = true,
                        merchantLocation = "Grand Lucky SCBD"
                    )
                    FinTrackRepository.addTransaction(newTx)
                    onShowToast("Koreksi berhasil disimpan & laporan diperbarui!")
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Koreksi & Update Laporan (${FinTrackRepository.formatRupiah(currentItemsTotal)})", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    FinTrackRepository.resetReceiptItems()
                    onShowToast("Item direset ke ekstraksi AI awal.")
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyMuted),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset ke Hasil Ekstraksi AI Awal", color = TextForeground)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
