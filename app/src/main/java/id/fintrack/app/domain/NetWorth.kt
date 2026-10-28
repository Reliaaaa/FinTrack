package id.fintrack.app.domain

enum class AssetClass(val label: String, val unit: String) {
    KAS("Kas dan rekening", "Rp"),
    EMAS("Emas", "gram"),
    SAHAM("Saham", "lembar"),
    REKSA_DANA("Reksa dana", "unit"),
    KRIPTO("Kripto", "koin"),
    LAINNYA("Lainnya", "unit");

    companion object {
        fun fromName(name: String): AssetClass = values().firstOrNull { it.name == name } ?: LAINNYA
    }
}

data class HoldingValue(
    val name: String,
    val assetClass: AssetClass,
    val quantity: Double,
    val costBasis: Long,
    val unitPrice: Long
) {
    val value: Long get() = Math.round(quantity * unitPrice)
}

data class NetWorthSummary(
    val assets: Long,
    val liabilities: Long,
    val net: Long,
    val byClass: Map<AssetClass, Long>,
    val gainLoss: Long,
    val gainLossPercent: Double?
) {
    /** Porsi tiap kelas aset dalam persen. */
    fun allocation(): Map<AssetClass, Double> =
        if (assets <= 0) emptyMap() else byClass.mapValues { it.value * 100.0 / assets }
}

object NetWorth {
    /**
     * cash = total saldo rekening. liabilities = sisa kewajiban (cicilan, paylater).
     * Untung rugi hanya dihitung untuk aset yang punya modal (costBasis lebih dari nol).
     */
    fun summarize(holdings: List<HoldingValue>, cash: Long, liabilities: Long): NetWorthSummary {
        val byClass = LinkedHashMap<AssetClass, Long>()
        if (cash != 0L) byClass[AssetClass.KAS] = cash
        var gain = 0L
        var cost = 0L
        for (h in holdings) {
            byClass[h.assetClass] = (byClass[h.assetClass] ?: 0L) + h.value
            if (h.costBasis > 0) {
                gain += h.value - h.costBasis
                cost += h.costBasis
            }
        }
        val assets = byClass.values.sum()
        val pct = if (cost > 0) gain * 100.0 / cost else null
        return NetWorthSummary(assets, liabilities, assets - liabilities, byClass, gain, pct)
    }
}
