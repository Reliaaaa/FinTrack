package id.fintrack.app.data

import android.content.Context

data class Settings(
    val incomeDay: Int,
    val monthlyIncome: Long,
    val notifCapture: Boolean
)

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)

    var incomeDay: Int
        get() = sp.getInt("income_day", 25)
        set(v) = sp.edit().putInt("income_day", v.coerceIn(1, 31)).apply()

    var monthlyIncome: Long
        get() = sp.getLong("monthly_income", 0L)
        set(v) = sp.edit().putLong("monthly_income", v.coerceAtLeast(0L)).apply()

    var notifCapture: Boolean
        get() = sp.getBoolean("notif_capture", false)
        set(v) = sp.edit().putBoolean("notif_capture", v).apply()

    fun snapshot() = Settings(incomeDay, monthlyIncome, notifCapture)
}
