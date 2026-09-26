package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.AppScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlAwlaTopBar(
    currentLanguage: AppLanguage,
    isDarkTheme: Boolean,
    isAdmin: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onToggleAdmin: (Boolean) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenCustomerDashboard: () -> Unit = onOpenProfile,
    modifier: Modifier = Modifier
) {
    var showLangMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title with Gold & Emerald Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* no-op */ }
                ) {
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, AlAwlaGoldPrimary, CircleShape),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.alawla_official_logo),
                            contentDescription = "Al-Awla Official Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = AppStrings.get("app_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "alawlatourtravel.com",
                            style = MaterialTheme.typography.labelSmall,
                            color = AlAwlaGoldDark
                        )
                    }
                }

                // Actions: Language Selector, Dark Mode Toggle, Admin Mode Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher Button
                    Box {
                        FilledTonalButton(
                            onClick = { showLangMenu = true },
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("language_switch_button"),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = showLangMenu,
                            onDismissRequest = { showLangMenu = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${lang.displayName} (${lang.nativeName})",
                                                fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal,
                                                color = if (lang == currentLanguage) AlAwlaEmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    },
                                    onClick = {
                                        onLanguageChange(lang)
                                        showLangMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Dark Mode Toggle
                    IconButton(
                        onClick = onToggleDarkTheme,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("dark_mode_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (isDarkTheme) AlAwlaGoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Customer Portal & Bookings Dashboard Button
                    IconButton(
                        onClick = onOpenCustomerDashboard,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("customer_portal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Customer Portal",
                            tint = AlAwlaEmeraldPrimary
                        )
                    }

                    // Admin Role Badge Switcher
                    IconButton(
                        onClick = {
                            if (isAdmin) {
                                onOpenAdmin()
                            } else {
                                onToggleAdmin(true)
                                onOpenAdmin()
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("admin_desk_button")
                    ) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin Desk",
                            tint = if (isAdmin) AlAwlaGoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlAwlaBottomBar(
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(AppScreen.HOME, Icons.Default.Home, "home"),
            Triple(AppScreen.UMRAH, Icons.Default.Mosque, "umrah"),
            Triple(AppScreen.TOURS, Icons.Default.Public, "tours"),
            Triple(AppScreen.CUSTOMER_DASHBOARD, Icons.Default.ConfirmationNumber, "bookings"),
            Triple(AppScreen.CHAT_SUPPORT, Icons.Default.ChatBubble, "support"),
            Triple(AppScreen.CONTACT, Icons.Default.HeadsetMic, "contact")
        )

        items.forEach { (screen, icon, labelKey) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = AppStrings.get(labelKey, currentLanguage),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = AppStrings.get(labelKey, currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AlAwlaEmeraldPrimary,
                    selectedTextColor = AlAwlaEmeraldPrimary,
                    indicatorColor = AlAwlaGoldContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                ),
                modifier = Modifier.testTag("nav_item_$labelKey")
            )
        }
    }
}

@Composable
fun BookingStatusBadge(
    status: BookingStatus,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        BookingStatus.APPROVED -> Triple(
            StatusApproved.copy(alpha = 0.15f),
            StatusApproved,
            when (currentLanguage) {
                AppLanguage.AMHARIC -> status.amharic
                AppLanguage.ARABIC -> status.arabic
                else -> status.label
            }
        )
        BookingStatus.PENDING -> Triple(
            StatusPending.copy(alpha = 0.15f),
            StatusPending,
            when (currentLanguage) {
                AppLanguage.AMHARIC -> status.amharic
                AppLanguage.ARABIC -> status.arabic
                else -> status.label
            }
        )
        BookingStatus.UNDER_REVIEW -> Triple(
            StatusUnderReview.copy(alpha = 0.15f),
            StatusUnderReview,
            when (currentLanguage) {
                AppLanguage.AMHARIC -> status.amharic
                AppLanguage.ARABIC -> status.arabic
                else -> status.label
            }
        )
        BookingStatus.DOCUMENTS_REQUIRED -> Triple(
            StatusDocsRequired.copy(alpha = 0.15f),
            StatusDocsRequired,
            when (currentLanguage) {
                AppLanguage.AMHARIC -> status.amharic
                AppLanguage.ARABIC -> status.arabic
                else -> status.label
            }
        )
        BookingStatus.REJECTED -> Triple(
            StatusRejected.copy(alpha = 0.15f),
            StatusRejected,
            when (currentLanguage) {
                AppLanguage.AMHARIC -> status.amharic
                AppLanguage.ARABIC -> status.arabic
                else -> status.label
            }
        )
        BookingStatus.COMPLETED -> Triple(
            StatusCompleted.copy(alpha = 0.15f),
            StatusCompleted,
            when (currentLanguage) {
                AppLanguage.AMHARIC -> status.amharic
                AppLanguage.ARABIC -> status.arabic
                else -> status.label
            }
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BookingReceiptDialog(
    booking: Booking,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AL-AWLA TOUR & TRAVEL",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = AlAwlaEmeraldPrimary
                        )
                        Text(
                            text = "Official Booking Confirmation",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated QR Code Frame
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(2.dp, AlAwlaGoldPrimary, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            modifier = Modifier.size(90.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = booking.bookingReference,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ticket Details Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ReceiptRow(label = "Reference No:", value = booking.bookingReference, isBold = true)
                        ReceiptRow(label = "Traveler Name:", value = booking.travelerName)
                        ReceiptRow(label = "Passport No:", value = booking.passportNumber)
                        ReceiptRow(label = "Package:", value = booking.packageName)
                        ReceiptRow(label = "Travel Date:", value = booking.travelDate)
                        ReceiptRow(label = "Passengers:", value = "${booking.numberOfTravelers} Guests")
                        ReceiptRow(
                            label = "Total Amount:",
                            value = "ETB ${"%,d".format(booking.totalAmountEtb)}",
                            isHighlight = true
                        )
                        ReceiptRow(label = "Status:", value = booking.status.label)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlAwlaEmeraldPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Done & Save Reference")
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold || isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) AlAwlaEmeraldPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
