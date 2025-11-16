/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.database.di

import android.content.Context
import androidx.room.Room
import com.taskmaster.database.TaskMasterDatabase
import com.taskmaster.database.dao.TaskDao
import com.taskmaster.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskMasterDatabase(@ApplicationContext context: Context): TaskMasterDatabase {
        return Room.databaseBuilder(
            context,
            TaskMasterDatabase::class.java,
            "taskmaster_db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: TaskMasterDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideTaskDao(database: TaskMasterDatabase): TaskDao {
        return database.taskDao()
    }
}