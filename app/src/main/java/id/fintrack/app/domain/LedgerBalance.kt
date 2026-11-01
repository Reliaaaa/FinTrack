package id.fintrack.app.domain

/** Checked integer arithmetic; aggregate balance policy is separate from input limits. */
object LedgerBalance {
    fun apply(balance: Long, delta: Long): Long = try {
        Math.addExact(balance, delta)
    } catch (error: ArithmeticException) {
        throw IllegalArgumentException("Perubahan saldo melewati batas bilangan yang didukung", error)
    }
}
