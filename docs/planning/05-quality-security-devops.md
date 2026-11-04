# FinTrack — QA, Security, DevOps dan gate Tech Lead

Status 31 Oktober 2026: planning untuk repository Reliaaaa/FinTrack. Repository telah ditemukan; README menunjukkan proyek Kotlin dengan app/, gradlew, tools/check-domain.sh dan TEST_RESULTS.md. Klaim implementasi pada DOCX harus diverifikasi terhadap commit tertentu. Paket ini tidak menyatakan audit keamanan, build Android, atau deploy telah berhasil. Pengguna memilih planning dan paket awal repository dahulu.

## QA: mencari bug secara terarah

QA menerima acceptance criteria dan commit SHA, menyiapkan data sintetis, menjalankan happy path, batas, kegagalan, serta pengujian eksplorasi. Bug harus dapat direproduksi sebelum dikembalikan ke pengembang. Setelah diperbaiki, QA menguji kasus asli dan regresi pada alur yang terpengaruh. Proses diulang sampai gate terpenuhi; tidak ada klaim bahwa semua bug sudah ditemukan.

| ID | Skenario dan data | Hasil wajib | Lapisan |
|---|---|---|---|
| QA-01 | A Rp1.000.000; pemasukan Rp500.000; pengeluaran Rp75.000 | A Rp1.425.000; simpan ganda/retry tidak menambah dua kali | Domain + Room + UI |
| QA-02 | Pengeluaran A Rp75.000 diubah Rp50.000 lalu dipindah ke B | Dampak lama kembali tepat sekali; B berkurang tepat Rp50.000 | Integrasi |
| QA-03 | Transfer A ke B Rp200.000; hentikan proses di tengah operasi | Kedua sisi tersimpan bersama atau tidak sama sekali; total tetap | Integrasi + fault injection |
| QA-04 | Hapus salah satu sisi transfer | Kedua sisi dibalik; laporan income/expense tidak berubah | Integrasi |
| QA-05 | Dua operasi bersamaan menghabiskan saldo | Hasil konsisten dengan kebijakan saldo; tidak ada lost update | Concurrency |
| QA-06 | CSV sama dua kali; dua transaksi sah dengan nilai/tanggal sama namun ref beda | Duplikat identitas dicegah; transaksi sah berbeda tidak ikut hilang | Parser + Room |
| QA-07 | CSV kutip/koma/CRLF/BOM, header hilang, lebih 1 MB/2.000 baris, angka negatif/overflow | Preview error; tidak ada mutasi sebagian tanpa konfirmasi | Parser + UI |
| QA-08 | Catatan ekspor diawali =, +, -, @ | Mode ekspor aman spreadsheet tidak mengeksekusi formula; escaping terdokumentasi | Ekspor |
| QA-09 | Anggaran 79%, 80%, 99%, 100%, >100%; edit/hapus transaksi | Status sesuai batas; progres dan nominal akurat | Domain + UI |
| QA-10 | Gajian tanggal 31 pada Februari; tahun kabisat; gajian hari ini | Aturan clamp akhir bulan konsisten dan tidak menghitung income dua kali | Domain |
| QA-11 | Likuid 3 juta, tagihan 900 ribu, 14 hari | Batas Rp150.000; kas kurang menghasilkan nol dan peringatan | Domain |
| QA-12 | Proyeksi 90 hari, cicilan 2 kali tersisa, hari pertama negatif | Cicilan berhenti tepat; tanggal defisit dan saldo minimum benar | Domain |
| QA-13 | Tagihan ditandai lunas dua kali, undo, perubahan rekening | Satu pembayaran terkait; undo atomik; tidak dobel mengurangi kas | Integrasi |
| QA-14 | Goal diperbarui 1 juta; portofolio manual bertambah | Progres goal tidak menciptakan uang; nilai investasi tidak menduplikasi kas | Domain |
| QA-15 | Unit/price pecahan, harga stale, refresh gagal, respons simbol lama terlambat | Presisi sesuai kontrak; waktu/sumber harga tampil; hasil lama tidak menimpa simbol baru | Domain + network |
| QA-16 | Bagi Rp100 ke 3 peserta; pajak/diskon proporsional | Jumlah bagian tepat Rp100; tidak ada rupiah hilang | Property test |
| QA-17 | Backup lengkap lalu restore; password salah; file rusak; storage penuh | Kesetaraan data; kegagalan tidak menghapus database semula | Integrasi + perangkat |
| QA-18 | Upgrade setiap schema yang didukung, termasuk data besar | Saldo/riwayat tetap; tidak destructive migration | Migration test |
| QA-19 | Tolak/cabut izin notifikasi; pesan palsu/tidak dikenali/duplikat | Manual tetap bekerja; draf butuh konfirmasi; tidak langsung mengubah saldo | Perangkat |
| QA-20 | OCR item, diskon, pajak; PDF terkunci/rusak/berukuran besar | Batas parsing; koreksi dan persetujuan sebelum transaksi | Parser + UI |
| QA-21 | Font besar, layar kecil, TalkBack, keyboard, rotasi, process death | Tombol/nominal terbaca; draft terpelihara sesuai desain | UI + perangkat |
| QA-22 | Offline lama, reconnect, retry sync, edit dua perangkat, delete vs edit | Idempotency; konflik eksplisit; delete tidak hidup kembali diam-diam | Sync integration |
| QA-23 | Akun A meminta envelope/objek B; token revoked; admin biasa | Ditolak; tidak bocor isi maupun metadata tak berhak | API security |
| QA-24 | Logout/lock, background, screenshot/widget/notification | Data tersembunyi sesuai setting; session dan key lifecycle benar | Perangkat |

Property tests penting: transfer menjaga jumlah saldo; edit lalu undo mengembalikan state; jumlah split sama tagihan; retry idempoten; export/restore mempertahankan entitas; urutan operasi yang sah tidak menghasilkan ledger tanpa referensi. Perhitungan tanggal memakai clock yang dapat diatur dan zona yang disepakati.

Matriks perangkat mencakup minimum OS yang benar-benar didukung source, OS target distribusi, ukuran kecil/besar, font 200%, locale Indonesia, zona waktu berubah, offline, storage menipis, serta process death. Target produk dari MD: pencatatan <=5 detik, onboarding <=3 menit, crash-free sessions >=99%. Ukur dengan skenario dan sampel yang dinyatakan; bukan janji tanpa pengukuran. Laporan harus mencantumkan jumlah sesi/perangkat agar persentase tidak menyesatkan.

Bug report: ID, build SHA/version, OS/perangkat, prasyarat, langkah, expected/actual, reproduksibilitas, severity, bukti disamarkan, owner, tes regresi, hasil retest. Blocker: build tak dapat dijalankan atau kehilangan data utama; critical: akses tak berhak/kunci rilis bocor/kerusakan finansial luas; high: alur inti salah tanpa workaround memadai; medium/low disesuaikan dampak. Severity teknis terpisah dari prioritas bisnis.

## Security: bukti sebelum kesimpulan

Security mulai dari inventaris entry point dan alur data. Review terfokus pada parser, Room/migrasi, file sharing/export, backup/enkripsi, Keystore/biometrik, WebView, notification listener, auth dan tenant isolation, sync conflict, dependency dan CI/CD. Gunakan Codex Security sesuai scope saat source tersedia untuk diperiksa: scan repository untuk baseline, diff scan untuk perubahan. Hasil otomatis ditriage, direproduksi secara aman, diperbaiki owner, lalu diverifikasi pada commit baru.

Tidak boleh ada label AES-256, E2EE, bank connected, atau aman tersertifikasi bila kontrol/buktinya belum ada. Biometrik membuka akses aplikasi bukan bukti database terenkripsi. Enkripsi cloud harus menjelaskan key recovery, perangkat baru, revocation, rotasi, metadata yang terlihat server, dan penghapusan; backend analitik plaintext tidak kompatibel dengan klaim E2EE ledger.

Usulan kebijakan lengkap ada di repository-starter/docs/SECURITY.proposed.md. Tidak ada pengecualian/suppression atau penerimaan risiko yang dianggap telah disetujui. Draft belum aktif sebagai SECURITY.md scanner. Proses aktivasi mengikuti skill Codex Security define-security-policy: review diff dan persetujuan pemilik sebelum mengaktifkan kebijakan.

## DevOps: build dan deploy terpisah

| Tahap | Pemicu usulan | Bukti hasil | Gate |
|---|---|---|---|
| Validasi planning | PR/push | Berkas planning diperiksa | Tidak menyatakan aplikasi lulus |
| Android debug | PR/push setelah konfigurasi | Unit tests, lint, APK, instrumentation report | Tidak memakai signing production |
| Backend CI | PR/push setelah backend ada | Unit/integration/API contract + image test | Database test terisolasi |
| Staging | Commit tepercaya yang lulus CI | Digest, migrasi, health check, smoke test | Environment staging dan secret terpisah |
| Android internal testing | Rilis yang disetujui | AAB signed, versionCode, hasil install/update | Play account/config/signing milik pemilik |
| Production | Rilis manual bertag/commit terverifikasi | Version/digest, monitoring, rollback readiness | Tech Lead + gate pemilik/environment |

Untuk fase sekarang hanya planning dan paket repository; build/deploy aplikasi belum diaktifkan. Detail integrasi dan template ada di repository-starter/docs/DEVOPS-SETUP.md. Template tidak boleh dipindahkan ke workflow aktif tanpa verifikasi toolchain/source/host dan penggantian activation gate.

Komentar otomatis dirancang pada PR terkait atau commit bila tanpa PR, saat mulai dan selesai, mencakup run ID, attempt, SHA, target, hasil, link tes/artefak. Success/failure/cancelled tidak disamarkan. Notifier independen dari job build, tidak mengeksekusi kode PR/fork. Izin tulis hanya untuk komentar; jangan memberikan token rilis ke job PR. Notifier membutuhkan file pada default branch dan kebijakan Actions yang mengizinkan penulisan; keadaan ini harus diuji setelah aktivasi. Pelaporan per-run tidak menjanjikan streaming log/job atau pengiriman ketika GitHub/API gagal.

Monitoring backend: error rate, latensi, kapasitas DB, antrean sync, kuota upstream, health check, sertifikat, failed auth dan deployment. Mobile: crash/ANR dengan redaksi data, gagal migrasi/restore, sync conflict. Retensi dan akses log ditentukan sebelum produksi. Tentukan RPO/RTO bersama pemilik dan ukur melalui latihan restore; usulan awal staging RPO 24 jam/RTO 4 jam harus dikonfirmasi terhadap biaya dan kebutuhan. Rollback server ke image sebelumnya, schema expand-contract; Android memakai update perbaikan dan migrasi teruji.

## Tech Lead: kembalikan pekerjaan yang belum memenuhi gate

Review memeriksa kesesuaian fitur, kontrak saldo/data, kebutuhan desain, pembagian modul, error handling, tes, keamanan, observability, dan operasional. Keputusan harus salah satu: APPROVED atau CHANGES REQUESTED. Cantumkan blocker, alasan, owner, acceptance criteria perbaikan, dan bukti yang diperlukan. Perubahan dikembalikan ke pemilik tugas, bukan dipaksa merge oleh DevOps.

Release gate: seluruh acceptance criteria kritis lulus; bug blocker/critical nol; high diperbaiki atau keputusan risiko pemilik eksplisit dengan batas waktu (tidak otomatis mengizinkan kebocoran/kehilangan dana); migration+restore+rollback teruji; source/artefak/release SHA sama; dokumentasi fitur sesuai kenyataan; Security dan QA selesai pada scope; Tech Lead approve. Review independen pada pekerjaan berisiko tinggi diutamakan. Manager menetapkan prioritas dan risiko bisnis, agent tidak mengubah scope uang nyata sepihak.

## Rujukan teknis resmi

GitHub menjelaskan workflow_run tersedia untuk in_progress dan completed, hanya berjalan bila workflow ada di default branch, serta dapat memperoleh token tulis; karena itu notifier tidak mengeksekusi artefak tak tepercaya. [Events that trigger workflows](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows#workflow_run).

Izin token dibatasi per-job dan secret tidak diasumsikan selalu berhasil disamarkan. [Secure use reference](https://docs.github.com/en/actions/reference/security/secure-use). Fallback komentar commit membutuhkan contents:write. [Commit comments API](https://docs.github.com/en/rest/commits/comments#create-a-commit-comment).
