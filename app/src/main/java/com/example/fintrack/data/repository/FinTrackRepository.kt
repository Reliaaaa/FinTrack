package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.data.local.AppDatabase
import com.example.fintrack.data.local.PreferenceManager
import com.example.fintrack.data.model.Account
import com.example.fintrack.data.model.BudgetConfig
import com.example.fintrack.data.model.MarketAsset
import com.example.fintrack.data.model.ReceiptItem
import com.example.fintrack.data.model.SavingsGoal
import com.example.fintrack.data.model.SimCardItem
import com.example.fintrack.data.model.SpendingLocation
import com.example.fintrack.data.model.SubscriptionItem
import com.example.fintrack.data.model.Transaction
import com.example.fintrack.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FinTrackRepository {
    private var preferenceManager: PreferenceManager? = null
    private var appDatabase: AppDatabase? = null

    private val _isOnboarded = MutableStateFlow(false)
    val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

    private val localeID = Locale("id", "ID")
    private val numberFormat = NumberFormat.getNumberInstance(localeID)

    fun formatRupiah(amount: Long, withPrefix: Boolean = true): String {
        val formatted = numberFormat.format(kotlin.math.abs(amount))
        return if (withPrefix) "Rp $formatted" else formatted
    }

    // Dynamic Date & Time Utilities
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentMonth(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getPreviousMonth(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun getFormattedToday(): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", localeID)
        return sdf.format(Date())
    }

    fun getDateOffset(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun getDateOffsetFormatted(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("d MMM yyyy", localeID)
        return sdf.format(cal.time)
    }

    fun getFutureDate(daysAhead: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysAhead)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun getRecentDays(): List<Pair<String, String>> {
        val daySdf = SimpleDateFormat("EEE", localeID)
        val dateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            Pair(daySdf.format(cal.time), dateSdf.format(cal.time))
        }
    }

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(getCurrentMonth())
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _hideBalance = MutableStateFlow(false)
    val hideBalance: StateFlow<Boolean> = _hideBalance.asStateFlow()

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    // Clean database for fresh usage - users create their own accounts
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    // Clean database for fresh usage - user starts empty
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // Monthly Category Budgets - clean for user configuration
    private val _budgets = MutableStateFlow<List<BudgetConfig>>(emptyList())
    val budgets: StateFlow<List<BudgetConfig>> = _budgets.asStateFlow()

    // SIM Cards (Dual SIM Setup)
    private val _simCards = MutableStateFlow(
        listOf(
            SimCardItem(
                simSlot = 1,
                provider = "Telkomsel",
                planName = "SimPATI InternetMAX 35GB",
                totalQuotaGb = 35.0,
                remainingQuotaGb = 8.4,
                cost = 125000L,
                daysLeft = 3,
                dueDate = getFutureDate(3),
                autoDebit = true,
                isPrimaryData = true
            ),
            SimCardItem(
                simSlot = 2,
                provider = "XL Axiata",
                planName = "XL Xtra Combo Flex 20GB",
                totalQuotaGb = 20.0,
                remainingQuotaGb = 16.2,
                cost = 90000L,
                daysLeft = 18,
                dueDate = getFutureDate(18),
                autoDebit = false,
                isPrimaryData = false
            )
        )
    )
    val simCards: StateFlow<List<SimCardItem>> = _simCards.asStateFlow()

    // Subscriptions List - clean for user configuration
    private val _subscriptions = MutableStateFlow<List<SubscriptionItem>>(emptyList())
    val subscriptions: StateFlow<List<SubscriptionItem>> = _subscriptions.asStateFlow()

    // Savings Goals - clean for user configuration
    private val _savingsGoals = MutableStateFlow<List<SavingsGoal>>(emptyList())
    val savingsGoals: StateFlow<List<SavingsGoal>> = _savingsGoals.asStateFlow()

    // Commodities, Forex & IDX Stocks
    private val _marketAssets = MutableStateFlow(
        listOf(
            MarketAsset(
                symbol = "ANTAM",
                name = "Emas Murni 24K (LBMA Certified)",
                price = 1485000L,
                changePercent = 0.95,
                type = "Emas",
                highPrice = 1543000L,
                lowPrice = 1125000L
            ),
            MarketAsset(
                symbol = "UBS",
                name = "Emas Batangan UBS",
                price = 1465000L,
                changePercent = 0.42,
                type = "Emas",
                highPrice = 1510000L,
                lowPrice = 1110000L
            ),
            MarketAsset(
                symbol = "USD/IDR",
                name = "Dolar Amerika Serikat (JISDOR BI)",
                price = 15845L,
                changePercent = -0.16,
                type = "Valas",
                highPrice = 15890L,
                lowPrice = 15820L
            ),
            MarketAsset(
                symbol = "EUR/IDR",
                name = "Euro Uni Eropa",
                price = 16712L,
                changePercent = 0.24,
                type = "Valas"
            ),
            MarketAsset(
                symbol = "SGD/IDR",
                name = "Dolar Singapura",
                price = 11820L,
                changePercent = 0.12,
                type = "Valas"
            ),
            MarketAsset(
                symbol = "JPY/IDR",
                name = "Yen Jepang (per 100 JPY)",
                price = 10235L,
                changePercent = -0.45,
                type = "Valas"
            ),
            MarketAsset(
                symbol = "BBCA",
                name = "PT Bank Central Asia Tbk",
                price = 10150L,
                changePercent = 1.75,
                type = "Saham",
                highPrice = 10200L,
                lowPrice = 9950L,
                volumeLot = "84.2M",
                rsi = 58.4,
                macd = "+42.1 (Golden Cross)",
                foreignFlow = "+214.8 M"
            ),
            MarketAsset(
                symbol = "BBRI",
                name = "PT Bank Rakyat Indonesia Tbk",
                price = 4620L,
                changePercent = 2.21,
                type = "Saham",
                highPrice = 4680L,
                lowPrice = 4550L,
                volumeLot = "120.4M"
            ),
            MarketAsset(
                symbol = "TLKM",
                name = "PT Telkom Indonesia Tbk",
                price = 2850L,
                changePercent = -0.70,
                type = "Saham",
                highPrice = 2900L,
                lowPrice = 2820L,
                volumeLot = "45.1M"
            ),
            MarketAsset(
                symbol = "ASII",
                name = "PT Astra International Tbk",
                price = 5100L,
                changePercent = 1.49,
                type = "Saham",
                highPrice = 5150L,
                lowPrice = 5025L,
                volumeLot = "32.0M"
            )
        )
    )
    val marketAssets: StateFlow<List<MarketAsset>> = _marketAssets.asStateFlow()

    // Spending Locations / Geo-Hotspots - clean for user transactions
    private val _spendingLocations = MutableStateFlow<List<SpendingLocation>>(emptyList())
    val spendingLocations: StateFlow<List<SpendingLocation>> = _spendingLocations.asStateFlow()

    // Scanned Receipt Items for OCR Studio - clean for user's own receipts
    private val _receiptItems = MutableStateFlow<List<ReceiptItem>>(emptyList())
    val receiptItems: StateFlow<List<ReceiptItem>> = _receiptItems.asStateFlow()

    fun addReceiptItem(item: ReceiptItem) {
        _receiptItems.value = _receiptItems.value + item
    }

    fun removeReceiptItem(id: String) {
        _receiptItems.value = _receiptItems.value.filter { it.id != id }
    }

    fun updateReceiptItemQty(id: String, delta: Int) {
        _receiptItems.value = _receiptItems.value.map {
            if (it.id == id) {
                val newQty = (it.quantity + delta).coerceAtLeast(1)
                it.copy(quantity = newQty)
            } else it
        }
    }

    fun resetReceiptItems() {
        _receiptItems.value = emptyList()
    }

    fun clearReceiptItems() {
        _receiptItems.value = emptyList()
    }

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

    fun setPrimaryAccount(accountId: String) {
        _accounts.value = _accounts.value.map {
            it.copy(isPrimary = it.id == accountId)
        }
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

    fun toggleSimDebit(simSlot: Int) {
        _simCards.value = _simCards.value.map {
            if (it.simSlot == simSlot) it.copy(autoDebit = !it.autoDebit) else it
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        preferenceManager?.let { prefs ->
            prefs.userName = profile.name
            prefs.userNickname = profile.nickname
            prefs.userEmail = profile.email
            prefs.biometricEnabled = profile.biometricEnabled
            prefs.twoFactorEnabled = profile.twoFactorEnabled
        }
    }

    fun init(context: Context) {
        val prefs = PreferenceManager(context)
        preferenceManager = prefs
        appDatabase = AppDatabase.getDatabase(context)

        _isOnboarded.value = prefs.isOnboarded
        if (prefs.isOnboarded && prefs.userName.isNotBlank()) {
            _userProfile.value = _userProfile.value.copy(
                name = prefs.userName,
                nickname = prefs.userNickname.ifBlank { prefs.userName },
                email = prefs.userEmail.ifBlank { "pengguna@fintrack.id" },
                phone = prefs.userPhone.ifBlank { "+62 812-0000-0000" },
                currency = prefs.currency,
                biometricEnabled = prefs.biometricEnabled,
                twoFactorEnabled = prefs.twoFactorEnabled
            )
        }
    }

    fun completeOnboarding(name: String, nickname: String, email: String, phone: String, initialBalance: Long) {
        val prefs = preferenceManager
        if (prefs != null) {
            prefs.isOnboarded = true
            prefs.userName = name
            prefs.userNickname = nickname
            prefs.userEmail = email
            prefs.userPhone = phone
        }
        _isOnboarded.value = true
        _userProfile.value = _userProfile.value.copy(
            name = name,
            nickname = nickname,
            email = email,
            phone = phone
        )
        // Reset all secondary balances to 0, and set the primary account to user's initial selected balance
        _accounts.value = listOf(
            Account(
                id = "1",
                name = "Kas & Rekening Utama",
                type = "Bank",
                amount = initialBalance,
                accountNumber = "•••• 1001",
                adminFee = 0L,
                interestRate = 0.0,
                isPrimary = true
            ),
            Account(
                id = "2",
                name = "Tabungan Digital / E-Wallet",
                type = "E-Wallet",
                amount = 0L,
                accountNumber = phone.ifBlank { "0812 •••• 0001" },
                adminFee = 0L,
                interestRate = 0.0
            ),
            Account(
                id = "3",
                name = "Portofolio Sekuritas & Investasi",
                type = "Sekuritas",
                amount = 0L,
                accountNumber = "ID: RDN-001",
                cashRdn = 0L,
                stockValue = 0L
            )
        )
        // If initial balance is greater than 0, create an initial deposit transaction
        if (initialBalance > 0) {
            _transactions.value = listOf(
                Transaction(
                    id = System.currentTimeMillis(),
                    name = "Saldo Awal Pendaftaran",
                    category = "Pemasukan",
                    amount = initialBalance,
                    date = getTodayDate(),
                    account = "Kas & Rekening Utama",
                    note = "Setoran saldo awal akun FinTrack",
                    type = "income"
                )
            )
        } else {
            _transactions.value = emptyList()
        }
    }

    fun resetAllData() {
        preferenceManager?.resetAllPreferences()
        _isOnboarded.value = false
        _userProfile.value = UserProfile(
            name = "",
            nickname = "",
            email = "",
            phone = "",
            userCode = "USR-" + (100000..999999).random()
        )
        // Clear all accounts, balances, and transactions
        _accounts.value = listOf(
            Account(
                id = "1",
                name = "Kas & Rekening Utama",
                type = "Bank",
                amount = 0L,
                accountNumber = "•••• 1001",
                isPrimary = true
            )
        )
        _transactions.value = emptyList()
        _budgets.value = emptyList()
        _savingsGoals.value = emptyList()
    }
}
