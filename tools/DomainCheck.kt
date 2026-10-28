import id.fintrack.app.domain.*
import java.time.LocalDate

private var passed = 0
private var failed = 0

private fun <T> eq(name: String, actual: T, expected: T) {
    if (actual == expected) {
        passed++
    } else {
        failed++
        println("GAGAL  $name\n   diharapkan: $expected\n   hasil:      $actual")
    }
}

fun main() {
    // Money.format
    eq("format 1250000", Money.format(1_250_000), "Rp1.250.000")
    eq("format negatif", Money.format(-5_000), "-Rp5.000")
    eq("format nol", Money.format(0), "Rp0")
    eq("format 999", Money.format(999), "Rp999")
    eq("compact jt", Money.compact(1_500_000), "1,5 jt")
    eq("compact rb", Money.compact(250_000), "250 rb")

    // Money.parse
    eq("parse id penuh", Money.parse("Rp 1.250.000,00"), 1_250_000L)
    eq("parse gaya inggris", Money.parse("Rp1,250,000.00"), 1_250_000L)
    eq("parse rb", Money.parse("50rb"), 50_000L)
    eq("parse jt desimal koma", Money.parse("1,5jt"), 1_500_000L)
    eq("parse jt desimal titik", Money.parse("2.5jt"), 2_500_000L)
    eq("parse ribuan satu titik", Money.parse("1.500"), 1_500L)
    eq("parse huruf", Money.parse("abc"), null)
    eq("parse kosong", Money.parse(""), null)

    // Notification parser
    val expense = NotificationParser.parse("com.bca", "m-BCA", "Transfer berhasil ke ANDI SAPUTRA sebesar Rp 150.000 pada 12/10")
    eq("notif expense tipe", expense?.type, TxType.EXPENSE)
    eq("notif expense nominal", expense?.amount, 150_000L)
    eq("notif expense nama", expense?.description, "ANDI SAPUTRA")

    val income = NotificationParser.parse("id.dana", "DANA", "Anda menerima Rp2.500.000 dari BUDI")
    eq("notif income tipe", income?.type, TxType.INCOME)
    eq("notif income nominal", income?.amount, 2_500_000L)
    eq("notif income nama", income?.description, "BUDI")

    val pay = NotificationParser.parse("com.gojek.app", "Pembayaran berhasil", "Pembayaran Rp45.000,00 di Bakmi GM berhasil")
    eq("notif bayar nominal", pay?.amount, 45_000L)
    eq("notif bayar nama", pay?.description, "Bakmi GM")

    eq("notif otp ditolak", NotificationParser.parse("com.bca", "BCA", "Kode OTP 123456 untuk transaksi Rp100.000"), null)
    eq("notif promo ditolak", NotificationParser.parse("ovo.id", "OVO", "Diskon hingga Rp50.000 untuk pembayaran pertama"), null)
    eq("notif paket asing ditolak", NotificationParser.parse("com.example.app", "x", "Pembayaran Rp10.000"), null)
    eq("notif tanpa nominal", NotificationParser.parse("com.bca", "BCA", "Transfer berhasil"), null)

    // Categorizer
    eq("kategori grab", Categorizer.categorize("GrabBike ke kampus"), Category.TRANSPORT)
    eq("kategori bakmi", Categorizer.categorize("Bakmi GM TP6"), Category.MAKAN)
    eq("kategori tidak dikenal", Categorizer.categorize("xyz"), Category.LAINNYA)
    eq("kategori kredivo", Categorizer.categorize("Kredivo cicilan 3"), Category.CICILAN)

    // Obligations
    val today = LocalDate.of(2026, 10, 21)
    eq("nextDue sama bulan", ObligationMath.nextDue(today, 25), LocalDate.of(2026, 10, 25))
    eq("nextDue bulan depan", ObligationMath.nextDue(today, 5), LocalDate.of(2026, 11, 5))
    eq("nextDue hari ini", ObligationMath.nextDue(today, 21), today)
    eq("nextDue hari 31 di Februari", ObligationMath.nextDue(LocalDate.of(2027, 2, 10), 31), LocalDate.of(2027, 2, 28))
    val spec = ObligationSpec(500_000, 25, remaining = 2)
    eq("occurrences dibatasi sisa", ObligationMath.occurrences(today, LocalDate.of(2027, 3, 31), spec).size, 2)
    eq("occurrences tanpa batas", ObligationMath.occurrences(today, LocalDate.of(2027, 1, 31), ObligationSpec(100, 25)).size, 4)
    eq("occurrences selesai", ObligationMath.occurrences(today, LocalDate.of(2027, 1, 31), ObligationSpec(100, 25, 0)).size, 0)
    val paidSpec = ObligationSpec(100, 25, paidThrough = java.time.YearMonth.of(2026, 10))
    eq("sudah dibayar dilewati", ObligationMath.occurrences(today, LocalDate.of(2026, 11, 30), paidSpec), listOf(LocalDate.of(2026, 11, 25)))
    eq("sisa utang", ObligationMath.remainingLiability(listOf(ObligationSpec(300_000, 5, 4), ObligationSpec(100_000, 5), ObligationSpec(50_000, 5, 0))), 1_200_000L)
    eq("beban bulanan", ObligationMath.monthlyLoad(listOf(ObligationSpec(300_000, 5), ObligationSpec(200_000, 10, 0))), 300_000L)
    eq("rasio beban", ObligationMath.loadRatio(listOf(ObligationSpec(300_000, 5)), 1_000_000), 30.0)
    eq("rasio tanpa pemasukan", ObligationMath.loadRatio(listOf(ObligationSpec(300_000, 5)), 0), null)

    // Safe to spend
    val sts = SafeToSpendCalc.compute(
        liquid = 3_000_000,
        obligations = listOf(ObligationSpec(500_000, 25)),
        today = today,
        nextIncome = LocalDate.of(2026, 11, 1)
    )
    eq("sts hari tersisa", sts.daysLeft, 11)
    eq("sts dicadangkan", sts.reserved, 500_000L)
    eq("sts bisa dipakai", sts.spendable, 2_500_000L)
    eq("sts harian", sts.dailyAllowance, 227_272L)
    val deficit = SafeToSpendCalc.compute(100_000, listOf(ObligationSpec(500_000, 25)), today, LocalDate.of(2026, 11, 1))
    eq("sts defisit", deficit.isDeficit, true)
    eq("sts defisit harian nol", deficit.dailyAllowance, 0L)
    val noIncome = SafeToSpendCalc.compute(1_000_000, emptyList(), today, null)
    eq("sts akhir bulan", noIncome.horizonEnd, LocalDate.of(2026, 10, 31))
    eq("sts akhir bulan hari", noIncome.daysLeft, 11)
    eq("gajian bulan ini", SafeToSpendCalc.nextIncomeDate(today, 25), LocalDate.of(2026, 10, 25))
    eq("gajian bulan depan", SafeToSpendCalc.nextIncomeDate(today, 1), LocalDate.of(2026, 11, 1))

    // Split bill
    val items = listOf(
        SplitItem("Nasi goreng", 35_000, listOf("A")),
        SplitItem("Es teh", 10_000, listOf("A", "B")),
        SplitItem("Pizza", 100_000, listOf("A", "B", "C"))
    )
    val split = SplitBill.split(items, extras = 14_500)
    eq("split jumlah sama dengan total", split.values.sum(), 35_000L + 10_000 + 100_000 + 14_500)
    eq("split tiga orang", split.keys.toList(), listOf("A", "B", "C"))
    val diskon = SplitBill.split(items, extras = -10_000)
    eq("split dengan diskon", diskon.values.sum(), 145_000L - 10_000)
    eq("distribute sisa rupiah", SplitBill.distribute(100, mapOf("a" to 1L, "b" to 1L, "c" to 1L)).values.sum(), 100L)
    eq("distribute urutan sisa", SplitBill.distribute(100, mapOf("a" to 1L, "b" to 1L, "c" to 1L)), mapOf("a" to 34L, "b" to 33L, "c" to 33L))
    eq("split kosong", SplitBill.split(emptyList(), 0), emptyMap())

    // Net worth
    val summary = NetWorth.summarize(
        holdings = listOf(
            HoldingValue("Emas", AssetClass.EMAS, 10.0, 15_000_000, 1_600_000),
            HoldingValue("BBCA", AssetClass.SAHAM, 500.0, 4_500_000, 9_000),
            HoldingValue("BTC", AssetClass.KRIPTO, 0.01, 0, 1_500_000_000)
        ),
        cash = 5_000_000,
        liabilities = 2_000_000
    )
    eq("aset total", summary.assets, 5_000_000L + 16_000_000 + 4_500_000 + 15_000_000)
    eq("kekayaan bersih", summary.net, summary.assets - 2_000_000)
    eq("untung rugi", summary.gainLoss, (16_000_000L - 15_000_000) + (4_500_000 - 4_500_000))
    eq("untung rugi persen", summary.gainLossPercent?.let { Math.round(it * 100) / 100.0 }, 5.13)
    eq("alokasi jumlah 100", Math.round(summary.allocation().values.sum()), 100L)

    // Zakat
    eq("nisab", Zakat.nisab(1_500_000), 127_500_000L)
    eq("zakat tercapai", Zakat.maal(200_000_000, 1_500_000).due, 5_000_000L)
    eq("zakat belum tercapai", Zakat.maal(100_000_000, 1_500_000).reached, false)
    eq("zakat harga emas nol", Zakat.maal(500_000_000, 0).reached, false)

    // Dana musiman
    eq("dana musiman", SeasonalGoal.monthlyContribution(6_000_000, 0, today, LocalDate.of(2027, 3, 10)), 1_200_000L)
    eq("dana musiman tercapai", SeasonalGoal.monthlyContribution(1_000_000, 1_000_000, today, LocalDate.of(2027, 3, 10)), 0L)
    eq("dana musiman bulan sama", SeasonalGoal.monthlyContribution(1_000_000, 0, today, LocalDate.of(2026, 10, 30)), 1_000_000L)
    eq("dana musiman pembulatan atas", SeasonalGoal.monthlyContribution(1_000_001, 0, today, LocalDate.of(2026, 12, 15)), 500_001L)

    // Laporan
    val recs = listOf(
        TxRecord(TxType.INCOME, 5_000_000, Category.GAJI, LocalDate.of(2026, 10, 1)),
        TxRecord(TxType.EXPENSE, 100_000, Category.MAKAN, LocalDate.of(2026, 10, 2)),
        TxRecord(TxType.EXPENSE, 300_000, Category.TRANSPORT, LocalDate.of(2026, 10, 3)),
        TxRecord(TxType.EXPENSE, 50_000, Category.MAKAN, LocalDate.of(2026, 10, 4)),
        TxRecord(TxType.EXPENSE, 999, Category.MAKAN, LocalDate.of(2026, 9, 4))
    )
    val rep = Reports.month(recs, 2026, 10)
    eq("laporan pemasukan", rep.income, 5_000_000L)
    eq("laporan pengeluaran", rep.expense, 450_000L)
    eq("laporan kategori teratas", rep.byCategory.first().first, Category.TRANSPORT)
    eq("laporan neto", rep.net, 4_550_000L)

    // Forecast boundaries and finite obligations.
    val jan = LocalDate.of(2026,1,30)
    val f = Forecast.calculate(jan,1_000_000,2_000_000,31,100_000,emptyList(),200_000,3)
    eq("forecast titik awal",f.points.first().balance,800_000L)
    eq("forecast gajian",f.points[1].balance,2_700_000L)
    eq("forecast akhir",f.points.last().balance,2_500_000L)
    val finite=Forecast.calculate(LocalDate.of(2026,1,1),1_000_000,0,25,0,listOf(ObligationSpec(100_000,1,2)),0,90)
    eq("cicilan terbatas tidak diulang",finite.points.last().balance,800_000L)
    val paid=Forecast.calculate(LocalDate.of(2026,1,1),1_000_000,0,25,0,listOf(ObligationSpec(100_000,1,1,java.time.YearMonth.of(2026,1))),0,90)
    eq("tagihan dibayar dilewati",paid.points.last().balance,900_000L)
    val futureDeficit=Forecast.calculate(jan,100_000,0,25,60_000,emptyList(),0,3)
    eq("defisit pertama",futureDeficit.firstDeficit?.date,jan.plusDays(2))
    eq("gajian akhir februari",Forecast.calculate(LocalDate.of(2026,2,27),0,100,31,0,emptyList(),0,2).points[1].balance,100L)
    val sameDay=Forecast.calculate(LocalDate.of(2026,1,25),1_000,500,25,0,emptyList(),0,1)
    eq("gaji hari ini tidak dihitung ulang",sameDay.points.last().balance,1_000L)
    val csv="tanggal,jenis,jumlah,kategori,catatan,ref\n2026-01-01,EXPENSE,15000,MAKAN,\"Kopi, roti\",A1\n2026-01-02,INCOME,500000,GAJI,Gaji,A2"
    val imported=CsvImport.parse(csv)
    eq("CSV jumlah baris",imported.size,2)
    eq("CSV koma dalam kutip",imported[0].note,"Kopi, roti")
    eq("CSV nominal",imported[0].amount,15_000L)
    eq("CSV fingerprint deterministik",imported[0].fingerprint(1),imported[0].fingerprint(1))
    eq("CSV rekening berbeda",imported[0].fingerprint(1)==imported[0].fingerprint(2),false)
    eq("CSV ref berbeda",imported[0].fingerprint(1)==imported[0].copy(reference="A3").fingerprint(1),false)
    eq("CSV jenis transfer ditolak",runCatching { CsvImport.parse(csv.replace("EXPENSE","TRANSFER_OUT")) }.isFailure,true)
    eq("CSV nominal negatif ditolak",runCatching { CsvImport.parse(csv.replace("15000","-1")) }.isFailure,true)
    eq("CSV pecahan ditolak",runCatching { CsvImport.parse(csv.replace("15000","1.5")) }.isFailure,true)
    eq("CSV tanggal salah ditolak",runCatching { CsvImport.parse(csv.replace("2026-01-01","2026-02-30")) }.isFailure,true)
    eq("CSV kosong ditolak",runCatching { CsvImport.parse("") }.isFailure,true)
    eq("CSV kutip tidak lengkap",runCatching { CsvImport.parse(csv+"\n\"") }.isFailure,true)

    println("Lulus: $passed, gagal: $failed")
    if (failed > 0) System.exit(1)
}
