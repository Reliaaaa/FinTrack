@file:OptIn(ExperimentalMaterial3Api::class)

package id.fintrack.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.domain.Money
import id.fintrack.app.domain.ObligationKind
import id.fintrack.app.domain.ObligationMath
import id.fintrack.app.ui.AmountField
import id.fintrack.app.ui.FinTrackCard
import id.fintrack.app.ui.DropdownField
import id.fintrack.app.ui.EmptyNote
import id.fintrack.app.ui.FormDialog
import id.fintrack.app.ui.IntField
import id.fintrack.app.ui.LabeledValue
import id.fintrack.app.ui.TextInput
import id.fintrack.app.ui.fmtDate
import id.fintrack.app.vm.MainViewModel
import java.util.Locale

@Composable
fun ObligationsScreen(vm: MainViewModel) {
    val state by vm.obligations.collectAsStateWithLifecycle()
    var markId by remember { mutableStateOf<Long?>(null) }
    var deleteId by remember { mutableStateOf<Long?>(null) }
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah kewajiban")
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Text("Kewajiban", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
            item {
                FinTrackCard {
                    LabeledValue("Beban per bulan", Money.format(state.monthlyLoad), emphasize = true)
                    val ratio = state.ratio
                    if (ratio == null) {
                        Text("Isi pemasukan bulanan di Pengaturan untuk melihat rasio beban.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        val high = ratio > ObligationMath.LOAD_RATIO_WARNING
                        Text(
                            String.format(Locale("id", "ID"), "%.1f", ratio) + " persen dari pemasukan",
                            color = if (high) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (high) Text("Di atas patokan 30 persen.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (state.rows.isEmpty()) {
                item { EmptyNote("Belum ada kewajiban. Catat cicilan, paylater, arisan, tagihan rutin, dan zakat.") }
            } else {
                items(state.rows, key = { it.entity.id }) { row ->
                    val o = row.entity
                    val kind = ObligationKind.fromName(o.kind)
                    FinTrackCard {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(0.dp)) {
                            Text(o.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { deleteId=o.id }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Hapus kewajiban")
                            }
                        }
                        LabeledValue(kind.label, Money.format(o.amount))
                        LabeledValue("Jatuh tempo berikutnya", fmtDate(row.nextDue))
                        if (o.remaining >= 0) LabeledValue("Sisa pembayaran", "${o.remaining} kali")
                        if (row.paidThisMonth) {
                            Text("Lunas bulan ini", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        } else if (o.remaining != 0) {
                            TextButton(onClick = { markId=o.id }) { Text("Tandai lunas bulan ini") }
                        } else {
                            Text("Selesai", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    markId?.let { id -> AlertDialog(onDismissRequest={markId=null},title={Text("Sudah mencatat pembayaran?")},text={Text("Tandai lunas hanya memperbarui status tagihan. Catat pengeluarannya di Transaksi terlebih dahulu agar saldo rekening tetap benar.")},confirmButton={TextButton(onClick={vm.markPaid(id);markId=null}){Text("Sudah, tandai lunas")}},dismissButton={TextButton(onClick={markId=null}){Text("Batal")}}) }
    deleteId?.let { id -> AlertDialog(onDismissRequest={deleteId=null},title={Text("Hapus jadwal tagihan?")},text={Text("Tagihan tidak lagi dicadangkan dalam proyeksi. Riwayat transaksi tetap tersimpan.")},confirmButton={TextButton(onClick={vm.deleteObligation(id);deleteId=null}){Text("Hapus")}},dismissButton={TextButton(onClick={deleteId=null}){Text("Batal")}}) }
    if (showAdd) {
        AddObligationDialog(
            onDismiss = { showAdd = false },
            onSave = { name, kind, amount, day, remaining ->
                vm.addObligation(name, kind, amount, day, remaining)
                showAdd = false
            }
        )
    }
}

@Composable
private fun AddObligationDialog(
    onDismiss: () -> Unit,
    onSave: (String, ObligationKind, Long, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf(ObligationKind.CICILAN) }
    var amount by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var remaining by remember { mutableStateOf("") }
    val value = amount.toLongOrNull() ?: 0L
    val dueDay = day.toIntOrNull() ?: 0

    FormDialog(
        title = "Kewajiban baru",
        confirmEnabled = name.isNotBlank() && value > 0 && dueDay in 1..31,
        onDismiss = onDismiss,
        onConfirm = { onSave(name, kind, value, dueDay, remaining.toIntOrNull() ?: -1) }
    ) {
        TextInput("Nama", name) { name = it }
        DropdownField("Jenis", ObligationKind.values().toList(), kind, { it.label }) { kind = it }
        AmountField("Jumlah per bulan", amount) { amount = it }
        IntField("Tanggal jatuh tempo (1 sampai 31)", day) { day = it }
        IntField("Sisa pembayaran (kosong jika tanpa batas)", remaining) { remaining = it }
    }
}
