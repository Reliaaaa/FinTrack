package id.fintrack.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FinTrackDao {
    @Query("SELECT * FROM budgets ORDER BY category")
    fun budgets(): Flow<List<BudgetEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudget(b: BudgetEntity)
    @Query("DELETE FROM budgets WHERE category = :category")
    suspend fun deleteBudget(category: String)
    @Query("SELECT * FROM goals ORDER BY dueDate")
    fun goals(): Flow<List<GoalEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGoal(g: GoalEntity)
    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: Long)
    @Query("DELETE FROM goals")
    suspend fun clearGoals()
    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun account(id: Long): AccountEntity?
    @Query("SELECT * FROM transactions WHERE transferGroup = :groupId")
    suspend fun transferRows(groupId: String): List<TransactionEntity>
    // Rekening
    @Query("SELECT * FROM accounts ORDER BY id")
    fun accounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY id")
    suspend fun accountsOnce(): List<AccountEntity>

    @Insert
    suspend fun insertAccount(account: AccountEntity): Long

    @Query("UPDATE accounts SET balance = balance + :delta WHERE id = :id")
    suspend fun adjustBalance(id: Long, delta: Long)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccount(id: Long)

    @Query("UPDATE transactions SET accountId = NULL WHERE accountId = :id")
    suspend fun detachTransactions(id: Long)

    // Transaksi
    @Query("SELECT * FROM transactions WHERE status = 'CONFIRMED' ORDER BY timestamp DESC")
    fun transactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE status = 'CONFIRMED' ORDER BY timestamp ASC")
    suspend fun transactionsOnce(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE status = 'DRAFT' ORDER BY timestamp DESC")
    fun drafts(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun transaction(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(tx: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(tx: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    // Kewajiban
    @Query("SELECT * FROM obligations ORDER BY dueDay, id")
    fun obligations(): Flow<List<ObligationEntity>>

    @Insert
    suspend fun insertObligation(o: ObligationEntity): Long

    @Update
    suspend fun updateObligation(o: ObligationEntity)

    @Query("SELECT * FROM obligations WHERE id = :id")
    suspend fun obligation(id: Long): ObligationEntity?

    @Query("DELETE FROM obligations WHERE id = :id")
    suspend fun deleteObligation(id: Long)

    // Aset
    @Query("SELECT * FROM holdings ORDER BY assetClass, name")
    fun holdings(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings")
    suspend fun holdingsOnce(): List<HoldingEntity>

    @Insert
    suspend fun insertHolding(h: HoldingEntity): Long

    @Update
    suspend fun updateHolding(h: HoldingEntity)

    @Query("DELETE FROM holdings WHERE id = :id")
    suspend fun deleteHolding(id: Long)

    // Hapus semua
    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM accounts")
    suspend fun clearAccounts()

    @Query("DELETE FROM obligations")
    suspend fun clearObligations()

    @Query("DELETE FROM holdings")
    suspend fun clearHoldings()
}
