package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AnswerEvaluation
import com.example.domain.model.Question
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PurpleAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockInterviewScreen(
    questions: List<Question>,
    currentIndex: Int,
    timerSeconds: Int,
    userAnswer: String,
    isRecording: Boolean,
    isEvaluating: Boolean,
    latestEvaluation: AnswerEvaluation?,
    onAnswerChanged: (String) -> Unit,
    onToggleRecording: () -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentQuestion = questions.getOrNull(currentIndex) ?: questions.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Mock Interview", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            val minutes = timerSeconds / 60
                            val seconds = timerSeconds % 60
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = IndigoPrimary
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${currentIndex + 1} of ${questions.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = currentQuestion?.category?.displayName ?: "AWS Cloud",
                    fontSize = 12.sp,
                    color = IndigoPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LinearProgressIndicator(
                progress = { (currentIndex + 1) / questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = IndigoPrimary,
                trackColor = IndigoPrimary.copy(alpha = 0.2f)
            )

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = currentQuestion?.title ?: "VPC Security Groups vs NACLs",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQuestion?.prompt ?: "Explain the statefulness and rule evaluation logic of AWS Security Groups vs Network ACLs.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            // Answer Input Area
            OutlinedTextField(
                value = userAnswer,
                onValueChange = onAnswerChanged,
                placeholder = { Text("Type or speak your technical answer here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("mock_answer_input"),
                shape = RoundedCornerShape(16.dp)
            )

            // Audio Recording Simulation Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleRecording,
                    colors = if (isRecording) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer) else ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("voice_recording_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (isRecording) MaterialTheme.colorScheme.error else IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isRecording) "Stop Speaking" else "Voice Input (AI Voice)")
                }

                Button(
                    onClick = onSubmitAnswer,
                    enabled = userAnswer.isNotBlank() && !isEvaluating,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("submit_answer_button")
                ) {
                    if (isEvaluating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Evaluate Answer")
                    }
                }
            }

            // AI Evaluation Result Display
            AnimatedVisibility(visible = latestEvaluation != null) {
                latestEvaluation?.let { eval ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("evaluation_result_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AI Score: ${eval.overallScore}/100",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = IndigoPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Instant Feedback",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScorePill(title = "Accuracy", score = eval.technicalAccuracy)
                                ScorePill(title = "Clarity", score = eval.communication)
                                ScorePill(title = "Depth", score = eval.completeness)
                            }

                            Text(
                                text = "Strengths:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = IndigoPrimary
                            )
                            eval.strengths.forEach { s ->
                                Text("• $s", fontSize = 13.sp)
                            }

                            Text(
                                text = "Suggested Improvement:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PurpleAccent
                            )
                            Text(eval.suggestedImprovement, fontSize = 13.sp, lineHeight = 18.sp)

                            Button(
                                onClick = onNextQuestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .testTag("next_question_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Next Question")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScorePill(title: String, score: Int) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontSize = 11.sp, color = Color.Gray)
            Text("$score%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IndigoPrimary)
        }
    }
}
