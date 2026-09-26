package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Avatar & Name Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(AlAwlaEmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Phone: ${user.phone} • Passport: ${user.passportNumber}", fontSize = 11.sp, color = AlAwlaGoldDark)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Traveler Profile", fontSize = 12.sp)
                    }
                }
            }
        }

        // Language & Preference Settings
        item {
            Text(text = "App Settings & Preferences", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = AlAwlaEmeraldPrimary)

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Language Switcher Row
                    Text(text = "Interface Language", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.values().forEach { lang ->
                            val isSelected = language == lang
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setLanguage(lang) },
                                label = { Text(lang.nativeName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AlAwlaEmeraldPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Customer Booking Dashboard Shortcut
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.CUSTOMER_DASHBOARD) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Customer Booking Dashboard")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sign In / Switch Account Button
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.AUTH) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (user.isLoggedIn) "Account Settings & Firebase Auth" else "Sign In with Firebase")
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Dark Mode Toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Dark Theme", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(text = "Luxury Dark / Warm Ivory theme", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { viewModel.toggleDarkTheme() }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Admin Role Toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Admin Mode & Operations", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(text = "Switch between Customer & Agency Desk", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = user.isAdmin,
                            onCheckedChange = { enabled ->
                                viewModel.toggleAdminMode(enabled)
                                if (enabled) viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                            }
                        )
                    }

                    if (user.isAdmin) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AlAwlaEmeraldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Agency Desk (Manage Bookings & Prices)")
                        }
                    }
                }
            }
        }

        // Notifications Inbox
        item {
            Text(text = "Notifications & Announcements (${notifications.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = AlAwlaEmeraldPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            if (notifications.isEmpty()) {
                Text(text = "No notifications yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                notifications.take(4).forEach { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = AlAwlaGoldDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = notif.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // About Al-Awla Tour & Travel (Mission & Vision)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AlAwlaEmeraldDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, AlAwlaGoldPrimary, CircleShape),
                            color = Color.White
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.alawla_official_logo),
                                contentDescription = "Al-Awla Official Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column {
                            Text(text = "ABOUT AL-AWLA TOUR & TRAVEL", color = AlAwlaGoldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            Text(text = "alawlatourtravel.com", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Our Mission", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "To provide seamlessly organized, spiritually transformative Umrah pilgrimages and world-class international travel experiences, anchored in genuine Ethiopian hospitality, unmatched reliability, and utmost peace of mind.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Our Global Vision", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "To be East Africa’s preeminent international tour operator and sacred pilgrimage provider, bridging travelers from Addis Ababa to the sacred sanctuaries of Saudi Arabia and the world's most breathtaking capitals.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Licensed by the Ministry of Tourism Ethiopia • ETAA Member • Official Saudi Umrah Partner",
                        color = AlAwlaGoldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, email, phone, passport, nationality ->
                viewModel.updateProfile(name, email, phone, passport, nationality)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun EditProfileDialog(
    user: com.example.data.model.UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }
    var passport by remember { mutableStateOf(user.passportNumber) }
    var nationality by remember { mutableStateOf(user.nationality) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Traveler Profile") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                OutlinedTextField(value = passport, onValueChange = { passport = it }, label = { Text("Passport Number") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                OutlinedTextField(value = nationality, onValueChange = { nationality = it }, label = { Text("Nationality") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, email, phone, passport, nationality) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
