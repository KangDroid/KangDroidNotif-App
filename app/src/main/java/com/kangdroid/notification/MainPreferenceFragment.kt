package com.kangdroid.notification

import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.preference.*
import com.kangdroid.notification.exception.PreferenceNullException
import com.kangdroid.notification.server.ServerManagement
import com.kangdroid.notification.settings.Settings
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class MainPreferenceFragment : PreferenceFragmentCompat(),  Preference.OnPreferenceChangeListener {
    // UI Constants
    private val KEY_SERVER_STATUS: String = "server_status"
    private val KEY_DISABLE_CHARGING: String = "disable_charging_state"
    private val KEY_SERVER_RELOAD: String = "server_reload"
    private val KEY_SERVER_URLEDIT: String = "enter_server_url"
    private val KEY_SERVER_PORTEDIT: String = "enter_server_port"

    // UI Variable

    private lateinit var mServerStatus: Preference
    private lateinit var mDisableCharging: SwitchPreference
    private lateinit var mCheckServerManual: Preference
    private lateinit var mServerURLEditor: EditTextPreference
    private lateinit var mServerPortEditor: EditTextPreference

    // Server - Related Variable
    private val mServerManagement: ServerManagement = ServerManagement()

    // Server Monitoring
    private lateinit var mServerMonitor: Job

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)

        // Shared Preference for getting values
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)


        // Preference
        mServerStatus = findPreference(KEY_SERVER_STATUS) as? Preference ?: throw PreferenceNullException()
        mServerStatus.title = getString(R.string.server_off)
        GlobalScope.launch(Dispatchers.IO) {
            val mSucceed = mServerManagement.checkServerAlive()

            withContext(Dispatchers.Main) {
                updateServerStatusUI(mSucceed)
            }
        }

        // Charging-Disable SwitchPreference
        mDisableCharging = findPreference(KEY_DISABLE_CHARGING) as? SwitchPreference ?: throw PreferenceNullException()
        Settings.mDisableChargingNotification = mSharedPreference.getBoolean(KEY_DISABLE_CHARGING, false)
        mDisableCharging.onPreferenceChangeListener = this

        // Manual Server Refresh
        mCheckServerManual = findPreference(KEY_SERVER_RELOAD) as? Preference ?: throw PreferenceNullException()
        mCheckServerManual.setOnPreferenceClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val mSucceed = mServerManagement.checkServerAlive()

                withContext(Dispatchers.Main) {
                    updateServerStatusUI(mSucceed)
                }
            }
            true
        }

        // Server URL
        mServerURLEditor = findPreference(KEY_SERVER_URLEDIT) as? EditTextPreference ?: throw PreferenceNullException()
        mServerURLEditor.text = ServerManagement.mServerBaseUrl
        mServerURLEditor.onPreferenceChangeListener = this

        // Server Port
        mServerPortEditor = findPreference(KEY_SERVER_PORTEDIT) as? EditTextPreference ?: throw PreferenceNullException()
        mServerPortEditor.text = ServerManagement.mServerPort
        mServerPortEditor.onPreferenceChangeListener = this
    }

    override fun onPause() {
        super.onPause()
        mServerMonitor.cancel()
    }

    override fun onResume() {
        super.onResume()
        // Server Status monitor
        mServerMonitor = GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val mSucceed = mServerManagement.checkServerAlive()

                withContext(Dispatchers.Main) {
                    updateServerStatusUI(mSucceed)
                }
                delay(2000)
            }
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            // URL Edit Update
            KEY_SERVER_URLEDIT -> {
                ServerManagement.mServerBaseUrl = newValue as String
                GlobalScope.launch(Dispatchers.IO) {
                    val mSucceed = mServerManagement.checkServerAlive()

                    withContext(Dispatchers.Main) {
                        updateServerStatusUI(mSucceed)
                    }
                }
            }

            // Port Edit Update
            KEY_SERVER_PORTEDIT -> {
                ServerManagement.mServerPort = newValue as String
                GlobalScope.launch(Dispatchers.IO) {
                    val mSucceed = mServerManagement.checkServerAlive()

                    withContext(Dispatchers.Main) {
                        updateServerStatusUI(mSucceed)
                    }
                }
            }

            // Charging Notification Update
            KEY_DISABLE_CHARGING -> {
                Settings.mDisableChargingNotification = newValue as Boolean
            }
        }
        return true
    }

    fun updateServerStatusUI(mServerRetStatus: Boolean) {
        val mErrorString = getString(R.string.server_connection_error)

        if (mServerRetStatus) {
            // Server URL Editor
            mServerURLEditor.text = ServerManagement.mServerBaseUrl
            mServerURLEditor.summary = ServerManagement.mServerBaseUrl

            // Server Port Editor
            mServerPortEditor.text = ServerManagement.mServerPort
            mServerPortEditor.summary = ServerManagement.mServerPort

            // Overall Server Connection State
            mServerStatus.title = getString(R.string.server_on)

        } else {
            // Server URL Editor
            mServerURLEditor.summary = mErrorString

            // Server Port Editor
            mServerPortEditor.summary = mErrorString

            // Overall Server Connection State
            mServerStatus.title = getString(R.string.server_off)
        }
    }
}