package com.taskmaster.feature.auth.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.taskmaster.feature.auth.ui.screen.LoginScreen
import com.taskmaster.feature.auth.ui.screen.RegisterScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable("login") {
        LoginScreen(
            onLoginSuccess = {
                // Navigate to main app
                navController.navigate("main_graph") {
                    popUpTo("auth_graph") { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate("register")
            }
        )
    }
    
    composable("register") {
        RegisterScreen(
            onRegisterSuccess = {
                // Navigate to main app
                navController.navigate("main_graph") {
                    popUpTo("auth_graph") { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )
    }
}
