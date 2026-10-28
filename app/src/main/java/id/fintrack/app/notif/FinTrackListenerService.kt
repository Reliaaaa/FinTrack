package id.fintrack.app.notif

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import id.fintrack.app.FinTrackApplication
import id.fintrack.app.domain.Categorizer
import id.fintrack.app.domain.NotificationParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Membaca notifikasi aplikasi bank dan dompet digital yang dikenal, lalu menyimpan draf transaksi.
 * Teks mentah notifikasi tidak disimpan. Hanya nominal, jenis dan nama tujuan hasil parsing.
 * Layanan tidak berbuat apa pun bila pengguna belum menyalakan fitur di Pengaturan.
 */
class FinTrackListenerService : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val app = application as FinTrackApplication
        if (!app.prefs.notifCapture) return
        if (sbn.packageName !in NotificationParser.KNOWN_SOURCES) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = (extras.getCharSequence(Notification.EXTRA_BIG_TEXT) ?: extras.getCharSequence(Notification.EXTRA_TEXT))?.toString()
        val parsed = NotificationParser.parse(sbn.packageName, title, text) ?: return

        val rawKey = "${sbn.key}|${sbn.postTime}|${parsed.type}|${parsed.amount}|${parsed.description}"
        val fingerprint = java.security.MessageDigest.getInstance("SHA-256").digest(rawKey.toByteArray()).joinToString("") { "%02x".format(it) }
        val category = Categorizer.categorize(parsed.description)
        scope.launch {
            try { app.repository.addDraft(parsed, sbn.postTime, fingerprint, category) }
            catch(e: kotlinx.coroutines.CancellationException) { throw e }
            catch(_: Exception) { /* No sensitive notification payload in logs. */ }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
