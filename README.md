# FinTrack - Smart Personal Finance & Wealth Management (Android)

FinTrack adalah aplikasi pengelola keuangan pribadi dan arus kas cerdas berbasis Android yang dibangun dengan **Kotlin** dan **Jetpack Compose (Material Design 3)**. Aplikasi ini dirancang untuk melacak arus kas harian, menyusun anggaran bulanan, memantau portofolio rekening & investasi, serta mendukung analitik mendalam.

## Fitur Utama (Core Features)

1. **Ringkasan (Dashboard / Cash Flow Overview)**
   - Total Saldo Tersedia dengan fitur sembunyikan/tampilkan nominal.
   - Ringkasan arus kas bulanan (Pemasukan vs Pengeluaran).
   - Grafik tren pengeluaran 7 hari interaktif.
   - Pemantauan real-time status anggaran aktif dengan indikator batas aman.
   - Daftar transaksi terbaru dan rekening terdaftar.

2. **Transaksi (Transactions Management)**
   - Pencarian real-time berdasarkan nama, kategori, rekening, atau catatan.
   - Filter cepat (Semua, Pemasukan, Pengeluaran, Kategori).
   - Pencatatan transaksi baru (Pemasukan/Pengeluaran, Akun, Nominal, Tanggal, Catatan).
   - Hapus transaksi langsung dari daftar.

3. **Anggaran (Monthly Budget Manager)**
   - Pagu anggaran per kategori bulanan (Makanan & Minuman, Belanja, Transportasi, Utilitas).
   - Ambang batas peringatan (warning threshold 80% & 100%).
   - Indikator Pagu Harian Aman (Safe Daily Spend Quota).
   - Pengaturan anggaran berulang (recurring monthly budgets).

4. **Rekening & Portofolio (Accounts & Net Worth)**
   - Pemantauan Total Kekayaan Bersih (Net Worth) lintas rekening Bank, E-Wallet, dan Investasi.
   - Estimasi kas pasif bulanan dari portofolio tabungan/reksadana.
   - Tambah rekening baru secara dinamis.

5. **Laporan & Analitik (Reports & Insights)**
   - Evaluasi arus kas bersih (Net Cash Flow) dan Rasio Tabungan (Savings Rate).
   - Skor Kesehatan Finansial (Financial Health Score).
   - Komposisi pengeluaran per kategori.
   - Fitur ekspor laporan ke format CSV dan cetak PDF.

6. **Emas, Valas & Saham (Market Watch & Investments)**
   - Harga emas batangan harian (Antam & UBS per gram).
   - Nilai tukar valuta asing utama (USD/IDR, EUR/IDR, SGD/IDR).
   - Pantauan saham blue-chip Bursa Efek Indonesia (BBCA, BBRI, TLKM, ASII, GOTO).

7. **Target Impian (Savings Goals)**
   - Target tabungan berjangka (Dana Darurat, Liburan, DP Rumah, Elektronik).
   - Progress bar persentase pencapaian target.
   - Tombol setoran cepat tabungan (+Rp 500.000).

8. **Pengingat Langganan & SIM (Subscriptions & Recurring Bills)**
   - Pelacak tagihan rutin (Netflix, Spotify, Kuota SIM, WiFi, Gym).
   - Perhitungan total beban pengeluaran rutin bulanan (burn rate).
   - Toggle sakelar status langganan aktif/nonaktif.

9. **Transaksi Instan (Quick 1-Tap Expenses)**
   - Pintasan sekali sentuh untuk mencatat pengeluaran umum (Kopi, Makan Siang, Bensin, Parkir, GoRide).

10. **Struk & Koreksi OCR (Receipt Studio)**
    - Pratinjau hasil pemindaian struk fisik.
    - Ekstraksi rincian per baris item, subtotal, dan PPN 11%.
    - Simpan langsung ke pembukuan FinTrack.

11. **Lokasi Pengeluaran (Spending Locations)**
    - Analisis kebiasaan belanja berdasarkan lokasi pusat perbelanjaan dan merchant favorit.

12. **Pusat Kontrol & Profil (Profile & Preferences)**
    - Profil pengguna, akun utama, dan mata uang (IDR).
    - Keamanan biometrik (Fingerprint / Face ID lock).
    - Preferensi notifikasi peringatan anggaran.

## Arsitektur Teknis

- **Bahasa**: Kotlin (100%)
- **UI Framework**: Jetpack Compose dengan Material Design 3 (M3)
- **Desain Tema**: Dark Navy (`#0B1326`, `#131D31`) dengan aksen Cobalt Blue (`#5B83FF`) dan Mint Green (`#4EDEA3`)
- **State Management**: Kotlin StateFlow & Repository Pattern
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`, `libs.versions.toml`)
- **Target SDK**: Android SDK 35 (Android 15), Min SDK 26 (Android 8.0)
- **Arsitektur Target**: MVVM / Clean Architecture dengan isolasi data layer, domain model, dan UI composables
