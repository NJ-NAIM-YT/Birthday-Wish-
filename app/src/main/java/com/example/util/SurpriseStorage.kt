package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MusicTrack
import com.example.model.Relationship
import com.example.model.SurpriseData
import com.example.model.SurpriseTheme
import org.json.JSONArray
import org.json.JSONObject

class SurpriseStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wishora_surprises_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_SURPRISES = "saved_surprises_json"
    }

    fun getAllSurprises(): List<SurpriseData> {
        val raw = prefs.getString(KEY_SAVED_SURPRISES, null) ?: return emptyList()
        val list = mutableListOf<SurpriseData>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val item = SurpriseData(
                    id = obj.getString("id"),
                    recipientName = obj.getString("recipientName"),
                    relationship = try {
                        Relationship.valueOf(obj.getString("relationship"))
                    } catch (_: Exception) {
                        Relationship.FRIEND
                    },
                    message = obj.getString("message"),
                    senderName = obj.getString("senderName"),
                    theme = try {
                        SurpriseTheme.valueOf(obj.getString("theme"))
                    } catch (_: Exception) {
                        SurpriseTheme.CUTE
                    },
                    photoUri = obj.optString("photoUri", null).takeIf { it != "null" && !it.isNullOrBlank() },
                    avatarPreset = obj.optString("avatarPreset", "cake"),
                    musicTrack = try {
                        MusicTrack.valueOf(obj.getString("musicTrack"))
                    } catch (_: Exception) {
                        MusicTrack.CLASSIC_BDAY
                    },
                    ageOrMilestone = obj.optString("ageOrMilestone", null).takeIf { it != "null" && !it.isNullOrBlank() },
                    createdTimestamp = obj.optLong("createdTimestamp", System.currentTimeMillis())
                )
                list.add(item)
            }
        } catch (_: Exception) {}
        return list.sortedByDescending { it.createdTimestamp }
    }

    fun saveSurprise(surprise: SurpriseData) {
        val current = getAllSurprises().toMutableList()
        current.removeAll { it.id == surprise.id }
        current.add(0, surprise)

        val array = JSONArray()
        for (item in current) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("recipientName", item.recipientName)
                put("relationship", item.relationship.name)
                put("message", item.message)
                put("senderName", item.senderName)
                put("theme", item.theme.name)
                put("photoUri", item.photoUri ?: "")
                put("avatarPreset", item.avatarPreset ?: "cake")
                put("musicTrack", item.musicTrack.name)
                put("ageOrMilestone", item.ageOrMilestone ?: "")
                put("createdTimestamp", item.createdTimestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_SAVED_SURPRISES, array.toString()).apply()
    }

    fun deleteSurprise(id: String) {
        val current = getAllSurprises().toMutableList()
        current.removeAll { it.id == id }

        val array = JSONArray()
        for (item in current) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("recipientName", item.recipientName)
                put("relationship", item.relationship.name)
                put("message", item.message)
                put("senderName", item.senderName)
                put("theme", item.theme.name)
                put("photoUri", item.photoUri ?: "")
                put("avatarPreset", item.avatarPreset ?: "cake")
                put("musicTrack", item.musicTrack.name)
                put("ageOrMilestone", item.ageOrMilestone ?: "")
                put("createdTimestamp", item.createdTimestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_SAVED_SURPRISES, array.toString()).apply()
    }
}
