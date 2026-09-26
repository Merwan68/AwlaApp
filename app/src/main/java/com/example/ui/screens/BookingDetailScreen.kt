package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.MainViewModel
import com.example.ui.components.BookingReceiptDialog
import com.example.ui.components.BookingStatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    booking: Booking,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    var showReceipt by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking: ${booking.bookingReference}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showReceipt = true }) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = "View Voucher", tint = AlAwlaEmeraldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showReceipt = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Voucher")
                    }

                    Button(
                        onClick = { viewModel.openChatWithBooking(booking.bookingReference) },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chat with Support")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("booking_detail_screen"),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = booking.packageName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            BookingStatusBadge(status = booking.status, currentLanguage = language)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Travel Date: ${booking.travelDate} • ${booking.numberOfTravelers} Travelers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Amount: ETB ${"%,d".format(booking.totalAmountEtb)}", fontWeight = FontWeight.Bold, color = AlAwlaGoldDark, fontSize = 14.sp)
                    }
                }
            }

            // Timeline status tracker
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Booking Progress", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AlAwlaEmeraldPrimary)
                        Spacer(modifier = Modifier.height(14.dp))

                        TimelineStep(title = "Booking Submitted", desc = "Request registered in agency system", isDone = true)
                        TimelineStep(title = "Under Review", desc = "Staff verifying documents & seats", isDone = booking.status != BookingStatus.PENDING)
                        TimelineStep(title = "Documents Verified & Visa Issued", desc = "Electronic Saudi visa issuance", isDone = booking.status == BookingStatus.APPROVED || booking.status == BookingStatus.COMPLETED)
                        TimelineStep(title = "Flight & Hotel Confirmation", desc = "Final voucher & tickets generated", isDone = booking.status == BookingStatus.APPROVED || booking.status == BookingStatus.COMPLETED, isLast = true)
                    }
                }
            }

            // Traveler details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Traveler Information", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        DetailLine("Full Name", booking.travelerName)
                        DetailLine("Phone", booking.phone)
                        DetailLine("Email", booking.email)
                        DetailLine("Gender / DOB", "${booking.gender} • ${booking.dateOfBirth}")
                        DetailLine("Passport No", booking.passportNumber)
                        DetailLine("Emergency Contact", "${booking.emergencyContactName} (${booking.emergencyContactPhone})")
                    }
                }
            }

            // Uploaded Documents
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Document Repository", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        DocStatusLine("Passport Digital Copy", isAttached = booking.uploadedPassportUrl.isNotBlank())
                        DocStatusLine("Passport Photo (White BG)", isAttached = booking.uploadedPhotoUrl.isNotBlank())
                        DocStatusLine("Yellow Fever Card / Visa", isAttached = booking.uploadedVisaDocUrl.isNotBlank())
                    }
                }
            }

            // Agency Notes
            if (booking.adminNotes.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AlAwlaEmeraldContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Al-Awla Agency Update", fontWeight = FontWeight.Bold, color = AlAwlaEmeraldPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = booking.adminNotes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }

    if (showReceipt) {
        BookingReceiptDialog(booking = booking, onDismiss = { showReceipt = false })
    }
}

@Composable
private fun TimelineStep(title: String, desc: String, isDone: Boolean, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isDone) AlAwlaEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp)
                        .background(if (isDone) AlAwlaEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 10.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DocStatusLine(label: String, isAttached: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isAttached) AlAwlaEmeraldContainer else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = if (isAttached) "Verified ✓" else "Not Attached",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAttached) AlAwlaOnEmeraldContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
