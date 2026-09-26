package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.ChatMessage
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSupportScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val language by viewModel.currentLanguage.collectAsState()
    val messages by viewModel.allChatMessages.collectAsState()
    val isAgentTyping by viewModel.isAgentTyping.collectAsState()
    val activeBookingRef by viewModel.chatBookingRef.collectAsState()

    var inputMessage by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "Umrah Visa processing status",
        "Hotel distance from Holy Kaaba",
        "Diaspora family sponsorship details",
        "Change flight reservation dates",
        "Luggage allowance on Ethiopian Airlines"
    )

    // Scroll to bottom when message list changes
    LaunchedEffect(messages.size, isAgentTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_support_screen")
    ) {
        // Chat Header Top Card
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(1.dp, AlAwlaGoldPrimary, CircleShape),
                            color = Color.White
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.alawla_official_logo),
                                contentDescription = "Support Agent",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Al-Awla Concierge Desk",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2ECC71))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Online • Addis Ababa & Jeddah",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Direct Action Shortcuts (Phone, WhatsApp)
                    Row {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+251986111333"))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = "Call Hotline", tint = AlAwlaEmeraldPrimary)
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/251986111333"))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ChatBubble, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                        }
                    }
                }

                // Booking context badge if tagged
                activeBookingRef?.let { ref ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AlAwlaGoldContainer.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = AlAwlaGoldDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Discussing Booking #$ref",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlAwlaOnGoldContainer
                                )
                            }

                            IconButton(
                                onClick = { viewModel.clearChatBookingRef() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = AlAwlaOnGoldContainer, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }

            if (isAgentTyping) {
                item {
                    AgentTypingIndicator()
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickQuestions) { question ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendChatMessage(question, activeBookingRef)
                    },
                    label = { Text(question, fontSize = 11.sp) }
                )
            }
        }

        // Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = {
                        Text(
                            AppStrings.get("chat_placeholder", language),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendChatMessage(inputMessage, activeBookingRef)
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(AlAwlaEmeraldPrimary)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isAgent = message.isAgent
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAgent) Alignment.Start else Alignment.End
    ) {
        if (isAgent) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall,
                color = AlAwlaEmeraldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isAgent) 4.dp else 16.dp,
                bottomEnd = if (isAgent) 16.dp else 4.dp
            ),
            color = if (isAgent) MaterialTheme.colorScheme.surfaceVariant else AlAwlaEmeraldPrimary,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.bookingReference != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isAgent) AlAwlaEmeraldContainer else AlAwlaGoldPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "Ref: ${message.bookingReference}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAgent) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = if (isAgent) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AgentTypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(AlAwlaEmeraldPrimary)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Al-Awla Concierge is preparing your answer...",
            style = MaterialTheme.typography.labelSmall,
            color = AlAwlaEmeraldPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
