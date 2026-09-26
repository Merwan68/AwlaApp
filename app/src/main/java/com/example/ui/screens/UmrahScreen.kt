package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.data.model.PackageCategory
import com.example.data.model.TravelPackage
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun UmrahScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val packages by viewModel.packages.collectAsState()
    var selectedTier by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val tiers = listOf("All", "Standard", "VIP", "VVIP", "VVIP Premium", "Ramadan Special")

    val filtered = remember(packages, selectedTier, searchQuery) {
        packages.filter { it.category == PackageCategory.UMRAH }
            .filter { selectedTier == "All" || it.tier.equals(selectedTier, ignoreCase = true) }
            .filter {
                searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.hotelMakkah.contains(searchQuery, ignoreCase = true) ||
                it.hotelMadinah.contains(searchQuery, ignoreCase = true)
            }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("umrah_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header info banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AlAwlaEmeraldDark
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "AL-AWLA SACRED UMRAH PACKAGES",
                        color = AlAwlaGoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complete Pilgrimage Packages (10 Nights)",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Includes return Ethiopian Airlines flights, Saudi Umrah visa, Makkah & Madinah hotels, luxury AC coaches, and dedicated Ethiopian Mutawwif scholars.",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("umrah_search_field"),
                placeholder = { Text("Search by package, hotel, or feature...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = AlAwlaEmeraldPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Tier Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tiers) { tier ->
                    val isSelected = selectedTier == tier
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTier = tier },
                        label = { Text(tier) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AlAwlaEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Packages List
        items(filtered) { pkg ->
            UmrahDetailedItemCard(
                pkg = pkg,
                language = language,
                onViewDetails = { viewModel.viewPackageDetail(pkg) },
                onBookNow = { viewModel.startBooking(pkg) }
            )
        }
    }
}

@Composable
fun UmrahDetailedItemCard(
    pkg: TravelPackage,
    language: AppLanguage,
    onViewDetails: () -> Unit,
    onBookNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onViewDetails),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Tier Pill
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = AlAwlaEmeraldPrimary
                ) {
                    Text(
                        text = pkg.tier,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Price display
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "ETB ${"%,d".format(pkg.priceEtb)}",
                        color = AlAwlaGoldLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Approx. $${pkg.priceUsd} USD",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = when (language) {
                        AppLanguage.AMHARIC -> pkg.titleAm
                        AppLanguage.ARABIC -> pkg.titleAr
                        else -> pkg.title
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Makkah & Madinah Hotels details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = AlAwlaEmeraldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Makkah (7 Nights)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = pkg.hotelMakkah,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = AlAwlaGoldDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Madinah (3 Nights)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = pkg.hotelMadinah,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Room Occupancy
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, modifier = Modifier.size(16.dp), tint = AlAwlaEmeraldPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Room Arrangement: ${pkg.occupancy}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(AppStrings.get("view_details", language), fontSize = 12.sp)
                    }

                    Button(
                        onClick = onBookNow,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlAwlaEmeraldPrimary
                        )
                    ) {
                        Text(AppStrings.get("quick_book", language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
