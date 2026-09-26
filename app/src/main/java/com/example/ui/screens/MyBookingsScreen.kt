package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.Booking
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.BookingReceiptDialog
import com.example.ui.components.BookingStatusBadge
import com.example.ui.theme.*

@Composable
fun MyBookingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()
    var receiptBooking by remember { mutableStateOf<Booking?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(bookings, searchQuery) {
        bookings.filter {
            searchQuery.isBlank() ||
            it.bookingReference.contains(searchQuery, ignoreCase = true) ||
            it.packageName.contains(searchQuery, ignoreCase = true) ||
            it.travelerName.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_bookings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "My Travel Bookings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Track real-time pilgrimage and tour status, download vouchers, and contact support.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by reference (e.g. ALW-2026)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        if (filtered.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.EventBusy, contentDescription = null, tint = AlAwlaGoldDark, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No Bookings Found", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Explore our Umrah packages and international tours to book your next journey.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.UMRAH) },
                            colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
                        ) {
                            Text("Browse Umrah Packages")
                        }
                    }
                }
            }
        } else {
            items(filtered) { booking ->
                BookingItemCard(
                    booking = booking,
                    language = language,
                    onViewDetails = { viewModel.viewBookingDetail(booking) },
                    onViewReceipt = { receiptBooking = booking },
                    onChatSupport = { viewModel.openChatWithBooking(booking.bookingReference) }
                )
            }
        }
    }

    receiptBooking?.let { b ->
        BookingReceiptDialog(booking = b, onDismiss = { receiptBooking = null })
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    language: AppLanguage,
    onViewDetails: () -> Unit,
    onViewReceipt: () -> Unit,
    onChatSupport: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetails),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.bookingReference,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = AlAwlaEmeraldPrimary
                    )
                    Text(
                        text = "Travel Date: ${booking.travelDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                BookingStatusBadge(status = booking.status, currentLanguage = language)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = booking.packageName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "Primary Traveler: ${booking.travelerName} • ${booking.numberOfTravelers} Guests",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            Text(
                text = "Total: ETB ${"%,d".format(booking.totalAmountEtb)}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AlAwlaGoldDark,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (booking.adminNotes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Agency Update: ${booking.adminNotes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AlAwlaEmeraldPrimary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewReceipt,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Voucher", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onChatSupport,
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat Support", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
