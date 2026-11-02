package id.fintrack.app.vm

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.fintrack.app.FinTrackApplication
import id.fintrack.app.data.GoalEntity
import id.fintrack.app.domain.Forecast
import id.fintrack.app.data.HoldingEntity
import id.fintrack.app.data.ObligationEntity
import id.fintrack.app.data.Settings
import id.fintrack.app.data.TransactionEntity
import id.fintrack.app.domain.AssetClass
import id.fintrack.app.domain.Category
import id.fintrack.app.domain.HoldingValue
import id.fintrack.app.domain.NetWorth
import id.fintrack.app.domain.ObligationKind
import id.fintrack.app.domain.ObligationMath
import id.fintrack.app.domain.ObligationSpec
import id.fintrack.app.domain.Reports
import id.fintrack.app.domain.SafeToSpend
import id.fintrack.app.domain.SafeToSpendCalc
import id.fintrack.app.domain.TxRecord
import id.fintrack.app.domain.TxType
import id.fintrack.app.market.PriceClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

data class TransactionSaveState(
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val fintrack = app as FinTrackApplication
    private val repo = fintrack.repository
    private val prefs = fintrack.prefs
    private val keepAlive = SharingStarted.WhileSubscribed(5_000)

    private val settingsFlow = MutableStateFlow(prefs.snapshot())
    val settings: StateFlow<Settings> = settingsFlow

    val budgets = repo.budgets.stateIn(viewModelScope, keepAlive, emptyList())
    val billPayments = repo.billPayments.stateIn(viewModelScope, keepAlive, emptyList())
    val goals = repo.goals.stateIn(viewModelScope, keepAlive, emptyList())
    val accounts = repo.accounts.stateIn(viewModelScope, keepAlive, emptyList())
    val transactions = repo.transactions.stateIn(viewModelScope, keepAlive, emptyList())
    val drafts = repo.drafts.stateIn(viewModelScope, keepAlive, emptyList())
    val holdings = repo.holdings.stateIn(viewModelScope, keepAlive, emptyList())
    val obligationEntities = repo.obligations.stateIn(viewModelScope, keepAlive, emptyList())

    private fun spec(o: ObligationEntity) = ObligationSpec(
        amount = o.amount,
        dueDay = o.dueDay,
        remaining = o.remaining,
        paidThrough = o.lastPaidMonth?.let { runCatching { YearMonth.parse(it) }.getOrNull() }
    )

    val home: StateFlow<HomeState> = combine(
        accounts, obligationEntities, transactions, drafts, settingsFlow
    ) { acc, obl, tx, dr, st ->
        val today = LocalDate.now()
        val liquid = acc.filter { it.liquid }.sumOf { it.balance }
        val nextIncome = SafeToSpendCalc.nextIncomeDate(today, st.incomeDay)
        val safe = SafeToSpendCalc.compute(liquid, obl.map { spec(it) }, today, nextIncome)
        val upcoming = obl.flatMap { o ->
            ObligationMath.occurrences(today, today.plusDays(14), spec(o)).map {
                UpcomingItem(o.name, ObligationKind.fromName(o.kind), o.amount, it)
            }
        }.sortedBy { it.date }
        HomeState(acc.isNotEmpty(), liquid, safe, nextIncome, upcoming, tx.take(5), dr.size)
    }.stateIn(
        viewModelScope, keepAlive,
        HomeState(false, 0L, SafeToSpend(0L, 0L, 0L, 1, LocalDate.now()), LocalDate.now(), emptyList(), emptyList(), 0)
    )

    val obligations: StateFlow<ObligationsState> = combine(obligationEntities, settingsFlow) { obl, st ->
        val today = LocalDate.now()
        val ym = YearMonth.from(today).toString()
        val rows = obl.map { ObligationRow(it, ObligationMath.nextDue(today, it.dueDay), it.lastPaidMonth == ym) }
            .sortedBy { it.nextDue }
        val specs = obl.map { spec(it) }
        ObligationsState(rows, ObligationMath.monthlyLoad(specs), ObligationMath.loadRatio(specs, st.monthlyIncome), st.monthlyIncome)
    }.stateIn(viewModelScope, keepAlive, ObligationsState(emptyList(), 0L, null, 0L))

    val assets: StateFlow<AssetsState> = combine(accounts, holdings, obligationEntities) { acc, hold, obl ->
        val values = hold.map {
            HoldingValue(it.name, AssetClass.fromName(it.assetClass), it.quantity, it.costBasis, it.unitPrice)
        }
        val summary = NetWorth.summarize(values, acc.sumOf { it.balance }, ObligationMath.remainingLiability(obl.map { spec(it) }))
        AssetsState(summary, hold, acc)
    }.stateIn(viewModelScope, keepAlive, AssetsState(NetWorth.summarize(emptyList(), 0L, 0L), emptyList(), emptyList()))

    val report: StateFlow<ReportState> = transactions.map { list ->
        val zone = ZoneId.systemDefault()
        val now = YearMonth.now()
        val records = list.filter { it.transferGroup == null }.map {
            TxRecord(
                if (it.type == TxType.INCOME.name) TxType.INCOME else TxType.EXPENSE,
                it.amount,
                Category.fromName(it.category),
                Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate()
            )
        }
        val label = now.month.getDisplayName(TextStyle.FULL, Locale("id", "ID")) + " " + now.year
        ReportState(label, Reports.month(records, now.year, now.monthValue))
    }.stateIn(viewModelScope, keepAlive, ReportState("", Reports.month(emptyList(), 2000, 1)))

    // Pesan singkat untuk snackbar
    private val messageFlow = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = messageFlow
    fun consumeMessage() { messageFlow.value = null }

    fun transfer(from: Long, to: Long, amount: Long) = operations.submit("transfer") { repo.transfer(from, to, amount) }
    fun saveBudget(category: String, amount: Long) = operations.submit("saveBudget") { repo.saveBudget(category, amount) }
    fun deleteBudget(category: String) = operations.submit("deleteBudget") { repo.deleteBudget(category) }
    fun saveGoal(g: GoalEntity) = operations.submit("saveGoal") { repo.saveGoal(g) }
    fun deleteGoal(id: Long) = operations.submit("deleteGoal") { repo.deleteGoal(id) }
    private val importPreviewFlow = MutableStateFlow<List<id.fintrack.app.domain.ImportRow>>(emptyList())
    val importPreview: StateFlow<List<id.fintrack.app.domain.ImportRow>> = importPreviewFlow
    private val importStatusFlow = MutableStateFlow("")
    val importStatus: StateFlow<String> = importStatusFlow
    private val importBusyFlow = MutableStateFlow(false)
    val importBusy: StateFlow<Boolean> = importBusyFlow
    fun readImport(uri: Uri) {
        if (importBusyFlow.value) return
        operations.reset("commitImport")
        importBusyFlow.value = true
        importPreviewFlow.value = emptyList()
        viewModelScope.launch {
            importStatusFlow.value = "Membaca CSV..."
            try {
                val rows = withContext(Dispatchers.IO) {
                    val input = requireNotNull(getApplication<Application>().contentResolver.openInputStream(uri))
                    val bytes = input.use { stream ->
                        val out = java.io.ByteArrayOutputStream()
                        val buffer = ByteArray(8192)
                        while(out.size() <= 1_000_000) {
                            val n = stream.read(buffer,0,minOf(buffer.size,1_000_001-out.size()))
                            if(n < 0) break
                            out.write(buffer,0,n)
                        }
                        out.toByteArray()
                    }
                    require(bytes.size <= 1_000_000) { "CSV maksimal 1 MB" }
                    id.fintrack.app.domain.CsvImport.parse(bytes.toString(Charsets.UTF_8))
                }
                importPreviewFlow.value = rows
                importStatusFlow.value = "${rows.size} baris siap ditinjau"
            } catch(e: CancellationException) { throw e }
            catch(e: Exception) { importStatusFlow.value = e.message ?: "CSV tidak dapat dibaca" }
            finally { importBusyFlow.value = false }
        }
    }
    fun commitImport(accountId: Long) {
        if (importBusyFlow.value || operations.state("commitImport").value.saved) return
        val rows = importPreviewFlow.value
        if (rows.isEmpty()) return
        importBusyFlow.value = true
        operations.submit("commitImport") {
            try {
                val n = repo.importRows(accountId, rows)
                importPreviewFlow.value = emptyList()
                importStatusFlow.value = "$n transaksi ditambahkan, ${rows.size-n} duplikat dilewati"
            } finally {
                importBusyFlow.value = false
            }
        }
    }
    // Rekening
    fun addAccount(name: String, type: String, balance: Long, liquid: Boolean) {
        operations.submit("addAccount") { repo.addAccount(name, type, balance, liquid) }
    }

    fun deleteAccount(id: Long) {
        operations.submit("deleteAccount") { repo.deleteAccount(id) }
    }

    val operations = OperationStore(viewModelScope)

    // Transaksi
    private val transactionSaveFlow = MutableStateFlow(TransactionSaveState())
    val transactionSave: StateFlow<TransactionSaveState> = transactionSaveFlow

    fun resetTransactionSave() {
        if (!transactionSaveFlow.value.saving) transactionSaveFlow.value = TransactionSaveState()
    }

    private fun saveTransaction(block: suspend () -> Unit) {
        // Set synchronously so repeated taps cannot enqueue another write.
        if (transactionSaveFlow.value.saving || transactionSaveFlow.value.saved) return
        transactionSaveFlow.value = TransactionSaveState(saving = true)
        viewModelScope.launch {
            try {
                block()
                transactionSaveFlow.value = TransactionSaveState(saved = true)
                messageFlow.value = "Tersimpan"
            } catch (e: CancellationException) {
                transactionSaveFlow.value = TransactionSaveState()
                throw e
            } catch (e: Exception) {
                transactionSaveFlow.value = TransactionSaveState(error = e.message ?: "Gagal menyimpan. Coba lagi.")
            }
        }
    }

    fun addTransaction(accountId: Long, type: TxType, amount: Long, category: Category, note: String, timestamp: Long = System.currentTimeMillis()) {
        saveTransaction { repo.addTransaction(accountId, type, amount, category, note, timestamp) }
    }

    fun editTransaction(id: Long, accountId: Long, type: TxType, amount: Long, category: Category, note: String, timestamp: Long) = saveTransaction {
        repo.editTransaction(id,accountId,type,amount,category,note,timestamp)
    }

    fun deleteTransaction(tx: TransactionEntity) {
        operations.submit("deleteTransaction") { repo.deleteTransaction(tx.id) }
    }

    fun confirmDraft(id: Long, accountId: Long, category: Category) {
        operations.submit("confirmDraft") { repo.confirmDraft(id, accountId, category) }
    }

    fun discardDraft(id: Long) {
        operations.submit("discardDraft:$id") { repo.discardDraft(id) }
    }

    // Kewajiban
    fun addObligation(name: String, kind: ObligationKind, amount: Long, dueDay: Int, remaining: Int) {
        operations.submit("addObligation") { repo.addObligation(name, kind.name, amount, dueDay, remaining) }
    }

    fun payBill(id: Long, period: String, accountId: Long?, existingTransactionId: Long?) {
        operations.submit("payBill") { repo.payBill(id, period, accountId, existingTransactionId) }
    }

    fun deleteObligation(id: Long) {
        operations.submit("deleteObligation") { repo.deleteObligation(id) }
    }

    fun cancelBillPayment(id: Long, period: String, transactionId: Long, deleteExpense: Boolean) {
        operations.submit("cancelBillPayment") { repo.cancelBillPayment(id, period, transactionId, deleteExpense) }
    }

    // Aset
    fun addHolding(name: String, assetClass: AssetClass, symbol: String?, quantity: Double, costBasis: Long, unitPrice: Long) {
        operations.submit("addHolding") { repo.addHolding(name, assetClass.name, symbol, quantity, costBasis, unitPrice) }
    }

    fun setHoldingPrice(id: Long, price: Long) {
        operations.submit("setHoldingPrice") { repo.setHoldingPrice(id, price) }
    }

    fun deleteHolding(h: HoldingEntity) {
        operations.submit("deleteHolding") { repo.deleteHolding(h.id) }
    }

    // Harga dan grafik
    private val priceStatusFlow = MutableStateFlow<String?>(null)
    val priceStatus: StateFlow<String?> = priceStatusFlow

    private var priceJob: kotlinx.coroutines.Job? = null
    fun refreshPrices() {
        if(priceJob?.isActive == true) return
        priceJob = viewModelScope.launch {
            priceStatusFlow.value = "Memuat harga..."
            priceStatusFlow.value = try {
                val n = repo.applyPrices(PriceClient.indodaxLast())
                "$n aset diperbarui"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                "Gagal memuat harga: ${e.message ?: "jaringan"}"
            }
        }
    }

    private val candlesFlow = MutableStateFlow<String?>(null)
    val candles: StateFlow<String?> = candlesFlow
    private val candleStatusFlow = MutableStateFlow<String?>(null)
    val candleStatus: StateFlow<String?> = candleStatusFlow

    private var candleJob: kotlinx.coroutines.Job? = null
    fun loadCandles(symbol: String) {
        candleJob?.cancel()
        candlesFlow.value = null
        candleJob = viewModelScope.launch {
            candleStatusFlow.value = "Memuat grafik..."
            try {
                candlesFlow.value = PriceClient.indodaxCandles(symbol)
                candleStatusFlow.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                candleStatusFlow.value = "Gagal memuat grafik: ${e.message ?: "jaringan"}"
            }
        }
    }

    // Pengaturan
    fun saveSettings(incomeDay: Int, monthlyIncome: Long) = operations.submit("saveSettings") {
        prefs.incomeDay = incomeDay
        prefs.monthlyIncome = monthlyIncome
        settingsFlow.value = prefs.snapshot()
        messageFlow.value = "Pengaturan tersimpan"
    }

    fun setNotifCapture(on: Boolean) = operations.submit("setNotifCapture") {
        prefs.notifCapture = on
        settingsFlow.value = prefs.snapshot()
    }

    fun exportTo(uri: Uri) {
        operations.submit("exportTo") {
                val csv = repo.exportCsv()
                withContext(Dispatchers.IO) {
                    val out = getApplication<Application>().contentResolver.openOutputStream(uri)
                        ?: throw IllegalStateException("file tujuan tidak bisa ditulis")
                    out.use { it.write(csv.toByteArray(Charsets.UTF_8)) }
                }
                messageFlow.value = "Ekspor selesai"
        }
    }

    fun wipeAll() {
        operations.submit("wipeAll") {
            repo.wipe()
            messageFlow.value = "Semua data dihapus"
        }
    }
}
