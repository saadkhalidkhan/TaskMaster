package com.taskmaster.core.data.local

interface TokenManager {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clearTokens()
    suspend fun refreshToken(): String?
    fun isTokenExpired(): Boolean
    fun getTokenExpiryTime(): Long?
}
