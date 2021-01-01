package com.kangdroid.notification

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isNotificationGranted()) {
            val intent = Intent( "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            showWarningDialog(intent)
        }
        setContentView(R.layout.activity_main)
    }

    fun isNotificationGranted(): Boolean {
        val mNotifSets = NotificationManagerCompat.getEnabledListenerPackages(this)
        return mNotifSets.contains(packageName)
    }

    fun showWarningDialog(intent: Intent) {
        var innerDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        with (innerDialog) {
            setTitle("Notice")
            setMessage("Notification access permission is not granted yet.\nPress ok to open settings, press no to quit this application.")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                startActivity(intent)
            })
            setNegativeButton("No") { dialog, which ->
                finishAffinity()
            }
            show()
        }
    }
}