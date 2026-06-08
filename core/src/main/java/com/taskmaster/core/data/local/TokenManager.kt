/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.core.data.local

import kotlinx.coroutines.flow.Flow

interface TokenManager {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getUserId(): String?
    fun getUserEmail(): String?
    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        expiryTimeMs: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000
    )
    suspend fun clearSession()
    suspend fun restoreSession()
    fun isLoggedIn(): Flow<Boolean>
    fun isTokenExpired(): Boolean
    fun getTokenExpiryTime(): Long?
    suspend fun refreshToken(): String?
}
