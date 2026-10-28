@file:OptIn(ExperimentalMaterial3Api::class)

package id.fintrack.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.saveable.rememberSaveable
import java.time.LocalDate
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import id.fintrack.app.data.AccountEntity
import id.fintrack.app.data.TransactionEntity
import id.fintrack.app.domain.Categorizer
import id.fintrack.app.domain.Category
import id.fintrack.app.domain.Money
import id.fintrack.app.domain.TxType
import id.fintrack.app.ui.AmountField
import id.fintrack.app.ui.FinTrackCard
import id.fintrack.app.ui.DropdownField
import id.fintrack.app.ui.EmptyNote
import id.fintrack.app.ui.FormDialog
import id.fintrack.app.ui.SectionTitle
import id.fintrack.app.ui.TextInput
import id.fintrack.app.ui.fmtDate
import id.fintrack.app.vm.MainViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun TransactionsScreen(vm: MainViewModel) {
    val txs by vm.transactions.collectAsStateWithLifecycle()
    val drafts by vm.drafts.collectAsStateWithLifecycle()
    val accounts by vm.accounts.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<TransactionEntity?>(null) }
    var deleting by remember { mutableStateOf<TransactionEntity?>(null) }
    var search by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("ALL") }
    val shown = txs.filter { t -> (filter=="ALL" || if(filter=="TRANSFER") t.transferGroup!=null else t.type==filter) && (t.note.contains(search,true) || Category.fromName(t.category).label.contains(search,true)) }
    var showAdd by remember { mutableStateOf(false) }
    var confirming by remember { mutableStateOf<TransactionEntity?>(null) }
    val names = remember(accounts) { accounts.associate { it.id to it.name } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah transaksi")
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Text("Transaksi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }

            if (drafts.isNotEmpty()) {
                item { SectionTitle("Menunggu konfirmasi (${drafts.size})") }
                items(drafts, key = { "d" + it.id }) { d ->
                    FinTrackCard {
                        Text(d.note.ifEmpty { "Transaksi dari notifikasi" }, fontWeight = FontWeight.SemiBold)
                        Text(
                            (if (d.type == TxType.INCOME.name) "Masuk " else "Keluar ") + Money.format(d.amount) +
                                "  " + Category.fromName(d.category).label
                        )
                        Row {
                            TextButton(onClick = { confirming = d }) { Text("Konfirmasi") }
                            TextButton(onClick = { vm.discardDraft(d.id) }) { Text("Buang") }
                        }
                    }
                }
            }

            item { TextInput("Cari transaksi",search){search=it}
                Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    listOf("ALL" to "Semua","EXPENSE" to "Keluar","INCOME" to "Masuk","TRANSFER" to "Transfer").forEach { (key,label) -> FilterChip(selected=filter==key,onClick={filter=key},label={Text(label)}) }
                }
            }
            item { SectionTitle("Riwayat") }
            if (shown.isEmpty()) {
                item { EmptyNote("Belum ada transaksi. Tekan tombol tambah.") }
            } else {
                items(shown, key = { it.id }) { t ->
                    TransactionRow(t, names[t.accountId] ?: "", onDelete = { deleting=t }, onEdit = { editing=t })
                }
            }
        }
    }

    if (showAdd || editing!=null) {
        AddTransactionDialog(
            accounts = accounts,
            initial = editing,
            onDismiss = { showAdd = false; editing=null },
            onSave = { acc, type, amount, category, note, timestamp ->
                val old=editing
                if(old==null) vm.addTransaction(acc, type, amount, category, note, timestamp)
                else vm.editTransaction(old.id,acc,type,amount,category,note,timestamp)
                editing=null
                showAdd = false
            }
        )
    }
    deleting?.let { t -> AlertDialog(onDismissRequest={deleting=null},title={Text("Hapus transaksi?")},text={Text(if(t.transferGroup!=null) "Kedua sisi transfer dihapus dan kedua saldo dikembalikan." else "Saldo rekening dikembalikan sesuai transaksi ini.")},confirmButton={TextButton(onClick={vm.deleteTransaction(t);deleting=null}){Text("Hapus")}},dismissButton={TextButton(onClick={deleting=null}){Text("Batal")}}) }
    confirming?.let { d ->
        ConfirmDraftDialog(
            draft = d,
            accounts = accounts,
            onDismiss = { confirming = null },
            onConfirm = { acc, category ->
                vm.confirmDraft(d.id, acc, category)
                confirming = null
            }
        )
    }
}

@Composable
private fun TransactionRow(t: TransactionEntity, accountName: String, onDelete: () -> Unit, onEdit: () -> Unit) {
    val date = Instant.ofEpochMilli(t.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    val income = t.type == TxType.INCOME.name || t.type=="TRANSFER_IN"
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(t.note.ifEmpty { Category.fromName(t.category).label }, fontWeight = FontWeight.SemiBold)
            Text(
                listOf(fmtDate(date), Category.fromName(t.category).label, accountName).filter { it.isNotEmpty() }.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            (if (income) "+" else "-") + Money.format(t.amount),
            color = if (income) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp)
        )
        if(t.transferGroup==null) IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, contentDescription="Ubah transaksi") }
        IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, contentDescription = "Hapus transaksi") }
    }
}

@Composable
private fun AddTransactionDialog(
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    initial: TransactionEntity? = null,
    onSave: (Long, TxType, Long, Category, String, Long) -> Unit
) {
    var type by remember { mutableStateOf(initial?.type?.let(TxType::valueOf)?:TxType.EXPENSE) }
    var amount by remember { mutableStateOf(initial?.amount?.toString()?:"") }
    var note by remember { mutableStateOf(initial?.note?:"") }
    var category by remember { mutableStateOf(initial?.category?.let(Category::fromName)?:Category.LAINNYA) }
    var categoryTouched by remember { mutableStateOf(initial!=null) }
    var account by remember { mutableStateOf(accounts.firstOrNull { it.id==initial?.accountId }?:accounts.firstOrNull()) }
    var date by remember { mutableStateOf(initial?.timestamp?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }?.toString()?:LocalDate.now().toString()) }
    val parsedDate=runCatching { LocalDate.parse(date) }.getOrNull()
    val value = amount.toLongOrNull() ?: 0L

    FormDialog(
        title = if(initial==null) "Catat Transaksi" else "Ubah Transaksi",
        confirmEnabled = value > 0 && account != null && parsedDate!=null && !parsedDate.isAfter(LocalDate.now()),
        onDismiss = onDismiss,
        onConfirm = { account?.let { onSave(it.id, type, value, category, note, requireNotNull(parsedDate).atTime(12,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) } }
    ) {
        if (accounts.isEmpty()) {
            Text("Tambah rekening dulu di tab Aset.", color = MaterialTheme.colorScheme.error)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = type == TxType.EXPENSE, onClick = { type = TxType.EXPENSE }, label = { Text("Pengeluaran") })
            FilterChip(selected = type == TxType.INCOME, onClick = { type = TxType.INCOME }, label = { Text("Pemasukan") })
        }
        AmountField("Jumlah", amount) { amount = it }
        TextInput("Tanggal YYYY-MM-DD",date){date=it}
        TextInput("Catatan", note) {
            note = it
            if (!categoryTouched) category = Categorizer.categorize(it)
        }
        DropdownField("Kategori", Category.values().toList(), category, { it.label }) {
            category = it
            categoryTouched = true
        }
        DropdownField("Rekening", accounts, account, { it.name }) { account = it }
    }
}

@Composable
private fun ConfirmDraftDialog(
    draft: TransactionEntity,
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Category) -> Unit
) {
    var account by remember { mutableStateOf(accounts.firstOrNull()) }
    var category by remember { mutableStateOf(Category.fromName(draft.category)) }

    FormDialog(
        title = "Konfirmasi transaksi",
        confirmLabel = "Konfirmasi",
        confirmEnabled = account != null,
        onDismiss = onDismiss,
        onConfirm = { account?.let { onConfirm(it.id, category) } }
    ) {
        Text(draft.note.ifEmpty { "Transaksi dari notifikasi" }, fontWeight = FontWeight.SemiBold)
        Text((if (draft.type == TxType.INCOME.name) "Masuk " else "Keluar ") + Money.format(draft.amount))
        DropdownField("Kategori", Category.values().toList(), category, { it.label }) { category = it }
        DropdownField("Rekening", accounts, account, { it.name }) { account = it }
    }
}
