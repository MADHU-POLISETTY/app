package com.example.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.UserProfile
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onProfileUpdate: ((UserProfile) -> Unit)? = null,
    onSignOutClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }

    var editName by remember(userProfile) { mutableStateOf(userProfile.fullName) }
    var editEmail by remember(userProfile) { mutableStateOf(userProfile.email) }
    var editCollege by remember(userProfile) { mutableStateOf(userProfile.college) }
    var editDegree by remember(userProfile) { mutableStateOf(userProfile.degree) }
    var editSkill by remember(userProfile) { mutableStateOf(userProfile.primarySkill) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Candidate Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.testTag("edit_profile_icon")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Avatar Box
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                color = IndigoPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userProfile.fullName.ifBlank { "U" }.take(1),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userProfile.fullName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userProfile.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ProfileDetailRow(label = "Full Candidate Name", value = userProfile.fullName)
                    ProfileDetailRow(label = "Email Address", value = userProfile.email)
                    ProfileDetailRow(label = "Primary Skill Focus", value = userProfile.primarySkill)
                    ProfileDetailRow(label = "College / University", value = userProfile.college)
                    ProfileDetailRow(label = "Degree", value = userProfile.degree)
                    ProfileDetailRow(label = "Graduation Year", value = userProfile.graduationYear)
                    ProfileDetailRow(label = "Experience Level", value = userProfile.experienceLevel)
                }
            }

            // Edit Profile Button
            OutlinedButton(
                onClick = { isEditing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("edit_profile_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile Details", fontWeight = FontWeight.Bold)
            }

            // Sign Out Button
            Button(
                onClick = onSignOutClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sign_out_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out")
            }
        }

        if (isEditing) {
            AlertDialog(
                onDismissRequest = { isEditing = false },
                title = { Text("Edit Candidate Profile", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Candidate Full Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_name_input")
                        )
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editCollege,
                            onValueChange = { editCollege = it },
                            label = { Text("College / University") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editDegree,
                            onValueChange = { editDegree = it },
                            label = { Text("Degree Program") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editSkill,
                            onValueChange = { editSkill = it },
                            label = { Text("Primary Focus Skill") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editName.isNotBlank()) {
                                val updated = userProfile.copy(
                                    fullName = editName.trim(),
                                    email = editEmail.trim(),
                                    college = editCollege.trim(),
                                    degree = editDegree.trim(),
                                    primarySkill = editSkill.trim()
                                )
                                onProfileUpdate?.invoke(updated)
                                isEditing = false
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("save_profile_button")
                    ) {
                        Text("Save Changes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IndigoPrimary)
    }
}
