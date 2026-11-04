# Usulan kebijakan keamanan FinTrack

Status: draft untuk review pemilik. Belum menjadi SECURITY.md aktif, bukan laporan audit, tidak menetapkan pengecualian yang telah disetujui. Target aktivasi setelah review: root repository/SECURITY.md. Repository Reliaaaa/FinTrack telah ditemukan, tetapi source belum diaudit dalam paket planning ini.

## Sistem dan cakupan

Android Kotlin/Compose, database Room lokal, parser CSV/notifikasi, WebView grafik, dan pipeline/dependensi. Lokasi aktual Android adalah app/ di root. Perluasan yang direncanakan meliputi PDF/OCR, backup, backend pasar/sync, dan panel React; lokasi target services/api, apps/admin-web, contracts dan infra belum berarti modul tersebut sudah ada. Pemindahan Android ke apps/android memerlukan keputusan terpisah.

Pemilik produk menentukan cakupan rilis dan penerimaan risiko. Security bertanggung jawab pada review dan bukti; DevOps pada identitas rilis, secret, infrastruktur dan pemulihan. Fase awal mencatat keuangan; tidak mengirim uang atau order investasi.

## Aset dan batas kepercayaan

Aset: catatan finansial, kunci enkripsi, token akun, backup, signing key, integritas saldo, isolasi pengguna, dan artefak rilis. Input tak tepercaya: CSV/PDF/foto, notifikasi pihak lain, respons penyedia harga, deep link, payload sync, browser/admin, dependensi, dan kode PR/fork. Perangkat yang sepenuhnya dikuasai penyerang tetap risiko residu; kondisi ini bukan pengecualian otomatis terhadap semua temuan lokal.

Batas: aplikasi ke penyimpanan lokal; aplikasi ke dokumen ekspor; UI ke domain/repository; perangkat ke API; tenant ke tenant; operator ke metadata; PR ke runner tepercaya; build ke signing/deploy.

## Invarian wajib

- Otorisasi diperiksa untuk setiap objek, tidak hanya autentikasi request. Backend tidak menerima owner_id klien sebagai otoritas.
- Tidak mengumpulkan kredensial bank, PIN bank, SMS, atau token sesi broker tak resmi.
- Tidak ada data keuangan atau secret pada log, crash report, analytics, PR comment, maupun commit.
- Transfer dan perubahan saldo atomik; request retry dan impor ulang tidak menggandakan nilai.
- Parser dibatasi ukuran, jumlah item, kedalaman, dan waktu. Format tidak valid ditolak sebelum commit.
- Backup terenkripsi memiliki verifikasi integritas; restore divalidasi di area sementara lalu diganti atomik. Kunci hilang tidak diatasi dengan membuka enkripsi server.
- Cloud yang disebut E2EE hanya menyimpan envelope terenkripsi; operator/server tidak membaca ledger. Metadata yang tetap terlihat didokumentasikan.
- Kunci berada dalam mekanisme platform yang sesuai; biometrik tidak diklaim sebagai enkripsi database dengan sendirinya.
- WebView tidak memuat origin arbitrer atau mengekspos bridge istimewa pada konten tak tepercaya.
- Consent dapat ditarik. Menolak akses notifikasi tidak memblokir transaksi manual.
- Token CI minim izin; job pemberi komentar tidak checkout atau menjalankan isi PR/artefak build. Signing/deploy hanya kode tepercaya.

## Temuan dan tingkat risiko

Laporan harus menjelaskan input penyerang, prasyarat, jalur ke dampak, bukti reproduksi aman, dan perbaikan. Ekspos ledger lintas pengguna, pencurian signing key, pengambilalihan akun, atau akses cloud tanpa izin berpotensi high/critical menurut jangkauan aktual. Kerusakan saldo akibat input/konkurensi dilaporkan juga sebagai integritas data. Angka scanner tanpa jalur yang dapat dijangkau bukan vonis; jangan menutup temuan hanya karena tes lulus.

## Pengecualian dan risiko diterima

Belum ada pengecualian atau risiko diterima yang disetujui pemilik. Layanan pihak ketiga berada di luar kendali kode, tetapi konfigurasi, integrasi, validasi respons dan pemakaian secret tetap termasuk cakupan. Batas assurance perangkat dan penyedia harus dicatat, bukan digunakan sebagai suppression umum.

## Batas yang diketahui

Dokumen FinTrack menyebut database aplikasi belum dienkripsi secara khusus, kunci aplikasi dan backup lengkap belum tersedia. Ini klaim dokumentasi yang harus diverifikasi pada source. Tidak ada hasil scan source atau uji penetrasi pada paket planning ini. Kontak pelaporan privat, masa dukungan versi, cakupan production, dan kebijakan retensi belum diputuskan. Jangan memublikasikan detail eksploit atau data pengguna di issue publik.
