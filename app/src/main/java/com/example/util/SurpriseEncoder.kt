package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import com.example.model.MusicTrack
import com.example.model.Relationship
import com.example.model.SurpriseData
import com.example.model.SurpriseTheme
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object SurpriseEncoder {

    const val URL_PREFIX = "https://wishora.app/surprise?d="
    const val SCHEME_PREFIX = "wishora://surprise?d="

    const val PRIVACY_DISCLAIMER =
        "Note: Wishora surprise links store the message and theme directly within the link itself. " +
        "They do not require accounts or cloud databases, but anyone with the link can view it."

    fun encodeSurprise(surprise: SurpriseData): String {
        val json = JSONObject().apply {
            put("id", surprise.id)
            put("n", surprise.recipientName)
            put("r", surprise.relationship.name)
            put("m", surprise.message)
            put("s", surprise.senderName)
            put("t", surprise.theme.name)
            put("a", surprise.avatarPreset ?: "cake")
            put("mu", surprise.musicTrack.name)
            if (!surprise.ageOrMilestone.isNullOrBlank()) {
                put("ag", surprise.ageOrMilestone)
            }
            put("ts", surprise.createdTimestamp)
        }

        val rawBytes = json.toString().toByteArray(StandardCharsets.UTF_8)
        // Compress with GZIP to keep URL compact
        val compressed = ByteArrayOutputStream().use { baos ->
            GZIPOutputStream(baos).use { gz ->
                gz.write(rawBytes)
            }
            baos.toByteArray()
        }

        return Base64.encodeToString(compressed, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun decodeSurprise(encoded: String): SurpriseData? {
        return try {
            val cleanEncoded = when {
                encoded.contains("?d=") -> encoded.substringAfter("?d=")
                encoded.startsWith(URL_PREFIX) -> encoded.removePrefix(URL_PREFIX)
                encoded.startsWith(SCHEME_PREFIX) -> encoded.removePrefix(SCHEME_PREFIX)
                else -> encoded.trim()
            }

            val decodedBytes = Base64.decode(cleanEncoded, Base64.URL_SAFE or Base64.NO_WRAP)
            val jsonString = ByteArrayInputStream(decodedBytes).use { bais ->
                GZIPInputStream(bais).use { gz ->
                    gz.reader(StandardCharsets.UTF_8).readText()
                }
            }

            val obj = JSONObject(jsonString)
            SurpriseData(
                id = obj.optString("id", System.currentTimeMillis().toString()),
                recipientName = obj.getString("n"),
                relationship = try {
                    Relationship.valueOf(obj.optString("r", Relationship.FRIEND.name))
                } catch (_: Exception) {
                    Relationship.FRIEND
                },
                message = obj.getString("m"),
                senderName = obj.optString("s", "A Caring Friend"),
                theme = try {
                    SurpriseTheme.valueOf(obj.optString("t", SurpriseTheme.CUTE.name))
                } catch (_: Exception) {
                    SurpriseTheme.CUTE
                },
                avatarPreset = obj.optString("a", "cake"),
                musicTrack = try {
                    MusicTrack.valueOf(obj.optString("mu", MusicTrack.CLASSIC_BDAY.name))
                } catch (_: Exception) {
                    MusicTrack.CLASSIC_BDAY
                },
                ageOrMilestone = obj.optString("ag", null),
                createdTimestamp = obj.optLong("ts", System.currentTimeMillis())
            )
        } catch (_: Exception) {
            null
        }
    }

    fun buildShareableLink(surprise: SurpriseData): String {
        return "$URL_PREFIX${encodeSurprise(surprise)}"
    }

    fun shareToWhatsApp(context: Context, surprise: SurpriseData) {
        val link = buildShareableLink(surprise)
        val text = "🎁 *Birthday Surprise for ${surprise.recipientName}!* 🎉\n\n" +
                "I created an interactive birthday surprise gift box for you on Wishora!\n\n" +
                "Tap here to open your gift & blow the candles: \n$link\n\n" +
                "— From ${surprise.senderName} ❤️"

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to standard share
            shareGeneric(context, surprise)
        }
    }

    fun shareGeneric(context: Context, surprise: SurpriseData) {
        val link = buildShareableLink(surprise)
        val text = "🎁 Birthday Surprise for ${surprise.recipientName}!\n\n" +
                "Open your animated gift box and celebrate: $link\n\n" +
                "— From ${surprise.senderName}"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Birthday Surprise for ${surprise.recipientName}!")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Birthday Surprise via")
        context.startActivity(shareIntent)
    }

    fun copyLinkToClipboard(context: Context, surprise: SurpriseData) {
        val link = buildShareableLink(surprise)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Wishora Birthday Surprise", link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Surprise link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }
}
