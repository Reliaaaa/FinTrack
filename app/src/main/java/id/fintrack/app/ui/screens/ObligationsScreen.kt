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
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import id.fintrack.app.ui.OperationConfirm
import id.fintrack.app.ui.OperationButton
import id.fintrack.app.ui.OperationForm
import id.fintrack.app.ui.FormDialog
import id.fintrack.app.ui.IntField
import id.fintrack.app.ui.LabeledValue
import id.fintrack.app.ui.TextInput
import id.fintrack.app.ui.fmtDate
import id.fintrack.app.vm.MainViewModel
import java.util.Locale

@Composable
fun ObligationsScreen(vm: MainViewModel, onBack: () -> Unit) {
    val state by vm.obligations.collectAsStateWithLifecycle()
    val payments by vm.billPayments.collectAsStateWithLifecycle()
    var cancelPayment by remember { mutableStateOf<id.fintrack.app.data.BillPaymentEntity?>(null) }
    var markId by remember { mutableStateOf<Long?>(null) }
    var deleteId by remember { mutableStateOf<Long?>(null) }
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title={Text("Tagihan dan cicilan")}, navigationIcon={
            IconButton(onClick=onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") }
        }) },
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
                        payments.firstOrNull { it.obligationId == o.id && it.period == o.lastPaidMonth }?.let { payment ->
                            if (payment.transactionId != null) {
                                TextButton(onClick={cancelPayment=payment}) { Text("Batalkan catatan pembayaran ${payment.period}") }
                            } else {
                                Text("Penanda lunas historis; belum terkait transaksi.")
                            }
                        }
                        LabeledValue("Jatuh tempo berikutnya", fmtDate(row.nextDue))
                        if (o.remaining >= 0) LabeledValue("Sisa pembayaran", "${o.remaining} kali")
                        if (row.paidThisMonth) {
                            Text("Lunas bulan ini", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        } else if (o.remaining != 0) {
                            TextButton(onClick = { markId=o.id }) { Text("Catat pembayaran") }
                        } else {
                            Text("Selesai", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    cancelPayment?.let { payment ->
        var deleteExpense by remember(payment) { mutableStateOf(false) }
        OperationForm(vm, "cancelBillPayment", onSaved = { cancelPayment=null }) {
        FormDialog(title="Batalkan catatan pembayaran ${payment.period}?", confirmLabel="Konfirmasi pembatalan",
            confirmEnabled=true, onDismiss={cancelPayment=null}, onConfirm={
                payment.transactionId?.let { vm.cancelBillPayment(payment.obligationId, payment.period, it, deleteExpense) }
            }) {
            Text("Status kembali belum lunas dan sisa cicilan dipulihkan. Ini hanya koreksi catatan.")
            if(payment.origin == "CREATED") {
                DropdownField("Pengeluaran", listOf(false,true), deleteExpense,
                    { if(it) "Hapus dan pulihkan saldo" else "Pertahankan pengeluaran" }) { deleteExpense=it }
            }
            Text(if(deleteExpense) "Pengeluaran dihapus; nominalnya dikembalikan ke saldo rekening." else "Kaitan dilepas. Pengeluaran dan saldo tetap; jangan catat pengeluaran kedua saat mengaitkannya kembali.")
        }
        }
    }
    markId?.let { id ->
        val accounts by vm.accounts.collectAsStateWithLifecycle()
        val transactions by vm.transactions.collectAsStateWithLifecycle()
        val period = remember(id) { java.time.YearMonth.now().toString() }
        var accountId by remember(id) { mutableStateOf<Long?>(null) }
        var txId by remember(id) { mutableStateOf<Long?>(null) }
        var mode by remember(id) { mutableStateOf("Baru") }
        val bill = state.rows.firstOrNull { it.entity.id == id }?.entity
        val candidates = transactions.filter { it.type == "EXPENSE" && it.transferGroup == null && it.amount == bill?.amount }
        OperationForm(vm, "payBill", onSaved = { markId=null }) {
        FormDialog(title="Catat pembayaran $period", confirmLabel="Konfirmasi",
            confirmEnabled=if(mode == "Baru") accountId != null else txId != null,
            onDismiss={markId=null}, onConfirm={
                vm.payBill(id, period, if(mode == "Baru") accountId else null, if(mode == "Kaitkan") txId else null)
            }) {
            Text("${bill?.name.orEmpty()} — ${Money.format(bill?.amount ?: 0L)}")
            DropdownField("Cara pencatatan", listOf("Baru", "Kaitkan"), mode, { it }) { mode=it }
            if(mode == "Baru") {
                Text("Membuat pengeluaran dan mengurangi saldo rekening. Tidak mengirim uang.")
                DropdownField("Rekening", accounts, accounts.firstOrNull { it.id == accountId }, { it.name }) { accountId=it.id }
            } else {
                Text("Pilih pengeluaran yang sudah dicatat. Saldo tidak dikurangi lagi.")
                DropdownField("Transaksi", candidates, candidates.firstOrNull { it.id == txId },
                    { "#${it.id} ${it.note} (${java.time.Instant.ofEpochMilli(it.timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDate()})" }) { txId=it.id }
            }
        }
        }
    }
    deleteId?.let { id -> OperationConfirm(vm, "deleteObligation", "Hapus jadwal tagihan?", "Tagihan tidak lagi dicadangkan dalam proyeksi. Riwayat transaksi tetap tersimpan.", { deleteId=null }, { vm.deleteObligation(id) }) }
    if (showAdd) {
        OperationForm(vm, "addObligation", onSaved = { showAdd=false }) {
        AddObligationDialog(
            onDismiss = { showAdd = false },
            onSave = { name, kind, amount, day, remaining ->
                vm.addObligation(name, kind, amount, day, remaining)
            }
        )
        }
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
