package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SurpriseTheme

@Composable
fun GiftBox3D(
    theme: SurpriseTheme,
    recipientName: String,
    senderName: String,
    isOpen: Boolean,
    onOpenGift: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Idle Wobble Animation
    val infiniteTransition = rememberInfiniteTransition(label = "gift_idle")
    val wobbleAngle by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble"
    )
    val idleScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_scale"
    )

    // Open Animation States
    val lidOffset by animateFloatAsState(
        targetValue = if (isOpen) -180f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "lid_offset"
    )
    val lidRotation by animateFloatAsState(
        targetValue = if (isOpen) -28f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "lid_rotation"
    )
    val giftAlpha by animateFloatAsState(
        targetValue = if (isOpen) 0f else 1f,
        animationSpec = tween(600, delayMillis = 300),
        label = "gift_alpha"
    )

    val primaryColor = Color(theme.primaryColorHex)
    val secondaryColor = Color(theme.secondaryColorHex)
    val ribbonColor = Color(theme.accentColorHex)

    fun triggerHaptic() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Hint
            Text(
                text = "A Birthday Surprise For",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recipientName.ifBlank { "You" },
                color = primaryColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Sent with ❤️ by ${senderName.ifBlank { "Someone Special" }}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // Gift Box Container
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(if (!isOpen) idleScale else 1f)
                    .rotate(if (!isOpen) wobbleAngle else 0f)
                    .alpha(giftAlpha)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isOpen) {
                            triggerHaptic()
                            onOpenGift()
                        }
                    }
                    .testTag("gift_box_interactive"),
                contentAlignment = Alignment.Center
            ) {
                // Box Base
                Box(
                    modifier = Modifier
                        .size(190.dp, 160.dp)
                        .align(Alignment.BottomCenter)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(primaryColor, secondaryColor)
                            ),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    // Vertical Ribbon
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .background(ribbonColor)
                    )
                    // Horizontal Ribbon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .align(Alignment.Center)
                            .background(ribbonColor)
                    )
                }

                // Box Lid (lifts up on tap)
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, lidOffset.dp.roundToPx()) }
                        .rotate(lidRotation)
                        .size(210.dp, 56.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 48.dp)
                        .shadow(12.dp, RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(secondaryColor, primaryColor)
                            ),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    // Lid ribbon
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .background(ribbonColor)
                    )

                    // Ribbon Bow Top
                    Box(
                        modifier = Modifier
                            .size(54.dp, 36.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (-20).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ribbonColor,
                            modifier = Modifier.size(26.dp)
                        ) {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (!isOpen) {
                        triggerHaptic()
                        onOpenGift()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                ),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .height(56.dp)
                    .testTag("open_gift_button")
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = "Tap to open",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tap to Unwrap Gift Box 🎁",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
