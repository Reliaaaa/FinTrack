# Aktivasi bertahap repository FinTrack

Keputusan pengguna saat ini: planning dan paket awal repository dahulu. Target repository: https://github.com/Reliaaaa/FinTrack, default branch master. README menunjukkan app/, gradlew, tools/check-domain.sh dan TEST_RESULTS.md. Jangan memindahkan struktur existing ke apps/android hanya untuk mengikuti contoh; petakan struktur aktual dalam ADR setelah audit.

## Isi paket

- Planning validation: memeriksa keberadaan berkas planning, bukan mengompilasi aplikasi.
- Run comments: menulis komentar saat run yang dipantau mulai dan selesai. Memakai payload event dan API GitHub saja; tidak checkout kode, mengunduh artefak, atau menjalankan input PR.
- ci-templates/*.yml.example: rancangan Android, backend, dan staging yang tidak dijalankan GitHub. Activation gate sengaja gagal agar template yang dipindahkan tanpa konfigurasi tidak melaporkan sukses palsu.
- docs/SECURITY.proposed.md: usulan kebijakan, belum aktif sebagai kebijakan Codex Security.

## Integrasi pada repository existing

1. Baca instruksi/policy/workflow existing; buat branch planning dari master terbaru.
2. Simpan paket ini sebagai docs/planning/repository-starter jika baru ingin review. File .github di dalam subfolder tidak berjalan.
3. Jika validasi planning akan dijalankan pada PR, salin hanya workflow planning ke .github/workflows dan sesuaikan semua path check ke lokasi berkas planning yang dipilih. Jangan menimpa workflow existing.
4. Tinjau workflow komentar di PR. Ia baru menerima workflow_run setelah berada pada default branch master. Tidak ada komentar otomatis dari notifier sebelum syarat ini terpenuhi.
5. Aktifkan Actions dan izin token sesuai organisasi. Comment PR membutuhkan pull-requests:write; fallback comment commit membutuhkan contents:write. Izin tulis hanya pada notifier; workflow build tetap contents:read. Tidak membutuhkan PAT.
6. Tambahkan nama workflow existing yang memang ingin dipantau pada allowlist YAML dan dictionary Python; target environment harus benar. Nama template: Android CI, Backend CI, Deploy staging, Deploy production. Workflow di luar daftar tidak mendapat komentar.
7. Setelah source/toolchain diverifikasi, baru aktifkan build template dengan jalur aktual (repo sekarang tampak app/ dan gradlew di root). Kunci versi JDK/SDK/Gradle/Node mengikuti kompatibilitas source dan kebijakan distribusi saat aktivasi.

## Komentar tiap run

Event in_progress menghasilkan komentar mulai; completed menghasilkan hasil final GitHub (success/failure/cancelled/timed_out/skipped dan lain-lain). Komentar memuat nama workflow, SHA, run ID, attempt, target, serta link log/tes/artefak. Setiap rerun punya attempt tersendiri. Marker dan pemeriksaan komentar bot menghindari duplikasi jika event diproses ulang. Jika ada PR terkait, komentar pada PR; jika tidak, pada commit.

Tidak ada nominal keuangan, nama nasabah, token, log mentah, atau signing key pada komentar. Workflow hanya melaporkan hasil run; deployment harus memasukkan health check dan verifikasi versi sebelum boleh menyimpulkan sukses. Artifact debug bukan release produksi.

Keterbatasan operasional: belum diuji end-to-end di GitHub dalam paket ini. Izin organisasi, API rate limit/outage, commit tak dapat diakses, atau workflow yang dibatalkan sebelum mulai dapat menghalangi komentar mulai. Kegagalan notifier terlihat sebagai run gagal; DevOps harus memeriksa dan menjalankan ulang notifier bila diperlukan. Komentar ini pelaporan per-run, bukan streaming setiap baris log. Deployment yang menunggu approval environment terlihat dalam run link; status job terperinci memerlukan perluasan webhook GitHub App bila dibutuhkan.

## Verifikasi saat diaktifkan

Uji pada branch percobaan: sukses, gagal, dibatalkan, rerun, PR internal, PR fork, push tanpa PR, API write ditolak, run paralel, serta percobaan judul/branch berisi karakter khusus. Pastikan tidak ada checkout/eksekusi PR dalam notifier. Pastikan event final tetap dilaporkan setelah build dibatalkan. Validasi juga post sukses tanpa duplikasi setelah rerun notifier.

## Deploy berikutnya

Pilih host/backend region dan biaya; siapkan staging/production environments terpisah; OIDC untuk identitas cloud bila penyedia mendukung; secret terpisah per environment; signing keystore milik pemilik aplikasi di penyimpanan secret; artefak immutable berdasar digest/SHA. Produksi memerlukan reviewer dan gate release. Perubahan schema memakai expand-contract, snapshot yang sudah diuji restore, dan migrasi kompatibel versi sebelumnya. Rollback backend memakai digest sebelumnya; Android menggunakan rilis perbaikan dengan versionCode lebih tinggi, bukan berharap downgrade database aman.

Referensi resmi diperiksa saat planning:
- https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows#workflow_run
- https://docs.github.com/en/actions/reference/security/secure-use
- https://docs.github.com/en/rest/commits/comments#create-a-commit-comment

Paket overlay repository menempatkan dokumen di docs/planning/, skill di .agents/skills/, AGENTS.md di root, serta dua workflow planning/komentar di .github/workflows/. Planning validation telah memakai path overlay ini. Folder repository-starter sendiri adalah arsip konfigurasi untuk ditinjau, bukan proyek Android pengganti.
