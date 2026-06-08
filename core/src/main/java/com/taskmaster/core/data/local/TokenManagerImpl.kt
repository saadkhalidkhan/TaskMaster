/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenManager {

    private val cache = AtomicReference(SessionCache())

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val TOKEN_EXPIRY_KEY = longPreferencesKey("token_expiry")
    }

    override fun getAccessToken(): String? = cache.get().accessToken

    override fun getRefreshToken(): String? = cache.get().refreshToken

    override fun getUserId(): String? = cache.get().userId

    override fun getUserEmail(): String? = cache.get().email

    override suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        expiryTimeMs: Long
    ) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[USER_ID_KEY] = userId
            prefs[USER_EMAIL_KEY] = email
            prefs[TOKEN_EXPIRY_KEY] = expiryTimeMs
        }
        cache.set(SessionCache(accessToken, refreshToken, userId, email, expiryTimeMs))
    }

    override suspend fun clearSession() {
        dataStore.edit { it.clear() }
        cache.set(SessionCache())
    }

    override suspend fun restoreSession() {
        val prefs = dataStore.data.first()
        cache.set(
            SessionCache(
                accessToken = prefs[ACCESS_TOKEN_KEY],
                refreshToken = prefs[REFRESH_TOKEN_KEY],
                userId = prefs[USER_ID_KEY],
                email = prefs[USER_EMAIL_KEY],
                expiryTimeMs = prefs[TOKEN_EXPIRY_KEY] ?: 0L
            )
        )
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            !prefs[ACCESS_TOKEN_KEY].isNullOrBlank() && !prefs[USER_ID_KEY].isNullOrBlank()
        }
    }

    override fun isTokenExpired(): Boolean {
        val expiryTime = getTokenExpiryTime() ?: return true
        return expiryTime < System.currentTimeMillis()
    }

    override fun getTokenExpiryTime(): Long? {
        val expiry = cache.get().expiryTimeMs
        return expiry.takeIf { it > 0L }
    }

    override suspend fun refreshToken(): String? {
        val refresh = getRefreshToken() ?: return null
        val newAccessToken = "refreshed_$refresh"
        val userId = getUserId() ?: return null
        val email = getUserEmail() ?: return null
        saveSession(
            accessToken = newAccessToken,
            refreshToken = refresh,
            userId = userId,
            email = email
        )
        return newAccessToken
    }

    private data class SessionCache(
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val userId: String? = null,
        val email: String? = null,
        val expiryTimeMs: Long = 0L
    )
}
