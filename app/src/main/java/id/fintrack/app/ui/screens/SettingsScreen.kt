package id.fintrack.app.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.ui.AmountField
import id.fintrack.app.ui.FinTrackCard
import id.fintrack.app.ui.IntField
import id.fintrack.app.ui.LabeledValue
import id.fintrack.app.ui.ScreenScaffold
import id.fintrack.app.ui.SectionTitle
import id.fintrack.app.vm.MainViewModel
import java.time.LocalDate

private fun listenerEnabled(context: Context): Boolean =
    NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)

@Composable
fun SettingsScreen(vm: MainViewModel, onBack: () -> Unit) {
    val s by vm.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var day by remember(s.incomeDay) { mutableStateOf(s.incomeDay.toString()) }
    var income by remember(s.monthlyIncome) { mutableStateOf(if (s.monthlyIncome > 0) s.monthlyIncome.toString() else "") }
    var access by remember { mutableStateOf(listenerEnabled(context)) }
    var confirmWipe by remember { mutableStateOf(false) }

    // Pengguna kembali dari layar akses notifikasi sistem. Baca ulang statusnya.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { access = listenerEnabled(context) }

    val exporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) vm.exportTo(uri)
    }

    val dayValue = day.toIntOrNull()
    val dayValid = dayValue != null && dayValue in 1..31

    ScreenScaffold("Pengaturan", onBack) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle("Gajian")
            IntField("Tanggal gajian (1 sampai 31)", day) { day = it }
            AmountField("Pemasukan bulanan", income) { income = it }
            Button(
                onClick = { vm.saveSettings(dayValue ?: s.incomeDay, income.toLongOrNull() ?: 0L) },
                enabled = dayValid
            ) { Text("Simpan") }
            if (!dayValid && day.isNotEmpty()) {
                Text("Tanggal hfintrack antara 1 dan 31.", color = MaterialTheme.colorScheme.error)
            }

            SectionTitle("Notifikasi bank dan dompet")
            FinTrackCard {
                Text(
                    "FinTrack membaca notifikasi dari aplikasi bank dan dompet digital yang dikenal, lalu membuat draf transaksi. " +
                        "Teks notifikasi tidak disimpan. Yang disimpan hanya nominal, jenis, dan nama tujuan. " +
                        "Tiap draf menunggu konfirmasi Anda. Pengenalan format masih eksperimental dan bisa melewatkan atau menggandakan transaksi."
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tangkap notifikasi", fontWeight = FontWeight.SemiBold)
                    Switch(checked = s.notifCapture, onCheckedChange = { vm.setNotifCapture(it) })
                }
                LabeledValue("Akses notifikasi sistem", if (access) "Aktif" else "Belum diberikan")
                if (!access) {
                    Text(
                        "Berikan akses di pengaturan sistem agar fitur ini bekerja.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }) { Text("Buka pengaturan akses") }
            }

            SectionTitle("Data")
            OutlinedButton(onClick = { exporter.launch("fintrack-transaksi-${LocalDate.now()}.csv") }) {
                Text("Ekspor transaksi (CSV)")
            }
            OutlinedButton(
                onClick = { confirmWipe = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Hapus semua data") }
            Text(
                "Data keuangan tersimpan lokal di perangkat. Permintaan harga pasar dikirim ke Indodax. Database belum dienkripsi secara khusus oleh aplikasi.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SectionTitle("Tentang")
            FinTrackCard {
                LabeledValue("Versi", "0.1.0")
                Text("Harga kripto: Indodax.", style = MaterialTheme.typography.bodySmall)
                Text(
                    "TradingView Lightweight Charts™. Copyright (c) 2026 TradingView, Inc. Lisensi Apache-2.0.",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { uriHandler.openUri("https://www.tradingview.com/") }) { Text("tradingview.com") }
            }
        }
    }

    if (confirmWipe) {
        AlertDialog(
            onDismissRequest = { confirmWipe = false },
            title = { Text("Hapus semua data?") },
            text = { Text("Rekening, transaksi, kewajiban, dan aset dihapus dari perangkat ini. Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.wipeAll()
                    confirmWipe = false
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmWipe = false }) { Text("Batal") } }
        )
    }
}
