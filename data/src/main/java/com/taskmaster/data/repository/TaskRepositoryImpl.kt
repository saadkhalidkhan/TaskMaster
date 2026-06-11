/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.data.repository

import com.taskmaster.core.common.Result
import com.taskmaster.core.data.local.TokenManager
import com.taskmaster.core.domain.model.CreateTaskRequest
import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.model.TaskCategory
import com.taskmaster.core.domain.model.TaskPriority
import com.taskmaster.core.domain.model.TaskStatus
import com.taskmaster.core.domain.model.UpdateTaskRequest
import com.taskmaster.core.domain.repository.TaskRepository
import com.taskmaster.data.mapper.toDomain
import com.taskmaster.data.mapper.toEntity
import com.taskmaster.database.dao.TaskDao
import com.taskmaster.database.entity.TaskEntity
import com.taskmaster.networking.api.TaskMasterApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMasterApi: TaskMasterApi,
    private val tokenManager: TokenManager
) : TaskRepository {

    override suspend fun getTasks(page: Int, pageSize: Int): Result<List<Task>> {
        return try {
            val response = taskMasterApi.getTasks(page, pageSize)
            if (response.success) {
                val tasks = response.data?.data ?: emptyList()
                tasks.forEach { taskDao.insertTask(it.toEntity()) }
                Result.Success(tasks)
            } else {
                Result.Error(response.message ?: "Failed to fetch tasks")
            }
        } catch (e: Exception) {
            getLocalTasksOrError(e.message ?: "Network error")
        }
    }

    override suspend fun getTaskById(id: String): Result<Task?> {
        return try {
            val response = taskMasterApi.getTaskById(id)
            if (response.success) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "Failed to fetch task")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            val response = taskMasterApi.createTask(
                CreateTaskRequest(
                    title = task.title,
                    description = task.description,
                    dueDate = task.dueDate,
                    priority = task.priority,
                    category = task.category
                )
            )
            if (response.success) {
                response.data?.let { createdTask ->
                    taskDao.insertTask(createdTask.toEntity())
                    Result.Success(createdTask)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to create task")
            }
        } catch (e: Exception) {
            saveTaskLocally(task)
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return try {
            val response = taskMasterApi.updateTask(
                task.taskId.toString(),
                UpdateTaskRequest(
                    title = task.title,
                    description = task.description,
                    dueDate = task.dueDate,
                    isCompleted = task.isCompleted,
                    priority = task.priority,
                    status = task.status,
                    category = task.category
                )
            )
            if (response.success) {
                response.data?.let { updatedTask ->
                    taskDao.updateTask(updatedTask.toEntity())
                    Result.Success(updatedTask)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to update task")
            }
        } catch (e: Exception) {
            updateTaskLocally(task)
        }
    }

    override suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            val response = taskMasterApi.deleteTask(id)
            if (response.success) {
                taskDao.deleteTaskById(id.toInt())
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to delete task")
            }
        } catch (e: Exception) {
            deleteTaskLocally(id)
        }
    }

    override suspend fun getTasksByStatus(status: TaskStatus): Result<List<Task>> {
        return try {
            val response = taskMasterApi.getTasksByStatus(status.name.lowercase())
            if (response.success) {
                Result.Success(response.data ?: emptyList())
            } else {
                Result.Error(response.message ?: "Failed to fetch tasks by status")
            }
        } catch (e: Exception) {
            getLocalTasksByStatusOrError(status, e.message ?: "Network error")
        }
    }

    override suspend fun getTasksByCategory(category: TaskCategory): Result<List<Task>> {
        return try {
            val response = taskMasterApi.getTasksByCategory(category.name.lowercase())
            if (response.success) {
                Result.Success(response.data ?: emptyList())
            } else {
                Result.Error(response.message ?: "Failed to fetch tasks by category")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getTasksByPriority(priority: TaskPriority): Result<List<Task>> {
        return try {
            val response = taskMasterApi.getTasksByPriority(priority.name.lowercase())
            if (response.success) {
                Result.Success(response.data ?: emptyList())
            } else {
                Result.Error(response.message ?: "Failed to fetch tasks by priority")
            }
        } catch (e: Exception) {
            getLocalTasksByPriorityOrError(priority, e.message ?: "Network error")
        }
    }

    override suspend fun searchTasks(query: String): Result<List<Task>> {
        return try {
            val response = taskMasterApi.searchTasks(query)
            if (response.success) {
                Result.Success(response.data ?: emptyList())
            } else {
                Result.Error(response.message ?: "Failed to search tasks")
            }
        } catch (e: Exception) {
            getLocalSearchTasksOrError(query, e.message ?: "Network error")
        }
    }

    override suspend fun getTasksByDateRange(startDate: Long, endDate: Long): Result<List<Task>> {
        return try {
            val response = taskMasterApi.getTasksByDateRange(startDate, endDate)
            if (response.success) {
                Result.Success(response.data ?: emptyList())
            } else {
                Result.Error(response.message ?: "Failed to fetch tasks by date range")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override fun observeTasks(): Flow<List<Task>> {
        val userId = tokenManager.getUserId() ?: return flowOf(emptyList())
        return taskDao.getTasksForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTaskById(taskId: Int): Flow<Task?> {
        return taskDao.getTaskById(taskId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getTaskStatistics(): Result<Map<String, Int>> {
        return try {
            val response = taskMasterApi.getTaskStatistics()
            if (response.success) {
                response.data?.let { stats ->
                    Result.Success(
                        mapOf(
                            "totalTasks" to stats.totalTasks,
                            "completedTasks" to stats.completedTasks,
                            "pendingTasks" to stats.pendingTasks,
                            "overdueTasks" to stats.overdueTasks
                        )
                    )
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to fetch task statistics")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    private suspend fun getLocalTasksOrError(message: String): Result<List<Task>> {
        val userId = tokenManager.getUserId() ?: return Result.Error(message)
        return try {
            val tasks = taskDao.getTasksForUser(userId).first().map { it.toDomain() }
            Result.Success(tasks)
        } catch (e: Exception) {
            Result.Error(message)
        }
    }

    private suspend fun saveTaskLocally(task: Task): Result<Task> {
        return try {
            taskDao.insertTask(task.toEntity())
            Result.Success(task)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save task locally")
        }
    }

    private suspend fun updateTaskLocally(task: Task): Result<Task> {
        return try {
            taskDao.updateTask(task.toEntity())
            Result.Success(task)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update task locally")
        }
    }

    private suspend fun deleteTaskLocally(id: String): Result<Unit> {
        return try {
            taskDao.deleteTaskById(id.toInt())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete task locally")
        }
    }

    private suspend fun getLocalSearchTasksOrError(query: String, message: String): Result<List<Task>> {
        val userId = tokenManager.getUserId() ?: return Result.Error(message)
        return try {
            val tasks = taskDao.searchTasksLocal(userId, query).map { it.toDomain() }
            Result.Success(tasks)
        } catch (e: Exception) {
            Result.Error(message)
        }
    }

    private suspend fun getLocalTasksByStatusOrError(status: TaskStatus, message: String): Result<List<Task>> {
        val userId = tokenManager.getUserId() ?: return Result.Error(message)
        return try {
            val tasks = taskDao.getTasksByStatusLocal(userId, status.name).map { it.toDomain() }
            Result.Success(tasks)
        } catch (e: Exception) {
            Result.Error(message)
        }
    }

    private suspend fun getLocalTasksByPriorityOrError(priority: TaskPriority, message: String): Result<List<Task>> {
        val userId = tokenManager.getUserId() ?: return Result.Error(message)
        return try {
            val tasks = taskDao.getTasksByPriorityLocal(userId, priority.name).map { it.toDomain() }
            Result.Success(tasks)
        } catch (e: Exception) {
            Result.Error(message)
        }
    }
}
