/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val taskId: Int = 0,
    val userId: String,
    val title: String,
    val description: String?,
    val dueDate: Long?,
    val isCompleted: Boolean,
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
    PERSONAL, WORK, SHOPPING, HEALTH, EDUCATION, OTHER
}
