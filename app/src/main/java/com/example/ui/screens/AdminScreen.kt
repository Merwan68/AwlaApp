package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.PackageCategory
import com.example.data.model.TravelPackage
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.BookingStatusBadge
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()
    val packages by viewModel.packages.collectAsState()

    var activeTab by remember { mutableStateOf(0) }
    var bookingSearchQuery by remember { mutableStateOf("") }
    var bookingStatusFilter by remember { mutableStateOf("ALL") }

    var selectedBookingForEdit by remember { mutableStateOf<Booking?>(null) }
    var selectedPackageForPriceEdit by remember { mutableStateOf<TravelPackage?>(null) }
    var selectedPackageForFullEdit by remember { mutableStateOf<TravelPackage?>(null) }
    var showCreatePackageDialog by remember { mutableStateOf(false) }

    var packageFilterCategory by remember { mutableStateOf("ALL") }
    var packageSearchQuery by remember { mutableStateOf("") }

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var broadcastSent by remember { mutableStateOf(false) }

    // Filtered bookings
    val filteredBookings = remember(bookings, bookingSearchQuery, bookingStatusFilter) {
        bookings.filter { b ->
            val matchesSearch = bookingSearchQuery.isBlank() ||
                    b.travelerName.contains(bookingSearchQuery, ignoreCase = true) ||
                    b.bookingReference.contains(bookingSearchQuery, ignoreCase = true) ||
                    b.phone.contains(bookingSearchQuery, ignoreCase = true) ||
                    b.passportNumber.contains(bookingSearchQuery, ignoreCase = true) ||
                    b.packageName.contains(bookingSearchQuery, ignoreCase = true)

            val matchesStatus = when (bookingStatusFilter) {
                "ALL" -> true
                "PENDING" -> b.status == BookingStatus.PENDING
                "UNDER_REVIEW" -> b.status == BookingStatus.UNDER_REVIEW
                "APPROVED" -> b.status == BookingStatus.APPROVED
                "COMPLETED" -> b.status == BookingStatus.COMPLETED
                "REJECTED" -> b.status == BookingStatus.REJECTED
                else -> true
            }

            matchesSearch && matchesStatus
        }
    }

    // Filtered packages
    val filteredPackages = remember(packages, packageFilterCategory, packageSearchQuery) {
        packages.filter { p ->
            val matchesCategory = when (packageFilterCategory) {
                "ALL" -> true
                "UMRAH" -> p.category == PackageCategory.UMRAH
                "INTERNATIONAL" -> p.category == PackageCategory.INTERNATIONAL
                else -> true
            }
            val matchesSearch = packageSearchQuery.isBlank() ||
                    p.title.contains(packageSearchQuery, ignoreCase = true) ||
                    p.destination.contains(packageSearchQuery, ignoreCase = true) ||
                    p.hotelMakkah.contains(packageSearchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    // Metrics calculations
    val totalCount = bookings.size
    val approvedCount = bookings.count { it.status == BookingStatus.APPROVED || it.status == BookingStatus.COMPLETED }
    val pendingCount = bookings.count { it.status == BookingStatus.PENDING || it.status == BookingStatus.UNDER_REVIEW }
    val totalRevenue = bookings.sumOf { it.totalAmountEtb }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Al-Awla Operations Desk", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Bookings & Live Price Controller", fontSize = 11.sp, color = AlAwlaGoldDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (viewModel.isFirebaseConnected()) StatusApproved.copy(alpha = 0.2f) else AlAwlaGoldPrimary.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (viewModel.isFirebaseConnected()) StatusApproved else AlAwlaGoldDark)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (viewModel.isFirebaseConnected()) "Firestore Sync Live" else "Room DB Active",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewModel.isFirebaseConnected()) StatusApproved else AlAwlaGoldDark
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleAdminMode(false); viewModel.navigateTo(AppScreen.HOME) }) {
                        Text("Exit Desk", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_screen")
        ) {
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AlAwlaEmeraldPrimary,
                edgePadding = 12.dp
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Overview") },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Bookings ($totalCount)") },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = null) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Packages & Prices (${packages.size})") },
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = { Text("Broadcast") },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null) }
                )
            }

            when (activeTab) {
                0 -> {
                    // Dashboard Metrics
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(text = "Agency Performance Overview", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MetricCard(title = "Total Bookings", value = "$totalCount", color = AlAwlaEmeraldPrimary, modifier = Modifier.weight(1f))
                                MetricCard(title = "Approved Pilgrims", value = "$approvedCount", color = StatusApproved, modifier = Modifier.weight(1f))
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MetricCard(title = "Pending Action", value = "$pendingCount", color = StatusPending, modifier = Modifier.weight(1f))
                                MetricCard(title = "Gross Revenue", value = "ETB ${"%,d".format(totalRevenue)}", color = AlAwlaGoldDark, modifier = Modifier.weight(1f))
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Quick Control Shortcuts", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { activeTab = 1 },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
                                    ) {
                                        Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("View & Manage Customer Bookings ($totalCount)")
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { activeTab = 2 },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = AlAwlaGoldPrimary, contentColor = Color.Black)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Post New Package or Edit Prices (${packages.size})")
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.navigateTo(AppScreen.CHAT_SUPPORT) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Open Live Support Chat Desk")
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Bookings Management
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(text = "Pilgrim & Traveler Bookings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Review passenger documents, update approval statuses, and add agency notes.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Search bar
                            OutlinedTextField(
                                value = bookingSearchQuery,
                                onValueChange = { bookingSearchQuery = it },
                                placeholder = { Text("Search by name, ref, phone, passport...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Status Filter Chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val filters = listOf("ALL", "PENDING", "UNDER_REVIEW", "APPROVED", "COMPLETED", "REJECTED")
                                items(filters) { filter ->
                                    FilterChip(
                                        selected = bookingStatusFilter == filter,
                                        onClick = { bookingStatusFilter = filter },
                                        label = { Text(filter.replace("_", " ")) }
                                    )
                                }
                            }
                        }

                        if (filteredBookings.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.AssignmentLate, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("No bookings match your filter criteria.", fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        } else {
                            items(filteredBookings) { booking ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = booking.bookingReference, fontWeight = FontWeight.ExtraBold, color = AlAwlaEmeraldPrimary)
                                            BookingStatusBadge(status = booking.status, currentLanguage = language)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(text = booking.packageName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "Traveler: ${booking.travelerName} • ${booking.gender}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(text = "Phone: ${booking.phone} • Email: ${booking.email}", style = MaterialTheme.typography.bodySmall)
                                        Text(text = "Passport: ${booking.passportNumber} (${booking.nationality})", fontSize = 12.sp, color = AlAwlaGoldDark)
                                        Text(text = "Date: ${booking.travelDate} • Travelers: ${booking.numberOfTravelers}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "Total Price: ETB ${"%,d".format(booking.totalAmountEtb)}", fontWeight = FontWeight.Bold, color = AlAwlaGoldDark, fontSize = 13.sp)

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Documents Indicator Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            DocStatusPill("Passport Scan", booking.uploadedPassportUrl.isNotBlank())
                                            DocStatusPill("Photo", booking.uploadedPhotoUrl.isNotBlank())
                                            DocStatusPill("Visa Doc", booking.uploadedVisaDocUrl.isNotBlank())
                                        }

                                        if (booking.adminNotes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = AlAwlaEmeraldPrimary.copy(alpha = 0.1f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Agency Note: ${booking.adminNotes}",
                                                    fontSize = 11.sp,
                                                    color = AlAwlaEmeraldDark,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { selectedBookingForEdit = booking },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Update Status / Note", fontSize = 11.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { viewModel.openChatWithBooking(booking.bookingReference) },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Chat", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Packages & Prices Management Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Travel Packages & Live Pricing", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Edit prices, modify hotel details, or post new travel offers live to the app.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Button(
                                    onClick = { showCreatePackageDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Post New", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Search packages
                            OutlinedTextField(
                                value = packageSearchQuery,
                                onValueChange = { packageSearchQuery = it },
                                placeholder = { Text("Search packages by title, hotel, destination...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Category filter chips
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = packageFilterCategory == "ALL",
                                    onClick = { packageFilterCategory = "ALL" },
                                    label = { Text("All (${packages.size})") }
                                )
                                FilterChip(
                                    selected = packageFilterCategory == "UMRAH",
                                    onClick = { packageFilterCategory = "UMRAH" },
                                    label = { Text("Umrah Packages") }
                                )
                                FilterChip(
                                    selected = packageFilterCategory == "INTERNATIONAL",
                                    onClick = { packageFilterCategory = "INTERNATIONAL" },
                                    label = { Text("International Tours") }
                                )
                            }
                        }

                        items(filteredPackages) { pkg ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (pkg.category == PackageCategory.UMRAH) AlAwlaEmeraldPrimary.copy(alpha = 0.15f) else AlAwlaGoldPrimary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (pkg.category == PackageCategory.UMRAH) "UMRAH • ${pkg.tier}" else "INTERNATIONAL TOUR",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (pkg.category == PackageCategory.UMRAH) AlAwlaEmeraldPrimary else AlAwlaGoldDark,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AlAwlaGoldPrimary.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "ETB ${"%,d".format(pkg.priceEtb)}",
                                                fontWeight = FontWeight.ExtraBold,
                                                color = AlAwlaGoldDark,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(text = pkg.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = "${pkg.durationDays} Days / ${pkg.durationNights} Nights • ${pkg.destination}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (pkg.hotelMakkah.isNotBlank()) {
                                        Text(text = "🏨 Makkah: ${pkg.hotelMakkah}", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                    }
                                    if (pkg.hotelMadinah.isNotBlank()) {
                                        Text(text = "🏨 Madinah: ${pkg.hotelMadinah}", fontSize = 11.sp)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Quick Edit Price Button
                                        Button(
                                            onClick = { selectedPackageForPriceEdit = pkg },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = AlAwlaGoldPrimary, contentColor = Color.Black)
                                        ) {
                                            Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Change Price", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Full Edit Package Details Button
                                        OutlinedButton(
                                            onClick = { selectedPackageForFullEdit = pkg },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Edit Details", fontSize = 11.sp)
                                        }

                                        // Delete Package Button
                                        IconButton(
                                            onClick = { viewModel.deletePackage(pkg.id) }
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Notification Broadcaster
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(text = "Agency Push Notification Broadcaster", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Send real-time alerts to all customers regarding flight updates, new packages, and promotions.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = broadcastTitle,
                                onValueChange = { broadcastTitle = it },
                                label = { Text("Announcement Title") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = broadcastMessage,
                                onValueChange = { broadcastMessage = it },
                                label = { Text("Announcement Body") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                                        viewModel.broadcastAnnouncement(broadcastTitle, broadcastMessage)
                                        broadcastSent = true
                                        broadcastTitle = ""
                                        broadcastMessage = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Broadcast Alert to All Users")
                            }

                            if (broadcastSent) {
                                Text(
                                    text = "Broadcast alert dispatched successfully! Check notifications.",
                                    color = StatusApproved,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for changing booking status & adding notes
    selectedBookingForEdit?.let { booking ->
        AdminStatusEditDialog(
            booking = booking,
            onDismiss = { selectedBookingForEdit = null },
            onSave = { newStatus, note ->
                viewModel.updateBookingStatus(booking.id, newStatus, note)
                selectedBookingForEdit = null
            }
        )
    }

    // Dialog for Quick Price Editing
    selectedPackageForPriceEdit?.let { pkg ->
        QuickPriceEditDialog(
            pkg = pkg,
            onDismiss = { selectedPackageForPriceEdit = null },
            onSave = { newPriceEtb ->
                viewModel.updatePackagePrice(pkg.id, newPriceEtb)
                selectedPackageForPriceEdit = null
            }
        )
    }

    // Dialog for Full Package Editing
    selectedPackageForFullEdit?.let { pkg ->
        FullPackageEditDialog(
            pkg = pkg,
            onDismiss = { selectedPackageForFullEdit = null },
            onSave = { updatedPkg ->
                viewModel.updatePackage(updatedPkg)
                selectedPackageForFullEdit = null
            }
        )
    }

    // Dialog for Creating New Package
    if (showCreatePackageDialog) {
        CreatePackageDialog(
            onDismiss = { showCreatePackageDialog = false },
            onSave = { newPkg ->
                viewModel.createPackage(newPkg)
                showCreatePackageDialog = false
            }
        )
    }
}

@Composable
private fun DocStatusPill(label: String, uploaded: Boolean) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (uploaded) StatusApproved.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = if (uploaded) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (uploaded) StatusApproved else Color.Gray,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (uploaded) StatusApproved else Color.Gray
            )
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun AdminStatusEditDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSave: (BookingStatus, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(booking.status) }
    var note by remember { mutableStateOf(booking.adminNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Booking Status & Note") },
        text = {
            Column {
                Text(text = "Reference: ${booking.bookingReference}", fontWeight = FontWeight.Bold, color = AlAwlaEmeraldPrimary)
                Text(text = "Traveler: ${booking.travelerName}", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Set Approval Status:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                BookingStatus.values().forEach { status ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = status.label, fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Agency Note to Pilgrim") },
                    placeholder = { Text("e.g. Visa approved. Passport ready for pickup at Bethel office.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedStatus, note) },
                colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
            ) {
                Text("Save Status")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun QuickPriceEditDialog(
    pkg: TravelPackage,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var priceText by remember { mutableStateOf(pkg.priceEtb.toString()) }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Package Price (ETB)") },
        text = {
            Column {
                Text(text = pkg.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(text = "Current: ETB ${"%,d".format(pkg.priceEtb)}", fontSize = 12.sp, color = AlAwlaGoldDark)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        priceText = it
                        errorText = ""
                    },
                    label = { Text("New Price in ETB") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    isError = errorText.isNotBlank(),
                    supportingText = if (errorText.isNotBlank()) { { Text(errorText, color = MaterialTheme.colorScheme.error) } } else null
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Saving this price will immediately update it on the Home screen, packages catalog, and checkout forms for all users.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = priceText.trim().toLongOrNull()
                    if (parsed != null && parsed > 0) {
                        onSave(parsed)
                    } else {
                        errorText = "Please enter a valid positive number"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
            ) {
                Text("Save Price")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun FullPackageEditDialog(
    pkg: TravelPackage,
    onDismiss: () -> Unit,
    onSave: (TravelPackage) -> Unit
) {
    var title by remember { mutableStateOf(pkg.title) }
    var priceText by remember { mutableStateOf(pkg.priceEtb.toString()) }
    var durationDaysText by remember { mutableStateOf(pkg.durationDays.toString()) }
    var hotelMakkah by remember { mutableStateOf(pkg.hotelMakkah) }
    var hotelMadinah by remember { mutableStateOf(pkg.hotelMadinah) }
    var description by remember { mutableStateOf(pkg.description) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(text = "Edit Package Details", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                item {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Package Title") }, modifier = Modifier.fillMaxWidth())
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price (ETB)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = durationDaysText,
                            onValueChange = { durationDaysText = it },
                            label = { Text("Days") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (pkg.category == PackageCategory.UMRAH) {
                    item {
                        OutlinedTextField(value = hotelMakkah, onValueChange = { hotelMakkah = it }, label = { Text("Makkah Hotel") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = hotelMadinah, onValueChange = { hotelMadinah = it }, label = { Text("Madinah Hotel") }, modifier = Modifier.fillMaxWidth())
                    }
                }

                item {
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val price = priceText.toLongOrNull() ?: pkg.priceEtb
                                val days = durationDaysText.toIntOrNull() ?: pkg.durationDays
                                onSave(
                                    pkg.copy(
                                        title = title,
                                        priceEtb = price,
                                        durationDays = days,
                                        hotelMakkah = hotelMakkah,
                                        hotelMadinah = hotelMadinah,
                                        description = description
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePackageDialog(
    onDismiss: () -> Unit,
    onSave: (TravelPackage) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(PackageCategory.UMRAH) }
    var tier by remember { mutableStateOf("VIP") }
    var priceText by remember { mutableStateOf("") }
    var durationDaysText by remember { mutableStateOf("10") }
    var destination by remember { mutableStateOf("Makkah & Madinah") }
    var hotelMakkah by remember { mutableStateOf("") }
    var hotelMadinah by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(text = "Post New Travel Package", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(text = "This will immediately publish to all app users.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = category == PackageCategory.UMRAH,
                            onClick = {
                                category = PackageCategory.UMRAH
                                destination = "Makkah & Madinah"
                            },
                            label = { Text("Umrah") }
                        )
                        FilterChip(
                            selected = category == PackageCategory.INTERNATIONAL,
                            onClick = {
                                category = PackageCategory.INTERNATIONAL
                                destination = "Dubai, UAE"
                            },
                            label = { Text("International") }
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Package Title *") },
                        placeholder = { Text(if (category == PackageCategory.UMRAH) "e.g. 10 Nights Ramadan Special Umrah" else "e.g. Dubai Luxury City & Desert Tour") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price (ETB) *") },
                            placeholder = { Text("e.g. 245000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = durationDaysText,
                            onValueChange = { durationDaysText = it },
                            label = { Text("Days") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (category == PackageCategory.UMRAH) {
                    item {
                        OutlinedTextField(
                            value = hotelMakkah,
                            onValueChange = { hotelMakkah = it },
                            label = { Text("Makkah Hotel") },
                            placeholder = { Text("e.g. Swissôtel Makkah (Clock Tower)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = hotelMadinah,
                            onValueChange = { hotelMadinah = it },
                            label = { Text("Madinah Hotel") },
                            placeholder = { Text("e.g. Anwar Al Madinah Mövenpick") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Package Description") },
                        placeholder = { Text("Complete package including roundtrip flights from Addis Ababa, 5-star hotel accommodations, full VIP transfers, and guided ziyarat.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank() && priceText.isNotBlank()) {
                                    val price = priceText.trim().toLongOrNull() ?: 200000L
                                    val days = durationDaysText.toIntOrNull() ?: 10
                                    val newPackage = TravelPackage(
                                        id = "pkg_${UUID.randomUUID().toString().take(8)}",
                                        title = title,
                                        titleAm = title,
                                        titleAr = title,
                                        category = category,
                                        tier = tier,
                                        priceEtb = price,
                                        priceUsd = (price / 125).toInt(),
                                        durationDays = days,
                                        durationNights = (days - 1).coerceAtLeast(1),
                                        hotelMakkah = hotelMakkah,
                                        hotelMadinah = hotelMadinah,
                                        hotelGeneral = if (hotelMakkah.isNotBlank()) "$hotelMakkah / $hotelMadinah" else "Luxury 5-Star Hotel",
                                        occupancy = "Quad / Triple / Double",
                                        inclusions = listOf("Roundtrip Flights (Addis Ababa - Jeddah/Madinah)", "Hotel Accommodations", "Umrah Visa Processing", "Luxury VIP Bus Transfers", "Experienced Ethiopian Scholar Guide"),
                                        exclusions = listOf("Personal shopping & laundry", "Room service outside breakfast"),
                                        description = description.ifBlank { "Exclusive premium travel package provided by Al-Awla Tour & Travel Services." },
                                        descriptionAm = description.ifBlank { "ልዩ የጉዞ ጥቅል በአል-አውላ ቱር እና ትራቭል የቀረበ።" },
                                        descriptionAr = description.ifBlank { "برنامج سفر فاخر من شركة الأولى للسياحة والسفر." },
                                        destination = destination,
                                        imageUrl = "hero_banner",
                                        isFeatured = true
                                    )
                                    onSave(newPackage)
                                }
                            },
                            enabled = title.isNotBlank() && priceText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
                        ) {
                            Text("Publish Package")
                        }
                    }
                }
            }
        }
    }
}
