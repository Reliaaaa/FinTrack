---
name: fintrack-qa
description: "Cari dan reproduksi bug FinTrack, verifikasi perbaikan dan regresi pada domain, Room, UI, parser, API dan sync. Gunakan untuk pengujian aplikasi ini; bukan janji bebas semua bug."
---

# FinTrack — QA

Gunakan commit dan acceptance criteria terbaru; TEST_RESULTS.md lama hanya referensi. Baca docs/planning/05-quality-security-devops.md. Prioritaskan saldo, transfer, edit/delete, pembayaran berulang, duplikat impor, akhir bulan, backup/restore dan migration. Untuk cloud uji dua perangkat, reorder/retry, tombstone, konflik dan isolasi akun.

Tulis skenario dengan data sintetis dan expected result sebelum eksekusi. Periksa invariant: total transfer tetap, operasi ulang tidak menambah efek, draft tidak mengubah saldo, split bill menjumlah tepat, restore mempertahankan entitas. Uji jalur gagal/process death, jaringan, storage, izin dicabut, font besar, TalkBack dan perangkat minimum/target. Uji eksplorasi dibatasi sasaran dan waktu; jangan loop tak terbatas sampai mengklaim semua bug ditemukan.

Laporkan bug dengan build SHA, perangkat, prasyarat, langkah, actual/expected, frekuensi, severity, bukti disamarkan dan owner. Bug yang sulit direproduksi tetap dicatat sebagai belum terkonfirmasi, bukan diabaikan. Setelah fix, ulangi kasus asli dan regresi terdampak. Hentikan rilis jika ada kehilangan data atau akses tak berhak yang belum ditangani.

Output: hasil aktual dan log, bug terbuka, coverage skenario/perangkat, retest serta residual risk. Jangan menyebut build, tes atau benchmark lulus hanya karena skrip tersedia. Temuan keamanan diarahkan ke Security untuk validasi sesuai scope.
