package id.fintrack.app.domain

enum class TxType { INCOME, EXPENSE }

data class ParsedTx(
    val type: TxType,
    val amount: Long,
    val description: String,
    val sourceLabel: String
)

/**
 * Mengubah teks notifikasi aplikasi bank atau dompet digital menjadi draf transaksi.
 *
 * Pola kata di bawah adalah heuristik umum. Format pesan asli tiap bank berbeda
 * dan berubah dari waktu ke waktu. Uji dengan notifikasi nyata dari perangkat
 * Anda, lalu sesuaikan daftar kata. Nama paket aplikasi juga perlu dicek di Play Store.
 */
object NotificationParser {
    /** Nama paket aplikasi sumber. BELUM DIVERIFIKASI, cek satu per satu sebelum rilis. */
    val KNOWN_SOURCES: Map<String, String> = mapOf(
        "com.bca" to "BCA mobile",
        "com.bca.mybca.omni.android" to "myBCA",
        "com.bcadigital.blu" to "blu",
        "id.bmri.livin" to "Livin",
        "id.co.bri.brimo" to "BRImo",
        "src.com.bni" to "BNI Mobile",
        "com.btpn.dc" to "Jenius",
        "com.jago.digitalBanking" to "Jago",
        "id.co.bankbkemobile.digitalbank" to "SeaBank",
        "com.gojek.app" to "GoPay",
        "ovo.id" to "OVO",
        "id.dana" to "DANA",
        "com.shopee.id" to "ShopeePay",
        "com.telkom.mwallet" to "LinkAja"
    )

    private val amountRegex = Regex("""(?i)(?:rp|idr)\.?\s?([0-9][0-9.,]*[0-9]|[0-9])""")
    private val merchantRegex = Regex("""(?i)\b(?:ke|di|dari|kepada|untuk)\s+([A-Za-z0-9][A-Za-z0-9 &.'-]{2,40})""")
    private val merchantStop = Regex("""(?i)\s+(?:pada|tgl|tanggal|sebesar|senilai|sejumlah|dengan|rp|idr|melalui|via|berhasil)\b""")

    private val rejectWords = listOf("otp", "kode verifikasi", "jangan berikan", "promo", "diskon", "voucher", "hingga rp", "up to", "limit")
    private val incomeWords = listOf("menerima", "diterima", "dana masuk", "saldo masuk", "transfer masuk", "kredit", "refund", "pengembalian dana")
    private val expenseWords = listOf("pembayaran", "dibayar", "bayar", "transfer berhasil", "transfer ke", "berhasil mengirim", "terkirim", "pembelian", "debit", "tarik tunai", "belanja", "top up berhasil", "topup berhasil")

    fun parse(packageName: String, title: String?, text: String?): ParsedTx? {
        val label = KNOWN_SOURCES[packageName] ?: return null
        val full = listOfNotNull(title, text).joinToString(" ").trim()
        if (full.isEmpty()) return null
        val lower = full.lowercase()
        if (rejectWords.any { lower.contains(it) }) return null

        val amountText = amountRegex.find(full)?.groupValues?.get(1) ?: return null
        val amount = Money.parse(amountText) ?: return null
        if (amount <= 0) return null

        val type = when {
            incomeWords.any { lower.contains(it) } -> TxType.INCOME
            expenseWords.any { lower.contains(it) } -> TxType.EXPENSE
            else -> return null
        }
        return ParsedTx(type, amount, guessDescription(full, title, label), label)
    }

    private fun guessDescription(full: String, title: String?, label: String): String {
        val raw = merchantRegex.find(full)?.groupValues?.get(1)?.trim()
        if (raw != null) {
            val cut = merchantStop.split(raw).first().trim().trimEnd('.', ',', '-')
            if (cut.length >= 3) return cut
        }
        val t = title?.trim().orEmpty()
        return if (t.isNotEmpty()) t else label
    }
}
