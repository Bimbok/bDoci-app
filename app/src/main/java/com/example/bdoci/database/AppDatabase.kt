package com.example.bdoci.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bdoci.models.Doc

@Database(entities = [Doc::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun docDao(): DocDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bdoci_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}