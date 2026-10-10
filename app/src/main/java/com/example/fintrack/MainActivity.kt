package com.example.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.data.model.Account
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.fintrack.data.model.BudgetConfig
import com.example.fintrack.data.model.SavingsGoal
import com.example.fintrack.data.model.SubscriptionItem
import com.example.fintrack.data.model.Transaction
import com.example.fintrack.data.repository.FinTrackRepository
import com.example.fintrack.ui.screens.AccountsScreen
import com.example.fintrack.ui.screens.BudgetScreen
import com.example.fintrack.ui.screens.GoalsScreen
import com.example.fintrack.ui.screens.InstantExpenseScreen
import com.example.fintrack.ui.screens.InvestmentsScreen
import com.example.fintrack.ui.screens.OnboardingScreen
import com.example.fintrack.ui.screens.OverviewScreen
import com.example.fintrack.ui.screens.ProfileSettingsScreen
import com.example.fintrack.ui.screens.ReceiptScannerScreen
import com.example.fintrack.ui.screens.ReportsScreen
import com.example.fintrack.ui.screens.SpendingLocationsScreen
import com.example.fintrack.ui.screens.SubscriptionsScreen
import com.example.fintrack.ui.screens.TransactionsScreen
import com.example.fintrack.ui.theme.DarkNavyBackground
import com.example.fintrack.ui.theme.DarkNavyBorder
import com.example.fintrack.ui.theme.DarkNavyCard
import com.example.fintrack.ui.theme.DarkNavyCardElevated
import com.example.fintrack.ui.theme.DarkNavyMuted
import com.example.fintrack.ui.theme.FinTrackTheme
import com.example.fintrack.ui.theme.PrimaryCobalt
import com.example.fintrack.ui.theme.SecondaryMint
import com.example.fintrack.ui.theme.TextForeground
import com.example.fintrack.ui.theme.TextMuted
import com.example.fintrack.worker.WorkManagerScheduler
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FinTrackRepository.init(applicationContext)
        WorkManagerScheduler.scheduleDailyBriefing(applicationContext)
        setContent {
            FinTrackTheme {
                FinTrackRoot()
            }
        }
    }
}

@Composable
fun FinTrackRoot() {
    val isOnboarded by FinTrackRepository.isOnboarded.collectAsState()

    // Request notification permission for Android 13+ (POST_NOTIFICATIONS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { /* Handled */ }
        )
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (!isOnboarded) {
        OnboardingScreen(
            onComplete = { name, nickname, email, phone, initialBalance ->
                FinTrackRepository.completeOnboarding(name, nickname, email, phone, initialBalance)
            }
        )
    } else {
        FinTrackApp()
    }
}

enum class FinTrackScreen(val title: String, val icon: ImageVector) {
    OVERVIEW("Ringkasan", Icons.Default.TrendingUp),
    TRANSACTIONS("Transaksi", Icons.Default.ReceiptLong),
    BUDGETS("Anggaran", Icons.Default.PieChart),
    ACCOUNTS("Rekening", Icons.Default.AccountBalanceWallet),
    REPORTS("Laporan", Icons.Default.BarChart),
    MARKETS("Emas & Valas", Icons.Default.MonetizationOn),
    GOALS("Target", Icons.Default.Savings),
    SUBSCRIPTIONS("Langganan", Icons.Default.NotificationsActive),
    INSTANT("Instan", Icons.Default.ElectricBolt),
    RECEIPT("Struk OCR", Icons.Default.Receipt),
    LOCATIONS("Lokasi", Icons.Default.Place),
    PROFILE("Profil", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinTrackApp() {
    var currentScreen by remember { mutableStateOf(FinTrackScreen.OVERVIEW) }
    var showMoreMenuSheet by remember { mutableStateOf(false) }

    // Dialog state controllers
    var showAddTxDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddSubDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showToast: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val selectedPeriod by FinTrackRepository.selectedPeriod.collectAsState()

    Scaffold(
        containerColor = DarkNavyBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryCobalt),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "fintrack.",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextForeground
                        )
                    }
                },
                actions = {
                    var showCalendarDialog by remember { mutableStateOf(false) }
                    val context = LocalContext.current

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyCard)
                            .border(1.dp, PrimaryCobalt.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable { showCalendarDialog = true }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Buka Kalender Pengeluaran",
                                tint = SecondaryMint,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = selectedPeriod,
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryMint,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    if (showCalendarDialog) {
                        val allTx by FinTrackRepository.transactions.collectAsState()
                        val daysWithTx = remember(allTx) {
                            allTx.groupBy { it.date }
                        }
                        var selectedDateFilter by remember { mutableStateOf(FinTrackRepository.getTodayDate()) }

                        AlertDialog(
                            onDismissRequest = { showCalendarDialog = false },
                            containerColor = DarkNavyCardElevated,
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    alignItems = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryCobalt)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Kalender Finansial",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextForeground
                                        )
                                    }
                                }
                            },
                            text = {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Pilih tanggal untuk melihat rincian pengeluaran & pemasukan, atau sinkronkan langsung dengan aplikasi Kalender bawaan Android.",
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Switch Period Buttons (Bulan Ini / Bulan Lalu)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val currMonth = FinTrackRepository.getCurrentMonth()
                                        val prevMonth = FinTrackRepository.getPreviousMonth()

                                        Button(
                                            onClick = {
                                                FinTrackRepository.setPeriod(currMonth)
                                                showToast("Menampilkan transaksi $currMonth")
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selectedPeriod == currMonth) PrimaryCobalt else DarkNavyBackground
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "Bulan Ini ($currMonth)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                FinTrackRepository.setPeriod(prevMonth)
                                                showToast("Menampilkan transaksi $prevMonth")
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selectedPeriod == prevMonth) PrimaryCobalt else DarkNavyBackground
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "Bulan Lalu ($prevMonth)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 7 Recent Days selector with indicators
                                    Text(
                                        text = "RIWAYAT HARIAN TERAKHIR:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        FinTrackRepository.getRecentDays().forEach { (dayName, dateStr) ->
                                            val hasExpense = daysWithTx[dateStr]?.any { it.amount < 0 } == true
                                            val isSelected = selectedDateFilter == dateStr

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        if (isSelected) PrimaryCobalt
                                                        else DarkNavyBackground
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (hasExpense) SecondaryMint.copy(alpha = 0.5f) else Color.Transparent,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { selectedDateFilter = dateStr }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = dayName,
                                                        fontSize = 10.sp,
                                                        color = if (isSelected) Color.White else TextMuted
                                                    )
                                                    val dayNum = dateStr.takeLast(2)
                                                    Text(
                                                        text = dayNum,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else TextForeground
                                                    )
                                                    if (hasExpense) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(SecondaryMint)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Transactions on selected date
                                    val txsOnDate = daysWithTx[selectedDateFilter] ?: emptyList()
                                    Text(
                                        text = "Transaksi Tanggal $selectedDateFilter (${txsOnDate.size}):",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SecondaryMint,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (txsOnDate.isEmpty()) {
                                        Text(
                                            text = "Tidak ada riwayat pengeluaran atau pemasukan pada tanggal ini.",
                                            color = TextMuted,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    } else {
                                        txsOnDate.take(3).forEach { tx ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                alignItems = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = tx.name,
                                                    color = TextForeground,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = FinTrackRepository.formatRupiah(tx.amount),
                                                    color = if (tx.amount > 0) SecondaryMint else Color(0xFFFF5252),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Button to open Android System Calendar Intent
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                    data = android.net.Uri.parse("content://com.android.calendar/time")
                                                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                showToast("Gagal membuka kalender Android: Aplikasi kalender tidak terdeteksi")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = SecondaryMint,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Buka di Kalender Android HP",
                                            color = SecondaryMint,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showCalendarDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                                ) {
                                    Text("Tutup", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkNavyBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkNavyCard,
                contentColor = TextForeground
            ) {
                val primaryNavItems = listOf(
                    FinTrackScreen.OVERVIEW,
                    FinTrackScreen.TRANSACTIONS,
                    FinTrackScreen.BUDGETS,
                    FinTrackScreen.ACCOUNTS
                )

                primaryNavItems.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryCobalt,
                            selectedTextColor = PrimaryCobalt,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = PrimaryCobalt.copy(alpha = 0.15f)
                        )
                    )
                }

                // "Lainnya" (More) Tab
                val isMoreSelected = currentScreen !in primaryNavItems
                NavigationBarItem(
                    selected = isMoreSelected,
                    onClick = { showMoreMenuSheet = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Fitur Lainnya"
                        )
                    },
                    label = {
                        Text(
                            text = "Lainnya",
                            fontSize = 11.sp,
                            fontWeight = if (isMoreSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryCobalt,
                        selectedTextColor = PrimaryCobalt,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = PrimaryCobalt.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                FinTrackScreen.OVERVIEW -> OverviewScreen(
                    onNavigateToTransactions = { currentScreen = FinTrackScreen.TRANSACTIONS },
                    onNavigateToBudgets = { currentScreen = FinTrackScreen.BUDGETS },
                    onNavigateToAccounts = { currentScreen = FinTrackScreen.ACCOUNTS },
                    onNavigateToReports = { currentScreen = FinTrackScreen.REPORTS },
                    onNavigateToInstant = { currentScreen = FinTrackScreen.INSTANT },
                    onOpenAddTransaction = { showAddTxDialog = true }
                )
                FinTrackScreen.TRANSACTIONS -> TransactionsScreen(
                    onOpenAddTransaction = { showAddTxDialog = true }
                )
                FinTrackScreen.BUDGETS -> BudgetScreen(
                    onOpenAddBudget = { showAddBudgetDialog = true }
                )
                FinTrackScreen.ACCOUNTS -> AccountsScreen(
                    onOpenAddAccount = { showAddAccountDialog = true }
                )
                FinTrackScreen.REPORTS -> ReportsScreen(
                    onShowToast = showToast
                )
                FinTrackScreen.MARKETS -> InvestmentsScreen()
                FinTrackScreen.GOALS -> GoalsScreen(
                    onOpenAddGoal = { showAddGoalDialog = true },
                    onShowToast = showToast
                )
                FinTrackScreen.SUBSCRIPTIONS -> SubscriptionsScreen(
                    onOpenAddSub = { showAddSubDialog = true }
                )
                FinTrackScreen.INSTANT -> InstantExpenseScreen(
                    onShowToast = showToast
                )
                FinTrackScreen.RECEIPT -> ReceiptScannerScreen(
                    onShowToast = showToast
                )
                FinTrackScreen.LOCATIONS -> SpendingLocationsScreen()
                FinTrackScreen.PROFILE -> {
                    val context = LocalContext.current
                    ProfileSettingsScreen(
                        onShowToast = showToast,
                        onResetApp = {
                            FinTrackRepository.resetAllData()
                            showToast("Semua data berhasil di-reset. Silakan isi nama kembali.")
                        },
                        onTriggerGeminiTest = {
                            WorkManagerScheduler.triggerImmediateNewsBriefing(context)
                        }
                    )
                }
            }
        }
    }

    // More Features Bottom Sheet
    if (showMoreMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreMenuSheet = false },
            containerColor = DarkNavyCard,
            contentColor = TextForeground,
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Semua Fitur FinTrack",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextForeground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val extraScreens = listOf(
                    FinTrackScreen.REPORTS,
                    FinTrackScreen.MARKETS,
                    FinTrackScreen.GOALS,
                    FinTrackScreen.SUBSCRIPTIONS,
                    FinTrackScreen.INSTANT,
                    FinTrackScreen.RECEIPT,
                    FinTrackScreen.LOCATIONS,
                    FinTrackScreen.PROFILE
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(extraScreens.size) { idx ->
                        val scr = extraScreens[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkNavyBackground)
                                .clickable {
                                    currentScreen = scr
                                    showMoreMenuSheet = false
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryCobalt.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = scr.icon,
                                    contentDescription = scr.title,
                                    tint = PrimaryCobalt,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = scr.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextForeground
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Add Transaction Dialog
    if (showAddTxDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Makanan & Minuman") }
        var amountText by remember { mutableStateOf("") }
        var isExpense by remember { mutableStateOf(true) }
        var account by remember { mutableStateOf("BCA") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTxDialog = false },
            containerColor = DarkNavyCard,
            title = {
                Text(
                    text = "Catat Transaksi Baru",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Type selector (Pengeluaran, Pemasukan, Transfer)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyBackground)
                            .padding(2.dp)
                    ) {
                        listOf("Pengeluaran" to true, "Pemasukan" to false).forEach { (lbl, isExp) ->
                            val isSel = isExpense == isExp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) PrimaryCobalt else Color.Transparent)
                                    .clickable { isExpense = isExp }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = if (isSel) Color.White else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Quick Chips (+50rb, +100rb, +500rb, Pas)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(50000L to "+50rb", 100000L to "+100rb", 500000L to "+500rb").forEach { (addVal, lbl) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkNavyBackground)
                                    .clickable {
                                        val current = amountText.toLongOrNull() ?: 0L
                                        amountText = (current + addVal).toString()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = TextForeground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Transaksi / Merchant", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                        label = { Text("Nominal (Rp)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategori (e.g. Makanan, Belanja)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = account,
                        onValueChange = { account = it },
                        label = { Text("Rekening (e.g. BCA, GoPay)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toLongOrNull() ?: 0L
                        if (name.isNotBlank() && amount > 0L) {
                            FinTrackRepository.addTransaction(
                                Transaction(
                                    id = System.currentTimeMillis(),
                                    name = name,
                                    category = category,
                                    amount = if (isExpense) -amount else amount,
                                    date = FinTrackRepository.getTodayDate(),
                                    account = account,
                                    note = note
                                )
                            )
                            showToast("Transaksi '$name' berhasil disimpan!")
                            showAddTxDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddTxDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                ) {
                    Text("Batal")
                }
            }
        )
    }

    // Add Budget Dialog
    if (showAddBudgetDialog) {
        var budgetName by remember { mutableStateOf("") }
        var limitText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddBudgetDialog = false },
            containerColor = DarkNavyCard,
            title = {
                Text(
                    text = "Tambah Pagu Anggaran",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = budgetName,
                        onValueChange = { budgetName = it },
                        label = { Text("Nama Kategori Anggaran", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { limitText = it.filter { char -> char.isDigit() } },
                        label = { Text("Batas Pagu Bulanan (Rp)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = limitText.toLongOrNull() ?: 0L
                        if (budgetName.isNotBlank() && limit > 0L) {
                            FinTrackRepository.addBudget(
                                BudgetConfig(
                                    name = budgetName,
                                    limit = limit,
                                    threshold = 80,
                                    recurring = true,
                                    month = selectedPeriod
                                )
                            )
                            showToast("Pagu anggaran '$budgetName' berhasil ditambahkan!")
                            showAddBudgetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddBudgetDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }

    // Add Account Dialog
    if (showAddAccountDialog) {
        var accName by remember { mutableStateOf("") }
        var accType by remember { mutableStateOf("Rekening Bank") }
        var accBalanceText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddAccountDialog = false },
            containerColor = DarkNavyCard,
            title = {
                Text(
                    text = "Tambah Rekening Baru",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = accName,
                        onValueChange = { accName = it },
                        label = { Text("Nama Rekening / Bank / E-Wallet", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = accType,
                        onValueChange = { accType = it },
                        label = { Text("Tipe (Rekening Bank, E-Wallet, Investasi)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = accBalanceText,
                        onValueChange = { accBalanceText = it.filter { char -> char.isDigit() } },
                        label = { Text("Saldo Awal (Rp)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = accBalanceText.toLongOrNull() ?: 0L
                        if (accName.isNotBlank()) {
                            FinTrackRepository.addAccount(
                                Account(
                                    id = System.currentTimeMillis().toString(),
                                    name = accName,
                                    type = accType,
                                    amount = amount,
                                    accountNumber = "9982-1209"
                                )
                            )
                            showToast("Rekening '$accName' berhasil ditambahkan!")
                            showAddAccountDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddAccountDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }

    // Add Goal Dialog
    if (showAddGoalDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var targetAmountText by remember { mutableStateOf("") }
        var goalCategory by remember { mutableStateOf("Tabungan") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            containerColor = DarkNavyCard,
            title = {
                Text(
                    text = "Tambah Target Impian",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Judul Target", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = targetAmountText,
                        onValueChange = { targetAmountText = it.filter { char -> char.isDigit() } },
                        label = { Text("Target Nominal (Rp)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = targetAmountText.toLongOrNull() ?: 0L
                        if (goalTitle.isNotBlank() && target > 0L) {
                            val targetYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 1
                            FinTrackRepository.addGoal(
                                SavingsGoal(
                                    id = System.currentTimeMillis().toString(),
                                    title = goalTitle,
                                    targetAmount = target,
                                    currentAmount = 0L,
                                    deadline = "Desember $targetYear",
                                    category = goalCategory
                                )
                            )
                            showToast("Target '$goalTitle' berhasil dibuat!")
                            showAddGoalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddGoalDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }

    // Add Subscription Dialog
    if (showAddSubDialog) {
        var subName by remember { mutableStateOf("") }
        var subCostText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSubDialog = false },
            containerColor = DarkNavyCard,
            title = {
                Text(
                    text = "Tambah Pengingat Langganan",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextForeground,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Nama Layanan / SIM Kuota", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = subCostText,
                        onValueChange = { subCostText = it.filter { char -> char.isDigit() } },
                        label = { Text("Biaya Bulanan (Rp)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyBackground,
                            unfocusedContainerColor = DarkNavyBackground,
                            focusedTextColor = TextForeground,
                            unfocusedTextColor = TextForeground,
                            focusedBorderColor = PrimaryCobalt,
                            unfocusedBorderColor = DarkNavyBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cost = subCostText.toLongOrNull() ?: 0L
                        if (subName.isNotBlank() && cost > 0L) {
                            FinTrackRepository.addSubscription(
                                SubscriptionItem(
                                    id = System.currentTimeMillis().toString(),
                                    name = subName,
                                    cost = cost,
                                    billingCycle = "Bulanan",
                                    nextDueDate = FinTrackRepository.getFutureDate(30),
                                    active = true
                                )
                            )
                            showToast("Langganan '$subName' berhasil ditambahkan!")
                            showAddSubDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddSubDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }
}
