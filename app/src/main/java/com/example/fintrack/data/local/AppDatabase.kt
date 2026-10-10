package com.example.fintrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fintrack.data.local.dao.AccountDao
import com.example.fintrack.data.local.dao.BudgetDao
import com.example.fintrack.data.local.dao.SavingsGoalDao
import com.example.fintrack.data.local.dao.SubscriptionDao
import com.example.fintrack.data.local.dao.TransactionDao
import com.example.fintrack.data.local.dao.UserProfileDao
import com.example.fintrack.data.local.entity.AccountEntity
import com.example.fintrack.data.local.entity.BudgetEntity
import com.example.fintrack.data.local.entity.SavingsGoalEntity
import com.example.fintrack.data.local.entity.SubscriptionEntity
import com.example.fintrack.data.local.entity.TransactionEntity
import com.example.fintrack.data.local.entity.UserProfileEntity

@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        SubscriptionEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fintrack_local_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
