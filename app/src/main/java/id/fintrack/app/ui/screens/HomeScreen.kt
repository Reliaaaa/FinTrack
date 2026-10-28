package id.fintrack.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.domain.*
import id.fintrack.app.ui.*
import id.fintrack.app.vm.MainViewModel
import java.time.*
import java.time.format.TextStyle
import java.util.Locale

@Composable fun HomeScreen(vm: MainViewModel, goTo: (String) -> Unit) {
    val s by vm.home.collectAsStateWithLifecycle()
    val accounts by vm.accounts.collectAsStateWithLifecycle()
    val report by vm.report.collectAsStateWithLifecycle()
    val tx by vm.transactions.collectAsStateWithLifecycle()
    val budgets by vm.budgets.collectAsStateWithLifecycle()
    var hidden by rememberSaveable { mutableStateOf(false) }
    fun money(v: Long) = if (hidden) "••••••" else Money.format(v)
    LazyColumn(contentPadding=PaddingValues(20.dp), verticalArrangement=Arrangement.spacedBy(18.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalanceWallet, null, tint=FinGreen, modifier=Modifier.size(32.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("FinTrack", fontSize=20.sp, fontWeight=FontWeight.Bold)
                    Text("Finance Tracker", color=MaterialTheme.colorScheme.onSurfaceVariant, fontSize=12.sp)
                }
                IconButton(onClick={goTo("more")}) { Icon(Icons.Outlined.Apps, "Semua fitur") }
                IconButton(onClick={goTo("settings")}) { Icon(Icons.Outlined.Settings, "Pengaturan") }
            }
        }
        item { Column { Text("Keuanganmu, lebih terarah", style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold); Text("Kelola arus kas dengan cerdas", color=MaterialTheme.colorScheme.onSurfaceVariant, fontSize=13.sp) } }
        item {
            Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF2563EB),Color(0xFF163EA6))),RoundedCornerShape(20.dp)).padding(20.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text("Total Saldo Tersedia", Modifier.weight(1f), color=Color.White.copy(alpha=.8f))
                    IconButton(onClick={hidden=!hidden}, Modifier.size(32.dp)) { Icon(if(hidden) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, "Tampilkan atau sembunyikan saldo", tint=Color.White) }
                }
                Text(money(s.liquid), fontSize=30.sp, fontWeight=FontWeight.Bold, color=Color.White)
                Text(accounts.filter { it.liquid }.joinToString("  •  ") { it.name }.ifEmpty { "Mulai dengan menambahkan rekening" }, color=Color.White.copy(alpha=.8f), fontSize=12.sp)
                HorizontalDivider(color=Color.White.copy(alpha=.18f))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) { Text("↓ Pemasukan bulan ini",fontSize=11.sp,color=Color.White.copy(alpha=.8f));Text(money(report.summary.income),fontWeight=FontWeight.SemiBold,color=Color.White) }
                    Column(Modifier.weight(1f)) { Text("↑ Pengeluaran bulan ini",fontSize=11.sp,color=Color.White.copy(alpha=.8f));Text(money(report.summary.expense),fontWeight=FontWeight.SemiBold,color=Color.White) }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                listOf(Triple("tx","Catat Baru",Icons.Outlined.Add),Triple("budget","Anggaran",Icons.Outlined.PieChart),Triple("assets","Portofolio",Icons.Outlined.AccountBalanceWallet),Triple("forecast","Simulasi",Icons.Outlined.QueryStats)).forEach { (route,label,icon) ->
                    Column(horizontalAlignment=Alignment.CenterHorizontally) { FilledTonalIconButton(onClick={goTo(route)}){Icon(icon,label)};Text(label,fontSize=11.sp) }
                }
            }
        }
        item {
            FinTrackCard {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Aman dipakai per hari",fontWeight=FontWeight.SemiBold);Icon(Icons.Outlined.Shield,null,tint=FinGreen) }
                Text(money(s.safe.dailyAllowance),fontSize=27.sp,fontWeight=FontWeight.Bold,color=if(s.safe.isDeficit) MaterialTheme.colorScheme.error else FinGreen)
                Text("Sampai ${fmtDate(s.nextIncome)} • ${s.safe.daysLeft} hari",fontSize=12.sp)
                Text("Tagihan dicadangkan: ${money(s.safe.reserved)}",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                if(s.safe.isDeficit) Text("Kas belum cukup untuk tagihan sebelum pemasukan berikutnya.",color=MaterialTheme.colorScheme.error)
                if(!s.hasAccounts) Button(onClick={goTo("assets")}){Text("Tambah rekening pertama")}
                else TextButton(onClick={goTo("forecast")}){Text("Cek dampak rencana belanja →")}
            }
        }
        item {
            FinTrackCard {
                SectionTitle("Tren Pengeluaran 7 Hari")
                val today=LocalDate.now()
                val daily=(6 downTo 0).map { offset ->
                    val date=today.minusDays(offset.toLong())
                    date to tx.filter { it.type==TxType.EXPENSE.name && Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()==date }.sumOf { it.amount }
                }
                Text("Total: ${money(daily.sumOf { it.second })}",fontSize=12.sp)
                val max=daily.maxOf { it.second }.coerceAtLeast(1)
                Row(Modifier.fillMaxWidth().height(128.dp),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.Bottom) {
                    daily.forEach { (date,amount) -> Column(horizontalAlignment=Alignment.CenterHorizontally) {
                        if(!hidden) Text(Money.compact(amount),fontSize=9.sp)
                        Box(Modifier.padding(vertical=6.dp).width(22.dp).height((80f*amount/max).coerceAtLeast(3f).dp).background(if(date==today) FinBlue else MaterialTheme.colorScheme.primary.copy(alpha=.55f),RoundedCornerShape(topStart=5.dp,topEnd=5.dp)))
                        Text(date.dayOfWeek.getDisplayName(TextStyle.SHORT,Locale("id")),fontSize=10.sp)
                    } }
                }
            }
        }
        item { SectionTitle("Status Anggaran & Peringatan") }
        if(budgets.isEmpty()) item { TextButton(onClick={goTo("budget")}){Text("Atur batas anggaran pertama") } }
        items(budgets.take(3),key={it.category}) { b ->
            val used=report.summary.byCategory.firstOrNull { it.first.name==b.category }?.second?:0L
            FinTrackCard { LabeledValue(Category.fromName(b.category).label,if(hidden) "••••" else "${used*100/b.amount}%")
                LinearProgressIndicator(progress={ (used.toFloat()/b.amount).coerceIn(0f,1f) },modifier=Modifier.fillMaxWidth(),color=if(used>=b.amount) MaterialTheme.colorScheme.error else FinGreen)
                Text("${money(used)} / ${money(b.amount)}",fontSize=12.sp)
            }
        }
        if(s.draftCount>0) item { TextButton(onClick={goTo("tx")}){Text("${s.draftCount} draf notifikasi menunggu tinjauan") } }
        item { SectionTitle("Transaksi Terakhir") }
        if(s.recent.isEmpty()) item { EmptyNote("Belum ada transaksi. Catat pemasukan atau pengeluaran pertamamu.") }
        items(s.recent,key={it.id}) { t -> FinTrackCard {
            Text(t.note.ifBlank { Category.fromName(t.category).label },fontWeight=FontWeight.SemiBold)
            LabeledValue(if(t.transferGroup!=null) "Transfer internal" else Category.fromName(t.category).label,money(t.amount))
        } }
        item { TextButton(onClick={goTo("obl")}) { Text("Lihat tagihan & cicilan →") } }
    }
}
