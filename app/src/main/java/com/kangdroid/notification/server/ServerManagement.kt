package com.kangdroid.notification.server

import android.util.Log
import com.kangdroid.notification.dto.NotificationData
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ServerManagement {
    private val TAG_SERVER = "ServerManagement"
    private val mServerBaseUrl : String = "http://192.168.0.46"
    private val mServerPort: String = "8080"
    companion object {
        var mServerStatus: Boolean = false
    }

    fun checkServerAlive(mUpdateCallback: ((Any, Boolean) -> Unit)?, mArgs: Any?) {
        val mRetrofit = Retrofit.Builder()
                .baseUrl("$mServerBaseUrl:$mServerPort")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val mApi = mRetrofit.create(CallAPI::class.java)
        val mGetValue = mApi.getNotificationCount()
        mGetValue.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                mServerStatus = if (response.isSuccessful) {
                    Log.d(TAG_SERVER, "Successful Response!")
                    true
                } else {
                    Log.e(TAG_SERVER, "onResponse is called, but value was not successful")
                    false
                }

                if (mUpdateCallback != null && mArgs != null) {
                    mUpdateCallback(mArgs, mServerStatus)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG_SERVER, "$t")
                mServerStatus = false
            }
        })
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
        if (!mServerStatus) {
            Log.e(TAG_SERVER, "Server is NOT running")
            return
        }
        /**
         * TODO: Prompt to user
         * TODO: Also give more debugging information.
         */
        if (title == null || content == null || reqPackage == null) {
            Log.e(TAG_SERVER, "Either of title/content/reqPackage is NULL. Skipping posting.")
            return
        }
        val mRetrofit = Retrofit.Builder()
                .baseUrl("$mServerBaseUrl:$mServerPort")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val mApi = mRetrofit.create(CallAPI::class.java)

        var inputParam: HashMap<String, Any> = HashMap()
        with(inputParam) {
            put("reqPackage", reqPackage)
            put("title", title)
            put("content", content)
            put("genDate", getCurDateInFormat())
        }

        mApi.postTestValue(inputParam).enqueue(object : Callback<NotificationData> {
            override fun onResponse(
                    call: Call<NotificationData>,
                    response: Response<NotificationData>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG_SERVER, "Post completed!")
                } else {
                    Log.e(TAG_SERVER, "onResponse: But Failed")
                }
            }

            override fun onFailure(call: Call<NotificationData>, t: Throwable) {
                Log.e(TAG_SERVER, "$t")
            }
        })
    }
}