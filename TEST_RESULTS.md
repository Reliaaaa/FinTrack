# Hasil validasi FinTrack 0.1.0

Tanggal: 28 Oktober 2026. Semua data pengujian merupakan data buatan.

## Hasil yang terverifikasi

| Pemeriksaan | Hasil |
| --- | --- |
| Build `:app:assembleDebug` | Lulus. APK dihasilkan dan dipasang pada emulator. |
| Build `:app:assembleDebugAndroidTest` | Lulus. Paket pengujian berhasil dipasang. |
| `:app:lintDebug` | Lulus dengan 0 error dan 8 peringatan. |
| Uji domain mandiri | 95 pemeriksaan lulus, 0 gagal. |
| Uji instrumentasi Room | 6 tes lulus pada Android 10/API 29 x86_64. |
| Verifikasi tanda tangan APK | Lulus melalui `apksigner verify`, skema v2, satu penanda tangan debug. |
| Pemeriksaan UI | Dasbor, navigasi Aset, dan formulir rekening baru terbuka. Rekening Demo dengan saldo Rp1.000.000 berhasil dibuat dan muncul pada total aset. Setelah force-stop dan aplikasi dibuka kembali, rekening serta saldo tersebut tetap tampil di dasbor. Warna, teks, kartu saldo, dan navigasi bawah diperiksa pada emulator 640 × 1280. |
| Dokumen Word | 16 halaman dirender dan diperiksa secara visual. |

Build memakai JDK 17, Gradle 8.11.1, Android SDK 35, Build Tools 35.0.0, dan Kotlin 2.0.21. Uji domain mandiri memakai compiler Kotlin 2.0.20 bawaan distribusi Gradle. Emulator berjalan tanpa akselerasi KVM. Pada peluncuran pertama, System UI emulator sempat tidak merespons dan perintah tunggu activity mengalami timeout. Setelah dialog System UI ditutup, dasbor tampil. Kejadian ini tidak diperlakukan sebagai bukti kinerja aplikasi pada perangkat fisik.

## Enam skenario database

1. Menambah pengeluaran, mengedit jenis/nominal/rekening, lalu menghapus transaksi mengembalikan saldo yang benar.
2. Transfer menghasilkan dua baris berpasangan, menjaga total kas, dan penghapusannya membalik kedua sisi tanpa dihitung sebagai pendapatan/pengeluaran.
3. Transfer dengan saldo kurang atau rekening tujuan tidak valid tidak meninggalkan perubahan parsial.
4. Impor berulang melewati fingerprint yang sama; dua referensi berbeda tetap menghasilkan transaksi yang berbeda.
5. Draf notifikasi tidak mengubah saldo sebelum konfirmasi; konfirmasi kedua tidak menambah transaksi lagi.
6. Rekening dengan transaksi tidak dapat dihapus; hapus seluruh data juga membersihkan anggaran dan target.

Log ringkas tersimpan di `docs/validation/domain.txt`, `android-tests.txt`, dan `lint.txt`. Kode tes tersedia pada `app/src/androidTest/java/id/fintrack/app/LedgerTest.kt`. Jalankan kembali dengan emulator/perangkat aktif menggunakan `./gradlew :app:connectedDebugAndroidTest`.

## Peringatan lint

Tiga saran pembaruan versi dependensi; satu saran atribut backup untuk Android sebelum 12 (manifest sudah menetapkan `allowBackup=false`); satu qualifier resource v26 yang berlebih; satu ikon monochrome yang belum tersedia; dan dua saran memindahkan versi dependensi tes ke katalog TOML. Peringatan ini tidak menghambat build. Versi dependensi tidak diperbarui otomatis agar tidak memperluas perubahan sebelum distribusi awal.

## Cakupan yang belum terverifikasi

- Perangkat fisik, Android API 26/35/36, rotasi, mode font besar, dan seluruh variasi ukuran layar.
- Alur notifikasi dari masing-masing aplikasi bank, penanganan format notifikasi di dunia nyata, serta pembatasan proses latar belakang vendor.
- Seluruh interaksi UI dan kegagalan jaringan. Pemeriksaan ticker publik Indodax dilakukan terpisah; ini bukan pengujian menyeluruh integrasi grafik pada perangkat.
- Uji penetrasi, audit keamanan, enkripsi database, pemulihan cadangan, migrasi data Arus, dan publikasi Google Play.
- Integrasi bank/Stockbit/eksekusi perdagangan tidak diimplementasikan. Tidak ada uji pembayaran uang nyata.

APK disiapkan untuk uji coba pribadi, bukan rilis produksi bertanda tangan milik pengguna. Hasil tes ini tidak menjamin semua kasus pemakaian atau perangkat telah tercakup.

SHA-256 APK debug yang disertakan: `e3fadf219b97b584a3a142e82f08a33ee5970233e1b5bc7a82814155ef6a7301`.
