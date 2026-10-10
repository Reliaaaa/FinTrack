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

    // Complete Accounts List matching HTML Specification (Total: Rp 133.400.000)
    private val _accounts = MutableStateFlow(
        listOf(
            Account(
                id = "1",
                name = "BCA Tahapan Prioritas",
                type = "Bank",
                amount = 45200000L,
                accountNumber = "•••• 8821",
                adminFee = 17000L,
                interestRate = 0.10,
                isPrimary = true
            ),
            Account(
                id = "2",
                name = "Mandiri Tabungan Payroll",
                type = "Bank",
                amount = 15000000L,
                accountNumber = "•••• 4410",
                adminFee = 12500L,
                interestRate = 0.08
            ),
            Account(
                id = "3",
                name = "Bank Jago Digital Saver",
                type = "Bank",
                amount = 2200000L,
                accountNumber = "•••• 1029",
                adminFee = 0L,
                interestRate = 2.50
            ),
            Account(
                id = "4",
                name = "GoPay Tabungan by Jago",
                type = "E-Wallet",
                amount = 14750000L,
                accountNumber = "0812 •••• 9920",
                adminFee = 0L,
                interestRate = 3.00
            ),
            Account(
                id = "5",
                name = "OVO Saldo & Investasi",
                type = "E-Wallet",
                amount = 7500000L,
                accountNumber = "0812 •••• 9920",
                adminFee = 0L,
                interestRate = 3.50
            ),
            Account(
                id = "6",
                name = "Stockbit Sekuritas (RDN BCA)",
                type = "Sekuritas",
                amount = 48750000L,
                accountNumber = "ID Kustodian: XL123456",
                cashRdn = 6250000L,
                stockValue = 42500000L
            )
        )
    )
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    // Transactions list
    private val _transactions = MutableStateFlow(
        listOf(
            Transaction(
                id = 1,
                name = "PT Teknologi Finansial",
                category = "Pemasukan",
                amount = 15000000L,
                date = getTodayDate(),
                account = "BCA Utama (•••• 4821)",
                note = "Gaji Bulanan",
                type = "income"
            ),
            Transaction(
                id = 2,
                name = "Supermarket GrandLucky",
                category = "Makanan & Minuman",
                amount = -850000L,
                date = getTodayDate(),
                account = "BCA Utama (•••• 4821)",
                note = "Makanan & Bahan Pokok",
                type = "expense",
                merchantLocation = "SCBD Sudirman",
                receiptImageAttached = true,
                rewardsPoints = 15
            ),
            Transaction(
                id = 3,
                name = "Tagihan Listrik & WiFi",
                category = "Tagihan & Utilitas",
                amount = -450000L,
                date = getDateOffset(1),
                account = "BCA Utama (•••• 4821)",
                note = "Utilitas Rutin Rumah",
                type = "expense"
            ),
            Transaction(
                id = 4,
                name = "Grab Car ke Kantor",
                category = "Transportasi",
                amount = -70000L,
                date = getDateOffset(1),
                account = "GoPay Wallet",
                note = "Perjalanan Kerja PP",
                type = "expense"
            ),
            Transaction(
                id = 5,
                name = "Kopi Kenangan Senopati",
                category = "Makanan & Minuman",
                amount = -65000L,
                date = getDateOffset(2),
                account = "GoPay Wallet",
                note = "Kopi Sore Tim",
                type = "expense",
                merchantLocation = "Jl. Senopati No. 41"
            ),
            Transaction(
                id = 6,
                name = "Dividen Saham BBCA",
                category = "Investasi",
                amount = 650000L,
                date = getDateOffset(3),
                account = "Stockbit Sekuritas",
                note = "Dividen Tunai Interim",
                type = "income"
            ),
            Transaction(
                id = 7,
                name = "IKEA Alam Sutera",
                category = "Belanja Bulanan",
                amount = -1250000L,
                date = getDateOffset(4),
                account = "BCA Utama (•••• 4821)",
                note = "Perlengkapan Meja Kerja",
                type = "expense",
                receiptImageAttached = true
            )
        )
    )
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // Monthly Category Budgets (Matching 50/30/20 Formula)
    private val _budgets = MutableStateFlow(
        listOf(
            BudgetConfig(name = "Makanan & Minuman", limit = 3000000L, threshold = 80, recurring = true, month = getCurrentMonth(), categoryGroup = "Kebutuhan"),
            BudgetConfig(name = "Belanja Bulanan", limit = 2000000L, threshold = 90, recurring = true, month = getCurrentMonth(), categoryGroup = "Kebutuhan"),
            BudgetConfig(name = "Transportasi", limit = 1500000L, threshold = 80, recurring = true, month = getCurrentMonth(), categoryGroup = "Kebutuhan"),
            BudgetConfig(name = "Tagihan & Utilitas", limit = 1000000L, threshold = 85, recurring = true, month = getCurrentMonth(), categoryGroup = "Kebutuhan"),
            BudgetConfig(name = "Hiburan & Rekreasi", limit = 800000L, threshold = 75, recurring = true, month = getCurrentMonth(), categoryGroup = "Keinginan"),
            BudgetConfig(name = "Investasi & Tabungan", limit = 1700000L, threshold = 80, recurring = true, month = getCurrentMonth(), categoryGroup = "Tabungan")
        )
    )
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

    // Subscriptions List
    private val _subscriptions = MutableStateFlow(
        listOf(
            SubscriptionItem(
                id = "1",
                name = "Netflix Premium 4K",
                cost = 186000L,
                billingCycle = "Bulanan",
                nextDueDate = getFutureDate(2),
                daysLeft = 2,
                active = true,
                category = "Hiburan & Streaming",
                paymentSource = "Jenius Visa (...4819)"
            ),
            SubscriptionItem(
                id = "2",
                name = "Spotify Family Plan",
                cost = 86900L,
                billingCycle = "Bulanan",
                nextDueDate = getFutureDate(8),
                daysLeft = 8,
                active = true,
                category = "Hiburan & Streaming",
                paymentSource = "GoPay Wallet"
            ),
            SubscriptionItem(
                id = "3",
                name = "ChatGPT Plus (OpenAI)",
                cost = 315000L, // ~$20.00
                billingCycle = "Bulanan",
                nextDueDate = getFutureDate(16),
                daysLeft = 16,
                active = true,
                category = "Produktivitas & Kerja",
                paymentSource = "Mandiri Virtual (...9921)"
            ),
            SubscriptionItem(
                id = "4",
                name = "Notion AI Workspace",
                cost = 150000L,
                billingCycle = "Bulanan",
                nextDueDate = getFutureDate(6),
                daysLeft = 6,
                active = true,
                category = "Produktivitas & Kerja",
                paymentSource = "BCA Prioritas"
            ),
            SubscriptionItem(
                id = "5",
                name = "iCloud+ 200GB (Apple)",
                cost = 45000L,
                billingCycle = "Bulanan",
                nextDueDate = getFutureDate(22),
                daysLeft = 22,
                active = true,
                category = "Hiburan & Streaming",
                paymentSource = "Apple ID Saldo"
            )
        )
    )
    val subscriptions: StateFlow<List<SubscriptionItem>> = _subscriptions.asStateFlow()

    // Savings Goals
    private val _savingsGoals = MutableStateFlow(
        listOf(
            SavingsGoal(
                id = "1",
                title = "MacBook Pro M3 14-inch",
                targetAmount = 28500000L,
                currentAmount = 12000000L,
                deadline = "15 Desember $currentYear",
                category = "Gadget & Produktivitas",
                reasonNote = "Investasi kerja freelance dan produktivitas desain UI/UX",
                iconName = "laptop_mac"
            ),
            SavingsGoal(
                id = "2",
                title = "Dana Darurat 6 Bulan",
                targetAmount = 50000000L,
                currentAmount = 32500000L,
                deadline = "Desember $currentYear",
                category = "Keamanan Finansial",
                reasonNote = "Pondasi ketahanan finansial keluarga",
                iconName = "shield"
            ),
            SavingsGoal(
                id = "3",
                title = "Liburan Musim Dingin Jepang",
                targetAmount = 25000000L,
                currentAmount = 14200000L,
                deadline = "Maret ${currentYear + 1}",
                category = "Liburan",
                reasonNote = "Trip Tokyo & Kyoto bersama keluarga",
                iconName = "flight"
            )
        )
    )
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

    // Spending Locations / Geo-Hotspots
    private val _spendingLocations = MutableStateFlow(
        listOf(
            SpendingLocation(
                id = "1",
                name = "Grand Lucky Superstore - SCBD",
                city = "Jakarta Selatan",
                totalSpent = 1420000L,
                transactionCount = 3,
                category = "Supermarket",
                distance = "1.2 km dari kantor",
                lastVisited = "Kemarin, 19:42 WIB"
            ),
            SpendingLocation(
                id = "2",
                name = "Plaza Indonesia Mall Area",
                city = "Jakarta Pusat",
                totalSpent = 2450000L,
                transactionCount = 2,
                category = "Belanja & Fesyen",
                distance = "4.5 km dari rumah",
                lastVisited = "12 Nov, 15:10 WIB"
            ),
            SpendingLocation(
                id = "3",
                name = "Kopi Kenangan - Senopati",
                city = "Jakarta Selatan",
                totalSpent = 180000L,
                transactionCount = 4,
                category = "Kafe & Kopi",
                distance = "800 m dari kantor",
                lastVisited = "Hari ini, 08:30 WIB"
            ),
            SpendingLocation(
                id = "4",
                name = "SPBU Pertamina 31.129 Kuningan",
                city = "Jakarta Selatan",
                totalSpent = 650000L,
                transactionCount = 3,
                category = "Bahan Bakar & Tol",
                distance = "Mobilitas Rutin",
                lastVisited = "10 Nov, 07:15 WIB"
            ),
            SpendingLocation(
                id = "5",
                name = "Sushi Tei - Senayan City",
                city = "Jakarta Pusat",
                totalSpent = 770000L,
                transactionCount = 2,
                category = "Restoran",
                distance = "2.1 km",
                lastVisited = "07 Nov, 20:15 WIB"
            )
        )
    )
    val spendingLocations: StateFlow<List<SpendingLocation>> = _spendingLocations.asStateFlow()

    // Scanned Receipt Items for OCR Studio
    private val _receiptItems = MutableStateFlow(
        listOf(
            ReceiptItem("1", "Minyak Goreng Sania 2L", "Kebutuhan Pokok", 38000L, 2),
            ReceiptItem("2", "Daging Sapi Tenderloin 500g", "Lauk Pauk", 185000L, 1),
            ReceiptItem("3", "Beras Organik Mentik 5kg", "Kebutuhan Pokok", 115000L, 1),
            ReceiptItem("4", "Susu Segar Greenfields 1L", "Minuman & Dairy", 32000L, 3),
            ReceiptItem("5", "Aneka Buah Segar Apel & Jeruk", "Buah & Sayur", 128000L, 1),
            ReceiptItem("6", "Roti Gandum & Keju Slice", "Sarapan", 80000L, 1)
        )
    )
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
        _receiptItems.value = listOf(
            ReceiptItem("1", "Minyak Goreng Sania 2L", "Kebutuhan Pokok", 38000L, 2),
            ReceiptItem("2", "Daging Sapi Tenderloin 500g", "Lauk Pauk", 185000L, 1),
            ReceiptItem("3", "Beras Organik Mentik 5kg", "Kebutuhan Pokok", 115000L, 1),
            ReceiptItem("4", "Susu Segar Greenfields 1L", "Minuman & Dairy", 32000L, 3),
            ReceiptItem("5", "Aneka Buah Segar Apel & Jeruk", "Buah & Sayur", 128000L, 1),
            ReceiptItem("6", "Roti Gandum & Keju Slice", "Sarapan", 80000L, 1)
        )
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
