package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AlAwlaBottomBar
import com.example.ui.components.AlAwlaTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                AlAwlaAppContent(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun AlAwlaAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val selectedPackage by viewModel.selectedPackage.collectAsState()
    val selectedBooking by viewModel.selectedBooking.collectAsState()
    val recentBooking by viewModel.recentCreatedBooking.collectAsState()

    // Handle back button for sub-screens
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != AppScreen.PACKAGE_DETAIL &&
                currentScreen != AppScreen.BOOKING_FORM &&
                currentScreen != AppScreen.BOOKING_DETAIL &&
                currentScreen != AppScreen.ADMIN_DASHBOARD &&
                currentScreen != AppScreen.AUTH &&
                currentScreen != AppScreen.CUSTOMER_DASHBOARD
            ) {
                AlAwlaTopBar(
                    currentLanguage = language,
                    isDarkTheme = isDarkTheme,
                    isAdmin = user.isAdmin,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                    onToggleAdmin = { viewModel.toggleAdminMode(it) },
                    onOpenProfile = { viewModel.navigateTo(AppScreen.PROFILE) },
                    onOpenAdmin = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) },
                    onOpenCustomerDashboard = { viewModel.navigateTo(AppScreen.CUSTOMER_DASHBOARD) }
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.BOOKING_FORM &&
                currentScreen != AppScreen.PACKAGE_DETAIL &&
                currentScreen != AppScreen.ADMIN_DASHBOARD &&
                currentScreen != AppScreen.AUTH
            ) {
                AlAwlaBottomBar(
                    currentScreen = currentScreen,
                    currentLanguage = language,
                    onNavigate = { viewModel.navigateTo(it) }
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
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.UMRAH -> UmrahScreen(viewModel = viewModel)
                AppScreen.TOURS -> InternationalToursScreen(viewModel = viewModel)
                AppScreen.PACKAGE_DETAIL -> {
                    selectedPackage?.let { pkg ->
                        PackageDetailScreen(pkg = pkg, viewModel = viewModel)
                    } ?: HomeScreen(viewModel = viewModel)
                }
                AppScreen.BOOKING_FORM -> {
                    selectedPackage?.let { pkg ->
                        BookingScreen(pkg = pkg, viewModel = viewModel)
                    } ?: HomeScreen(viewModel = viewModel)
                }
                AppScreen.BOOKING_SUCCESS -> {
                    recentBooking?.let { b ->
                        BookingSuccessScreen(booking = b, viewModel = viewModel)
                    } ?: HomeScreen(viewModel = viewModel)
                }
                AppScreen.MY_BOOKINGS -> MyBookingsScreen(viewModel = viewModel)
                AppScreen.CUSTOMER_DASHBOARD -> CustomerDashboardScreen(viewModel = viewModel)
                AppScreen.AUTH -> AuthScreen(viewModel = viewModel)
                AppScreen.BOOKING_DETAIL -> {
                    selectedBooking?.let { b ->
                        BookingDetailScreen(booking = b, viewModel = viewModel)
                    } ?: MyBookingsScreen(viewModel = viewModel)
                }
                AppScreen.CHAT_SUPPORT -> ChatSupportScreen(viewModel = viewModel)
                AppScreen.CONTACT -> ContactScreen(viewModel = viewModel)
                AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                AppScreen.ADMIN_DASHBOARD -> AdminScreen(viewModel = viewModel)
            }
        }
    }
}
