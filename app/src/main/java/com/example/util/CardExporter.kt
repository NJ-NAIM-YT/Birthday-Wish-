package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.SurpriseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object CardExporter {

    suspend fun generateCardBitmap(context: Context, surprise: SurpriseData): Bitmap = withContext(Dispatchers.Default) {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val theme = surprise.theme
        val topColor = theme.backgroundColorHex.toInt()
        val bottomColor = theme.surfaceColorHex.toInt()

        // Background Gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), topColor, bottomColor, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Decorative background stars & confetti
        val confettiPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val colors = theme.confettiColors.map { it.toInt() }
        val random = java.util.Random(surprise.createdTimestamp)

        for (i in 0 until 120) {
            confettiPaint.color = colors[i % colors.size]
            val cx = random.nextFloat() * width
            val cy = random.nextFloat() * height
            val radius = 4f + random.nextFloat() * 12f
            canvas.drawCircle(cx, cy, radius, confettiPaint)
        }

        // Card Surface Container
        val cardMargin = 80f
        val cardRect = RectF(cardMargin, 160f, width - cardMargin, height - 160f)
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.surfaceColorHex.toInt()
            alpha = 230
        }
        canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)

        // Card Border Accent
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColorHex.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawRoundRect(cardRect, 48f, 48f, borderPaint)

        // Header Title
        val brandPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColorHex.toInt()
            textSize = 42f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("WISHORA • BIRTHDAY CELEBRATION", width / 2f, 260f, brandPaint)

        // Emoji & Cake Icon
        val emojiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 100f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🎂✨🎉", width / 2f, 380f, emojiPaint)

        // Happy Birthday Text
        val hbdPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("HAPPY BIRTHDAY!", width / 2f, 480f, hbdPaint)

        // Recipient Name
        val namePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.primaryColorHex.toInt()
            textSize = 80f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(surprise.recipientName, width / 2f, 580f, namePaint)

        // Relationship & Age Tag
        val tagText = buildString {
            append("To my wonderful ${surprise.relationship.displayName}")
            if (!surprise.ageOrMilestone.isNullOrBlank()) {
                append(" • Celebrating ${surprise.ageOrMilestone}!")
            }
        }
        val tagPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(tagText, width / 2f, 650f, tagPaint)

        // Divider Line
        val divPaint = Paint().apply {
            color = theme.accentColorHex.toInt()
            strokeWidth = 3f
        }
        canvas.drawLine(cardMargin + 80f, 700f, width - cardMargin - 80f, 700f, divPaint)

        // Message text block (using StaticLayout for multi-line support)
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 44f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val textWidth = (width - cardMargin * 2 - 140f).toInt()
        val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(surprise.message, 0, surprise.message.length, textPaint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(14f, 1f)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(surprise.message, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1f, 14f, true)
        }

        canvas.save()
        canvas.translate(cardMargin + 70f, 750f)
        staticLayout.draw(canvas)
        canvas.restore()

        // Sender Signature
        val signPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColorHex.toInt()
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("With all my love, ${surprise.senderName} ❤️", width / 2f, height - 280f, signPaint)

        // Footer Brand
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            textSize = 32f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Made with Wishora • Make Every Birthday Special", width / 2f, height - 210f, footerPaint)

        bitmap
    }

    suspend fun saveCardToGallery(context: Context, surprise: SurpriseData): Uri? = withContext(Dispatchers.IO) {
        val bitmap = generateCardBitmap(context, surprise)
        val filename = "Wishora_${surprise.recipientName.replace(" ", "_")}_${System.currentTimeMillis()}.png"

        var outputStream: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wishora")
                }
                imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    outputStream = context.contentResolver.openOutputStream(imageUri)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Wishora"
                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val imageFile = File(file, filename)
                outputStream = FileOutputStream(imageFile)
                imageUri = Uri.fromFile(imageFile)
            }

            outputStream?.let {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
            }
        } catch (_: Exception) {
            imageUri = null
        } finally {
            outputStream?.close()
        }

        imageUri
    }

    suspend fun shareCardImage(context: Context, surprise: SurpriseData) = withContext(Dispatchers.IO) {
        val bitmap = generateCardBitmap(context, surprise)
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "wishora_card_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, "🎁 Birthday Surprise for ${surprise.recipientName} from ${surprise.senderName}! Made with Wishora ✨")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Birthday Surprise Card"))
            }
        } catch (e: Exception) {
            // Fallback to text link share
            SurpriseEncoder.shareGeneric(context, surprise)
        }
    }
}
