package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.BookingStatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val isCloudConnected = viewModel.isFirebaseConnected()

    // Filter bookings relevant to current user
    val userBookings = remember(allBookings, user) {
        val matches = allBookings.filter {
            it.email.equals(user.email, ignoreCase = true) ||
                    it.travelerName.contains(user.name, ignoreCase = true) ||
                    user.email.isBlank() // If guest, show all created
        }
        if (matches.isNotEmpty()) matches else allBookings
    }

    val primaryBooking = userBookings.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Customer Booking Dashboard",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Live Visa Tracking & Itinerary",
                            fontSize = 11.sp,
                            color = AlAwlaGoldDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Home")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.CHAT_SUPPORT) }) {
                        Icon(imageVector = Icons.Default.ChatBubble, contentDescription = "Support Chat", tint = AlAwlaEmeraldPrimary)
                    }
                    TextButton(onClick = { viewModel.logout() }) {
                        Text("Sign Out", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("customer_dashboard_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Customer Header Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AlAwlaEmeraldDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, AlAwlaGoldPrimary, CircleShape),
                                color = Color.White
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.website_awlalogo),
                                    contentDescription = "User Avatar",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ahlan wa Sahlan,",
                                    color = AlAwlaGoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = user.name.ifBlank { "Valued Traveler" },
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = user.email.ifBlank { "Registered Pilgrim" },
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AlAwlaGoldPrimary.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (isCloudConnected) StatusApproved else AlAwlaGoldPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isCloudConnected) "Firestore Live" else "Room Synced",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlAwlaGoldPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Passport: ${user.passportNumber} • ${user.nationality}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )

                            Text(
                                text = "Phone: ${user.phone}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Spotlight on Active Booking / Live Pilgrimage Tracker
            if (primaryBooking != null) {
                item {
                    Text(
                        text = "Active Pilgrimage Status Tracker",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = AlAwlaEmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
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
                                        text = primaryBooking.bookingReference,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = AlAwlaEmeraldPrimary
                                    )
                                    Text(
                                        text = "Travel Date: ${primaryBooking.travelDate}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                BookingStatusBadge(status = primaryBooking.status, currentLanguage = language)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = primaryBooking.packageName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // 5-Stage Visual Status Progression Tracker
                            PilgrimageStatusStepper(status = primaryBooking.status)

                            Spacer(modifier = Modifier.height(14.dp))

                            // Official Agency Notes from Bethel Future Mall Office
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (primaryBooking.adminNotes.isNotBlank()) AlAwlaEmeraldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AlAwlaEmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Official Agency Update from Bethel Office:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = AlAwlaEmeraldPrimary
                                        )
                                        Text(
                                            text = if (primaryBooking.adminNotes.isNotBlank()) {
                                                primaryBooking.adminNotes
                                            } else {
                                                "Your booking is active. Documents are being reviewed by the Al-Awla visa processing division at Bethel Future Mall Office 305."
                                            },
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Document Verification Badges
                            Text(
                                text = "Document Verification Checklist:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DocumentCheckBadge("Passport Scan", primaryBooking.uploadedPassportUrl.isNotBlank())
                                DocumentCheckBadge("Photo (White BG)", primaryBooking.uploadedPhotoUrl.isNotBlank())
                                DocumentCheckBadge("Visa Certificate", primaryBooking.uploadedVisaDocUrl.isNotBlank() || primaryBooking.status == BookingStatus.APPROVED)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.selectBooking(primaryBooking)
                                        viewModel.navigateTo(AppScreen.BOOKING_DETAIL)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary)
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View E-Ticket", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.openChatWithBooking(primaryBooking.bookingReference)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chat Officer", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Contact Helpline Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0943989999"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp), tint = AlAwlaEmeraldPrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("0943989999", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        val url = "https://wa.me/2519119558887?text=Hello%20Al-Awla,%20inquiring%20about%20booking%20${primaryBooking.bookingReference}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                ) {
                                    Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Luggage,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = AlAwlaEmeraldPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Active Bookings Found",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "You haven't placed an Umrah or international tour booking yet.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.UMRAH) },
                                colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Explore Umrah Packages")
                            }
                        }
                    }
                }
            }

            // All Bookings History Section
            if (userBookings.size > 1) {
                item {
                    Text(
                        text = "Booking History (${userBookings.size})",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(userBookings.drop(1)) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectBooking(booking)
                                viewModel.navigateTo(AppScreen.BOOKING_DETAIL)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = booking.bookingReference,
                                    fontWeight = FontWeight.Bold,
                                    color = AlAwlaEmeraldPrimary
                                )
                                BookingStatusBadge(status = booking.status, currentLanguage = language)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = booking.packageName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                text = "Date: ${booking.travelDate} • ETB ${"%,d".format(booking.totalAmountEtb)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PilgrimageStatusStepper(status: BookingStatus) {
    val steps = listOf(
        "Submitted",
        "Verification",
        "Visa & Flight",
        "Vouchers",
        "Completed"
    )

    val currentStepIndex = when (status) {
        BookingStatus.PENDING -> 0
        BookingStatus.UNDER_REVIEW -> 1
        BookingStatus.DOCUMENTS_REQUIRED -> 1
        BookingStatus.APPROVED -> 3
        BookingStatus.COMPLETED -> 4
        BookingStatus.REJECTED -> 0
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, title ->
                val isCompleted = index <= currentStepIndex
                val isCurrent = index == currentStepIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> AlAwlaEmeraldPrimary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = title,
                        fontSize = 9.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCompleted) AlAwlaEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentCheckBadge(label: String, isDone: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isDone) StatusApproved.copy(alpha = 0.15f) else StatusPending.copy(alpha = 0.15f),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                contentDescription = null,
                tint = if (isDone) StatusApproved else StatusPending,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDone) StatusApproved else StatusPending
            )
        }
    }
}
