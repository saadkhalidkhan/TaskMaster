package com.taskmaster.core.domain.usecase

import com.taskmaster.core.common.Result
import com.taskmaster.core.domain.model.*
import com.taskmaster.core.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Task Use Cases
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 20) = 
        taskRepository.getTasks(page, pageSize)
}

class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(id: String) = 
        taskRepository.getTaskById(id)
}

class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = 
        taskRepository.createTask(task)
}

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = 
        taskRepository.updateTask(task)
}

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(id: String) = 
        taskRepository.deleteTask(id)
}

class GetTasksByStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(status: TaskStatus) = 
        taskRepository.getTasksByStatus(status)
}

class GetTasksByCategoryUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(category: TaskCategory) = 
        taskRepository.getTasksByCategory(category)
}

class GetTasksByPriorityUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(priority: TaskPriority) = 
        taskRepository.getTasksByPriority(priority)
}

class SearchTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(query: String) = 
        taskRepository.searchTasks(query)
}

class GetTasksByDateRangeUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(startDate: Long, endDate: Long) = 
        taskRepository.getTasksByDateRange(startDate, endDate)
}

class ObserveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = 
        taskRepository.observeTasks()
}

class GetTaskStatisticsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke() = 
        taskRepository.getTaskStatistics()
}

// Auth Use Cases
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) = 
        authRepository.login(email, password)
}

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String) = 
        authRepository.register(email, password, name)
}

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = 
        authRepository.logout()
}

class RefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = 
        authRepository.refreshToken()
}

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String) = 
        authRepository.forgotPassword(email)
}

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String, newPassword: String) = 
        authRepository.resetPassword(token, newPassword)
}

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String) = 
        authRepository.changePassword(currentPassword, newPassword)
}

class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = 
        authRepository.isLoggedIn()
}

class VerifyEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String) = 
        authRepository.verifyEmail(token)
}

// Wrapper classes for easier injection
class AuthUseCases @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase
) {
    suspend fun loginUser(email: String, password: String) = loginUseCase(email, password)
    suspend fun registerUser(username: String, email: String, password: String) = registerUseCase(email, password, username)
    suspend fun logoutUser() = logoutUseCase()
    suspend fun refreshToken() = refreshTokenUseCase()
    suspend fun forgotPassword(email: String) = forgotPasswordUseCase(email)
    suspend fun resetPassword(token: String, newPassword: String) = resetPasswordUseCase(token, newPassword)
    suspend fun changePassword(currentPassword: String, newPassword: String) = changePasswordUseCase(currentPassword, newPassword)
    fun isLoggedIn() = isLoggedInUseCase()
    suspend fun verifyEmail(token: String) = verifyEmailUseCase(token)
}

class TaskUseCases @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTasksByStatusUseCase: GetTasksByStatusUseCase,
    private val getTasksByCategoryUseCase: GetTasksByCategoryUseCase,
    private val getTasksByPriorityUseCase: GetTasksByPriorityUseCase,
    private val searchTasksUseCase: SearchTasksUseCase,
    private val getTasksByDateRangeUseCase: GetTasksByDateRangeUseCase,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val getTaskStatisticsUseCase: GetTaskStatisticsUseCase
) {
    suspend fun getTasks(page: Int = 1, pageSize: Int = 20) = getTasksUseCase(page, pageSize)
    suspend fun getTaskById(id: String) = getTaskByIdUseCase(id)
    suspend fun createTask(task: Task) = createTaskUseCase(task)
    suspend fun updateTask(task: Task) = updateTaskUseCase(task)
    suspend fun deleteTask(id: String) = deleteTaskUseCase(id)
    suspend fun getTasksByStatus(status: TaskStatus) = getTasksByStatusUseCase(status)
    suspend fun getTasksByCategory(category: TaskCategory) = getTasksByCategoryUseCase(category)
    suspend fun getTasksByPriority(priority: TaskPriority) = getTasksByPriorityUseCase(priority)
    suspend fun searchTasks(query: String) = searchTasksUseCase(query)
    suspend fun getTasksByDateRange(startDate: Long, endDate: Long) = getTasksByDateRangeUseCase(startDate, endDate)
    fun observeTasks(): Flow<List<Task>> = observeTasksUseCase()
    suspend fun getTaskStatistics() = getTaskStatisticsUseCase()
    
    // Additional methods for ViewModels
    fun getTasksForUser(userId: String): Flow<Result<List<Task>>> {
        return observeTasksUseCase().map { tasks ->
            Result.Success(tasks.filter { it.userId == userId })
        }
    }
    
    fun getTaskById(taskId: Int): Flow<Result<Task?>> {
        return observeTasksUseCase().map { tasks ->
            Result.Success(tasks.find { it.taskId == taskId })
        }
    }
    
    suspend fun saveTask(task: Task): Result<Task> {
        return if (task.taskId == 0) {
            createTaskUseCase(task)
        } else {
            updateTaskUseCase(task)
        }
    }
}