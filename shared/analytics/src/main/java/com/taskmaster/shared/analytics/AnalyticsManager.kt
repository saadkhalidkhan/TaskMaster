package com.taskmaster.shared.analytics

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

interface AnalyticsManager {
    fun logEvent(eventName: String, params: Map<String, String>? = null)
    fun setUserProperty(name: String, value: String?)
}

@Singleton
class PlaceholderAnalyticsManager : AnalyticsManager {
    override fun logEvent(eventName: String, params: Map<String, String>?) {
        // Placeholder implementation - log to console for now
        println("Analytics Event: $eventName with params: $params")
    }

    override fun setUserProperty(name: String, value: String?) {
        // Placeholder implementation - log to console for now
        println("Analytics Property: $name = $value")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsManager(): AnalyticsManager {
        return PlaceholderAnalyticsManager()
    }
}