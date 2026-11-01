# Implementasi awal — 2 November 2026

DB-003 dimulai dengan pemeriksaan overflow saldo. LedgerBalance memakai Math.addExact sebelum saldo disimpan sebagai Long. Repository menghitung dan menulis saldo di dalam transaksi Room yang sama dengan mutasi ledger. DAO tidak lagi menggunakan penjumlahan SQLite yang dapat mengubah integer overflow menjadi REAL. Batas saldo bisnis absolut 10^15 dalam rancangan kontrak belum diterapkan; perubahan ini mempertahankan saldo negatif dan rentang Long existing.

Validasi aktual: baseline 95 pemeriksaan domain lulus. Setelah enam kasus aritmetika ditambahkan, 101 lulus dan 0 gagal melalui tools/check-domain.ps1, JBR 21.0.11, compiler Kotlin 2.0.20, target JVM 17. Dua tes Room baru memeriksa rollback transaksi dan transfer saat overflow; belum dijalankan karena tidak ada emulator/perangkat terdaftar. Integrasi Repository/DAO terbaru belum dikompilasi ulang.

Build baseline sebelumnya mencapai compileDebugKotlin tetapi gagal validateSigningDebug karena .android tidak writable. Percobaan berikutnya gagal karena cache .gradle tidak writable. Tidak ada APK baru atau hasil lint sukses yang diklaim. Izin gabungan belum diberikan; pengulangan build dihentikan sesuai arahan pengguna untuk menghemat usage. Upaya komentar status melalui konektor GitHub gagal karena tool tidak tersedia.

Kontrak tanggal bisnis, operation ID, skema occurrence/payment dan migrasi marker historis ada dalam 10-ledger-contract.md. Bagian tersebut masih rancangan, belum kode aktif. Pembayaran tagihan atomik dan migrasi database belum selesai. Sebelum merge perubahan saldo, jalankan compile/lint dan seluruh delapan tes Room serta review independen. Status: REVIEW, bukan siap rilis.
