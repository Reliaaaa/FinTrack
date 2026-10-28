package id.fintrack.app.domain

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

data class SafeToSpend(
    val dailyAllowance: Long,
    val spendable: Long,
    val reserved: Long,
    val daysLeft: Int,
    val horizonEnd: LocalDate
) {
    val isDeficit: Boolean get() = spendable < 0
}

object SafeToSpendCalc {
    /**
     * Uang yang aman dipakai per hari sampai pemasukan berikutnya.
     * liquid = saldo rekening yang bisa dipakai (kas, bank, dompet digital).
     * Kewajiban yang jatuh tempo sebelum pemasukan berikutnya dicadangkan lebih dulu.
     * Tanpa tanggal pemasukan yang valid, horizon adalah akhir bulan ini.
     */
    fun compute(
        liquid: Long,
        obligations: List<ObligationSpec>,
        today: LocalDate,
        nextIncome: LocalDate?
    ): SafeToSpend {
        val horizonEnd = if (nextIncome != null && nextIncome.isAfter(today)) {
            nextIncome.minusDays(1)
        } else {
            YearMonth.from(today).atEndOfMonth()
        }
        val daysLeft = (ChronoUnit.DAYS.between(today, horizonEnd).toInt() + 1).coerceAtLeast(1)
        val reserved = ObligationMath.totalDue(today, horizonEnd, obligations)
        val spendable = liquid - reserved
        val daily = if (spendable > 0) spendable / daysLeft else 0L
        return SafeToSpend(daily, spendable, reserved, daysLeft, horizonEnd)
    }

    /** Tanggal gajian berikutnya dari hari gajian bulanan. */
    fun nextIncomeDate(today: LocalDate, incomeDay: Int): LocalDate {
        val thisMonth = ObligationMath.dueDateIn(YearMonth.from(today), incomeDay)
        return if (thisMonth.isAfter(today)) thisMonth
        else ObligationMath.dueDateIn(YearMonth.from(today).plusMonths(1), incomeDay)
    }
}
