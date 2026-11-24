package com.example.peertutoringsystem.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.peertutoringsystem.data.model.Session
import java.util.concurrent.TimeUnit

// Sets and cancels the reminder for a session
object ReminderScheduler {

    // How long before the session the reminder appears
    private const val MINUTES_BEFORE = 15L

    private fun workName(sessionId: Int) = "session_reminder_$sessionId"

    // Schedules (or replaces) the reminder for this session
    fun schedule(context: Context, session: Session) {
        val reminderTime = session.dateTime - TimeUnit.MINUTES.toMillis(MINUTES_BEFORE)
        val delay = reminderTime - System.currentTimeMillis()

        val data = workDataOf(
            ReminderWorker.KEY_SESSION_ID to session.sessionId,
            ReminderWorker.KEY_DATE_TIME to session.dateTime,
            ReminderWorker.KEY_MODE to session.mode,
            ReminderWorker.KEY_LOCATION to session.locationOrLink
        )

        // If the session is less than 15 minutes away, the reminder fires straight away
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(maxOf(delay, 0L), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        // REPLACE means rescheduling a session swaps the old reminder for the new one
        WorkManager.getInstance(context).enqueueUniqueWork(
            workName(session.sessionId), ExistingWorkPolicy.REPLACE, request
        )
    }

    // Cancels the reminder (used when a session is cancelled or completed)
    fun cancel(context: Context, sessionId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(sessionId))
    }
}