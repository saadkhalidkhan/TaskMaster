/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.feature.auth.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.taskmaster.feature.auth.ui.screen.LoginScreen
import com.taskmaster.feature.auth.ui.screen.RegisterScreen

object AuthRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
}

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onAuthenticated: () -> Unit
) {
    composable(AuthRoutes.LOGIN) {
        LoginScreen(
            onLoginSuccess = onAuthenticated,
            onNavigateToRegister = {
                navController.navigate(AuthRoutes.REGISTER)
            }
        )
    }

    composable(AuthRoutes.REGISTER) {
        RegisterScreen(
            onRegisterSuccess = onAuthenticated,
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )
    }
}
