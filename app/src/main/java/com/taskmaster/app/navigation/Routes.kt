/**
 * @author Saad Khan
 * @date June 2025
 */
package com.taskmaster.app.navigation

import com.taskmaster.feature.tasks.ui.navigation.TaskRoutes

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val TASK_LIST = TaskRoutes.TASK_LIST
    const val TASK_DETAIL = TaskRoutes.TASK_DETAIL
    const val CREATE_TASK = TaskRoutes.CREATE_TASK
    const val EDIT_TASK = TaskRoutes.EDIT_TASK

    fun taskDetail(taskId: Int) = TaskRoutes.taskDetail(taskId)
    fun editTask(taskId: Int) = TaskRoutes.editTask(taskId)
}
