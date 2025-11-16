/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.core.domain.usecase

import com.taskmaster.core.common.Result
import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.model.TaskStatistics
import com.taskmaster.core.domain.model.User
import com.taskmaster.core.domain.repository.AuthRepository
import com.taskmaster.core.domain.repository.TaskRepository
import com.taskmaster.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun loginUser(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
    
    suspend fun registerUser(username: String, email: String, password: String): Result<User> {
        return authRepository.register(username, email, password)
    }
    
    suspend fun logoutUser(): Result<Unit> {
        return authRepository.logout()
    }
    
    suspend fun refreshToken(): Result<String> {
        return authRepository.refreshToken()
    }
    
    suspend fun forgotPassword(email: String): Result<Unit> {
        return authRepository.forgotPassword(email)
    }
    
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return authRepository.resetPassword(token, newPassword)
    }
    
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return authRepository.changePassword(currentPassword, newPassword)
    }
    
    suspend fun verifyEmail(token: String): Result<Unit> {
        return authRepository.verifyEmail(token)
    }
}

class TaskUseCases @Inject constructor(
    private val taskRepository: TaskRepository
) {
    fun getTasksForUser(userId: String): Flow<Result<List<Task>>> {
        return taskRepository.getTasksForUser(userId)
    }
    
    fun getTaskById(taskId: Int): Flow<Result<Task?>> {
        return taskRepository.getTaskById(taskId)
    }
    
    suspend fun saveTask(task: Task): Result<Task> {
        return taskRepository.saveTask(task)
    }
    
    suspend fun updateTask(task: Task): Result<Task> {
        return taskRepository.updateTask(task)
    }
    
    suspend fun deleteTask(taskId: Int): Result<Unit> {
        return taskRepository.deleteTask(taskId)
    }
    
    fun getTasksByStatus(userId: String, status: String): Flow<Result<List<Task>>> {
        return taskRepository.getTasksByStatus(userId, status)
    }
    
    fun getTasksByCategory(userId: String, category: String): Flow<Result<List<Task>>> {
        return taskRepository.getTasksByCategory(userId, category)
    }
    
    fun getTasksByPriority(userId: String, priority: String): Flow<Result<List<Task>>> {
        return taskRepository.getTasksByPriority(userId, priority)
    }
    
    fun searchTasks(userId: String, query: String): Flow<Result<List<Task>>> {
        return taskRepository.searchTasks(userId, query)
    }
    
    fun getTasksByDateRange(userId: String, startDate: Long, endDate: Long): Flow<Result<List<Task>>> {
        return taskRepository.getTasksByDateRange(userId, startDate, endDate)
    }
    
    fun getTaskStatistics(userId: String): Flow<Result<TaskStatistics>> {
        return taskRepository.getTaskStatistics(userId)
    }
}

class UserUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    fun getCurrentUser(): Flow<Result<User?>> {
        return userRepository.getCurrentUser()
    }
    
    suspend fun updateUser(user: User): Result<User> {
        return userRepository.updateUser(user)
    }
    
    suspend fun deleteUser(): Result<Unit> {
        return userRepository.deleteUser()
    }
    
    suspend fun updateUserProfile(updates: Map<String, String?>): Result<User> {
        return userRepository.updateUserProfile(updates)
    }
}
