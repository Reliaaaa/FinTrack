package id.fintrack.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object TxStatus {
    const val CONFIRMED = "CONFIRMED"
    const val DRAFT = "DRAFT"
}

object TxSource {
    const val MANUAL = "MANUAL"
    const val NOTIFICATION = "NOTIFICATION"
}

/** type: KAS, BANK, DOMPET, LAINNYA. liquid = ikut dihitung sebagai uang yang bisa dipakai. */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val balance: Long,
    val liquid: Boolean = true
)

/**
 * Draf dari notifikasi punya accountId null sampai pengguna mengonfirmasi.
 * fingerprint mencegah draf ganda untuk notifikasi yang sama.
 */
@Entity(tableName = "transactions", indices = [Index(value = ["fingerprint"], unique = true)])
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long?,
    val type: String,
    val amount: Long,
    val category: String,
    val note: String,
    val timestamp: Long,
    val source: String,
    val status: String,
    val fingerprint: String? = null,
    val transferGroup: String? = null
)

/** remaining = jumlah bayar tersisa, -1 tanpa batas. lastPaidMonth berformat yyyy-MM. */
@Entity(tableName = "obligations")
data class ObligationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val kind: String,
    val amount: Long,
    val dueDay: Int,
    val remaining: Int,
    val lastPaidMonth: String? = null
)

/** symbol diisi pasangan Indodax (misal btc_idr) bila harga ingin diambil otomatis. */
@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val assetClass: String,
    val symbol: String?,
    val quantity: Double,
    val costBasis: Long,
    val unitPrice: Long,
    val priceUpdatedAt: Long = 0
)

@Entity(tableName = "budgets")
data class BudgetEntity(@PrimaryKey val category: String, val amount: Long)
@Entity(tableName = "goals")
data class GoalEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0,
 val name: String, val target: Long, val saved: Long, val dueDate: String)
