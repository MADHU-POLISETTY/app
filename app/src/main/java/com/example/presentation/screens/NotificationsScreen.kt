package com.example.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IndigoPrimary
import com.example.util.NotificationHelper

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var dailyReminderEnabled by remember { mutableStateOf(true) }
    var resumeAlertsEnabled by remember { mutableStateOf(true) }
    var newQuestionsAlertsEnabled by remember { mutableStateOf(true) }

    val notificationsList = remember {
        mutableStateListOf(
            NotificationItem("1", "Daily Practice Reminder", "Complete 3 AWS VPC scenario questions to maintain your 4-day streak!", "2h ago"),
            NotificationItem("2", "AI Resume Feedback Ready", "Your resume fit score was updated to 85/100 with 3 recommended questions.", "1d ago"),
            NotificationItem("3", "New Question Pack Added", "15 new System Design & Cloud Scalability questions available in Question Bank.", "2d ago")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (notificationsList.isNotEmpty()) {
                        IconButton(onClick = {
                            notificationsList.clear()
                            Toast.makeText(context, "Notifications cleared", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = Color.Gray)
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Interactive Trigger Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_notification_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = IndigoPrimary)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "System Status Bar Notifications",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            text = "Test and send instant system status bar notifications to verify your device notification queue.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )

                        Button(
                            onClick = {
                                val title = "PrepWise Mock Drill Alert"
                                val body = "Time for your daily 10-minute AWS & DSA practice session! Boost your readiness score today."
                                
                                NotificationHelper.sendSystemNotification(context, title, body)
                                
                                notificationsList.add(
                                    0,
                                    NotificationItem(
                                        id = System.currentTimeMillis().toString(),
                                        title = title,
                                        body = body,
                                        timeAgo = "Just now"
                                    )
                                )
                                Toast.makeText(context, "System notification dispatched!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = IndigoPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("send_test_notification_btn")
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send System Notification Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Notification Settings Preferences
            item {
                Text("Alert Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Daily Practice Reminders", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Get reminded at 8:00 AM for streak drills", fontSize = 12.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = dailyReminderEnabled,
                                onCheckedChange = { dailyReminderEnabled = it }
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Resume Audit & Gap Alerts", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Receive notifications when resume scan completes", fontSize = 12.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = resumeAlertsEnabled,
                                onCheckedChange = { resumeAlertsEnabled = it }
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("New Question Bank Releases", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Notify when new tech categories are uploaded", fontSize = 12.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = newQuestionsAlertsEnabled,
                                onCheckedChange = { newQuestionsAlertsEnabled = it }
                            )
                        }
                    }
                }
            }

            // Notification Inbox Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Inbox Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${notificationsList.size} Notifications", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (notificationsList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notifications. Tap 'Send System Notification Now' above!", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                items(notificationsList, key = { it.id }) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notification_item_${notif.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = IndigoPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(notif.timeAgo, fontSize = 11.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    notif.body,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
