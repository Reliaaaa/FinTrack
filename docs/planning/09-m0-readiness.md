# M0 — inventaris kode dan urutan kerja pertama

1 November 2026. Pemeriksaan statis pada commit `4e8f7ec5c8dffe19ec7ee3cf7a4ccdc0253b3a4f`. Ini adalah handoff persiapan implementasi; tidak menjalankan build, tes perangkat, atau audit keamanan. Sub-agent yang diminta membantu terkena batas penggunaan; catatan ini diselesaikan agent utama dan belum mendapat review independen.

## Inventaris aktual (AR-001, DB-001)

Satu modul Gradle `:app` dalam settings.gradle.kts. Application ID dan namespace `id.fintrack.app`; min SDK 26, compile/target SDK 35, Java 17. gradle/libs.versions.toml menetapkan AGP 8.7.3, Kotlin 2.0.21, KSP 2.0.21-1.0.28, Compose BOM 2024.12.01, Room 2.6.1; wrapper Gradle 8.11.1. Ini versi yang tercatat pada source, bukan rekomendasi versi terbaru.

| Bagian | Bukti dalam repository | Hasil pemeriksaan |
|---|---|---|
| Komposisi aplikasi | app/src/main/java/id/fintrack/app/FinTrackApplication.kt | Room, Repository dan Prefs dibuat di Application |
| UI dan state | ui/FinTrackApp.kt, vm/MainViewModel.kt (di bawah paket aplikasi) | Compose Navigation, satu MainViewModel, StateFlow; belum dipisah per fitur |
| Domain | domain/ | Kalkulasi uang, forecast, kewajiban, anggaran/laporan, parser CSV/notifikasi dan split bill tersedia |
| Penyimpanan | data/AppDatabase.kt | Room versi 1, exportSchema aktif, enam entitas |
| Relasi | data/Entities.kt | transactions.accountId nullable; tidak ada deklarasi ForeignKey. fingerprint memiliki indeks unik; transferGroup belum memiliki indeks eksplisit |
| Rekening | AccountEntity | balance Long disimpan langsung; saldo awal belum disimpan terpisah untuk rekonsiliasi |
| Transaksi | TransactionEntity | amount Long, timestamp Long, status/source String, transferGroup dan fingerprint opsional |
| Tagihan | ObligationEntity | Jadwal memakai dueDay, remaining, lastPaidMonth; belum ada entitas occurrence atau relasi pembayaran |
| Aset | HoldingEntity | quantity Double; costBasis/unitPrice Long. Konversi ke decimal adalah pekerjaan migrasi terpisah |
| Anggaran/target | BudgetEntity, GoalEntity | Anggaran berkunci kategori; target menyimpan progres dan dueDate String |
| Migrasi | FinTrackApplication.kt | Builder tidak mendaftarkan migration. Belum ada jalur upgrade skema yang bisa dinyatakan teruji |
| Integrasi | market/PriceClient.kt, ui/PriceChart.kt, notif/FinTrackListenerService.kt | Adapter harga, grafik dan listener notifikasi tersedia; bukan integrasi transaksi uang nyata |
| Lisensi aset | app/src/main/assets/chart/LICENSE-lightweight-charts.txt, chart/NOTICE, PLUS_JAKARTA_LICENSE.txt | Berkas lisensi grafik dan font tersedia; inventaris lisensi transitif belum lengkap |

Backend cloud, panel React, sinkronisasi, OCR/PDF dan backup lengkap belum menjadi modul pada settings Gradle atau struktur source yang diperiksa. Jangan membuat ulang ledger/UI yang sudah ada hanya untuk mengikuti struktur usulan.

## Perbedaan penting terhadap acceptance MVP

1. **Pembayaran tagihan (DB-004, MB-017–019):** Repository.markPaid hanya mengubah lastPaidMonth dan remaining. Tidak membuat pengeluaran atau menyimpan tautan transaksi. Ini batas implementasi yang juga disebut README, bukan bukti fitur pembayaran terpadu sudah selesai.
2. **Idempotency manual (MB-005/006/011):** addTransaction dan transfer memakai transaksi Room, tetapi tidak menerima operation ID stabil. Pemanggilan ulang dapat membuat operasi baru; perlindungan ketukan ganda harus diuji dari UI sampai repository. Draf dan impor sudah memiliki mekanisme berbeda untuk mencegah pengulangan.
3. **Batas saldo (DB-003):** nominal per operasi dibatasi pada beberapa metode, tetapi akumulasi balance melalui DAO perlu diuji terhadap overflow. Batas input saja belum membuktikan saldo agregat aman.
4. **Tanggal bisnis (DB-003):** transaksi memakai timestamp; MainViewModel dan exportCsv memakai ZoneId.systemDefault. Perubahan zona dapat mengubah pengelompokan tanggal; tetapkan kontrak tanggal sebelum migrasi.
5. **Navigasi (MB-001):** tab aktual Dasbor, Transaksi, Anggaran, Aset, Laporan. Peta rencana mengusulkan Riwayat/Lainnya; perbedaan ini memerlukan keputusan desain, bukan penggantian diam-diam.

Temuan di atas adalah pembacaan jalur kode dan gap acceptance. Dampak runtime serta severity bug harus ditetapkan lewat reproduksi dengan data sintetis.

## Baseline QA-001

TEST_RESULTS.md mencatat hasil 28 Oktober: 95 pemeriksaan domain, enam tes Room API 29, build debug dan lint. LedgerTest.kt memang memuat enam tes: edit lintas rekening, transfer/undo, transfer invalid, impor ulang, konfirmasi draf ulang, dan hapus rekening/wipe. Tidak ada hasil eksekusi baru dalam pekerjaan ini.

Pada tahap build yang berikutnya, jalankan dari root repository dengan toolchain sesuai source:

```text
bash tools/check-domain.sh <direktori-gradle-8.11.1>/lib
gradlew.bat :app:assembleDebug :app:lintDebug
gradlew.bat :app:connectedDebugAndroidTest
```

Perintah terakhir memerlukan emulator/perangkat aktif. Simpan commit, versi perangkat, log, jumlah tes dan kegagalan; jangan menyalin hasil historis sebagai bukti baru. Tambahkan pengujian migrasi, retry manual, overflow, perubahan zona, pembayaran tagihan dan pemulihan process death sebelum menutup acceptance terkait.

## Urutan paket kerja siap ditugaskan

| Urutan | Tiket / owner → reviewer | Hasil dan acceptance | Status |
|---|---|---|---|
| 1 | AR-001, DB-001 / Architect + Database → Tech Lead | Lengkapi inventaris lisensi transitif dan verifikasi schema export cocok dengan enam entitas | Review inventaris |
| 2 | AR-002 / Architect → Database + Tech Lead | ADR mempertahankan app/, menetapkan batas UI/domain/data, kontrak uang/tanggal dan operation ID; backend/React tetap tahap lanjutan | Siap dirancang |
| 3 | QA-001 / QA → Tech Lead | Reproduksi baseline pada commit tercatat, laporkan kegagalan dan perangkat aktual | Menunggu tahap build |
| 4 | DB-002/003 / Database → Mobile + QA | Rencana migrasi tanpa kehilangan saldo/riwayat, fixture v1, rekonsiliasi dan tes batas nominal/tanggal | Setelah ADR |
| 5 | DB-004 / Database → Architect + QA | Occurrence unik per jadwal/periode dan relasi pembayaran; tentukan penanganan lastPaidMonth lama tanpa menciptakan debit baru | Setelah kontrak migrasi |
| 6 | MB-017–019 / Mobile Backend → QA + Tech Lead | Konfirmasi debit tepat sekali, kaitkan transaksi lama tanpa debit baru, pembatalan atomik; failure injection tidak meninggalkan data parsial | Setelah skema dan baseline |

Untuk DB-004, data historis lastPaidMonth tidak cukup untuk menebak rekening atau transaksi pembayar. Migrasi wajib mempertahankan penanda lama dengan asal yang jelas; jangan menyisipkan pengeluaran asumsi. Tiap paket implementasi dibatasi satu perubahan perilaku dan dipecah lagi bila melebihi dua hari kerja.

## Keputusan Tech Lead sementara

**CHANGES_REQUESTED untuk kesiapan implementasi penuh**, dengan owner dan syarat di tabel. Inventaris ini dapat dipakai untuk menyusun ADR dan test plan. TL-001 belum diterima karena QA-001 belum direproduksi, SC-001 belum memiliki threat model yang direview, dan review independen belum dilakukan. Tidak ada izin deploy atau merge yang ditambahkan oleh dokumen ini.
