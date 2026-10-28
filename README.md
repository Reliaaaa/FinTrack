# FinTrack Finance Tracker

Aplikasi Android native Kotlin dan Jetpack Compose, berdasarkan proyek Arus dan desain FinTrack yang diberikan pengguna.

## Menjalankan

1. Ekstrak arsip ini. Buka direktori `fintrack-android` di Android Studio.
2. Gunakan JDK 17. Pasang Android SDK 35 dan Build Tools 35.0.0 melalui SDK Manager.
3. Tunggu Gradle sync. Pilih perangkat Android 8.0 (API 26) atau lebih baru.
4. Jalankan konfigurasi `app`.

Linux/macOS: `./gradlew :app:assembleDebug :app:lintDebug`

Windows: `gradlew.bat :app:assembleDebug :app:lintDebug`

APK dihasilkan pada `app/build/outputs/apk/debug/app-debug.apk`. APK debug yang disertakan dalam keluaran terpisah ditujukan untuk mencoba aplikasi. Untuk distribusi produksi, atur signing key milik sendiri dan selesaikan validasi perangkat.

## Fitur

- Dasbor navy dengan kartu saldo biru, nominal tersembunyi, tren tujuh hari, dan ringkasan anggaran.
- Rekening manual, kas, bank, dan e-wallet. Pemisahan uang harian dan saldo nonlikuid.
- Tambah, edit, hapus, cari, dan filter transaksi. Koreksi saldo atomik.
- Transfer internal berpasangan. Tidak dihitung sebagai pendapatan atau pengeluaran.
- Anggaran bulanan per kategori dan indikator pemakaian 80% / 100%.
- Target tabungan persisten dan estimasi setoran.
- Jadwal tagihan/cicilan serta batas aman belanja sampai gajian.
- Simulasi kas 90 hari, saldo terendah, dan tanggal defisit.
- Laporan bulanan per rekening, komposisi kategori, rasio tabungan, dan CSV.
- Impor CSV dengan pratinjau, validasi, dan fingerprint duplikat.
- Portofolio manual, untung/rugi, harga kripto Indodax, grafik TradingView Lightweight Charts.
- Draf notifikasi opsional dan eksperimental, wajib dikonfirmasi.
- Kalkulator bagi tagihan, dana musiman, dan zakat.

## Batas yang perlu dipahami

FinTrack mencatat keuangan. Tidak ada transfer uang nyata, eksekusi order broker, login bank/Stockbit, sinkronisasi cloud, enkripsi database khusus, biometrik, OCR struk, impor PDF, widget, atau pengingat terjadwal. Harga saham/emas/reksa dana diisi manual. Pustaka TradingView tidak menyertakan data pasar.

Menandai tagihan lunas hanya memperbarui jadwal. Catat pengeluaran lebih dahulu. Menambah portofolio dan memperbarui progres target tidak mengubah saldo rekening. Ekspor CSV bukan backup lengkap dan ekspor berisi transfer tidak dapat langsung diimpor kembali tanpa menyesuaikan format.

Proyek baru memakai `id.fintrack.app` dan tidak memigrasi data Arus. Kode font dan grafik dibundel agar tampilan dasar dapat digunakan tanpa internet. Harga pasar tetap memerlukan internet.

## Dokumentasi

- `docs/FEATURES.md`: uraian fitur panjang, rumus, batas, dan roadmap.
- `docs/DESIGN_MAPPING.md`: pemetaan layar desain.
- `TEST_RESULTS.md`: hasil validasi aktual.
- `samples/mutasi-contoh.csv`: data contoh buatan. Gunakan rekening percobaan dengan saldo nol.
- `tools/DomainCheck.kt`: uji logika tanpa perangkat Android.

## Uji domain

Jalankan `bash tools/check-domain.sh /path/ke/gradle-8.11.1/lib` menggunakan distribusi Gradle 8.11.1. Skrip memakai compiler Kotlin 2.0.20 yang dibundel Gradle untuk pengujian domain terpisah. Build aplikasi menggunakan Kotlin 2.0.21 sesuai version catalog.

## Lisensi pihak ketiga

TradingView Lightweight Charts 5.2.1: Apache-2.0, lihat `app/src/main/assets/chart/LICENSE-lightweight-charts.txt` dan `NOTICE`. Atribusi ditampilkan pada grafik dan halaman Pengaturan.

Plus Jakarta Sans: SIL Open Font License, lihat `app/src/main/assets/PLUS_JAKARTA_LICENSE.txt`.
