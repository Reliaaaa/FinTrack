package id.fintrack.app.data

import androidx.room.withTransaction
import id.fintrack.app.domain.LedgerBalance
import id.fintrack.app.domain.Category
import id.fintrack.app.domain.ParsedTx
import id.fintrack.app.domain.TxType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Repository(private val db: AppDatabase) {
    private val dao = db.dao()

    // All callers must remain inside db.withTransaction: read, check and write are atomic.
    private suspend fun adjustBalance(id: Long, delta: Long) {
        val account = requireNotNull(dao.account(id)) { "Rekening tidak ditemukan" }
        dao.setBalance(id, LedgerBalance.apply(account.balance, delta))
    }

    val budgets = dao.budgets()
    val billPayments = dao.billPayments()
    val goals = dao.goals()

    val accounts = dao.accounts()
    val transactions = dao.transactions()
    val drafts = dao.drafts()
    val obligations = dao.obligations()
    val holdings = dao.holdings()

    private fun signed(type: String, amount: Long): Long =
        if (type == TxType.INCOME.name) amount else -amount

    // Rekening
    suspend fun addAccount(name: String, type: String, balance: Long, liquid: Boolean) {
        require(name.isNotBlank() && balance >= 0 && balance <= 1_000_000_000_000_000L)
        dao.insertAccount(AccountEntity(name = name.trim(), type = type, balance = balance, liquid = liquid))
    }

    suspend fun deleteAccount(id: Long) {
        db.withTransaction {
            require(dao.transactionsOnce().none { it.accountId == id }) { "Rekening memiliki riwayat. Hapus transaksi terkait dahulu." }
            dao.deleteAccount(id)
        }
    }

    // Transaksi
    suspend fun addTransaction(
        accountId: Long,
        type: TxType,
        amount: Long,
        category: Category,
        note: String,
        timestamp: Long = System.currentTimeMillis()
    ) {
        require(amount > 0 && amount <= 1_000_000_000_000_000L)
        db.withTransaction {
            requireNotNull(dao.account(accountId)) { "Rekening tidak ditemukan" }
            dao.insertTransaction(
                TransactionEntity(
                    accountId = accountId,
                    type = type.name,
                    amount = amount,
                    category = category.name,
                    note = note.trim(),
                    timestamp = timestamp,
                    source = TxSource.MANUAL,
                    status = TxStatus.CONFIRMED
                )
            )
            adjustBalance(accountId, signed(type.name, amount))
        }
    }

    suspend fun editTransaction(id: Long, accountId: Long, type: TxType, amount: Long, category: Category, note: String, timestamp: Long) {
        require(amount in 1..1_000_000_000_000_000L)
        db.withTransaction {
            require(dao.paymentLinks(id) == 0) { "Transaksi terkait tagihan belum dapat diedit" }
            val old = requireNotNull(dao.transaction(id))
            require(old.status == TxStatus.CONFIRMED && old.transferGroup == null)
            requireNotNull(dao.account(accountId))
            old.accountId?.let { adjustBalance(it, -signed(old.type, old.amount)) }
            dao.updateTransaction(old.copy(accountId=accountId,type=type.name,amount=amount,
                category=category.name,note=note.trim(),timestamp=timestamp))
            adjustBalance(accountId,signed(type.name,amount))
        }
    }

    suspend fun deleteTransaction(id: Long) {
        db.withTransaction {
            require(dao.paymentLinks(id) == 0) { "Transaksi terkait tagihan belum dapat dihapus" }
            val tx = dao.transaction(id)
            if (tx != null) {
                if (tx.transferGroup != null) {
                    for (row in dao.transferRows(tx.transferGroup)) {
                        row.accountId?.let { adjustBalance(it, if (row.type == "TRANSFER_IN") -row.amount else row.amount) }
                        dao.deleteTransaction(row.id)
                    }
                    return@withTransaction
                }
                val acc = tx.accountId
                if (tx.status == TxStatus.CONFIRMED && acc != null) {
                    adjustBalance(acc, -signed(tx.type, tx.amount))
                }
                dao.deleteTransaction(id)
            }
        }
    }

    /** Simpan draf dari notifikasi. Mengembalikan false bila notifikasi yang sama sudah tercatat. */
    suspend fun addDraft(parsed: ParsedTx, timestamp: Long, fingerprint: String, category: Category): Boolean {
        require(parsed.amount in 1..1_000_000_000_000_000L)
        val id = dao.insertTransaction(
            TransactionEntity(
                accountId = null,
                type = parsed.type.name,
                amount = parsed.amount,
                category = category.name,
                note = parsed.description,
                timestamp = timestamp,
                source = TxSource.NOTIFICATION,
                status = TxStatus.DRAFT,
                fingerprint = fingerprint
            )
        )
        return id != -1L
    }

    suspend fun confirmDraft(id: Long, accountId: Long, category: Category) {
        db.withTransaction {
            val tx = dao.transaction(id)
            if (tx != null && tx.status == TxStatus.DRAFT) {
                requireNotNull(dao.account(accountId)) { "Rekening tidak ditemukan" }
                dao.updateTransaction(tx.copy(accountId = accountId, category = category.name, status = TxStatus.CONFIRMED))
                adjustBalance(accountId, signed(tx.type, tx.amount))
            }
        }
    }

    suspend fun discardDraft(id: Long) {
        val tx = dao.transaction(id)
        if (tx != null && tx.status == TxStatus.DRAFT) dao.deleteTransaction(id)
    }

    suspend fun transfer(from: Long, to: Long, amount: Long) {
        require(from != to && amount > 0 && amount <= 1_000_000_000_000_000L)
        db.withTransaction {
            val a = requireNotNull(dao.account(from))
            val b = requireNotNull(dao.account(to))
            require(a.balance >= amount) { "Saldo sumber tidak cukup" }
            val group = java.util.UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            for ((id, type, note) in listOf(Triple(from, "TRANSFER_OUT", "Ke ${b.name}"), Triple(to, "TRANSFER_IN", "Dari ${a.name}"))) {
                dao.insertTransaction(TransactionEntity(accountId=id, type=type, amount=amount,
                    category=Category.LAINNYA.name, note=note, timestamp=now, source=TxSource.MANUAL,
                    status=TxStatus.CONFIRMED, transferGroup=group))
            }
            adjustBalance(from, -amount)
            adjustBalance(to, amount)
        }
    }
    suspend fun saveBudget(category: String, amount: Long) {
        require(amount > 0)
        dao.saveBudget(BudgetEntity(category, amount))
    }
    suspend fun deleteBudget(category: String) = dao.deleteBudget(category)
    suspend fun saveGoal(g: GoalEntity) {
        require(g.name.isNotBlank() && g.target > 0 && g.saved in 0..g.target)
        java.time.LocalDate.parse(g.dueDate)
        dao.saveGoal(g)
    }
    suspend fun deleteGoal(id: Long) = dao.deleteGoal(id)

    suspend fun importRows(accountId: Long, rows: List<id.fintrack.app.domain.ImportRow>): Int {
        var inserted = 0
        db.withTransaction {
            requireNotNull(dao.account(accountId)) { "Rekening tidak ditemukan" }
            for (r in rows) {
                val id = dao.insertTransaction(TransactionEntity(accountId=accountId,type=r.type.name,
                    amount=r.amount,category=r.category.name,note=r.note,timestamp=r.timestamp(),
                    source="CSV",status=TxStatus.CONFIRMED,fingerprint=r.fingerprint(accountId)))
                if (id != -1L) { adjustBalance(accountId,signed(r.type.name,r.amount));inserted++ }
            }
        }
        return inserted
    }

    // Kewajiban
    suspend fun addObligation(name: String, kind: String, amount: Long, dueDay: Int, remaining: Int) {
        dao.insertObligation(
            ObligationEntity(name = name.trim(), kind = kind, amount = amount, dueDay = dueDay.coerceIn(1, 31), remaining = remaining)
        )
    }

    /** One receipt per bill/month; debit and schedule update commit together. */
    suspend fun payBill(id: Long, period: String, accountId: Long?, existingTransactionId: Long? = null) {
        java.time.YearMonth.parse(period)
        db.withTransaction {
            if (dao.billPayment(id, period) != null) return@withTransaction
            require(period == java.time.YearMonth.now().toString()) { "Buka ulang pembayaran untuk bulan berjalan" }
            val o = requireNotNull(dao.obligation(id)) { "Tagihan tidak ditemukan" }
            require(o.lastPaidMonth != period) { "Tagihan sudah ditandai lunas" }
            require(o.remaining != 0 && o.amount in 1..1_000_000_000_000_000L)
            val txId = if (existingTransactionId != null) {
                val tx = requireNotNull(dao.transaction(existingTransactionId))
                require(tx.status == TxStatus.CONFIRMED && tx.type == TxType.EXPENSE.name && tx.transferGroup == null)
                require(tx.amount == o.amount && tx.accountId != null && dao.account(tx.accountId) != null)
                require(dao.paymentLinks(tx.id) == 0) { "Transaksi sudah terkait tagihan" }
                tx.id
            } else {
                val account = requireNotNull(accountId) { "Pilih rekening" }
                requireNotNull(dao.account(account))
                val newId = dao.insertTransaction(TransactionEntity(accountId=account, type=TxType.EXPENSE.name,
                    amount=o.amount, category=Category.LAINNYA.name, note="Tagihan: ${o.name} ($period)",
                    timestamp=System.currentTimeMillis(), source="BILL", status=TxStatus.CONFIRMED))
                require(newId != -1L)
                adjustBalance(account, -o.amount)
                newId
            }
            dao.insertBillPayment(BillPaymentEntity(id, period, txId, if (existingTransactionId == null) "CREATED" else "LINKED"))
            dao.updateObligation(o.copy(lastPaidMonth=period, remaining=if (o.remaining > 0) o.remaining - 1 else o.remaining))
        }
    }

    suspend fun cancelBillPayment(id: Long, period: String, expectedTransactionId: Long, deleteExpense: Boolean) {
        db.withTransaction {
            val payment = dao.billPayment(id, period) ?: return@withTransaction
            require(payment.transactionId == expectedTransactionId) { "Pembayaran berubah. Buka ulang konfirmasi." }
            require(payment.origin != "LEGACY_MARKER") { "Penanda historis memerlukan rekonsiliasi manual" }
            val o = requireNotNull(dao.obligation(id))
            require(o.lastPaidMonth == period) { "Batalkan pembayaran terbaru terlebih dahulu" }
            if (deleteExpense) {
                require(payment.origin == "CREATED") { "Pengeluaran yang dikaitkan harus tetap disimpan" }
                val tx = requireNotNull(dao.transaction(expectedTransactionId))
                require(tx.type == TxType.EXPENSE.name && tx.status == TxStatus.CONFIRMED && tx.transferGroup == null)
                adjustBalance(requireNotNull(tx.accountId), tx.amount)
                dao.deleteTransaction(tx.id)
            }
            dao.deleteBillPayment(id, period)
            dao.updateObligation(o.copy(lastPaidMonth=dao.latestPaidPeriod(id),
                remaining=if (o.remaining >= 0) Math.addExact(o.remaining, 1) else o.remaining))
        }
    }

    suspend fun deleteObligation(id: Long) = db.withTransaction {
        require(dao.obligationPayments(id) == 0) { "Jadwal memiliki riwayat pembayaran dan belum dapat dihapus" }
        dao.deleteObligation(id)
    }

    // Aset
    suspend fun addHolding(name: String, assetClass: String, symbol: String?, quantity: Double, costBasis: Long, unitPrice: Long) {
        require(name.isNotBlank() && quantity.isFinite() && quantity > 0 && unitPrice > 0 && costBasis >= 0)
        require(quantity * unitPrice <= 1_000_000_000_000_000.0)
        dao.insertHolding(
            HoldingEntity(
                name = name.trim(),
                assetClass = assetClass,
                symbol = if(assetClass == "KRIPTO") symbol?.trim()?.lowercase()?.ifEmpty { null } else null,
                quantity = quantity,
                costBasis = costBasis,
                unitPrice = unitPrice,
                priceUpdatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun setHoldingPrice(id: Long, unitPrice: Long) {
        val h = dao.holdingsOnce().firstOrNull { it.id == id } ?: return
        require(unitPrice > 0 && h.quantity * unitPrice <= 1_000_000_000_000_000.0)
        dao.updateHolding(h.copy(unitPrice = unitPrice, priceUpdatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteHolding(id: Long) = dao.deleteHolding(id)

    /** prices: pasangan (huruf kecil) ke harga rupiah. Mengembalikan jumlah aset yang diperbarui. */
    suspend fun applyPrices(prices: Map<String, Long>): Int {
        var updated = 0
        val now = System.currentTimeMillis()
        for (h in dao.holdingsOnce()) {
            if(h.assetClass != "KRIPTO") continue
            val price = h.symbol?.let { prices[it] } ?: continue
            if(price <= 0 || h.quantity * price > 1_000_000_000_000_000.0) continue
            dao.updateHolding(h.copy(unitPrice = price, priceUpdatedAt = now))
            updated++
        }
        return updated
    }

    // Ekspor dan hapus
    suspend fun exportCsv(): String {
        val names = dao.accountsOnce().associate { it.id to it.name }
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val zone = ZoneId.systemDefault()
        val sb = StringBuilder("tanggal,jenis,jumlah,kategori,rekening,catatan,sumber\n")
        for (t in dao.transactionsOnce()) {
            val date = Instant.ofEpochMilli(t.timestamp).atZone(zone).toLocalDateTime().format(fmt)
            sb.append(csv(date)).append(',')
                .append(csv(t.type)).append(',')
                .append(t.amount).append(',')
                .append(csv(Category.fromName(t.category).label)).append(',')
                .append(csv(names[t.accountId] ?: "")).append(',')
                .append(csv(t.note)).append(',')
                .append(csv(t.source)).append('\n')
        }
        return sb.toString()
    }

    /** Kutip sel CSV. Awalan rumus (=, +, -, @) diberi tanda petik agar tidak dieksekusi spreadsheet. */
    private fun csv(value: String): String {
        var v = value
        if (v.trimStart().firstOrNull() in listOf('=', '+', '-', '@') || v.startsWith("\t") || v.startsWith("\r")) v = "'$v"
        return "\"" + v.replace("\"", "\"\"") + "\""
    }

    suspend fun wipe() {
        db.withTransaction {
            dao.clearBillPayments()
            dao.clearTransactions()
            dao.clearAccounts()
            dao.clearObligations()
            dao.clearHoldings()
            dao.clearGoals()
            dao.clearBudgets()
        }
    }
}
