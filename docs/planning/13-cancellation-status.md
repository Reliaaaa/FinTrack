# Pembatalan pembayaran — 2 November 2026

MB-019, status REVIEW. UI pembayaran terbaru menawarkan lepas kaitan dengan pengeluaran tetap, atau hapus pengeluaran sekaligus untuk pembayaran berorigin CREATED. Pengeluaran LINKED hanya boleh dilepas. Penanda LEGACY_MARKER tidak dibatalkan otomatis karena data koreksi historis tidak cukup.

Repository memulihkan remaining dan lastPaidMonth dari receipt yang tersisa dalam satu transaksi Room. Opsi hapus mengembalikan saldo dengan aritmetika checked. Receipt yang sudah tidak ada menghasilkan no-op sehingga konfirmasi pembatalan berulang tidak menggandakan pengembalian. ID transaksi yang berbeda dari receipt terbaru ditolak. UI juga memperlihatkan marker historis agar pengguna tidak mengira sudah ada pengeluaran tertaut.

Validasi aktual: tes SQL migrasi dan diff check lulus. Dua tes Room baru disiapkan untuk refund sekali, pemulihan cicilan, unlink tanpa refund dan stale cancellation terhadap transaksi pengganti; belum dijalankan. Tidak ada build Android atau pengujian UI baru. Memperbaiki encoding pemisah teks dialog pembayaran menjadi UTF-8.

Batas sebelum rilis: operation ID stabil dan generation receipt belum diimplementasikan. Retry pembayaran lama yang datang setelah pembatalan, atau pembatalan lama setelah unlink lalu relink transaksi yang sama, belum dapat dibedakan secara lengkap. Tambahkan identitas intent/generation beserta tes interleaving sebelum menyatakan idempotency lintas siklus selesai. Review independen, build, schema export v2, migration test Android dan tes Room tetap wajib. Perubahan ini bukan siap merge atau deploy.
