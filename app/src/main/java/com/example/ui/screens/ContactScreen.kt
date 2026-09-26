package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun ContactScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("contact_screen"),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Official Brand Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AlAwlaEmeraldDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, AlAwlaGoldPrimary, CircleShape),
                            color = Color.White,
                            shadowElevation = 3.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.alawla_official_logo),
                                contentDescription = "Al-Awla Official Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Al-Awla Tour & Travel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "alawlatourtravel.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = AlAwlaGoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Official travel and pilgrimage operations headquartered in Addis Ababa, Ethiopia, with on-ground executive logistics in Makkah, Madinah, and Jeddah, Saudi Arabia.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.openChatWithBooking(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = AlAwlaGoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Live Support Chat", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Direct Contact Methods (Phone, WhatsApp, Telegram, Email)
        item {
            Text(
                text = "Direct Communications",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AlAwlaEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            ContactActionCard(
                icon = Icons.Default.Phone,
                iconColor = AlAwlaEmeraldPrimary,
                title = "Official Phone Hotlines",
                subtitle = "+251 911 955 8887 • 0943989999",
                actionLabel = "Call Now",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+2519119558887"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ContactActionCard(
                icon = Icons.Default.ChatBubble,
                iconColor = Color(0xFF25D366),
                title = "Official WhatsApp Desk",
                subtitle = "+251 911 955 8887 • Instant inquiry and booking support",
                actionLabel = "Chat on WhatsApp",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/2519119558887"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ContactActionCard(
                icon = Icons.Default.PlayCircle,
                iconColor = Color(0xFF000000),
                title = "Official TikTok Channel",
                subtitle = "@awla.tour.travel • Videos, pilgrim reviews & live updates",
                actionLabel = "Open TikTok",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@awla.tour.travel?_r=1&_t=ZS-99j3r2IrkpB"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ContactActionCard(
                icon = Icons.Default.Email,
                iconColor = Color(0xFFEA4335),
                title = "Corporate Inquiries & Booking",
                subtitle = "info@alawlatourtravel.com / booking@alawlatourtravel.com",
                actionLabel = "Email Us",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:info@alawlatourtravel.com"))
                    context.startActivity(intent)
                }
            )
        }

        // Office Locations & Google Maps
        item {
            Text(
                text = "Headquarters & Branch Offices",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AlAwlaEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = AlAwlaEmeraldPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Addis Ababa Main Office (Bethel)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "Bethel Future Mall, 3rd Floor, Office No. 305, Addis Ababa, Ethiopia",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AlAwlaGoldDark
                            )
                            Text(
                                text = "ቤተል ፊዩቸር ሞል 3ኛ ፎቅ ቢሮ ቁጥር 305፣ አዲስ አበባ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Registration Hotline: 0943989999 • Hours: Mon - Sat 8:30 AM - 6:30 PM",
                                fontSize = 11.sp,
                                color = AlAwlaEmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Default.Apartment, contentDescription = null, tint = AlAwlaGoldDark, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Kingdom of Saudi Arabia Liaison", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "King Abdulaziz Rd, Jeddah & Clock Royal Center, Makkah",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Operations: 24/7 on-ground executive pilgrimage logistics",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:9.006,38.705?q=Bethel+Future+Mall+Addis+Ababa"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Office on Google Maps")
                    }
                }
            }
        }

        // Authentic Website Photography Gallery Card
        item {
            Text(
                text = "Official Gallery (alawlatourtravel.com)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AlAwlaEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(120.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.website_mekka3),
                                contentDescription = "Makkah Al-Mukarramah",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(120.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.website_mekka2),
                                contentDescription = "Holy Kaaba Sanctuary",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(120.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.website_ustaz),
                                contentDescription = "Ustaz Abdul Fattah Umrah",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Authentic pilgrimage captures from Makkah Al-Mukarramah and executive tour operations.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Social Media Community Links
        item {
            Text(
                text = "Connect on Social Media",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AlAwlaEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SocialIconItem(icon = Icons.Default.Facebook, name = "Facebook", onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/alawlatravel"))
                    context.startActivity(intent)
                })
                SocialIconItem(icon = Icons.Default.CameraAlt, name = "Instagram", onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/alawlatravel"))
                    context.startActivity(intent)
                })
                SocialIconItem(icon = Icons.Default.VideoLibrary, name = "YouTube", onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@alawlatours"))
                    context.startActivity(intent)
                })
                SocialIconItem(icon = Icons.Default.Public, name = "Website", onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://alawlatourtravel.com"))
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
private fun ContactActionCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    actionLabel: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
            }

            Text(
                text = actionLabel,
                color = AlAwlaEmeraldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun SocialIconItem(icon: ImageVector, name: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = name, tint = AlAwlaEmeraldPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = name, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
