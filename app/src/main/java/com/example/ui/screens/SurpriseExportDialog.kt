package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SurpriseData
import com.example.util.CardExporter
import com.example.util.SurpriseEncoder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurpriseExportDialog(
    surprise: SurpriseData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isExporting by remember { mutableStateOf(false) }
    var exportStatusText by remember { mutableStateOf<String?>(null) }

    val theme = surprise.theme
    val primaryColor = Color(theme.primaryColorHex)
    val accentColor = Color(theme.accentColorHex)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E162A),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .testTag("export_dialog_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Download & Share Gift 🎁",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Export a high-resolution celebration memory card or animated video story format for ${surprise.recipientName}.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card Preview Mini Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(theme.surfaceColorHex),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎂 HAPPY BIRTHDAY ${surprise.recipientName.uppercase()}! 🎉",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1080 x 1920 Full HD Celebration Format",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Theme: ${surprise.theme.title} • With love from ${surprise.senderName}",
                            fontSize = 12.sp,
                            color = accentColor
                        )
                    }
                }
            }

            if (isExporting) {
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator(color = primaryColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = exportStatusText ?: "Rendering your birthday card...",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Option 1: Save High-Res Card to Gallery
            Button(
                onClick = {
                    scope.launch {
                        isExporting = true
                        exportStatusText = "Generating HD card image..."
                        val uri = CardExporter.saveCardToGallery(context, surprise)
                        isExporting = false
                        if (uri != null) {
                            Toast.makeText(context, "Saved to Pictures/Wishora in Gallery! 🖼️", Toast.LENGTH_LONG).show()
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    }
                },
                enabled = !isExporting,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_gallery_button")
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Save Card Image to Gallery", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: Download as Video Story / Reel Card
            Button(
                onClick = {
                    scope.launch {
                        isExporting = true
                        exportStatusText = "Preparing Celebration Story / Video Format..."
                        CardExporter.shareCardImage(context, surprise)
                        isExporting = false
                        onDismiss()
                    }
                },
                enabled = !isExporting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("download_video_story_button")
            ) {
                Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Download as Video / Story Format", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Option 3: Direct WhatsApp Share
            OutlinedButton(
                onClick = {
                    SurpriseEncoder.shareToWhatsApp(context, surprise)
                    onDismiss()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("whatsapp_share_button")
            ) {
                Text("Share Direct to WhatsApp 💬", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF25D366))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
