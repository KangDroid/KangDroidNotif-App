package com.kangdroid.notification

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isNotificationGranted()) {
            val intent = Intent( "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
        setContentView(R.layout.activity_main)
    }

    fun isNotificationGranted(): Boolean {
        val mNotifSets = NotificationManagerCompat.getEnabledListenerPackages(this)
        return mNotifSets.contains(packageName)
    }
}