---
name: fintrack-designer
description: "Buat atau review alur layar dan desain FinTrack, termasuk Canva, token, state dan handoff Compose. Gunakan untuk desain aplikasi ini; bukan ilustrasi umum atau klaim fitur aktif."
---

# FinTrack — Designer

Baca docs/planning/03-screen-map.md dan baseline perilaku sebelum mockup. Jika memakai Canva, temukan tools Canva dan ikuti batas edit/preview-nya; gunakan skill presentasi hanya bila output memang presentasi. Untuk gambar raster gunakan imagegen saat tepat, bukan mengganti spesifikasi interaksi dengan gambar.

Baseline token: latar #0B1326, kartu #171F33, primary #2563EB, positif #4EDEA3, teks #DAE2FD. Pilih token konsisten saat sumber berbeda. Android memakai dp/sp, target sentuh minimum 48dp dan font yang tersedia; ukur kontras. Rancang empty, loading, error, offline, izin ditolak, sukses dan pembatalan untuk setiap alur relevan. Offline tidak menghalangi pencatatan lokal. Bedakan kunci aplikasi dengan login cloud.

Gunakan ID layar yang stabil dan petakan ke backlog. Transfer internal hanya catatan; tombol tidak boleh menyiratkan uang dikirim bank. Angka contoh diberi label; biometrik aktif, keamanan dan bank terhubung hanya berdasarkan status nyata. Import harus preview/validasi sebelum commit; restore menjelaskan penggantian data serta pemulihan saat gagal.

Canva hasil generasi adalah draft sampai copy, ID, nilai dan alurnya diperiksa. Perbaiki label salah dan catat isu yang belum diselesaikan; jangan sebut final/handoff-ready jika masih ada cacat. Output berupa alur, daftar layar, token, state matrix, copy, tautan desain dan catatan QA; QA teks bukan pengganti inspeksi visual.
