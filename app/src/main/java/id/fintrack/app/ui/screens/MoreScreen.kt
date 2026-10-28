package id.fintrack.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.fintrack.app.ui.FinTrackCard

private data class MoreItem(val route: String, val title: String, val subtitle: String)

private val items = listOf(
    MoreItem("import", "Impor mutasi CSV", "Tinjau transaksi dan cegah impor ganda"),
    MoreItem("forecast", "Proyeksi kas 90 hari", "Cek dampak belanja dan tanggal saldo menipis"),
    MoreItem("goals", "Target tabungan impian", "Progres target dan kebutuhan setoran bulanan"),
    MoreItem("transfer", "Transfer antar rekening", "Pencatatan perpindahan dana internal"),
    MoreItem("obl", "Tagihan dan cicilan", "Jadwal jatuh tempo serta sisa pembayaran"),
    MoreItem("report", "Laporan bulanan", "Pemasukan, pengeluaran, dan kategori terbesar"),
    MoreItem("split", "Bagi tagihan", "Bagi makan bersama dengan pajak dan layanan proporsional"),
    MoreItem("zakat", "Zakat maal", "Hitung dari kekayaan bersih dan harga emas"),
    MoreItem("seasonal", "Dana musiman", "Setoran bulanan untuk THR, Lebaran, atau liburan"),
    MoreItem("settings", "Pengaturan", "Gajian, notifikasi, ekspor, dan hapus data")
)

@Composable
fun MoreScreen(onOpen: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("Lainnya", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        items(items, key = { it.route }) { item ->
            FinTrackCard(Modifier.clickable { onOpen(item.route) }) {
                Column(Modifier.fillMaxWidth().padding(0.dp)) {
                    Text(item.title, fontWeight = FontWeight.SemiBold)
                    Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
