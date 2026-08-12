package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val category: String,
    val subcategory: String,
    val difficulty: String,
    val type: String,
    val title: String,
    val prompt: String,
    val optionsJson: String,
    val correctAnswer: String,
    val explanation: String,
    val sampleCode: String = ""
)

@Entity(tableName = "interview_results")
data class InterviewResultEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val scorePercentage: Int,
    val date: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val detailedFeedback: String
)

@Entity(tableName = "resume_analysis")
data class ResumeAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val resumeScore: Int,
    val candidateName: String,
    val strongSkillsCsv: String,
    val missingSkillsCsv: String,
    val recommendedQuestionsJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val college: String,
    val degree: String,
    val graduationYear: String,
    val primarySkill: String,
    val experienceLevel: String,
    val overallScore: Int,
    val questionsAttempted: Int,
    val averageScore: Int,
    val currentStreak: Int,
    val readinessPercentage: Int
)
