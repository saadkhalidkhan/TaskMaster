/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taskmaster.app.navigation.Routes
import com.taskmaster.app.ui.viewmodel.AppViewModel
import com.taskmaster.feature.auth.ui.screen.LoginScreen
import com.taskmaster.feature.auth.ui.screen.RegisterScreen
import com.taskmaster.feature.tasks.ui.navigation.tasksNavGraph

@Composable
fun TaskMasterNavHost(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel()
) {
    val sessionReady by appViewModel.sessionReady.collectAsState()
    val isLoggedIn by appViewModel.isLoggedIn.collectAsState()

    if (!sessionReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.TASK_LIST else Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.TASK_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.TASK_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        tasksNavGraph(
            navController = navController,
            onLogout = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.TASK_LIST) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}
