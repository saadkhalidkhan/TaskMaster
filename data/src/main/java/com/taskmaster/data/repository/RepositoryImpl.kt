package com.taskmaster.data.repository

import com.taskmaster.core.common.Result
import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.model.TaskCategory
import com.taskmaster.core.domain.model.TaskPriority
import com.taskmaster.core.domain.model.TaskStatus
import com.taskmaster.core.domain.model.User
import com.taskmaster.core.domain.repository.AuthRepository
import com.taskmaster.core.domain.repository.TaskRepository
import com.taskmaster.database.dao.TaskDao
import com.taskmaster.database.dao.UserDao
import com.taskmaster.database.entity.TaskEntity
import com.taskmaster.database.entity.UserEntity
import com.taskmaster.networking.api.TaskMasterApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMasterApi: TaskMasterApi
) : TaskRepository {

    override suspend fun getTasks(page: Int, pageSize: Int): Result<List<Task>> {
        return try {
            val response = taskMasterApi.getTasks(page, pageSize)
            if (response.success) {
                Result.Success(response.data?.data ?: emptyList())
            } else {
                Result.Error(response.message ?: "Failed to fetch tasks")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
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
                com.taskmaster.core.domain.model.CreateTaskRequest(
                    title = task.title,
                    description = task.description,
                    dueDate = task.dueDate,
                    priority = task.priority,
                    category = task.category
                )
            )
            if (response.success) {
                response.data?.let { createdTask ->
                    // Save to local database
                    taskDao.insertTask(createdTask.toEntity())
                    Result.Success(createdTask)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to create task")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return try {
            val response = taskMasterApi.updateTask(
                task.taskId.toString(),
                com.taskmaster.core.domain.model.UpdateTaskRequest(
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
                    // Update local database
                    taskDao.updateTask(updatedTask.toEntity())
                    Result.Success(updatedTask)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to update task")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            val response = taskMasterApi.deleteTask(id)
            if (response.success) {
                // Remove from local database
                taskDao.deleteTask(TaskEntity(taskId = id.toInt(), userId = "", title = ""))
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to delete task")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
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
            Result.Error(e.message ?: "Network error")
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
            Result.Error(e.message ?: "Network error")
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
            Result.Error(e.message ?: "Network error")
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
        return taskDao.getTasksForUser("current_user_id").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTaskStatistics(): Result<Map<String, Int>> {
        return try {
            val response = taskMasterApi.getTaskStatistics()
            if (response.success) {
                response.data?.let { stats ->
                    Result.Success(mapOf(
                        "totalTasks" to stats.totalTasks,
                        "completedTasks" to stats.completedTasks,
                        "pendingTasks" to stats.pendingTasks,
                        "overdueTasks" to stats.overdueTasks
                    ))
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to fetch task statistics")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val taskMasterApi: TaskMasterApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = taskMasterApi.login(
                com.taskmaster.core.domain.model.LoginRequest(email, password)
            )
            if (response.success) {
                response.data?.let { user ->
                    // Save user to local database
                    userDao.insertUser(user.toEntity())
                    Result.Success(user)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to login")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val response = taskMasterApi.register(
                com.taskmaster.core.domain.model.RegisterRequest(
                    username = name,
                    email = email,
                    password = password
                )
            )
            if (response.success) {
                response.data?.let { user ->
                    // Save user to local database
                    userDao.insertUser(user.toEntity())
                    Result.Success(user)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to register")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val response = taskMasterApi.logout()
            if (response.success) {
                // Clear local user data
                // TODO: Implement token clearing
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to logout")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun refreshToken(): Result<String> {
        return try {
            val response = taskMasterApi.refreshToken(
                com.taskmaster.core.domain.model.RefreshTokenRequest("refresh_token")
            )
            if (response.success) {
                Result.Success(response.data ?: "")
            } else {
                Result.Error(response.message ?: "Failed to refresh token")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = taskMasterApi.forgotPassword(mapOf("email" to email))
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to send reset email")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            val response = taskMasterApi.resetPassword(
                mapOf("token" to token, "newPassword" to newPassword)
            )
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to reset password")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val response = taskMasterApi.changePassword(
                mapOf("currentPassword" to currentPassword, "newPassword" to newPassword)
            )
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to change password")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return userDao.getUserById("current_user_id").map { it != null }
    }

    override suspend fun verifyEmail(token: String): Result<Unit> {
        return try {
            val response = taskMasterApi.verifyEmail(mapOf("token" to token))
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to verify email")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}

// Extension functions to convert between domain and entity models
private fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        taskId = taskId,
        userId = userId,
        title = title,
        description = description,
        dueDate = dueDate,
        isCompleted = isCompleted
    )
}

private fun TaskEntity.toDomain(): Task {
    return Task(
        taskId = taskId,
        userId = userId,
        title = title,
        description = description,
        dueDate = dueDate,
        isCompleted = isCompleted
    )
}

private fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        username = username,
        email = email
    )
}

private fun UserEntity.toDomain(): User {
    return User(
        userId = userId,
        username = username,
        email = email
    )
}
