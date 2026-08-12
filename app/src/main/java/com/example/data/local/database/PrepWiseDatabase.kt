package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        QuestionEntity::class,
        InterviewResultEntity::class,
        ResumeAnalysisEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PrepWiseDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun interviewResultDao(): InterviewResultDao
    abstract fun resumeAnalysisDao(): ResumeAnalysisDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: PrepWiseDatabase? = null

        fun getDatabase(context: Context): PrepWiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrepWiseDatabase::class.java,
                    "prepwise_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
