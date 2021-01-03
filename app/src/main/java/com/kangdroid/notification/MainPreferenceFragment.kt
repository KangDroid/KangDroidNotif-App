package com.kangdroid.notification

import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.preference.*
import com.kangdroid.notification.server.ServerManagement
import com.kangdroid.notification.settings.Settings
import java.lang.IllegalArgumentException

class MainPreferenceFragment : PreferenceFragmentCompat(),  Preference.OnPreferenceChangeListener {
    private val TAG_VAL: String = "MainPreference"
    private var mServerStatus: Preference? = null
    private val mServerManagement: ServerManagement = ServerManagement()
    private var mDisableCharging: SwitchPreference? = null
    private var mCheckServerManual: Preference? = null
    private var mServerURLEditor: EditTextPreference? = null
    private var mServerPortEditor: EditTextPreference? = null
    private lateinit var mUpdateServerStatus: (Any, Boolean) -> Unit

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)

        // Set this object to ServerManagement
        ServerManagement.mMainUI = this

        // Shared Preference for getting values
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)


        // Preference
        mServerStatus = findPreference("server_status") as Preference?
        mServerStatus?.title = "Server Status: OFF"

        // Lambda function to update server status with "checkServerAlive()"
        mUpdateServerStatus = {
            mServerPane, mStatus ->
            if (mStatus) {
                (mServerPane as Preference).title = "Server Status: ON"
            } else {
                (mServerPane as Preference).title = "Server Status: OFF"
            }
        }
        mServerManagement.checkServerAlive(mUpdateServerStatus, mServerStatus)

        // Charging-Disable SwitchPreference
        mDisableCharging = findPreference("disable_charging_state") as SwitchPreference?
        Settings.Companion.mDisableChargingNotification = mSharedPreference.getBoolean("disable_charging_state", false)
        mDisableCharging?.setOnPreferenceChangeListener(this)

        // Manual Server Refresh
        mCheckServerManual = findPreference("server_reload") as Preference?
        mCheckServerManual?.setOnPreferenceClickListener {
            if (it.key == "server_reload") {
                mServerManagement.checkServerAlive(mUpdateServerStatus, mServerStatus)
            }
            true
        }

        // Server URL
        mServerURLEditor = findPreference("enter_server_url") as EditTextPreference?
        mServerURLEditor?.setOnPreferenceChangeListener(this)

        // Server Port
        mServerPortEditor = findPreference("enter_server_port") as EditTextPreference?
        mServerPortEditor?.setOnPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference?.key == "enter_server_url") {
            ServerManagement.mServerBaseUrl = newValue as String
            mServerManagement.checkServerAlive(null, 0)
        } else if (preference?.key == "enter_server_port") {
            ServerManagement.mServerPort = newValue as String
            mServerManagement.checkServerAlive(null, 1)
        } else if (preference?.key == "disable_charging_state") {
            Settings.Companion.mDisableChargingNotification = newValue as Boolean
        }
        return true
    }

    /**
     * Mode: 0 for ServerURL Editor, 1 for ServerPORT Editor
     */
    fun updateServerStatusUI(mode: Int) {
        when(mode) {
            // When ServerURL
            0 -> {
                mServerURLEditor?.summary = (if (ServerManagement.mServerStatus) {
                    ServerManagement.mServerBaseUrl
                } else {
                    "Error"
                }).toString()
            }

            // When ServerPORT
            1 -> {
                mServerPortEditor?.summary = (if (ServerManagement.mServerStatus) {
                    ServerManagement.mServerPort
                } else {
                    "Error"
                }).toString()
            }
        }
    }
}