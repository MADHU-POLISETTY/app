package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CodingQuestion
import com.example.domain.model.ExecutionResult
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodingInterviewScreen(
    codingQuestions: List<CodingQuestion>,
    selectedQuestion: CodingQuestion,
    selectedLanguage: String,
    codeSnippet: String,
    isRunning: Boolean,
    executionResult: ExecutionResult?,
    isShowingSolution: Boolean,
    onQuestionSelected: (CodingQuestion) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onCodeSnippetChanged: (String) -> Unit,
    onResetCodeClick: () -> Unit,
    onToggleSolutionClick: () -> Unit,
    onRunCodeClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val languages = listOf("Python", "Java", "C++", "Kotlin")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coding Interview IDE", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onResetCodeClick,
                        modifier = Modifier.testTag("reset_code_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Template")
                    }
                    IconButton(
                        onClick = onToggleSolutionClick,
                        modifier = Modifier.testTag("toggle_solution_button")
                    ) {
                        Icon(
                            imageVector = if (isShowingSolution) Icons.Default.VisibilityOff else Icons.Default.Lightbulb,
                            contentDescription = "Solution & Hints",
                            tint = if (isShowingSolution) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Problem Selector Carousel
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Coding Problem (${codingQuestions.size} Available):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    codingQuestions.forEach { question ->
                        val isSelected = question.id == selectedQuestion.id
                        val badgeColor = when (question.difficulty.lowercase()) {
                            "easy" -> Color(0xFF059669)
                            "medium" -> Color(0xFFD97706)
                            else -> Color(0xFFDC2626)
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { onQuestionSelected(question) },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = question.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = badgeColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = question.difficulty,
                                            color = badgeColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.testTag("question_chip_${question.id}")
                        )
                    }
                }
            }

            // Problem Statement Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("problem_statement_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedQuestion.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IndigoPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = selectedQuestion.category,
                                    color = IndigoPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            val diffColor = when (selectedQuestion.difficulty.lowercase()) {
                                "easy" -> Color(0xFF059669)
                                "medium" -> Color(0xFFD97706)
                                else -> Color(0xFFDC2626)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = diffColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = selectedQuestion.difficulty,
                                    color = diffColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = selectedQuestion.description,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )

                    if (selectedQuestion.constraints.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Constraints:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = selectedQuestion.constraints,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "⏱️ Time: ${selectedQuestion.timeComplexity}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IndigoPrimary
                            )
                            Text(
                                text = "💾 Space: ${selectedQuestion.spaceComplexity}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IndigoPrimary
                            )
                        }

                        TextButton(
                            onClick = onToggleSolutionClick,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isShowingSolution) "Hide Solution" else "View AI Solution",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Solution Explanation Modal / Card
            AnimatedVisibility(visible = isShowingSolution) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solution_explanation_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Optimal Approach & Explanation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF92400E)
                            )
                        }

                        Text(
                            text = selectedQuestion.solutionExplanation,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            // Language Selector Bar & Run Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    languages.forEach { lang ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = { onLanguageSelected(lang) },
                            label = { Text(lang, fontSize = 12.sp) }
                        )
                    }
                }

                Button(
                    onClick = onRunCodeClick,
                    enabled = !isRunning,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("run_code_button")
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run Code")
                    }
                }
            }

            // Monospace Code Editor
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A)
            ) {
                OutlinedTextField(
                    value = codeSnippet,
                    onValueChange = onCodeSnippetChanged,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .testTag("code_editor_input"),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Execution Results Card
            AnimatedVisibility(visible = executionResult != null) {
                executionResult?.let { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("execution_results_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "All Test Cases Passed (${res.passedCount}/${res.totalCount})",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                }
                                Text(
                                    text = "${res.runtimeMs} ms • ${res.memoryMb} MB",
                                    fontSize = 12.sp,
                                    color = IndigoPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            res.testCases.forEachIndexed { i, tc ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Test Case ${i + 1}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = IndigoPrimary
                                            )
                                            Text(
                                                text = "PASSED",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color(0xFF059669)
                                            )
                                        }
                                        Text(
                                            text = "Input: ${tc.input}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = "Expected Output: ${tc.expectedOutput}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
