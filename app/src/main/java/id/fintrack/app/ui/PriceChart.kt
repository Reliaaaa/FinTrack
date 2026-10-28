package id.fintrack.app.ui

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Grafik candle memakai TradingView Lightweight Charts (Apache-2.0).
 * Halaman dan pustaka dibaca dari assets lalu dimuat sebagai string, tanpa akses file dan tanpa jaringan.
 * candlesJson: [{time, open, high, low, close}] dengan time dalam detik.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PriceChart(candlesJson: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val ready = remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                settings.domStorageEnabled = false
                setBackgroundColor(0)
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        ready.value = true
                    }
                }
                val assets = context.assets
                val page = assets.open("chart/chart.html").bufferedReader().use { it.readText() }
                val lib = assets.open("chart/lightweight-charts.js").bufferedReader().use { it.readText() }
                val html = page.replace(
                    "<script src=\"lightweight-charts.js\"></script>",
                    "<script>$lib</script>"
                )
                loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            }
        },
        update = { web ->
            if (ready.value && candlesJson != null) {
                web.evaluateJavascript("setCandles($candlesJson)", null)
            }
        }
    )
}
