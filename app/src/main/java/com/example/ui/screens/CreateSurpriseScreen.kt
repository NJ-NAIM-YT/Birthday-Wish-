package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.AiWishWriter
import com.example.audio.BirthdayAudioPlayer
import com.example.model.MusicTrack
import com.example.model.Relationship
import com.example.model.SurpriseData
import com.example.model.SurpriseTheme
import com.example.model.WishLanguage
import com.example.model.WishTone
import com.example.util.SurpriseStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSurpriseScreen(
    initialTheme: SurpriseTheme = SurpriseTheme.LOVE,
    onSurpriseCreated: (SurpriseData) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val storage = remember { SurpriseStorage(context) }

    var currentStep by remember { mutableIntStateOf(1) } // 1: Info, 2: Theme, 3: Message & AI, 4: Visuals & Music
    var isLivePreviewMode by remember { mutableStateOf(false) }

    // Form Fields
    var recipientName by remember { mutableStateOf("") }
    var recipientNameError by remember { mutableStateOf(false) }
    var selectedRelationship by remember { mutableStateOf(Relationship.ROMANTIC_PARTNER) }
    var senderName by remember { mutableStateOf("") }
    var senderNameError by remember { mutableStateOf(false) }
    var ageOrMilestone by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf(initialTheme) }
    var messageText by remember {
        mutableStateOf("Happy Birthday to the most amazing person! Wishing you endless happiness, laughter, and love today and always! 🎂✨💖")
    }
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    var selectedAvatarPreset by remember { mutableStateOf("cake") }
    var selectedMusicTrack by remember { mutableStateOf(MusicTrack.CLASSIC_BDAY) }

    // AI Wish Writer Bottom Sheet state
    var showAiWriterSheet by remember { mutableStateOf(false) }

    // Audio preview player
    val previewAudioPlayer = remember { BirthdayAudioPlayer() }
    val isPlayingPreview by previewAudioPlayer.isPlaying.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            previewAudioPlayer.stop()
        }
    }

    BackHandler {
        previewAudioPlayer.stop()
        if (isLivePreviewMode) {
            isLivePreviewMode = false
        } else if (currentStep > 1) {
            currentStep -= 1
        } else {
            onNavigateBack()
        }
    }

    // Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
        }
    }

    val currentSurprise = SurpriseData(
        recipientName = recipientName.ifBlank { "Birthday Star" },
        relationship = selectedRelationship,
        message = messageText.ifBlank { "Happy Birthday! Wishing you joy and blessings!" },
        senderName = senderName.ifBlank { "Your Secret Admirer" },
        theme = selectedTheme,
        photoUri = selectedPhotoUri,
        avatarPreset = selectedAvatarPreset,
        musicTrack = selectedMusicTrack,
        ageOrMilestone = ageOrMilestone.ifBlank { null }
    )

    if (isLivePreviewMode) {
        // Show live reveal preview
        SurpriseRevealScreen(
            surprise = currentSurprise,
            onNavigateBack = { isLivePreviewMode = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Birthday Surprise 🎁",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) currentStep -= 1 else onNavigateBack()
                        },
                        modifier = Modifier.testTag("create_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { isLivePreviewMode = true },
                        modifier = Modifier.testTag("live_preview_button")
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live Preview", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF140D22))
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF1B132C),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep -= 1 },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Previous", color = Color.White)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (currentStep < 4) {
                        Button(
                            onClick = {
                                if (currentStep == 1) {
                                    if (recipientName.isBlank()) {
                                        recipientNameError = true
                                        Toast.makeText(context, "Please enter recipient name", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (senderName.isBlank()) {
                                        senderNameError = true
                                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                }
                                currentStep += 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(selectedTheme.primaryColorHex)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("step_next_button")
                        ) {
                            Text("Next Step", fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (recipientName.isBlank() || senderName.isBlank()) {
                                    Toast.makeText(context, "Please complete recipient & your name", Toast.LENGTH_SHORT).show()
                                    currentStep = 1
                                    return@Button
                                }
                                previewAudioPlayer.stop()
                                val finalized = currentSurprise
                                storage.saveSurprise(finalized)
                                onSurpriseCreated(finalized)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(selectedTheme.accentColorHex)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("finish_create_button")
                        ) {
                            Text("Launch Surprise 🚀", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF0F0A19)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Step Progress Indicator
            StepProgressHeader(
                currentStep = currentStep,
                themeColor = Color(selectedTheme.primaryColorHex)
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (currentStep) {
                1 -> StepOneRecipientInfo(
                    recipientName = recipientName,
                    recipientNameError = recipientNameError,
                    onRecipientNameChange = {
                        recipientName = it
                        recipientNameError = false
                    },
                    relationship = selectedRelationship,
                    onRelationshipChange = { selectedRelationship = it },
                    senderName = senderName,
                    senderNameError = senderNameError,
                    onSenderNameChange = {
                        senderName = it
                        senderNameError = false
                    },
                    ageOrMilestone = ageOrMilestone,
                    onAgeChange = { ageOrMilestone = it }
                )

                2 -> StepTwoThemeGallery(
                    selectedTheme = selectedTheme,
                    onSelectTheme = { selectedTheme = it }
                )

                3 -> StepThreeMessageAndAi(
                    messageText = messageText,
                    onMessageChange = { messageText = it },
                    recipientName = recipientName,
                    relationship = selectedRelationship,
                    onOpenAiSheet = { showAiWriterSheet = true }
                )

                4 -> StepFourVisualsAndMusic(
                    selectedPhotoUri = selectedPhotoUri,
                    onPickPhoto = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemovePhoto = { selectedPhotoUri = null },
                    selectedAvatar = selectedAvatarPreset,
                    onSelectAvatar = { selectedAvatarPreset = it },
                    selectedTrack = selectedMusicTrack,
                    onSelectTrack = {
                        selectedMusicTrack = it
                        if (isPlayingPreview) {
                            previewAudioPlayer.play(it, loop = false)
                        }
                    },
                    isPlayingAudio = isPlayingPreview,
                    onTogglePreviewAudio = {
                        if (isPlayingPreview) {
                            previewAudioPlayer.stop()
                        } else {
                            previewAudioPlayer.play(selectedMusicTrack, loop = false)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // AI Wish Writer Bottom Sheet
    if (showAiWriterSheet) {
        AiWishWriterBottomSheet(
            recipientName = recipientName,
            relationship = selectedRelationship,
            onWishSelected = { chosenWish ->
                messageText = chosenWish
                showAiWriterSheet = false
                Toast.makeText(context, "Wish applied to surprise card! ✨", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showAiWriterSheet = false }
        )
    }
}

@Composable
fun StepProgressHeader(currentStep: Int, themeColor: Color) {
    val steps = listOf("Recipient", "Theme", "Wishes & AI", "Music & Media")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val stepNum = index + 1
            val isActive = stepNum <= currentStep
            val isCurrent = stepNum == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isActive) themeColor else Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$stepNum",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) Color.White else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOneRecipientInfo(
    recipientName: String,
    recipientNameError: Boolean,
    onRecipientNameChange: (String) -> Unit,
    relationship: Relationship,
    onRelationshipChange: (Relationship) -> Unit,
    senderName: String,
    senderNameError: Boolean,
    onSenderNameChange: (String) -> Unit,
    ageOrMilestone: String,
    onAgeChange: (String) -> Unit
) {
    var expandedRelationshipDropdown by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 1: Who is this surprise for?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Wishora crafts personalized celebration touches tailored to your bond.",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Recipient Name
        OutlinedTextField(
            value = recipientName,
            onValueChange = onRecipientNameChange,
            label = { Text("Recipient Name *") },
            placeholder = { Text("e.g. Sarah, Ayan, Mom, Priya") },
            isError = recipientNameError,
            supportingText = {
                if (recipientNameError) Text("Recipient name is required", color = MaterialTheme.colorScheme.error)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_recipient_name"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF4081),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Relationship Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedRelationshipDropdown,
            onExpandedChange = { expandedRelationshipDropdown = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "${relationship.emoji} ${relationship.displayName}",
                onValueChange = {},
                readOnly = true,
                label = { Text("Relationship") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRelationshipDropdown) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("relationship_selector"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF4081),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expandedRelationshipDropdown,
                onDismissRequest = { expandedRelationshipDropdown = false },
                modifier = Modifier.background(Color(0xFF221735))
            ) {
                Relationship.values().forEach { rel ->
                    DropdownMenuItem(
                        text = {
                            Text("${rel.emoji} ${rel.displayName}", color = Color.White, fontSize = 14.sp)
                        },
                        onClick = {
                            onRelationshipChange(rel)
                            expandedRelationshipDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sender Name
        OutlinedTextField(
            value = senderName,
            onValueChange = onSenderNameChange,
            label = { Text("Your Name (Sender) *") },
            placeholder = { Text("e.g. Alex, Maya, Your Bestie") },
            isError = senderNameError,
            supportingText = {
                if (senderNameError) Text("Sender name is required", color = MaterialTheme.colorScheme.error)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_sender_name"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF4081),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Optional Age or Milestone
        OutlinedTextField(
            value = ageOrMilestone,
            onValueChange = onAgeChange,
            label = { Text("Age or Milestone (Optional)") },
            placeholder = { Text("e.g. 21, Sweet 16, 25th, 50th") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_age_milestone"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF4081),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun StepTwoThemeGallery(
    selectedTheme: SurpriseTheme,
    onSelectTheme: (SurpriseTheme) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 2: Choose a Surprise Theme",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Select from 6 bespoke artistic themes designed for every recipient personality.",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        val themes = SurpriseTheme.values()
        themes.forEach { theme ->
            val isSelected = theme == selectedTheme
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelectTheme(theme) }
                    .testTag("theme_card_${theme.name.lowercase()}"),
                shape = RoundedCornerShape(18.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(theme.accentColorHex)) else null,
                colors = CardDefaults.cardColors(
                    containerColor = Color(theme.surfaceColorHex)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(theme.primaryColorHex),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = theme.emoji, fontSize = 26.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = theme.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = theme.subtitle,
                            fontSize = 13.sp,
                            color = Color(theme.accentColorHex)
                        )
                    }

                    if (isSelected) {
                        Surface(
                            shape = CircleShape,
                            color = Color(theme.accentColorHex),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepThreeMessageAndAi(
    messageText: String,
    onMessageChange: (String) -> Unit,
    recipientName: String,
    relationship: Relationship,
    onOpenAiSheet: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Step 3: Birthday Message",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Write your heartfelt message or tap AI Wish Writer.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Wish Writer Banner Button
        Surface(
            onClick = onOpenAiSheet,
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF6A1B9A),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("open_ai_writer_button")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Wish Writer",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "✨ AI Wish Writer & Templates",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "English & Bengali wishes with personalized tones",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
                Text("Open", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = messageText,
            onValueChange = onMessageChange,
            label = { Text("Birthday Message *") },
            placeholder = { Text("Write your sweet birthday message here...") },
            minLines = 4,
            maxLines = 7,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_message_text"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF4081),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun StepFourVisualsAndMusic(
    selectedPhotoUri: String?,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    selectedAvatar: String,
    onSelectAvatar: (String) -> Unit,
    selectedTrack: MusicTrack,
    onSelectTrack: (MusicTrack) -> Unit,
    isPlayingAudio: Boolean,
    onTogglePreviewAudio: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 4: Photo & Music",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Add an optional memory photo and choose your birthday melody.",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Photo memory selector
        Text(
            text = "Optional Photo Memory 📸",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedPhotoUri != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF221735)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = Uri.parse(selectedPhotoUri),
                        contentDescription = "Selected photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Photo Selected", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Will appear in Polaroid memory frame", color = Color.Gray, fontSize = 12.sp)
                    }
                    IconButton(onClick = onRemovePhoto) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove photo", tint = Color(0xFFFF5252))
                    }
                }
            }
        } else {
            OutlinedButton(
                onClick = onPickPhoto,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("upload_photo_button")
            ) {
                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color(0xFFFF4081))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Photo from Gallery", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar preset alternative
        Text(
            text = "Or Choose a Festive Avatar Preset",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val avatars = listOf(
            "cake" to "🎂",
            "heart" to "💖",
            "star" to "⭐",
            "crown" to "👑",
            "gift" to "🎁",
            "party" to "🎉",
            "balloons" to "🎈"
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(avatars) { (key, emoji) ->
                val isSelected = selectedAvatar == key && selectedPhotoUri == null
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color(0xFFFF4081) else Color(0xFF221735),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)) else null,
                    modifier = Modifier
                        .size(52.dp)
                        .clickable {
                            onSelectAvatar(key)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Music Track Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Background Music & Melody 🎵",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Preview player button
            IconButton(
                onClick = onTogglePreviewAudio,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFF4081), CircleShape)
                    .testTag("preview_audio_button")
            ) {
                Icon(
                    imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Test audio",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val tracks = MusicTrack.values()
        tracks.forEach { track ->
            val isSelected = track == selectedTrack
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectTrack(track) }
                    .testTag("track_${track.name.lowercase()}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF381F54) else Color(0xFF1E152D)
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF4081)) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = track.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = track.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(text = track.description, color = Color.Gray, fontSize = 12.sp)
                    }
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = Color(0xFFFF4081))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiWishWriterBottomSheet(
    recipientName: String,
    relationship: Relationship,
    onWishSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedLanguage by remember { mutableStateOf(WishLanguage.ENGLISH) }
    var selectedTone by remember { mutableStateOf(WishTone.HEARTFELT) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: AI Generator, 1: Template Library

    var isGenerating by remember { mutableStateOf(false) }
    var generatedWish by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF191124),
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
                .testTag("ai_wish_writer_sheet")
        ) {
            Text(
                text = "✨ AI Wish Writer & Library",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Craft the perfect birthday wish in English or Bengali (বাংলা).",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            // Language Switcher Chips
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WishLanguage.values().forEach { lang ->
                    FilterChip(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang.displayName, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF4081),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF261A39),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs: AI Generator vs Template Library
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFFFF4081)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("⚡ AI Generator", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color.White else Color.Gray) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("📚 Template Library (${if (selectedLanguage == WishLanguage.BENGALI) "বাংলা" else "EN"})", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color.White else Color.Gray) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Tone selector
                Text(
                    text = "Select Tone:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(WishTone.values()) { tone ->
                        FilterChip(
                            selected = selectedTone == tone,
                            onClick = { selectedTone = tone },
                            label = { Text("${tone.emoji} ${tone.displayName}", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF9C27B0),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF261A39),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Generate Button
                Button(
                    onClick = {
                        scope.launch {
                            isGenerating = true
                            val result = AiWishWriter.generateWish(
                                recipientName = recipientName,
                                relationship = relationship,
                                tone = selectedTone,
                                language = selectedLanguage,
                                apiKey = try {
                                    val field = com.example.BuildConfig::class.java.getDeclaredField("GEMINI_API_KEY")
                                    field.isAccessible = true
                                    (field.get(null) as? String) ?: ""
                                } catch (_: Throwable) { "" }
                            )
                            generatedWish = result
                            isGenerating = false
                        }
                    },
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("ai_generate_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing Wish...", color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Personalized Wish", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Show Generated Wish
                generatedWish?.let { wish ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1C48)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "“$wish”",
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onWishSelected(wish) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("use_generated_wish_button")
                            ) {
                                Text("Use This Wish ✨", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            } else {
                // Template Library List
                val filteredTemplates = AiWishWriter.templates.filter { it.language == selectedLanguage }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredTemplates.forEach { template ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF221735)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${template.tone.emoji} ${template.title}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700)
                                    )
                                    TextButton(
                                        onClick = { onWishSelected(template.text) },
                                        modifier = Modifier.testTag("use_template_button_${template.title.lowercase().replace(" ", "_")}")
                                    ) {
                                        Text("Apply", color = Color(0xFFFF4081), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = template.text,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
