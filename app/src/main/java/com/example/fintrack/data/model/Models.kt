package com.example.fintrack.data.model

data class Transaction(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val category: String,
    val amount: Long, // Positive for income, negative for expense
    val date: String, // YYYY-MM-DD
    val account: String,
    val note: String = "",
    val type: String = if (amount < 0) "expense" else "income", // "expense", "income", "transfer"
    val merchantLocation: String = "",
    val receiptImageAttached: Boolean = false,
    val rewardsPoints: Int = 0
)

data class Account(
    val id: String,
    val name: String,
    val type: String, // "Bank", "E-Wallet", "Sekuritas"
    val amount: Long,
    val accountNumber: String = "",
    val adminFee: Long = 0L,
    val interestRate: Double = 0.0,
    val isPrimary: Boolean = false,
    val cashRdn: Long = 0L,
    val stockValue: Long = 0L,
    val pphTaxRate: Double = 20.0
)

data class BudgetConfig(
    val name: String,
    val limit: Long,
    val threshold: Int = 80, // percentage for warning
    val recurring: Boolean = true,
    val month: String = "",
    val categoryGroup: String = "Kebutuhan" // "Kebutuhan" (50%), "Keinginan" (30%), "Tabungan" (20%)
)

data class SavingsGoal(
    val id: String,
    val title: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val deadline: String,
    val category: String,
    val reasonNote: String = "",
    val iconName: String = "laptop_mac"
)

data class SubscriptionItem(
    val id: String,
    val name: String,
    val cost: Long,
    val billingCycle: String = "Bulanan",
    val nextDueDate: String,
    val daysLeft: Int = 3,
    val active: Boolean = true,
    val category: String = "Hiburan & Streaming",
    val paymentSource: String = "Jenius Visa (...4819)",
    val autoRenewal: Boolean = true
)

data class SimCardItem(
    val simSlot: Int, // 1 or 2
    val provider: String, // "Telkomsel", "XL"
    val planName: String,
    val totalQuotaGb: Double,
    val remainingQuotaGb: Double,
    val cost: Long,
    val daysLeft: Int,
    val dueDate: String,
    val autoDebit: Boolean = true,
    val isPrimaryData: Boolean = true
)

data class MarketAsset(
    val symbol: String,
    val name: String,
    val price: Long,
    val changePercent: Double,
    val type: String, // "Emas", "Valas", "Saham", "Komoditas"
    val highPrice: Long = price,
    val lowPrice: Long = price,
    val volumeLot: String = "",
    val rsi: Double = 58.4,
    val macd: String = "+42.1 (Golden Cross)",
    val foreignFlow: String = "+214.8 M"
)

data class SpendingLocation(
    val id: String,
    val name: String,
    val city: String,
    val totalSpent: Long,
    val transactionCount: Int,
    val category: String,
    val distance: String = "1.2 km",
    val lastVisited: String = "Kemarin"
)

data class ReceiptItem(
    val id: String,
    val name: String,
    val category: String,
    val unitPrice: Long,
    val quantity: Int
)

data class UserProfile(
    val name: String = "Sarah Amanda Putri",
    val nickname: String = "Sarah",
    val primaryAccount: String = "BCA Tahapan Prioritas (•••• 8821)",
    val email: String = "sarah.amanda@email.com",
    val phone: String = "+62 812-8923-4410",
    val birthDate: String = "14 Agustus 1996",
    val userCode: String = "USR-829104",
    val biometricEnabled: Boolean = true,
    val twoFactorEnabled: Boolean = true,
    val currency: String = "IDR",
    val cycleStartDate: Int = 25
)
