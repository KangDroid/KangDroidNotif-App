package com.kangdroid.notification.server

import android.util.Log
import com.kangdroid.notification.dto.NotificationData
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.IllegalArgumentException
import kotlin.collections.HashMap

class ServerManagement {
    companion object {
        private val TAG_SERVER = "ServerManagement"
        private lateinit var mRetrofit: Retrofit
        private lateinit var mApi: CallAPI

        var mServerBaseUrl : String = "http://192.168.0.46"
        var mServerPort: String = "8080"

        fun initServer(): Boolean {
            try {
                mRetrofit = Retrofit.Builder()
                    .baseUrl("$mServerBaseUrl:$mServerPort")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            } catch (e: IllegalArgumentException) {

                // When IAE Occurred
                Log.e(TAG_SERVER, "Error occurred when connecting server: ${e.message}")
                e.printStackTrace()

                return false
            }
            mApi = mRetrofit.create(CallAPI::class.java)

            return true
        }

        fun checkServerAlive(): Boolean {
            if (!initServer()) {
                return false
            }
            val mGetValue = mApi.getNotificationCount()
            var mResponse: Response<String>? = null

            try {
                mResponse = mGetValue.execute()
            } catch (e: Exception) {
                Log.e(TAG_SERVER, "Error Connecting server: ${mServerBaseUrl}:${mServerPort}")
                Log.e(TAG_SERVER, e.stackTraceToString())
            }

            return mResponse?.isSuccessful ?: false
        }

        /**
         * POST Method
         */
        fun call_post_retro(title: String?, content: String?, reqPackage: String?): Boolean {
            if (!initServer()) {
                return false
            }
            /**
             * TODO: Prompt to user
             * TODO: Also give more debugging information.
             */
            if (title == null || content == null || reqPackage == null) {
                Log.e(TAG_SERVER, "Either of title/content/reqPackage is NULL. Skipping posting.")
                return false
            }

            var inputParam: HashMap<String, Any> = HashMap()
            with(inputParam) {
                put("reqPackage", reqPackage)
                put("title", title)
                put("content", content)
            }

            val mPostValue = mApi.postTestValue(inputParam)
            var mResponse: Response<NotificationData>? = null

            try {
                mResponse = mPostValue.execute()
            } catch (e: Exception) {
                Log.e(TAG_SERVER, e.stackTraceToString())
            }

            return mResponse?.isSuccessful ?: false
        }
    }
}