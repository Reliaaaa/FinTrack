package com.example.fintrack.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val UNIQUE_WORK_NAME = "fintrack_daily_0845_briefing"
    private const val IMMEDIATE_WORK_NAME = "fintrack_immediate_briefing"

    /**
     * Schedules daily financial intelligence analysis by Gemini AI at 08:45 WIB.
     * Uses Asia/Jakarta (WIB, UTC+7) time reference.
     */
    fun scheduleDailyBriefing(context: Context) {
        val wibZone = TimeZone.getTimeZone("Asia/Jakarta")
        val now = Calendar.getInstance(wibZone)

        val target = Calendar.getInstance(wibZone).apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 45)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If 08:45 WIB has already passed today, schedule for 08:45 WIB tomorrow
        if (now.after(target)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelayMillis = target.timeInMillis - now.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<NewsSchedulerWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(NewsSchedulerWorker.WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }

    /**
     * Runs an immediate one-time briefing task for instant testing,
     * allowing the user to experience the Gemini AI analysis pop-up notification without waiting.
     */
    fun triggerImmediateNewsBriefing(context: Context) {
        val immediateRequest = OneTimeWorkRequestBuilder<NewsSchedulerWorker>()
            .addTag("immediate_test_briefing")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )
    }
}
