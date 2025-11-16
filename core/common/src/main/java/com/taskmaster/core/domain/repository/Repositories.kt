/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.core.domain.repository

import com.taskmaster.core.common.Result
import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.model.TaskStatistics
import com.taskmaster.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(username: String, email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(): Result<String>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun verifyEmail(token: String): Result<Unit>
}

interface TaskRepository {
    suspend fun getTasksForUser(userId: String): Flow<Result<List<Task>>>
    suspend fun getTaskById(taskId: Int): Flow<Result<Task?>>
    suspend fun saveTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun deleteTask(taskId: Int): Result<Unit>
    suspend fun getTasksByStatus(userId: String, status: String): Flow<Result<List<Task>>>
    suspend fun getTasksByCategory(userId: String, category: String): Flow<Result<List<Task>>>
    suspend fun getTasksByPriority(userId: String, priority: String): Flow<Result<List<Task>>>
    suspend fun searchTasks(userId: String, query: String): Flow<Result<List<Task>>>
    suspend fun getTasksByDateRange(userId: String, startDate: Long, endDate: Long): Flow<Result<List<Task>>>
    suspend fun getTaskStatistics(userId: String): Flow<Result<TaskStatistics>>
}

interface UserRepository {
    suspend fun getCurrentUser(): Flow<Result<User?>>
    suspend fun updateUser(user: User): Result<User>
    suspend fun deleteUser(): Result<Unit>
    suspend fun updateUserProfile(updates: Map<String, String?>): Result<User>
}
