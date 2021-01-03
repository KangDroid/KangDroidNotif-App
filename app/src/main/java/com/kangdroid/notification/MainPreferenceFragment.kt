package com.kangdroid.notification

import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.preference.*
import com.kangdroid.notification.server.ServerManagement
import com.kangdroid.notification.settings.Settings
import java.lang.IllegalArgumentException

class MainPreferenceFragment : PreferenceFragmentCompat(),  Preference.OnPreferenceChangeListener {
    // UI Constants
    private val KEY_SERVER_STATUS: String = "server_status"
    private val KEY_DISABLE_CHARGING: String = "disable_charging_state"
    private val KEY_SERVER_RELOAD: String = "server_reload"
    private val KEY_SERVER_URLEDIT: String = "enter_server_url"
    private val KEY_SERVER_PORTEDIT: String = "enter_server_port"

    // UI Variable
    private var mServerStatus: Preference? = null
    private var mDisableCharging: SwitchPreference? = null
    private var mCheckServerManual: Preference? = null
    private var mServerURLEditor: EditTextPreference? = null
    private var mServerPortEditor: EditTextPreference? = null

    // Server - Related Variable
    private val mServerManagement: ServerManagement = ServerManagement()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)

        // Set this object to ServerManagement
        ServerManagement.mMainUI = this

        // Shared Preference for getting values
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)


        // Preference
        mServerStatus = findPreference(KEY_SERVER_STATUS) as Preference?
        mServerStatus?.title = "Server Status: OFF"
        mServerManagement.checkServerAlive()

        // Charging-Disable SwitchPreference
        mDisableCharging = findPreference(KEY_DISABLE_CHARGING) as SwitchPreference?
        Settings.mDisableChargingNotification = mSharedPreference.getBoolean(KEY_DISABLE_CHARGING, false)
        mDisableCharging?.onPreferenceChangeListener = this

        // Manual Server Refresh
        mCheckServerManual = findPreference(KEY_SERVER_RELOAD) as Preference?
        mCheckServerManual?.setOnPreferenceClickListener {
            if (it.key == KEY_SERVER_RELOAD) {
                mServerManagement.checkServerAlive()
            }
            true
        }

        // Server URL
        mServerURLEditor = findPreference(KEY_SERVER_URLEDIT) as EditTextPreference?
        mServerURLEditor?.text = ServerManagement.mServerBaseUrl
        mServerURLEditor?.onPreferenceChangeListener = this

        // Server Port
        mServerPortEditor = findPreference(KEY_SERVER_PORTEDIT) as EditTextPreference?
        mServerPortEditor?.text = ServerManagement.mServerPort
        mServerPortEditor?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            // URL Edit Update
            KEY_SERVER_URLEDIT -> {
                ServerManagement.mServerBaseUrl = newValue as String
                mServerManagement.checkServerAlive()
            }

            // Port Edit Update
            KEY_SERVER_PORTEDIT -> {
                ServerManagement.mServerPort = newValue as String
                mServerManagement.checkServerAlive()
            }

            // Charging Notification Update
            KEY_DISABLE_CHARGING -> {
                Settings.mDisableChargingNotification = newValue as Boolean
            }
        }
        return true
    }

    fun updateServerStatusUI() {
        val mErrorString = "Error connecting server."

        if (ServerManagement.mServerStatus) {
            // Server URL Editor
            mServerURLEditor?.text = ServerManagement.mServerBaseUrl
            mServerURLEditor?.summary = ServerManagement.mServerBaseUrl

            // Server Port Editor
            mServerPortEditor?.text = ServerManagement.mServerPort
            mServerPortEditor?.summary = ServerManagement.mServerPort

            // Overall Server Connection State
            mServerStatus?.title = "Server Status: ON"

        } else {
            // Server URL Editor
            mServerURLEditor?.summary = mErrorString

            // Server Port Editor
            mServerPortEditor?.summary = mErrorString

            // Overall Server Connection State
            mServerStatus?.title = "Server Status: OFF"
        }
    }
}