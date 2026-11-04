---
name: fintrack-devops
description: "Siapkan atau jalankan build, deployment dan komentar status FinTrack di GitHub sesuai target pengguna. Gunakan untuk CI/CD, artefak, signing, environment dan rollback; planning tidak otomatis menjalankan deploy."
---

# FinTrack — DevOps

Periksa repo target dan branch terbaru; default proyek yang diketahui Reliaaaa/FinTrack, master, Android di root app/. Jangan membuat repo tebakan atau menimpa kode dengan starter. Baca docs/planning dan runbook. Gunakan GitHub connector bila memadai; jangan mengakses secret untuk sekadar planning.

Bedakan tiga mode: planning/template, build uji, deploy. Pada mode planning buat paket yang reviewable dan template .example; jangan trigger APK/deploy. Pada build, kunci toolchain sesuai source, jalankan domain/lint/test relevan, simpan artefak dengan commit dan checksum. Pada deploy, pilih environment/host yang sudah diputuskan, identitas cloud minimum, artefak immutable, migrasi kompatibel dan smoke/health/version check. Signing key milik pemilik, bukan key sementara produksi.

Pengguna proyek meminta komentar setiap build/deploy. Laporkan mulai dan final termasuk failed/cancelled dengan SHA, run ID/attempt, target dan tautan hasil. Komentar di PR terkait, fallback commit; jangan tulis secret atau log keuangan. Daftarkan nama workflow dalam notifier allowlist. workflow_run notifier harus ada di default branch; workflow di subfolder dokumen belum aktif. Jangan klaim notifier bekerja sebelum smoke test.

Job pelaporan berhak tulis hanya memakai metadata terpercaya/API; tidak checkout atau execute PR/fork/artefak. Build untrusted tidak mendapat kredensial rilis. Jika komentar gagal, tandai kegagalan pelaporan dan lakukan retry terbatas dengan identitas run yang sama; jangan spam atau ulang deployment untuk mengulang komentar.

Output: perubahan workflow, hasil run nyata, lokasi artefak, status deploy dan pemulihan. Rollback backend ke digest sebelumnya; Android umumnya rilis perbaikan versionCode lebih tinggi dengan migration kompatibel. Jangan merge/deploy produksi di luar scope yang diotorisasi.
