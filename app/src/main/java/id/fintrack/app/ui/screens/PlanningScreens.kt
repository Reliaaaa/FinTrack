package id.fintrack.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.data.GoalEntity
import id.fintrack.app.domain.*
import id.fintrack.app.ui.*
import id.fintrack.app.vm.MainViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable fun BudgetScreen(vm: MainViewModel) {
    val budgets by vm.budgets.collectAsStateWithLifecycle()
    val report by vm.report.collectAsStateWithLifecycle()
    var add by rememberSaveable { mutableStateOf(false) }
    var category by remember { mutableStateOf(Category.MAKAN) }
    var amount by rememberSaveable { mutableStateOf("") }
    var removing by remember { mutableStateOf<String?>(null) }
    LazyColumn(contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        item { Text("Anggaran",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold);Text(report.label,color=MaterialTheme.colorScheme.onSurfaceVariant) }
        item { FinTrackCard {
            Text("Batas pengeluaran bulanan")
            Text(Money.format(budgets.sumOf { it.amount }),style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold)
            Text("Batas kategori berlaku setiap bulan. Pengeluaran dihitung dari transaksi bulan berjalan.",style=MaterialTheme.typography.bodySmall)
            Button(onClick={category=Category.MAKAN;amount="";add=true}){Text("Atur batas kategori")}
        } }
        if(budgets.isEmpty()) item { EmptyNote("Belum ada anggaran. Mulai dari makan, transportasi, atau belanja.") }
        items(budgets,key={it.category}) { b ->
            val used=report.summary.byCategory.firstOrNull { it.first.name==b.category }?.second?:0L
            val ratio=used.toFloat()/b.amount
            FinTrackCard {
                LabeledValue(Category.fromName(b.category).label, when {ratio>=1 -> "Melebihi batas";ratio>=.8 -> "Mendekati batas";else -> "Normal"})
                Text(Money.format(used),style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
                LinearProgressIndicator(progress={ratio.coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth(),color=if(ratio>=1) MaterialTheme.colorScheme.error else FinGreen)
                LabeledValue("Batas",Money.format(b.amount))
                LabeledValue(if(used>b.amount) "Lebih" else "Sisa",Money.format(kotlin.math.abs(b.amount-used)))
                Row { TextButton(onClick={category=Category.fromName(b.category);amount=b.amount.toString();add=true}){Text("Ubah")};TextButton(onClick={removing=b.category}){Text("Hapus batas")} }
            }
        }
    }
    if(add) FormDialog("Atur Batas Anggaran",confirmEnabled=(amount.toLongOrNull()?:0)>0,onDismiss={add=false},onConfirm={vm.saveBudget(category.name,amount.toLong());add=false}) {
        DropdownField("Kategori",Category.values().filter { it!=Category.GAJI },category,{it.label}){category=it}
        AmountField("Batas per bulan",amount){amount=it}
    }
    removing?.let { c -> AlertDialog(onDismissRequest={removing=null},title={Text("Hapus batas anggaran?")},text={Text("Riwayat transaksi tetap tersimpan.")},confirmButton={TextButton(onClick={vm.deleteBudget(c);removing=null}){Text("Hapus")}},dismissButton={TextButton(onClick={removing=null}){Text("Batal")}}) }
}

@Composable fun GoalsScreen(vm: MainViewModel, onBack: () -> Unit) {
    val goals by vm.goals.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<GoalEntity?>(null) }
    var adding by rememberSaveable { mutableStateOf(false) }
    var removing by remember { mutableStateOf<GoalEntity?>(null) }
    ScreenScaffold("Target Tabungan Impian",onBack) { pad ->
        LazyColumn(Modifier.padding(pad),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            item { Button(onClick={adding=true}){Text("Tambah target")};Text("Progres dicatat manual. Nilai target bukan saldo rekening tambahan.",style=MaterialTheme.typography.bodySmall) }
            if(goals.isEmpty()) item { EmptyNote("Buat target laptop, dana darurat, atau liburan.") }
            items(goals,key={it.id}) { g -> FinTrackCard {
                LabeledValue(g.name,if(g.saved>=g.target) "Tercapai" else fmtDate(LocalDate.parse(g.dueDate)))
                Text(Money.format(g.saved),style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
                LinearProgressIndicator(progress={ (g.saved.toFloat()/g.target).coerceIn(0f,1f) },modifier=Modifier.fillMaxWidth(),color=FinGreen)
                LabeledValue("Target",Money.format(g.target))
                LabeledValue("Kebutuhan setoran per bulan",Money.format(SeasonalGoal.monthlyContribution(g.target,g.saved,LocalDate.now(),LocalDate.parse(g.dueDate))))
                Row { TextButton(onClick={editing=g}){Text("Ubah progres")}; TextButton(onClick={removing=g}){Text("Hapus")} }
            } }
        }
    }
    if(adding || editing!=null) {
        val existing=editing
        var name by remember(existing) { mutableStateOf(existing?.name?:"") }
        var target by remember(existing) { mutableStateOf(existing?.target?.toString()?:"") }
        var saved by remember(existing) { mutableStateOf(existing?.saved?.toString()?:"0") }
        var date by remember(existing) { mutableStateOf(existing?.dueDate?:LocalDate.now().plusMonths(6).toString()) }
        val parsed=runCatching { LocalDate.parse(date) }.getOrNull()
        val t=target.toLongOrNull()?:0L;val v=saved.toLongOrNull()?:-1L
        FormDialog("Target Tabungan",confirmEnabled=name.isNotBlank()&&t>0&&v in 0..t&&parsed!=null,onDismiss={adding=false;editing=null},onConfirm={vm.saveGoal(GoalEntity(existing?.id?:0,name,t,v,date));adding=false;editing=null}) {
            TextInput("Nama target",name){name=it}
            AmountField("Jumlah target",target){target=it}
            AmountField("Sudah terkumpul",saved){saved=it}
            TextInput("Tanggal target YYYY-MM-DD",date){date=it}
            if(parsed==null) Text("Gunakan tanggal valid, contoh 2027-06-30.",color=MaterialTheme.colorScheme.error)
        }
    }
    removing?.let { g -> AlertDialog(onDismissRequest={removing=null},title={Text("Hapus target ${g.name}?")},confirmButton={TextButton(onClick={vm.deleteGoal(g.id);removing=null}){Text("Hapus")}},dismissButton={TextButton(onClick={removing=null}){Text("Batal")}}) }
}

@Composable fun ForecastScreen(vm: MainViewModel,onBack: () -> Unit) {
    val accounts by vm.accounts.collectAsStateWithLifecycle()
    val obligations by vm.obligationEntities.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    var spending by rememberSaveable { mutableStateOf("50000") }
    var purchase by rememberSaveable { mutableStateOf("0") }
    val liquid=accounts.filter { it.liquid }.sumOf { it.balance }
    val specs=obligations.map { ObligationSpec(it.amount,it.dueDay,it.remaining,it.lastPaidMonth?.let(YearMonth::parse)) }
    val daily=spending.toLongOrNull()?:0L;val planned=purchase.toLongOrNull()?:0L
    val result=Forecast.calculate(LocalDate.now(),liquid,settings.monthlyIncome,settings.incomeDay,daily,specs,planned)
    val base=Forecast.calculate(LocalDate.now(),liquid,settings.monthlyIncome,settings.incomeDay,daily,specs)
    ScreenScaffold("Proyeksi Kas 90 Hari",onBack) { pad ->
        LazyColumn(Modifier.padding(pad),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            item { FinTrackCard { Text("Cek sebelum belanja",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Text("Simulasi dari saldo likuid, pemasukan bulanan, dan jadwal tagihan. Hasil berubah mengikuti asumsi yang kamu isi.") } }
            item { AmountField("Belanja harian di luar tagihan",spending){spending=it};AmountField("Rencana belanja sekali hari ini",purchase){purchase=it} }
            item { FinTrackCard {
                LabeledValue("Saldo awal",Money.format(liquid))
                LabeledValue("Pemasukan bulanan",Money.format(settings.monthlyIncome))
                Text("Tanggal pemasukan: ${settings.incomeDay}. Saldo awal dianggap sudah memuat transaksi hari ini. Tagihan hari ini yang belum ditandai lunas tetap dikurangi.",style=MaterialTheme.typography.bodySmall)
                if(settings.monthlyIncome==0L) Text("Pemasukan belum diatur. Tambahkan melalui Pengaturan.",color=MaterialTheme.colorScheme.error)
                Text("Saldo terendah setelah rencana",style=MaterialTheme.typography.labelLarge)
                Text(Money.format(result.lowest.balance),style=MaterialTheme.typography.headlineMedium,color=if(result.lowest.balance<0) MaterialTheme.colorScheme.error else FinGreen)
                Text(fmtDate(result.lowest.date))
                result.firstDeficit?.let { Text("Saldo diperkirakan negatif mulai ${fmtDate(it.date)}.",color=MaterialTheme.colorScheme.error) }
                LabeledValue("Terendah tanpa rencana",Money.format(base.lowest.balance))
                ForecastChart(result.points)
                LabeledValue("Akhir hari ke 90",Money.format(result.points.last().balance))
            } }
            item { SectionTitle("Ringkasan per 15 hari") }
            items(result.points.filterIndexed { i,_ -> i%15==0 }) { p -> LabeledValue(fmtDate(p.date),Money.format(p.balance)) }
            item { Text("Dana target tabungan belum dikunci dari kas. Biaya harian adalah asumsi manual. Saldo tidak otomatis mengurangi investasi yang dicatat pada portofolio.",style=MaterialTheme.typography.bodySmall) }
        }
    }
}
@Composable private fun ForecastChart(points: List<ForecastPoint>) {
    val color=MaterialTheme.colorScheme.primary
    Canvas(Modifier.fillMaxWidth().height(150.dp).padding(vertical=12.dp)) {
        val min=points.minOf { it.balance }.toDouble();val max=points.maxOf { it.balance }.toDouble()
        val range=(max-min).coerceAtLeast(1.0)
        val path=Path()
        points.forEachIndexed { i,p ->
            val x=size.width*i/(points.size-1);val y=(size.height*(1-(p.balance-min)/range)).toFloat()
            if(i==0) path.moveTo(x,y) else path.lineTo(x,y)
        }
        drawPath(path,color,style=Stroke(3.dp.toPx()))
    }
}

@Composable fun TransferScreen(vm: MainViewModel,onBack: () -> Unit) {
    val accounts by vm.accounts.collectAsStateWithLifecycle()
    var from by rememberSaveable { mutableStateOf<Long?>(null) };var to by rememberSaveable { mutableStateOf<Long?>(null) }
    var amount by rememberSaveable { mutableStateOf("") }
    val source=accounts.firstOrNull { it.id==from };val target=accounts.firstOrNull { it.id==to }
    val value=amount.toLongOrNull()?:0L
    ScreenScaffold("Transfer Antar Rekening",onBack) { pad ->
        LazyColumn(Modifier.padding(pad),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            item { FinTrackCard { Text("Catat perpindahan dana",style=MaterialTheme.typography.titleLarge);Text("Pencatatan ini mengubah dua saldo di FinTrack. Transfer uang sebenarnya dilakukan melalui aplikasi bank atau dompetmu.") } }
            item { DropdownField("Sumber",accounts,source,{it.name}){from=it.id};DropdownField("Tujuan",accounts.filter { it.id!=from },target,{it.name}){to=it.id};AmountField("Nominal transfer",amount){amount=it} }
            item { source?.let { LabeledValue("Saldo sumber",Money.format(it.balance)) };if(value>(source?.balance?:0)) Text("Saldo sumber tidak cukup.",color=MaterialTheme.colorScheme.error) }
            item { Button(onClick={vm.transfer(requireNotNull(from),requireNotNull(to),value);amount=""},enabled=source!=null&&target!=null&&from!=to&&value>0&&value<=source.balance){Text("Catat transfer")} }
            item { Text("Transfer tidak masuk ke total pemasukan atau pengeluaran. Menghapus salah satu sisi transfer juga mengembalikan kedua saldo.",style=MaterialTheme.typography.bodySmall) }
        }
    }
}
