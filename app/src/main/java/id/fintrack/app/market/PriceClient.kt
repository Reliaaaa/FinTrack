package id.fintrack.app.market

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Harga kripto dari API publik Indodax, tanpa kunci API.
 * Bentuk respons di bawah mengikuti dokumentasi publik Indodax saat riset dan BELUM diuji
 * terhadap server langsung dari proyek ini. Cek respons nyata bila parsing gagal.
 * Batas Indodax saat riset: 180 permintaan per menit.
 */
object PriceClient {
    private const val BASE = "https://indodax.com"

    /** Pasangan (huruf kecil, misal btc_idr) ke harga terakhir dalam rupiah. */
    suspend fun indodaxLast(): Map<String, Long> = withContext(Dispatchers.IO) {
        val root = JSONObject(get("$BASE/api/ticker_all"))
        val tickers = root.getJSONObject("tickers")
        val out = HashMap<String, Long>()
        for (key in tickers.keys()) {
            val last = tickers.optJSONObject(key)?.optString("last")?.toDoubleOrNull() ?: continue
            if(last.isFinite() && last >= 0.5 && last <= 1e15) out[key.lowercase()] = Math.round(last)
        }
        out
    }

    /**
     * Candle harian untuk grafik. Hasil berupa string JSON array siap diberikan ke chart.html:
     * [{time, open, high, low, close}] dengan time dalam detik, urut naik tanpa duplikat.
     */
    suspend fun indodaxCandles(pair: String, days: Int = 120): String = withContext(Dispatchers.IO) {
        require(pair.matches(Regex("[a-zA-Z0-9_]{3,30}"))) { "Pasangan pasar tidak valid" }
        val symbol = pair.uppercase().replace("_", "")
        val to = System.currentTimeMillis() / 1000
        val from = to - days * 86_400L
        val arr = JSONArray(get("$BASE/tradingview/history_v2?symbol=$symbol&tf=1D&from=$from&to=$to"))
        val rows = ArrayList<JSONObject>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            var time = o.optLong("Time", o.optLong("time", 0L))
            if (time > 100_000_000_000L) time /= 1000
            if (time <= 0L) continue
            rows.add(
                JSONObject()
                    .put("time", time)
                    .put("open", o.optDouble("Open", o.optDouble("open", 0.0)))
                    .put("high", o.optDouble("High", o.optDouble("high", 0.0)))
                    .put("low", o.optDouble("Low", o.optDouble("low", 0.0)))
                    .put("close", o.optDouble("Close", o.optDouble("close", 0.0)))
            )
        }
        val sorted = rows.sortedBy { it.getLong("time") }.distinctBy { it.getLong("time") }
        require(sorted.isNotEmpty()) { "Data grafik belum tersedia untuk pasangan ini" }
        JSONArray(sorted).toString()
    }

    private fun get(url: String): String {
        val c = URL(url).openConnection() as HttpURLConnection
        try {
            c.connectTimeout = 10_000
            c.readTimeout = 15_000
            c.setRequestProperty("User-Agent", "FinTrack/0.1 (Android)")
            c.setRequestProperty("Accept", "application/json")
            if (c.responseCode !in 200..299) throw IOException("HTTP ${c.responseCode}")
            return c.inputStream.bufferedReader().use { it.readText() }
        } finally {
            c.disconnect()
        }
    }
}
