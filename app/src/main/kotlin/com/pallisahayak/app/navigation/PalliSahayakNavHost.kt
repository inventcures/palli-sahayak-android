package com.pallisahayak.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pallisahayak.feature.home.ui.AshaDashboardScreen
import com.pallisahayak.feature.onboarding.ui.LanguageSelectScreen
import com.pallisahayak.feature.onboarding.ui.PinSetupScreen
import com.pallisahayak.feature.onboarding.ui.RoleSelectScreen
import com.pallisahayak.feature.query.ui.VoiceQueryScreen

@Composable
fun PalliSahayakNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "onboarding_language") {

        composable("onboarding_language") {
            LanguageSelectScreen(
                onNext = { navController.navigate("onboarding_role") },
            )
        }

        composable("onboarding_role") {
            RoleSelectScreen(
                onNext = { navController.navigate("onboarding_pin") },
            )
        }

        composable("onboarding_pin") {
            PinSetupScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding_language") { inclusive = true }
                    }
                },
            )
        }

        composable("home") {
            AshaDashboardScreen(
                onVoiceQuery = { navController.navigate("voice_query") },
                onPatientClick = { patientId -> /* Phase 4 */ },
                onSettings = { /* Phase 7 */ },
            )
        }

        composable("voice_query") {
            VoiceQueryScreen()
        }
    }
}
