package id.fintrack.app.domain

enum class Category(val label: String) {
    MAKAN("Makan"),
    TRANSPORT("Transportasi"),
    BELANJA("Belanja"),
    TAGIHAN("Tagihan"),
    HIBURAN("Hiburan"),
    KESEHATAN("Kesehatan"),
    PENDIDIKAN("Pendidikan"),
    CICILAN("Cicilan"),
    ZAKAT("Zakat dan sedekah"),
    TRANSFER("Transfer"),
    GAJI("Gaji"),
    LAINNYA("Lainnya");

    companion object {
        fun fromName(name: String): Category = values().firstOrNull { it.name == name } ?: LAINNYA
    }
}

/** Kategori otomatis berbasis kata kunci. Berjalan di perangkat, tanpa jaringan. */
object Categorizer {
    private val rules: List<Pair<Category, List<String>>> = listOf(
        Category.GAJI to listOf("gaji", "salary", "payroll", "thr"),
        Category.CICILAN to listOf("cicilan", "kredivo", "akulaku", "spaylater", "gopaylater", "paylater", "angsuran"),
        Category.ZAKAT to listOf("zakat", "infaq", "infak", "sedekah", "baznas", "kitabisa"),
        Category.TRANSPORT to listOf("grab", "gojek", "gocar", "gobike", "maxim", "bluebird", "krl", "mrt", "tol", "parkir", "bensin", "pertamina", "kai"),
        Category.MAKAN to listOf("makan", "bakmi", "kfc", "mcd", "mcdonald", "starbucks", "kopi", "warung", "resto", "gofood", "grabfood", "shopeefood", "a&w", "ah pek", "bakso", "nasi"),
        Category.TAGIHAN to listOf("pln", "token listrik", "pdam", "telkom", "indihome", "bpjs", "pulsa", "paket data", "internet", "wifi"),
        Category.HIBURAN to listOf("netflix", "spotify", "youtube", "bioskop", "xxi", "cgv", "imax", "steam", "game"),
        Category.KESEHATAN to listOf("apotek", "klinik", "rumah sakit", "halodoc", "dokter", "kimia farma"),
        Category.PENDIDIKAN to listOf("sekolah", "spp", "ukt", "kuliah", "universitas", "kursus", "udemy", "buku"),
        Category.BELANJA to listOf("tokopedia", "shopee", "tiktok", "lazada", "indomaret", "alfamart", "belanja", "toko"),
        Category.TRANSFER to listOf("transfer", "tarik tunai", "top up", "topup")
    )

    fun categorize(text: String): Category {
        val t = text.lowercase()
        for ((category, words) in rules) {
            if (words.any { t.contains(it) }) return category
        }
        return Category.LAINNYA
    }
}
