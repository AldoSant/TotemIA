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
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.totem.ia.data.SettingsManager
import com.totem.ia.ui.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        setContent {
            val hasSeenOnboarding by settingsManager.hasSeenOnboardingFlow.collectAsState(initial = null)
            val scope = rememberCoroutineScope()

            val darkColorScheme = androidx.compose.material3.darkColorScheme(
                primary = Color(0xFF7C4DFF),
                secondary = Color(0xFF00E5FF),
                background = Color(0xFF0D0D1A),
                surface = Color(0xFF1A1A2E),
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
            )

            MaterialTheme(colorScheme = darkColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasSeenOnboarding == null) return@Surface // Wait for storage

                    val navController = rememberNavController()
                    val startDest = if (hasSeenOnboarding == true) "journeys" else "onboarding"

                    NavHost(navController = navController, startDestination = startDest) {
                        composable("onboarding") {
                            OnboardingScreen(onComplete = {
                                scope.launch {
                                    settingsManager.saveHasSeenOnboarding(true)
                                    navController.navigate("journeys") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            })
                        }
                        composable("journeys") {
                            JourneyListScreen(
                                onJourneyClick = { id -> navController.navigate("journey/$id") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToProgress = { navController.navigate("progress") }
                            )
                        }
                        composable("progress") {
                            ProgressScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("journey/{id}") {
                            JourneyDetailScreen(
                                onBack = { navController.popBackStack() },
                                onStartChapter = { jId, cId -> navController.navigate("episode/$jId/$cId") }
                            )
                        }
                        composable("episode/{journeyId}/{chapterId}") {
                            EpisodePlayerScreen(
                                onClose = { navController.popBackStack() }
                            )
                        }
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
