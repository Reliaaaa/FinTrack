package id.fintrack.app.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.fintrack.app.ui.screens.*
import id.fintrack.app.ui.screens.AssetsScreen
import id.fintrack.app.ui.screens.HomeScreen
import id.fintrack.app.ui.screens.MoreScreen
import id.fintrack.app.ui.screens.ObligationsScreen
import id.fintrack.app.ui.screens.ReportScreen
import id.fintrack.app.ui.screens.SeasonalScreen
import id.fintrack.app.ui.screens.SettingsScreen
import id.fintrack.app.ui.screens.SplitScreen
import id.fintrack.app.ui.screens.TransactionsScreen
import id.fintrack.app.ui.screens.ZakatScreen
import id.fintrack.app.vm.MainViewModel

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Dasbor", Icons.Outlined.Home),
    Tab("tx", "Transaksi", Icons.Outlined.Receipt),
    Tab("budget", "Anggaran", Icons.Outlined.PieChart),
    Tab("assets", "Aset", Icons.Outlined.AccountBalanceWallet),
    Tab("report", "Laporan", Icons.Outlined.BarChart)
)

private fun NavHostController.goTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun FinTrackApp(vm: MainViewModel = viewModel()) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val snackbar = remember { SnackbarHostState() }
    val message by vm.message.collectAsStateWithLifecycle()

    LaunchedEffect(message) {
        val m = message
        if (m != null) {
            snackbar.showSnackbar(m)
            vm.consumeMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (tabs.any { it.route == route }) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = { nav.goTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            nav,
            startDestination = "home",
            modifier = Modifier.padding(padding).consumeWindowInsets(padding)
        ) {
            composable("home") { HomeScreen(vm) { nav.goTab(it) } }
            composable("import") { ImportScreen(vm) { nav.popBackStack() } }
            composable("budget") { BudgetScreen(vm) }
            composable("forecast") { ForecastScreen(vm) { nav.popBackStack() } }
            composable("goals") { GoalsScreen(vm) { nav.popBackStack() } }
            composable("transfer") { TransferScreen(vm) { nav.popBackStack() } }
            composable("tx") { TransactionsScreen(vm) }
            composable("obl") { ObligationsScreen(vm) }
            composable("assets") { AssetsScreen(vm) }
            composable("more") { MoreScreen { nav.navigate(it) } }
            composable("report") { ReportScreen(vm) { nav.popBackStack() } }
            composable("split") { SplitScreen { nav.popBackStack() } }
            composable("zakat") { ZakatScreen(vm) { nav.popBackStack() } }
            composable("seasonal") { SeasonalScreen { nav.popBackStack() } }
            composable("settings") { SettingsScreen(vm) { nav.popBackStack() } }
        }
    }
}
