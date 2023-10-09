package com.example.bodycomposition.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bodycomposition.dao.converter.FloatArrayConverter
import com.example.bodycomposition.dao.converter.LocalDateConverter

/**
 * @see [reference](https://developer.android.com/training/data-storage/room#database)
 */
@Database(entities = [User::class], version = 1, exportSchema = false)
@TypeConverters(FloatArrayConverter::class, LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    // Singleton
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "face-db"
                    )
                        .addTypeConverter(FloatArrayConverter())
                        .addTypeConverter(LocalDateConverter())
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}