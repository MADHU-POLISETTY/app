package com.example.presentation.navigation

object ScreenRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    
    const val HOME = "home"
    const val QUESTION_BANK = "question_bank"
    const val QUESTION_DETAIL = "question_detail/{questionId}"
    const val MOCK_INTERVIEW = "mock_interview"
    const val INTERVIEW_RESULT = "interview_result"
    const val RESUME_ANALYZER = "resume_analyzer"
    const val CODING_INTERVIEW = "coding_interview"
    const val PERFORMANCE = "performance"
    const val INTERVIEW_HISTORY = "interview_history"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"

    fun buildQuestionDetailRoute(id: String) = "question_detail/$id"
}
