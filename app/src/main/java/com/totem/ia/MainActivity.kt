package com.totem.ia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.totem.ia.ui.SettingsScreen
import com.totem.ia.ui.TotemScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkColorScheme = androidx.compose.material3.darkColorScheme(
                primary = androidx.compose.ui.graphics.Color(0xFF7C4DFF),
                secondary = androidx.compose.ui.graphics.Color(0xFF00E5FF),
                background = androidx.compose.ui.graphics.Color(0xFF0D0D1A),
                surface = androidx.compose.ui.graphics.Color(0xFF1A1A2E),
                onPrimary = androidx.compose.ui.graphics.Color.White,
                onSecondary = androidx.compose.ui.graphics.Color.Black,
                onBackground = androidx.compose.ui.graphics.Color.White,
                onSurface = androidx.compose.ui.graphics.Color.White
            )

            MaterialTheme(colorScheme = darkColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "chat") {
                        composable("chat") {
                            TotemScreen(
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
