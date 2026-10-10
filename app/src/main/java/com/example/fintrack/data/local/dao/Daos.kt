package com.example.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fintrack.data.local.entity.AccountEntity
import com.example.fintrack.data.local.entity.BudgetEntity
import com.example.fintrack.data.local.entity.SavingsGoalEntity
import com.example.fintrack.data.local.entity.SubscriptionEntity
import com.example.fintrack.data.local.entity.TransactionEntity
import com.example.fintrack.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY isPrimary DESC, name ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountEntity>)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Query("UPDATE accounts SET amount = amount + :delta WHERE name = :accountName")
    suspend fun adjustBalance(accountName: String, delta: Long)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM accounts")
    suspend fun clearAll()
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY id ASC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<BudgetEntity>)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets")
    suspend fun clearAll()
}

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals ORDER BY id ASC")
    fun getAllGoals(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<SavingsGoalEntity>)

    @Update
    suspend fun updateGoal(goal: SavingsGoalEntity)

    @Query("UPDATE savings_goals SET currentAmount = currentAmount + :amount WHERE id = :id")
    suspend fun addContribution(id: String, amount: Long)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM savings_goals")
    suspend fun clearAll()
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY nextDueDate ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(sub: SubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subs: List<SubscriptionEntity>)

    @Query("UPDATE subscriptions SET isActive = :isActive WHERE id = :id")
    suspend fun setSubscriptionActive(id: String, isActive: Boolean)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM subscriptions")
    suspend fun clearAll()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 'primary_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profiles")
    suspend fun clearAll()
}
