package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.navigation.ScreenRoutes
import com.example.presentation.screens.*
import com.example.presentation.viewmodel.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationHelper.sendSystemNotification(
                this,
                "PrepWise AI Active",
                "Daily practice reminders enabled! Complete your AWS Cloud & DSA drills to keep your streak."
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Request POST_NOTIFICATIONS runtime permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationHelper.sendSystemNotification(
                    this,
                    "PrepWise Practice Alert",
                    "Target Role: AWS Cloud & DevOps. 3 new questions available in your daily queue."
                )
            }
        } else {
            NotificationHelper.sendSystemNotification(
                this,
                "PrepWise Practice Alert",
                "Target Role: AWS Cloud & DevOps. 3 new questions available in your daily queue."
            )
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val userProfile by viewModel.userProfile.collectAsState()
                val filteredQuestions by viewModel.filteredQuestions.collectAsState()
                val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()

                val activeInterviewQuestions by viewModel.activeInterviewQuestions.collectAsState()
                val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
                val userAnswerInput by viewModel.userAnswerInput.collectAsState()
                val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
                val timerSeconds by viewModel.timerSeconds.collectAsState()
                val isEvaluatingAnswer by viewModel.isEvaluatingAnswer.collectAsState()
                val latestAnswerEvaluation by viewModel.latestAnswerEvaluation.collectAsState()
                val mockInterviewSessionResult by viewModel.mockInterviewSessionResult.collectAsState()

                val resumeTextInput by viewModel.resumeTextInput.collectAsState()
                val isAnalyzingResume by viewModel.isAnalyzingResume.collectAsState()
                val resumeAnalysisResult by viewModel.resumeAnalysisResult.collectAsState()

                val selectedLanguage by viewModel.selectedLanguage.collectAsState()
                val codeSnippet by viewModel.codeSnippet.collectAsState()
                val isRunningCode by viewModel.isRunningCode.collectAsState()
                val codeExecutionResult by viewModel.codeExecutionResult.collectAsState()
                val selectedCodingQuestion by viewModel.selectedCodingQuestion.collectAsState()
                val isShowingSolution by viewModel.isShowingSolution.collectAsState()
                val codingQuestions = viewModel.codingQuestions
                val interviewResultsHistory by viewModel.interviewResultsHistory.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = ScreenRoutes.SPLASH,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(ScreenRoutes.SPLASH) {
                        SplashScreen(
                            onTimeout = {
                                if (isLoggedIn) {
                                    navController.navigate(ScreenRoutes.HOME) {
                                        popUpTo(ScreenRoutes.SPLASH) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(ScreenRoutes.ONBOARDING) {
                                        popUpTo(ScreenRoutes.SPLASH) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    composable(ScreenRoutes.ONBOARDING) {
                        OnboardingScreen(
                            onFinishOnboarding = {
                                navController.navigate(ScreenRoutes.LOGIN)
                            }
                        )
                    }

                    composable(ScreenRoutes.LOGIN) {
                        LoginScreen(
                            onAuthenticate = { email, pass ->
                                viewModel.authenticate(email, pass)
                            },
                            onLoginSuccess = {
                                navController.navigate(ScreenRoutes.HOME) {
                                    popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate(ScreenRoutes.REGISTER)
                            },
                            onNavigateToForgotPassword = {
                                navController.navigate(ScreenRoutes.FORGOT_PASSWORD)
                            },
                            onNavigateBack = {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable(ScreenRoutes.REGISTER) {
                        RegisterScreen(
                            onRegisterSuccess = { name, email, pass, college, degree, gradYear, skill ->
                                viewModel.registerUser(name, email, pass, college, degree, gradYear, skill)
                                navController.navigate(ScreenRoutes.HOME) {
                                    popUpTo(ScreenRoutes.REGISTER) { inclusive = true }
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(ScreenRoutes.FORGOT_PASSWORD) {
                        ForgotPasswordScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onPasswordResetUpdated = { email, newPass ->
                                viewModel.updatePassword(email, newPass)
                            }
                        )
                    }

                    composable(ScreenRoutes.HOME) {
                        HomeScreen(
                            userProfile = userProfile,
                            onNavigateToMockInterview = {
                                viewModel.startMockInterview()
                                navController.navigate(ScreenRoutes.MOCK_INTERVIEW)
                            },
                            onNavigateToQuestionBank = {
                                navController.navigate(ScreenRoutes.QUESTION_BANK)
                            },
                            onNavigateToResumeAnalyzer = {
                                navController.navigate(ScreenRoutes.RESUME_ANALYZER)
                            },
                            onNavigateToCoding = {
                                navController.navigate(ScreenRoutes.CODING_INTERVIEW)
                            },
                            onNavigateToPerformance = {
                                navController.navigate(ScreenRoutes.PERFORMANCE)
                            },
                            onNavigateToNotifications = {
                                navController.navigate(ScreenRoutes.NOTIFICATIONS)
                            },
                            onNavigateToProfile = {
                                navController.navigate(ScreenRoutes.PROFILE)
                            },
                            onNavigateToTestInsights = {
                                navController.navigate(ScreenRoutes.TEST_INSIGHTS)
                            }
                        )
                    }

                    composable(ScreenRoutes.QUESTION_BANK) {
                        QuestionBankScreen(
                            questions = filteredQuestions,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            onCategorySelected = { viewModel.setCategoryFilter(it) },
                            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                            onQuestionClick = {},
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(ScreenRoutes.MOCK_INTERVIEW) {
                        MockInterviewScreen(
                            questions = activeInterviewQuestions,
                            currentIndex = currentQuestionIndex,
                            timerSeconds = timerSeconds,
                            userAnswer = userAnswerInput,
                            isRecording = isRecordingVoice,
                            isEvaluating = isEvaluatingAnswer,
                            latestEvaluation = latestAnswerEvaluation,
                            onAnswerChanged = { viewModel.updateAnswerInput(it) },
                            onToggleRecording = { viewModel.toggleVoiceRecording() },
                            onSubmitAnswer = {
                                viewModel.submitCurrentAnswer {}
                            },
                            onNextQuestion = {
                                viewModel.nextQuestionOrFinish {
                                    navController.navigate(ScreenRoutes.INTERVIEW_RESULT) {
                                        popUpTo(ScreenRoutes.MOCK_INTERVIEW) { inclusive = true }
                                    }
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(ScreenRoutes.INTERVIEW_RESULT) {
                        InterviewResultScreen(
                            sessionResult = mockInterviewSessionResult,
                            onRetakeInterview = {
                                viewModel.startMockInterview()
                                navController.navigate(ScreenRoutes.MOCK_INTERVIEW) {
                                    popUpTo(ScreenRoutes.INTERVIEW_RESULT) { inclusive = true }
                                }
                            },
                            onNavigateHome = {
                                navController.navigate(ScreenRoutes.HOME) {
                                    popUpTo(ScreenRoutes.HOME) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(ScreenRoutes.RESUME_ANALYZER) {
                        ResumeAnalyzerScreen(
                            resumeText = resumeTextInput,
                            isAnalyzing = isAnalyzingResume,
                            analysisResult = resumeAnalysisResult,
                            onResumeTextChange = { viewModel.updateResumeText(it) },
                            onAnalyzeClick = { viewModel.analyzeResumeText() },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(ScreenRoutes.CODING_INTERVIEW) {
                        CodingInterviewScreen(
                            codingQuestions = codingQuestions,
                            selectedQuestion = selectedCodingQuestion,
                            selectedLanguage = selectedLanguage,
                            codeSnippet = codeSnippet,
                            isRunning = isRunningCode,
                            executionResult = codeExecutionResult,
                            isShowingSolution = isShowingSolution,
                            onQuestionSelected = { viewModel.setSelectedCodingQuestion(it) },
                            onLanguageSelected = { viewModel.setSelectedLanguage(it) },
                            onCodeSnippetChanged = { viewModel.updateCodeSnippet(it) },
                            onResetCodeClick = { viewModel.resetCodeSnippet() },
                            onToggleSolutionClick = { viewModel.toggleSolutionExplanation() },
                            onRunCodeClick = { viewModel.runCode() },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(ScreenRoutes.PERFORMANCE) {
                        PerformanceScreen(
                            userProfile = userProfile,
                            history = interviewResultsHistory,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(ScreenRoutes.NOTIFICATIONS) {
                        NotificationsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(ScreenRoutes.PROFILE) {
                        ProfileScreen(
                            userProfile = userProfile,
                            onProfileUpdate = { updated ->
                                viewModel.updateProfile(updated)
                            },
                            onSignOutClick = {
                                viewModel.logout()
                                navController.navigate(ScreenRoutes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(ScreenRoutes.TEST_INSIGHTS) {
                        DeveloperInsightsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
