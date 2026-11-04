# FinTrack: kontrak kerja agent

Paket ini adalah fondasi planning untuk repository Reliaaaa/FinTrack. Repository telah ditemukan dan README menunjukkan proyek Kotlin, tetapi paket ini belum memverifikasi kode, menjalankan build Android, melakukan deploy backend, atau melakukan audit keamanan aplikasi. Instruksi existing repository tetap diperiksa sebelum menggabungkan berkas ini.

## Alur kerja

1. PM memilih satu item backlog, acceptance criteria, dependency, dan batas perubahan.
2. Architect menetapkan kontrak data/API dan mencatat perubahan desain sebagai ADR.
3. Designer menyerahkan alur, keadaan kosong/loading/error, aksesibilitas, dan referensi layar.
4. Mobile Backend dan Database mengimplementasikan dalam branch fitur dengan tes relevan.
5. QA mereproduksi bug, melampirkan bukti yang disamarkan, dan menguji ulang perbaikan.
6. Security memvalidasi jalur serangan dan dampak; hasil scan otomatis bukan bukti tunggal.
7. Tech Lead memberi APPROVED atau CHANGES REQUESTED disertai alasan dan owner. Perubahan yang ditolak kembali ke owner; ulangi pemeriksaan yang terdampak.
8. DevOps melakukan build/deploy hanya dari commit yang lulus gate, melaporkan status run dan menyimpan artefak.

## Batas implementasi

- Android utama Kotlin/Compose; React untuk panel operasional opsional. Perubahan keputusan membutuhkan ADR.
- Data pribadi utama di perangkat. Cloud opsional tidak boleh diam-diam menerima data keuangan plaintext.
- Semua perubahan saldo, pasangan transfer, dan pembayaran tagihan harus atomik dan dapat diuji ulang tanpa duplikasi.
- Jangan menyimpan token, signing key, data rekening nyata, atau hasil ekspor pengguna dalam repo/log/komentar.
- Jangan mengklaim fitur aktif, audit lulus, build sukses, atau deploy selesai tanpa bukti.
- Jangan mengeksekusi kode PR/fork dengan token tulis, kredensial rilis, atau environment production.
- Jangan menghapus data untuk melewati kegagalan migrasi.
- Kebijakan keamanan masih berupa usulan di docs/planning/repository-starter/docs/SECURITY.proposed.md; aktivasi dan keputusan risiko berada pada pemilik proyek.

## Skill proyek

Sembilan skill pada .agents/skills/ membagi PM, Architect, Designer, Mobile/Backend, Database, QA, Security, DevOps dan Tech Lead. Gunakan sesuai tugas; tidak perlu memuat semuanya untuk perubahan kecil. Referensi planning ada di docs/planning/README.md. Skill tidak mengubah otorisasi pengguna dan tidak berarti agent berjalan terus-menerus.

## Handoff minimum

Task ID, commit SHA, perubahan perilaku, file berubah, hasil tes dan perintah, bukti UI bila relevan, bug terbuka, risiko, keputusan yang diperlukan, dan reviewer berikutnya.

## Definition of Done

Acceptance criteria teruji; kode direview; tes relevan lulus; tidak ada bug blocker/critical terbuka; risiko high ditangani atau keputusan pemilik terdokumentasi; migrasi/rollback tersedia bila relevan; dokumentasi dan status fitur diperbarui; Tech Lead menyetujui. Ini tidak berarti bebas seluruh bug.
