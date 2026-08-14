package com.example.data.repository

import android.content.Context
import com.example.data.local.database.PrepWiseDatabase
import com.example.data.local.entity.InterviewResultEntity
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.ResumeAnalysisEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.remote.GeminiService
import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PrepWiseRepository(context: Context) {
    private val db = PrepWiseDatabase.getDatabase(context)
    private val questionDao = db.questionDao()
    private val resultDao = db.interviewResultDao()
    private val resumeDao = db.resumeAnalysisDao()
    private val userProfileDao = db.userProfileDao()

    val allQuestions: Flow<List<Question>> = questionDao.getAllQuestions().map { entities ->
        if (entities.isEmpty()) {
            getInitialSampleQuestions()
        } else {
            entities.map { it.toDomain() }
        }
    }

    val interviewResults: Flow<List<InterviewSession>> = resultDao.getAllResults().map { list ->
        list.map {
            InterviewSession(
                id = it.id,
                title = it.title,
                category = it.category,
                scorePercentage = it.scorePercentage,
                date = it.date,
                totalQuestions = it.totalQuestions,
                durationMinutes = it.durationMinutes,
                detailedFeedback = it.detailedFeedback
            )
        }
    }

    val userProfile: Flow<UserProfile> = combine(
        userProfileDao.getUserProfile(),
        resultDao.getAllResults()
    ) { profileEntity, resultsList ->
        val baseProfile = profileEntity?.toDomain() ?: UserProfile()
        if (resultsList.isEmpty()) {
            baseProfile
        } else {
            val totalQuestions = resultsList.sumOf { it.totalQuestions }
            val avgScore = resultsList.map { it.scorePercentage }.average().toInt()
            val readiness = (avgScore * 0.85 + (resultsList.size * 3).coerceAtMost(15)).toInt().coerceIn(0, 100)
            val streak = resultsList.size.coerceAtLeast(1)
            baseProfile.copy(
                questionsAttempted = totalQuestions,
                averageScore = avgScore,
                overallScore = avgScore,
                readinessPercentage = readiness,
                currentStreak = streak
            )
        }
    }

    suspend fun registerAndSaveUser(email: String, pass: String, profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile.toEntity())
        com.example.data.remote.FirebaseService.registerOrAuthenticateWithFirebase(email, pass)
        com.example.data.remote.FirebaseService.saveUserProfileToFirestore(profile)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile.toEntity())
        com.example.data.remote.FirebaseService.saveUserProfileToFirestore(profile)
    }

    suspend fun evaluateAnswer(questionTitle: String, questionPrompt: String, answer: String): AnswerEvaluation {
        return GeminiService.evaluateAnswer(questionTitle, questionPrompt, answer)
    }

    suspend fun analyzeResume(resumeText: String): ResumeAnalysisResult {
        val result = GeminiService.analyzeResume(resumeText)
        resumeDao.insertAnalysis(
            ResumeAnalysisEntity(
                resumeScore = result.score,
                candidateName = result.extractedInfo.name,
                strongSkillsCsv = result.strongSkills.joinToString(","),
                missingSkillsCsv = result.missingSkills.joinToString(","),
                recommendedQuestionsJson = result.recommendedQuestions.joinToString(";")
            )
        )
        return result
    }

    suspend fun saveInterviewResult(session: InterviewSession) {
        resultDao.insertResult(
            InterviewResultEntity(
                id = session.id,
                title = session.title,
                category = session.category,
                scorePercentage = session.scorePercentage,
                date = session.date,
                totalQuestions = session.totalQuestions,
                durationMinutes = session.durationMinutes,
                detailedFeedback = session.detailedFeedback
            )
        )
        com.example.data.remote.FirebaseService.saveInterviewResultToFirestore(session)

        // Calculate and update real performance summary based on actual test results
        val existingEntity = userProfileDao.getSingleProfile()
        val currentProfile = existingEntity?.toDomain() ?: UserProfile()
        val allResults = resultDao.getResultsList()

        val totalQuestions = allResults.sumOf { it.totalQuestions }
        val avgScore = if (allResults.isNotEmpty()) allResults.map { it.scorePercentage }.average().toInt() else 0
        val readiness = if (allResults.isNotEmpty()) (avgScore * 0.85 + (allResults.size * 3).coerceAtMost(15)).toInt().coerceIn(0, 100) else 0
        val streak = allResults.size.coerceAtLeast(1)

        val updatedProfile = currentProfile.copy(
            questionsAttempted = totalQuestions,
            averageScore = avgScore,
            overallScore = avgScore,
            readinessPercentage = readiness,
            currentStreak = streak
        )

        userProfileDao.insertOrUpdateProfile(updatedProfile.toEntity())
        com.example.data.remote.FirebaseService.saveUserProfileToFirestore(updatedProfile)
    }

    suspend fun seedInitialQuestionsIfEmpty() {
        val sample = getInitialSampleQuestions().map { it.toEntity() }
        questionDao.insertQuestions(sample)
    }

    private fun QuestionEntity.toDomain(): Question {
        return Question(
            id = id,
            category = try { Category.valueOf(category) } catch (e: Exception) { Category.AWS_CLOUD },
            subcategory = subcategory,
            difficulty = try { Difficulty.valueOf(difficulty) } catch (e: Exception) { Difficulty.INTERMEDIATE },
            type = try { QuestionType.valueOf(type) } catch (e: Exception) { QuestionType.TECHNICAL_EXPLANATION },
            title = title,
            prompt = prompt,
            options = if (optionsJson.isBlank()) emptyList() else optionsJson.split("|"),
            correctAnswer = correctAnswer,
            explanation = explanation,
            sampleCode = sampleCode
        )
    }

    private fun Question.toEntity(): QuestionEntity {
        return QuestionEntity(
            id = id,
            category = category.name,
            subcategory = subcategory,
            difficulty = difficulty.name,
            type = type.name,
            title = title,
            prompt = prompt,
            optionsJson = options.joinToString("|"),
            correctAnswer = correctAnswer,
            explanation = explanation,
            sampleCode = sampleCode
        )
    }

    private fun UserProfileEntity.toDomain(): UserProfile {
        return UserProfile(
            fullName = fullName,
            email = email,
            college = college,
            degree = degree,
            graduationYear = graduationYear,
            primarySkill = primarySkill,
            experienceLevel = experienceLevel,
            overallScore = overallScore,
            questionsAttempted = questionsAttempted,
            averageScore = averageScore,
            currentStreak = currentStreak,
            readinessPercentage = readinessPercentage
        )
    }

    private fun UserProfile.toEntity(): UserProfileEntity {
        return UserProfileEntity(
            email = email,
            fullName = fullName,
            college = college,
            degree = degree,
            graduationYear = graduationYear,
            primarySkill = primarySkill,
            experienceLevel = experienceLevel,
            overallScore = overallScore,
            questionsAttempted = questionsAttempted,
            averageScore = averageScore,
            currentStreak = currentStreak,
            readinessPercentage = readinessPercentage
        )
    }

    fun getInitialSampleQuestions(): List<Question> {
        return listOf(
            Question(
                id = "aws-1",
                category = Category.AWS_CLOUD,
                subcategory = "VPC & Networking",
                difficulty = Difficulty.INTERMEDIATE,
                type = QuestionType.TECHNICAL_EXPLANATION,
                title = "VPC Security Groups vs Network ACLs",
                prompt = "Explain the fundamental differences between Security Groups and Network Access Control Lists (NACLs) in AWS VPC, focusing on statefulness and evaluation order.",
                explanation = "Security Groups are stateful firewalls attached to ENIs operating at the instance level. NACLs are stateless firewalls operating at the subnet level evaluated in rule-number order."
            ),
            Question(
                id = "aws-2",
                category = Category.AWS_CLOUD,
                subcategory = "EC2 & Scaling",
                difficulty = Difficulty.BEGINNER,
                type = QuestionType.MCQ,
                title = "EC2 Instance Purchase Types",
                prompt = "Which AWS EC2 purchase option is best suited for workloads with flexible start/end times that can withstand interruption in exchange for up to 90% savings?",
                options = listOf("On-Demand Instances", "Reserved Instances", "Spot Instances", "Dedicated Hosts"),
                correctAnswer = "Spot Instances",
                explanation = "Spot Instances offer up to 90% discount compared to On-Demand prices for workloads that can tolerate brief terminations."
            ),
            Question(
                id = "aws-3",
                category = Category.AWS_CLOUD,
                subcategory = "S3 Storage",
                difficulty = Difficulty.ADVANCED,
                type = QuestionType.SCENARIO_BASED,
                title = "S3 Consistency & Lifecycle Architecture",
                prompt = "Design an automated tiered storage solution using S3 Bucket Lifecycle rules for high-frequency logs that transition to Glacier Instant Retrieval after 30 days and Glacier Deep Archive after 90 days.",
                explanation = "S3 Lifecycle policies transition objects based on age. Instant Retrieval provides ms retrieval, whereas Deep Archive offers lowest cost for long-term audit logs."
            ),
            Question(
                id = "devops-1",
                category = Category.CLOUD_DEVOPS,
                subcategory = "Docker & Containers",
                difficulty = Difficulty.INTERMEDIATE,
                type = QuestionType.TECHNICAL_EXPLANATION,
                title = "Docker Container vs VM Architecture",
                prompt = "Describe how Docker container isolation works using Linux kernel cgroups and namespaces compared to hypervisor-based Virtual Machines.",
                explanation = "Docker shares the host OS kernel and isolates processes using PID/NET namespaces and resource caps via cgroups, whereas VMs virtualize physical hardware via a hypervisor."
            ),
            Question(
                id = "devops-2",
                category = Category.CLOUD_DEVOPS,
                subcategory = "Kubernetes",
                difficulty = Difficulty.ADVANCED,
                type = QuestionType.SCENARIO_BASED,
                title = "K8s Deployment Ingress & Pod Autoscaling",
                prompt = "How do Horizontal Pod Autoscaler (HPA) and Cluster Autoscaler cooperate in Kubernetes during sudden high-traffic spikes?",
                explanation = "HPA scales out pod replicas based on CPU/Memory metrics. When node resources are exhausted, Cluster Autoscaler provisions new worker nodes."
            ),
            Question(
                id = "dsa-1",
                category = Category.DATA_STRUCTURES,
                subcategory = "Trees & Graphs",
                difficulty = Difficulty.INTERMEDIATE,
                type = QuestionType.CODING,
                title = "Binary Tree Level Order Traversal",
                prompt = "Given the root of a binary tree, return the level order traversal of its nodes' values (i.e. left to right, level by level).",
                sampleCode = "class TreeNode {\n  int val;\n  TreeNode left, right;\n}\n\nList<List<Integer>> levelOrder(TreeNode root) {\n  // Implement BFS using Queue\n}",
                explanation = "Use a Queue data structure to perform Breadth-First Search (BFS), tracking queue size at each level."
            ),
            Question(
                id = "dsa-2",
                category = Category.DATA_STRUCTURES,
                subcategory = "Dynamic Programming",
                difficulty = Difficulty.ADVANCED,
                type = QuestionType.CODING,
                title = "Longest Increasing Subsequence",
                prompt = "Given an integer array nums, return the length of the longest strictly increasing subsequence in O(N log N) time.",
                sampleCode = "def lengthOfLIS(nums: List[int]) -> int:\n    # Implement DP or Binary Search (Patience Sorting)",
                explanation = "Maintain an array tails using binary search (bisect_left) to update active candidate subsequences in O(N log N) time."
            ),
            Question(
                id = "sd-1",
                category = Category.SYSTEM_DESIGN,
                subcategory = "Scalability & Caching",
                difficulty = Difficulty.ADVANCED,
                type = QuestionType.SCENARIO_BASED,
                title = "Design a Distributed Rate Limiter",
                prompt = "Architect a distributed rate limiter for a high-traffic API Gateway capable of handling 100,000 requests/sec. Discuss Token Bucket vs Sliding Window Logs using Redis.",
                explanation = "Redis with Lua scripts guarantees atomic updates for Sliding Window Counter algorithms across cluster nodes with sub-millisecond latency."
            ),
            Question(
                id = "se-1",
                category = Category.SOFTWARE_ENGINEERING,
                subcategory = "DBMS & SQL",
                difficulty = Difficulty.INTERMEDIATE,
                type = QuestionType.SHORT_ANSWER,
                title = "ACID Properties in Transactions",
                prompt = "Define the Isolation levels in relational databases (Read Uncommitted, Read Committed, Repeatable Read, Serializable) and the phenomena they prevent (Dirty Read, Non-Repeatable Read, Phantom Read).",
                explanation = "Serializable prevents dirty reads, non-repeatable reads, and phantom reads using two-phase locking or multi-version concurrency control (MVCC)."
            )
        )
    }

    private fun getSampleInterviewResults(): List<InterviewSession> {
        return listOf(
            InterviewSession(
                id = "sess-101",
                title = "AWS Infrastructure & Cloud Security Mock",
                category = "AWS Cloud",
                scorePercentage = 86,
                date = "12 Aug 2026",
                totalQuestions = 10,
                durationMinutes = 18,
                detailedFeedback = "Excellent understanding of EC2 security groups and IAM role delegation. Strong performance on S3 bucket policies."
            ),
            InterviewSession(
                id = "sess-100",
                title = "DevOps CI/CD & Kubernetes Evaluation",
                category = "Cloud & DevOps",
                scorePercentage = 78,
                date = "09 Aug 2026",
                totalQuestions = 8,
                durationMinutes = 15,
                detailedFeedback = "Good grasp of Docker container layers and git branch workflows. Recommended reviewing Kubernetes Ingress controller rules."
            ),
            InterviewSession(
                id = "sess-099",
                title = "System Design & Distributed Caching",
                category = "System Design",
                scorePercentage = 82,
                date = "05 Aug 2026",
                totalQuestions = 6,
                durationMinutes = 22,
                detailedFeedback = "Solid architectural approach to database sharding and Redis cache invalidation strategies."
            )
        )
    }
}
