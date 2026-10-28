package id.fintrack.app.domain
import java.time.LocalDate
import java.time.YearMonth

data class ForecastPoint(val date: LocalDate, val balance: Long)
data class ForecastResult(val points: List<ForecastPoint>) {
    val lowest: ForecastPoint get() = points.minBy { it.balance }
    val firstDeficit: ForecastPoint? get() = points.firstOrNull { it.balance < 0 }
}
object Forecast {
    /** Starts tomorrow: current balance already includes today's booked activity. */
    fun calculate(today: LocalDate, balance: Long, monthlyIncome: Long, incomeDay: Int,
                  dailyExpense: Long, obligations: List<ObligationSpec>, purchase: Long = 0,
                  days: Int = 90): ForecastResult {
        require(monthlyIncome >= 0 && dailyExpense >= 0 && purchase >= 0 && days in 1..365)
        val end = today.plusDays(days.toLong())
        // Count from today so a finite installment due today is not repeated an extra month.
        val dues = obligations.flatMap { o -> ObligationMath.occurrences(today, end, o).map { it to o.amount } }
            .groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }
        var running = balance - purchase - (dues[today] ?: 0L)
        val points = mutableListOf(ForecastPoint(today, running))
        for (i in 1..days) {
            val date = today.plusDays(i.toLong())
            if (date == ObligationMath.dueDateIn(YearMonth.from(date), incomeDay)) running += monthlyIncome
            running -= dailyExpense + (dues[date] ?: 0L)
            points.add(ForecastPoint(date, running))
        }
        return ForecastResult(points)
    }
}
