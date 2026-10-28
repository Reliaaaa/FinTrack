package id.fintrack.app.vm

import id.fintrack.app.data.AccountEntity
import id.fintrack.app.data.HoldingEntity
import id.fintrack.app.data.ObligationEntity
import id.fintrack.app.data.TransactionEntity
import id.fintrack.app.domain.MonthSummary
import id.fintrack.app.domain.NetWorthSummary
import id.fintrack.app.domain.ObligationKind
import id.fintrack.app.domain.SafeToSpend
import java.time.LocalDate

data class UpcomingItem(val name: String, val kind: ObligationKind, val amount: Long, val date: LocalDate)

data class HomeState(
    val hasAccounts: Boolean,
    val liquid: Long,
    val safe: SafeToSpend,
    val nextIncome: LocalDate,
    val upcoming: List<UpcomingItem>,
    val recent: List<TransactionEntity>,
    val draftCount: Int
)

data class ObligationRow(val entity: ObligationEntity, val nextDue: LocalDate, val paidThisMonth: Boolean)

data class ObligationsState(
    val rows: List<ObligationRow>,
    val monthlyLoad: Long,
    val ratio: Double?,
    val income: Long
)

data class AssetsState(
    val summary: NetWorthSummary,
    val holdings: List<HoldingEntity>,
    val accounts: List<AccountEntity>
)

data class ReportState(val label: String, val summary: MonthSummary)
