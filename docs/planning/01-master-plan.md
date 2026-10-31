# FinTrack — Rencana Pengembangan Fullstack

Tanggal: 31 Oktober 2026, Asia/Jakarta. Pemilik keputusan produk: Varellio. Repository: https://github.com/Reliaaaa/FinTrack. Cakupan pekerjaan saat ini: **planning dan paket awal repository**, sesuai pilihan pengguna. Implementasi aplikasi, deployment, dan audit keamanan runtime menjadi pekerjaan berikutnya.

## Keputusan inti

Lanjutkan aplikasi Android Kotlin + Jetpack Compose yang sudah ada. Pertahankan Room sebagai penyimpanan utama di perangkat. Tambahkan backend TypeScript secara bertahap untuk layanan harga, akun opsional, dan sinkronisasi terenkripsi. React + TypeScript dipakai untuk panel operasional jika kebutuhan tersebut disetujui; bukan penulisan ulang aplikasi Android.

Rilis berikutnya mengutamakan integritas saldo, backup/pemulihan, pembayaran tagihan yang terhubung ke pencatatan, dan kunci aplikasi. Pengguna tetap dapat mencatat tanpa akun dan tanpa internet. Transfer dalam fase awal hanya mencatat perpindahan saldo; aplikasi tidak memindahkan uang melalui bank.

## Dasar keputusan dan status bukti

| Sumber | Yang diperoleh | Perlakuan dalam planning |
|---|---|---|
| Daftar Fitur Aplikasi Keuangan Android, 10 Oktober 2026 | Produk Arus, data lokal, proyeksi 90 hari, riset penyedia | Kebutuhan produk; harga, izin, kuota API dan ketentuan hukum perlu diverifikasi sebelum integrasi |
| FinTrack_Dokumentasi_Fitur, 27 Oktober 2026 | Perilaku Kotlin, rumus, batas fitur, database, pekerjaan lanjutan | Baseline fungsional terdokumentasi, bukan bukti semua skenario telah diuji |
| ZIP Stitch | 32 HTML layar, DESIGN.md dan spesifikasi arsitektur | Referensi desain; file screen.png masing-masing 28 byte bukan screenshot yang dapat dipakai untuk review visual |
| Repository master pada commit aaa94566182b167918f87789f2ef9e830ebc393b | Proyek Android, app/, Gradle, kode domain, tes Room, dokumentasi | Kode sudah ada. Pemeriksaan sesi ini terbatas pada inventaris, konfigurasi dan dokumen baseline; bukan audit seluruh kode |
| TEST_RESULTS.md tanggal 28 Oktober 2026 | Mencatat 95 pemeriksaan domain dan 6 tes Room API 29 lulus; lint 0 error/8 warning | Hasil historis milik repository. Belum dijalankan ulang dalam pekerjaan planning ini |

Konfigurasi repository yang dibaca: namespace `id.fintrack.app`, minSdk 26, compileSdk/targetSdk 35, Java 17, versi aplikasi 0.1.0. Ini adalah konfigurasi saat ini, bukan klaim versi terbaru atau kepatuhan Google Play saat rilis nanti.

## Konflik antarfile yang diselesaikan

| Perbedaan | Keputusan usulan | Alasan |
|---|---|---|
| Arus vs FinTrack | FinTrack; pertahankan application ID | Repository sudah memakai identitas ini; hindari aplikasi/database baru tanpa migrasi |
| Kotlin Compose vs React Native | Kotlin Android; React web opsional | Memanfaatkan kode yang sudah ada; tidak membangun dua UI mobile sekaligus |
| Cloud wajib vs data lokal/cloud opsional | Lokal tetap dapat digunakan sendiri | Sesuai prinsip produk dan perilaku baseline |
| Microservices untuk 10 juta MAU vs aplikasi awal | Backend modular dalam satu layanan | Target skala ZIP belum didukung trafik atau kebutuhan operasional nyata |
| Biometrik/AES/bank terhubung pada mockup | Tampilkan status hanya setelah fitur bekerja | Mockup bukan bukti implementasi |
| Notifikasi fase 2 vs sudah tersedia eksperimental | Pertahankan nonaktif default; uji ulang sebelum promosi | Tidak membuang fondasi, tidak menjanjikan kompatibilitas bank |
| Target pengguna pemula vs visual untuk investor kaya | Bahasa sederhana dan alur cepat; warna navy dipertahankan | Kebutuhan pengguna lebih penting daripada deskripsi pemasaran desain |
| Anggaran per kategori vs versi per bulan | Pertahankan perilaku baseline; versi bulanan menjadi perubahan skema terencana | Hindari perubahan histori tanpa keputusan eksplisit |

## Struktur sembilan peran

Sembilan peran adalah pembagian tanggung jawab. Mereka dapat dijalankan bergiliran atau paralel sesuai kapasitas; jumlah peran bukan sembilan proses yang terus berjalan otomatis. Setiap tugas memiliki satu penanggung jawab dan reviewer berbeda.

| Peran | Tanggung jawab | Keluaran wajib | Peninjau |
|---|---|---|---|
| 1. Project Manager | Pecah ide menjadi tugas kecil, prioritas, dependency, kriteria penerimaan | Backlog ber-ID, sprint, keputusan scope | Manajer produk + Tech Lead |
| 2. Architect | Batas modul, kontrak API, alur data, keputusan teknologi | ADR, struktur proyek, kontrak | Tech Lead + Security |
| 3. Designer | Alur pengguna, semua layar dan kondisi kosong/gagal/offline | Peta layar, token, wireframe Canva, spesifikasi interaksi | PM + QA + Tech Lead |
| 4. Mobile & Backend Developer | Implementasi Android serta API; React hanya saat masuk scope | PR kecil, perubahan kontrak, bukti uji | QA + Security + Tech Lead |
| 5. Database Engineer | Room/SQLite, skema server, migrasi, konsistensi, backup | Skema, migrasi, uji integritas/pemulihan | Architect + QA + Security |
| 6. QA | Reproduksi bug, pengujian domain/UI/integrasi/regresi | Kasus uji, laporan bug, bukti verifikasi fix | Tech Lead |
| 7. Security Developer | Ancaman, review izin/data, pemeriksaan kode dan pipeline | Temuan dengan bukti, dampak, perbaikan dan verifikasi | Tech Lead + pemilik risiko |
| 8. DevOps | Build, artefak, staging, signing, deployment, komentar status | Workflow, runbook, hasil run, rollback | QA + Security + Tech Lead |
| 9. Tech Lead | Review seluruh keluaran; menolak perubahan yang gagal memenuhi syarat | APPROVE atau CHANGES_REQUESTED dengan alasan konkret | Manajer menyelesaikan keputusan scope/risiko |

PM tidak menyatakan fitur selesai hanya karena PR dibuat. Developer tidak menyetujui perubahan sendiri. Tech Lead dapat mengembalikan pekerjaan ke penanggung jawab dengan daftar perbaikan dan tes yang harus dijalankan ulang.

## Siklus kerja

Ide → PM membuat tiket → Architect menetapkan kontrak bila diperlukan → Designer menetapkan perilaku → Mobile/Backend dan Database mengerjakan → QA menguji → Security memeriksa area berisiko → Tech Lead review → DevOps menyiapkan rilis → verifikasi setelah rilis.

Status tiket: `BACKLOG → READY → IN_PROGRESS → REVIEW → QA → SECURITY (bila relevan) → TECH_LEAD → DONE`. Gunakan `BLOCKED` dengan dependency jelas; `CHANGES_REQUESTED` kembali ke owner. Batasi satu pekerjaan aktif per owner untuk area file yang sama. Perubahan arsitektur/database mendahului implementasi yang bergantung padanya.

**Siap dikerjakan** berarti: masalah jelas, sumber kebutuhan diketahui, acceptance criteria terukur, dependency tersedia, dan batas perubahan ditentukan. **Selesai** berarti: kriteria dipenuhi, tes relevan lulus, bug penghambat ditutup, migrasi/rollback dinilai, dokumentasi diperbarui, dan Tech Lead menyetujui. Untuk tiket planning, bukti berupa artefak yang konsisten; tidak perlu menjalankan build aplikasi yang tidak berubah.

## Tahapan dan estimasi awal

Estimasi di bawah adalah waktu kalender dengan asumsi satu developer Android, satu backend/DB, QA sebagian waktu, serta desain/DevOps/Tech Lead berbagi waktu. Agent membantu pengerjaan tetapi tidak menghilangkan waktu review, perangkat, akses layanan atau keputusan produk. Jika satu orang menangani semua peran, susun ulang kapasitas setelah milestone awal; jangan memakai angka ini sebagai janji tanggal rilis.

| Tahap | Keluaran | Estimasi | Syarat keluar |
|---|---|---|---|
| M0 — Baseline & planning | Inventaris kode, backlog, desain, ADR, konfigurasi CI awal | 1–2 minggu | Planning disetujui; baseline dapat direproduksi pada pekerjaan implementasi berikutnya |
| M1 — Lokal dapat dipercaya | Ledger stabil, backup/restore lengkap, kunci aplikasi, tagihan terpadu, regresi | 6–10 minggu | Saldo tidak menyimpang; upgrade/restore terbukti; bug kritis/tinggi ditutup |
| M2 — Kenyamanan | OCR lokal, PDF bank terbatas, pengingat, hardening parser notifikasi | 4–8 minggu | Tiap format didukung memiliki fixture; hasil ekstraksi dikonfirmasi pengguna |
| M3 — Fullstack opsional | Backend harga, auth cloud opsional, sync E2EE, perangkat/recovery, React ops bila dibutuhkan | 6–10 minggu | Uji dua perangkat, konflik, tenant isolation, kehilangan kunci, restore dan staging lulus |
| M4 — Integrasi lanjutan | Discovery saham berlisensi, bank/pembayaran resmi bila dibutuhkan | Belum diestimasi | Mitra, ruang lingkup hukum, biaya, kredensial sandbox dan acceptance disepakati |

M1–M3 sekitar 16–28 minggu dengan asumsi di atas, di luar M0 dan review eksternal. Tambahkan 3–6 minggu jika audit menunjukkan fondasi harus dibangun ulang. Re-estimasi setelah audit baseline dan dua sprint pengerjaan pertama. Widget, kuota SIM, peta, dan alat tambahan berada di backlog lanjutan; tidak menghambat rilis lokal yang dapat dipercaya.

Urutan awal yang paling penting: reproduksi baseline → tutup celah integritas/migrasi → pembayaran tagihan terhubung → backup/restore → kunci aplikasi → uji perangkat → beta lokal. Backend harga bisa disiapkan paralel setelah kontrak, tetapi tidak menjadi syarat pencatatan offline.

## Ukuran keberhasilan dan batas scope

Target dari brief: catat transaksi ≤5 detik dan onboarding hingga dashboard terisi ≤3 menit. Ukur pada pengguna uji dengan skenario dan perangkat yang ditetapkan; bedakan waktu input manusia dari waktu respons sistem. Target sesi bebas crash ≥99% dievaluasi pada periode dan jumlah sesi yang dilaporkan, dengan pengumpulan telemetri seminimal mungkin dan persetujuan yang sesuai.

Tambahan gate integritas: setiap perubahan saldo terjadi sekali; transfer bersih nol; restore menghasilkan total dan jumlah entitas yang sama; tidak ada penghapusan data diam-diam saat migrasi. Target ini diverifikasi dengan tes, bukan hanya angka persentase coverage.

Chat antarpengguna, payment gateway dan login wajib **bukan** kebutuhan MVP yang ditemukan. Login ditambahkan ketika cloud diaktifkan. Tidak ada scraping login bank, penggunaan token sesi Stockbit, atau klaim prediksi investasi pasti. Kajian aturan/lisensi menjadi tugas sebelum integrasi, bukan asumsi dari dokumen lama.

## Repository dan DevOps

Paket sekarang ditujukan untuk branch planning terpisah dan draft PR ke `master`. Pertahankan `app/`, Gradle dan kode yang ada. Masukkan dokumen ke `docs/planning/`, panduan peran serta contoh workflow ke lokasi yang dijelaskan pada paket starter. Tidak ada deploy atau build APK pada tahap yang dipilih ini.

Rancangan komentar: setiap run build/deploy kelak menghasilkan komentar saat berjalan dan saat selesai; komentar memuat run ID/attempt, commit, environment, hasil dan tautan log/artefak. PR memperoleh komentar PR; run tanpa PR memperoleh komentar commit. Gagal, dibatalkan dan rollback harus terlihat, tidak disebut berhasil. Rincian batas aktivasi notifier dijelaskan di dokumen kualitas dan runbook.

Branch protection dan akun reviewer belum diubah. Pemilik reviewer, penyedia hosting, akun Play Console, biaya, key signing, credential dan recovery policy diputuskan ketika memasuki implementasi/deployment. Jangan memasukkan secret ke komentar, issue, dokumen atau repository publik.

## Referensi teknis yang diperiksa

- [Android: offline-first](https://developer.android.com/topic/architecture/data-layer/offline-first) — sumber lokal dan antrean sinkronisasi sebagai dasar rancangan.
- [GitHub: workflow events](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows) — rancangan pemicu dan pelaporan run.
- [GitHub: secure use](https://docs.github.com/en/actions/reference/security/secure-use) — pemisahan kode PR dari workflow berhak tulis.
- [Baseline repository](https://github.com/Reliaaaa/FinTrack/tree/aaa94566182b167918f87789f2ef9e830ebc393b) dan [hasil tes historis](https://github.com/Reliaaaa/FinTrack/blob/aaa94566182b167918f87789f2ef9e830ebc393b/TEST_RESULTS.md).
