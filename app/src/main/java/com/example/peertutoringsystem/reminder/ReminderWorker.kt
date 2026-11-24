package com.example.peertutoringsystem.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.peertutoringsystem.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Runs shortly before a session and shows a notification, even if the app is closed
class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val sessionId = inputData.getInt(KEY_SESSION_ID, -1)
        val dateTime = inputData.getLong(KEY_DATE_TIME, 0L)
        val mode = inputData.getString(KEY_MODE) ?: ""
        val place = inputData.getString(KEY_LOCATION) ?: ""

        // Android 13+: no permission means no notification (the app asks for it in commit 7)
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        createChannel()

        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(dateTime))
        val modeText = if (mode == "ONLINE") "Online" else "In person"

        val openApp = PendingIntent.getActivity(
            applicationContext, 0,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Tutoring session soon")
            .setContentText("$modeText at $time: $place")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(sessionId, notification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Session reminders", NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "session_reminders"
        const val KEY_SESSION_ID = "session_id"
        const val KEY_DATE_TIME = "date_time"
        const val KEY_MODE = "mode"
        const val KEY_LOCATION = "location"
    }
}