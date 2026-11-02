# Implementasi desain tahap 1 — 2 November 2026

MB-001 / DS-001: lima tab mengikuti peta layar (Dasbor, Riwayat, Anggaran, Aset, Lainnya). Laporan tetap tersedia melalui Lainnya. Pintasan dasbor ke layar sekunder kini push ke back stack, sedangkan perpindahan tab mempertahankan save/restore state. Tagihan memiliki tombol kembali yang terlihat.

Menu Lainnya dibagi menjadi Rencana keuangan, Catatan dan laporan, Kalkulator, serta Preferensi aplikasi. Sepuluh tujuan memakai kartu navy existing, label yang menjelaskan tindakan, chevron dekoratif, margin 20dp dan target sentuh minimum 48dp. Item memiliki semantics Button. Label Catat transfer menjelaskan bahwa fitur hanya mencatat perpindahan saldo. Fitur cloud/OCR yang belum tersedia tidak ditambahkan sebagai tombol palsu.

Riwayat menggunakan tombol Catat dengan ikon dan teks. Tombol privasi saldo dasbor diperbesar dari 32dp menjadi 48dp, dengan label aksesibilitas Tampilkan/Sembunyikan sesuai keadaan. Token warna dan font existing dipertahankan pada tahap ini.

Pemeriksaan statis: lima tab; seluruh sepuluh menu unik menunjuk route terdaftar; pemanggil ObligationsScreen disesuaikan; diff check lulus. Belum menjalankan build Compose, emulator, screenshot aktual, uji TalkBack atau font besar. Ini bukan bukti seluruh 32 layar referensi selesai atau visual sudah sesuai. Daftar berikutnya: review visual dasbor/Riwayat/Lainnya, field/dialog, empty/loading/error state, kemudian layar aset dan anggaran. Canva tetap draft dengan koreksi yang tercatat.
