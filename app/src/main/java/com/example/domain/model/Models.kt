package com.example.domain.model

enum class Category(val displayName: String) {
    AWS_CLOUD("AWS Cloud"),
    CLOUD_DEVOPS("Cloud & DevOps"),
    DATA_STRUCTURES("Data Structures"),
    SYSTEM_DESIGN("System Design"),
    SOFTWARE_ENGINEERING("Software Engineering")
}

enum class Difficulty(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

enum class QuestionType(val displayName: String) {
    MCQ("Multiple Choice"),
    SHORT_ANSWER("Short Answer"),
    TECHNICAL_EXPLANATION("Technical Explanation"),
    CODING("Coding"),
    SCENARIO_BASED("Scenario Based")
}

data class Question(
    val id: String,
    val category: Category,
    val subcategory: String,
    val difficulty: Difficulty,
    val type: QuestionType,
    val title: String,
    val prompt: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val explanation: String = "",
    val sampleCode: String = ""
)

data class AnswerEvaluation(
    val overallScore: Int,
    val technicalAccuracy: Int,
    val conceptUnderstanding: Int,
    val completeness: Int,
    val communication: Int,
    val relevance: Int,
    val confidence: Int,
    val problemSolving: Int,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val suggestedImprovement: String
)

data class InterviewSession(
    val id: String,
    val title: String,
    val category: String,
    val scorePercentage: Int,
    val date: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val detailedFeedback: String = ""
)

data class ResumeData(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val education: String = "",
    val skills: List<String> = emptyList(),
    val cloudSkills: List<String> = emptyList(),
    val devOpsSkills: List<String> = emptyList(),
    val projects: List<String> = emptyList(),
    val experience: String = ""
)

data class ResumeAnalysisResult(
    val score: Int,
    val extractedInfo: ResumeData,
    val strongSkills: List<String>,
    val missingSkills: List<String>,
    val recommendedQuestions: List<String>
)

data class UserProfile(
    val fullName: String = "Candidate User",
    val email: String = "candidate@example.com",
    val college: String = "University Name",
    val degree: String = "B.S. Computer Science",
    val graduationYear: String = "2026",
    val primarySkill: String = "Software Engineering",
    val experienceLevel: String = "Intermediate",
    val joinDate: String = "2026",
    val overallScore: Int = 0,
    val questionsAttempted: Int = 0,
    val averageScore: Int = 0,
    val currentStreak: Int = 0,
    val readinessPercentage: Int = 0
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean = false,
    val type: String = "Reminder"
)

data class TestCase(
    val input: String,
    val expectedOutput: String,
    val actualOutput: String = "",
    val passed: Boolean = false
)

data class ExecutionResult(
    val passedCount: Int,
    val totalCount: Int,
    val runtimeMs: Long,
    val memoryMb: Int,
    val testCases: List<TestCase>
)

data class CodingQuestion(
    val id: String,
    val title: String,
    val difficulty: String,
    val category: String,
    val description: String,
    val constraints: String = "",
    val timeComplexity: String = "O(N)",
    val spaceComplexity: String = "O(1)",
    val starterTemplates: Map<String, String>,
    val testCases: List<TestCase>,
    val solutionExplanation: String = ""
)

