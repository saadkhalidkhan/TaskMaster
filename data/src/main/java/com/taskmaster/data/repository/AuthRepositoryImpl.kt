/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.data.repository

import com.taskmaster.core.common.Result
import com.taskmaster.core.data.local.TokenManager
import com.taskmaster.core.domain.model.LoginRequest
import com.taskmaster.core.domain.model.RefreshTokenRequest
import com.taskmaster.core.domain.model.RegisterRequest
import com.taskmaster.core.domain.model.User
import com.taskmaster.core.domain.repository.AuthRepository
import com.taskmaster.data.mapper.toDomain
import com.taskmaster.data.mapper.toEntity
import com.taskmaster.database.dao.UserDao
import com.taskmaster.networking.api.TaskMasterApi
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val taskMasterApi: TaskMasterApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = taskMasterApi.login(LoginRequest(email, password))
            if (response.success) {
                response.data?.let { user ->
                    persistUserSession(user)
                    Result.Success(user)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to login")
            }
        } catch (e: Exception) {
            loginOffline(email, e.message ?: "Network error")
        }
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val response = taskMasterApi.register(
                RegisterRequest(
                    username = name,
                    email = email,
                    password = password
                )
            )
            if (response.success) {
                response.data?.let { user ->
                    persistUserSession(user)
                    Result.Success(user)
                } ?: Result.Error("No data returned")
            } else {
                Result.Error(response.message ?: "Failed to register")
            }
        } catch (e: Exception) {
            registerOffline(name, email)
        }
    }

    override suspend fun logout(): Result<Unit> {
        try {
            taskMasterApi.logout()
        } catch (_: Exception) {
            // Clear local session even when the remote logout fails.
        }
        tokenManager.getUserId()?.let { userDao.deleteUserById(it) }
        tokenManager.clearSession()
        return Result.Success(Unit)
    }

    override suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = tokenManager.getRefreshToken() ?: "refresh_token"
            val response = taskMasterApi.refreshToken(RefreshTokenRequest(refreshToken))
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

    override fun isLoggedIn(): Flow<Boolean> = tokenManager.isLoggedIn()

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

    private suspend fun persistUserSession(user: User) {
        userDao.insertUser(user.toEntity())
        tokenManager.saveSession(
            accessToken = "local_${user.userId}",
            refreshToken = "refresh_${user.userId}",
            userId = user.userId,
            email = user.email
        )
    }

    private suspend fun loginOffline(email: String, message: String): Result<User> {
        val localUser = userDao.getUserByEmail(email)
        return if (localUser != null) {
            val user = localUser.toDomain()
            persistUserSession(user)
            Result.Success(user)
        } else {
            Result.Error(message)
        }
    }

    private suspend fun registerOffline(name: String, email: String): Result<User> {
        return if (userDao.getUserByEmail(email) != null) {
            Result.Error("An account with this email already exists")
        } else {
            val user = User(
                userId = UUID.randomUUID().toString(),
                username = name,
                email = email
            )
            persistUserSession(user)
            Result.Success(user)
        }
    }
}
