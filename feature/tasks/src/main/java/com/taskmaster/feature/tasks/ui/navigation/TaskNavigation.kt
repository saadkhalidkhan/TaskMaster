package com.taskmaster.feature.tasks.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.taskmaster.feature.tasks.ui.screen.CreateEditTaskScreen
import com.taskmaster.feature.tasks.ui.screen.TaskDetailScreen
import com.taskmaster.feature.tasks.ui.screen.TaskListScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable("task_list") {
        TaskListScreen(
            onNavigateToTaskDetail = { taskId ->
                navController.navigate("task_detail/$taskId")
            },
            onNavigateToCreateTask = {
                navController.navigate("create_task")
            }
        )
    }
    
    composable("task_detail/{taskId}") { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull() ?: 0
        TaskDetailScreen(
            taskId = taskId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToEditTask = { taskId ->
                navController.navigate("edit_task/$taskId")
            }
        )
    }
    
    composable("create_task") {
        CreateEditTaskScreen(
            onTaskSaved = {
                navController.popBackStack()
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
    
    composable("edit_task/{taskId}") { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull() ?: 0
        CreateEditTaskScreen(
            taskId = taskId,
            onTaskSaved = {
                navController.popBackStack()
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
