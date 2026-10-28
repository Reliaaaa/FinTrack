@file:OptIn(ExperimentalMaterial3Api::class)

package id.fintrack.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.domain.AssetClass
import id.fintrack.app.domain.Money
import id.fintrack.app.domain.SeasonalGoal
import id.fintrack.app.domain.SplitBill
import id.fintrack.app.domain.SplitItem
import id.fintrack.app.domain.Zakat
import id.fintrack.app.ui.AmountField
import id.fintrack.app.ui.FinTrackCard
import id.fintrack.app.ui.DecimalField
import id.fintrack.app.ui.EmptyNote
import id.fintrack.app.ui.LabeledValue
import id.fintrack.app.ui.ScreenScaffold
import id.fintrack.app.ui.SectionTitle
import id.fintrack.app.ui.TextInput
import id.fintrack.app.ui.fmtDate
import id.fintrack.app.vm.MainViewModel
import java.time.LocalDate
import java.util.Locale

@Composable
fun ReportScreen(vm: MainViewModel, onBack: () -> Unit) {
    val tx by vm.transactions.collectAsStateWithLifecycle()
    val accounts by vm.accounts.collectAsStateWithLifecycle()
    var monthText by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(java.time.YearMonth.now().toString()) }
    val month=java.time.YearMonth.parse(monthText)
    var accountId by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<Long?>(null) }
    val selected=accounts.firstOrNull { it.id==accountId }
    val records=tx.filter { it.transferGroup==null && (accountId==null || it.accountId==accountId) }.map { t ->
        id.fintrack.app.domain.TxRecord(id.fintrack.app.domain.TxType.valueOf(t.type),t.amount,id.fintrack.app.domain.Category.fromName(t.category),java.time.Instant.ofEpochMilli(t.timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDate())
    }
    val summary=id.fintrack.app.domain.Reports.month(records,month.year,month.monthValue)
    val exporter=androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/csv")) { uri -> if(uri!=null) vm.exportTo(uri) }
    ScreenScaffold("Laporan Keuangan", onBack) { pad ->
        Column(Modifier.padding(pad).padding(20.dp).fillMaxSize().verticalScroll(rememberScrollState()),verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                androidx.compose.material3.TextButton(onClick={monthText=month.minusMonths(1).toString()}){Text("‹ Sebelumnya")}
                Text(month.toString(),modifier=Modifier.padding(top=12.dp),fontWeight=FontWeight.Bold)
                androidx.compose.material3.TextButton(onClick={monthText=month.plusMonths(1).toString()},enabled=month<java.time.YearMonth.now()){Text("Berikut ›")}
            }
            id.fintrack.app.ui.DropdownField("Rekening",accounts,selected,{it.name}){accountId=it.id}
            if(accountId!=null) androidx.compose.material3.TextButton(onClick={accountId=null}){Text("Semua rekening")}
            FinTrackCard {
                Text("Net Tabungan Bersih",color=MaterialTheme.colorScheme.onSurfaceVariant)
                Text(Money.format(summary.net),style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold,color=if(summary.net>=0) id.fintrack.app.ui.FinGreen else MaterialTheme.colorScheme.error)
                LabeledValue("Pemasukan", Money.format(summary.income))
                LabeledValue("Pengeluaran", Money.format(summary.expense))
                if(summary.income>0) LabeledValue("Rasio tabungan",String.format(Locale("id"),"%.1f%%",summary.net*100.0/summary.income))
            }
            SectionTitle("Breakdown Pengeluaran")
            if(summary.byCategory.isEmpty()) EmptyNote("Belum ada pengeluaran dalam periode ini.")
            else {
                val palette=listOf(id.fintrack.app.ui.FinBlue,id.fintrack.app.ui.FinGreen,androidx.compose.ui.graphics.Color(0xFFFFB3AD),androidx.compose.ui.graphics.Color(0xFFB4C5FF),androidx.compose.ui.graphics.Color(0xFFFFC66D))
                androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(170.dp)) {
                    var start=-90f
                    val side=size.height
                    summary.byCategory.forEachIndexed { i,(_,amount) ->
                        val sweep=360f*amount/summary.expense
                        drawArc(palette[i%palette.size],start,sweep,false,topLeft=androidx.compose.ui.geometry.Offset((size.width-side)/2+18,18f),size=androidx.compose.ui.geometry.Size(side-36,side-36),style=androidx.compose.ui.graphics.drawscope.Stroke(28f))
                        start+=sweep
                    }
                }
                summary.byCategory.forEach { (cat,amount) -> FinTrackCard {
                    LabeledValue(cat.label,Money.format(amount))
                    LinearProgressIndicator(progress={amount.toFloat()/summary.expense},modifier=Modifier.fillMaxWidth())
                } }
            }
            FinTrackCard {
                SectionTitle("Evaluasi Kas")
                Text(when { summary.income==0L -> "Belum ada pemasukan tercatat pada periode ini.";summary.net<0 -> "Pengeluaran lebih besar daripada pemasukan. Periksa kategori terbesar dan jadwal tagihan.";else -> "Pemasukan melebihi pengeluaran sebesar ${Money.format(summary.net)}. Periksa target tabungan untuk mengalokasikan sisa kas." })
                Text("Evaluasi dihitung dengan aturan sederhana dari transaksi tercatat.",style=MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(onClick={exporter.launch("fintrack-transaksi-${LocalDate.now()}.csv")}){Text("Ekspor semua riwayat CSV")}
            Text("CSV berisi seluruh periode dan rekening, termasuk pasangan transfer internal. Filter di atas hanya berlaku untuk tampilan laporan.",style=MaterialTheme.typography.bodySmall)
        }
    }
}

private class SplitRow(name: String = "", price: String = "", shared: Set<String> = emptySet()) {
    var name by mutableStateOf(name)
    var price by mutableStateOf(price)
    var shared by mutableStateOf(shared)
}

@Composable
fun SplitScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var peopleText by remember { mutableStateOf("") }
    var extras by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    val rows = remember { mutableStateListOf(SplitRow()) }
    val people = peopleText.split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinct()

    val items = rows.mapNotNull { row ->
        val p = row.price.toLongOrNull() ?: 0L
        val who = people.filter { it in row.shared }
        if (p > 0 && who.isNotEmpty()) SplitItem(row.name.ifBlank { "Item" }, p, who) else null
    }
    val extraTotal = (extras.toLongOrNull() ?: 0L) - (discount.toLongOrNull() ?: 0L)
    val result = SplitBill.split(items, extraTotal)

    ScreenScaffold("Bagi tagihan", onBack) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextInput("Peserta, pisahkan dengan koma", peopleText) { peopleText = it }
            SectionTitle("Item")
            rows.forEachIndexed { index, row ->
                FinTrackCard {
                    TextInput("Nama item", row.name) { row.name = it }
                    AmountField("Harga", row.price) { row.price = it }
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        people.forEach { person ->
                            FilterChip(
                                selected = person in row.shared,
                                onClick = {
                                    row.shared = if (person in row.shared) row.shared - person else row.shared + person
                                },
                                label = { Text(person) }
                            )
                        }
                    }
                    if (rows.size > 1) {
                        OutlinedButton(onClick = { rows.removeAt(index) }) { Text("Hapus item") }
                    }
                }
            }
            OutlinedButton(onClick = { rows.add(SplitRow()) }) { Text("Tambah item") }
            AmountField("Pajak dan layanan", extras) { extras = it }
            AmountField("Diskon", discount) { discount = it }

            SectionTitle("Hasil")
            if (result.isEmpty()) {
                EmptyNote("Isi peserta, harga item, lalu pilih siapa yang ikut tiap item.")
            } else {
                FinTrackCard {
                    result.forEach { (name, amount) -> LabeledValue(name, Money.format(amount)) }
                    LabeledValue("Total", Money.format(result.values.sum()), emphasize = true)
                }
                Button(onClick = {
                    val text = buildString {
                        append("Bagi tagihan\n")
                        result.forEach { (n, a) -> append("$n: ${Money.format(a)}\n") }
                        append("Total: ${Money.format(result.values.sum())}")
                    }
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(send, null))
                }) { Text("Bagikan hasil") }
            }
        }
    }
}

@Composable
fun ZakatScreen(vm: MainViewModel, onBack: () -> Unit) {
    val assets by vm.assets.collectAsStateWithLifecycle()
    val gold = assets.holdings.firstOrNull { it.assetClass == AssetClass.EMAS.name }?.unitPrice ?: 0L
    var wealth by remember(assets.summary.net) { mutableStateOf(assets.summary.net.coerceAtLeast(0L).toString()) }
    var goldPrice by remember(gold) { mutableStateOf(if (gold > 0) gold.toString() else "") }
    val result = Zakat.maal(wealth.toLongOrNull() ?: 0L, goldPrice.toLongOrNull() ?: 0L)

    ScreenScaffold("Zakat maal", onBack) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AmountField("Harta yang dizakati", wealth) { wealth = it }
            AmountField("Harga emas per gram", goldPrice) { goldPrice = it }
            FinTrackCard {
                LabeledValue("Nisab (85 gram emas)", Money.format(result.nisab))
                LabeledValue("Tarif", "2,5 persen")
                if ((goldPrice.toLongOrNull() ?: 0L) <= 0L) {
                    Text("Isi harga emas untuk menghitung nisab.")
                } else if (result.reached) {
                    LabeledValue("Zakat yang dibayar", Money.format(result.due), emphasize = true)
                } else {
                    Text("Harta belum mencapai nisab.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
            Text(
                "Perhitungan mengikuti ketentuan umum: nisab 85 gram emas, tarif 2,5 persen, harta dimiliki satu tahun. Tanyakan lembaga zakat Anda untuk rincian akhir.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SeasonalScreen(onBack: () -> Unit) {
    var target by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf("") }
    var dueText by remember { mutableStateOf(LocalDate.now().plusMonths(6).toString()) }
    val due = runCatching { LocalDate.parse(dueText) }.getOrNull()
    val monthly = if (due != null) {
        SeasonalGoal.monthlyContribution(target.toLongOrNull() ?: 0L, saved.toLongOrNull() ?: 0L, LocalDate.now(), due)
    } else null

    ScreenScaffold("Dana musiman", onBack) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AmountField("Target dana", target) { target = it }
            AmountField("Sudah terkumpul", saved) { saved = it }
            TextInput("Tanggal butuh (yyyy-mm-dd)", dueText) { dueText = it.take(10) }
            FinTrackCard {
                if (due == null) {
                    Text("Format tanggal belum benar.", color = MaterialTheme.colorScheme.error)
                } else {
                    LabeledValue("Dibutuhkan pada", fmtDate(due))
                    LabeledValue("Setoran per bulan", Money.format(monthly ?: 0L), emphasize = true)
                }
            }
        }
    }
}
