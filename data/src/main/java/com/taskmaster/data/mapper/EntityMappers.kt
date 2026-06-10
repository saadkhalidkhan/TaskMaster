/**
 * @author Saad Khan
 * @date June 2025
 */
package com.taskmaster.data.mapper

import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.model.TaskCategory
import com.taskmaster.core.domain.model.TaskPriority
import com.taskmaster.core.domain.model.TaskStatus
import com.taskmaster.core.domain.model.User
import com.taskmaster.database.entity.TaskEntity
import com.taskmaster.database.entity.UserEntity

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        taskId = taskId,
        userId = userId,
        title = title,
        description = description,
        dueDate = dueDate,
        isCompleted = isCompleted,
        priority = priority.name,
        status = status.name,
        category = category.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        taskId = taskId,
        userId = userId,
        title = title,
        description = description,
        dueDate = dueDate,
        isCompleted = isCompleted,
        priority = runCatching { TaskPriority.valueOf(priority) }.getOrDefault(TaskPriority.MEDIUM),
        status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.PENDING),
        category = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.PERSONAL),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        username = username,
        email = email
    )
}

fun UserEntity.toDomain(): User {
    return User(
        userId = userId,
        username = username,
        email = email
    )
}
