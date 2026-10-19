package com.example.model

import java.util.UUID

enum class SurpriseTheme(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val backgroundColorHex: Long,
    val surfaceColorHex: Long,
    val accentColorHex: Long,
    val confettiColors: List<Long>
) {
    LOVE(
        title = "Love",
        subtitle = "Romantic Red & Rose Petals",
        emoji = "💖",
        primaryColorHex = 0xFFE91E63,
        secondaryColorHex = 0xFFFF4081,
        backgroundColorHex = 0xFF2A0815,
        surfaceColorHex = 0xFF4A1028,
        accentColorHex = 0xFFFFD700,
        confettiColors = listOf(0xFFFF1493, 0xFFFF69B4, 0xFFFFD700, 0xFFFF4500, 0xFFFFFFFF)
    ),
    CUTE(
        title = "Cute",
        subtitle = "Pastel Pink & Sweet Confetti",
        emoji = "🎀",
        primaryColorHex = 0xFFFF6B8B,
        secondaryColorHex = 0xFFFF8E53,
        backgroundColorHex = 0xFF1C132B,
        surfaceColorHex = 0xFF352055,
        accentColorHex = 0xFF4DEEEA,
        confettiColors = listOf(0xFFFF69B4, 0xFF70D6FF, 0xFFFF70A6, 0xFFFF9770, 0xFFFFD670)
    ),
    LUXURY(
        title = "Luxury",
        subtitle = "Midnight Black & Champagne Gold",
        emoji = "👑",
        primaryColorHex = 0xFFFFD700,
        secondaryColorHex = 0xFFE5A93C,
        backgroundColorHex = 0xFF121214,
        surfaceColorHex = 0xFF232226,
        accentColorHex = 0xFFFFF1C5,
        confettiColors = listOf(0xFFFFD700, 0xFFD4AF37, 0xFFFAF0BE, 0xFFFFFFFF, 0xFFDAA520)
    ),
    FLORAL(
        title = "Floral",
        subtitle = "Botanical Lavender & Blossom",
        emoji = "🌸",
        primaryColorHex = 0xFF9C27B0,
        secondaryColorHex = 0xFFBA68C8,
        backgroundColorHex = 0xFF1A102F,
        surfaceColorHex = 0xFF2E1C4E,
        accentColorHex = 0xFFE1BEE7,
        confettiColors = listOf(0xFFBA68C8, 0xFFF48FB1, 0xFF80CBC4, 0xFFFFE082, 0xFFFFFFFF)
    ),
    FUNNY(
        title = "Funny",
        subtitle = "Vibrant Quirky & Party Memes",
        emoji = "🎉",
        primaryColorHex = 0xFFFF5722,
        secondaryColorHex = 0xFFFFC107,
        backgroundColorHex = 0xFF1B1A3A,
        surfaceColorHex = 0xFF2C2A5E,
        accentColorHex = 0xFF00E676,
        confettiColors = listOf(0xFFFF3D00, 0xFFFFEA00, 0xFF00E676, 0xFF2979FF, 0xFFFF007F)
    ),
    FAMILY(
        title = "Family",
        subtitle = "Warm Sunset & Cozy Nostalgia",
        emoji = "🏡",
        primaryColorHex = 0xFFFB8C00,
        secondaryColorHex = 0xFFFFA726,
        backgroundColorHex = 0xFF231610,
        surfaceColorHex = 0xFF3E271D,
        accentColorHex = 0xFFFFCC80,
        confettiColors = listOf(0xFFFB8C00, 0xFFFFB74D, 0xFFFFE082, 0xFFFF7043, 0xFFFFFFFF)
    )
}

enum class Relationship(val displayName: String, val emoji: String) {
    ROMANTIC_PARTNER("Romantic Partner", "❤️"),
    SPOUSE("Spouse / Wife / Husband", "💍"),
    PARENT("Parent / Mom / Dad", "🌟"),
    SIBLING("Sibling / Brother / Sister", "🤝"),
    BEST_FRIEND("Best Friend", "✨"),
    RELATIVE("Relative / Cousin", "🌻"),
    FRIEND("Friend", "🎈"),
    COLLEAGUE("Colleague / Mentor", "💼")
}

enum class MusicTrack(val title: String, val description: String, val icon: String) {
    CLASSIC_BDAY("Classic Birthday", "Traditional festive acoustic chime", "🎂"),
    MUSIC_BOX("Music Box Melody", "Gentle, sparkling music box lullaby", "✨"),
    CELEBRATION_POP("Celebration Pop", "Upbeat rhythmic synth celebration", "🎉"),
    ROMANTIC_PIANO("Romantic Harmony", "Warm harmonic piano celebration", "💖"),
    PERSONALIZED_SONG("Personalized Birthday Song", "Custom tune synthesized for the recipient", "🎵")
}

enum class WishTone(val displayName: String, val emoji: String) {
    HEARTFELT("Heartfelt & Loving", "💖"),
    ROMANTIC("Deeply Romantic", "🌹"),
    FUNNY("Humorous & Playful", "😂"),
    POETIC("Poetic & Dreamy", "✨"),
    INSPIRATIONAL("Inspirational & Uplifting", "🚀"),
    SHORT_SWEET("Short & Sweet", "🍰")
}

enum class WishLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    BENGALI("Bengali (বাংলা)", "bn")
}

data class SurpriseData(
    val id: String = UUID.randomUUID().toString(),
    val recipientName: String,
    val relationship: Relationship = Relationship.FRIEND,
    val message: String,
    val senderName: String,
    val theme: SurpriseTheme = SurpriseTheme.CUTE,
    val photoUri: String? = null,
    val avatarPreset: String? = "cake",
    val musicTrack: MusicTrack = MusicTrack.CLASSIC_BDAY,
    val ageOrMilestone: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)
