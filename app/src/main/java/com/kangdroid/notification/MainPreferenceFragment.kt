package com.kangdroid.notification

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kangdroid.notification.server.CallAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainPreferenceFragment : PreferenceFragmentCompat() {
    private val TAG_VAL: String = "MainPreference"
    private var mServerStatus: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)

        // Preference
        mServerStatus = findPreference("server_status") as Preference?
        mServerStatus?.title = "Server Status: OFF"
        check_server_alive()
    }

    /**
     * GET Method
     */
    fun check_server_alive() {
        val BASE_URL: String = "http://192.168.0.46:8080/get/NotifCount/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(CallAPI::class.java)
        val getValueTmp = api.getNotificationCount()
        getValueTmp.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d(TAG_VAL, "Successful Response!")
                    mServerStatus?.title = "Server Status: ON"
                } else {
                    Log.e(TAG_VAL, "onResponse is called, but value was not successful")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG_VAL, "$t")
            }
        })
    }
}