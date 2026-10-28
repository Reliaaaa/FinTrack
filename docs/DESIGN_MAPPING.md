# Pemetaan desain FinTrack

Sumber: arsip desain Stitch yang diberikan pengguna. Implementasi native Jetpack Compose.

| Kelompok desain | Implementasi | Status |
| --- | --- | --- |
| Dasbor | Navy, kartu saldo biru, arus kas bulanan, aksi cepat, grafik 7 hari, anggaran, transaksi terakhir | Diimplementasikan dengan data nyata |
| Catat transaksi | Form transaksi dan dialog edit, rekening, kategori, tanggal, catatan | Inti diimplementasikan. Lampiran struk dan rewards belum tersedia |
| Modal batas anggaran | Batas per kategori berulang bulanan, progres, peringatan 80% dan 100% | Diimplementasikan |
| Aset tabungan rekening | Rekening likuid/nonlikuid, nilai bersih, alokasi, aset investasi | Inti diimplementasikan. Bunga/admin otomatis dan rekonsiliasi belum tersedia |
| Target tabungan impian | Target nominal, tanggal, progres persisten, setoran bulanan | Diimplementasikan |
| Laporan | Periode bulanan, filter rekening, breakdown, rasio tabungan, CSV | Inti diimplementasikan. PDF dan pengiriman email belum tersedia |
| Pasar saham portofolio | Catatan saham manual, modal, P/L. Harga kripto Indodax dan grafik | Sebagian. API saham IDX dan broker belum terhubung |
| Harga emas dan kurs | Nilai emas manual | Kurs otomatis belum tersedia |
| Pembaca e statement | Impor CSV standar dengan pratinjau dan deduplikasi | PDF bank belum tersedia |
| OCR struk dan detail item | Belum ada pemindai OCR atau penyimpanan foto | Rencana lanjutan |
| Pengingat kuota dan siklus SIM | Belum ada model kuota atau penjadwal | Rencana lanjutan |
| Widget | Belum ada Android app widget | Rencana lanjutan |
| Edit profil dan biometrik | Pengaturan pemasukan dan izin notifikasi | Profil dan biometrik belum tersedia |

Komponen statis yang menyatakan terverifikasi, terhubung ke bank, AES-256, sinkronisasi otomatis, atau rewards tidak digunakan sebagai klaim pada aplikasi.

Fitur tambahan di dalam gaya desain yang sama: batas aman harian, simulasi kas 90 hari, transfer internal, kalkulator bagi tagihan, dana musiman, dan target tabungan.
