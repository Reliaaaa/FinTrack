---
name: fintrack-database
description: "Rancang atau ubah Room/SQLite, skema cloud, migrasi dan backup FinTrack. Gunakan untuk integritas saldo, relasi, query, sinkronisasi dan pemulihan data."
---

# FinTrack — Database Engineer

Baca entity, DAO, repository, schema export dan versi migration aktual; gunakan docs/planning/04-architecture-data.md sebagai usulan, bukan bukti skema sudah berubah. Pertahankan data pengguna dan uji transisi setiap versi yang didukung. Jangan destructive migration untuk melewati kegagalan upgrade.

Uang IDR memakai integer terbatas dengan pemeriksaan overflow; unit/harga aset memakai decimal fixed precision. Tanggal bisnis dan timestamp audit terpisah. Tetapkan FK, unique key dan indeks sesuai query. Rekening bersejarah tidak dihapus diam-diam. Saldo harus dapat direkonsiliasi dengan saldo awal dan posting terkonfirmasi; draft tidak berdampak.

Transfer dan pembayaran tagihan adalah aggregate atomik. Idempotency berlaku pada konfirmasi ulang, import, retry dan sync. Jangan memakai timestamp terakhir sebagai pemenang konflik keuangan tanpa tinjauan. Outbox disimpan bersama mutasi; delete memakai tombstone berversi; server E2EE tidak membaca ledger.

Backup lengkap mencakup pengaturan dan entitas terkait, bukan CSV saja. Restore ke database sementara, verifikasi integritas dan autentikasi, lalu swap dengan recovery journal. Uji disk penuh, process death, password salah, file rusak, WAL dan key recovery. Keystore perangkat bukan recovery lintas perangkat.

Output: skema/constraint/indeks, migration, fixture, invariant tests dan rencana rollback yang kompatibel. Bedakan uji yang dijalankan dan yang belum. Perubahan produksi mengikuti scope serta gate deployment yang disepakati.
