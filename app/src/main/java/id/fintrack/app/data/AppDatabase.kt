package id.fintrack.app.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AccountEntity::class, TransactionEntity::class, ObligationEntity::class, HoldingEntity::class, BudgetEntity::class, GoalEntity::class, BillPaymentEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): FinTrackDao
}
