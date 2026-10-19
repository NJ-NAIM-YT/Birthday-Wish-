package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.SurpriseData
import com.example.model.SurpriseTheme
import com.example.ui.screens.CreateSurpriseScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SurpriseRevealScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.SurpriseEncoder
import com.example.util.SurpriseStorage

sealed class AppScreen {
    data object Home : AppScreen()
    data class Create(val initialTheme: SurpriseTheme = SurpriseTheme.LOVE) : AppScreen()
    data class Reveal(val surprise: SurpriseData) : AppScreen()
}

class MainActivity : ComponentActivity() {

    private var initialSurpriseData: SurpriseData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if opened via deep link / web link
        handleIncomingIntent(intent)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F0A19)
                ) {
                    WishoraApp(initialSurprise = initialSurpriseData)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            val encodedString = data.toString()
            val decoded = SurpriseEncoder.decodeSurprise(encodedString)
            if (decoded != null) {
                initialSurpriseData = decoded
                SurpriseStorage(this).saveSurprise(decoded)
            }
        }
    }
}

@Composable
fun WishoraApp(initialSurprise: SurpriseData?) {
    var currentScreen by remember {
        mutableStateOf<AppScreen>(
            if (initialSurprise != null) AppScreen.Reveal(initialSurprise) else AppScreen.Home
        )
    }

    when (val screen = currentScreen) {
        is AppScreen.Home -> {
            HomeScreen(
                onCreateSurprise = { theme ->
                    currentScreen = AppScreen.Create(theme)
                },
                onOpenSurprise = { surprise ->
                    currentScreen = AppScreen.Reveal(surprise)
                }
            )
        }

        is AppScreen.Create -> {
            CreateSurpriseScreen(
                initialTheme = screen.initialTheme,
                onSurpriseCreated = { surprise ->
                    currentScreen = AppScreen.Reveal(surprise)
                },
                onNavigateBack = {
                    currentScreen = AppScreen.Home
                }
            )
        }

        is AppScreen.Reveal -> {
            SurpriseRevealScreen(
                surprise = screen.surprise,
                onNavigateBack = {
                    currentScreen = AppScreen.Home
                }
            )
        }
    }
}
