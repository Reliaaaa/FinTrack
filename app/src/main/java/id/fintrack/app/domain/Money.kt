package id.fintrack.app.domain

/** Format dan baca nominal rupiah. Semua nilai disimpan sebagai Long rupiah penuh. */
object Money {
    fun format(amount: Long): String {
        val sign = if (amount < 0) "-" else ""
        val digits = Math.abs(amount).toString()
        val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
        return "${sign}Rp$grouped"
    }

    /** Versi ringkas untuk ruang sempit: 1,5 jt, 250 rb. */
    fun compact(amount: Long): String {
        val abs = Math.abs(amount)
        val sign = if (amount < 0) "-" else ""
        return when {
            abs >= 1_000_000_000 -> sign + trim(abs / 1_000_000_000.0) + " M"
            abs >= 1_000_000 -> sign + trim(abs / 1_000_000.0) + " jt"
            abs >= 1_000 -> sign + trim(abs / 1_000.0) + " rb"
            else -> "$sign$abs"
        }
    }

    private fun trim(v: Double): String {
        val r = Math.round(v * 10) / 10.0
        return if (r == Math.floor(r)) r.toLong().toString() else r.toString().replace('.', ',')
    }

    /**
     * Baca teks nominal. Contoh: "Rp 1.250.000,00", "Rp1,250,000.00", "50rb", "1,5jt".
     * Mengembalikan null jika teks tidak berbentuk angka.
     */
    fun parse(raw: String): Long? {
        var s = raw.trim().lowercase().replace("rp", "").replace("idr", "").replace(" ", "")
        if (s.isEmpty()) return null
        var mult = 1L
        val suffixes = listOf("juta" to 1_000_000L, "jt" to 1_000_000L, "ribu" to 1_000L, "rb" to 1_000L, "k" to 1_000L)
        for ((suffix, m) in suffixes) {
            if (s.endsWith(suffix)) {
                mult = m
                s = s.dropLast(suffix.length)
                break
            }
        }
        val value = toDecimal(s) ?: return null
        return Math.round(value * mult)
    }

    private fun toDecimal(n: String): Double? {
        if (n.isEmpty() || !n.all { it.isDigit() || it == '.' || it == ',' }) return null
        val lastDot = n.lastIndexOf('.')
        val lastComma = n.lastIndexOf(',')
        val decimalIdx = when {
            lastDot >= 0 && lastComma >= 0 -> maxOf(lastDot, lastComma)
            lastDot >= 0 -> singleSeparatorIndex(n, '.')
            lastComma >= 0 -> singleSeparatorIndex(n, ',')
            else -> -1
        }
        val cleaned = buildString {
            n.forEachIndexed { i, ch ->
                if (ch.isDigit()) append(ch) else if (i == decimalIdx) append('.')
            }
        }
        return cleaned.toDoubleOrNull()
    }

    /** Satu pemisah dengan tepat tiga digit sesudahnya dianggap pemisah ribuan. */
    private fun singleSeparatorIndex(n: String, c: Char): Int {
        val count = n.count { it == c }
        val idx = n.lastIndexOf(c)
        val after = n.length - idx - 1
        return if (count == 1 && after != 3) idx else -1
    }
}
