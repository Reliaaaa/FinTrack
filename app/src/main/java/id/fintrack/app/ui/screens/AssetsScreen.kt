@file:OptIn(ExperimentalMaterial3Api::class)

package id.fintrack.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.data.AccountEntity
import id.fintrack.app.data.HoldingEntity
import id.fintrack.app.domain.AccountType
import id.fintrack.app.domain.AssetClass
import id.fintrack.app.domain.Money
import id.fintrack.app.ui.AmountField
import id.fintrack.app.ui.FinTrackCard
import id.fintrack.app.ui.DecimalField
import id.fintrack.app.ui.DropdownField
import id.fintrack.app.ui.EmptyNote
import id.fintrack.app.ui.FormDialog
import id.fintrack.app.ui.LabeledValue
import id.fintrack.app.ui.PriceChart
import id.fintrack.app.ui.SectionTitle
import id.fintrack.app.ui.TextInput
import id.fintrack.app.vm.MainViewModel
import java.util.Locale

@Composable
fun AssetsScreen(vm: MainViewModel) {
    val state by vm.assets.collectAsStateWithLifecycle()
    val priceStatus by vm.priceStatus.collectAsStateWithLifecycle()
    var deletingAccount by remember { mutableStateOf<Long?>(null) }
    var deletingHolding by remember { mutableStateOf<HoldingEntity?>(null) }
    var showAccount by remember { mutableStateOf(false) }
    var showHolding by remember { mutableStateOf(false) }
    var pricing by remember { mutableStateOf<HoldingEntity?>(null) }
    var charting by remember { mutableStateOf<HoldingEntity?>(null) }
    val s = state.summary

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("Aset", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            FinTrackCard {
                Text("Kekayaan bersih", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    Money.format(s.net),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (s.net < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                LabeledValue("Total aset", Money.format(s.assets))
                LabeledValue("Sisa cicilan dan paylater", Money.format(s.liabilities))
                val pct = s.gainLossPercent
                if (pct != null) {
                    LabeledValue(
                        "Untung rugi aset bermodal",
                        Money.format(s.gainLoss) + " (" + String.format(Locale("id", "ID"), "%.1f", pct) + " persen)",
                        emphasize = true
                    )
                }
            }
        }
        if (s.assets > 0) {
            item { SectionTitle("Alokasi") }
            items(s.allocation().entries.sortedByDescending { it.value }.toList(), key = { it.key.name }) { e ->
                Column {
                    LabeledValue(e.key.label, String.format(Locale("id", "ID"), "%.1f", e.value) + " persen")
                    LinearProgressIndicator(progress = { (e.value / 100.0).toFloat().coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        item { SectionTitle("Rekening dan dompet") }
        if (state.accounts.isEmpty()) item { EmptyNote("Belum ada rekening.") }
        items(state.accounts, key = { "a" + it.id }) { a ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(a.name, fontWeight = FontWeight.SemiBold)
                    Text(
                        AccountType.fromName(a.type).label + if (a.liquid) "" else ", tidak dihitung sebagai uang harian",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(Money.format(a.balance), fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { deletingAccount=a.id }) { Icon(Icons.Outlined.Delete, contentDescription = "Hapus rekening") }
            }
        }
        item { OutlinedButton(onClick = { showAccount = true }) { Text("Tambah rekening") } }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                SectionTitle("Emas, saham, reksa dana, kripto")
                IconButton(onClick = { vm.refreshPrices() }) { Icon(Icons.Outlined.Refresh, contentDescription = "Perbarui harga kripto") }
            }
        }
        priceStatus?.let { item { Text(it, style = MaterialTheme.typography.bodySmall) } }
        if (state.holdings.isEmpty()) item { EmptyNote("Belum ada aset. Harga emas, saham, dan reksa dana diisi manual. Harga kripto bisa diambil otomatis.") }
        items(state.holdings, key = { "h" + it.id }) { h ->
            HoldingCard(h, onPrice = { pricing = h }, onChart = { charting = h; h.symbol?.let { vm.loadCandles(it) } }, onDelete = { deletingHolding=h })
        }
        item { OutlinedButton(onClick = { showHolding = true }) { Text("Tambah aset") } }
    }

    deletingAccount?.let { id -> AlertDialog(onDismissRequest={deletingAccount=null},title={Text("Hapus rekening?")},text={Text("Hanya rekening tanpa riwayat transaksi yang dapat dihapus.")},confirmButton={TextButton(onClick={vm.deleteAccount(id);deletingAccount=null}){Text("Hapus")}},dismissButton={TextButton(onClick={deletingAccount=null}){Text("Batal")}}) }
    deletingHolding?.let { h -> AlertDialog(onDismissRequest={deletingHolding=null},title={Text("Hapus aset ${h.name}?")},text={Text("Ini menghapus catatan portofolio tanpa menjual aset sebenarnya.")},confirmButton={TextButton(onClick={vm.deleteHolding(h);deletingHolding=null}){Text("Hapus")}},dismissButton={TextButton(onClick={deletingHolding=null}){Text("Batal")}}) }
    if (showAccount) {
        AddAccountDialog(onDismiss = { showAccount = false }) { name, type, balance, liquid ->
            vm.addAccount(name, type.name, balance, liquid)
            showAccount = false
        }
    }
    if (showHolding) {
        AddHoldingDialog(onDismiss = { showHolding = false }) { name, cls, symbol, qty, cost, price ->
            vm.addHolding(name, cls, symbol, qty, cost, price)
            showHolding = false
        }
    }
    pricing?.let { h ->
        var price by remember(h.id) { mutableStateOf(h.unitPrice.toString()) }
        FormDialog(
            title = "Harga ${h.name}",
            confirmEnabled = (price.toLongOrNull() ?: 0L) > 0,
            onDismiss = { pricing = null },
            onConfirm = {
                vm.setHoldingPrice(h.id, price.toLongOrNull() ?: h.unitPrice)
                pricing = null
            }
        ) {
            AmountField("Harga per ${AssetClass.fromName(h.assetClass).unit}", price) { price = it }
        }
    }
    charting?.let { h ->
        val candles by vm.candles.collectAsStateWithLifecycle()
        val status by vm.candleStatus.collectAsStateWithLifecycle()
        AlertDialog(
            onDismissRequest = { charting = null },
            title = { Text(h.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PriceChart(candles, Modifier.fillMaxWidth().height(280.dp))
                    status?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    Text("Data: Indodax. Grafik: TradingView Lightweight Charts.", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = { TextButton(onClick = { charting = null }) { Text("Tutup") } }
        )
    }
}

@Composable
private fun HoldingCard(h: HoldingEntity, onPrice: () -> Unit, onChart: () -> Unit, onDelete: () -> Unit) {
    val cls = AssetClass.fromName(h.assetClass)
    val value = Math.round(h.quantity * h.unitPrice)
    FinTrackCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(h.name, fontWeight = FontWeight.SemiBold)
                Text(
                    cls.label + ", " + qtyText(h.quantity) + " " + cls.unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, contentDescription = "Hapus aset") }
        }
        LabeledValue("Nilai", Money.format(value), emphasize = true)
        LabeledValue("Harga per ${cls.unit}", Money.format(h.unitPrice))
        Text("Harga dicatat: " + java.time.Instant.ofEpochMilli(h.priceUpdatedAt).atZone(java.time.ZoneId.systemDefault()).format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")),style=MaterialTheme.typography.bodySmall)
        Text(if(cls==AssetClass.KRIPTO && h.symbol!=null) "Dapat diperbarui dari Indodax. Tekan perbarui untuk harga terbaru." else "Harga manual",style=MaterialTheme.typography.bodySmall)
        if (h.costBasis > 0) {
            val gain = value - h.costBasis
            LabeledValue(
                "Untung rugi",
                Money.format(gain) + " (" + String.format(Locale("id", "ID"), "%.1f", gain * 100.0 / h.costBasis) + " persen)"
            )
        }
        Row {
            TextButton(onClick = onPrice) { Text("Ubah harga") }
            if (h.symbol != null) TextButton(onClick = onChart) { Text("Grafik") }
        }
    }
}

private fun qtyText(q: Double): String =
    if (q == Math.floor(q) && q < 1e12) q.toLong().toString() else String.format(Locale("id", "ID"), "%.8f", q).trimEnd('0').trimEnd(',')

@Composable
private fun AddAccountDialog(onDismiss: () -> Unit, onSave: (String, AccountType, Long, Boolean) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(AccountType.BANK) }
    var balance by remember { mutableStateOf("") }
    var liquid by remember { mutableStateOf(true) }

    FormDialog(
        title = "Rekening baru",
        confirmEnabled = name.isNotBlank(),
        onDismiss = onDismiss,
        onConfirm = { onSave(name, type, balance.toLongOrNull() ?: 0L, liquid) }
    ) {
        TextInput("Nama (misal BCA, GoPay)", name) { name = it }
        DropdownField("Jenis", AccountType.values().toList(), type, { it.label }) { type = it }
        AmountField("Saldo sekarang", balance) { balance = it }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Switch(checked = liquid, onCheckedChange = { liquid = it })
            Text("Hitung sebagai uang harian")
        }
    }
}

@Composable
private fun AddHoldingDialog(
    onDismiss: () -> Unit,
    onSave: (String, AssetClass, String?, Double, Long, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cls by remember { mutableStateOf(AssetClass.EMAS) }
    var symbol by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val quantity = qty.replace(',', '.').toDoubleOrNull() ?: 0.0

    FormDialog(
        title = "Aset baru",
        confirmEnabled = name.isNotBlank() && quantity > 0.0 && (price.toLongOrNull() ?: 0L) > 0,
        onDismiss = onDismiss,
        onConfirm = { onSave(name, cls, symbol.ifBlank { null }, quantity, cost.toLongOrNull() ?: 0L, price.toLongOrNull() ?: 0L) }
    ) {
        TextInput("Nama (misal Antam, BBCA, Bitcoin)", name) { name = it }
        DropdownField("Kelas", AssetClass.values().filter { it != AssetClass.KAS }, cls, { it.label }) { cls = it }
        if (cls == AssetClass.KRIPTO) TextInput("Pasangan Indodax (misal btc_idr)", symbol) { symbol = it }
        DecimalField("Jumlah (${cls.unit})", qty) { qty = it }
        AmountField("Total modal (opsional)", cost) { cost = it }
        AmountField("Harga per ${cls.unit}", price) { price = it }
    }
}
