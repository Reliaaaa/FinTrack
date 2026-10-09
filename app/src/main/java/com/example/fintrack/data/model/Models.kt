package com.example.fintrack.data.model

data class Transaction(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val category: String,
    val amount: Long, // Positive for income, negative for expense
    val date: String, // YYYY-MM-DD
    val account: String,
    val note: String = ""
)

data class Account(
    val id: String,
    val name: String,
    val type: String, // "Rekening Bank", "E-Wallet", "Investasi"
    val amount: Long,
    val accountNumber: String = ""
)

data class BudgetConfig(
    val name: String,
    val limit: Long,
    val threshold: Int = 80, // percentage for warning
    val recurring: Boolean = true,
    val month: String = ""
)

data class SavingsGoal(
    val id: String,
    val title: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val deadline: String,
    val category: String
)

data class SubscriptionItem(
    val id: String,
    val name: String,
    val cost: Long,
    val billingCycle: String, // "Bulanan", "Tahunan"
    val nextDueDate: String,
    val active: Boolean = true,
    val category: String = "Hiburan"
)

data class MarketAsset(
    val symbol: String,
    val name: String,
    val price: Long,
    val changePercent: Double,
    val type: String // "Emas", "Valas", "Saham"
)

data class SpendingLocation(
    val id: String,
    val name: String,
    val city: String,
    val totalSpent: Long,
    val transactionCount: Int,
    val category: String
)

data class UserProfile(
    val name: String = "Sarah Anderson",
    val nickname: String = "Sarah",
    val primaryAccount: String = "BCA",
    val email: String = "sarah.anderson@example.com",
    val biometricEnabled: Boolean = true,
    val currency: String = "IDR"
)
