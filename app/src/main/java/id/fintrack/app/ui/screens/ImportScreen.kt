package id.fintrack.app.ui.screens
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.ui.*
import id.fintrack.app.domain.Money
import id.fintrack.app.vm.MainViewModel
@Composable fun ImportScreen(vm: MainViewModel,onBack: () -> Unit) {
    val accounts by vm.accounts.collectAsStateWithLifecycle()
    val rows by vm.importPreview.collectAsStateWithLifecycle()
    val status by vm.importStatus.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<Long?>(null) }
    var confirm by remember { mutableStateOf(false) }
    val account=accounts.firstOrNull { it.id==selected }
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { it?.let(vm::readImport) }
    ScreenScaffold("Impor Mutasi CSV",onBack) { pad ->
        LazyColumn(Modifier.padding(pad),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            item { FinTrackCard { Text("Tinjau sebelum disimpan",style=MaterialTheme.typography.titleLarge);Text("Gunakan CSV UTF-8 dengan header tanggal,jenis,jumlah,kategori,catatan. Tanggal YYYY-MM-DD, jenis INCOME atau EXPENSE, jumlah rupiah bulat. Maksimal 2.000 baris dan 1 MB.");Text("Impor menambah atau mengurangi saldo rekening terpilih. Siapkan saldo awal sebelum periode mutasi agar saldo tidak terhitung dua kali.",style=MaterialTheme.typography.bodySmall) } }
            item { DropdownField("Rekening tujuan",accounts,account,{it.name}){selected=it.id};Button(onClick={picker.launch(arrayOf("text/*","application/csv","application/vnd.ms-excel"))}){Text("Pilih berkas CSV")}; Text(status) }
            if(rows.isNotEmpty()) item { Button(onClick={confirm=true},enabled=account!=null){Text("Impor ${rows.size} transaksi")};Text("Baris identik dilewati. Tambahkan kolom ref yang berbeda untuk dua transaksi yang benar-benar berbeda tetapi memiliki isi sama.",style=MaterialTheme.typography.bodySmall) }
            items(rows.take(100)) { r -> FinTrackCard { LabeledValue(r.date.toString(),Money.format(r.amount));Text("${r.type} • ${r.note}") } }
            if(rows.size>100) item { Text("Pratinjau menampilkan 100 baris pertama. Seluruh ${rows.size} baris sudah diperiksa.") }
        }
    }
    if(confirm) AlertDialog(onDismissRequest={confirm=false},title={Text("Impor ke ${account?.name}?")},text={Text("Saldo rekening akan berubah sesuai transaksi. Duplikat impor ke rekening yang sama akan dilewati.")},confirmButton={TextButton(onClick={selected?.let(vm::commitImport);confirm=false}){Text("Impor")}},dismissButton={TextButton(onClick={confirm=false}){Text("Batal")}})
}
