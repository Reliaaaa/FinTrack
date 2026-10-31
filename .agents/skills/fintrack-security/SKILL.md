---
name: fintrack-security
description: "Rencanakan atau jalankan review keamanan FinTrack sesuai scope pengguna. Pilih skill Codex Security yang tepat untuk kebijakan, repository, diff, temuan atau fix; jangan menyamakan planning dengan audit."
---

# FinTrack — Security Developer

Pertama tentukan objek dan tahap: planning, repository snapshot, PR/diff, temuan, atau fix. Baca kebijakan repository yang berlaku; docs/SECURITY.proposed.md hanya usulan. Jika Codex Security tersedia, baca skill relevan: define-security-policy untuk kebijakan; security-scan untuk repository; security-diff-scan untuk perubahan; triage-finding/validation untuk temuan; fix-finding atau verify-fix saat pengguna meminta. Jangan menjalankan seluruh rangkaian hanya karena nama FinTrack muncul.

Planning menghasilkan scope kontrol dan acceptance, bukan temuan tervalidasi. Scan menggunakan commit immutable, source dan bukti jalur data. Fokus FinTrack: import/export, archive/PDF/OCR bounds, OTP/notification, database/migration, app lock/Keystore/recovery, WebView bridge, API tenant isolation, replay/idempotency, log, dependency dan CI/signing. Biometrik bukan enkripsi; E2EE perlu lifecycle key serta metadata yang dinyatakan.

Periksa bahwa notifier berhak tulis tidak checkout/menjalankan PR atau artefaknya; signing dan deploy tidak tersedia pada fork. Validasi temuan sebelum klaim exploitability. Jangan mengunggah data finansial nyata atau secret untuk tes. Aktivitas eksternal/produksi mengikuti izin pengguna dan tools; skill ini tidak memperluas otorisasi.

Output: scope, bukti, severity dengan alasan, perbaikan, pengujian ulang dan batas pemeriksaan. Tidak ada sertifikasi aman/bebas celah tanpa dasar. Keputusan menerima risiko dilakukan pemilik yang berwenang, bukan disisipkan sebagai suppression baru.
