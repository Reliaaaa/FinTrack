# FinTrack — backlog pelaksanaan

Tanggal rencana: 31 Oktober 2026. Pemilik keputusan produk: manajer aplikasi. Backlog berikut adalah target pelaksanaan. Repository Reliaaaa/FinTrack telah diperiksa terbatas pada inventaris, konfigurasi build dan dokumen pada commit aaa94566182b167918f87789f2ef9e830ebc393b. Belum ada build aplikasi yang dijalankan ulang. Tiket akses DO-001 terpenuhi; artefak planning PM/DS/AR tersedia untuk review; tiket implementasi tetap menunggu verifikasi acceptance.

## Dasar dan batas scope

Dokumen fitur 10 Oktober menetapkan Android, data lokal, cloud opsional dan tanpa pemindahan uang pada fase pertama. Dokumen FinTrack 27 Oktober menjelaskan implementasi Kotlin/Compose, Room, transaksi, anggaran, proyeksi dan impor. Perilaku tersebut perlu ditelusuri pada kode dan diuji per acceptance. README dan TEST_RESULTS.md repository menguatkan adanya baseline, tetapi hasil tes historis tidak menggantikan pengujian commit baru. ZIP menyediakan 32 referensi HTML, spesifikasi dan sistem visual; bukan proyek Android siap build. Gambar `screen.png` dalam ZIP masing-masing hanya 28 byte sehingga tidak menjadi bukti tampilan yang dapat direview.

Usulan dasar: Kotlin/Compose untuk Android; React untuk panel operasional bila dibutuhkan; backend Node/TypeScript modular bertahap; Room sebagai database perangkat. Tidak menambahkan chat, payment atau login wajib hanya karena dicontohkan dalam permintaan. Login diperlukan ketika pengguna memilih layanan cloud. Integrasi uang nyata tetap discovery sampai mitra, akses dan scope disepakati.

## Cara memakai backlog

PM = Project Manager; AR = Architect; DS = Designer; MB = Mobile/Backend; DB = Database; QA = Quality Assurance; SC = Security; DO = DevOps; TL = Tech Lead. Owner adalah penanggung jawab utama, bukan satu-satunya pelaksana. Peran MB memiliki dua jalur tugas, Android dan backend, dengan satu kontrak API yang direview bersama. Penguji perubahan berbeda dari pembuatnya.

Setiap ID adalah satu hasil yang bisa ditinjau. Pecah lagi menjadi tiket implementasi bila estimasi setelah inspeksi kode melebihi dua hari kerja. Dependency adalah prasyarat minimum, bukan izin melewati gerbang milestone. `M1 gate` berarti TL-003 sudah lulus; `M2 gate` berarti TL-004 sudah lulus; `M3 gate` berarti TL-005 sudah lulus. Untuk fitur yang sudah ada, pekerjaan berarti audit/perbaikan terhadap acceptance, bukan implementasi ulang.

Alur status: **Direncanakan → Siap → Dikerjakan → Review → Uji → Diterima**. Review atau uji gagal: **Dikembalikan** dengan bukti, owner perbaikan dan syarat uji ulang. Tiket selesai wajib mencantumkan commit/PR, bukti acceptance criteria, hasil tes relevan, dan risiko tersisa.

## M0 — validasi fondasi dan persiapan tim

| ID | Hasil terkecil | Owner | Dependency | Kriteria diterima |
|---|---|---|---|---|
| PM-001 | Daftar konflik kebutuhan | PM | — | Kotlin vs React Native, local vs cloud, Arus vs FinTrack dan status fitur diberi keputusan/usulan eksplisit. |
| PM-002 | Scope MVP dan prioritas | PM | PM-001 | M1 wajib, M2/M3 opsional lanjutan, M4 discovery; tidak ada pemindahan uang di MVP. |
| PM-003 | Ukuran keberhasilan | PM | PM-002 | Protokol uji catat ≤5 detik, onboarding ≤3 menit, sesi bebas crash ≥99% mencantumkan sampel dan periode pengukuran; target belum diklaim tercapai. |
| AR-001 | Inventaris kode asli | AR | URL/lokasi sumber tersedia | Commit dasar, modul, dependensi, lisensi dan paket `id.fintrack.app` tercatat; setiap klaim dokumen ditandai ditemukan/tidak ditemukan. |
| DO-001 | Akses repository tujuan | DO | URL repo dari manajer | Owner/repo dan izin branch terverifikasi; tidak ada penulisan ke repo tebakan. |
| AR-002 | Keputusan stack dan batas modul | AR | PM-002, AR-001 | ADR menjelaskan native Android, posisi React, backend modular dan alasan belum memerlukan Kafka/Kubernetes. |
| DB-001 | Inventaris skema aktual | DB | AR-001 | Entitas, versi database, indeks dan migrasi dibandingkan dengan dokumentasi tanpa menghapus data. |
| DS-001 | Sistem visual tunggal | DS | PM-001 | Token warna/font/spacing dipilih, 32 referensi terpetakan dan komponen punya keadaan error/disabled. |
| QA-001 | Baseline pengujian | QA | AR-001 | Tes, build dan uji perangkat yang benar-benar dijalankan dicatat; hasil yang belum dijalankan ditandai. |
| SC-001 | Model ancaman awal | SC | AR-002, DB-001 | Aset, batas kepercayaan dan skenario kehilangan perangkat, impor berbahaya, kebocoran log, sync lintas akun tercatat. |
| DO-002 | Branch dan aturan review | DO | DO-001 | Branch kerja terpisah, required checks dan review TL dipasang sesuai kemampuan repo; pengecualian terdokumentasi. |
| TL-001 | Review kelayakan fondasi | TL | AR-002, QA-001, SC-001 | Daftar bagian pakai ulang/perbaiki/bangun baru disetujui beserta risiko; baseline tidak disamakan dengan rilis siap pakai. |

## M1 — MVP lokal yang menjaga integritas dan pemulihan data

| ID | Hasil terkecil | Owner | Dependency | Kriteria diterima |
|---|---|---|---|---|
| DS-002 | Prototipe onboarding | DS | DS-001, PM-002 | Jalur tambah rekening, pengaturan gaji dan dashboard dapat diikuti tanpa login. |
| MB-001 | Navigasi utama | MB | AR-002, DS-002 | Dasbor, Riwayat, Anggaran, Aset dan Lainnya memiliki back behavior serta state restoration. |
| DB-002 | Skema rekening dan transaksi | DB | DB-001, AR-002 | FK, arah transaksi, sumber, status dan identitas transfer didokumentasikan serta diuji. |
| DB-003 | Representasi uang dan tanggal | DB | DB-002 | Rupiah memakai integer dengan batas overflow; tanggal bisnis tidak bergeser ketika zona perangkat berubah. |
| MB-002 | Buat rekening | MB | DB-003, MB-001 | Nama, jenis, saldo awal dan penanda likuid tersimpan; tidak meminta kredensial bank. |
| MB-003 | Edit rekening | MB | MB-002 | Mengubah nama/likuid tidak membuat transaksi atau menggandakan saldo. |
| MB-004 | Arsip atau hapus rekening | MB | MB-003 | Rekening bersejarah tidak dihapus diam-diam; rekening kosong dapat dihapus setelah konfirmasi. |
| MB-005 | Catat pemasukan | MB | MB-002 | Nominal positif, rekening dan tanggal valid tersimpan; saldo bertambah tepat sekali saat tombol diketuk ganda. |
| MB-006 | Catat pengeluaran | MB | MB-005 | Nominal positif tersimpan; saldo berkurang tepat sekali; kebijakan saldo negatif terlihat jelas. |
| MB-007 | Saran kategori lokal | MB | MB-006 | Aturan kata kunci dapat dikoreksi; pilihan manual tidak ditimpa lagi oleh perubahan catatan. |
| MB-008 | Riwayat dan pencarian | MB | MB-006 | Pencarian catatan/kategori dan filter jenis memberi hasil benar serta empty state yang jelas. |
| MB-009 | Edit transaksi atomik | MB | MB-008 | Ubah pengeluaran 50.000 menjadi 40.000 mengembalikan 10.000; perpindahan rekening membalik dampak lama. |
| MB-010 | Hapus transaksi | MB | MB-009 | Konfirmasi menampilkan dampak; saldo kembali tepat sekali, pembatalan dialog tidak mengubah apa pun. |
| MB-011 | Catat transfer internal | MB | DB-002, MB-010 | Rekening berbeda dan nominal valid; dua sisi tersimpan atomik, total aset tetap, income/expense tidak bertambah. |
| MB-012 | Batalkan catatan transfer | MB | MB-011 | Hapus salah satu sisi menghapus pasangan dan memulihkan dua saldo dalam satu operasi. |
| QA-002 | Invarian saldo | QA | MB-012 | Rangkaian tambah/edit/hapus/transfer, retry dan kegagalan simpan tidak meninggalkan saldo melenceng. |
| MB-013 | Dashboard saldo nyata | MB | QA-002 | Saldo likuid dibedakan dari total aset; tidak ada saldo contoh pada instalasi baru. |
| MB-014 | Ringkasan bulanan dan 7 hari | MB | MB-013 | Transfer tidak dihitung sebagai pendapatan/belanja; batas bulan dan hari konsisten dengan tanggal bisnis. |
| MB-015 | Pengaturan pemasukan rutin | MB | MB-013 | Nominal dan hari 1–31 valid; tanggal akhir bulan diperlihatkan sebelum disimpan. |
| DB-004 | Skema jadwal dan occurrence tagihan | DB | DB-003 | Identitas tiap jatuh tempo unik; jadwal rutin dipisahkan dari pembayaran yang sudah terjadi. |
| MB-016 | Buat/edit jadwal tagihan | MB | DB-004 | Jenis, nominal, hari jatuh tempo dan sisa cicilan tersimpan; nol berarti selesai, kosong berarti berulang. |
| MB-017 | Pembayaran tagihan terpadu | MB | MB-016, MB-006 | Satu konfirmasi membuat pengeluaran dan status lunas atomik; ulang konfirmasi tidak mendebit lagi. |
| MB-018 | Kaitkan pembayaran yang sudah dicatat | MB | MB-017 | Pengguna memilih transaksi relevan; tidak dibuat pengeluaran kedua. |
| MB-019 | Batalkan pembayaran tagihan | MB | MB-018 | Status occurrence, sisa cicilan dan dampak transaksi dikembalikan sesuai pilihan yang jelas. |
| MB-020 | Hitung batas belanja harian | MB | MB-019, MB-015 | Kas 3.000.000 − tagihan 900.000, 14 hari menghasilkan 150.000/hari; defisit menjadi 0 dan peringatan. |
| MB-021 | Mesin proyeksi 90 hari | MB | MB-020 | Pendapatan, belanja dan tagihan tidak dihitung dua kali; tanggal 31 dan cicilan terbatas ditangani. |
| MB-022 | Layar simulasi pembelian | MB | MB-021 | Menampilkan minimum, tanggal defisit, akhir periode dan pembanding; simulasi tidak mengubah saldo. |
| QA-003 | Uji kalender perencanaan | QA | MB-022 | Gajian hari ini, Februari, tahun kabisat, ganti zona dan tagihan dibayar lulus contoh hasil yang telah ditetapkan. |
| DB-005 | Skema batas anggaran | DB | DB-003 | Untuk MVP satu batas aktif per kategori; perubahan tidak mengklaim versi historis yang belum disimpan. |
| MB-023 | Atur/hapus anggaran | MB | DB-005 | Nominal positif mengganti batas kategori yang sama; penghapusan tidak menghapus transaksi. |
| MB-024 | Progres anggaran | MB | MB-023, MB-006 | Status <80%, ≥80%, ≥100% benar; progres penuh tetap menampilkan kelebihan nominal. |
| MB-025 | Buat/edit target tabungan | MB | DB-003 | Nama, tujuan, progres dan tanggal tersimpan; progres 0 sampai target; tidak mengubah saldo rekening. |
| MB-026 | Estimasi setoran target | MB | MB-025 | Sisa 5.000.000 selama 5 bulan menghasilkan 1.000.000; tanggal lewat memakai pembagi minimum satu. |
| MB-027 | Catat aset manual | MB | DB-003 | Unit/kelas/modal/harga berlabel jelas; tambah holding tidak menciptakan uang atau mendebit rekening otomatis. |
| MB-028 | Nilai portofolio dan kewajiban | MB | MB-027, MB-016 | Total aset dan estimasi kewajiban memakai definisi terlihat; nilai angsuran tidak disebut pokok pinjaman. |
| MB-029 | Laporan periode/rekening | MB | MB-014 | Income, expense, selisih dan kategori sesuai filter; rasio tabungan kosong bila income nol. |
| MB-030 | Parser CSV | MB | DB-003 | UTF-8, kutip/koma, kolom wajib, 1 MB/2.000 baris dan pesan nomor baris; invalid file tidak disimpan sebagian. |
| MB-031 | Preview dan pilih rekening impor | MB | MB-030 | Seluruh baris divalidasi, hingga 100 baris ditampilkan; dampak saldo dan kebutuhan saldo awal dijelaskan. |
| DB-006 | Deduplikasi impor | DB | MB-031 | Fingerprint memasukkan rekening dan ref; impor ulang identik dilewati; ref berbeda dapat mewakili transaksi identik nyata. |
| MB-032 | Konfirmasi impor atomik | MB | DB-006 | Konfirmasi tunggal menambah seluruh baris valid dan saldo; gagal simpan mengembalikan kondisi awal. |
| MB-033 | Ekspor CSV sesuai scope | MB | MB-029, MB-032 | Pengguna memilih semua/hasil filter; header dan transfer konsisten; formula spreadsheet dinetralkan. |
| QA-004 | Uji impor/ekspor | QA | MB-033 | Duplikat, kutip, Unicode, formula, file besar dan saldo awal teruji; ekspor CSV tidak disebut backup lengkap. |
| SC-002 | Desain backup terenkripsi | SC | DB-001, SC-001 | Format versi, autentikasi ciphertext, pengelolaan kunci, password salah dan pemulihan perangkat baru ditentukan. |
| MB-034 | Buat backup lengkap | MB | SC-002 | Rekening, transaksi, jadwal, pembayaran, aset, anggaran, target dan pengaturan tercakup dalam berkas terenkripsi. |
| MB-035 | Preview pemulihan backup | MB | MB-034 | Versi, integritas, ketersediaan ruang dan password diperiksa sebelum database aktif diganti. |
| DB-007 | Pemulihan atomik | DB | MB-035 | Restore gagal tidak merusak database lama; sukses mengganti satu snapshot konsisten dan menghitung ulang saldo. |
| QA-005 | Uji backup lintas pemasangan | QA | DB-007 | Data sintetis pulih di pemasangan bersih; salah password, file rusak, versi tak didukung dan kehabisan ruang aman. |
| MB-036 | Kunci aplikasi | MB | SC-001 | Autentikasi perangkat/biometrik, fallback dan relock kembali dari background diuji; gagal autentikasi tidak membuka data. |
| MB-037 | Privasi tampilan dan ekspor | MB | MB-036 | Mode sembunyi nominal berlaku pada layar yang didefinisikan; share hanya setelah unlock; preview recent-apps ditinjau. |
| MB-038 | Hapus seluruh data lokal | MB | DB-007 | Ringkasan cakupan dan konfirmasi kuat; data serta pengaturan yang dijanjikan terhapus, berkas eksternal dijelaskan terpisah. |
| DB-008 | Migrasi skema tanpa kehilangan data | DB | DB-007, DB-004 | Upgrade snapshot versi lama menjaga riwayat dan saldo; tidak memakai destructive fallback di rilis. |
| SC-003 | Review keamanan MVP | SC | MB-038, DB-008 | Temuan penyimpanan, backup, log, ekspor dan komponen Android memiliki bukti/reproduksi; temuan kritis/tinggi ditangani. |
| QA-006 | Uji aksesibilitas dan perangkat | QA | DS-002, MB-038 | Font besar, TalkBack, layar kecil, offline, process death dan rotasi tidak kehilangan entri atau menutup aksi penting. |
| DO-003 | Pipeline build APK uji | DO | DO-002, AR-001 | Commit tetap menghasilkan lint/tes/APK dan checksum; job gagal tidak menerbitkan artefak sebagai sukses. |
| DO-004 | Komentar setiap build | DO | DO-003 | Awal dan hasil build tercatat dengan run URL, commit, status dan artefak; gagal/cancel tetap terlacak tanpa komentar ganda. |
| TL-002 | Review lintas modul | TL | QA-002, QA-003, QA-004, QA-005, SC-003 | Kode, model data, UX dan tes sesuai acceptance; pekerjaan gagal dikembalikan dengan tindakan spesifik. |
| TL-003 | Gerbang MVP lokal | TL | TL-002, QA-006, DO-004, DB-008 | Instalasi/upgrade/restore lulus; tidak ada blocker saldo atau temuan kritis/tinggi yang belum ditangani; release notes jujur. |

## M2 — kenyamanan input dan fitur perangkat

Urutan dalam M2: pengingat → draf notifikasi → OCR → PDF. Widget, kuota dan peta adalah kandidat terpisah; manajer dapat menundanya tanpa menunda fungsi inti.

| ID | Hasil terkecil | Owner | Dependency | Kriteria diterima |
|---|---|---|---|---|
| MB-039 | Jadwalkan pengingat tagihan | MB | M1 gate | Izin ditolak tetap dapat mencatat manual; pengingat diperbarui setelah edit/lunas/restart. |
| MB-040 | Izin akses notifikasi opsional | MB | M1 gate | Default mati; penjelasan sebelum masuk pengaturan Android; tidak meminta akses SMS atau kredensial bank. |
| MB-041 | Parser notifikasi allowlist | MB | MB-040 | Hanya aplikasi yang diizinkan; OTP/promosi tidak menjadi transaksi; isi mentah tidak masuk log. |
| MB-042 | Review draf notifikasi | MB | MB-041 | Pilih rekening dan koreksi sebelum konfirmasi; draf tidak mengubah saldo, discard aman. |
| QA-007 | Regresi parser notifikasi | QA | MB-042 | Fixture per aplikasi/versi, duplikat dan format berubah tidak menghasilkan transaksi final otomatis. |
| MB-043 | OCR foto lokal | MB | M1 gate | Pilih foto, proses, batal dan gagal terbaca ditangani; foto tidak dikirim ke cloud tanpa scope baru. |
| MB-044 | Koreksi item OCR | MB | MB-043 | Ubah qty/harga, tambah/hapus item; total termasuk pajak/diskon terlihat dan dapat diperbaiki. |
| MB-045 | Simpan hasil OCR | MB | MB-044 | Preview final, rekening dan kategori wajib; simpan ulang tidak menambah pengeluaran kedua. |
| MB-046 | Bagi tagihan | MB | MB-044 | Pembagian proporsional dan sisa rupiah menjumlah tepat ke total; tidak membuat utang atau pembayaran otomatis. |
| MB-047 | Parser PDF bank pertama | MB | M1 gate, format sampel legal tersedia | Satu format bank disebut jelas; file terkunci/tidak didukung dibedakan, password tidak disimpan. |
| MB-048 | Review dan rekonsiliasi PDF | MB | MB-047, DB-006 | Selisih saldo awal/akhir, baris ambigu dan duplikat ditampilkan sebelum commit atomik. |
| QA-008 | Uji file dan media rusak | QA | MB-045, MB-048 | PDF/foto rusak, besar dan parser timeout tidak crash atau mengubah saldo. |
| MB-049 | Widget privat | MB | M1 gate | Pengguna memilih isi dan visibility; data tersembunyi secara default pada permukaan sensitif yang dipilih. |
| MB-050 | Quick tile/catat cepat | MB | MB-049 | Dari tile menuju form dengan unlock bila terkunci; tidak membuka transaksi lewat bypass kunci. |
| MB-051 | Kuota SIM manual dan siklus | MB | M1 gate, prioritas disetujui PM | Data kuota berasal dari input pengguna; tidak mengklaim pembacaan operator otomatis; rollover tanggal jelas. |
| MB-052 | Lokasi pengeluaran opsional | MB | M1 gate, prioritas disetujui PM | Izin hanya saat dibutuhkan, lokasi manual tetap tersedia; hapus lokasi tidak menghapus transaksi. |
| MB-053 | Adapter harga kripto | MB | M1 gate, akses/lisensi diverifikasi | Provider/time stamp jelas, gagal jaringan mempertahankan harga lama berlabel; data keuangan pribadi tidak dikirim. |
| MB-054 | Grafik pasar dan atribusi | MB | MB-053 | Simbol berubah membatalkan hasil lama; atribusi/lisensi dibundel; harga pasar tidak disamakan dengan saldo rekening. |
| SC-004 | Review perluasan permukaan data | SC | QA-007, QA-008, MB-050 | Parser, foto, lokasi, widget dan WebView ditinjau; jalur izin/berbagi tidak melewati app lock. |
| TL-004 | Gerbang M2 | TL | SC-004, QA-007, QA-008 | Hanya fitur yang lulus acceptance diaktifkan; format bank/provider yang belum diuji tetap nonaktif. |

## M3 — cloud opsional dan panel operasional

M3 membutuhkan keputusan pemulihan kunci dan model sinkronisasi lebih dahulu. Data keuangan terenkripsi dari ujung ke ujung tidak dapat sekaligus dianalisis dalam bentuk terbaca oleh backend tanpa perubahan produk dan persetujuan yang jelas.

| ID | Hasil terkecil | Owner | Dependency | Kriteria diterima |
|---|---|---|---|---|
| AR-003 | Kontrak sync dan recovery | AR | M1 gate, SC-002 | Envelope terenkripsi, versi, tombstone, konflik, idempotensi dan kehilangan kunci ditetapkan dalam ADR. |
| SC-005 | Protokol E2EE dan key recovery | SC | AR-003 | Library/protokol yang dipilih direview; server tidak mendapat plaintext atau kunci pembuka; tradeoff recovery dijelaskan. |
| MB-055 | API autentikasi cloud | MB | AR-003 | Login, logout/revoke dan expiry diuji; mode lokal tetap berjalan tanpa akun. |
| MB-056 | Opt-in akun di Android | MB | MB-055, SC-005 | Pengguna melihat data yang dikirim, recovery dan cara menonaktifkan sync sebelum mengaktifkan. |
| DB-009 | Penyimpanan envelope cloud | DB | AR-003 | Data terisolasi per akun/perangkat; versi/replay dan batas ukuran tercakup tanpa menyimpan saldo plaintext. |
| MB-057 | Outbox dan push idempoten | MB | DB-009, MB-056 | Retry setelah offline tidak menduplikasi transaksi; perubahan lokal tetap tahan process death. |
| MB-058 | Pull, tombstone dan konflik | MB | MB-057 | Dua perangkat mengedit record yang sama menghasilkan keputusan konflik eksplisit; penghapusan tidak hidup kembali. |
| QA-009 | Uji sync multi-perangkat | QA | MB-058 | Offline panjang, reorder, retry, logout dan konflik transfer tidak merusak saldo; akses lintas akun ditolak. |
| MB-059 | Hapus akun dan data cloud | MB | MB-058 | Reautentikasi, cakupan lokal/cloud dan masa retensi jelas; akun terhapus tidak bisa sync kembali dengan token lama. |
| MB-060 | Panel React operasional | MB | MB-055, kebutuhan panel dikonfirmasi | Hanya status layanan/pekerjaan dan metadata perlu; operator tidak melihat isi keuangan E2EE. |
| DO-005 | Deploy backend staging | DO | MB-055, DB-009 | Build immutable, migrasi aman, health check dan rollback diuji dengan data sintetis. |
| DO-006 | Komentar setiap deploy | DO | DO-005, DO-004 | Environment, commit/digest, mulai/selesai/gagal/cancel, health check dan tautan run dilaporkan; secret tidak ditulis. |
| SC-006 | Audit auth/API/sync | SC | QA-009, MB-059, MB-060 | Auth bypass, cross-user access, replay, refresh token dan log diuji; bukti temuan dan verifikasi perbaikan tersedia. |
| TL-005 | Gerbang cloud beta | TL | SC-006, DO-006 | Restore kunci, konflik, pemisahan tenant, rollback dan penghapusan akun lulus sebelum cloud ditawarkan. |

## M4 — discovery integrasi eksternal, bukan komitmen implementasi

| ID | Hasil terkecil | Owner | Dependency | Kriteria diterima |
|---|---|---|---|---|
| PM-004 | Kelayakan data saham/emas/valas | PM | Kebutuhan produk dikonfirmasi | Harga, hak penggunaan, kuota, atribusi dan ketersediaan untuk akun pengguna diperiksa langsung; alternatif manual tetap ada. |
| PM-005 | Kelayakan banking/payment | PM | M3 gate, persetujuan scope baru | Mitra resmi, aliran dana, kewajiban hukum, biaya dan dukungan operasional divalidasi bersama pihak yang tepat. |
| AR-004 | Desain integrasi sandbox | AR | PM-005 | Status pending/success/failed/unknown, webhook, idempotency dan rekonsiliasi dirancang sebelum tombol uang nyata. |
| TL-006 | Keputusan lanjut/tunda | TL | PM-004, AR-004 | Business case, risiko dan biaya diterima manajer; tanpa keputusan ini tidak ada eksekusi pembayaran nyata. |

## Urutan sprint dan estimasi bersyarat

Estimasi adalah rentang perencanaan untuk tim kecil yang memiliki kemampuan Android, backend, QA dan desain; bukan janji durasi agent atau biaya. Sembilan peran tidak berarti sembilan orang bekerja penuh waktu. Audit M0 menentukan apakah fondasi benar-benar dapat dipakai ulang.

| Tahap | Urutan kerja | Rentang awal |
|---|---|---|
| M0 | Audit sumber, keputusan scope/stack, desain dan CI | 1–2 minggu setelah akses kode/repo tersedia |
| M1 | Rekening/transaksi → perencanaan/laporan → impor → backup/lock → pengujian dan rilis uji | 6–10 minggu bila fondasi dapat dipakai; tambah 3–6 minggu bila harus membangun ulang |
| M2 | Pengingat/notifikasi → OCR → PDF; widget/kuota/peta ditimbang per fitur | 4–8 minggu untuk paket kenyamanan terpilih; parser bank tambahan diestimasi terpisah |
| M3 | ADR crypto/sync → auth → push/pull → konflik/recovery → staging dan audit | 6–10 minggu; dapat berjalan sebagian paralel setelah kontrak M1 stabil |
| M4 | Kelayakan kontrak/mitra dan sandbox | Belum layak diberi tanggal implementasi sebelum akses dan keputusan scope |

Buffer perbaikan/review sudah perlu dialokasikan pada tiap sprint; jangan menjadwalkan semua kapasitas untuk fitur baru. PM memperbarui estimasi setelah dua sprint berdasarkan throughput dan temuan, bukan menganggap jumlah tiket sebagai ukuran waktu.

## Syarat tiket siap dan selesai

Siap: tujuan pengguna, mockup/state relevan, definisi data, acceptance criteria, dependency dan reviewer jelas. Untuk parser/integrasi, fixture serta kontrak data tersedia. Untuk perubahan uang/database, contoh sebelum-sesudah dan skenario kegagalan disepakati.

Selesai: acceptance terbukti; perubahan ditinjau TL; QA menguji jalur utama dan kegagalan; Security dilibatkan sesuai permukaan yang berubah; migrasi/rollback relevan diuji; build/deploy memiliki tautan serta komentar status. QA mengulangi reproduksi → perbaikan → uji ulang sampai blocker yang ditemukan selesai, tanpa menjanjikan semua bug di seluruh kondisi sudah hilang.

Sumber rencana: `Daftar Fitur Aplikasi Keuangan Android (1).md` (10 Oktober 2026), `FinTrack_Dokumentasi_Fitur.docx` (27 Oktober 2026), serta `stitch_personal_finance_tracker_app.zip`. Nama fase M0–M4 adalah penyusunan ulang untuk pelaksanaan, bukan klaim fase asli dokumen telah selesai.
