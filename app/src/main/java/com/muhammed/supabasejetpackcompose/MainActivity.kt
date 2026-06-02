package com.muhammed.supabasejetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import com.muhammed.supabasejetpackcompose.ui.theme.SupabaseJetpackComposeTheme
import com.muhammed.supabasejetpackcompose.presentation.navigation.NotesAppRoot

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SupabaseJetpackComposeTheme {
                NotesAppRoot()
            }
        }
    }
}