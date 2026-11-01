# Pembayaran tagihan — 2 November 2026

DB-004 / MB-017 / MB-018: implementasi untuk review. Room dinaikkan ke versi 2 dengan tabel bill_payments, primary key jadwal/periode dan transaksi unik. Receipt merangkap occurrence yang sudah dibayar; occurrence belum dibayar tetap berasal dari jadwal. Ini tahap terbatas dari rancangan 10-ledger-contract.md, bukan implementasi seluruh kontrak operation ID, tanggal bisnis atau snapshot occurrence.

UI menawarkan pengeluaran baru dari rekening pilihan atau pengaitan pengeluaran CONFIRMED non-transfer dengan nominal sama. Repository memvalidasi kembali dan menyimpan transaksi, saldo, receipt, serta sisa cicilan dalam satu withTransaction. Receipt existing membuat retry menjadi no-op. Pembayaran baru dibatasi bulan berjalan; retry receipt lama tetap tidak mendebit lagi.

Migration 1→2 menyalin lastPaidMonth sebagai LEGACY_MARKER tanpa transactionId dan tidak mengubah saldo/remaining. Penanda lama tetap lunas; rekonsiliasi tautannya belum disediakan. Tidak ada foreign key baru pada tahap ini; akses lewat repository melindungi penghapusan jadwal/transaksi tertaut dan wipe menghapus receipt terlebih dahulu. Pembatalan, arsip jadwal dan pengeditan transaksi tertaut belum tersedia; operasi tersebut ditolak dengan pesan sampai MB-019 diimplementasikan.

Validasi dijalankan: tools/check-bill-migration.py membuat database SQLite dari schema export v1, menjalankan SQL migrasi aktual, memeriksa marker, remaining tetap, tidak ada debit, serta constraint periode/transaksi unik — lulus. git diff --check lulus. Ini tidak menggantikan validasi schema Room pada Android.

Tiga tes instrumentasi ditambahkan: retry pembayaran, linking tanpa debit/dilarang dipakai ulang, dan rollback ketika saldo underflow. Belum dijalankan. Compile Android, validasi UI, migration instrumentation dan schema export v2 dari KSP masih wajib sebelum merge. Tidak mengubah schema v1 atau menulis schema v2 buatan. Tidak ada klaim APK/deploy baru. Status REVIEW; QA dan reviewer independen belum memberi persetujuan.
