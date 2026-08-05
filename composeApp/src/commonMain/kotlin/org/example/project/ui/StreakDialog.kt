package org.example.project.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.example.project.dailystreak.model.StreakEvent

@Composable
fun StreakDialog(
    event: StreakEvent,
    onDismiss: () -> Unit
) {

    val title: String
    val message: String

    when (event) {

        is StreakEvent.FirstPlay -> {
            title = "🔥 Your streak begins!"
            message =
                "Keep typing every day to build your streak."
        }


        is StreakEvent.StreakStarted -> {
            title = "🔥 ${event.streak} Day Streak!"
            message =
                "Great start! Come back tomorrow."
        }


        is StreakEvent.StreakContinued -> {
            title = "🔥 ${event.streak} Days!"
            message =
                "Your consistency is improving."
        }


        is StreakEvent.StreakBroken -> {
            title = "💔 Streak Broken"
            message =
                "Your ${event.previousStreak} day streak ended. Start again today!"
        }


        is StreakEvent.WelcomeBack -> {
            title = "🥺 We missed you!"
            message =
                "You have been away for ${event.daysAway} days. Let's get typing again!"
        }


        is StreakEvent.Milestone -> {
            title = "🎉 ${event.streak} Days!"
            message =
                "Amazing achievement!"
        }


        else -> return
    }


    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {
            Text(message)
        },

        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Continue")
            }
        }
    )
}