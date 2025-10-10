package com.taskmaster.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taskmaster.feature.auth.ui.screen.LoginScreen
import com.taskmaster.feature.auth.ui.screen.RegisterScreen
import com.taskmaster.feature.tasks.ui.screen.TaskListScreen
import com.taskmaster.feature.tasks.ui.screen.CreateEditTaskScreen

@Composable
fun TaskMasterNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("tasks") {
                        popUpTo("login") { inclusive = true }
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
                    navController.navigate("tasks") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("tasks") {
            TaskListScreen(
                onNavigateToCreateTask = {
                    navController.navigate("create_task")
                },
                onNavigateToTaskDetail = { taskId ->
                    // Navigate to task detail screen
                }
            )
        }
        
        composable("create_task") {
            CreateEditTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTaskSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}
