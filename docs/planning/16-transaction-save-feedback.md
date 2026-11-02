# Simpan transaksi: hasil operasi dan retensi input

Scope: formulir tambah/ubah transaksi manual pada Riwayat, termasuk pintasan Catat Baru dari Dasbor. Tidak mengubah schema atau operasi saldo repository.

Sebelumnya formulir langsung ditutup setelah meluncurkan coroutine. Kesalahan repository menyebabkan input hilang dari layar. Kini MainViewModel mengekspos status saving/saved/error khusus transaksi; formulir ditutup hanya setelah repository berhasil. Kesalahan ditampilkan di dalam dialog dan input tetap tersedia selama komposisi dialog tersebut hidup. Simpan dapat dicoba kembali setelah gagal.

Status saving ditetapkan sebelum coroutine diluncurkan. Permintaan tambahan diabaikan selama saving maupun saved yang belum dikonsumsi. Saat saving, tombol Simpan/Batal dinonaktifkan, dialog tidak bisa ditutup lewat back/tap luar, dan kolom sementara diganti pesan progres sehingga input tidak berubah di tengah operasi. Sukses dikonsumsi oleh layar; batal menghapus pesan error.

## Pemeriksaan dan batas

- Review statis: tambah dan ubah menggunakan jalur saveTransaction; operasi lain tetap memakai mutate. CancellationException dilempar kembali. Default dismissEnabled=true menjaga perilaku dialog lain.
- git diff --check dan pemeriksaan tujuan navigasi dijalankan; bukan bukti build atau uji interaksi Android.
- Build/perangkat belum dijalankan karena hambatan environment pada tahap sebelumnya. Tidak ada klaim UI atau QA lulus.
- Retensi input lintas rotasi/process death belum ditangani. Penjagaan ketukan berulang ini bukan idempotency persisten lintas restart.
- Formulir tagihan, transfer dan impor belum mendapat perubahan ini.

## Uji penerimaan yang masih harus dijalankan

1. Tambah transaksi valid: progres terlihat, formulir tutup setelah sukses, tepat satu transaksi dan perubahan saldo sesuai nominal.
2. Gunakan rekening uji dengan saldo Long.MAX_VALUE dan tambah pemasukan 1: pesan overflow tampil, formulir dan input tetap ada, saldo/transaksi tidak berubah. Ubah nominal/rekening lalu coba lagi.
3. Ubah transaksi yang terikat pembayaran tagihan: penolakan repository tampil di dialog, input koreksi tetap tersedia, transaksi lama tidak berubah.
4. Ketuk Simpan cepat berulang dengan operasi tertunda: hanya satu operasi repository berjalan. Batal/back/tap luar selama saving tidak menutup dialog.
5. Setelah gagal, batal dan buka formulir baru: error sebelumnya hilang. Setelah sukses, buka lagi: formulir baru dapat disimpan.

Status review: membutuhkan build dan uji perangkat sebelum siap rilis.
