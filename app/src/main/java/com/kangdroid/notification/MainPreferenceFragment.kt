package com.kangdroid.notification

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kangdroid.notification.server.ServerManagement

class MainPreferenceFragment : PreferenceFragmentCompat() {
    private val TAG_VAL: String = "MainPreference"
    private var mServerStatus: Preference? = null
    private val mServerManagement: ServerManagement = ServerManagement()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)

        // Preference
        mServerStatus = findPreference("server_status") as Preference?
        mServerStatus?.title = "Server Status: OFF"

        // Lambda function to update server status with "checkServerAlive()"
        val mUpdateServerStatus: (Any, Boolean) -> Unit = {
            mServerPane, mStatus ->
            if (mStatus) {
                (mServerPane as Preference).title = "Server Status: ON"
            } else {
                (mServerPane as Preference).title = "Server Status: OFF"
            }
        }
        mServerManagement.checkServerAlive(mUpdateServerStatus, mServerStatus)
    }
}