package id.fintrack.app.domain

data class SplitItem(val name: String, val price: Long, val sharedBy: List<String>)

object SplitBill {
    /**
     * Bagi daftar item ke peserta. Pajak dan layanan (extras) dibagi sebanding subtotal tiap orang.
     * Diskon dimasukkan sebagai nilai negatif pada extras. Jumlah hasil selalu sama dengan total tagihan.
     */
    fun split(items: List<SplitItem>, extras: Long): Map<String, Long> {
        val subtotals = LinkedHashMap<String, Long>()
        for (item in items) {
            if (item.sharedBy.isEmpty()) continue
            val parts = distribute(item.price, item.sharedBy.associateWith { 1L })
            for ((name, part) in parts) subtotals[name] = (subtotals[name] ?: 0L) + part
        }
        if (subtotals.isEmpty()) return emptyMap()
        val extraParts = distribute(extras, subtotals)
        return subtotals.mapValues { (name, sub) -> sub + (extraParts[name] ?: 0L) }
    }

    /** Bagi total menurut bobot. Sisa rupiah diberikan ke bobot dengan sisa terbesar. */
    fun distribute(total: Long, weights: Map<String, Long>): Map<String, Long> {
        if (weights.isEmpty()) return emptyMap()
        val sign = if (total < 0) -1L else 1L
        val t = Math.abs(total)
        val effective = if (weights.values.sum() <= 0L) weights.mapValues { 1L } else weights
        val sum = effective.values.sum()
        val result = LinkedHashMap<String, Long>()
        val remainders = ArrayList<Pair<String, Long>>()
        var assigned = 0L
        for ((key, w) in effective) {
            val share = t * w / sum
            result[key] = share
            assigned += share
            remainders.add(key to (t * w % sum))
        }
        var left = t - assigned
        for ((key, _) in remainders.sortedByDescending { it.second }) {
            if (left <= 0) break
            result[key] = (result[key] ?: 0L) + 1
            left--
        }
        return result.mapValues { it.value * sign }
    }
}
