# FinTrack Finance Tracker

Versi pengembangan 0.1.0 • 27 Oktober 2026 • Disiapkan untuk Varellio

FinTrack menggabungkan pencatatan transaksi, rekening pribadi, anggaran, target tabungan, tagihan, serta catatan portofolio dalam satu aplikasi Android. Dokumen ini menjelaskan perilaku yang diimplementasikan, perhitungan yang digunakan, cara mencoba aplikasi, dan pekerjaan lanjutan untuk fitur yang belum terhubung.

Implementasi melanjutkan fondasi Kotlin dan Jetpack Compose dari proyek awal. Identitas aplikasi diubah menjadi FinTrack dengan package id.fintrack.app. Desain mengikuti referensi yang diberikan: latar navy, kartu saldo biru, aksen hijau, kartu bersudut membulat, serta tampilan berbahasa Indonesia. Angka pada layar berasal dari data pengguna. Aplikasi baru dimulai tanpa saldo contoh.

## Cakupan versi ini

| Kelompok | Perilaku |
| --- | --- |
| Pencatatan | Rekening, pemasukan, pengeluaran, edit, hapus, dan transfer internal. |
| Perencanaan | Anggaran kategori, target tabungan, tagihan, batas belanja harian, dan simulasi 90 hari. |
| Data dan analisis | Impor CSV dengan tinjauan, ekspor CSV, serta laporan periode bulanan. |
| Portofolio | Catatan aset manual, harga kripto Indodax, dan grafik Lightweight Charts. |
| Banking | Pengelolaan catatan rekening. Belum memindahkan uang melalui bank. |

## Cara membaca status

Istilah diimplementasikan berarti kode dan alur tersedia dalam proyek. Hasil pengujian dicatat terpisah pada bagian validasi. Integrasi bersyarat memerlukan koneksi internet atau izin perangkat. Rencana berarti fitur belum dapat digunakan dalam versi ini. Dokumen tidak menyamakan rancangan antarmuka dengan fitur yang sudah bekerja.

# Mulai menggunakan aplikasi

FinTrack Finance Tracker  |  Bagian 02

## Pemasangan dan proyek

APK pengembangan dapat dipasang pada perangkat Android 8.0 atau lebih baru. Buka berkas APK di perangkat, lalu ikuti permintaan izin pemasangan dari sumber berkas yang digunakan. APK ini adalah build debug untuk mencoba fitur. Paket rilis untuk distribusi umum perlu menggunakan kunci penandatanganan milik pengembang dan pemeriksaan perangkat yang lebih luas.

Untuk mengembangkan kode, ekstrak proyek, lalu buka direktori fintrack-android di Android Studio. Proyek memakai JDK 17, compile SDK 35, target SDK 35, dan Build Tools 35.0.0. Biarkan Gradle mengunduh dependensi. Jalankan konfigurasi app pada emulator atau ponsel. Berkas local.properties mengikuti lokasi Android SDK di komputer masing-masing dan tidak disertakan dalam paket sumber.

## Pengaturan awal

Pertama, buka Portofolio atau Aset dan tambahkan rekening. Isikan nama seperti BCA, uang tunai, atau GoPay, lalu masukkan saldo saat mulai mencatat. Pilih apakah rekening termasuk uang harian. Rekening yang tidak termasuk uang harian tetap menambah total aset, tetapi tidak digunakan untuk batas aman belanja dan proyeksi kas.

Kedua, buka Pengaturan melalui dasbor. Isi tanggal pemasukan bulanan dan nominalnya. Tambahkan jadwal tagihan, cicilan, atau paylater melalui Semua Fitur. Angka batas aman belanja akan berubah berdasarkan saldo likuid dan kewajiban sebelum pemasukan berikutnya. Ketiga, catat transaksi baru dan atur anggaran kategori yang paling sering digunakan.

## Mencoba tanpa mencampur data nyata

Gunakan rekening bernama Uji Coba untuk latihan. Berkas samples/mutasi-contoh.csv berisi data contoh dengan penanda referensi DEMO. Atur saldo awal rekening percobaan menjadi nol sebelum mengimpor contoh. Setelah selesai, hapus transaksi percobaan terlebih dahulu, lalu hapus rekeningnya. Aplikasi tidak menambahkan data contoh secara otomatis saat dibuka.

Tidak ada akun login FinTrack pada versi ini. Data berada pada perangkat dan tidak berpindah otomatis ke ponsel lain. Ekspor CSV menjaga salinan riwayat transaksi, tetapi bukan cadangan lengkap seluruh pengaturan, tagihan, aset, dan target.

# Implementasi desain yang diberikan

FinTrack Finance Tracker  |  Bagian 03

Referensi desain digunakan sebagai sumber warna, susunan informasi, label navigasi, dan bentuk kartu. Implementasi dibuat sebagai layar Android native. Konten statis seperti nama Sarah, saldo contoh, status terverifikasi, dan klaim keamanan pada mockup diganti dengan data atau status yang benar-benar tersedia.

| Layar referensi | Penerapan pada FinTrack |
| --- | --- |
| Dasbor | Kartu saldo biru, ringkasan pemasukan dan pengeluaran, aksi cepat, grafik tujuh hari, anggaran, dan transaksi terakhir. |
| Catat transaksi | Jenis pemasukan atau pengeluaran, nominal, kategori, rekening, catatan, dan tanggal. Transfer memakai alur terpisah. |
| Aset dan rekening | Saldo per rekening, kekayaan bersih, alokasi aset, modal, harga, serta untung rugi. |
| Anggaran | Batas per kategori, progres, sisa anggaran, serta status mendekati atau melewati batas. |
| Laporan | Pilihan bulan dan rekening, ringkasan kas, grafik komposisi, rincian kategori, dan ekspor CSV. |
| Target tabungan | Target nominal, tanggal, progres manual, dan estimasi setoran bulanan. |
| OCR dan e statement PDF | Belum tersedia. Impor CSV tersedia sebagai jalur yang sudah diimplementasikan. |
| Widget dan kuota SIM | Masuk rencana lanjutan. Tidak ditampilkan sebagai fungsi aktif. |

## Sistem visual

Warna latar utama adalah #0B1326. Kartu menggunakan #171F33. Kartu saldo dan tombol penting memakai #2563EB, sementara indikator positif menggunakan #4EDEA3. Teks utama berwarna #DAE2FD. Judul menggunakan Plus Jakarta Sans yang dibundel untuk penggunaan offline. Grafik memakai nilai nyata dari transaksi atau data pasar yang berhasil dimuat.

Desain sumber belum diterjemahkan seluruhnya secara satu banding satu. Dialog Android menyesuaikan ruang layar dan keyboard. Klaim biometrik aktif, AES 256, poin rewards, sinkronisasi bank, dan akun broker terhubung tidak dipasang sebagai dekorasi karena fungsi tersebut belum tersedia.

# Dasbor dan batas aman belanja

FinTrack Finance Tracker  |  Bagian 04

## Informasi yang ditampilkan

Dasbor menyatukan saldo likuid, pemasukan bulan berjalan, pengeluaran bulan berjalan, grafik pengeluaran tujuh hari terakhir, ringkasan anggaran, transaksi terbaru, serta jumlah draf notifikasi. Ikon mata menyembunyikan nominal pada dasbor selama pengaturan itu aktif. Fitur ini bukan kunci aplikasi dan tidak menyembunyikan data di layar lain.

Kartu saldo menghitung hanya rekening yang ditandai sebagai uang harian. Perbedaan antara saldo likuid dan total aset disengaja. Saham, emas, atau rekening yang dikecualikan tidak dianggap siap dipakai untuk belanja sehari-hari. Transfer antar-rekening tidak menambah pemasukan atau pengeluaran bulanan.

## Rumus batas aman harian

Sisa bebas = saldo likuid dikurangi total tagihan yang belum ditandai lunas dan jatuh tempo sampai sehari sebelum pemasukan berikutnya. Batas harian = sisa bebas positif dibagi jumlah hari dalam periode tersebut. Hasil rupiah dibulatkan ke bawah. Jika sisa bebas negatif, batas harian menjadi nol dan kartu menampilkan peringatan kekurangan kas.

| Komponen contoh | Nilai |
| --- | --- |
| Saldo likuid | Rp3.000.000 |
| Tagihan sebelum pemasukan | Rp900.000 |
| Sisa bebas | Rp2.100.000 |
| Jumlah hari | 14 hari |
| Batas harian | Rp150.000 |

## Aturan dan batas perhitungan

Jika tanggal pemasukan jatuh pada hari ini, perhitungan menggunakan jadwal bulan berikutnya. Aplikasi menganggap transaksi pemasukan hari ini sudah tercermin pada saldo awal. Tanggal 31 pada bulan yang lebih pendek bergeser ke hari terakhir bulan tersebut. Nilai tagihan berasal dari jadwal yang pengguna masukkan, sehingga tagihan yang belum tercatat tidak dapat ikut dicadangkan.

Target tabungan tidak otomatis mengunci saldo. Pengguna dapat menaruh dana target pada rekening yang tidak termasuk uang harian agar dana itu dikeluarkan dari perhitungan. Batas harian juga belum menyediakan cadangan darurat terpisah. Angka ini adalah hasil dari data yang tercatat, bukan jaminan bahwa seluruh pengeluaran mendatang sudah tercakup.

# Rekening dan transfer internal

FinTrack Finance Tracker  |  Bagian 05

## Rekening sebagai sumber saldo

Rekening memiliki nama, jenis, saldo, dan penanda uang harian. Jenis yang tersedia mencakup kas, bank, dompet digital, serta lainnya. Nama rekening tidak memerlukan nomor rekening atau kredensial bank. Saldo awal diisi manual sebagai titik mulai pencatatan. Pemasukan menambah saldo dan pengeluaran mengurangi saldo rekening terkait.

Pengguna dapat memiliki beberapa rekening dengan tujuan berbeda. Contohnya, rekening belanja harian ditandai likuid, sedangkan rekening dana pendidikan tidak. Keduanya masuk ke total aset. Hanya rekening likuid yang memengaruhi batas belanja harian dan proyeksi. Rekening yang sudah memiliki riwayat tidak langsung dihapus agar saldo dan jejak transaksi tidak terputus.

## Pencatatan transfer

Alur transfer meminta rekening sumber, rekening tujuan, dan nominal. Sumber dan tujuan harus berbeda. Nominal harus positif dan tidak boleh melebihi saldo sumber. Aplikasi menyimpan dua baris transaksi yang memiliki identitas grup sama. Saldo sumber berkurang dan saldo tujuan bertambah dalam satu operasi database.

| Skenario | Perubahan |
| --- | --- |
| BCA ke GoPay sebesar Rp200.000 | BCA berkurang Rp200.000 dan GoPay bertambah Rp200.000. |
| Total saldo kedua rekening | Tetap sama jika keduanya termasuk kelompok yang sama. |
| Laporan pemasukan dan pengeluaran | Tidak bertambah akibat transfer internal. |
| Menghapus satu sisi transfer | Kedua baris dihapus dan kedua saldo dikembalikan. |

Transfer pada FinTrack adalah pencatatan. Tombolnya tidak mengirim perintah pembayaran ke bank. Pengguna tetap melakukan transfer sebenarnya melalui aplikasi penyedia rekening. Untuk transfer dari rekening likuid ke rekening nonlikuid, total aset tetap sama, tetapi kas yang dapat digunakan sehari-hari akan turun.

## Pembatasan versi ini

Belum ada biaya transfer terpisah di formulir. Catat biaya administrasi sebagai pengeluaran tambahan. Belum tersedia rekonsiliasi saldo otomatis, nomor rekening terenkripsi khusus, bunga rekening otomatis, atau koneksi RDN. Pencatatan selisih saldo dapat dilakukan sebagai transaksi koreksi dengan catatan yang jelas.

# Pencatatan dan koreksi transaksi

FinTrack Finance Tracker  |  Bagian 06

## Data transaksi

Formulir transaksi memuat jenis, nominal rupiah, tanggal, catatan, kategori, dan rekening sumber. Jenis manual adalah pemasukan atau pengeluaran. Tanggal menggunakan format YYYY-MM-DD dan tidak boleh melewati hari ini. Nominal harus positif. Transaksi tanpa rekening tidak dapat disimpan melalui formulir manual.

Catatan membantu aplikasi menyarankan kategori dengan aturan kata kunci lokal. Kata seperti kopi atau makan dapat mengarah ke kategori Makan, sedangkan gaji mengarah ke Gaji. Pengguna tetap dapat mengganti hasilnya. Setelah kategori disentuh pengguna, perubahan catatan tidak terus menimpa pilihan tersebut. Fungsi ini tidak menggunakan model AI atau mengirim catatan ke layanan eksternal.

## Riwayat dan pencarian

Riwayat menampilkan transaksi terbaru lebih dahulu. Pencarian mencocokkan catatan atau nama kategori. Filter membatasi daftar ke semua transaksi, pengeluaran, pemasukan, atau transfer. Transaksi yang berasal dari notifikasi belum memengaruhi saldo sampai pengguna mengonfirmasinya. Sumber pencatatan tetap disimpan untuk membedakan input manual, notifikasi, dan impor CSV.

## Mengubah transaksi

Edit transaksi mengembalikan dampak transaksi lama, memperbarui isinya, lalu menerapkan dampak baru dalam satu transaksi database. Dengan cara ini, perubahan nominal atau perpindahan rekening tidak menghasilkan penambahan saldo ganda. Contohnya, mengubah pengeluaran Rp50.000 menjadi Rp40.000 akan mengembalikan Rp10.000 ke rekening yang sama.

Transfer internal tidak diedit dari formulir transaksi biasa. Hapus pasangan transfer yang salah, lalu catat transfer pengganti. Penghapusan meminta konfirmasi. Untuk transaksi biasa, saldo dikembalikan sesuai arah transaksi. Untuk transfer, kedua sisi dikembalikan bersama. Tombol hapus tidak berarti membatalkan pembayaran nyata pada bank.

## Tanggal dan presisi

Nominal uang disimpan sebagai bilangan bulat rupiah. Input tanggal manual disimpan pada pukul 12.00 zona perangkat agar laporan berbasis tanggal tetap mudah dipahami. Jam asli transaksi tidak dapat dipilih pada versi ini. Jika pengguna mengubah zona waktu perangkat, batas tanggal pada transaksi berbasis waktu perlu diperhatikan saat membandingkan laporan.

# Anggaran dan target tabungan

FinTrack Finance Tracker  |  Bagian 07

## Anggaran per kategori

Pengguna menetapkan batas bulanan untuk kategori seperti makan, transportasi, belanja, atau hiburan. Satu kategori memiliki satu batas aktif. Mengatur kategori yang sama mengganti batas sebelumnya. Jumlah terpakai dihitung dari transaksi pengeluaran pada bulan berjalan, sehingga edit dan penghapusan transaksi langsung memengaruhi progres anggaran.

Status normal ditampilkan ketika pemakaian di bawah 80 persen. Mulai 80 persen, kategori dianggap mendekati batas. Pada 100 persen atau lebih, antarmuka menampilkan status melewati batas. Progres visual dibatasi sampai penuh, sedangkan nominal tetap menunjukkan kelebihan sebenarnya. Pengguna tetap dapat mencatat transaksi ketika batas terlampaui.

Batas yang dibuat berlaku berulang setiap bulan. Aplikasi belum menyimpan versi batas berbeda untuk setiap bulan dan belum memindahkan sisa anggaran ke bulan berikutnya. Menghapus batas tidak menghapus transaksi. Dasbor menampilkan beberapa anggaran pertama sebagai ringkasan, sementara layar Anggaran menampilkan seluruh batas yang tersimpan.

## Target tabungan impian

Setiap target menyimpan nama, nominal tujuan, jumlah yang sudah terkumpul, dan tanggal sasaran. Pengguna dapat menambah target, mengubah progres, atau menghapusnya. Nominal terkumpul tidak boleh negatif atau melebihi target. Target ditandai tercapai ketika progres sama dengan nominal tujuan.

Estimasi setoran bulanan dihitung dari sisa target dibagi selisih bulan kalender. Pembagi minimal satu bulan. Hasil dibulatkan ke atas agar nominal bulanan tidak terlalu rendah akibat pembulatan. Target Rp6.000.000 dengan progres Rp1.000.000 dan lima bulan tersisa menghasilkan estimasi Rp1.000.000 per bulan.

## Perbedaan dengan saldo rekening

Target adalah catatan kemajuan, bukan rekening baru. Mengubah progres target tidak mengurangi kas, tidak menciptakan transaksi, dan tidak menambah kekayaan bersih. Catat perpindahan uang sebenarnya melalui rekening. Jika dana tersebut perlu dipisahkan dari batas harian, gunakan rekening nonlikuid. Target yang sudah melewati tanggal sasaran tetap dapat diperbarui, dengan kebutuhan setoran dihitung menggunakan minimal satu bulan.

# Tagihan cicilan dan proyeksi kas

FinTrack Finance Tracker  |  Bagian 08

## Jadwal kewajiban

Jadwal menyimpan nama, jenis, nominal bulanan, tanggal jatuh tempo, dan sisa jumlah pembayaran. Jenis mencakup cicilan, paylater, arisan, tagihan rutin, serta zakat dan sedekah. Sisa pembayaran kosong berarti berulang tanpa batas. Nilai nol berarti selesai. Tanggal jatuh tempo menyesuaikan panjang bulan.

Tandai lunas memperbarui status bulan berjalan dan mengurangi jumlah pembayaran tersisa jika jumlahnya dibatasi. Operasi ini tidak membuat pengeluaran otomatis. Dialog meminta pengguna mencatat pembayaran pada layar Transaksi terlebih dahulu. Menandai jadwal yang sama dua kali pada bulan yang sama tidak mengurangi jumlah pembayaran lagi.

## Proyeksi 90 hari

Simulasi memakai saldo likuid saat ini, nominal pemasukan bulanan, tanggal pemasukan, jadwal kewajiban, asumsi pengeluaran harian, dan satu rencana pembelian hari ini. Titik awal mengurangi rencana pembelian serta tagihan hari ini yang belum ditandai lunas. Mulai besok, saldo diperbarui setiap hari selama 90 hari.

Saldo hari berikutnya = saldo hari sebelumnya + pemasukan terjadwal − belanja harian − tagihan pada tanggal tersebut. Tagihan dengan jumlah pembayaran terbatas berhenti ketika jatahnya habis. Jadwal yang sudah ditandai lunas dilewati untuk bulan yang terkait. Saldo boleh turun di bawah nol agar kekurangan dana terlihat, bukan disembunyikan.

## Hasil dan perbandingan

Layar menampilkan grafik saldo, titik saldo terendah, tanggal saldo pertama kali negatif, saldo akhir, dan ringkasan setiap 15 hari. Nilai terendah tanpa rencana pembelian ditampilkan sebagai pembanding. Pengguna dapat mengubah asumsi belanja harian atau nilai pembelian untuk melihat pengaruhnya sebelum membuat transaksi nyata.

Belanja harian harus diisi di luar tagihan yang sudah dijadwalkan agar tidak dihitung dua kali. Aplikasi belum memprediksi kebiasaan otomatis dari riwayat, belum memasukkan inflasi, dan belum memperkirakan hasil investasi. Pemasukan yang terjadi hari ini dianggap sudah tercermin pada saldo. Input simulasi tidak langsung mengubah rekening.

# Portofolio dan integrasi pasar

FinTrack Finance Tracker  |  Bagian 09

## Catatan aset

Aset investasi menyimpan nama, kelas aset, jumlah unit, modal total, harga per unit, serta waktu pembaruan harga. Kelas aset meliputi saham, emas, reksa dana, kripto, dan kategori lain yang tersedia pada formulir. Nilai aset dihitung sebagai jumlah unit dikalikan harga per unit. Untung atau rugi belum terealisasi adalah nilai aset dikurangi modal.

Unit saham menggunakan lembar sesuai label aplikasi, bukan lot. Pengguna perlu mengubah jumlah lot ke lembar sebelum memasukkan kepemilikan. Harga emas dimasukkan per gram. Modal adalah total biaya perolehan yang pengguna isi, sehingga biaya transaksi dapat dimasukkan ke modal jika ingin ikut tercermin. Menambah aset tidak otomatis mengambil uang dari rekening.

## Kekayaan bersih

Total aset menggabungkan saldo rekening dan nilai portofolio. Kewajiban dengan sisa pembayaran terbatas ikut mengurangi kekayaan bersih. Nilai utang ini menggunakan nominal pembayaran dikalikan sisa pembayaran, sehingga dapat mencakup bunga atau biaya yang ada dalam angsuran. Angka tersebut bukan rincian pokok pinjaman dari bank.

## Sumber harga dan grafik

Harga kripto dapat diperbarui melalui API publik Indodax menggunakan pasangan seperti btc_idr. Grafik candlestick menggunakan TradingView Lightweight Charts 5.2.1 dengan data OHLC dari Indodax. Pustaka grafik disertakan dalam aplikasi, beserta lisensi dan atribusinya. Koneksi internet dibutuhkan untuk mengambil data pasar. Pembaruan harga dipicu pengguna, bukan koneksi streaming.

Harga saham, emas, dan reksa dana tetap diisi manual. Harga yang gagal diambil tidak diganti dengan data contoh. Aplikasi menampilkan waktu pencatatan harga agar pengguna bisa mengenali data lama. Nilai harga disimpan dalam rupiah bulat, sehingga aset dengan harga di bawah satu rupiah belum didukung secara presisi.

## Stockbit dan TradingView

Versi ini tidak memakai token sesi Stockbit dan tidak mengeksekusi order. API resmi pihak ketiga Stockbit belum berhasil diverifikasi. TradingView menyatakan API data umum belum tersedia dan REST API mereka ditujukan untuk broker. Penggunaan Lightweight Charts tidak memberikan data saham maupun akses transaksi broker. Integrasi saham berikutnya membutuhkan penyedia data dan hak penggunaan yang jelas.

# Impor mutasi dan ekspor data

FinTrack Finance Tracker  |  Bagian 10

## Format impor

Impor menerima CSV UTF-8 dengan pemisah koma. Header wajib adalah tanggal, jenis, jumlah, kategori, dan catatan. Kolom ref bersifat opsional. Tanggal memakai YYYY-MM-DD atau tanggal dan waktu ISO. Jenis harus INCOME atau EXPENSE. Jumlah merupakan rupiah bulat positif tanpa pemisah ribuan. Kategori yang tidak dikenali masuk ke Lainnya.

| Kolom | Contoh dan arti |
| --- | --- |
| tanggal | 2026-10-02 |
| jenis | EXPENSE |
| jumlah | 25000 |
| kategori | MAKAN atau Makan |
| catatan | Makan siang |
| ref | INV20261002A, identitas tambahan opsional |

Berkas dibatasi 1 MB dan 2.000 transaksi. Teks berisi koma dapat diapit tanda kutip ganda sesuai format CSV. Berkas ditolak bila tanggal, jumlah, atau jenis tidak valid. Pesan kesalahan menunjukkan baris yang perlu diperiksa. Sistem tidak menyimpan sebagian berkas ketika ada baris yang gagal divalidasi.

## Tinjauan dan duplikat

Setelah memilih berkas, pengguna melihat jumlah transaksi dan pratinjau hingga 100 baris pertama. Seluruh baris tetap diperiksa. Pengguna memilih satu rekening tujuan dan mengonfirmasi sebelum transaksi masuk ke database. Saldo rekening berubah sesuai jumlah pemasukan dan pengeluaran dari berkas.

Identitas duplikat dihitung dari rekening, tanggal, jenis, jumlah, kategori, catatan, dan ref. Mengimpor ulang isi sama ke rekening sama melewati baris yang sudah tersimpan. Untuk dua transaksi nyata dengan isi identik, gunakan ref berbeda. Mekanisme ini mencegah pengulangan impor CSV, tetapi tidak otomatis menyatukan transaksi manual atau notifikasi yang isinya mirip.

## Ekspor dan saldo awal

Ekspor menghasilkan CSV seluruh transaksi terkonfirmasi, termasuk sumber dan pasangan transfer. Sel teks yang berpotensi dibaca sebagai rumus spreadsheet diberi awalan pelindung. Ekspor bukan cadangan database lengkap. CSV ekspor yang memuat transfer tidak dapat langsung diimpor karena impor hanya menerima pemasukan dan pengeluaran. Siapkan saldo awal sebelum periode mutasi agar impor tidak menggandakan saldo yang sudah mencakup transaksi tersebut.

# Notifikasi dan perlindungan data

FinTrack Finance Tracker  |  Bagian 11

## Draf dari notifikasi

Fitur notifikasi bersifat opsional dan nonaktif secara default. Pengguna harus menyalakan sakelar di FinTrack serta memberikan akses notifikasi melalui pengaturan Android. Layanan hanya mencoba membaca paket aplikasi yang termasuk daftar sumber. Isi yang menyerupai OTP, kode verifikasi, promosi, atau limit disaring sebelum dibuat sebagai draf.

Pengenalan memakai kata kunci untuk arah transaksi, pola nominal rupiah, dan perkiraan nama tujuan. Data yang disimpan adalah hasil parsing, bukan seluruh isi pesan. Draf belum memiliki rekening sampai pengguna memilihnya. Menyetujui draf mengubah status dan saldo secara atomik. Pengguna dapat membuang draf yang salah tanpa memengaruhi saldo.

## Batas pengenalan

Format bank dan e-wallet dapat berubah. Daftar paket serta pola pesan perlu diuji pada perangkat dan versi aplikasi yang digunakan. Notifikasi yang tidak membawa nominal dapat terlewat. Perubahan identitas atau waktu notifikasi dapat menghasilkan draf tambahan. Pengguna harus meninjau draf dan membandingkannya dengan transaksi yang sudah dicatat sebelum mengonfirmasi.

Aplikasi tidak membaca SMS, meminta PIN bank, meminta password bank, atau menyimpan API key broker. Pengenalan notifikasi bukan koneksi rekening resmi. Mengaktifkan fitur tidak berarti seluruh mutasi bank tersinkronisasi. Menolak izin tetap memungkinkan pencatatan manual dan impor CSV.

## Penyimpanan

Room menyimpan database dalam ruang privat aplikasi. Android backup dinonaktifkan pada manifest. Versi ini belum menerapkan enkripsi database khusus aplikasi, kunci biometrik, autentikasi cloud, atau pemulihan cadangan lengkap. Karena itu, layar tidak mengklaim perlindungan AES 256 atau sinkronisasi terenkripsi yang belum diimplementasikan.

Permintaan pasar dikirim ke domain Indodax untuk harga atau simbol yang diminta. Catatan keuangan tidak dikirim sebagai bagian dari permintaan tersebut. Ekspor CSV membuat berkas biasa di lokasi yang dipilih pengguna. Berkas ekspor berada di luar perlindungan ruang privat aplikasi dan perlu dikelola sesuai kebutuhan pengguna.

## Penghapusan

Hapus Semua Data meminta konfirmasi dan menghapus rekening, transaksi, draf, tagihan, aset, anggaran, serta target. Pengaturan tanggal pemasukan dan sakelar notifikasi tetap merupakan pengaturan aplikasi. Berkas CSV yang sudah diekspor tidak ikut terhapus.

# Laporan dan alat tambahan

FinTrack Finance Tracker  |  Bagian 12

## Laporan bulanan

Pengguna dapat berpindah ke bulan sebelumnya dan kembali sampai bulan berjalan. Filter rekening membatasi laporan pada satu rekening, dengan opsi kembali ke semua rekening. Laporan menampilkan pemasukan, pengeluaran, selisih, rasio tabungan jika pemasukan positif, grafik komposisi pengeluaran, serta jumlah per kategori.

Transfer internal tidak masuk sebagai pemasukan atau pengeluaran, termasuk ketika melihat satu rekening. Saldo yang berpindah tetap terlihat pada riwayat transfer. Rasio tabungan dihitung dari selisih pemasukan dan pengeluaran dibagi pemasukan. Nilai dapat negatif. Jika pemasukan nol, rasio tidak ditampilkan untuk menghindari pembagian dengan nol.

Evaluasi kas memakai aturan sederhana. Jika tidak ada pemasukan, teks menjelaskan kekosongan data. Jika pengeluaran lebih besar, teks mengarahkan perhatian pada kategori dan tagihan. Jika selisih positif, nominal sisa ditampilkan. Fitur ini tidak mengklaim menghasilkan rekomendasi AI. Tombol ekspor menghasilkan seluruh riwayat CSV, terlepas dari filter laporan yang sedang dipilih.

## Bagi tagihan

Alat bagi tagihan menerima daftar peserta, item pembelian, peserta yang berbagi setiap item, biaya tambahan, dan diskon. Biaya tambahan serta diskon dibagi secara proporsional terhadap bagian peserta. Perhitungan menggunakan pembagian rupiah dan penyesuaian sisa agar total pembagian tetap sama dengan total tagihan. Hasil dapat dibagikan melalui menu berbagi Android setelah pengguna memilih aplikasi tujuan.

Kalkulator tidak membuat utang, piutang, permintaan pembayaran, atau transaksi otomatis. Pengguna mencatat pembayaran sendiri. Nama peserta dan rincian item merupakan isi yang pengguna masukkan. Aplikasi tidak mengakses kontak untuk menentukan penerima.

## Dana musiman dan kalkulator zakat

Dana musiman menghitung kebutuhan setoran berdasarkan target, jumlah terkumpul, serta tanggal sasaran. Alat ini tetap tersedia dari fondasi proyek awal. Untuk menyimpan beberapa tujuan, gunakan Target Tabungan. Kalkulator zakat memakai parameter sederhana, termasuk harga emas yang dimasukkan pengguna dan ketentuan yang tertulis di layar. Hasil kalkulator tidak dipotong otomatis dari rekening dan tidak melakukan pembayaran ke lembaga mana pun.

# Arsitektur dan model data

FinTrack Finance Tracker  |  Bagian 13

Aplikasi memakai satu activity dengan UI Jetpack Compose. Navigasi menghubungkan dasbor, transaksi, anggaran, aset, laporan, dan layar alat. MainViewModel menggabungkan aliran data untuk kebutuhan UI. Repository mengatur perubahan database. Logika perhitungan ditempatkan pada paket domain agar dapat diuji tanpa Android SDK.

| Entitas | Isi utama |
| --- | --- |
| AccountEntity | Nama, jenis, saldo rupiah, dan penanda kas likuid. |
| TransactionEntity | Rekening, arah, nominal, kategori, catatan, waktu, sumber, status, fingerprint, dan grup transfer. |
| ObligationEntity | Nama, jenis, nominal bulanan, hari jatuh tempo, sisa pembayaran, serta bulan terakhir dibayar. |
| HoldingEntity | Nama aset, kelas, simbol opsional, jumlah unit, modal, harga per unit, dan waktu harga. |
| BudgetEntity | Kategori sebagai kunci serta batas nominal bulanan. |
| GoalEntity | Nama target, nominal tujuan, progres, dan tanggal sasaran. |

## Aturan konsistensi

Penambahan transaksi dan perubahan saldo dilakukan di dalam transaksi Room. Edit membalik dampak lama sebelum menerapkan dampak baru. Transfer membuat dua baris dalam grup sama. Penghapusan transfer menghapus kedua baris dan membalik kedua saldo. Indeks unik pada fingerprint mencegah penambahan ulang baris impor atau identitas notifikasi yang sama.

Operasi menyimpan data menangani kegagalan dengan pesan di antarmuka. Permintaan harga dibatasi satu pekerjaan aktif untuk mencegah tombol refresh membuat banyak permintaan serentak. Saat simbol grafik berubah, pekerjaan grafik sebelumnya dibatalkan agar hasil simbol lama tidak menimpa tampilan baru.

## Lokasi kode yang penting

Paket data memuat entities, DAO, repository, dan database. Paket domain memuat SafeToSpend, Forecast, CsvImport, SplitBill, Money, serta perhitungan aset. Paket market memuat PriceClient. Paket notif memuat listener Android. Folder ui/screens berisi layar aplikasi. Folder assets/chart memuat pustaka grafik dan lisensinya.

Namespace baru id.fintrack.app menggunakan database aplikasi tersendiri. Proyek tidak melakukan migrasi otomatis dari aplikasi Arus yang mungkin sudah terpasang. Untuk perubahan skema pada versi berikutnya, tambahkan migrasi Room dan pengujian upgrade sebelum merilisnya kepada pengguna dengan data tersimpan.

# Validasi dan skenario pengujian

FinTrack Finance Tracker  |  Bagian 14

Pengujian dibagi menjadi perhitungan domain, build Android, pemeriksaan lint, dan pengujian interaksi. Status akhir dicatat pada TEST_RESULTS.md di dalam paket sumber. Hasil build tidak dengan sendirinya membuktikan semua kondisi perangkat telah teruji. Data yang digunakan untuk pemeriksaan berasal dari contoh buatan, bukan rekening bank pengguna.

| Kelompok | Cakupan pemeriksaan |
| --- | --- |
| Domain | Format uang, kategori, parser notifikasi, jadwal cicilan, batas harian, aset, pembagian tagihan, target, dan laporan. |
| Proyeksi | Tanggal akhir bulan, gajian hari ini, defisit pertama, jumlah cicilan terbatas, dan tagihan yang sudah lunas. |
| CSV | Kutip dan koma, nominal negatif, pecahan, tanggal tidak valid, kolom wajib, serta identitas duplikat. |
| Android | Kompilasi APK debug, lint, instalasi, dan skenario UI yang berhasil dijalankan. |
| Pasar | Kesesuaian endpoint resmi, pemeriksaan respons ticker, serta penanganan jaringan gagal. |

## Skenario penerimaan utama

Buat dua rekening, lalu catat pemasukan dan pengeluaran. Pastikan saldo berubah tepat sekali. Edit nominal dan rekening sumber, lalu periksa saldo lama serta saldo baru. Hapus transaksi dan pastikan saldo kembali. Pindahkan dana antar-rekening dan pastikan total pemasukan serta pengeluaran tidak berubah. Hapus salah satu sisi transfer dan periksa kedua saldo.

Buat anggaran, lalu tambah pengeluaran sampai melewati 80 persen dan 100 persen. Pastikan status berubah. Buat target tabungan, ubah progres, lalu tutup dan buka ulang aplikasi untuk memeriksa persistensi. Buat cicilan dengan dua pembayaran tersisa dan pastikan simulasi tidak terus menguranginya pada bulan ketiga.

Impor berkas contoh dua kali ke rekening yang sama. Impor pertama harus menambah transaksi, sedangkan impor kedua harus melewati duplikat. Uji CSV salah format agar pesan kesalahan muncul sebelum penyimpanan. Coba permintaan harga saat jaringan terputus dan pastikan saldo keuangan tidak terpengaruh.

## Pemeriksaan sebelum distribusi lebih luas

Uji pada ukuran layar kecil, font besar, rotasi, perangkat Android versi terbaru, serta saat aplikasi dihentikan sistem. Periksa ulang alur notifikasi pada setiap bank yang ingin didukung. Audit kebijakan privasi dan alur data sesuai perilaku aplikasi yang benar-benar dirilis. Paket ini belum merupakan sertifikasi keamanan atau persetujuan distribusi toko aplikasi.

# Fitur lanjutan yang belum tersedia

FinTrack Finance Tracker  |  Bagian 15

Daftar berikut memisahkan fitur dalam referensi desain yang memerlukan pekerjaan tambahan. Tidak ada tombol aktif yang mengklaim sudah melakukan login bank, transaksi broker, pemindaian OCR, atau sinkronisasi cloud. Pengembangan berikutnya dapat memakai model data dan pola navigasi yang sudah tersedia.

| Tahap | Fitur | Pekerjaan berikutnya |
| --- | --- | --- |
| 1 | Cadangan dan pemulihan | Format cadangan lengkap, enkripsi, pemulihan atomik, serta uji data rusak. |
| 1 | Pembayaran tagihan terpadu | Hubungkan status lunas dengan transaksi dan dukung pembatalan tanpa inkonsistensi. |
| 1 | Kunci aplikasi | Autentikasi perangkat atau biometrik dengan penguncian ulang saat kembali dari latar belakang. |
| 2 | OCR struk | Pemilihan foto, OCR lokal, koreksi item, pajak, diskon, dan konfirmasi nominal sebelum menyimpan. |
| 2 | E statement PDF | Parser untuk format bank tertentu, penanganan dokumen terkunci, serta rekonsiliasi saldo. |
| 2 | Pengingat jatuh tempo | Penjadwalan Android, izin notifikasi, waktu lokal, dan pembaruan setelah restart perangkat. |
| 2 | Kuota kartu SIM dan widget | Model siklus kuota, konfigurasi tampilan, serta pembaruan widget yang hemat baterai. |
| 3 | Data saham IDX | Kontrak penyedia resmi, adapter backend, cache, sumber harga, dan aturan atribusi. |
| 3 | Banking langsung | Mitra layanan, autentikasi resmi, persetujuan pengguna, status transaksi, dan rekonsiliasi. |
| 3 | Sinkronisasi cloud | Akun, enkripsi, resolusi konflik, penghapusan akun, dan pemulihan data. |

## Urutan pengembangan yang disarankan

Utamakan integritas data dan pemulihan sebelum menambah koneksi uang nyata. Setelah cadangan, pembayaran tagihan terpadu, dan pengujian perangkat selesai, OCR serta pengingat dapat menambah kenyamanan penggunaan. Koneksi bank atau broker memerlukan kebutuhan produk, akses resmi, dan pengujian integrasi yang lebih luas. Dokumen ini tidak menetapkan bahwa semua layanan tersebut tersedia untuk akun perorangan.

# Rujukan teknis dan daftar keluaran

FinTrack Finance Tracker  |  Bagian 16

## Sumber desain dan kode

Rancangan visual berasal dari arsip stitch_personal_finance_tracker_app.zip yang diberikan pengguna. Fondasi kode berasal dari arus-android.zip. Dokumen awal daftar fitur digunakan sebagai konteks kebutuhan. Klaim integrasi, keamanan, dan fitur dalam dokumen awal tidak otomatis dianggap sudah tersedia pada implementasi FinTrack.

## Rujukan API dan pustaka

Rujukan berikut diperiksa pada 27 Oktober 2026. Gunakan dokumentasi penyedia ketika memperbarui integrasi. URL adalah sumber teknis, bukan endpoint yang menerima data keuangan pribadi FinTrack.

## Indodax Public REST API

https://github.com/btcid/indodax-official-api-docs/blob/master/Public-RestAPI.md

## TradingView tentang API data

https://www.tradingview.com/support/solutions/43000474413-i-need-access-to-your-api-in-order-to-get-data-or-indicator-values/

## Lightweight Charts Getting started

https://tradingview.github.io/lightweight-charts/docs

## Lightweight Charts source dan lisensi

https://github.com/tradingview/lightweight-charts

## Plus Jakarta Sans dan lisensi font

https://github.com/google/fonts/tree/main/ofl/plusjakartasans

## Keluaran proyek

Paket sumber memuat proyek Android, Gradle wrapper, kode domain, UI, contoh CSV, panduan build, catatan pengujian, pemetaan desain, dan dokumentasi fitur dalam format teks. Dokumen Word ini menjadi penjelasan fitur yang dapat diedit. APK debug, jika tercantum dalam hasil akhir, merupakan paket pemasangan untuk pengujian pribadi.

Belum ada akun produksi, API key privat, kredensial bank, penandatanganan rilis, atau publikasi ke Google Play. Semua pekerjaan berikutnya sebaiknya dimulai dari catatan pengujian dan daftar batas fitur agar perubahan baru tidak mengganggu saldo serta riwayat yang sudah tersimpan.
