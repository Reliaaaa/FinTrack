package id.fintrack.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.fintrack.app.ui.FinTrackCard

private data class MoreItem(val route: String, val title: String, val subtitle: String)
private data class MoreGroup(val title: String, val items: List<MoreItem>)
private val groups = listOf(
    MoreGroup("Rencana keuangan", listOf(
        MoreItem("obl", "Tagihan dan cicilan", "Jadwal, pembayaran, dan sisa cicilan"),
        MoreItem("goals", "Target tabungan", "Pantau progres dan kebutuhan setoran"),
        MoreItem("forecast", "Proyeksi kas 90 hari", "Simulasikan belanja sebelum mengambil keputusan")
    )),
    MoreGroup("Catatan dan laporan", listOf(
        MoreItem("report", "Laporan bulanan", "Ringkasan pemasukan dan pengeluaran"),
        MoreItem("import", "Impor mutasi CSV", "Periksa transaksi sebelum menyimpan"),
        MoreItem("transfer", "Catat transfer", "Pindahkan catatan saldo antar rekening; bukan kirim uang")
    )),
    MoreGroup("Kalkulator", listOf(
        MoreItem("split", "Bagi tagihan", "Hitung pembagian, pajak, dan layanan"),
        MoreItem("seasonal", "Dana musiman", "Rencanakan THR, liburan, dan kebutuhan tahunan"),
        MoreItem("zakat", "Zakat maal", "Estimasi dari kekayaan bersih dan harga emas")
    )),
    MoreGroup("Preferensi aplikasi", listOf(
        MoreItem("settings", "Pengaturan", "Gajian, notifikasi, ekspor, dan pengelolaan data")
    ))
)

@Composable
fun MoreScreen(onOpen: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Lainnya", style=MaterialTheme.typography.headlineMedium, fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Rencanakan, tinjau, dan kelola catatan keuanganmu.",
                style=MaterialTheme.typography.bodyMedium, color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        groups.forEach { group ->
            item(key=group.title) {
                Text(group.title, modifier=Modifier.padding(top=12.dp),
                    style=MaterialTheme.typography.titleSmall, color=MaterialTheme.colorScheme.secondary)
            }
            items(group.items, key={it.route}) { item ->
                FinTrackCard(Modifier.clickable(role=Role.Button, onClickLabel="Buka ${item.title}") { onOpen(item.route) }) {
                    Row(Modifier.fillMaxWidth().heightIn(min=48.dp), verticalAlignment=Alignment.CenterVertically,
                        horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(4.dp)) {
                            Text(item.title, style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.SemiBold)
                            Text(item.subtitle, style=MaterialTheme.typography.bodyMedium, color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Outlined.ChevronRight, contentDescription=null, tint=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
