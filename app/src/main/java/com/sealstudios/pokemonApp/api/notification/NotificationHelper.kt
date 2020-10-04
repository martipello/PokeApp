package com.sealstudios.pokemonApp.api.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.work.ForegroundInfo
import com.sealstudios.pokemonApp.MainActivity
import com.sealstudios.pokemonApp.R


class NotificationHelper constructor(
    private val applicationContext: Context
) {

    fun sendOnGoingNotification(
        id: Int,
        title: String,
        progressText: String,
        progress: Int,
        max: Int
    ): ForegroundInfo {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID_KEY, id)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val largeIcon = getLargeNotificationIcon()
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
        val notification =
            buildNotificationBuilder(largeIcon, title, progressText, pendingIntent)

        Log.d("NOTIFICATION", "progress is $progress max is $max")

        with(notification) {
            createChannels(this, notificationManager)
            priority = if (progress != max) PRIORITY_MAX else PRIORITY_MIN
            setOngoing(progress != max)
            setProgress(max, progress, false)
            setContentText(progressText)
            setContentTitle(title)
            setTicker(title)
        }
        return ForegroundInfo(id, notification.build())

    }

    fun sendNotification(id: Int, subtitle: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID_KEY, id)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val bitmap = getLargeNotificationIcon()
        val titleNotification = applicationContext.getString(R.string.notification_title)
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
        val notification =
            buildNotificationBuilder(bitmap, titleNotification, subtitle, pendingIntent)

        createChannels(notification, notificationManager)
        notification.priority = PRIORITY_MAX
        notificationManager.notify(id, notification.build())
    }

    private fun getLargeNotificationIcon(): Bitmap? {
        return BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.pokeball_vector
        )
    }

    private fun buildNotificationBuilder(
        bitmap: Bitmap?,
        title: String,
        subtitle: String,
        pendingIntent: PendingIntent?
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.pokeball_vector)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    private fun createChannels(
        notification: NotificationCompat.Builder,
        notificationManager: NotificationManager
    ) {
        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)
            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val NOTIFICATION_ID_KEY = "com.sealstudios.pokemonApp.notification_id"
        const val NOTIFICATION_NAME = "PokemonApp"
        const val NOTIFICATION_CHANNEL = "com.sealstudios.pokemonApp.download_channel"
    }
}
