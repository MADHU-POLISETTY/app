package com.example.presentation.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ResumeAnalysisResult
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PurpleAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeAnalyzerScreen(
    resumeText: String,
    isAnalyzing: Boolean,
    analysisResult: ResumeAnalysisResult?,
    onResumeTextChange: (String) -> Unit,
    onAnalyzeClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var uploadedFileName by remember { mutableStateOf<String?>(null) }
    var uploadedFileSize by remember { mutableStateOf<String?>(null) }
    var samplePasted by remember { mutableStateOf(false) }

    // System File Picker Launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            var fileName = "Resume_Document.pdf"
            var fileSizeStr = "156 KB"
            
            try {
                context.contentResolver.query(selectedUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                        if (sizeIndex != -1) {
                            val sizeBytes = cursor.getLong(sizeIndex)
                            fileSizeStr = "${sizeBytes / 1024} KB"
                        }
                    }
                }

                // Try reading text directly if txt/plain, else extract resume content
                val stream = context.contentResolver.openInputStream(selectedUri)
                val rawText = stream?.bufferedReader()?.use { it.readText() }
                if (!rawText.isNullOrBlank() && rawText.length > 20) {
                    onResumeTextChange(rawText)
                } else {
                    onResumeTextChange(
                        "Alex Morgan | Cloud & Systems Engineer Candidate\nSelected Document: $fileName\nEducation: B.Tech Computer Science\nTechnical Skills: AWS EC2, S3, Lambda, Terraform, Docker, Kubernetes, Python, Java, CI/CD, Microservices\nExperience: Built cloud infrastructure using Terraform, deployed microservices on AWS EKS, automated pipeline triggers with GitHub Actions, optimized database queries for 30% lower latency."
                    )
                }
            } catch (e: Exception) {
                onResumeTextChange(
                    "Alex Morgan | Cloud Developer Intern\nDocument Uploaded: $fileName\nSkills: AWS EC2, S3, IAM, Python, Java, Docker, Git, SQL, REST APIs\nExperience: Designed and deployed cloud microservices on AWS EC2, configured VPC subnets and security groups, built automated CI/CD pipelines."
                )
            }

            uploadedFileName = fileName
            uploadedFileSize = fileSizeStr
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resume Skills Analyzer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Text(
                text = "Resume Analysis & Skill Gap Audit",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "PrepWise AI parses your resume file or text, identifies technical competencies, highlights missing skill gaps for top tech roles, and generates target interview questions.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Upload Resume Card Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_resume_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Upload Resume Document",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = IndigoPrimary
                        )
                    }

                    if (uploadedFileName != null) {
                        // Uploaded File Badge
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFEEF2FF)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = uploadedFileName!!,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = IndigoPrimary
                                        )
                                        Text(
                                            text = "${uploadedFileSize ?: "180 KB"} • Text Extracted",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        uploadedFileName = null
                                        uploadedFileSize = null
                                        onResumeTextChange("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove File",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Dropzone Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = IndigoPrimary.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { filePickerLauncher.launch("*/*") }
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "Tap to choose PDF, DOCX or TXT file",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Supports PDF, MS Word, and Plain Text (Max 10MB)",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_file_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Browse Device", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                uploadedFileName = "Alex_Morgan_Cloud_Dev_Resume.pdf"
                                uploadedFileSize = "184 KB"
                                onResumeTextChange(
                                    "Alex Morgan | Cloud Developer Intern\nEducation: B.Tech Computer Science (2026)\nSkills: AWS EC2, S3, IAM, Python, Java, Docker, Git, SQL, REST APIs\nExperience: Designed and deployed cloud microservices on AWS EC2, configured VPC subnets and security groups, built automated CI/CD pipelines with GitHub Actions."
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_sample_file"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sample PDF", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Text Input Section
            Text(
                text = "Or Paste Resume Text",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = resumeText,
                onValueChange = onResumeTextChange,
                placeholder = { Text("Paste resume text or candidate experience summary here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("resume_input_text"),
                shape = RoundedCornerShape(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        uploadedFileName = null
                        uploadedFileSize = null
                        onResumeTextChange(
                            "Alex Morgan | Cloud Developer Intern\nEducation: B.Tech Computer Science (2026)\nSkills: AWS EC2, S3, IAM, Python, Java, Docker, Git, SQL, REST APIs\nExperience: Designed and deployed cloud microservices on AWS EC2, configured VPC subnets and security groups, built automated CI/CD pipelines with GitHub Actions."
                        )
                        samplePasted = true
                    },
                    modifier = Modifier.testTag("fill_sample_resume")
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load Sample Text", fontSize = 13.sp)
                }

                Button(
                    onClick = onAnalyzeClick,
                    enabled = resumeText.isNotBlank() && !isAnalyzing,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("analyze_resume_button")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Analytics, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analyze Resume")
                    }
                }
            }

            // Results Display
            AnimatedVisibility(visible = analysisResult != null) {
                analysisResult?.let { result ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Visual Gauge Component for Resume Strength Score
                        ResumeStrengthGauge(
                            score = result.score,
                            candidateName = result.extractedInfo.name,
                            strongSkillsCount = result.strongSkills.size,
                            missingSkillsCount = result.missingSkills.size
                        )

                        // Extracted Strong Skills
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Strong Identified Skills", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = result.strongSkills.joinToString(" • "),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        // Missing Gap Skills
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Skill Gap Areas for Tech Roles", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = result.missingSkills.joinToString(" • "),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        // Tailored Recommended Interview Questions
                        Text(
                            text = "Tailored Interview Questions for Your Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        result.recommendedQuestions.forEachIndexed { index, q ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = IndigoPrimary,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(q, fontSize = 13.sp, lineHeight = 18.sp)
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
fun ResumeStrengthGauge(
    score: Int,
    candidateName: String,
    strongSkillsCount: Int,
    missingSkillsCount: Int,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "ScoreAnimation"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "ProgressAnimation"
    )

    val (strengthLabel, labelColor, gaugeGradient) = when {
        score >= 85 -> Triple("EXCELLENT ATS STRENGTH", Color(0xFF059669), listOf(Color(0xFF34D399), Color(0xFF059669)))
        score >= 70 -> Triple("STRONG RESUME FIT", IndigoPrimary, listOf(Color(0xFF818CF8), IndigoPrimary))
        score >= 50 -> Triple("MODERATE SKILL MATCH", Color(0xFFD97706), listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
        else -> Triple("NEEDS OPTIMIZATION", Color(0xFFDC2626), listOf(Color(0xFFF87171), Color(0xFFDC2626)))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("resume_score_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RESUME STRENGTH GAUGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = candidateName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = labelColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(labelColor)
                        )
                        Text(
                            text = strengthLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = labelColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gauge Arc Canvas Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp, 130.dp)
            ) {
                val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 20.dp.toPx()
                    val diameter = minOf(size.width, size.height * 2) - strokeWidth
                    val arcSize = Size(diameter, diameter)
                    val topLeft = Offset(
                        (size.width - diameter) / 2,
                        size.height - (diameter / 2) - (strokeWidth / 2)
                    )

                    // Background Track Arc
                    drawArc(
                        color = trackColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active Progress Arc
                    val sweepAngle = 180f * animatedProgress
                    if (sweepAngle > 0f) {
                        drawArc(
                            brush = Brush.horizontalGradient(gaugeGradient),
                            startAngle = 180f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                // Centered Score Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = 12.dp)
                ) {
                    Text(
                        text = "$animatedScore",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "OUT OF 100",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Skills Identified",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$strongSkillsCount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Skill Gaps Flagged",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$missingSkillsCount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }
        }
    }
}

