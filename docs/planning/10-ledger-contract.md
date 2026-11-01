# Kontrak ledger dan rencana migrasi

2 November 2026. AR-002 / DB-003 / DB-004. Status: rancangan untuk review sebelum perubahan skema; belum diimplementasikan. Struktur app/ dan application ID dipertahankan. UI → ViewModel → use case/repository → Room; domain tidak bergantung Compose. Backend dan React tetap modul lanjutan.

## Uang, tanggal dan pengulangan operasi

- Rupiah berupa Long, nominal operasi positif maksimal 10^15. Saldo boleh negatif untuk pengeluaran sesuai perilaku existing, tetapi dibatasi absolut 10^15. Hitung dengan Math.addExact/subtractExact dan validasi batas di dalam transaksi Room sebelum menulis; pelanggaran membatalkan seluruh operasi. Unit aset menggunakan decimal dalam migrasi tersendiri.
- Tanggal bisnis disimpan ISO LocalDate, terpisah dari createdAt/updatedAt berupa epoch UTC. Perubahan zona perangkat tidak mengubah tanggal bisnis yang telah disimpan. Pada migrasi v1, pilih satu zona migrasi, rekam zona tersebut, lalu turunkan tanggal dari timestamp lama. Tidak mengklaim dapat memulihkan zona asli yang tidak pernah disimpan.
- Setiap intent simpan memperoleh operationId stabil sebelum submit, dipertahankan melalui retry dan pemulihan form. operationId unik di tabel operasi; payload kanonik turut disimpan. ID sama dengan payload sama mengembalikan hasil lama; ID sama dengan payload berbeda ditolak. Tombol disabled hanya bantuan UI, bukan jaminan integritas.
- Rekening menyimpan openingBalance. Migrasi menghitung openingBalance = balance - total posting terkonfirmasi dengan aritmetika checked; jika data tidak valid, upgrade gagal tanpa menghapus data. Transfer masuk/keluar dihitung menurut arah, draf diabaikan. Nilai hasil inferensi diberi provenance migrasi, bukan dianggap saldo awal historis yang terverifikasi.

## Pembayaran tagihan atomik

Occurrence memiliki ID, obligationId, periode yyyy-MM, nominal snapshot, status dan origin. Pasangan obligationId/periode unik. Payment mengaitkan occurrence dengan transactionId unik; satu transaksi tidak membayar dua occurrence dalam MVP. Pembayaran sebagian dan pembagian satu transaksi ke banyak tagihan berada di luar paket pertama.

Alur baru menerima occurrence, rekening, tanggal bisnis, kategori dan operationId. Dalam satu withTransaction: validasi jadwal/periode, cek operasi sebelumnya, buat pengeluaran, koreksi saldo, simpan payment, tandai occurrence lunas dan kurangi remaining bila terbatas. Kegagalan pada langkah mana pun melakukan rollback. Dua pemanggilan paralel untuk occurrence sama hanya menghasilkan satu debit.

Alur kaitkan transaksi lama mensyaratkan pengeluaran CONFIRMED non-transfer, rekening valid, nominal cocok, dan belum terkait payment lain. Pengguna memeriksa tanggal dan catatan sebelum konfirmasi. Linking tidak menulis ulang saldo. Editing transaksi terkait yang mengubah nominal/jenis/rekening ditolak sampai tautan dibatalkan secara eksplisit; perubahan catatan saja diperbolehkan.

Pembatalan mempunyai dua pilihan eksplisit: lepas kaitan dan pertahankan transaksi; atau batalkan pembayaran beserta pengeluaran (hanya untuk pengeluaran yang dibuat alur pembayaran). Keduanya memulihkan status dan remaining tepat sekali. Hapus transaksi yang masih tertaut diarahkan ke alur pembatalan, bukan delete generik. Jadwal dengan riwayat diarsipkan; tidak menghapus payment/occurrence.

## Migrasi bertahap tanpa debit asumsi

1. Bekukan schema v1 dan fixture berisi seluruh enam entitas, draf, transfer, rekening negatif, serta jadwal lunas/cicilan selesai.
2. Tambahkan kolom nullable dan tabel operation/occurrence/payment lebih dahulu. Backfill tanggal/openingBalance dengan validasi checked. Jangan mengubah balance existing.
3. lastPaidMonth v1 menjadi occurrence berorigin LEGACY_MARKER tanpa transactionId. Jangan membuat pengeluaran atau mengurangi remaining lagi. Jangan mengasumsikan bulan-bulan sebelum marker sudah dibayar. UI harus membedakan marker historis dan payment tertaut.
4. Marker lama dapat dikaitkan dengan transaksi existing setelah konfirmasi pengguna, tanpa debit dan tanpa pengurangan remaining kedua. Pembatalan marker lama membutuhkan keputusan pengguna atas koreksi sisa cicilan karena snapshot sebelum marker tidak tersedia.
5. Verifikasi jumlah baris, saldo per rekening, total transfer, fingerprint, nilai tagihan, goals dan budgets sebelum commit migrasi. Tulis migration eksplisit dan daftarkan di databaseBuilder; tidak memakai fallbackToDestructiveMigration.
6. Uji buka ulang database setelah upgrade, process death, disk penuh dan fixture rusak. Downgrade binary bukan rollback skema; gunakan rilis perbaikan kompatibel atau restore snapshot yang sudah diuji.

## Bukti yang wajib sebelum implementasi diterima

Tes harus membuktikan: retry/panggilan paralel satu debit, operationId berbeda payload ditolak, kegagalan sesudah insert tidak mengubah saldo, linking tanpa debit, kedua jenis pembatalan tepat sekali, edit/delete tertaut terlindungi, perpindahan bulan tidak menghidupkan pembayaran lama, serta migrasi marker tidak mengurangi saldo/remaining. QA memeriksa konfirmasi UI dan data sesudah restart. Tech Lead dan reviewer database menilai perubahan sebelum gate M1 diterima.
