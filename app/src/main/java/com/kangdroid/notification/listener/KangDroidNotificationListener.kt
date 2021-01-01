package com.kangdroid.notification.listener

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.kangdroid.notification.dto.NotificationData
import com.kangdroid.notification.server.CallAPI
import com.kangdroid.notification.server.ServerManagement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class KangDroidNotificationListener : NotificationListenerService() {
    val TAG_VAL: String = "RELKangDroidNotificationListener"
    val mServerManager: ServerManagement = ServerManagement()

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG_VAL, "Removed: ${sbn.toString()}")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn?.notification?.flags?.and(Notification.FLAG_GROUP_SUMMARY) !== 0) {
            return
        }

        Log.d(TAG_VAL, "Total Debug: $sbn")
        Log.d(TAG_VAL, "Package Name: ${sbn.packageName}")
        Log.d(
            TAG_VAL,
            "Notification Title: ${sbn.notification?.extras?.getString("android.title")}"
        )
        Log.d(TAG_VAL, "Notification Text: ${sbn.notification?.extras?.getString("android.text")}")

        // Call post
        mServerManager.call_post_retro("${sbn.notification?.extras?.getString("android.title")}", "${sbn.notification?.extras?.getString("android.text")}", sbn.packageName)
    }
}