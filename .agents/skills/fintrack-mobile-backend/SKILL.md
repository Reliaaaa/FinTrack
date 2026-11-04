---
name: fintrack-mobile-backend
description: "Implementasikan atau perbaiki fitur Android Kotlin dan backend FinTrack dari tiket yang disepakati. Gunakan untuk kode aplikasi/API dan React operasional yang masuk scope, bukan perencanaan umum."
---

# FinTrack — Mobile & Backend

Mulai dari tiket, acceptance criteria, repo dan commit yang jelas. Periksa AGENTS.md, ADR dan struktur aktual. Pertahankan app/ Kotlin/Compose serta application ID. Gunakan Room melalui repository dan use case; jangan menghitung atau menulis saldo langsung dari UI. React hanya untuk kebutuhan web yang termasuk tugas.

Operasi tambah/edit/hapus harus atomik terhadap saldo. Transfer menyimpan dan membalik dua sisi bersama, tidak menjadi income/expense. Pembayaran tagihan mengikat transaksi dan occurrence sekali; sediakan jalur mengaitkan transaksi yang sudah dicatat. Parser notifikasi/OCR/CSV membuat preview/draft sebelum konfirmasi; hindari penyimpanan OTP dan pesan mentah.

Backend memvalidasi otorisasi setiap objek, batas input, pagination, rate limit, expiry/revocation dan idempotency. Untuk E2EE jangan menambahkan ledger plaintext atau kunci pembuka di server. Jangan mengubah kontrak DB/API secara diam-diam. Jangan gunakan Double/Number floating untuk nominal uang; serialisasi integer besar/decimal sebagai string sesuai kontrak.

Selesaikan PR kecil dengan tes yang membuktikan perilaku relevan, petunjuk mencoba, dampak migrasi dan batas validasi. Build sukses tidak membuktikan UI atau security lulus. Gunakan skill Codex Security fix-finding hanya untuk permintaan memperbaiki kerentanan, bukan setiap bug biasa. Otorisasi fitur tidak otomatis mengizinkan deployment produksi.
