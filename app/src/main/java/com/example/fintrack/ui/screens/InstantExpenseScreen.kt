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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.data.model.Transaction
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.components.FinTrackCard
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted
import com.example.fintrack.ui.theme.WarningAmber

data class QuickExpensePreset(
    val name: String,
    val category: String,
    val amount: Long,
    val account: String,
    val icon: ImageVector
)

@Composable
fun InstantExpenseScreen(
    onShowToast: (String) -> Unit
) {
    val presets = listOf(
        QuickExpensePreset("Kopi Janji Jiwa / Kenangan", "Makanan & Minuman", 25000L, "GoPay", Icons.Default.LocalCafe),
        QuickExpensePreset("Makan Siang Nasi Padang", "Makanan & Minuman", 45000L, "BCA", Icons.Default.LunchDining),
        QuickExpensePreset("Isi Bensin Pertamax", "Transportasi", 35000L, "GoPay", Icons.Default.LocalGasStation),
        QuickExpensePreset("Parkir Mall / Gedung", "Transportasi", 5000L, "GoPay", Icons.Default.LocalParking),
        QuickExpensePreset("Ongkos GoRide / Grab", "Transportasi", 18000L, "GoPay", Icons.Default.TwoWheeler),
        QuickExpensePreset("Air Mineral & Camilan", "Makanan & Minuman", 12000L, "GoPay", Icons.Default.WaterDrop)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "Transaksi Instan",
            style = MaterialTheme.typography.headlineMedium,
            color = TextForeground,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Catat pengeluaran harian hanya dengan 1 sentuhan",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        FinTrackCard(backgroundColor = DarkNavyCardElevated) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Ketuk salah satu tombol di bawah untuk langsung mencatat transaksi ke rekening Anda tanpa perlu mengetik.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextForeground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(presets.size) { index ->
                val preset = presets[index]
                FinTrackCard(
                    onClick = {
                        val newTx = Transaction(
                            id = System.currentTimeMillis(),
                            name = preset.name,
                            category = preset.category,
                            amount = -preset.amount,
                            date = FinTrackRepository.getTodayDate(),
                            account = preset.account,
                            note = "Dicatat via Transaksi Instan"
                        )
                        FinTrackRepository.addTransaction(newTx)
                        onShowToast("Berhasil mencatat ${preset.name} (${FinTrackRepository.formatRupiah(preset.amount)})!")
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryCobalt.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = preset.icon, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextForeground
                            )
                            Text(
                                text = "${preset.category} · ${preset.account}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(WarningAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "-${FinTrackRepository.formatRupiah(preset.amount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
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
}
