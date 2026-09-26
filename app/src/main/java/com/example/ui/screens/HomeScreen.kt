package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.PackageCategory
import com.example.data.model.Review
import com.example.data.model.TravelPackage
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val packages by viewModel.packages.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    val umrahPackages = remember(packages) {
        packages.filter { it.category == PackageCategory.UMRAH }
    }
    val internationalTours = remember(packages) {
        packages.filter { it.category == PackageCategory.INTERNATIONAL }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Luxury Hero Banner
        item {
            HeroSection(
                language = language,
                onExploreUmrah = { viewModel.navigateTo(AppScreen.UMRAH) },
                onExploreTours = { viewModel.navigateTo(AppScreen.TOURS) }
            )
        }

        // Official 6th Round Umrah Announcement from alawlatourtravel.com
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { viewModel.navigateTo(AppScreen.UMRAH) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AlAwlaEmeraldDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AlAwlaGoldPrimary, RoundedCornerShape(12.dp)),
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.website_ustaz),
                            contentDescription = "Ustaz Abdul Fattah",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚠️", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "6ኛ ዙር የኡምራ ፓኬጅ ምዝገባ ላይ ነን!",
                                color = AlAwlaGoldPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "ከጥቅምት 18 - 28 ከተወዳጁ ኡስታዝ አብዱልፈታህ ጋር",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "ቤተል ፊዩቸር ሞል 3ኛ ፎቅ ቢሮ ቁጥር 305 • 0943989999",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AlAwlaGoldPrimary
                    )
                }
            }
        }

        // Quick Action Shortcuts Grid
        item {
            QuickActionsBar(
                language = language,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        // Featured Umrah Packages Section (Pricing & structure inspired by zadtravelagency.com & alawla)
        item {
            SectionHeader(
                title = AppStrings.get("featured_umrah", language),
                subtitle = "10 Nights • Makkah & Madinah • Direct Flights & Visa Included",
                actionText = AppStrings.get("view_details", language),
                onActionClick = { viewModel.navigateTo(AppScreen.UMRAH) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(umrahPackages.take(4)) { pkg ->
                    UmrahPackageCard(
                        pkg = pkg,
                        language = language,
                        onClick = { viewModel.viewPackageDetail(pkg) },
                        onBookNow = { viewModel.startBooking(pkg) }
                    )
                }
            }
        }

        // Global Destinations Carousel (Expanding Internationally: Dubai, Turkey, Malaysia, etc.)
        item {
            SectionHeader(
                title = AppStrings.get("global_destinations", language),
                subtitle = "Explore the Wonders of the World with Al-Awla Premium Logistics",
                actionText = "All Tours",
                onActionClick = { viewModel.navigateTo(AppScreen.TOURS) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(internationalTours.take(5)) { tour ->
                    InternationalTourCard(
                        tour = tour,
                        language = language,
                        onClick = { viewModel.viewPackageDetail(tour) }
                    )
                }
            }
        }

        // Why Choose Al-Awla Tour & Travel (Ethiopia & Global)
        item {
            WhyChooseAlAwlaSection(language = language)
        }

        // Diaspora Sponsorship Banner
        item {
            DiasporaSponsorshipCard(
                language = language,
                onInquire = { viewModel.openChatWithBooking(null) }
            )
        }

        // Pilgrim & Traveler Testimonials
        item {
            SectionHeader(
                title = "Customer Experiences",
                subtitle = "Genuine feedback from honored pilgrims & world travelers",
                actionText = "See All",
                onActionClick = { /* no-op */ }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }

        // Travel Tips & FAQs Accordion
        item {
            TravelFaqSection()
        }
    }
}

@Composable
private fun HeroSection(
    language: AppLanguage,
    onExploreUmrah: () -> Unit,
    onExploreTours: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.hero_banner),
                contentDescription = "Al-Awla Travel Hero Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, AlAwlaGoldPrimary, CircleShape),
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.alawla_official_logo),
                            contentDescription = "Al-Awla Official Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AlAwlaGoldPrimary.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "AL-AWLA TOUR & TRAVEL SERVICES",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = AppStrings.get("tagline", language),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = "Premier Umrah Pilgrimages & International Luxury Tours",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onExploreUmrah,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlAwlaGoldPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Mosque, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Umrah Packages", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onExploreTours,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(Color.White, AlAwlaGoldLight))
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Global Tours", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsBar(
    language: AppLanguage,
    onNavigate: (AppScreen) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionButton(
            icon = Icons.Default.Mosque,
            title = "Umrah",
            color = AlAwlaEmeraldPrimary,
            onClick = { onNavigate(AppScreen.UMRAH) }
        )
        QuickActionButton(
            icon = Icons.Default.FlightTakeoff,
            title = "Tours",
            color = Color(0xFF1E88E5),
            onClick = { onNavigate(AppScreen.TOURS) }
        )
        QuickActionButton(
            icon = Icons.Default.ChatBubble,
            title = "Live Chat",
            color = AlAwlaGoldDark,
            onClick = { onNavigate(AppScreen.CHAT_SUPPORT) }
        )
        QuickActionButton(
            icon = Icons.Default.ConfirmationNumber,
            title = "Bookings",
            color = Color(0xFF8E24AA),
            onClick = { onNavigate(AppScreen.CUSTOMER_DASHBOARD) }
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.12f))
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = actionText,
            color = AlAwlaEmeraldPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier
                .clickable(onClick = onActionClick)
                .padding(4.dp)
        )
    }
}

@Composable
fun UmrahPackageCard(
    pkg: TravelPackage,
    language: AppLanguage,
    onClick: () -> Unit,
    onBookNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
            ) {
                val imageRes = when (pkg.imageUrl) {
                    "website_ustaz" -> R.drawable.website_ustaz
                    "website_mekka3" -> R.drawable.website_mekka3
                    "website_mekka2" -> R.drawable.website_mekka2
                    else -> R.drawable.website_mekka3
                }

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = pkg.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Tier badge
                Surface(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = AlAwlaEmeraldPrimary
                ) {
                    Text(
                        text = pkg.tier.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Price badge
                Surface(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = "ETB ${"%,d".format(pkg.priceEtb)}",
                        color = AlAwlaGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = when (language) {
                        AppLanguage.AMHARIC -> pkg.titleAm
                        AppLanguage.ARABIC -> pkg.titleAr
                        else -> pkg.title
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Hotel, contentDescription = null, tint = AlAwlaGoldDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pkg.hotelMakkah,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pkg.occupancy,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AlAwlaGoldPrimary, modifier = Modifier.size(13.dp))
                        Text(text = "${pkg.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onBookNow,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlAwlaEmeraldPrimary
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = AppStrings.get("quick_book", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InternationalTourCard(
    tour: TravelPackage,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_banner),
                    contentDescription = tour.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (tour.isComingSoon) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFC0392B)
                    ) {
                        Text(
                            text = AppStrings.get("coming_soon", language),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.BottomEnd),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "ETB ${"%,d".format(tour.priceEtb)}",
                            color = AlAwlaGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = tour.destination,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${tour.durationDays} Days • Guided Excursions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun WhyChooseAlAwlaSection(language: AppLanguage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = AppStrings.get("why_alawla", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            FeatureItem(
                icon = Icons.Default.Verified,
                title = "Ministry & IATA Accredited Operator",
                desc = "Fully licensed travel agency in Addis Ababa with direct Saudi Umrah operator contracts."
            )
            FeatureItem(
                icon = Icons.Default.CardTravel,
                title = "Diaspora Family Sponsorship & Guarantee",
                desc = "Family members overseas can fund and sponsor Umrah for parents in Ethiopia safely."
            )
            FeatureItem(
                icon = Icons.Default.DirectionsBus,
                title = "VIP Ground Fleet & Haramain Train",
                desc = "Brand new air-conditioned Mercedes buses and high-speed bullet train tickets included."
            )
            FeatureItem(
                icon = Icons.Default.SupportAgent,
                title = "24/7 Scholar Guides & Concierge Desk",
                desc = "Dedicated Mutawwif religious scholars and on-site hotel coordinators in Makkah & Madinah."
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AlAwlaEmeraldPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AlAwlaEmeraldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun DiasporaSponsorshipCard(
    language: AppLanguage,
    onInquire: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = AlAwlaEmeraldDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FamilyRestroom,
                    contentDescription = null,
                    tint = AlAwlaGoldPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = AppStrings.get("diaspora_sponsorship", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Living abroad in the USA, Canada, Europe, or the Gulf? Send your beloved family members from Ethiopia on a holy Umrah journey with full financial and document sponsorship processed through Al-Awla.",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onInquire,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AlAwlaGoldPrimary,
                    contentColor = Color.Black
                )
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inquire About Diaspora Sponsorship", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = review.userName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = review.userCountry,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                Row {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AlAwlaGoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${review.comment}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.packageName,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AlAwlaEmeraldPrimary
            )
        }
    }
}

@Composable
private fun TravelFaqSection() {
    var expandedItem by remember { mutableStateOf<Int?>(null) }

    val faqs = listOf(
        Pair("What documents are required for an Umrah Visa?", "An Ethiopian passport with at least 6 months validity from the date of travel, a clear passport-sized photo on a white background, and yellow fever vaccination card."),
        Pair("Can I pay in US Dollars, Euro, or ETB?", "Yes! Local travelers can pay in Ethiopian Birr (ETB) through Telebirr, CBE, or Awash Bank. Diaspora sponsors can make international payments via bank transfer or credit card."),
        Pair("What is the distance of your hotels from the Haram?", "Standard hotels (e.g., Safeer Al Misk) are 800m-1.2km with continuous 24/7 air-conditioned shuttle buses. VVIP hotels (Sheraton Jabal Al Kaaba & Pullman Zamzam) are directly overlooking or steps from the Haram courtyards.")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            faqs.forEachIndexed { index, (question, answer) ->
                val isExpanded = expandedItem == index
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedItem = if (isExpanded) null else index }
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = AlAwlaEmeraldPrimary
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Text(
                            text = answer,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
                if (index < faqs.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}
