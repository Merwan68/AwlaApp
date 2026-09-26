package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.TravelPackage
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    pkg: TravelPackage,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var travelerName by remember { mutableStateOf(currentUser.name) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var email by remember { mutableStateOf(currentUser.email) }
    var gender by remember { mutableStateOf("Male") }
    var dob by remember { mutableStateOf("1992-06-15") }
    var nationality by remember { mutableStateOf(currentUser.nationality) }
    var passportNumber by remember { mutableStateOf(currentUser.passportNumber) }
    var emergencyName by remember { mutableStateOf("Ahmed Abdusomed") }
    var emergencyPhone by remember { mutableStateOf("+251 911 345 678") }
    var travelDate by remember { mutableStateOf("2026-11-20") }
    var travelersCount by remember { mutableStateOf(1) }
    var notes by remember { mutableStateOf("") }

    // Document attachments state
    var passportUploaded by remember { mutableStateOf(true) }
    var photoUploaded by remember { mutableStateOf(true) }
    var visaDocUploaded by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }

    val totalPrice = pkg.priceEtb * travelersCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book: ${pkg.title}", maxLines = 1, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Total (${travelersCount} Travelers)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "ETB ${"%,d".format(totalPrice)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AlAwlaEmeraldPrimary
                        )
                    }

                    Button(
                        onClick = {
                            if (travelerName.isBlank() || phone.isBlank() || passportNumber.isBlank()) {
                                showError = true
                            } else {
                                viewModel.submitBooking(
                                    pkg = pkg,
                                    travelerName = travelerName,
                                    phone = phone,
                                    email = email,
                                    gender = gender,
                                    dob = dob,
                                    nationality = nationality,
                                    passportNumber = passportNumber,
                                    emergencyName = emergencyName,
                                    emergencyPhone = emergencyPhone,
                                    travelDate = travelDate,
                                    travelersCount = travelersCount,
                                    notes = notes,
                                    hasPassportDoc = passportUploaded,
                                    hasPhotoDoc = photoUploaded,
                                    hasVisaDoc = visaDocUploaded
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("submit_booking_confirm_button")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm Booking", fontWeight = FontWeight.Bold)
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
                .testTag("booking_form_lazy_column"),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Package Summary Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AlAwlaEmeraldDark)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AlAwlaGoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Mosque, contentDescription = null, tint = Color.Black)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(text = pkg.tier.uppercase(), fontSize = 10.sp, color = AlAwlaGoldPrimary, fontWeight = FontWeight.Bold)
                            Text(text = pkg.title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(text = "${pkg.durationDays} Days • ${pkg.destination}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Step 1: Traveler Personal Details
            item {
                SectionTitle("1. Primary Traveler Information")

                OutlinedTextField(
                    value = travelerName,
                    onValueChange = { travelerName = it },
                    label = { Text("Full Name (as in Passport)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Gender selection
                    Column(modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                        Text(text = "Gender", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = gender == "Male",
                                onClick = { gender = "Male" },
                                label = { Text("Male") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AlAwlaEmeraldPrimary, selectedLabelColor = Color.White)
                            )
                            FilterChip(
                                selected = gender == "Female",
                                onClick = { gender = "Female" },
                                label = { Text("Female") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AlAwlaEmeraldPrimary, selectedLabelColor = Color.White)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Date of Birth") },
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nationality,
                        onValueChange = { nationality = it },
                        label = { Text("Nationality") },
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = passportNumber,
                        onValueChange = { passportNumber = it },
                        label = { Text("Passport Number") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Step 2: Emergency Contact & Travel Details
            item {
                SectionTitle("2. Emergency Contact & Trip Schedule")

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = emergencyName,
                        onValueChange = { emergencyName = it },
                        label = { Text("Emergency Contact Name") },
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = emergencyPhone,
                        onValueChange = { emergencyPhone = it },
                        label = { Text("Emergency Phone") },
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = travelDate,
                        onValueChange = { travelDate = it },
                        label = { Text("Departure Date") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Travelers", style = MaterialTheme.typography.labelSmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (travelersCount > 1) travelersCount-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
                            }
                            Text(text = "$travelersCount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            IconButton(
                                onClick = { if (travelersCount < 10) travelersCount++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Step 3: Document Upload System
            item {
                SectionTitle("3. Document Upload System (Cloud Stored)")
                Text(
                    text = "Upload digital scans of your travel credentials for fast visa issuing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                DocumentUploadTile(
                    title = "Passport Scan (Bio page)",
                    subtitle = "Valid for at least 6 months",
                    isUploaded = passportUploaded,
                    onToggle = { passportUploaded = !passportUploaded }
                )

                DocumentUploadTile(
                    title = "White Background Passport Photo",
                    subtitle = "Clear recent headshot",
                    isUploaded = photoUploaded,
                    onToggle = { photoUploaded = !photoUploaded }
                )

                DocumentUploadTile(
                    title = "Previous Visa / Yellow Fever Card",
                    subtitle = "Optional supporting document",
                    isUploaded = visaDocUploaded,
                    onToggle = { visaDocUploaded = !visaDocUploaded }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Step 4: Special Notes
            item {
                SectionTitle("4. Special Notes or Diaspora Sponsorship Details")

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("e.g. Wheelchair assistance, dietary requests, Diaspora sponsor info...") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )

                if (showError) {
                    Text(
                        text = "Please fill in Traveler Name, Phone, and Passport Number.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = AlAwlaEmeraldPrimary,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun DocumentUploadTile(
    title: String,
    subtitle: String,
    isUploaded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUploaded) AlAwlaEmeraldContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isUploaded) AlAwlaEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUploaded) Icons.Default.Check else Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = if (isUploaded) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            TextButton(onClick = onToggle) {
                Text(
                    text = if (isUploaded) "Uploaded ✓" else "Attach File",
                    fontWeight = FontWeight.Bold,
                    color = if (isUploaded) AlAwlaEmeraldPrimary else MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
