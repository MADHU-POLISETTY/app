package com.example.data.remote

import android.util.Log
import com.example.domain.model.InterviewSession
import com.example.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FirebaseService {
    private const val TAG = "FirebaseService"
    
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Firestore Collections
    private const val COLLECTION_USERS = "user_profiles"
    private const val COLLECTION_RESULTS = "interview_results"

    /**
     * Registers or authenticates a user in Firebase Authentication so the account appears in Firebase Console Auth.
     */
    suspend fun registerOrAuthenticateWithFirebase(email: String, pass: String): String? {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty() || pass.isEmpty()) return null
        return try {
            val authResult = auth.createUserWithEmailAndPassword(cleanEmail, pass).await()
            val uid = authResult.user?.uid
            Log.d(TAG, "Successfully registered user in Firebase Auth with UID: $uid")
            uid
        } catch (e: Exception) {
            Log.w(TAG, "User registration in Firebase Auth encountered exception, attempting sign in: ${e.message}")
            try {
                val signInResult = auth.signInWithEmailAndPassword(cleanEmail, pass).await()
                val uid = signInResult.user?.uid
                Log.d(TAG, "Successfully signed in existing user in Firebase Auth with UID: $uid")
                uid
            } catch (e2: Exception) {
                Log.e(TAG, "Failed Firebase Auth registration/signin for $cleanEmail: ${e2.message}")
                null
            }
        }
    }

    /**
     * Saves or updates candidate user profile directly in Google Firebase Firestore Cloud Database.
     */
    suspend fun saveUserProfileToFirestore(userProfile: UserProfile): Boolean {
        return try {
            val sdfDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            val sdfTime = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
            val now = java.util.Date()
            val formattedDate = sdfDate.format(now)
            val formattedTime = sdfTime.format(now)

            val currentUid = auth.currentUser?.uid ?: ""

            val userMap = hashMapOf(
                "uid" to currentUid,
                "fullName" to userProfile.fullName,
                "email" to userProfile.email,
                "college" to userProfile.college,
                "degree" to userProfile.degree,
                "graduationYear" to userProfile.graduationYear,
                "primarySkill" to userProfile.primarySkill,
                "experienceLevel" to userProfile.experienceLevel,
                "overallScore" to userProfile.overallScore,
                "questionsAttempted" to userProfile.questionsAttempted,
                "averageScore" to userProfile.averageScore,
                "currentStreak" to userProfile.currentStreak,
                "readinessPercentage" to userProfile.readinessPercentage,
                "registrationDate" to formattedDate,
                "lastActiveDate" to formattedDate,
                "lastSyncTime" to formattedTime,
                "updatedAt" to System.currentTimeMillis()
            )

            val documentKey = userProfile.email.trim().lowercase().replace(".", "_")

            firestore.collection(COLLECTION_USERS)
                .document(documentKey)
                .set(userMap, SetOptions.merge())
                .await()

            if (currentUid.isNotEmpty()) {
                firestore.collection(COLLECTION_USERS)
                    .document(currentUid)
                    .set(userMap, SetOptions.merge())
                    .await()
            }

            Log.d(TAG, "Successfully saved user profile with performance stats to Firebase Firestore: ${userProfile.email}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile to Firebase Firestore", e)
            false
        }
    }

    /**
     * Retrieves user profile from Firebase Firestore Cloud Database.
     */
    suspend fun getUserProfileFromFirestore(emailKey: String = "user_example_com"): UserProfile? {
        return try {
            val snapshot = firestore.collection(COLLECTION_USERS)
                .document(emailKey)
                .get()
                .await()
            
            if (snapshot.exists()) {
                UserProfile(
                    fullName = snapshot.getString("fullName") ?: "Candidate User",
                    email = snapshot.getString("email") ?: "candidate@example.com",
                    college = snapshot.getString("college") ?: "University Name",
                    degree = snapshot.getString("degree") ?: "B.S. Computer Science",
                    graduationYear = snapshot.getString("graduationYear") ?: "2026",
                    primarySkill = snapshot.getString("primarySkill") ?: "Software Engineering",
                    overallScore = snapshot.getLong("overallScore")?.toInt() ?: 0,
                    questionsAttempted = snapshot.getLong("questionsAttempted")?.toInt() ?: 0,
                    averageScore = snapshot.getLong("averageScore")?.toInt() ?: 0,
                    currentStreak = snapshot.getLong("currentStreak")?.toInt() ?: 0,
                    readinessPercentage = snapshot.getLong("readinessPercentage")?.toInt() ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user profile from Firebase Firestore", e)
            null
        }
    }

    /**
     * Saves interview assessment session results to Firebase Firestore.
     */
    suspend fun saveInterviewResultToFirestore(session: InterviewSession): Boolean {
        return try {
            val resultMap = hashMapOf(
                "id" to session.id,
                "title" to session.title,
                "category" to session.category,
                "scorePercentage" to session.scorePercentage,
                "date" to session.date,
                "totalQuestions" to session.totalQuestions,
                "durationMinutes" to session.durationMinutes,
                "detailedFeedback" to session.detailedFeedback,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection(COLLECTION_RESULTS)
                .document(session.id)
                .set(resultMap, SetOptions.merge())
                .await()
            Log.d(TAG, "Successfully saved interview result to Firebase Firestore: ${session.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving interview result to Firebase Firestore", e)
            false
        }
    }
}
