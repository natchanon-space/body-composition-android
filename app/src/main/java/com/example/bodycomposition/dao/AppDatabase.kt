package com.example.bodycomposition.dao

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * @see [reference](https://developer.android.com/training/data-storage/room#database)
 */
@Database(entities = [User::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}