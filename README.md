# Wishora — Make Every Birthday Special 🎂🎁✨

**Wishora** is a complete, mobile-first Birthday Surprise Maker application designed for everyone: romantic partners, spouses, parents, siblings, relatives, and friends.

---

## ✨ Features

- **Interactive 3D Gift Box Reveal**:
  - Layered gift box with gentle wobble idle animation.
  - Interactive "Tap to Unwrap" interaction with lid flight, ribbon unwrapping, and haptic feedback.
- **Celebratory Confetti Explosion**:
  - Physics-based particle confetti system bursting with colors matching your selected theme.
- **Animated Birthday Cake & Blowable Candle**:
  - Multi-tier festive birthday cake with piping and milestone age badge.
  - Interactive candle with realistic flickering flame. Tap or blow to extinguish!
  - Smoke puff animation and golden "✨ Wish Granted! ✨" celebration banner with re-light capability.
- **AI Wish Writer (English & Bengali / বাংলা)**:
  - Smart prompt-based birthday wish synthesis tailored to recipient, relationship, and tone (Heartfelt, Romantic, Funny, Poetic, Inspirational, Short & Sweet).
  - 16+ authentic curated English and Bengali greeting templates for instant application.
  - Zero required paid APIs (intelligent offline AI engine with optional Gemini API integration).
- **Personalized Birthday Song Synthesizer**:
  - Real-time polyphonic audio synthesis using Android `AudioTrack` (zero external MP3 assets needed, zero data usage, completely free and offline).
  - Live animated equalizer visualization dancing to the melody.
- **Themes Gallery (6 Bespoke Aesthetics)**:
  - 💖 **Love**: Romantic Red & Rose Petals
  - 🎀 **Cute**: Pastel Pink & Sweet Confetti
  - 👑 **Luxury**: Midnight Black & Champagne Gold
  - 🌸 **Floral**: Botanical Lavender & Blossom
  - 🎉 **Funny**: Vibrant Quirky & Party Memes
  - 🏡 **Family**: Warm Sunset & Cozy Nostalgia
- **Photo Memory & Avatar Frame**:
  - Polaroid photo memory frame using Android Photo Picker (zero broad storage permissions).
  - Built-in festive avatar badges (Cake, Star, Crown, Heart, Gift, Party, Balloons).
- **Download as Video / Story Format & High-Res Card**:
  - 1080x1920 Full HD Celebration Card generation.
  - Save directly to Gallery or export to Instagram Stories / WhatsApp status.
- **URL-Safe Self-Contained Sharing**:
  - Non-sensitive surprise data encoded via URL-safe GZIP Base64.
  - Shareable via WhatsApp, system share sheet, and deep-links (`wishora://surprise?d=...`).
  - Clear transparency note explaining that URL links contain surprise data directly in the link and do not require cloud databases or login.
- **Local Storage & Management**:
  - Saved surprises persisted locally via `SharedPreferences`.

---

## 🚀 Setup & Build Instructions

### Prerequisites
- Android Studio Ladybug (2024.2+) or later
- JDK 17 or JDK 21
- Android SDK 36 (minSdk 24)

### Building the Project
```bash
# Compile and build the debug APK
gradle assembleDebug

# Run Robolectric local unit tests
gradle :app:testDebugUnitTest
```

### Free-Tier Architecture & Hosting
- **Frontend / Client**: 100% Native Jetpack Compose running client-side with Android AudioTrack, Canvas particles, and MediaStore.
- **Data Layer**: Self-contained compressed URL encoding (`GZIP + Base64`) and local on-device persistence. No paid database or cloud hosting needed.
- **AI Engine**: Local intelligent combinatorial synthesizer with seamless fallback, plus optional Google Gemini free-tier API support via `.env` (`GEMINI_API_KEY`).
