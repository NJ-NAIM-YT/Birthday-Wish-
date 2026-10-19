package com.example.ai

import com.example.model.Relationship
import com.example.model.WishLanguage
import com.example.model.WishTone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

object AiWishWriter {

    data class WishTemplate(
        val language: WishLanguage,
        val tone: WishTone,
        val relationship: Relationship?,
        val text: String,
        val title: String
    )

    val templates: List<WishTemplate> = listOf(
        // Bengali Templates
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.HEARTFELT,
            relationship = Relationship.ROMANTIC_PARTNER,
            title = "হৃদয়ের সুর",
            text = "শুভ জন্মদিন আমার জীবনের সবচেয়ে মিষ্টি মানুষটি! তোমার হাসিতে আমার পৃথিবী আলোকিত হয়। সৃষ্টিকর্তা তোমাকে সব সুখ আর শান্তিতে ভরিয়ে দিক। সারাজীবন এভাবেই পাশে থেকো। অনেক ভালোবাসি তোমাকে! ❤️"
        ),
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.ROMANTIC,
            relationship = Relationship.SPOUSE,
            title = "অফুরন্ত ভালোবাসা",
            text = "প্রিয়তম, জন্মদিনের অজস্র ভালোবাসা ও শুভকামনা! তোমার সাথে কাটানো প্রতিটি মুহূর্ত আমার কাছে অমূল্য উপহার। তুমি আমার শক্তি, আমার হাসি আর আমার সুখের ঠিকানা। শুভ জন্মদিন জীবনসঙ্গী! 💍✨"
        ),
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.HEARTFELT,
            relationship = Relationship.PARENT,
            title = "স্নেহ ও শ্রদ্ধা",
            text = "শুভ জন্মদিন প্রিয়তম মা/বাবা! তোমার নিঃস্বার্থ ভালোবাসা, ত্যাগ আর স্নেহের ছায়াতলে আমি আজকের এই আমি হতে পেরেছি। সৃষ্টিকর্তার কাছে তোমার সুস্থতা ও দীর্ঘায়ু প্রার্থনা করি। অনেক অনেক শ্রদ্ধা ও ভালোবাসা। 🌸🙏"
        ),
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.FUNNY,
            relationship = Relationship.BEST_FRIEND,
            title = "পাগলামির পার্টনার",
            text = "শুভ জন্মদিন দোস্ত! বয়স তো এক বছর বেড়ে গেল, কিন্তু বুদ্ধি এখনো সেই আগের মতোই রয়ে গেছে! যাই হোক, তুই আছিস বলেই জীবনটা এত মজার। আজকের মিষ্টি আর ট্রিট কবে দিচ্ছিস সেটা দ্রুত জানিয়ে দিস! 🎂🥳"
        ),
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.POETIC,
            relationship = null,
            title = "ফুলের সুবাসে শুভেচ্ছা",
            text = "একটি নতুন বছর, এক নতুন সকাল, তোমার জীবনে বয়ে আনুক বসন্তের রঙিন স্বপ্নজাল। হৃদয়ের গভীর থেকে জানাই শুভ জন্মদিনের স্নিগ্ধ শুভেচ্ছা! প্রতিটি দিন কাটুক আনন্দে ও ভালোবাসায়। 🌺✨"
        ),
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.INSPIRATIONAL,
            relationship = null,
            title = "স্বপ্নের উড়ান",
            text = "শুভ জন্মদিন! জীবনের এই নতুন বছরে তোমার প্রতিটি ইচ্ছা আর স্বপ্ন যেন বাস্তবে রূপ নেয়। সাহসের সাথে এগিয়ে যাও নিজের লক্ষ্যে, জয় তোমার হবেই। সর্বদা খুশিতে থেকো! 🚀🌟"
        ),
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.HEARTFELT,
            relationship = Relationship.SIBLING,
            title = "ভাই-বোনের বন্ধন",
            text = "শুভ জন্মদিন আমার প্রিয় ভাই/বোন! ছোটবেলার মারামারি থেকে শুরু করে সব সিক্রেট ভাগ করে নেওয়া—তুই সবসময় আমার সেরা বন্ধু। অনেক ভালো থাকিস, উজ্জ্বল হোক তোর ভবিষ্যৎ! 🎈💛"
        ),
        WishTemplate(
            language = WishLanguage.BENGALI,
            tone = WishTone.SHORT_SWEET,
            relationship = null,
            title = "ছোট্ট সুন্দর শুভেচ্ছা",
            text = "শুভ জন্মদিন! জীবন হোক রঙিন, আনন্দ হোক সীমাহীন, আর মিষ্টি হোক তোমার প্রতিটি দিন! 🍰🎉"
        ),

        // English Templates
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.ROMANTIC,
            relationship = Relationship.ROMANTIC_PARTNER,
            title = "To My Whole World",
            text = "Happy Birthday to the one who makes my heart skip a beat every single day. Being loved by you is the sweetest blessing of my life. May today bring you as much happiness, warmth, and magic as you bring into my world. I love you endlessly! 💖"
        ),
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.HEARTFELT,
            relationship = Relationship.SPOUSE,
            title = "My Forever Love",
            text = "Happy Birthday to my wonderful partner in life and love! Through every season, every laugh, and every adventure, you are my home and my greatest joy. Here's to celebrating you today and cherishing you always. 💍✨"
        ),
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.HEARTFELT,
            relationship = Relationship.PARENT,
            title = "With Endless Gratitude",
            text = "Happy Birthday, Mom/Dad! Thank you for your unconditional love, your patient guidance, and your warm embrace that always makes everything okay. Wishing you endless health, joy, and peace today and always. Love you more than words can say! 🌟"
        ),
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.FUNNY,
            relationship = Relationship.BEST_FRIEND,
            title = "Aging Like Fine Wine",
            text = "Happy Birthday to my favorite partner in crime! Don't worry about getting older—you're just leveling up in wisdom (and questionable life choices). Remember: calories from birthday cake don't count today. Now let's celebrate! 🎂🍕🎉"
        ),
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.POETIC,
            relationship = null,
            title = "Starlight & Dreams",
            text = "Like the quiet brilliance of stars at midnight, may your special day sparkle with wonder, peace, and unforgettable grace. Wishing you a year where every dream blossoms into sweet reality. Happy Birthday! 🌙✨"
        ),
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.INSPIRATIONAL,
            relationship = null,
            title = "Your Next Masterpiece",
            text = "Happy Birthday! Today marks the opening of a brand new chapter filled with untapped potential, bold dreams, and thrilling possibilities. Step forward with courage and let your light illuminate the world! 🚀🌟"
        ),
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.HEARTFELT,
            relationship = Relationship.SIBLING,
            title = "Always By Your Side",
            text = "Happy Birthday to someone who has been there through childhood scraped knees and adulthood's biggest moments! I am so grateful to call you my sibling and my confidant. Wishing you the happiest year yet! 🎈💛"
        ),
        WishTemplate(
            language = WishLanguage.ENGLISH,
            tone = WishTone.SHORT_SWEET,
            relationship = null,
            title = "Sweet & Sparkling",
            text = "Wishing you a birthday as bright, joyful, and sweet as you are! May all your heartfelt wishes come true today! 🍰🎉"
        )
    )

    suspend fun generateWish(
        recipientName: String,
        relationship: Relationship,
        tone: WishTone,
        language: WishLanguage,
        apiKey: String = ""
    ): String = withContext(Dispatchers.IO) {
        val cleanName = recipientName.trim().ifEmpty { "there" }

        // Attempt Gemini API if key is available
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val generated = callGeminiApi(cleanName, relationship, tone, language, apiKey)
                if (generated.isNotBlank()) {
                    return@withContext generated
                }
            } catch (_: Exception) {
                // Graceful fallback to offline smart engine
            }
        }

        // Offline AI smart wish synthesizer
        generateOfflineWish(cleanName, relationship, tone, language)
    }

    private fun generateOfflineWish(
        name: String,
        relationship: Relationship,
        tone: WishTone,
        language: WishLanguage
    ): String {
        return if (language == WishLanguage.BENGALI) {
            when (tone) {
                WishTone.ROMANTIC -> {
                    val openers = listOf(
                        "প্রিয় $name, শুভ জন্মদিন!",
                        "আমার হৃদয়ের সবচেয়ে ভালোবাসার মানুষ $name, শুভ জন্মদিন!",
                        "শুভ জন্মদিন আমার প্রিয় $name!"
                    )
                    val bodies = listOf(
                        "তোমার মিষ্টি হাসি আমার প্রতিটি দিনকে রঙিন করে তোলে। তোমার ভালোবাসাই আমার সবচেয়ে বড় প্রাপ্তি।",
                        "তুমি পাশে আছো বলেই জীবনটা এত অর্থবহ ও মায়াময়। সৃষ্টিকর্তা তোমাকে সর্বদা হাসিখুশি রাখুক।",
                        "তোমার সাথে কাটানো প্রতিটি মুহূর্ত আমার হৃদয়ে স্বর্ণাক্ষরে লেখা থাকবে। তুমি আমার জীবনের সবচেয়ে সুন্দর বিস্ময়।"
                    )
                    val closers = listOf(
                        "সারাজীবন এভাবেই পাশে থেকো। অনেক অনেক ভালোবাসি তোমাকে! 💖🌹",
                        "আজকের দিনটি তোমার মতোই সুন্দর ও মিষ্টি কাটুক। অফুরন্ত ভালোবাসা রইল! ✨❤️",
                        "তোমার প্রতিটি স্বপ্ন পূর্ণ হোক। শুভ জন্মদিন আমার প্রিয়তম! 🥂💍"
                    )
                    "${openers.random()} ${bodies.random()} ${closers.random()}"
                }
                WishTone.FUNNY -> {
                    val jokes = listOf(
                        "শুভ জন্মদিন $name! বয়স তো আরও এক বছর বেড়ে গেল, কিন্তু ক্যান্ডেল নেভানোর শক্তি কি এখনো আছে? 😜",
                        "হেই $name, শুভ জন্মদিন! বুড়ো হওয়ার ক্লাবে তোমাকে স্বাগতম! তবে মন খারাপ করো না, আজকের কেকটা কিন্তু একাই সাবাড় করবে না! 🎂😂",
                        "শুভ জন্মদিন দোস্ত $name! বয়স বাড়লেও পাগলামি যেন কখনোই কমে না। আজকের বিলটা কিন্তু তোমারই দেওয়ার কথা! 🍕🎉"
                    )
                    val wishes = listOf(
                        "হাসতে হাসতে পেট ফুলে যাক আর জন্মদিনের উপহারে বাড়ি ভরে উঠুক!",
                        "সৃষ্টিকর্তা তোমাকে দীর্ঘায়ু আর প্রচুর ধৈর্য্য দান করুক আমাকে সহ্য করার জন্য!",
                        "এই দিনটি উপভোগ করো মন ভরে। হ্যাপি বার্থডে!"
                    )
                    "${jokes.random()} ${wishes.random()} 🥳🎈"
                }
                WishTone.POETIC -> {
                    val poems = listOf(
                        "শুভ জন্মদিন $name! ভোরের প্রথম আলোর মতো তোমার জীবন হোক স্নিগ্ধ, আর সন্ধ্যার তারার মতো উজ্জ্বল।",
                        "ফুলের সুভাষ আর পাখির গানে মুখরিত হোক তোমার আজকের দিনটি, প্রিয় $name।",
                        "একটি নতুন বছর, এক নতুন সকাল, $name তোমার জীবনে আনুক রঙিন স্বপ্নের মায়াজাল।"
                    )
                    "${poems.random()} হৃদয়ের গভীরতম কোণ থেকে তোমার জন্য জানাই অবারিত ভালোবাসা ও শুভকামনা! 🌸✨"
                }
                WishTone.INSPIRATIONAL -> {
                    val inspo = listOf(
                        "শুভ জন্মদিন $name! প্রতিটি জন্মদিন একটি নতুন সুযোগ নিজের সেরা সংস্করণ হয়ে ওঠার।",
                        "প্রিয় $name, তুমি অসীম সম্ভাবনার অধিকারী। সাহস আর আত্মবিশ্বাস নিয়ে এগিয়ে যাও তোমার স্বপ্নের ঠিকানায়।",
                        "শুভ জন্মদিন $name! জীবনের সব বাধা পেরিয়ে তুমি পৌঁছে যাও সাফল্যের শীর্ষে।"
                    )
                    "${inspo.random()} তোমার আগামী দিনগুলো হোক গৌরবময় ও আনন্দমুখর! 🚀🌟"
                }
                WishTone.SHORT_SWEET -> {
                    "শুভ জন্মদিন $name! দিনটি কাটুক সীমাহীন আনন্দ, ভালোবাসা আর সুখে। অনেক অনেক শুভকামনা! 🍰🎉"
                }
                else -> { // HEARTFELT
                    val heartfelts = listOf(
                        "প্রিয় $name, তোমাকে জানাই জন্মদিনের আন্তরিক শুভেচ্ছা ও পরম ভালোবাসা।",
                        "শুভ জন্মদিন $name! তোমার মতো একজন ভালো মনের মানুষ আমার জীবনে থাকা সত্যি এক পরম আশীর্বাদ।",
                        "আজকের এই বিশেষ দিনে তোমার জন্য রইল হৃদয়ের সবটুকু ভালোবাসা ও দোয়া, $name।"
                    )
                    val blessings = listOf(
                        "সৃষ্টিকর্তা তোমার জীবনকে অনাবিল সুখ, শান্তি আর সুস্বাস্থ্যে পূর্ণ করে দিন।",
                        "তোমার মুখের হাসি যেন কখনো ম্লান না হয়। জীবনের প্রতিটি ধাপে যেন বিজয় তোমার সঙ্গী হয়।",
                        "তোমার আগামীর প্রতিটি দিন হোক মধুর ও আনন্দময়।"
                    )
                    "${heartfelts.random()} ${blessings.random()} শুভ জন্মদিন! 🎂💛"
                }
            }
        } else {
            // English
            when (tone) {
                WishTone.ROMANTIC -> {
                    val openers = listOf(
                        "Happy Birthday to the love of my life, $name! 💖",
                        "To my darling $name, wishing you the happiest birthday! 🌹",
                        "Happy Birthday, beautiful soul $name! ✨"
                    )
                    val bodies = listOf(
                        "Every moment spent with you feels like a beautiful dream come true. You make my world infinitely brighter.",
                        "Your warmth, your laughter, and your gentle heart mean everything to me. I fall in love with you more each day.",
                        "Thank you for being my constant, my joy, and my greatest adventure."
                    )
                    val closers = listOf(
                        "May today bring you as much pure happiness as you bring into my life. I love you endlessly! 💕",
                        "Here's to celebrating you today and holding your hand through all our tomorrows. Forever yours! 🥂",
                        "Wishing you a year filled with sweet kisses, deep laughter, and magical memories. Happy Birthday! 💫"
                    )
                    "${openers.random()} ${bodies.random()} ${closers.random()}"
                }
                WishTone.FUNNY -> {
                    val funnies = listOf(
                        "Happy Birthday $name! Don't worry about getting another year older—you're still way younger than you'll be next year! 😂",
                        "Happy Birthday, $name! They say wisdom comes with age... but hey, you're the proof that there are always exceptions! 😜",
                        "Cheers to $name on your birthday! Another 365 days of tolerating me like a champ. You deserve an award (and cake)! 🎂🍕"
                    )
                    val punchlines = listOf(
                        "May your day be filled with lots of presents and zero adult responsibilities!",
                        "Let's eat cake until we can't move. Have an epic birthday celebration!",
                        "Remember: calories consumed on your birthday go straight to your smile. Enjoy! 🎉"
                    )
                    "${funnies.random()} ${punchlines.random()}"
                }
                WishTone.POETIC -> {
                    val poetic = listOf(
                        "Like starlight dancing on quiet water, may your special day sparkle with calm grace and timeless joy, dear $name.",
                        "Another circle around the sun, another chapter written in gold. Happy Birthday, $name.",
                        "May gentle winds carry happiness to your doorstep, and may every tomorrow bloom with hope, $name."
                    )
                    "${poetic.random()} Wishing you peace, beauty, and unmeasured bliss on your birthday! 🌙✨"
                }
                WishTone.INSPIRATIONAL -> {
                    val inspo = listOf(
                        "Happy Birthday, $name! This year is your canvas—paint it with bold choices, courageous dreams, and unapologetic passion.",
                        "To $name: You are capable of moving mountains and creating wonders. Never forget how powerful your light is.",
                        "Happy Birthday $name! Embrace this new age with an open heart. The best chapters of your journey are unfolding right now."
                    )
                    "${inspo.random()} Go shine brightly and make this your most remarkable year yet! 🚀🌟"
                }
                WishTone.SHORT_SWEET -> {
                    "Happy Birthday, $name! Wishing you a day as wonderful, sweet, and bright as you are! 🍰🎉"
                }
                else -> { // HEARTFELT
                    val heartfelt = listOf(
                        "Happy Birthday, $name! I feel so blessed and grateful to have you in my life as my ${relationship.displayName.lowercase()}.",
                        "Wishing you the most wonderful birthday, $name! Your kindness and warmth brighten every room you enter.",
                        "Happy Birthday, $name! Today is all about celebrating the remarkable person you are."
                    )
                    val blessings = listOf(
                        "May the year ahead bring you robust health, great triumphs, and endless moments of pure happiness.",
                        "May your heart be filled with peace, and may all the love you give out return to you tenfold.",
                        "May all your dearest hopes and dreams come to life this year."
                    )
                    "${heartfelt.random()} ${blessings.random()} Have a fantastic celebration! 🎂💛"
                }
            }
        }
    }

    private fun callGeminiApi(
        name: String,
        relationship: Relationship,
        tone: WishTone,
        language: WishLanguage,
        apiKey: String
    ): String {
        val langInstruction = if (language == WishLanguage.BENGALI) "Bengali (বাংলা script)" else "English"
        val prompt = "Write a creative, personalized birthday wish in $langInstruction for $name who is my ${relationship.displayName}. " +
                "The tone must be ${tone.displayName}. Make it around 3-4 heartfelt sentences with warm celebratory emojis. Return ONLY the birthday message text, nothing else."

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 5000
        conn.readTimeout = 7000

        OutputStreamWriter(conn.outputStream).use { it.write(jsonBody.toString()) }

        if (conn.responseCode == 200) {
            val response = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
            val resObj = JSONObject(response)
            val candidates = resObj.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return text.trim()
                    }
                }
            }
        }
        return ""
    }
}
