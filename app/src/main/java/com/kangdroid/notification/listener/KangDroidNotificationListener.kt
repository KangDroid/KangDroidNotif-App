package com.kangdroid.notification.listener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class KangDroidNotificationListener: NotificationListenerService() {
    val TAG_VAL:String = "RELKangDroidNotificationListener"

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG_VAL, "Removed: ${sbn.toString()}")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.d(TAG_VAL, "Total Debug: ${sbn.toString()}")
        Log.d(TAG_VAL, "Package Name: ${sbn?.packageName}")
        Log.d(TAG_VAL, "Notification Title: ${sbn?.notification?.extras?.getString("android.title")}")
        Log.d(TAG_VAL, "Notification Text: ${sbn?.notification?.extras?.getString("android.text")}")
    }
}