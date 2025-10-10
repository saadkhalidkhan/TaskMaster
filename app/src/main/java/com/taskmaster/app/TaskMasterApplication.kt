package com.taskmaster.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-level components here
    }
}