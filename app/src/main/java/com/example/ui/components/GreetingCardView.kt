package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.SurpriseData
import com.example.model.SurpriseTheme

@Composable
fun GreetingCardView(
    surprise: SurpriseData,
    isPlayingMusic: Boolean,
    visualizerAmplitudes: FloatArray,
    onToggleMusic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = surprise.theme
    val primaryColor = Color(theme.primaryColorHex)
    val secondaryColor = Color(theme.secondaryColorHex)
    val surfaceColor = Color(theme.surfaceColorHex)
    val accentColor = Color(theme.accentColorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("greeting_card_view"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = primaryColor.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${surprise.theme.emoji} ${surprise.theme.title} Celebration",
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Happy Birthday Header
            Text(
                text = "HAPPY BIRTHDAY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = surprise.recipientName,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = primaryColor,
                textAlign = TextAlign.Center
            )

            // Relationship & milestone subtitle
            Text(
                text = buildString {
                    append("To My ${surprise.relationship.displayName} ${surprise.relationship.emoji}")
                    if (!surprise.ageOrMilestone.isNullOrBlank()) {
                        append(" • ${surprise.ageOrMilestone}")
                    }
                },
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            // Photo Memory / Avatar Section
            PhotoMemorySection(
                photoUri = surprise.photoUri,
                avatarPreset = surprise.avatarPreset,
                theme = theme
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Birthday Message Quote Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.25f),
                        RoundedCornerShape(18.dp)
                    )
                    .border(
                        1.dp,
                        accentColor.copy(alpha = 0.35f),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "“",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor.copy(alpha = 0.6f),
                        lineHeight = 24.sp
                    )
                    Text(
                        text = surprise.message,
                        fontSize = 16.sp,
                        lineHeight = 25.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Text(
                        text = "”",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor.copy(alpha = 0.6f),
                        lineHeight = 24.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Signature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "With all my love,",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${surprise.senderName} ❤️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(16.dp))

            // Personalized Birthday Song Audio Bar
            BirthdaySongPlayerBar(
                track = surprise.musicTrack,
                isPlaying = isPlayingMusic,
                visualizerAmplitudes = visualizerAmplitudes,
                onTogglePlay = onToggleMusic,
                accentColor = accentColor,
                primaryColor = primaryColor
            )
        }
    }
}

@Composable
fun PhotoMemorySection(
    photoUri: String?,
    avatarPreset: String?,
    theme: SurpriseTheme,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(theme.accentColorHex)

    if (!photoUri.isNullOrBlank()) {
        // Polaroid Frame
        Box(
            modifier = modifier
                .rotate(-2f)
                .shadow(12.dp, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = Uri.parse(photoUri),
                    contentDescription = "Birthday photo memory",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cherished Moments ❤️",
                    color = Color(0xFF2C2C2C),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    } else {
        // Decorative Avatar Badge
        Surface(
            shape = CircleShape,
            color = Color(theme.primaryColorHex).copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(2.dp, accentColor),
            modifier = modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val emoji = when (avatarPreset) {
                    "heart" -> "💖"
                    "star" -> "⭐"
                    "crown" -> "👑"
                    "gift" -> "🎁"
                    "party" -> "🎉"
                    "balloons" -> "🎈"
                    else -> "🎂"
                }
                Text(
                    text = emoji,
                    fontSize = 48.sp
                )
            }
        }
    }
}

@Composable
fun BirthdaySongPlayerBar(
    track: com.example.model.MusicTrack,
    isPlaying: Boolean,
    visualizerAmplitudes: FloatArray,
    onTogglePlay: () -> Unit,
    accentColor: Color,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Pause button
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(44.dp)
                    .background(primaryColor, CircleShape)
                    .testTag("music_play_toggle_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause song" else "Play song",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${track.icon} ${track.title}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isPlaying) "Playing music box tune..." else "Tap play to hear melody",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Dancing Equalizer Waves
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(28.dp)
            ) {
                val amps = if (isPlaying) visualizerAmplitudes else floatArrayOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f)
                amps.forEach { amp ->
                    val barHeight = (amp * 26f).coerceIn(4f, 26f).dp
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(2.dp))
                            .background(accentColor)
                    )
                }
            }
        }
    }
}
