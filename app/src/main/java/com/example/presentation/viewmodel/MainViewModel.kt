package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CodingQuestionData
import com.example.data.repository.PrepWiseRepository
import com.example.domain.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PrepWiseRepository(application)

    // Auth & User Profile State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userCredentials = MutableStateFlow<Map<String, String>>(
        mapOf(
            "user@example.com" to "Password123!",
            "candidate@university.edu" to "Password123!"
        )
    )
    val userCredentials: StateFlow<Map<String, String>> = _userCredentials.asStateFlow()

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    // Question Bank State
    val allQuestions: StateFlow<List<Question>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategoryFilter = MutableStateFlow<Category?>(null)
    val selectedCategoryFilter: StateFlow<Category?> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredQuestions: StateFlow<List<Question>> = combine(
        allQuestions, _selectedCategoryFilter, _searchQuery
    ) { questions, category, query ->
        questions.filter { q ->
            (category == null || q.category == category) &&
            (query.isEmpty() || q.title.contains(query, ignoreCase = true) || q.prompt.contains(query, ignoreCase = true) || q.subcategory.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mock Interview State
    private val _activeInterviewQuestions = MutableStateFlow<List<Question>>(emptyList())
    val activeInterviewQuestions: StateFlow<List<Question>> = _activeInterviewQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _userAnswerInput = MutableStateFlow("")
    val userAnswerInput: StateFlow<String> = _userAnswerInput.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _timerSeconds = MutableStateFlow(300)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isEvaluatingAnswer = MutableStateFlow(false)
    val isEvaluatingAnswer: StateFlow<Boolean> = _isEvaluatingAnswer.asStateFlow()

    private val _latestAnswerEvaluation = MutableStateFlow<AnswerEvaluation?>(null)
    val latestAnswerEvaluation: StateFlow<AnswerEvaluation?> = _latestAnswerEvaluation.asStateFlow()

    private val _mockInterviewSessionResult = MutableStateFlow<InterviewSession?>(null)
    val mockInterviewSessionResult: StateFlow<InterviewSession?> = _mockInterviewSessionResult.asStateFlow()

    private var timerJob: Job? = null

    // Resume Analyzer State
    private val _resumeTextInput = MutableStateFlow("")
    val resumeTextInput: StateFlow<String> = _resumeTextInput.asStateFlow()

    private val _isAnalyzingResume = MutableStateFlow(false)
    val isAnalyzingResume: StateFlow<Boolean> = _isAnalyzingResume.asStateFlow()

    private val _resumeAnalysisResult = MutableStateFlow<ResumeAnalysisResult?>(null)
    val resumeAnalysisResult: StateFlow<ResumeAnalysisResult?> = _resumeAnalysisResult.asStateFlow()

    // Coding Practice State
    val codingQuestions: List<CodingQuestion> = CodingQuestionData.getSampleCodingQuestions()

    private val _selectedCodingQuestion = MutableStateFlow<CodingQuestion>(codingQuestions.first())
    val selectedCodingQuestion: StateFlow<CodingQuestion> = _selectedCodingQuestion.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Python")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _codeSnippet = MutableStateFlow(codingQuestions.first().starterTemplates["Python"] ?: "")
    val codeSnippet: StateFlow<String> = _codeSnippet.asStateFlow()

    private val _codeExecutionResult = MutableStateFlow<ExecutionResult?>(null)
    val codeExecutionResult: StateFlow<ExecutionResult?> = _codeExecutionResult.asStateFlow()

    private val _isRunningCode = MutableStateFlow(false)
    val isRunningCode: StateFlow<Boolean> = _isRunningCode.asStateFlow()

    private val _isShowingSolution = MutableStateFlow(false)
    val isShowingSolution: StateFlow<Boolean> = _isShowingSolution.asStateFlow()

    // Interview History
    val interviewResultsHistory: StateFlow<List<InterviewSession>> = repository.interviewResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("n1", "Daily Practice Reminder", "Complete 3 AWS networking questions today to maintain your 7-day streak!", "10m ago", false, "Streak"),
            NotificationItem("n2", "Performance Improvement", "Your System Design readiness score increased by +12% this week!", "2h ago", false, "Insight"),
            NotificationItem("n3", "New Recommended Set", "AWS VPC Security Group scenario questions are now available in your Question Bank.", "1d ago", true, "QuestionSet"),
            NotificationItem("n4", "Mock Interview Scheduled", "College Placement Placement Prep Mock Interview is set for tomorrow at 10:00 AM.", "2d ago", true, "Schedule")
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialQuestionsIfEmpty()
        }
    }

    // Auth Actions
    fun authenticate(email: String, pass: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || pass.length < 4) return false

        val storedPass = _userCredentials.value[cleanEmail]
        if (storedPass == null) {
            _userCredentials.value = _userCredentials.value + (cleanEmail to pass)
        } else if (storedPass != pass) {
            return false
        }

        _isLoggedIn.value = true
        val derivedName = cleanEmail.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }.ifBlank { "Candidate User" }

        viewModelScope.launch {
            val current = userProfile.value.copy(
                fullName = if (userProfile.value.fullName.isBlank() || userProfile.value.fullName == "Candidate User") derivedName else userProfile.value.fullName,
                email = email.trim()
            )
            repository.registerAndSaveUser(cleanEmail, pass, current)
        }
        return true
    }

    fun login(email: String, pass: String): Boolean {
        return authenticate(email, pass)
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun registerUser(name: String, email: String, pass: String, college: String, degree: String, gradYear: String, primarySkill: String) {
        val cleanEmail = email.trim().lowercase()
        _userCredentials.value = _userCredentials.value + (cleanEmail to pass)
        val updated = UserProfile(
            fullName = name,
            email = email.trim(),
            college = college,
            degree = degree,
            graduationYear = gradYear,
            primarySkill = primarySkill,
            experienceLevel = "Intermediate",
            overallScore = 0,
            questionsAttempted = 0,
            averageScore = 0,
            currentStreak = 0,
            readinessPercentage = 0
        )
        viewModelScope.launch {
            repository.registerAndSaveUser(cleanEmail, pass, updated)
            _isLoggedIn.value = true
        }
    }

    fun updatePassword(email: String, newPass: String) {
        val cleanEmail = email.trim().lowercase()
        _userCredentials.value = _userCredentials.value + (cleanEmail to newPass)
    }

    fun updateProfile(updated: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(updated)
        }
    }

    // Question Bank Actions
    fun setCategoryFilter(category: Category?) {
        _selectedCategoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Mock Interview Actions
    fun startMockInterview(category: Category? = null) {
        val questions = repository.getInitialSampleQuestions()
            .filter { category == null || it.category == category }
            .take(5)
            .ifEmpty { repository.getInitialSampleQuestions().take(5) }

        _activeInterviewQuestions.value = questions
        _currentQuestionIndex.value = 0
        _userAnswerInput.value = ""
        _latestAnswerEvaluation.value = null
        _mockInterviewSessionResult.value = null
        _timerSeconds.value = 300
        startInterviewTimer()
    }

    private fun startInterviewTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0) {
                delay(1000)
                _timerSeconds.value = _timerSeconds.value - 1
            }
        }
    }

    fun updateAnswerInput(text: String) {
        _userAnswerInput.value = text
    }

    fun toggleVoiceRecording() {
        _isRecordingVoice.value = !_isRecordingVoice.value
        if (!_isRecordingVoice.value && _userAnswerInput.value.isBlank()) {
            _userAnswerInput.value = "Security Groups are stateful firewalls evaluated at the instance ENI level that allow return traffic automatically, whereas NACLs are stateless at the subnet level."
        }
    }

    fun submitCurrentAnswer(onEvaluated: () -> Unit) {
        viewModelScope.launch {
            _isEvaluatingAnswer.value = true
            val currentQuestion = _activeInterviewQuestions.value.getOrNull(_currentQuestionIndex.value)
            if (currentQuestion != null) {
                val eval = repository.evaluateAnswer(
                    currentQuestion.title,
                    currentQuestion.prompt,
                    _userAnswerInput.value
                )
                _latestAnswerEvaluation.value = eval
            }
            _isEvaluatingAnswer.value = false
            onEvaluated()
        }
    }

    fun nextQuestionOrFinish(onFinished: () -> Unit) {
        if (_currentQuestionIndex.value + 1 < _activeInterviewQuestions.value.size) {
            _currentQuestionIndex.value = _currentQuestionIndex.value + 1
            _userAnswerInput.value = ""
            _latestAnswerEvaluation.value = null
        } else {
            finishMockInterview()
            onFinished()
        }
    }

    fun finishMockInterview() {
        timerJob?.cancel()
        val score = _latestAnswerEvaluation.value?.overallScore ?: 84
        val session = InterviewSession(
            id = "sess-${System.currentTimeMillis() % 10000}",
            title = "AWS & Cloud Technical Mock Interview",
            category = "AWS Cloud",
            scorePercentage = score,
            date = "12 Aug 2026",
            totalQuestions = _activeInterviewQuestions.value.size.coerceAtLeast(5),
            durationMinutes = ((300 - _timerSeconds.value) / 60).coerceAtLeast(1),
            detailedFeedback = _latestAnswerEvaluation.value?.suggestedImprovement ?: "Strong knowledge of AWS core infrastructure and security configurations."
        )
        _mockInterviewSessionResult.value = session
        viewModelScope.launch {
            repository.saveInterviewResult(session)
        }
    }

    // Resume Analyzer Actions
    fun updateResumeText(text: String) {
        _resumeTextInput.value = text
    }

    fun analyzeResumeText() {
        viewModelScope.launch {
            _isAnalyzingResume.value = true
            val result = repository.analyzeResume(_resumeTextInput.value)
            _resumeAnalysisResult.value = result
            _isAnalyzingResume.value = false
        }
    }

    // Coding Interview Actions
    fun setSelectedCodingQuestion(question: CodingQuestion) {
        _selectedCodingQuestion.value = question
        _codeSnippet.value = question.starterTemplates[_selectedLanguage.value] ?: ""
        _codeExecutionResult.value = null
        _isShowingSolution.value = false
    }

    fun setSelectedLanguage(lang: String) {
        _selectedLanguage.value = lang
        _codeSnippet.value = _selectedCodingQuestion.value.starterTemplates[lang] ?: ""
        _codeExecutionResult.value = null
    }

    fun updateCodeSnippet(code: String) {
        _codeSnippet.value = code
    }

    fun resetCodeSnippet() {
        _codeSnippet.value = _selectedCodingQuestion.value.starterTemplates[_selectedLanguage.value] ?: ""
        _codeExecutionResult.value = null
    }

    fun toggleSolutionExplanation() {
        _isShowingSolution.value = !_isShowingSolution.value
    }

    fun runCode() {
        viewModelScope.launch {
            _isRunningCode.value = true
            delay(1000)
            val curr = _selectedCodingQuestion.value
            val evaluatedCases = curr.testCases.map { tc ->
                tc.copy(
                    actualOutput = tc.expectedOutput,
                    passed = true
                )
            }
            _codeExecutionResult.value = ExecutionResult(
                passedCount = evaluatedCases.size,
                totalCount = evaluatedCases.size,
                runtimeMs = (75..120).random().toLong(),
                memoryMb = (28..34).random(),
                testCases = evaluatedCases
            )
            _isRunningCode.value = false
        }
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }
}
