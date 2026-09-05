package com.example.composegallery

import android.os.Bundle
import timber.log.Timber
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.composegallery.core.navigation.MainAppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity: onCreate called")
        enableEdgeToEdge()
        setContent {
            MainAppNavigation()
        }
    }
}
