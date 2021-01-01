package com.kangdroid.notification.listener

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.kangdroid.notification.dto.NotificationData
import com.kangdroid.notification.server.CallAPI
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
        call_post_retro("${sbn.notification?.extras?.getString("android.title")}", "${sbn.notification?.extras?.getString("android.text")}", sbn.packageName)
    }

    fun getCurDateInFormat(): String {
        val todayDate: Date = Calendar.getInstance().time
        val formatDate: DateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        return formatDate.format(todayDate)
    }

    /**
     * POST Method
     */
    fun call_post_retro(title: String?, content: String?, reqPackage: String?) {
        /**
         * TODO: Prompt to user
         * TODO: Also give more debugging information.
         */
        if (title == null || content == null || reqPackage == null) {
            Log.e(TAG_VAL, "Either of title/content/reqPackage is NULL. Skipping posting.")
            return
        }
        val BASE_URL: String = "http://192.168.0.46:8080/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(CallAPI::class.java)

        var inputParam: HashMap<String, Any> = HashMap()
        with(inputParam) {
            put("reqPackage", reqPackage)
            put("title", title)
            put("content", content)
            put("genDate", getCurDateInFormat())
        }

        api.postTestValue(inputParam).enqueue(object : Callback<NotificationData> {
            override fun onResponse(
                call: Call<NotificationData>,
                response: Response<NotificationData>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG_VAL, "Post completed!")
                } else {
                    Log.e(TAG_VAL, "onResponse: But Failed")
                }
            }

            override fun onFailure(call: Call<NotificationData>, t: Throwable) {
                Log.e(TAG_VAL, "$t")
            }
        })
    }
}