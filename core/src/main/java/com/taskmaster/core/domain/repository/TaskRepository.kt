/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.core.domain.repository

import com.taskmaster.core.common.Result
import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.model.TaskStatus
import com.taskmaster.core.domain.model.TaskCategory
import com.taskmaster.core.domain.model.TaskPriority
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasks(page: Int = 1, pageSize: Int = 20): Result<List<Task>>
    suspend fun getTaskById(id: String): Result<Task?>
    suspend fun createTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun deleteTask(id: String): Result<Unit>
    suspend fun getTasksByStatus(status: TaskStatus): Result<List<Task>>
    suspend fun getTasksByCategory(category: TaskCategory): Result<List<Task>>
    suspend fun getTasksByPriority(priority: TaskPriority): Result<List<Task>>
    suspend fun searchTasks(query: String): Result<List<Task>>
    suspend fun getTasksByDateRange(startDate: Long, endDate: Long): Result<List<Task>>
    fun observeTasks(): Flow<List<Task>>
    suspend fun getTaskStatistics(): Result<Map<String, Int>>
}
