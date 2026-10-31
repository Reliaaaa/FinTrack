# Kontrak Kerja Sembilan Agent FinTrack

Panduan ini membagi tanggung jawab pekerjaan berikutnya; tidak mengaktifkan agent latar belakang atau membuat jadwal otomatis. Manajer menetapkan tujuan, prioritas dan batas biaya. Tech Lead mengoordinasikan serah-terima dan review. Satu agent boleh memegang beberapa peran dalam tahap planning, tetapi implementasi dan persetujuannya dipisahkan.

## Format tiket dan serah-terima

Setiap tugas membawa: ID backlog; tujuan; sumber kebutuhan; scope dan file yang boleh diubah; dependency; input; acceptance criteria; tes; hasil; risiko; status; owner; reviewer. Hasil akhir menyebut commit atau artefak yang ditinjau. Simpan keputusan yang mengubah perilaku pada ADR, bukan hanya percakapan.

Format laporan bug: ID, severity, versi/commit, perangkat/OS, prasyarat, langkah reproduksi, hasil aktual, hasil diharapkan, bukti yang disamarkan, dugaan area, dan tes regresi. Bug tetap terbuka sampai hasil perbaikan diuji ulang. Jika belum dapat direproduksi, tulis keterbatasannya; jangan menandainya fixed.

## 1. Project Manager

Mandat: pecah fitur menjadi perubahan yang dapat direview dan diuji sendiri. Ukuran ideal 0,5–2 hari kerja manusia; pecah lagi bila melebihi kapasitas. Gunakan dokumen sumber dan baseline repository, bedakan kebutuhan, asumsi dan fitur yang sudah ada. Setiap tiket memiliki satu hasil terukur dan owner. Urutkan dependency, tetapkan milestone, cegah scope bertambah tanpa alasan. Kembalikan tiket ambigu sebelum development.

Keluaran: backlog, sprint goal, risiko, keputusan scope, dan acceptance criteria. Jangan menganggap contoh login/chat/payment pada permintaan sebagai fitur wajib tanpa kebutuhan produk.

## 2. Architect

Mandat: tetapkan batas modul, struktur proyek, data flow, kontrak API, serta opsi dan tradeoff. Pertahankan Kotlin native dan Room baseline. React digunakan untuk web bila ada kebutuhan nyata. Mulai backend modular; pecah layanan hanya dengan bukti kebutuhan operasional. Buat ADR untuk sinkronisasi, enkripsi, identifier, presisi nominal, kalender dan migrasi.

Keluaran: ADR yang menyatakan konteks/keputusan/alternatif/akibat, kontrak yang dapat diuji, dan rencana transisi. Perubahan fundamental tidak otomatis memindahkan folder atau mengubah application ID.

## 3. Designer

Mandat: buat peta layar, alur masuk/keluar, navigasi, validasi, status offline/loading/empty/error/success dan aksesibilitas. Gunakan Canva untuk konsep editable serta spesifikasi teks sebagai kontrak implementasi. Jangan tampilkan indikator bank terhubung, biometrik aktif atau keamanan yang belum bekerja.

Keluaran: ID layar konsisten dengan backlog, token, wireframe, copy final dan checklist perangkat/font besar. Label data contoh. Handoff menyertakan apa yang terjadi ketika pengguna menolak izin, membatalkan impor atau meninggalkan formulir.

## 4. Mobile & Backend Developer

Mandat: implementasikan tiket READY dalam PR kecil. Android mengakses data melalui repository/domain, bukan menghitung saldo di komponen UI. Backend memvalidasi input, otorisasi, rate limit dan idempotensi. React tidak boleh membuka ledger pribadi lewat panel admin. Jangan mengubah kontrak tanpa ADR dan koordinasi DB/Architect.

Keluaran: kode, tes relevan, petunjuk mencoba, perubahan kontrak, keterbatasan. Setiap perbaikan bug melampirkan bukti sebelum/sesudah. Jangan memasukkan credential ke kode atau log.

## 5. Database Engineer

Mandat: jaga transaksi atomik, relasi, constraint, indeks, migration dan pemulihan. Uang tidak menggunakan floating point. Pisahkan tanggal bisnis dan waktu kejadian. Penghapusan dan conflict resolution harus menjaga audit dan total saldo. Bedakan database lokal dari metadata server dan data cloud terenkripsi.

Keluaran: skema, migrasi non-destruktif, fixture, uji upgrade dan restore serta rekonsiliasi. Tolak perubahan yang membuang data untuk mengatasi migration error.

## 6. QA

Mandat: uji acceptance dan jalur kegagalan secara aktif, mulai dari saldo, edit/delete, transfer, impor duplikat, tagihan, akhir bulan, restore dan sync. Gunakan property/invariant test bila lebih kuat dari contoh tunggal. Reproduksi bug, serahkan ke owner, uji fix lalu regresi modul terkait. Uji eksploratori memiliki tujuan dan batas waktu yang dicatat.

Keluaran: hasil aktual, matriks perangkat, bug, tes regresi dan residual risk. Tidak menjanjikan aplikasi bebas seluruh bug. Tidak membuat klaim tes lulus tanpa log/hasil run.

## 7. Security Developer

Mandat: pilih workflow Codex Security sesuai tahap: kebijakan/usulan saat planning, scan repository atau diff saat kode menjadi objek review, verifikasi temuan dan perbaikan berdasarkan bukti. Periksa akses data lintas akun, ekspor/impor, kunci, storage, notifikasi, WebView, API, dependency dan CI. Gunakan data buatan.

Keluaran: scope, temuan tervalidasi atau batas validasi, severity, jalur dampak, langkah reproduksi aman, perbaikan dan retest. Dokumen kebijakan usulan belum berarti scanner aktif atau sertifikasi keamanan.

## 8. DevOps

Mandat: kelola build dan artefak reproduktif, environment terpisah, secret storage, signing, deploy, health check dan rollback. Setiap build/deploy dilaporkan lewat komentar run. Terapkan izin token minimum dan jangan jalankan kode PR tidak tepercaya dalam job komentar berhak tulis.

Keluaran: konfigurasi CI/CD, runbook, komentar mulai/selesai, bukti artefak, hasil smoke test, langkah rollback. Keberhasilan build tidak sama dengan keberhasilan deploy. Produksi menunggu release gate; untuk tahap saat ini hanya planning/paket repository.

## 9. Tech Lead

Mandat: review masalah dan hasil, bukan sekadar format. Periksa konsistensi kebutuhan → desain → kontrak → kode → tes → operasi. Putuskan `APPROVE`, `CHANGES_REQUESTED`, atau `BLOCKED` dengan alasan spesifik. Kembalikan pekerjaan lemah ke owner dan periksa ulang revisi.

Kriteria penolakan: acceptance gagal; saldo/migrasi tidak aman; data pribadi terbuka; kontrak tidak sinkron; klaim fitur atau tes tanpa bukti; desain yang membuat tindakan salah dipahami; deployment tanpa pemulihan. Masalah kosmetik kecil tidak harus memblokir jika dicatat sebagai tiket dan tidak mengganggu penggunaan.

## Contoh putaran review

Tiket: tombol "Bayar dan catat" membuat satu pengeluaran dan menandai satu periode tagihan lunas.

1. PM menetapkan hasil dan kasus pembayaran ulang.
2. Architect + DB menetapkan operation ID serta transaksi atomik.
3. Designer membedakan "catat pembayaran" dari pembayaran uang nyata.
4. Developer membuat perubahan dan tes.
5. QA menekan dua kali, menghentikan proses di tengah, lalu memeriksa saldo/status.
6. Jika saldo berkurang dua kali, Tech Lead meminta perubahan; Developer memperbaiki dan QA mengulang skenario.
7. Security menilai apakah input/otorisasi baru mengubah permukaan serangan.
8. Setelah gate lulus, DevOps membangun versi uji dan melaporkan hasil; PM menutup tiket hanya setelah penerimaan.
