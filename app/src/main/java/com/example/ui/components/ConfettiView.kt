package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float,
    val size: Float,
    val color: Color,
    val isCircle: Boolean
)

@Composable
fun ConfettiView(
    colors: List<Long>,
    modifier: Modifier = Modifier,
    particleCount: Int = 70
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
        )
    }

    val parsedColors = remember(colors) {
        if (colors.isNotEmpty()) colors.map { Color(it) }
        else listOf(Color(0xFFFFD700), Color(0xFFFF4081), Color(0xFF00E5FF), Color(0xFF76FF03))
    }

    val particles = remember {
        val rand = Random(System.currentTimeMillis())
        List(particleCount) {
            ConfettiParticle(
                initialX = rand.nextFloat(),
                initialY = rand.nextFloat() * 0.3f, // start around top-middle
                velocityX = (rand.nextFloat() - 0.5f) * 1.8f,
                velocityY = 0.5f + rand.nextFloat() * 1.2f,
                rotationSpeed = (rand.nextFloat() - 0.5f) * 720f,
                size = 12f + rand.nextFloat() * 18f,
                color = parsedColors[rand.nextInt(parsedColors.size)],
                isCircle = rand.nextBoolean()
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        val canvasWidth = size.width
        val canvasHeight = size.height

        for (p in particles) {
            val curX = (p.initialX * canvasWidth) + (p.velocityX * canvasWidth * t * 0.8f)
            val curY = (p.initialY * canvasHeight) + (p.velocityY * canvasHeight * t * 1.2f)
            val currentRotation = p.rotationSpeed * t
            val alpha = (1f - (t * 0.85f)).coerceIn(0f, 1f)

            if (curY <= canvasHeight) {
                rotate(degrees = currentRotation, pivot = Offset(curX, curY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size / 2f,
                            center = Offset(curX, curY)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(curX - p.size / 2f, curY - p.size / 4f),
                            size = Size(p.size, p.size * 0.6f)
                        )
                    }
                }
            }
        }
    }
}
