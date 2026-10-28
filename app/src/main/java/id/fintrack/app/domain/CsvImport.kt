package id.fintrack.app.domain
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class ImportRow(val date: LocalDate, val type: TxType, val amount: Long, val category: Category, val note: String, val reference: String) {
    fun fingerprint(accountId: Long): String {
        val text=listOf(accountId.toString(),date.toString(),type.name,amount.toString(),category.name,note,reference).joinToString("\u001f")
        return "csv:"+java.security.MessageDigest.getInstance("SHA-256").digest(text.toByteArray()).joinToString("") { "%02x".format(it) }
    }
    fun timestamp(): Long = date.atTime(12,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
object CsvImport {
    fun parse(text: String): List<ImportRow> {
        require(text.length<=1_000_000) { "Berkas maksimal 1 MB" }
        val rows=records(text.removePrefix("\uFEFF"))
        require(rows.isNotEmpty()) { "CSV kosong" }
        val header=rows.first().map { it.trim().lowercase() }
        val required=listOf("tanggal","jenis","jumlah","kategori","catatan")
        require(required.all { it in header }) { "Header wajib: tanggal,jenis,jumlah,kategori,catatan" }
        return rows.drop(1).filter { it.any(String::isNotBlank) }.mapIndexed { index, cells ->
            fun cell(key: String): String = cells.getOrNull(header.indexOf(key))?.trim().orEmpty()
            try {
                val rawDate=cell("tanggal")
                val date=if(rawDate.length==10) LocalDate.parse(rawDate) else LocalDateTime.parse(rawDate).toLocalDate()
                require(!date.isAfter(LocalDate.now())) { "Tanggal masa depan belum didukung" }
                val type=TxType.valueOf(cell("jenis").uppercase())
                val amount=cell("jumlah").toLong()
                require(amount in 1..1_000_000_000_000_000L) { "Jumlah harus rupiah bulat positif" }
                val cat=Category.values().firstOrNull { it.name.equals(cell("kategori"),true)||it.label.equals(cell("kategori"),true) }?:Category.LAINNYA
                ImportRow(date,type,amount,cat,cell("catatan").take(500),cell("ref").take(100))
            } catch(e: Exception) { throw IllegalArgumentException("Baris ${index+2}: ${e.message ?: "format salah"}") }
        }.also { require(it.size in 1..2000) { "CSV harus berisi 1 sampai 2000 transaksi" } }
    }
    private fun records(s: String): List<List<String>> {
        val rows=mutableListOf<List<String>>();var row=mutableListOf<String>();val field=StringBuilder();var quoted=false;var i=0
        while(i<s.length) {
            val c=s[i]
            when {
                c=='"' -> { if(quoted && i+1<s.length && s[i+1]=='"') { field.append('"');i++ } else quoted=!quoted }
                c==',' && !quoted -> { row.add(field.toString());field.clear() }
                c=='\n' && !quoted -> { row.add(field.toString().removeSuffix("\r"));rows.add(row);row=mutableListOf();field.clear() }
                else -> field.append(c)
            };i++
        }
        require(!quoted) { "Tanda kutip CSV tidak ditutup" }
        if(field.isNotEmpty() || row.isNotEmpty()) { row.add(field.toString().removeSuffix("\r"));rows.add(row) }
        return rows
    }
}
