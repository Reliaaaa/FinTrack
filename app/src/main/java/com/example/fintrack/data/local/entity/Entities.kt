package com.example.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val amount: Long, // Positif untuk pemasukan, negatif untuk pengeluaran
    val date: String,
    val account: String,
    val note: String = "",
    val type: String = "expense", // "expense", "income", "transfer"
    val merchantLocation: String? = null,
    val receiptImageAttached: Boolean = false,
    val rewardsPoints: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // "Bank", "E-Wallet", "Investasi", "Kas Tunai"
    val amount: Long,
    val accountNumber: String,
    val adminFee: Long = 0L,
    val interestRate: Double = 0.0,
    val isPrimary: Boolean = false
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val limit: Long,
    val spent: Long = 0L,
    val threshold: Int = 80,
    val iconName: String = "pie_chart"
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val deadline: String,
    val category: String,
    val reasonNote: String = "",
    val iconName: String = "savings"
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val amount: Long,
    val billingCycle: String = "Bulanan",
    val nextDueDate: String,
    val category: String = "Digital & Entertainment",
    val isActive: Boolean = true,
    val serviceLogoUrl: String? = null
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "primary_user",
    val name: String,
    val nickname: String,
    val email: String,
    val phone: String,
    val userCode: String,
    val currency: String = "IDR",
    val biometricEnabled: Boolean = false,
    val twoFactorEnabled: Boolean = true,
    val registeredDate: String = ""
)
