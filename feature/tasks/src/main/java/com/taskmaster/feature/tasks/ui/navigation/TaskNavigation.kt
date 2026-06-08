/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.feature.tasks.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.taskmaster.feature.tasks.ui.screen.CreateEditTaskScreen
import com.taskmaster.feature.tasks.ui.screen.TaskDetailScreen
import com.taskmaster.feature.tasks.ui.screen.TaskListScreen

object TaskRoutes {
    const val TASK_LIST = "task_list"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val CREATE_TASK = "create_task"
    const val EDIT_TASK = "edit_task/{taskId}"

    fun taskDetail(taskId: Int) = "task_detail/$taskId"
    fun editTask(taskId: Int) = "edit_task/$taskId"
}

fun NavGraphBuilder.tasksNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    composable(TaskRoutes.TASK_LIST) {
        TaskListScreen(
            onNavigateToTaskDetail = { taskId ->
                navController.navigate(TaskRoutes.taskDetail(taskId))
            },
            onNavigateToCreateTask = {
                navController.navigate(TaskRoutes.CREATE_TASK)
            },
            onLogout = onLogout
        )
    }

    composable(TaskRoutes.TASK_DETAIL) { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull() ?: 0
        TaskDetailScreen(
            taskId = taskId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToEditTask = { id ->
                navController.navigate(TaskRoutes.editTask(id))
            }
        )
    }

    composable(TaskRoutes.CREATE_TASK) {
        CreateEditTaskScreen(
            onTaskSaved = {
                navController.popBackStack()
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    composable(TaskRoutes.EDIT_TASK) { backStackEntry ->
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
