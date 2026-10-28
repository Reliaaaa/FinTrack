package id.fintrack.app.domain

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

enum class ObligationKind(val label: String) {
    CICILAN("Cicilan"),
    PAYLATER("Paylater"),
    ARISAN("Arisan"),
    TAGIHAN("Tagihan rutin"),
    ZAKAT("Zakat dan sedekah");

    companion object {
        fun fromName(name: String): ObligationKind = values().firstOrNull { it.name == name } ?: TAGIHAN
    }
}

/** Kewajiban bulanan. remaining = jumlah bayar tersisa, -1 berarti tanpa batas. */
data class ObligationSpec(
    val amount: Long,
    val dueDay: Int,
    val remaining: Int = -1,
    /** Bulan terakhir yang sudah dibayar. Jatuh tempo pada bulan ini atau sebelumnya dilewati. */
    val paidThrough: YearMonth? = null
)

object ObligationMath {
    /** Tanggal jatuh tempo berikutnya, termasuk hari ini. Hari 31 di bulan pendek jadi hari terakhir bulan itu. */
    fun nextDue(today: LocalDate, dueDay: Int): LocalDate {
        val thisMonth = dueDateIn(YearMonth.from(today), dueDay)
        return if (!thisMonth.isBefore(today)) thisMonth else dueDateIn(YearMonth.from(today).plusMonths(1), dueDay)
    }

    fun dueDateIn(month: YearMonth, dueDay: Int): LocalDate {
        val day = dueDay.coerceIn(1, month.lengthOfMonth())
        return month.atDay(day)
    }

    /** Semua tanggal bayar dalam [from, to], dibatasi sisa pembayaran. */
    fun occurrences(from: LocalDate, to: LocalDate, spec: ObligationSpec): List<LocalDate> {
        if (to.isBefore(from) || spec.remaining == 0) return emptyList()
        val result = ArrayList<LocalDate>()
        var date = nextDue(from, spec.dueDay)
        while (!date.isAfter(to)) {
            if (spec.remaining >= 0 && result.size >= spec.remaining) break
            val paid = spec.paidThrough != null && !YearMonth.from(date).isAfter(spec.paidThrough)
            if (!paid) result.add(date)
            date = dueDateIn(YearMonth.from(date).plusMonths(1), spec.dueDay)
        }
        return result
    }

    fun totalDue(from: LocalDate, to: LocalDate, specs: List<ObligationSpec>): Long =
        specs.sumOf { s -> occurrences(from, to, s).size.toLong() * s.amount }

    fun monthlyLoad(specs: List<ObligationSpec>): Long =
        specs.filter { it.remaining != 0 }.sumOf { it.amount }

    /** Sisa utang dari kewajiban yang punya batas pembayaran (cicilan, paylater). */
    fun remainingLiability(specs: List<ObligationSpec>): Long =
        specs.filter { it.remaining > 0 }.sumOf { it.amount * it.remaining }

    /** Rasio beban terhadap pemasukan bulanan, dalam persen. Null jika pemasukan nol. */
    fun loadRatio(specs: List<ObligationSpec>, monthlyIncome: Long): Double? =
        if (monthlyIncome <= 0) null else monthlyLoad(specs) * 100.0 / monthlyIncome

    /** Batas 30 persen adalah patokan umum dalam perencanaan keuangan, bukan aturan hukum. */
    const val LOAD_RATIO_WARNING = 30.0
}
