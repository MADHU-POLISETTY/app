package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.InterviewResultEntity
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.ResumeAnalysisEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE category = :category")
    fun getQuestionsByCategory(category: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
}

@Dao
interface InterviewResultDao {
    @Query("SELECT * FROM interview_results ORDER BY id DESC")
    fun getAllResults(): Flow<List<InterviewResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: InterviewResultEntity)
}

@Dao
interface ResumeAnalysisDao {
    @Query("SELECT * FROM resume_analysis ORDER BY id DESC LIMIT 1")
    fun getLatestAnalysis(): Flow<ResumeAnalysisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: ResumeAnalysisEntity)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
