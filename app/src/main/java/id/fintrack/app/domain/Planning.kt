package id.fintrack.app.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class ZakatResult(val nisab: Long, val reached: Boolean, val due: Long)

/**
 * Zakat maal dengan ketentuan umum: nisab setara 85 gram emas, tarif 2,5 persen,
 * harta sudah dimiliki satu tahun (haul). Lembaga zakat Anda menentukan rincian akhir.
 */
object Zakat {
    const val NISAB_GOLD_GRAMS = 85.0
    const val RATE_PERCENT = 2.5

    fun nisab(goldPricePerGram: Long): Long = Math.round(NISAB_GOLD_GRAMS * goldPricePerGram)

    fun maal(zakatableWealth: Long, goldPricePerGram: Long): ZakatResult {
        val nisab = nisab(goldPricePerGram)
        val reached = goldPricePerGram > 0 && zakatableWealth >= nisab
        val due = if (reached) Math.round(zakatableWealth * RATE_PERCENT / 100.0) else 0L
        return ZakatResult(nisab, reached, due)
    }
}

/** Dana musiman: THR, Lebaran, tahun ajaran baru, liburan. */
object SeasonalGoal {
    /** Setoran bulanan agar target tercapai pada tanggal due. Minimal satu bulan. */
    fun monthlyContribution(target: Long, saved: Long, today: LocalDate, due: LocalDate): Long {
        val remaining = target - saved
        if (remaining <= 0) return 0L
        val months = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), due.withDayOfMonth(1)).toInt()
        val m = months.coerceAtLeast(1)
        return (remaining + m - 1) / m
    }
}

data class TxRecord(val type: TxType, val amount: Long, val category: Category, val date: LocalDate)

data class MonthSummary(
    val income: Long,
    val expense: Long,
    val byCategory: List<Pair<Category, Long>>
) {
    val net: Long get() = income - expense
}

object Reports {
    fun month(records: List<TxRecord>, year: Int, month: Int): MonthSummary {
        val inMonth = records.filter { it.date.year == year && it.date.monthValue == month }
        val income = inMonth.filter { it.type == TxType.INCOME }.sumOf { it.amount }
        val expenses = inMonth.filter { it.type == TxType.EXPENSE }
        val byCat = expenses.groupBy { it.category }
            .map { (c, list) -> c to list.sumOf { it.amount } }
            .sortedByDescending { it.second }
        return MonthSummary(income, expenses.sumOf { it.amount }, byCat)
    }
}
