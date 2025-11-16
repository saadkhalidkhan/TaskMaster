/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taskmaster.database.dao.TaskDao
import com.taskmaster.database.dao.UserDao
import com.taskmaster.database.entity.TaskEntity
import com.taskmaster.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskMasterDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
}