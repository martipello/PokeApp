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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.work.ForegroundInfo
import com.sealstudios.pokemonApp.MainActivity
import com.sealstudios.pokemonApp.R


class NotificationHelper constructor(
        private val applicationContext: Context
) {

    fun sendFetchAllPokemonDataNotification(
            notificationArguments: NotificationArguments
    ): ForegroundInfo {

        val intent = createOpenActivityIntent(notificationArguments)
        val largeIcon = getLargeNotificationIcon()
        val openActivityPendingIntent = getActivity(applicationContext, 0, intent, 0)

        val cancelWorkIntent = cancelWorkIntent(notificationArguments)
        val cancelWorkPendingIntent = getActivity(applicationContext, 0, cancelWorkIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = buildNotificationBuilder(
                largeIcon,
                notificationArguments.title,
                notificationArguments.progressText,
                openActivityPendingIntent,
                cancelWorkPendingIntent)

        val notificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        with(notification) {
            createChannels(this, notificationManager)
            priority = if (notificationArguments.isPriority()) PRIORITY_MAX else PRIORITY_MIN
            setOngoing(notificationArguments.progress != notificationArguments.progressMax)
            setProgress(notificationArguments.progressMax, notificationArguments.progress, notificationArguments.indeterminate)
            setContentText(notificationArguments.progressText)
            setContentTitle(notificationArguments.title)
            setTicker(notificationArguments.title)
        }
        return ForegroundInfo(notificationArguments.id, notification.build())

    }

    private fun createOpenActivityIntent(notificationArguments: NotificationArguments): Intent {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID_KEY, notificationArguments.id)
        return intent
    }

    private fun cancelWorkIntent(notificationArguments: NotificationArguments): Intent {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(NOTIFICATION_CANCEL_WORK_KEY, notificationArguments.id)
        return intent
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
            contentIntent: PendingIntent?,
            cancelIntent: PendingIntent?
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.pokeball_vector)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSilent(true)
                .setContentIntent(contentIntent)
                .addAction(NotificationCompat.Action(null, "Cancel", cancelIntent))
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
        const val NOTIFICATION_CANCEL_WORK_KEY = "com.sealstudios.pokemonApp.notification_cancel_work"
        const val NOTIFICATION_NAME = "PokemonApp"
        const val NOTIFICATION_CHANNEL = "com.sealstudios.pokemonApp.download_channel"
    }
}

data class NotificationArguments(
        val id: Int,
        val title: String,
        val progressText: String,
        val progress: Int,
        val progressMax: Int,
        val indeterminate: Boolean = false
)

fun NotificationArguments.isPriority(): Boolean {
    return (progress != progressMax)
}
