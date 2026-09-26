package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.BookingReceiptDialog
import com.example.ui.theme.*

@Composable
fun BookingSuccessScreen(
    booking: Booking,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var showReceipt by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("booking_success_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Check Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AlAwlaEmeraldContainer)
                .border(2.dp, AlAwlaEmeraldPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = AlAwlaEmeraldPrimary,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Booking Request Submitted!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Thank you for choosing Al-Awla Tour & Travel. Your reservation is being reviewed by our pilgrimage and flight operations team.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Reference Code Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "BOOKING REFERENCE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = booking.bookingReference,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = AlAwlaEmeraldPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(text = booking.packageName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(text = "Date: ${booking.travelDate} • ${booking.numberOfTravelers} Guests", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Button(
            onClick = { showReceipt = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AlAwlaGoldPrimary, contentColor = Color.Black)
        ) {
            Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Booking Voucher & QR Code", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { viewModel.openChatWithBooking(booking.bookingReference) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = AlAwlaEmeraldPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chat with Agent about this Booking", color = AlAwlaEmeraldPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = { viewModel.navigateTo(AppScreen.HOME) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (showReceipt) {
        BookingReceiptDialog(booking = booking, onDismiss = { showReceipt = false })
    }
}
