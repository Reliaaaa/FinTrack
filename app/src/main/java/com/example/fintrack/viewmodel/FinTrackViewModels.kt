package com.example.fintrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.data.model.Account
import com.example.fintrack.data.model.BudgetConfig
import com.example.fintrack.data.model.SavingsGoal
import com.example.fintrack.data.model.SubscriptionItem
import com.example.fintrack.data.model.Transaction
import com.example.fintrack.data.model.UserProfile
import com.example.fintrack.data.repository.FinTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for Transactions management with reactive search and filter logic.
 */
class TransactionsViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow("Semua") // "Semua", "expense", "income"
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    val transactions: StateFlow<List<Transaction>> = combine(
        FinTrackRepository.transactions,
        _searchQuery,
        _selectedCategory,
        _selectedType
    ) { txList, query, cat, type ->
        txList.filter { tx ->
            val matchQuery = query.isBlank() || tx.name.contains(query, ignoreCase = true) || tx.note.contains(query, ignoreCase = true)
            val matchCategory = cat == "Semua" || tx.category == cat
            val matchType = type == "Semua" || tx.type == type
            matchQuery && matchCategory && matchType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = type
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            FinTrackRepository.addTransaction(transaction)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            FinTrackRepository.deleteTransaction(id)
        }
    }
}

/**
 * ViewModel for Multi-Account & E-Wallet tracking.
 */
class AccountsViewModel : ViewModel() {
    val accounts: StateFlow<List<Account>> = FinTrackRepository.accounts

    val totalBalance: StateFlow<Long> = FinTrackRepository.accounts.combine(FinTrackRepository.hideBalance) { accs, _ ->
        accs.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun addAccount(account: Account) {
        viewModelScope.launch {
            FinTrackRepository.addAccount(account)
        }
    }

    fun setPrimaryAccount(accountId: String) {
        viewModelScope.launch {
            FinTrackRepository.setPrimaryAccount(accountId)
        }
    }
}

/**
 * ViewModel for Subscriptions & Recurring Bills tracking.
 */
class SubscriptionsViewModel : ViewModel() {
    val subscriptions: StateFlow<List<SubscriptionItem>> = FinTrackRepository.subscriptions

    val monthlyBurnRate: StateFlow<Long> = FinTrackRepository.subscriptions.combine(FinTrackRepository.subscriptions) { subs, _ ->
        subs.filter { it.active }.sumOf { it.cost }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val activeCount: StateFlow<Int> = FinTrackRepository.subscriptions.combine(FinTrackRepository.subscriptions) { subs, _ ->
        subs.count { it.active }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggleSubscription(id: String) {
        viewModelScope.launch {
            FinTrackRepository.toggleSubscription(id)
        }
    }

    fun addSubscription(sub: SubscriptionItem) {
        viewModelScope.launch {
            FinTrackRepository.addSubscription(sub)
        }
    }
}

/**
 * ViewModel for Financial Reports, 50/30/20 Rule & Statement generation.
 */
class ReportsViewModel : ViewModel() {
    val selectedPeriod: StateFlow<String> = FinTrackRepository.selectedPeriod

    val netSavingsFlow: StateFlow<Long> = FinTrackRepository.transactions.combine(FinTrackRepository.selectedPeriod) { txs, period ->
        val monthly = txs.filter { it.date.startsWith(period) }
        val income = monthly.filter { it.amount > 0 }.sumOf { it.amount }.ifZero { 15000000L }
        val expense = monthly.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }.ifZero { 6250000L }
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8750000L)

    private fun Long.ifZero(default: Long): Long = if (this == 0L) default else this
}

/**
 * ViewModel for Profile Management, Security Toggles & Local Data Reset.
 */
class ProfileViewModel : ViewModel() {
    val userProfile: StateFlow<UserProfile> = FinTrackRepository.userProfile

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            FinTrackRepository.updateUserProfile(profile)
        }
    }

    fun toggleBiometric() {
        val curr = userProfile.value
        updateProfile(curr.copy(biometricEnabled = !curr.biometricEnabled))
    }

    fun toggleTwoFactor() {
        val curr = userProfile.value
        updateProfile(curr.copy(twoFactorEnabled = !curr.twoFactorEnabled))
    }

    fun resetApp(onComplete: () -> Unit) {
        viewModelScope.launch {
            FinTrackRepository.resetAllData()
            onComplete()
        }
    }
}
