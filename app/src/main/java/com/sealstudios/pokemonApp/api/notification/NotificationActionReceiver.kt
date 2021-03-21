package com.sealstudios.pokemonApp.api.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_ACTION_KEY
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_CANCEL_WORK_KEY

class NotificationActionReceiver(private val notificationClickListener: NotificationClickListener) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationAction = intent?.getStringExtra(NOTIFICATION_ACTION_KEY) ?: ""
        if (notificationAction == NOTIFICATION_CANCEL_WORK_KEY) {
            notificationClickListener.cancelAction()
            closeSystemTray(context)
        }
    }

    private fun closeSystemTray(context: Context?) {
        val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context?.sendBroadcast(it)
    }
}