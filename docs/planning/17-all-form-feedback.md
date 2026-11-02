# Umpan balik seluruh formulir persisten — 2 November 2026

Melanjutkan perbaikan transaksi manual pada 6d296ca. Scope adalah alur simpan yang sudah tersedia, bukan seluruh backlog fullstack atau seluruh layar rencana Canva.

## Perubahan

OperationStore menyediakan status terpisah per aksi: sedang memproses, berhasil, atau gagal. Penguncian sebelum coroutine berjalan menolak ketukan berulang sampai hasil berhasil dikonsumsi; kegagalan boleh dicoba ulang. FormDialog memakai status tersebut untuk menonaktifkan Simpan/Batal, menampilkan progres dan error, serta menutup hanya setelah berhasil. Input tetap tersimpan selama komposisi formulir hidup.

| Alur | Penanganan |
|---|---|
| Rekening; aset; harga aset | Dialog tetap terbuka setelah gagal |
| Anggaran; tambah/ubah target tabungan | Ditutup hanya setelah berhasil |
| Jadwal tagihan; catat/kaitkan pembayaran; pembatalan pembayaran | Pilihan tetap tersedia setelah gagal |
| Konfirmasi draft | Ditutup hanya setelah berhasil; buang draft menampilkan error |
| Hapus rekening/aset/transaksi/anggaran/target/tagihan; hapus seluruh data | Konfirmasi menunggu hasil, error tetap terlihat |
| Transfer | Nominal dikosongkan hanya setelah berhasil; pilihan tidak berubah selama proses |
| Impor CSV | Pratinjau dipertahankan jika gagal; baca berkas dan commit tidak dapat tumpang tindih |
| Pengaturan; sakelar tangkap notifikasi; ekspor CSV | Status operasi, penolakan ketukan berulang, dan error terlihat di layar |
| Tambah/ubah transaksi manual | Tetap memakai perbaikan 6d296ca |

Kalkulator tanpa penyimpanan dan pengambilan harga/chart tidak diubah. Schema Room v2 yang sebelumnya belum dihasilkan kini diekspor dari kompilasi aktual dan ikut disimpan.

## Validasi

- 11 pemeriksaan OperationStore lulus: ketukan ganda sebelum/sesudah suspensi, reset selama proses, isolasi aksi, sukses, konsumsi hasil, gagal lalu coba ulang, serta cancellation.
- 101 pemeriksaan domain lulus.
- Migrasi SQLite v1 ke v2 lulus, termasuk marker legacy tanpa debit, constraint unik, serta kecocokan kolom, primary key, foreign key, dan indeks terhadap ekspor Room v2.
- `assembleDebug` menghasilkan APK; `compileDebugAndroidTestKotlin` berhasil. APK `app/build/outputs/apk/debug/app-debug.apk`, SHA-256 `2e737879953564069fd40418bbc984b6a67fe474919bf18bc8da8773273acd95`.
- `lintAnalyzeDebug`, `lintAnalyzeDebugAndroidTest`, dan `lintAnalyzeDebugUnitTest` selesai, tetapi `lintReportDebug` gagal: tidak dapat menulis `app/build/reports/lint-results-debug.txt`. Karena itu perintah validasi gabungan masih gagal dan lint belum dinyatakan lulus. Log lokal: `build/forms-validation-online.log`, `build/forms-validation-final.log`. Jangan mematikan gate lint untuk menyatakan build validation berhasil.
- Pemeriksaan interaksi perangkat belum dijalankan: `adb devices` tidak menemukan perangkat. Kompilasi tes Android tidak berarti tes instrumentasi telah dijalankan.

Build lokal menggunakan `tools/build-debug.ps1 -JavaHome <JDK> -AndroidSdk <SDK>`. Tambahkan `-Offline` setelah dependensi tersedia. Script menyiapkan keystore debug di folder build yang diabaikan Git; tidak menggunakan signing produksi. Build cache bersama dinonaktifkan untuk menghindari hambatan akses lingkungan ini.

## Gate berikutnya

Uji setiap alur pada tabel dengan sukses, kegagalan repository/storage, ketukan berulang, batal, dan buka ulang. Pastikan tidak ada perubahan saldo/record setelah gagal dan input dapat dicoba ulang. Khusus impor: uji gagal commit lalu ulang tanpa memilih CSV lagi serta cegah penggantian CSV selama commit.

Retensi input lintas rotasi/process death belum diselesaikan; pengaman ketukan bukan idempotency persisten. Risiko replay bayar–batal–bayar pada dokumen 13 masih terbuka. Ekspor gagal dapat meninggalkan berkas tujuan parsial; pengguna harus mengulang ekspor. Pengaturan memakai mekanisme SharedPreferences yang sudah ada, bukan jaminan durability baru.

Belum ada verifikasi visual Android terhadap Canva, scan keamanan baru, atau deployment. Publikasi branch/draft PR dilaporkan terpisah dari hasil validasi ini. Status siap rilis masih membutuhkan lint selesai, uji perangkat dan penyelesaian blocker yang telah dicatat.
