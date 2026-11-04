# Sembilan skill pembantu FinTrack

Skill berisi instruksi kerja yang dapat dipakai kembali; skill tidak membuat agent berjalan terus-menerus. Sembilan peran dapat dikerjakan bergiliran sesuai kapasitas. Instruksi pengguna terbaru mengendalikan scope.

| Skill | Contoh penggunaan |
|---|---|
| `$fintrack-pm` | Pecah milestone backup menjadi tiket kecil dan acceptance |
| `$fintrack-architect` | Review ADR cloud E2EE dan kontrak sync |
| `$fintrack-designer` | Perbaiki alur impor dan copy Canva berdasarkan peta layar |
| `$fintrack-mobile-backend` | Kerjakan satu tiket Android/API yang sudah siap |
| `$fintrack-database` | Rancang migrasi tagihan ke occurrence tanpa kehilangan data |
| `$fintrack-qa` | Reproduksi bug debit ganda lalu retest perbaikan |
| `$fintrack-security` | Pilih dan jalankan review Codex Security sesuai objek |
| `$fintrack-devops` | Siapkan build uji dan komentar mulai/selesai sesuai izin |
| `$fintrack-tech-lead` | Review lintas peran dan kembalikan pekerjaan yang belum memenuhi syarat |

Dalam paket unduhan, setiap folder di `skills/` (di repository: `.agents/skills/`) memuat `SKILL.md` dan `agents/openai.yaml`. Nama dan deskripsi membatasi penggunaan pada FinTrack; pemilihan otomatis tidak dinonaktifkan. Skill tidak mengaktifkan deployment atau menyetujui tindakan eksternal dengan sendirinya.

## Skill dan plugin yang digunakan kembali

- **skill-creator**: format, metadata dan validasi skill.
- **Canva**: pembuatan/review board desain oleh Designer; hasil generasi wajib diperiksa.
- **GitHub**: baca repository, branch/PR, run dan komentar oleh DevOps sesuai otorisasi.
- **Codex Security**: kebijakan, scan repository/diff, triage dan fix dipilih sesuai permintaan; planning bukan audit.
- **Documents/Spreadsheets/Presentations**: digunakan hanya bila hasil yang diminta memang dokumen, spreadsheet atau presentasi.
- **write-like-me**: gaya penulisan ketika menulis atas nama pengguna; tidak mengganti fakta atau kebutuhan produk.

Plugin lain seperti Spotify, Pets, Sites atau kalender tidak dipanggil karena tidak diperlukan untuk planning ini.

## Pemasangan dan penggunaan

Paket repository menyediakan salinan dalam `.agents/skills/` pada branch planning agar skill mengikuti proyek. Salinan personal dapat dipasang pada folder skill Codex pengguna setelah izin menulis folder tersebut diberikan; status aktual dicatat di 08-review-status.md. Tidak ada folder skill lama yang ditimpa dalam paket awal ini.

Contoh instruksi kerja: “Gunakan $fintrack-tech-lead untuk menilai kesiapan M1. Gunakan backlog repository, delegasikan PM, Database dan QA bila tersedia, lalu kembalikan hasil yang gagal memenuhi acceptance.”

Saat memakai skill untuk implementasi, sertakan task ID atau hasil yang diinginkan. Skill membaca kondisi repository terbaru; tidak menganggap snapshot planning ini sebagai keadaan selamanya.
