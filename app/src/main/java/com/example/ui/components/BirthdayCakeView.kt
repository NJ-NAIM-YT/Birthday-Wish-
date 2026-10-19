package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SurpriseTheme

@Composable
fun BirthdayCakeView(
    theme: SurpriseTheme,
    ageOrMilestone: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCandleBlownOut by remember { mutableStateOf(false) }

    // Candle flame flicker animation
    val infiniteTransition = rememberInfiniteTransition(label = "flame_flicker")
    val flameScaleY by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale_y"
    )
    val flameOffsetX by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_offset_x"
    )

    val smokeAlpha by animateFloatAsState(
        targetValue = if (isCandleBlownOut) 0.8f else 0f,
        animationSpec = tween(500),
        label = "smoke_alpha"
    )

    fun onBlowCandle() {
        if (!isCandleBlownOut) {
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(150)
                }
            } catch (_: Exception) {}
            isCandleBlownOut = true
        }
    }

    val primaryColor = Color(theme.primaryColorHex)
    val secondaryColor = Color(theme.secondaryColorHex)
    val accentColor = Color(theme.accentColorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("birthday_cake_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(theme.surfaceColorHex).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Header
            Text(
                text = if (!isCandleBlownOut) "Make a Wish & Blow the Candle! 🎂" else "🎉 Wish Granted! 🌟",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = if (!isCandleBlownOut) accentColor else Color(0xFFFFD700),
                textAlign = TextAlign.Center
            )
            Text(
                text = if (!isCandleBlownOut) "Tap the glowing flame to blow it out" else "May all your birthday dreams come true!",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Birthday Cake Illustration Container
            Box(
                modifier = Modifier
                    .size(240.dp, 200.dp)
                    .clickable { onBlowCandle() }
                    .testTag("candle_tap_area"),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Tier 1 (Base Tier)
                Box(
                    modifier = Modifier
                        .size(200.dp, 60.dp)
                        .align(Alignment.BottomCenter)
                        .shadow(8.dp, RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(primaryColor, Color(theme.backgroundColorHex))
                            ),
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    // Frosting drips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(7) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.9f))
                            )
                        }
                    }

                    // Milestone badge if present
                    if (!ageOrMilestone.isNullOrBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = accentColor,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.Center)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ageOrMilestone,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // Tier 2 (Top Tier)
                Box(
                    modifier = Modifier
                        .size(140.dp, 50.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-55).dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(secondaryColor, primaryColor)
                            ),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    // Top Frosting
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(Color(0xFFFFF9C4), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(5) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF9C4))
                            )
                        }
                    }
                }

                // Candle Stem
                Box(
                    modifier = Modifier
                        .size(10.dp, 36.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-102).dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFFD54F), Color(0xFFFF7043))
                            ),
                            RoundedCornerShape(4.dp)
                        )
                )

                // Candle Flame or Smoke
                if (!isCandleBlownOut) {
                    // Flame
                    Box(
                        modifier = Modifier
                            .size(18.dp, 28.dp)
                            .align(Alignment.BottomCenter)
                            .offset(
                                x = flameOffsetX.dp,
                                y = (-136).dp
                            )
                            .scale(scaleX = 1f, scaleY = flameScaleY)
                            .shadow(16.dp, CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFFF176),
                                        Color(0xFFFF9800),
                                        Color(0xFFFF3D00)
                                    )
                                ),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                            )
                    )
                } else {
                    // Smoke puff when extinguished
                    Box(
                        modifier = Modifier
                            .size(24.dp, 24.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = (-138).dp)
                            .alpha(smokeAlpha)
                    ) {
                        Text(
                            text = "💨",
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            // Re-light candle button when blown out
            AnimatedVisibility(
                visible = isCandleBlownOut,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Button(
                    onClick = { isCandleBlownOut = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .testTag("relight_candle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-light Candle",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Light Candle Again 🕯️",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
