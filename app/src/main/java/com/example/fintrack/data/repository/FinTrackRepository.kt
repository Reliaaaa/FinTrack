package com.example.fintrack.data.repository

import com.example.fintrack.data.model.Account
import com.example.fintrack.data.model.BudgetConfig
import com.example.fintrack.data.model.MarketAsset
import com.example.fintrack.data.model.SavingsGoal
import com.example.fintrack.data.model.SpendingLocation
import com.example.fintrack.data.model.SubscriptionItem
import com.example.fintrack.data.model.Transaction
import com.example.fintrack.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

object FinTrackRepository {
    private val localeID = Locale("id", "ID")
    private val numberFormat = NumberFormat.getNumberInstance(localeID)

    fun formatRupiah(amount: Long, withPrefix: Boolean = true): String {
        val formatted = numberFormat.format(kotlin.math.abs(amount))
        return if (withPrefix) "Rp $formatted" else formatted
    }

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _selectedPeriod = MutableStateFlow("2024-10")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _hideBalance = MutableStateFlow(false)
    val hideBalance: StateFlow<Boolean> = _hideBalance.asStateFlow()

    private val _transactions = MutableStateFlow(
        listOf(
            Transaction(
                id = 1,
                name = "Gaji bulanan",
                category = "Pemasukan",
                amount = 15000000L,
                date = "2024-10-24",
                account = "BCA",
                note = "Gaji pokok bulan Oktober"
            ),
            Transaction(
                id = 2,
                name = "Kopi sore di Starbucks",
                category = "Makanan & Minuman",
                amount = -65000L,
                date = "2024-10-24",
                account = "GoPay",
                note = "Caramel Macchiato"
            ),
            Transaction(
                id = 3,
                name = "Belanja kebutuhan bulanan",
                category = "Belanja",
                amount = -850000L,
                date = "2024-10-23",
                account = "BCA",
                note = "Supermarket Grand Lucky"
            ),
            Transaction(
                id = 4,
                name = "Grab ke kantor",
                category = "Transportasi",
                amount = -45000L,
                date = "2024-10-23",
                account = "GoPay",
                note = "GrabCar PP"
            ),
            Transaction(
                id = 5,
                name = "Makan siang sushi",
                category = "Makanan & Minuman",
                amount = -75000L,
                date = "2024-10-22",
                account = "BCA",
                note = "Sushi Tei promo"
            ),
            Transaction(
                id = 6,
                name = "Dividen Saham BBCA",
                category = "Investasi",
                amount = 450000L,
                date = "2024-10-20",
                account = "Bibit",
                note = "Dividen interim"
            ),
            Transaction(
                id = 7,
                name = "Tagihan Listrik & WiFi",
                category = "Tagihan & Utilitas",
                amount = -620000L,
                date = "2024-10-18",
                account = "BCA",
                note = "PLN & Indihome"
            )
        )
    )
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _accounts = MutableStateFlow(
        listOf(
            Account(id = "1", name = "BCA", type = "Rekening Bank", amount = 18500000L, accountNumber = "8201-9234-88"),
            Account(id = "2", name = "GoPay", type = "E-Wallet", amount = 1500000L, accountNumber = "0812-9988-7711"),
            Account(id = "3", name = "Bibit", type = "Investasi", amount = 4500000L, accountNumber = "RD-9281-ID"),
            Account(id = "4", name = "Mandiri", type = "Rekening Bank", amount = 10000000L, accountNumber = "124-00-1827-11")
        )
    )
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _budgets = MutableStateFlow(
        listOf(
            BudgetConfig(name = "Makanan & Minuman", limit = 2500000L, threshold = 80, recurring = true, month = "2024-10"),
            BudgetConfig(name = "Belanja", limit = 2500000L, threshold = 80, recurring = true, month = "2024-10"),
            BudgetConfig(name = "Transportasi", limit = 1000000L, threshold = 80, recurring = true, month = "2024-10"),
            BudgetConfig(name = "Tagihan & Utilitas", limit = 1500000L, threshold = 85, recurring = true, month = "2024-10")
        )
    )
    val budgets: StateFlow<List<BudgetConfig>> = _budgets.asStateFlow()

    private val _savingsGoals = MutableStateFlow(
        listOf(
            SavingsGoal(id = "1", title = "Dana Darurat", targetAmount = 50000000L, currentAmount = 32500000L, deadline = "Desember 2025", category = "Keamanan Finansial"),
            SavingsGoal(id = "2", title = "Liburan ke Jepang", targetAmount = 25000000L, currentAmount = 14200000L, deadline = "Maret 2025", category = "Liburan"),
            SavingsGoal(id = "3", title = "DP Rumah Idaman", targetAmount = 150000000L, currentAmount = 45000000L, deadline = "Desember 2026", category = "Properti"),
            SavingsGoal(id = "4", title = "Upgrade Laptop Kerja", targetAmount = 22000000L, currentAmount = 18500000L, deadline = "November 2024", category = "Elektronik")
        )
    )
    val savingsGoals: StateFlow<List<SavingsGoal>> = _savingsGoals.asStateFlow()

    private val _subscriptions = MutableStateFlow(
        listOf(
            SubscriptionItem(id = "1", name = "Netflix Premium 4K", cost = 186000L, billingCycle = "Bulanan", nextDueDate = "2024-11-04", active = true, category = "Streaming Film"),
            SubscriptionItem(id = "2", name = "Spotify Family", cost = 86900L, billingCycle = "Bulanan", nextDueDate = "2024-11-12", active = true, category = "Musik"),
            SubscriptionItem(id = "3", name = "Telkomsel Halo 50GB", cost = 150000L, billingCycle = "Bulanan", nextDueDate = "2024-11-20", active = true, category = "Kuota & SIM"),
            SubscriptionItem(id = "4", name = "WiFi Indihome 50Mbps", cost = 375000L, billingCycle = "Bulanan", nextDueDate = "2024-11-18", active = true, category = "Internet Rumah"),
            SubscriptionItem(id = "5", name = "Gym Fitness First", cost = 450000L, billingCycle = "Bulanan", nextDueDate = "2024-11-01", active = true, category = "Kesehatan")
        )
    )
    val subscriptions: StateFlow<List<SubscriptionItem>> = _subscriptions.asStateFlow()

    private val _marketAssets = MutableStateFlow(
        listOf(
            MarketAsset(symbol = "ANTAM", name = "Emas Antam (per gram)", price = 1485000L, changePercent = 0.85, type = "Emas"),
            MarketAsset(symbol = "UBS", name = "Emas UBS (per gram)", price = 1465000L, changePercent = 0.42, type = "Emas"),
            MarketAsset(symbol = "USD/IDR", name = "Dolar Amerika Serikat", price = 15680L, changePercent = -0.15, type = "Valas"),
            MarketAsset(symbol = "EUR/IDR", name = "Euro Eropa", price = 17150L, changePercent = 0.30, type = "Valas"),
            MarketAsset(symbol = "SGD/IDR", name = "Dolar Singapura", price = 11950L, changePercent = 0.12, type = "Valas"),
            MarketAsset(symbol = "BBCA", name = "Bank Central Asia Tbk", price = 10450L, changePercent = 1.25, type = "Saham"),
            MarketAsset(symbol = "BBRI", name = "Bank Rakyat Indonesia Tbk", price = 4880L, changePercent = 0.62, type = "Saham"),
            MarketAsset(symbol = "TLKM", name = "Telkom Indonesia Tbk", price = 2980L, changePercent = -0.67, type = "Saham"),
            MarketAsset(symbol = "ASII", name = "Astra International Tbk", price = 5150L, changePercent = 1.78, type = "Saham"),
            MarketAsset(symbol = "GOTO", name = "GoTo Gojek Tokopedia Tbk", price = 72L, changePercent = 2.85, type = "Saham")
        )
    )
    val marketAssets: StateFlow<List<MarketAsset>> = _marketAssets.asStateFlow()

    private val _spendingLocations = MutableStateFlow(
        listOf(
            SpendingLocation(id = "1", name = "Grand Indonesia Shopping Town", city = "Jakarta Pusat", totalSpent = 1250000L, transactionCount = 4, category = "Belanja & Hiburan"),
            SpendingLocation(id = "2", name = "Senayan City Mall", city = "Jakarta Selatan", totalSpent = 850000L, transactionCount = 2, category = "Kuliner"),
            SpendingLocation(id = "3", name = "Starbucks Reserve FX", city = "Jakarta Pusat", totalSpent = 195000L, transactionCount = 3, category = "Kopi & Kafe"),
            SpendingLocation(id = "4", name = "Tebet Eco Park Kiosk", city = "Jakarta Selatan", totalSpent = 45000L, transactionCount = 1, category = "Makanan Ringan")
        )
    )
    val spendingLocations: StateFlow<List<SpendingLocation>> = _spendingLocations.asStateFlow()

    fun toggleHideBalance() {
        _hideBalance.value = !_hideBalance.value
    }

    fun setPeriod(period: String) {
        _selectedPeriod.value = period
    }

    fun addTransaction(transaction: Transaction) {
        _transactions.value = listOf(transaction) + _transactions.value
    }

    fun deleteTransaction(id: Long) {
        _transactions.value = _transactions.value.filter { it.id != id }
    }

    fun addAccount(account: Account) {
        _accounts.value = _accounts.value + account
    }

    fun addBudget(budget: BudgetConfig) {
        _budgets.value = _budgets.value + budget
    }

    fun updateBudget(budget: BudgetConfig) {
        _budgets.value = _budgets.value.map {
            if (it.name == budget.name) budget else it
        }
    }

    fun contributeGoal(goalId: String, amount: Long) {
        _savingsGoals.value = _savingsGoals.value.map {
            if (it.id == goalId) it.copy(currentAmount = it.currentAmount + amount) else it
        }
    }

    fun addGoal(goal: SavingsGoal) {
        _savingsGoals.value = _savingsGoals.value + goal
    }

    fun toggleSubscription(subId: String) {
        _subscriptions.value = _subscriptions.value.map {
            if (it.id == subId) it.copy(active = !it.active) else it
        }
    }

    fun addSubscription(sub: SubscriptionItem) {
        _subscriptions.value = _subscriptions.value + sub
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
    }
}
