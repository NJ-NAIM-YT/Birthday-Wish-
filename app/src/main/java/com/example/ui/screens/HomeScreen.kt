package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SurpriseData
import com.example.model.SurpriseTheme
import com.example.util.SurpriseEncoder
import com.example.util.SurpriseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onCreateSurprise: (SurpriseTheme) -> Unit,
    onOpenSurprise: (SurpriseData) -> Unit
) {
    val context = LocalContext.current
    val storage = remember { SurpriseStorage(context) }
    var savedSurprises by remember { mutableStateOf<List<SurpriseData>>(emptyList()) }

    var pasteLinkInput by remember { mutableStateOf("") }
    var pasteLinkError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        savedSurprises = storage.getAllSurprises()
    }

    val refreshList = {
        savedSurprises = storage.getAllSurprises()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0A19))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("home_screen_container")
    ) {
        // App Brand Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFF4081),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🎁", fontSize = 24.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Wishora",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Make Every Birthday Special",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Card with Primary Call to Action
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF221338)),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(26.dp))
                .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.4f), RoundedCornerShape(26.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF381552), Color(0xFF1E0E33))
                        )
                    )
                    .padding(22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = "✨ Interactive Gift Studio",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Create a Magic Birthday Surprise",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 28.sp
                            )
                        }
                        Text(text = "🎂", fontSize = 48.sp, modifier = Modifier.padding(start = 12.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Unbox 3D animated gifts, blow out realistic candles, write AI wishes in English & Bengali, and play personalized songs!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { onCreateSurprise(SurpriseTheme.LOVE) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("create_surprise_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create Surprise Now 🎁",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feature Highlights Ticker
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FeaturePill(icon = "🎁", label = "3D Reveal")
            FeaturePill(icon = "🕯️", label = "Blow Candle")
            FeaturePill(icon = "✨", label = "AI Writer")
            FeaturePill(icon = "🎵", label = "Custom Song")
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Themes Gallery Showcase
        Text(
            text = "Explore Themes Gallery 🎨",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Pick an aesthetic designed for romantic partners, parents, friends, and family",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(SurpriseTheme.values()) { theme ->
                ThemeCardItem(theme = theme, onSelect = { onCreateSurprise(theme) })
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Open a Surprise Link / Code Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open a Shared Surprise Link 💌",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Received a Wishora surprise link from a friend? Paste it below to unwrap!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = pasteLinkInput,
                    onValueChange = {
                        pasteLinkInput = it
                        pasteLinkError = null
                    },
                    placeholder = { Text("Paste wishora:// or https:// link here...", fontSize = 13.sp) },
                    singleLine = true,
                    isError = pasteLinkError != null,
                    supportingText = {
                        pasteLinkError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paste_link_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF4081),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (pasteLinkInput.isBlank()) {
                            pasteLinkError = "Please enter or paste a valid link"
                            return@Button
                        }
                        val decoded = SurpriseEncoder.decodeSurprise(pasteLinkInput)
                        if (decoded != null) {
                            storage.saveSurprise(decoded)
                            refreshList()
                            pasteLinkInput = ""
                            pasteLinkError = null
                            onOpenSurprise(decoded)
                        } else {
                            pasteLinkError = "Unable to decode surprise. Check link format."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("open_shared_link_button")
                ) {
                    Text("Unwrap Surprise 🎁", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // My Surprises Section (Local storage)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Created Surprises (${savedSurprises.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (savedSurprises.isEmpty()) {
            // Empty State
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191124)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No surprises yet!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Craft an unforgettable birthday moment for your loved ones now.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                savedSurprises.forEach { surprise ->
                    SavedSurpriseItem(
                        surprise = surprise,
                        onOpen = { onOpenSurprise(surprise) },
                        onShareWhatsApp = { SurpriseEncoder.shareToWhatsApp(context, surprise) },
                        onCopyLink = { SurpriseEncoder.copyLinkToClipboard(context, surprise) },
                        onDelete = {
                            storage.deleteSurprise(surprise.id)
                            refreshList()
                            Toast.makeText(context, "Surprise removed", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun FeaturePill(icon: String, label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF221634),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ThemeCardItem(theme: SurpriseTheme, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onSelect() }
            .testTag("gallery_theme_${theme.name.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(theme.surfaceColorHex)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(theme.accentColorHex).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(theme.primaryColorHex),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = theme.emoji, fontSize = 22.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = theme.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
            Text(
                text = theme.subtitle,
                fontSize = 11.sp,
                color = Color(theme.accentColorHex),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun SavedSurpriseItem(
    surprise: SurpriseData,
    onOpen: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onCopyLink: () -> Unit,
    onDelete: () -> Unit
) {
    val theme = surprise.theme
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val formattedDate = remember(surprise.createdTimestamp) {
        dateFormat.format(Date(surprise.createdTimestamp))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(theme.surfaceColorHex).copy(alpha = 0.9f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(theme.accentColorHex).copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("saved_surprise_item_${surprise.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = theme.emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = surprise.recipientName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${surprise.relationship.displayName} • From ${surprise.senderName}",
                            fontSize = 12.sp,
                            color = Color(theme.accentColorHex)
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onCopyLink,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Copy Link", fontSize = 11.sp, color = Color.White)
                    }
                    Button(
                        onClick = onOpen,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(theme.primaryColorHex)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("View Reveal 🎁", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
