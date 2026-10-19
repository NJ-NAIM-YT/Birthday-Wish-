package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BirthdayAudioPlayer
import com.example.model.SurpriseData
import com.example.ui.components.BirthdayCakeView
import com.example.ui.components.ConfettiView
import com.example.ui.components.GiftBox3D
import com.example.ui.components.GreetingCardView
import com.example.util.SurpriseEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurpriseRevealScreen(
    surprise: SurpriseData,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isGiftOpen by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val audioPlayer = remember { BirthdayAudioPlayer() }
    val isPlayingMusic by audioPlayer.isPlaying.collectAsState()
    val visualizerAmplitudes by audioPlayer.visualizerAmplitudes.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    BackHandler {
        audioPlayer.stop()
        onNavigateBack()
    }

    val theme = surprise.theme
    val bgTop = Color(theme.backgroundColorHex)
    val bgBottom = Color(theme.surfaceColorHex)
    val primaryColor = Color(theme.primaryColorHex)
    val accentColor = Color(theme.accentColorHex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (!isGiftOpen) "Birthday Surprise 🎁" else "Happy Birthday! 🎉",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            audioPlayer.stop()
                            onNavigateBack()
                        },
                        modifier = Modifier.testTag("reveal_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isGiftOpen) {
                        IconButton(
                            onClick = {
                                if (isPlayingMusic) audioPlayer.stop()
                                else audioPlayer.play(surprise.musicTrack)
                            },
                            modifier = Modifier.testTag("audio_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isPlayingMusic) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Toggle audio",
                                tint = accentColor
                            )
                        }
                        IconButton(
                            onClick = { SurpriseEncoder.shareGeneric(context, surprise) },
                            modifier = Modifier.testTag("share_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share surprise",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
                .padding(innerPadding)
        ) {
            // Interactive Confetti Layer
            if (showConfetti) {
                ConfettiView(
                    colors = theme.confettiColors,
                    particleCount = 85
                )
            }

            if (!isGiftOpen) {
                // Phase 1: Interactive Gift Box
                GiftBox3D(
                    theme = theme,
                    recipientName = surprise.recipientName,
                    senderName = surprise.senderName,
                    isOpen = isGiftOpen,
                    onOpenGift = {
                        isGiftOpen = true
                        showConfetti = true
                        audioPlayer.play(surprise.musicTrack)
                    }
                )
            } else {
                // Phase 2: Revealed Celebration Stream
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Celebration Banner Header
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "✨ A Special Surprise for ${surprise.recipientName}! ✨",
                            color = accentColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // 1. Birthday Cake with Blow-out candle
                    BirthdayCakeView(
                        theme = theme,
                        ageOrMilestone = surprise.ageOrMilestone
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Personalized Greeting Card & Photo Memory & Song Bar
                    GreetingCardView(
                        surprise = surprise,
                        isPlayingMusic = isPlayingMusic,
                        visualizerAmplitudes = visualizerAmplitudes,
                        onToggleMusic = {
                            if (isPlayingMusic) audioPlayer.stop()
                            else audioPlayer.play(surprise.musicTrack)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Action Buttons
                    // Download & Export Card / Video
                    Button(
                        onClick = { showExportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("download_card_video_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Download as Video / Card Format 🎬",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Share to WhatsApp
                    Button(
                        onClick = { SurpriseEncoder.shareToWhatsApp(context, surprise) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("reveal_whatsapp_button")
                    ) {
                        Text(
                            text = "Share Surprise to WhatsApp 💬",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Copy Link
                        OutlinedButton(
                            onClick = { SurpriseEncoder.copyLinkToClipboard(context, surprise) },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("reveal_copy_link_button")
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link", color = Color.White, fontSize = 13.sp)
                        }

                        // Replay Reveal
                        OutlinedButton(
                            onClick = {
                                isGiftOpen = false
                                showConfetti = false
                                audioPlayer.stop()
                            },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("replay_reveal_button")
                        ) {
                            Icon(imageVector = Icons.Default.Replay, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Replay Box 🔁", color = accentColor, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Privacy notice
                    Text(
                        text = SurpriseEncoder.PRIVACY_DISCLAIMER,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showExportDialog) {
        SurpriseExportDialog(
            surprise = surprise,
            onDismiss = { showExportDialog = false }
        )
    }
}
