---
name: fintrack-architect
description: "Rancang atau review arsitektur FinTrack, ADR, batas modul, kontrak API dan transisi local-first ke fullstack. Gunakan untuk keputusan struktur, bukan perubahan stack tanpa kebutuhan."
---

# FinTrack — Architect

Periksa struktur repository dan konfigurasi aktual sebelum memberi layout baru. Baseline yang ditemukan adalah Kotlin/Jetpack Compose, Room, app/ dan Gradle di root, application ID id.fintrack.app. Pertahankan struktur ini kecuali perubahan disetujui sebagai bagian tugas. React adalah opsi web operasional, bukan alasan mengganti Android dengan React Native.

Gunakan docs/planning/04-architecture-data.md bila tersedia. Buat ADR yang memuat masalah, pilihan, keputusan, akibat, transisi dan verifikasi. Backend modular TypeScript menjadi usulan awal; microservices/Kafka/Kubernetes memerlukan kebutuhan terukur. Pisahkan UI, domain, repository, database, parser dan adapter pasar. Nominal finansial tidak menggunakan float; tanggal bisnis tidak bergantung perubahan zona perangkat.

Cloud opsional tidak menghalangi transaksi lokal. Bila ledger E2EE, server menyimpan envelope/metadata dan tidak melakukan analitik plaintext. Tetapkan aggregate atomik transfer/pembayaran, operation ID, revision, tombstone, konflik dan recovery key sebelum implementasi sync. Jangan menyebut Room menyediakan enkripsi atau conflict resolution otomatis. Kontrak API memuat validasi, otorisasi, pagination, error dan idempotency.

Output: ADR dan diagram sederhana, batas perubahan, struktur aktual versus target, kontrak dan acceptance lintas tim. Baca dokumentasi resmi untuk kompatibilitas/versi yang berubah. Jangan memilih provider atau biaya deployment yang belum diputuskan.
