/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.shared.utils

import com.taskmaster.core.data.local.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    suspend fun saveAccessToken(token: String) {
        val userId = tokenManager.getUserId() ?: return
        val email = tokenManager.getUserEmail() ?: return
        tokenManager.saveSession(
            accessToken = token,
            refreshToken = tokenManager.getRefreshToken() ?: "",
            userId = userId,
            email = email
        )
    }

    suspend fun getAccessToken(): String? = tokenManager.getAccessToken()

    suspend fun saveRefreshToken(token: String) {
        val userId = tokenManager.getUserId() ?: return
        val email = tokenManager.getUserEmail() ?: return
        tokenManager.saveSession(
            accessToken = tokenManager.getAccessToken() ?: "",
            refreshToken = token,
            userId = userId,
            email = email
        )
    }

    suspend fun getRefreshToken(): String? = tokenManager.getRefreshToken()

    suspend fun getUserId(): String? = tokenManager.getUserId()

    suspend fun getUserEmail(): String? = tokenManager.getUserEmail()

    suspend fun clearAll() {
        tokenManager.clearSession()
    }
}

object DateUtils {
    fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }

    fun formatDateTime(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(date)
    }

    fun formatTime(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return formatter.format(date)
    }

    fun isToday(timestamp: Long): Boolean {
        val today = java.util.Calendar.getInstance()
        val date = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }

        return today.get(java.util.Calendar.YEAR) == date.get(java.util.Calendar.YEAR) &&
            today.get(java.util.Calendar.DAY_OF_YEAR) == date.get(java.util.Calendar.DAY_OF_YEAR)
    }

    fun isOverdue(timestamp: Long): Boolean {
        return timestamp < System.currentTimeMillis()
    }

    fun getDaysUntilDue(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = timestamp - now
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }

    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
            else -> formatDate(timestamp)
        }
    }
}

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }

    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2 && name.all { it.isLetter() || it.isWhitespace() }
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^\\+?[1-9]\\d{1,14}$"))
    }

    fun isValidTaskTitle(title: String): Boolean {
        return title.trim().length >= 3 && title.trim().length <= 100
    }

    fun isValidTaskDescription(description: String): Boolean {
        return description.trim().length <= 500
    }
}

object StringUtils {
    fun capitalizeFirstLetter(text: String): String {
        return text.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    fun truncateText(text: String, maxLength: Int): String {
        return if (text.length <= maxLength) text else "${text.take(maxLength)}..."
    }

    fun extractInitials(name: String): String {
        return name.split(" ")
            .take(2)
            .map { it.firstOrNull()?.uppercase() ?: "" }
            .joinToString("")
    }

    fun formatTaskCount(count: Int): String {
        return when (count) {
            0 -> "No tasks"
            1 -> "1 task"
            else -> "$count tasks"
        }
    }

    fun formatCompletionPercentage(completed: Int, total: Int): String {
        return if (total == 0) "0%" else "${(completed * 100 / total)}%"
    }
}

object ColorUtils {
    fun getPriorityColor(priority: com.taskmaster.core.domain.model.TaskPriority): String {
        return when (priority) {
            com.taskmaster.core.domain.model.TaskPriority.LOW -> "#4CAF50"
            com.taskmaster.core.domain.model.TaskPriority.MEDIUM -> "#FF9800"
            com.taskmaster.core.domain.model.TaskPriority.HIGH -> "#FF5722"
            com.taskmaster.core.domain.model.TaskPriority.URGENT -> "#D32F2F"
        }
    }

    fun getStatusColor(status: com.taskmaster.core.domain.model.TaskStatus): String {
        return when (status) {
            com.taskmaster.core.domain.model.TaskStatus.PENDING -> "#2196F3"
            com.taskmaster.core.domain.model.TaskStatus.IN_PROGRESS -> "#FF9800"
            com.taskmaster.core.domain.model.TaskStatus.COMPLETED -> "#4CAF50"
            com.taskmaster.core.domain.model.TaskStatus.CANCELLED -> "#F44336"
        }
    }
}
