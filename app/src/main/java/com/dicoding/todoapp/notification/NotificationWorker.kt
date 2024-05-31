package com.dicoding.todoapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.TASK_ID

class NotificationWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)
    private val repository by lazy {
        TaskRepository.getInstance(ctx)
    }

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun doWork(): Result {

        val task = repository.getNearestActiveTask()
        val pendingIntent = getPendingIntent(task)
        // Create a Notification channel if necessary
        val notificationManager: NotificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            // Register the channel with the system
            // Register the channel with the system.

            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(task.title)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentText(
                ctx.getString(
                    R.string.notify_content,
                    DateConverter.convertMillisToString(task.dueDateMillis)
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSubText(
                ctx.getString(
                    R.string.notify_channel_name,
                )
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, builder)
        return Result.success()
    }

}
