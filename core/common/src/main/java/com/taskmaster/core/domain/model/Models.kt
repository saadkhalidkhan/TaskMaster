/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val profilePictureUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class Task(
    val taskId: Int = 0,
    val userId: String,
    val title: String,
    val description: String? = null,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class TaskPriority {
    LOW, MEDIUM, HIGH, URGENT
}

@Serializable
enum class TaskStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}

@Serializable
enum class TaskCategory {
    PERSONAL, WORK, HEALTH, EDUCATION, FINANCE, OTHER
}

@Serializable
data class Project(
    val projectId: String,
    val name: String,
    val description: String? = null,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class TaskStatistics(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val overdueTasks: Int = 0,
    val completionRate: Double = 0.0
)

// API Request/Response models
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val dueDate: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL
)

@Serializable
data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val dueDate: Long? = null,
    val isCompleted: Boolean? = null,
    val priority: TaskPriority? = null,
    val status: TaskStatus? = null,
    val category: TaskCategory? = null
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalItems: Int
)
